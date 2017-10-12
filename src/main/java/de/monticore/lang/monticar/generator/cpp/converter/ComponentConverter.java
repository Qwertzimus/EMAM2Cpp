package de.monticore.lang.monticar.generator.cpp.converter;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.*;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc.types.EMAVariable;
import de.monticore.lang.math.math._symboltable.*;
import de.monticore.lang.math.math._symboltable.expression.*;
import de.monticore.lang.math.math._symboltable.matrix.*;
import de.monticore.lang.monticar.generator.*;
import de.monticore.lang.monticar.generator.cpp.*;
import de.monticore.lang.monticar.generator.cpp.instruction.ConnectInstructionCPP;
import de.monticore.lang.monticar.generator.cpp.instruction.ConstantConnectInstructionCPP;
import de.monticore.lang.monticar.generator.cpp.symbols.MathChainedExpression;
import de.monticore.lang.monticar.generator.cpp.symbols.MathStringExpression;
import de.monticore.lang.monticar.generator.optimization.MathInformationRegister;
import de.monticore.lang.monticar.generator.optimization.MathOptimizer;
import de.monticore.lang.monticar.mcexpressions._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sascha Schneiders
 *         Handles code generation for a component and its "subsymbols"
 */
public class ComponentConverter {

    public static BluePrintCPP currentBluePrint = null;

    public static BluePrint convertComponentSymbolToBluePrint(ExpandedComponentInstanceSymbol componentSymbol, MathStatementsSymbol mathStatementsSymbol, List<String> includeStrings, GeneratorCPP generatorCPP) {
        BluePrintCPP bluePrint = new BluePrintCPP(GeneralHelperMethods.getTargetLanguageComponentName(componentSymbol.getFullName()));
        ComponentConverter.currentBluePrint = bluePrint;
        bluePrint.setGenerator(generatorCPP);
        bluePrint.setOriginalSymbol(componentSymbol);
        bluePrint.addDefineGenerics(componentSymbol);

        addVariables(componentSymbol, bluePrint);

        String lastNameWithoutArrayPart = "";
        for (ExpandedComponentInstanceSymbol instanceSymbol : componentSymbol.getSubComponents()) {
            int arrayBracketIndex = instanceSymbol.getName().indexOf("[");
            boolean generateComponentInstance = true;
            if (arrayBracketIndex != -1) {
                generateComponentInstance = !instanceSymbol.getName().substring(0, arrayBracketIndex).equals(lastNameWithoutArrayPart);
                lastNameWithoutArrayPart = instanceSymbol.getName().substring(0, arrayBracketIndex);
                Log.info(lastNameWithoutArrayPart, "Without:");
                Log.info(generateComponentInstance + "", "Bool:");
            }
            if (generateComponentInstance) {
            }
            bluePrint.addVariable(ComponentInstanceConverter.convertComponentInstanceSymbolToVariable(instanceSymbol, componentSymbol));
        }

        //create arrays from variables that only differ at the end by _number_
        BluePrintFixer.fixBluePrintVariableArrays(bluePrint);
        MathInformationFilter.filterStaticInformation(componentSymbol, bluePrint, mathStatementsSymbol, generatorCPP, includeStrings);
        generateInitMethod(componentSymbol, bluePrint, generatorCPP, includeStrings);

        //generate execute method
        ComponentConverterMethodGeneration.generateExecuteMethod(componentSymbol, bluePrint, mathStatementsSymbol, generatorCPP, includeStrings);


        return bluePrint;
    }

    public static void addVariables(ExpandedComponentInstanceSymbol componentSymbol, BluePrintCPP bluePrint) {
        //add parameters as variables
        for (EMAVariable variable : componentSymbol.getParameters()) {
            Variable var = new Variable();
            var.setName(variable.getName());
            var.setTypeNameMontiCar(variable.getType());
            bluePrint.addVariable(var);
            bluePrint.getMathInformationRegister().addVariable(var);
            var.setIsParameterVariable(true);
        }
        //add ports as variables to blueprint
        for (PortSymbol port : componentSymbol.getPorts()) {
            bluePrint.addVariable(PortConverter.convertPortSymbolToVariable(port, port.getName(), bluePrint));
        }
    }

