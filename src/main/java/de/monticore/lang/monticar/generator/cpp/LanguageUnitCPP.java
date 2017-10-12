/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.lang.monticar.generator.cpp;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ExpandedComponentInstanceSymbol;
import de.monticore.lang.math.math._symboltable.MathStatementsSymbol;
import de.monticore.lang.monticar.generator.*;
import de.monticore.lang.monticar.generator.cpp.converter.ComponentConverter;
import de.monticore.lang.monticar.generator.cpp.instruction.ConnectInstructionCPP;
import de.monticore.lang.monticar.generator.order.ImplementExecutionOrder;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sascha Schneiders
 */
public class LanguageUnitCPP extends LanguageUnit {

    List<String> includeStrings = new ArrayList<>();

    GeneratorCPP generatorCPP;

    public LanguageUnitCPP() {

    }

    public void setGeneratorCPP(GeneratorCPP generatorCPP) {
        this.generatorCPP = generatorCPP;
    }

    public void generateBluePrints() {
        for (int i = 0; i < symbolsToConvert.size(); ++i) {
            Symbol symbol = symbolsToConvert.get(i);
            //only works with ExpandedComponentSymbols and MathStatementsSymbol

            if (symbol.isKindOf(ExpandedComponentInstanceSymbol.KIND)) {
                Log.info(symbol.toString(), "Current Symbol(size:" + symbolsToConvert.size() + ")" + ":");

                if (i + 1 < symbolsToConvert.size()) {
                    Symbol nextSymbol = symbolsToConvert.get(i + 1);
                    Log.info(nextSymbol.toString(), "Next Symbol:");
                    if (nextSymbol.isKindOf(MathStatementsSymbol.KIND)) {


                        BluePrint bluePrint = ComponentConverter.convertComponentSymbolToBluePrint((ExpandedComponentInstanceSymbol) symbol, (MathStatementsSymbol) nextSymbol, includeStrings, generatorCPP);
                        bluePrints.add(bluePrint);

                        ++i;
                    }

                } else {
                    BluePrint bluePrint = ComponentConverter.convertComponentSymbolToBluePrint((ExpandedComponentInstanceSymbol) symbol, includeStrings, generatorCPP);
                    bluePrints.add(bluePrint);
                }
            }

        }
    }

