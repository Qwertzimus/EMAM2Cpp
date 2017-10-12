package de.monticore.lang.monticar.generator.cpp.converter;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.*;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc.types.EMAVariable;
import de.monticore.lang.math.math._symboltable.*;
import de.monticore.lang.math.math._symboltable.expression.*;
import de.monticore.lang.math.math._symboltable.matrix.*;
import de.monticore.lang.monticar.generator.*;
import de.monticore.lang.monticar.generator.cpp.BluePrintCPP;
import de.monticore.lang.monticar.generator.cpp.GeneratorCPP;
import de.monticore.lang.monticar.generator.cpp.OctaveHelper;
import de.monticore.lang.monticar.generator.cpp.VariableStatic;
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
        BluePrintCPP bluePrint = new BluePrintCPP(ComponentConverter.getTargetLanguageComponentName(componentSymbol.getFullName()));
        ComponentConverter.currentBluePrint = bluePrint;
        bluePrint.setGenerator(generatorCPP);
        bluePrint.setOriginalSymbol(componentSymbol);
        bluePrint.addDefineGenerics(componentSymbol);
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
        ComponentConverter.fixBluePrintVariableArrays(bluePrint);
        filterStaticInformation(componentSymbol, bluePrint, mathStatementsSymbol, generatorCPP, includeStrings);
        generateInitMethod(componentSymbol, bluePrint, generatorCPP, includeStrings);


        //generate setInput Method from input variables
        /*{
            Method method = new Method("setInputs", "void");
            for (Variable v : bluePrint.getVariables()) {
                Log.info("Variable: " + v.getName(), "setInputBluePrintCreate:");

                if (v.isInputVariable() && !v.isConstantVariable()) {
                    method.addParameter(v);
                    Instruction instruction = new ConnectInstructionCPP(v, true, v, false);
                    method.addInstruction(instruction);
                } else if (v.isConstantVariable()) {
                    Instruction instruction = new ConstantConnectInstructionCPP(v, v);
                    method.addInstruction(instruction);
                }


            }
            bluePrint.addMethod(method);
        }*/

        //generate execute method
        generateExecuteMethod(componentSymbol, bluePrint, mathStatementsSymbol, generatorCPP, includeStrings);


        return bluePrint;
    }

    public static void filterStaticInformation(ExpandedComponentInstanceSymbol componentSymbol, BluePrintCPP bluePrint, MathStatementsSymbol mathStatementsSymbol, GeneratorCPP generatorCPP, List<String> includeStrings) {
        if (mathStatementsSymbol != null) {
            for (MathExpressionSymbol expressionSymbol : mathStatementsSymbol.getMathExpressionSymbols()) {
                if (expressionSymbol.isAssignmentDeclarationExpression()) {
                    MathValueSymbol mathValueSymbol = (MathValueSymbol) expressionSymbol;
                    if (mathValueSymbol.getType().getProperties().contains("static")) {
                        VariableStatic var = new VariableStatic(mathValueSymbol.getName(), Variable.STATIC);
                        var.setTypeNameTargetLanguage(TypeConverter.getVariableTypeNameForMathLanguageTypeName(mathValueSymbol.getType()));
                        if (mathValueSymbol.getValue() != null)
                            var.setAssignmentSymbol(mathValueSymbol.getValue());
                        for (MathExpressionSymbol dimension : mathValueSymbol.getType().getDimensions())
                            var.addDimensionalInformation(dimension.getTextualRepresentation());
                        bluePrint.getMathInformationRegister().addVariable(var);
                    }
                } else if (expressionSymbol.isValueExpression()) {
                    MathValueExpressionSymbol valueExpressionSymbol = (MathValueExpressionSymbol) expressionSymbol;
                    if (valueExpressionSymbol.isValueExpression()) {
                        MathValueSymbol mathValueSymbol = (MathValueSymbol) valueExpressionSymbol;
                        if (mathValueSymbol.getType().getProperties().contains("static")) {
                            VariableStatic var = new VariableStatic(mathValueSymbol.getName(), Variable.STATIC);
                            var.setTypeNameTargetLanguage(TypeConverter.getVariableTypeNameForMathLanguageTypeName(mathValueSymbol.getType()));
                            if (mathValueSymbol.getValue() != null)
                                var.setAssignmentSymbol(mathValueSymbol.getValue());
                            for (MathExpressionSymbol dimension : mathValueSymbol.getType().getDimensions())
                                var.addDimensionalInformation(dimension.getTextualRepresentation());

                            bluePrint.getMathInformationRegister().addVariable(var);
                        }
                    }
                }
            }
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
            String result = ComponentConverter.getTargetLanguageVariableInstanceName(subComponent.getName()) + ".init(" + parameterString + ");\n";

            TargetCodeInstruction instruction = new TargetCodeInstruction(result);
            method.addInstruction(instruction);
        }
        bluePrint.addMethod(method);
    }


    public static void generateExecuteMethod(ExpandedComponentInstanceSymbol componentSymbol, BluePrintCPP bluePrint, MathStatementsSymbol mathStatementsSymbol, GeneratorCPP generatorCPP, List<String> includeStrings) {
        Method method = new Method("execute", "void");

        for (ConnectorSymbol connector : componentSymbol.getConnectors()) {
            if (!connector.isConstant()) {
                Log.info("source:" + connector.getSource() + " target:" + connector.getTarget(), "Port info:");
                Variable v1 = PortConverter.getVariableForPortSymbol(connector, connector.getSource(), bluePrint);
                Variable v2 = PortConverter.getVariableForPortSymbol(connector, connector.getTarget(), bluePrint);


                Instruction instruction = new ConnectInstructionCPP(v2, v1);
                method.addInstruction(instruction);
            } else {
                if (connector.getSourcePort().isConstant()) {
                    ConstantPortSymbol constPort = (ConstantPortSymbol) connector.getSourcePort();
                    Variable v1 = new Variable();
                    v1.setName(constPort.getConstantValue().getValueAsString());
                    Variable v2 = PortConverter.getVariableForPortSymbol(connector, connector.getTarget(), bluePrint);


                    Instruction instruction = new ConnectInstructionCPP(v2, v1);
                    method.addInstruction(instruction);
                } else if (connector.getTargetPort().isConstant()) {
                    ConstantPortSymbol constPort = (ConstantPortSymbol) connector.getTargetPort();
                    Variable v2 = new Variable();
                    v2.setName(constPort.getConstantValue().getValueAsString());
                    Variable v1 = PortConverter.getVariableForPortSymbol(connector, connector.getSource(), bluePrint);


                    Instruction instruction = new ConnectInstructionCPP(v2, v1);
                    method.addInstruction(instruction);
                } else {
                    Log.error("0xWRONGCONNECTOR the connector is constant but target nor source are constant");
                }
            }
        }
        if (mathStatementsSymbol != null) {
            // add math implementation instructions to method
            List<MathExpressionSymbol> newMathExpressionSymbols = new ArrayList<>();
            MathOptimizer.currentBluePrint = bluePrint;
            int counter = 0;
            if (generatorCPP.useThreadingOptimizations()) {
                bluePrint.addAdditionalIncludeString("mingw.thread");
            }
            for (MathExpressionSymbol mathExpressionSymbol : mathStatementsSymbol.getMathExpressionSymbols()) {
                if (generatorCPP.useAlgebraicOptimizations()) {
                    List<MathExpressionSymbol> precedingExpressions = new ArrayList<>();
                    for (int i = 0; i < counter; ++i)
                        precedingExpressions.add(mathStatementsSymbol.getMathExpressionSymbols().get(i));
                    newMathExpressionSymbols.add(MathOptimizer.applyOptimizations(mathExpressionSymbol, precedingExpressions));
                    ++counter;
                }
                fixMathFunctions(mathExpressionSymbol, bluePrint);
                String result = ExecuteMethodGenerator.generateExecuteCode(mathExpressionSymbol, includeStrings);
                TargetCodeMathInstruction instruction = new TargetCodeMathInstruction(result, mathExpressionSymbol);
                method.addInstruction(instruction);
            }
        }
        bluePrint.addMethod(method);
    }





    public static BluePrint convertComponentSymbolToBluePrint(ExpandedComponentInstanceSymbol componentSymbol, List<String> includeStrings, GeneratorCPP generatorCPP) {
        return convertComponentSymbolToBluePrint(componentSymbol, null, includeStrings, generatorCPP);
    }


    public static String getTargetLanguageComponentName(String fullName) {
        return fullName.replaceAll("\\.", "_").replaceAll("\\[", "_").replaceAll("\\]", "_");
    }

    public static String getTargetLanguageVariableInstanceName(String componentName, BluePrint bluePrint) {
        while (!bluePrint.getVariable(componentName).isPresent() && componentName.contains("_")) {
            componentName = componentName.replaceFirst("\\_", "[");
            componentName = componentName.replaceFirst("\\_", "]");
        }
        return getTargetLanguageVariableInstanceName(componentName);
    }

    /**
     * fixes array access
     *
     * @param name
     * @return
     */
    public static String getTargetLanguageVariableInstanceName(String name) {
        String nameChanged = "";
        int indexSecond = 0;
        while (true) {
            int indexFirst = name.indexOf("[", indexSecond);
            if (indexFirst != -1) {
                nameChanged += name.substring(0, indexFirst);
            }
            if (indexFirst != -1) {
                indexSecond = name.indexOf("]", indexFirst + 1);
                if (indexSecond != -1) {
                    String subString = name.substring(indexFirst + 1, indexSecond);
                    ++indexSecond;
                    try {
                        nameChanged += "[" + (Integer.parseInt(subString) - 1) + "]";
                    } catch (Exception ex) {
                        nameChanged += "[" + subString + "- 1]";
                    }
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        if (indexSecond != -1 && name.length() > indexSecond)
            nameChanged += name.substring(indexSecond);
        if (nameChanged.equals(""))
            return name;
        return nameChanged;
    }

    public static void fixBluePrintVariableArrays(BluePrint bluePrint) {
        {
            List<Variable> newVars = new ArrayList<>();

            for (Variable v : bluePrint.getVariables()) {
                String currentArrayName = v.getNameWithoutArrayNamePart();
                Log.info(currentArrayName, "CurrentArrayName:");
                boolean add = true;
                for (Variable newVar : newVars) {
                    if (currentArrayName.equals(newVar.getNameWithoutArrayNamePart())) {
                        newVar.setName(newVar.getNameWithoutArrayNamePart());
                        add = false;
                        newVar.setArraySize(newVar.getArraySize() + 1);
                    }
                }
                if (add)
                    newVars.add(v);
            }
            Log.info("", "NEWVARS:");
            for (Variable v : newVars) {
                Log.info(v.getName(), "name:");
            }
            bluePrint.setVariables(newVars);
        }
    }

    public static void fixMathFunctions(MathExpressionSymbol mathExpressionSymbol, BluePrintCPP bluePrintCPP) {
        MathConverter.fixMathFunctions(mathExpressionSymbol, bluePrintCPP);
    }


}