    public static void generateInitMethod(ExpandedComponentInstanceSymbol componentSymbol, BluePrintCPP bluePrint, GeneratorCPP generatorCPP, List<String> includeStrings) {
        Method method = new Method("init", "void");
        for (Variable v : bluePrint.getMathInformationRegister().getVariables()) {
            Log.info(v.getName(), "Inspecting Variable:");
            if (v.isStaticVariable()) {
                //TODO add static variable filter function before generate init method
                //extract the static variable and their possible assignments
                //generate the static variable and their assignment if present
                //check if value is constant matrix/vector definition
                //fix this too
                VariableStatic variableStatic = (VariableStatic) v;

                if (!variableStatic.getAssignmentSymbol().isPresent()) {
                    String instructionString = "";
                    if (variableStatic.getVariableType().getTypeNameTargetLanguage().equals("Matrix")) {
                        instructionString = MathInformationRegister.getVariableInitName(v, bluePrint) + "=Matrix(" + v.getDimensionalInformation().get(0) + "," + v.getDimensionalInformation().get(1) + ");\n";
                    } else if (variableStatic.getVariableType().getTypeNameTargetLanguage().equals("double")) {
                        instructionString = MathInformationRegister.getVariableInitName(v, bluePrint) + "=0;\n";
                    } else {
                        //RowVector and ColumnVector generation not handled here
                    }
                    method.addInstruction(new TargetCodeInstruction(instructionString));
                } else {
                    String instructionString = "";
                    instructionString += variableStatic.getNameTargetLanguageFormat();
                    instructionString += "=" + MathConverter.getConstantConversion(variableStatic.getAssignmentSymbol().get());
                    instructionString += ";\n";
                    method.addInstruction(new TargetCodeInstruction(instructionString));

                }
            } else if (v.getVariableType().getTypeNameTargetLanguage().equals("Matrix")) {
                Log.info(v.getName(), "Matrix found:");
                if (v.isParameterVariable()) {
                    method.addInstruction(new TargetCodeInstruction("this->" + MathInformationRegister.getVariableInitName(v, bluePrint) + "=" + MathInformationRegister.getVariableInitName(v, bluePrint) + ";\n"));
                    method.addParameter(v);
                } else
                    method.addInstruction(new TargetCodeInstruction(MathInformationRegister.getVariableInitName(v, bluePrint) + "=Matrix(" + v.getDimensionalInformation().get(0) + "," + v.getDimensionalInformation().get(1) + ");\n"));
            }
        }

        for (Variable v : bluePrint.getVariables()) {
            Log.info("Variable: " + v.getName(), "initBluePrintCreate:");

            if (v.isInputVariable() && !v.isConstantVariable()) {
                //method.addParameter(v);
                //Instruction instruction = new ConnectInstructionCPP(v, true, v, false);
                //method.addInstruction(instruction);
            } else if (v.isConstantVariable()) {
                Instruction instruction = new ConstantConnectInstructionCPP(v, v);
                method.addInstruction(instruction);
            }


        }

        for (ExpandedComponentInstanceSymbol subComponent : componentSymbol.getSubComponents()) {
            String parameterString = "";
            for (ASTExpression var : subComponent.getArguments()) {
                if (var.getSymbol().isPresent()) {
                    parameterString += ((MathExpressionSymbol) var.getSymbol().get()).getTextualRepresentation();
                } else {
                    parameterString += var.toString();
                }
            }
            String result = GeneralHelperMethods.getTargetLanguageVariableInstanceName(subComponent.getName()) + ".init(" + parameterString + ");\n";

            TargetCodeInstruction instruction = new TargetCodeInstruction(result);
            method.addInstruction(instruction);
        }
        bluePrint.addMethod(method);
    }

    public static BluePrint convertComponentSymbolToBluePrint(ExpandedComponentInstanceSymbol componentSymbol, List<String> includeStrings, GeneratorCPP generatorCPP) {
        return convertComponentSymbolToBluePrint(componentSymbol, null, includeStrings, generatorCPP);
    }

    public static void fixMathFunctions(MathExpressionSymbol mathExpressionSymbol, BluePrintCPP bluePrintCPP) {
        MathFunctionFixer.fixMathFunctions(mathExpressionSymbol, bluePrintCPP);
    }


}