    public String getGeneratedHeader(BluePrintCPP bluePrint) {
        fixExecutionOrder(bluePrint);
        String resultString = "";
        //guard defines
        resultString += "#ifndef " + bluePrint.getName().toUpperCase() + "\n";
        resultString += "#define " + bluePrint.getName().toUpperCase() + "\n";
        if (generatorCPP.useMPIDefinitionFix())
            resultString += "#ifndef M_PI\n" +
                    "#define M_PI 3.14159265358979323846\n" +
                    "#endif\n";

        List<String> alreadyGeneratedIncludes = new ArrayList<>();
        //includes
        //add default include
        resultString += "#include \"octave/oct.h\"\n";
        alreadyGeneratedIncludes.add("octave/oct");
        for (Variable v : bluePrint.getVariables()) {
            //TODO remove multiple same includes
            if (v.hasInclude()) {
                if (!alreadyGeneratedIncludes.contains(v.getVariableType().getIncludeName())) {
                    alreadyGeneratedIncludes.add(v.getVariableType().getIncludeName());
                    resultString += "#include \"" + v.getVariableType().getIncludeName() + ".h\"\n";
                }
            }
        }

        for (String string : bluePrint.getAdditionalIncludeStrings())
            resultString += "#include \"" + string + ".h\"\n";

        for (String include : includeStrings) {
            resultString += include;
        }


        //class definition start
        resultString += "class " + bluePrint.getName() + "{\n";

        //const variables
        for (String constString : bluePrint.getConsts())
            resultString += constString;
        resultString += "public:\n";

        //input variable
        for (Variable v : bluePrint.getVariables()) {
            if (!v.isArray())
                resultString += v.getVariableType().getTypeNameTargetLanguage() + " " + v.getNameTargetLanguageFormat() + ";\n";
            else
                resultString += v.getVariableType().getTypeNameTargetLanguage() + " " + v.getNameTargetLanguageFormat() + "[" + v.getArraySize() + "]" + ";\n";
        }

        //generate methods
        for (Method method : bluePrint.getMethods()) {
            int counter = 0;
            resultString += method.getReturnTypeName() + " " + method.getName() + "(";
            for (Variable param : method.getParameters()) {
                if (counter == 0) {
                    ++counter;
                    resultString += param.getVariableType().getTypeNameTargetLanguage() + " " + param.getNameTargetLanguageFormat();
                } else {
                    resultString += ", " + param.getVariableType().getTypeNameTargetLanguage() + " " + param.getNameTargetLanguageFormat();
                }
                if (param.isArray())
                    resultString += "[" + param.getArraySize() + "]";
            }
            resultString += ")\n";//TODO add semicolon when using source files

            //method body start
            resultString += "{\n";

            for (Instruction instruction : method.getInstructions()) {
                if (instruction instanceof ConnectInstructionCPP) {
                    ConnectInstructionCPP connectInstructionCPP = (ConnectInstructionCPP) instruction;
                    Log.info("v1:" + connectInstructionCPP.getVariable1().getName() + "v2:" + connectInstructionCPP.getVariable2().getName(), "Instruction:");
                } else if (instruction instanceof ExecuteInstruction) {
                    ExecuteInstruction executeInstruction = (ExecuteInstruction) instruction;

                }
                Log.info(resultString, "beforRes:");
                resultString += instruction.getTargetLanguageInstruction();
                Log.info(resultString, "afterRes:");
            }

            //method body end
            resultString += "}\n";
        }


        resultString += "\n";
        //class definition end
        resultString += "};\n";

        //guard define end
        resultString += "#endif\n";
        Log.info(resultString, "Before RESSSS:");
        return resultString;
    }

    public void fixExecutionOrder(BluePrintCPP bluePrintCPP) {
        Method method = bluePrintCPP.getMethod("execute").get();
        Map<String, List<Instruction>> map = new HashMap<>();


        List<ExpandedComponentInstanceSymbol> threadableSubComponents = bluePrintCPP.getOriginalSymbol().getIndependentSubComponents();

        List<Instruction> otherInstructions = new ArrayList<>();
        for (Instruction instruction : method.getInstructions()) {
            if (instruction.isConnectInstruction()) {
                ConnectInstruction connectInstruction = (ConnectInstruction) instruction;
                if (connectInstruction.getVariable1().isCrossComponentVariable()) {
                    String name = connectInstruction.getVariable1().getComponentName();
                    Log.info(name, "ComponentName:");
                    if (map.containsKey(name)) {
                        map.get(name).add(instruction);
                    } else {
                        List<Instruction> list = new ArrayList<Instruction>();
                        list.add(instruction);
                        map.put(name, list);
                    }
                } else {
                    otherInstructions.add(instruction);
                }
            } else {
                otherInstructions.add(instruction);
            }
        }
        List<Instruction> newList = new ArrayList<>();

        List<ExpandedComponentInstanceSymbol> exOrder = ImplementExecutionOrder.exOrder(bluePrintCPP.getOriginalSymbol());

        for (ExpandedComponentInstanceSymbol instanceSymbol : exOrder) {
            String namey = instanceSymbol.getName();
            Log.info(namey, "Trying to add:");
            if (map.containsKey(namey)) {
                for (Instruction i : map.get(namey))
                    if (!newList.contains(i))
                        newList.add(i);
                Log.info(namey, "Added Namey:");
            }
            boolean add = bluePrintCPP.getOriginalSymbol().isSubComponent(instanceSymbol.getFullName());
            if (add) {
                ExecuteInstruction executeInstruction = (ExecuteInstruction) getExecuteInstruction(instanceSymbol, bluePrintCPP, threadableSubComponents);
                if (!newList.contains(executeInstruction))
                    newList.add(executeInstruction);
            }
        }
        for (ExpandedComponentInstanceSymbol subComponent : bluePrintCPP.getOriginalSymbol().getSubComponents()) {
            String namey = subComponent.getName();
            if (map.containsKey(namey)) {
                List<Instruction> instructionsToAdd = map.get(namey);
                for (Instruction i : instructionsToAdd) {
                    if (!newList.contains(i))
                        newList.add(i);
                }
                Log.info(namey, "Added Namey:");
            }
        }
        fixSlistExecutionOrder(bluePrintCPP.getOriginalSymbol(), newList, bluePrintCPP, threadableSubComponents);
        List<TargetCodeInstruction> joinInstructions = new ArrayList<>();

        //if (generatorCPP.useThreadingOptimizations())
        for (Instruction instruction : newList) {
            if (instruction.isExecuteInstruction()) {
                ExecuteInstruction executeInstruction = (ExecuteInstruction) instruction;
                if (executeInstruction.isCanBeThreaded()) {
                    joinInstructions.add(new TargetCodeInstruction(executeInstruction.getThreadName() + ".join();\n"));
                }
            }
        }

        newList.addAll(joinInstructions);
        newList.addAll(otherInstructions);
        method.setInstructions(newList);
    }

    public void fixSlistExecutionOrder(ExpandedComponentInstanceSymbol instanceSymbol, List<Instruction> newList, BluePrintCPP bluePrintCPP, List<ExpandedComponentInstanceSymbol> threadableComponents) {
        for (ExpandedComponentInstanceSymbol subComponent : instanceSymbol.getSubComponents()) {
            if (!listContainsExecuteInstruction(newList, subComponent.getName())) {
                ExecuteInstruction executeInstruction = (ExecuteInstruction) getExecuteInstruction(subComponent.getName(), bluePrintCPP, threadableComponents);

                newList.add(executeInstruction);
            }
        }

        /*int lastDotIndex = instanceSymbol.getFullName().lastIndexOf(".");
        if (lastDotIndex != -1) {
            String name = instanceSymbol.getFullName().substring(0, lastDotIndex);
            //Log.info(name,"Trying to fix name:");
            if (parentSymbol.isSubComponent(name)) {
                //Log.info(name,"IsSubcomponent ExecuteInstruction:");
                String nameToAdd;
                if (!listContainsExecuteInstruction(newList, name)) {
                    //Log.info(name,"Added additional ExecuteInstruction:");
                    newList.add(getExecuteInstruction(name));
                }
            }
        }*/
    }

    public boolean listContainsExecuteInstruction(List<Instruction> list, String name) {
        for (Instruction instruction : list) {
            if (instruction.isExecuteInstruction()) {
                ExecuteInstruction executeInstruction = (ExecuteInstruction) instruction;
                if (executeInstruction.getComponentName().equals(ComponentConverter.getTargetLanguageVariableInstanceName(name)))
                    return true;
            }
        }
        return false;
    }


    public Instruction getExecuteInstruction(String nameToAdd, BluePrintCPP bluePrintCPP, List<ExpandedComponentInstanceSymbol> threadableComponents) {
        boolean canBeThreaded = false;
        if (generatorCPP.useThreadingOptimizations())
            for (ExpandedComponentInstanceSymbol instanceSymbol : threadableComponents) {
                if (nameToAdd.equals(instanceSymbol.getName()))
                    canBeThreaded = true;
            }
        String name = ComponentConverter.getTargetLanguageComponentName(nameToAdd);
        Log.info(name, "Adding ExecuteInstruction:");
        return new ExecuteInstruction(name, bluePrintCPP, canBeThreaded);
    }

    public Instruction getExecuteInstruction(ExpandedComponentInstanceSymbol componentInstanceSymbol, BluePrintCPP bluePrintCPP, List<ExpandedComponentInstanceSymbol> threadableComponents) {
        return getExecuteInstruction(componentInstanceSymbol.getName(), bluePrintCPP, threadableComponents);
    }
}
