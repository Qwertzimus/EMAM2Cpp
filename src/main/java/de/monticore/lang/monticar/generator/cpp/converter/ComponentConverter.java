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
                String result = generateExecuteCode(mathExpressionSymbol, includeStrings);
                TargetCodeMathInstruction instruction = new TargetCodeMathInstruction(result, mathExpressionSymbol);
                method.addInstruction(instruction);
            }
        }
        bluePrint.addMethod(method);
    }


    public static String generateExecuteCode(MathExpressionSymbol mathExpressionSymbol, List<String> includeStrings) {
        //Not needed for execute method
        /*if (mathExpressionSymbol.isAssignmentDeclarationExpression()) {
            return generateExecuteCode((MathValueSymbol) mathExpressionSymbol, includeStrings);
        } */
        if (mathExpressionSymbol.isAssignmentExpression()) {
            return generateExecuteCode((MathAssignmentExpressionSymbol) mathExpressionSymbol, includeStrings);
        } else if (mathExpressionSymbol.isCompareExpression()) {
            return generateExecuteCode((MathCompareExpressionSymbol) mathExpressionSymbol, includeStrings);
        } else if (mathExpressionSymbol.isForLoopExpression()) {
            return generateExecuteCode((MathForLoopExpressionSymbol) mathExpressionSymbol, includeStrings);
        } else if (mathExpressionSymbol.isMatrixExpression()) {
            return generateExecuteCode((MathMatrixExpressionSymbol) mathExpressionSymbol, includeStrings);
        } else if (mathExpressionSymbol.isConditionalsExpression()) {
            //This is the real conditional expressions, the if/elseif/else statement is
            // called conditional expression in the math language
            //name in math langugae could be changed to avoid confusion
            return generateExecuteCode((MathConditionalExpressionsSymbol) mathExpressionSymbol, includeStrings);
        } else if (mathExpressionSymbol.isConditionalExpression()) {
            Log.error("ConditionalExpression should not be handled this way!");
            //return generateExecuteCode((MathConditionalExpressionSymbol) mathExpressionSymbol, includeStrings);
        } else if (mathExpressionSymbol.isArithmeticExpression()) {
            return generateExecuteCode((MathArithmeticExpressionSymbol) mathExpressionSymbol, includeStrings);
        } else if (mathExpressionSymbol.isValueExpression()) {
            return generateExecuteCode((MathValueExpressionSymbol) mathExpressionSymbol, includeStrings);
        } else if (mathExpressionSymbol.isMathValueTypeExpression()) {
            return generateExecuteCode((MathValueType) mathExpressionSymbol, includeStrings);
        } else if (mathExpressionSymbol.isPreOperatorExpression()) {
            return generateExecuteCode((MathPreOperatorExpressionSymbol) mathExpressionSymbol, includeStrings);
        } else if (mathExpressionSymbol.getExpressionID() == MathStringExpression.ID) {
            return generateExecuteCode((MathStringExpression) mathExpressionSymbol, includeStrings);
        } else if (mathExpressionSymbol.getExpressionID() == MathChainedExpression.ID) {
            return generateExecuteCode((MathChainedExpression) mathExpressionSymbol, includeStrings);
        } else if (mathExpressionSymbol.isParenthesisExpression()) {
            return generateExecuteCode((MathParenthesisExpressionSymbol) mathExpressionSymbol, includeStrings);
        } else {
            Log.info(mathExpressionSymbol.getTextualRepresentation(), "Symbol:");
            Log.error("Case not handled!");
        }
        return null;
    }

    public static String generateExecuteCode(MathParenthesisExpressionSymbol mathExpressionSymbol, List<String> includeStrings) {
        String result = "";
        result += "(";
        result += generateExecuteCode(mathExpressionSymbol.getMathExpressionSymbol(), includeStrings);
        result += ")";
        return result;
    }

    public static String generateExecuteCode(MathChainedExpression mathChainedExpression, List<String> includeStrings) {
        String result = "";
        result += generateExecuteCode(mathChainedExpression.getFirstExpressionSymbol(), includeStrings);
        result += generateExecuteCode(mathChainedExpression.getSecondExpressionSymbol(), includeStrings);
        return result;
    }

    public static String generateExecuteCode(MathStringExpression mathExpressionSymbol, List<String> includeStrings) {
        String result = "";
        result += mathExpressionSymbol.getTextualRepresentation();
        return result;
    }

    public static String generateExecuteCode(MathPreOperatorExpressionSymbol mathExpressionSymbol, List<String> includeStrings) {
        String result = "";
        result += mathExpressionSymbol.getOperator() + generateExecuteCode(mathExpressionSymbol.getMathExpressionSymbol(), includeStrings);
        return result;
    }

    public static String generateExecuteCode(MathCompareExpressionSymbol mathCompareExpressionSymbol, List<String> includeStrings) {
        String result = "";
        result += "(" + generateExecuteCode(mathCompareExpressionSymbol.getLeftExpression(), includeStrings) + " " + mathCompareExpressionSymbol.getCompareOperator();
        result += " " + generateExecuteCode(mathCompareExpressionSymbol.getRightExpression(), includeStrings) + ")";

        return result;
    }

    public static String generateExecuteCode(MathValueExpressionSymbol mathValueExpressionSymbol, List<String> includeStrings) {
        if (mathValueExpressionSymbol.isNameExpression()) {
            return generateExecuteCode((MathNameExpressionSymbol) mathValueExpressionSymbol, includeStrings);
        } else if (mathValueExpressionSymbol.isNumberExpression()) {
            return generateExecuteCode((MathNumberExpressionSymbol) mathValueExpressionSymbol, includeStrings);
        } else if (mathValueExpressionSymbol.isAssignmentDeclarationExpression()) {
            return generateExecuteCodeDeclaration((MathValueSymbol) mathValueExpressionSymbol, includeStrings);
        } else {
            Log.error("0xMAVAEXSY Case not handled!");
        }
        return null;
    }

    public static String generateExecuteCodeDeclaration(MathValueSymbol mathValueSymbol, List<String> includeStrings) {
        String result = "";
        List<String> properties = mathValueSymbol.getType().getProperties();
        if (properties.contains("static")) {
            Variable var = new Variable(mathValueSymbol.getName(), Variable.STATIC);
            var.setTypeNameTargetLanguage(TypeConverter.getVariableTypeNameForMathLanguageTypeName(mathValueSymbol.getType()));
            for (MathExpressionSymbol dimension : mathValueSymbol.getType().getDimensions())
                var.addDimensionalInformation(dimension.getTextualRepresentation());

            currentBluePrint.addVariable(var);
        } else {
            result += generateExecuteCode(mathValueSymbol.getType(), includeStrings);
            result += " " + mathValueSymbol.getName();
            if (mathValueSymbol.getValue() != null) {
                result += " = " + generateExecuteCode(mathValueSymbol.getValue(), includeStrings);
            }
            result += ";\n";
        }
        currentBluePrint.getMathInformationRegister().addVariable(mathValueSymbol);
        //result += mathValueSymbol.getTextualRepresentation();
        return result;
    }


    public static String generateExecuteCode(MathValueSymbol mathValueSymbol, List<String> includeStrings) {
        String result = "";

        result += generateExecuteCode(mathValueSymbol.getType(), includeStrings);
        result += " " + mathValueSymbol.getName();
        if (mathValueSymbol.getValue() != null) {
            result += " = " + generateExecuteCode(mathValueSymbol.getValue(), includeStrings);
            result += ";\n";
        }
        //result += mathValueSymbol.getTextualRepresentation();
        return result;
    }

    public static String generateExecuteCode(MathValueType mathValueType, List<String> includeStrings) {
        String result = "";
        if (mathValueType.isRationalType()) {
            if (mathValueType.getDimensions().size() == 0)
                return "double";
            else if (mathValueType.getDimensions().size() == 2) {
                Log.info("Dim1:" + mathValueType.getDimensions().get(0).getTextualRepresentation() + "Dim2: " + mathValueType.getDimensions().get(1).getTextualRepresentation(), "DIMS:");
                if (mathValueType.getDimensions().get(0).getTextualRepresentation().equals("1")) {
                    return "RowVector";
                } else if (mathValueType.getDimensions().get(1).getTextualRepresentation().equals("1")) {
                    return "ColumnVector";
                }
                return "Matrix";//TODO improve in future
            } else {
                Log.error("0xGEEXCOMAVAT Type conversion Case not handled!");
            }
        } else {
            Log.error("Case not handled!");
        }
        return result;
    }

    public static String generateExecuteCode(MathNameExpressionSymbol mathNameExpressionSymbol, List<String> includeStrings) {
        Log.info(mathNameExpressionSymbol.getNameToResolveValue(), "NameToResolveValue:");
        return mathNameExpressionSymbol.getNameToResolveValue();
    }


    public static String generateExecuteCode(MathNumberExpressionSymbol mathNumberExpressionSymbol, List<String> includeStrings) {
        return mathNumberExpressionSymbol.getValue().toString();
    }

    public static String generateExecuteCode(MathAssignmentExpressionSymbol mathAssignmentExpressionSymbol, List<String> includeStrings) {
        Log.info(mathAssignmentExpressionSymbol.getTextualRepresentation(), "mathAssignmentExpressionSymbol:");

        if (mathAssignmentExpressionSymbol.getMathMatrixAccessOperatorSymbol() != null) {
            if (MathConverter.fixForLoopAccess(mathAssignmentExpressionSymbol.getNameOfMathValue(), currentBluePrint)) {

                String result = mathAssignmentExpressionSymbol.getNameOfMathValue();
                result += generateExecuteCode(mathAssignmentExpressionSymbol.getMathMatrixAccessOperatorSymbol(), includeStrings, true) + " ";
                result += mathAssignmentExpressionSymbol.getAssignmentOperator().getOperator() + " ";
                result += generateExecuteCode(mathAssignmentExpressionSymbol.getExpressionSymbol(), includeStrings) + ";\n";

                return result;

            }
            if (mathAssignmentExpressionSymbol.getNameOfMathValue().equals("eigenVectors")) {
                for (Variable var : currentBluePrint.getMathInformationRegister().getVariables()) {
                    Log.info(var.getName(), "Var:");
                }
                Log.error("awda");
            }
            String result = mathAssignmentExpressionSymbol.getNameOfMathValue();
            result += generateExecuteCode(mathAssignmentExpressionSymbol.getMathMatrixAccessOperatorSymbol(), includeStrings) + " ";
            result += mathAssignmentExpressionSymbol.getAssignmentOperator().getOperator() + " ";
            result += generateExecuteCode(mathAssignmentExpressionSymbol.getExpressionSymbol(), includeStrings) + ";\n";

            return result;
        }

        return mathAssignmentExpressionSymbol.getNameOfMathValue() + " " + mathAssignmentExpressionSymbol.getAssignmentOperator().getOperator() + " " + generateExecuteCode(mathAssignmentExpressionSymbol.getExpressionSymbol(), includeStrings) + ";\n";
    }


    public static String generateExecuteCode(MathForLoopExpressionSymbol mathForLoopExpressionSymbol, List<String> includeStrings) {
        String result = "";
        //For loop head
        result += generateExecuteCode(mathForLoopExpressionSymbol.getForLoopHead(), includeStrings);
        //For loop body
        result += "{\n";
        for (MathExpressionSymbol mathExpressionSymbol : mathForLoopExpressionSymbol.getForLoopBody())
            result += generateExecuteCode(mathExpressionSymbol, includeStrings);
        result += "}\n";

        return result;
    }

    public static String generateExecuteCode(MathForLoopHeadSymbol mathForLoopHeadSymbol, List<String> includeStrings) {
        String result = "";
        result += ForLoopHeadConverter.getForLoopHeadCode(mathForLoopHeadSymbol, includeStrings);
        return result;
    }

    public static String generateExecuteCode(MathArithmeticExpressionSymbol mathExpressionSymbol, List<String> includeStrings) {
        String result = "";
        if (mathExpressionSymbol.getMathOperator().equals("^")) {
            List<MathExpressionSymbol> list = new ArrayList<MathExpressionSymbol>();
            list.add(mathExpressionSymbol.getLeftExpression());
            list.add(mathExpressionSymbol.getRightExpression());
            String valueListString = "(" + OctaveHelper.getOctaveValueListString(list) + ")";
            return OctaveHelper.getCallBuiltInFunctionFirstResult(mathExpressionSymbol.getLeftExpression(), "Fmpower", valueListString, false, 1);
        } else {
            result += /*"("+*/  generateExecuteCode(mathExpressionSymbol.getLeftExpression(), includeStrings) + mathExpressionSymbol.getMathOperator();

            if (mathExpressionSymbol.getRightExpression() != null)
                result += generateExecuteCode(mathExpressionSymbol.getRightExpression(), includeStrings);
        }
        return result;

    }


    public static String generateExecuteCode(MathConditionalExpressionsSymbol mathConditionalExpressionsSymbol, List<String> includeStrings) {
        String result = "";

        //if condition
        result += generateIfConditionCode(mathConditionalExpressionsSymbol.getIfConditionalExpression(), includeStrings);
        //else if condition
        for (MathConditionalExpressionSymbol mathConditionalExpressionSymbol : mathConditionalExpressionsSymbol.getIfElseConditionalExpressions())
            result += "else " + generateIfConditionCode(mathConditionalExpressionSymbol, includeStrings);
        //else block
        if (mathConditionalExpressionsSymbol.getElseConditionalExpression().isPresent()) {
            result += "else " + generateIfConditionCode(mathConditionalExpressionsSymbol.getElseConditionalExpression().get(), includeStrings);
        }
        return result;
    }

    public static String generateExecuteCode(MathMatrixExpressionSymbol mathMatrixExpressionSymbol, List<String> includeStrings) {
        String result = "";


        if (mathMatrixExpressionSymbol.isMatrixArithmeticExpression()) {
            return generateExecuteCode((MathMatrixArithmeticExpressionSymbol) mathMatrixExpressionSymbol, includeStrings);
        } else if (mathMatrixExpressionSymbol.isMatrixAccessExpression()) {
            return generateExecuteCode((MathMatrixAccessSymbol) mathMatrixExpressionSymbol, includeStrings);
        } else if (mathMatrixExpressionSymbol.isMatrixNameExpression()) {
            return generateExecuteCode((MathMatrixNameExpressionSymbol) mathMatrixExpressionSymbol, includeStrings);
        } else if (mathMatrixExpressionSymbol.isValueExpression()) {
            return generateExecuteCode((MathMatrixArithmeticValueSymbol) mathMatrixExpressionSymbol, includeStrings);
        }
        Log.info(mathMatrixExpressionSymbol.getTextualRepresentation(), "Symbol:");
        Log.info(mathMatrixExpressionSymbol.getClass().getName(), "Symbol Name:");
        Log.error("0xMAMAEXSY Case not handled!");
        return result;
    }

    public static String generateExecuteCode(MathMatrixArithmeticValueSymbol mathMatrixArithmeticValueSymbol, List<String> includeStrings) {
        return MathConverter.getConstantConversion(mathMatrixArithmeticValueSymbol);
    }

    public static String generateExecuteCode(MathMatrixNameExpressionSymbol mathMatrixNameExpressionSymbol, List<String> includeStrings) {
        String result = "";
        //TODO fix matrix access parameter stuff
        result += mathMatrixNameExpressionSymbol.getNameToAccess();
        if (mathMatrixNameExpressionSymbol.hasMatrixAccessExpression()) {
            mathMatrixNameExpressionSymbol.getMathMatrixAccessOperatorSymbol().setMathMatrixNameExpressionSymbol(mathMatrixNameExpressionSymbol);
            result += generateExecuteCode(mathMatrixNameExpressionSymbol.getMathMatrixAccessOperatorSymbol(), includeStrings);
        }
        return result;
    }

    public static String generateExecuteCode(MathMatrixArithmeticExpressionSymbol mathMatrixArithmeticExpressionSymbol, List<String> includeStrings) {
        String result = "";
        if (mathMatrixArithmeticExpressionSymbol.getMathOperator().equals("^")) {
            return generateExecuteCodeMatrixPowerOfOperator(mathMatrixArithmeticExpressionSymbol, includeStrings);
        } else if (mathMatrixArithmeticExpressionSymbol.getMathOperator().equals(".^")) {
            return generateExecuteCodeMatrixEEPowerOf(mathMatrixArithmeticExpressionSymbol, includeStrings);
        } else if (mathMatrixArithmeticExpressionSymbol.getMathOperator().equals("./")) {
            return generateExecuteCodeMatrixEEDivide(mathMatrixArithmeticExpressionSymbol, includeStrings);
        /*} else if (mathArithmeticExpressionSymbol.getMathOperator().equals("./")) {
            Log.error("reace");
            result += "\"ldivide\"";
            includeStrings.add("Helper");
        */
        } else {

            result += /*"(" +*/ generateExecuteCode(mathMatrixArithmeticExpressionSymbol.getLeftExpression(), includeStrings) + " " + mathMatrixArithmeticExpressionSymbol.getMathOperator();

            if (mathMatrixArithmeticExpressionSymbol.getRightExpression() != null)
                result += " " + generateExecuteCode(mathMatrixArithmeticExpressionSymbol.getRightExpression(), includeStrings);
        }
        /*result += ")"*/
        ;

        return result;
    }

    public static String generateExecuteCodeMatrixEEDivide(MathMatrixArithmeticExpressionSymbol mathExpressionSymbol, List<String> includeStrings) {
        List<MathExpressionSymbol> list = new ArrayList<MathExpressionSymbol>();
        list.add(mathExpressionSymbol.getLeftExpression());
        list.add(mathExpressionSymbol.getRightExpression());
        String valueListString = "(" + OctaveHelper.getOctaveValueListString(list) + ")";
        return OctaveHelper.getCallOctaveFunctionFirstResult(mathExpressionSymbol.getLeftExpression(), "ldivide", valueListString, false);
    }

    public static String generateExecuteCodeMatrixEEPowerOf(MathMatrixArithmeticExpressionSymbol mathExpressionSymbol, List<String> includeStrings) {
        List<MathExpressionSymbol> list = new ArrayList<MathExpressionSymbol>();
        list.add(mathExpressionSymbol.getLeftExpression());
        list.add(mathExpressionSymbol.getRightExpression());
        String valueListString = "(" + OctaveHelper.getOctaveValueListString(list) + ")";
        return OctaveHelper.getCallOctaveFunctionFirstResult(mathExpressionSymbol.getLeftExpression(), "power", valueListString, false);
    }

    public static String generateExecuteCodeMatrixPowerOfOperator(MathMatrixArithmeticExpressionSymbol mathExpressionSymbol, List<String> includeStrings) {
        List<MathExpressionSymbol> list = new ArrayList<MathExpressionSymbol>();
        list.add(mathExpressionSymbol.getLeftExpression());
        list.add(mathExpressionSymbol.getRightExpression());
        String valueListString = "(" + OctaveHelper.getOctaveValueListString(list) + ")";
        return OctaveHelper.getCallBuiltInFunctionFirstResult(mathExpressionSymbol.getLeftExpression(), "Fmpower", valueListString, false, 1);
    }

    public static String generateExecuteCode(MathMatrixAccessOperatorSymbol mathMatrixAccessOperatorSymbol, List<String> includeStrings, boolean setMinusOne) {
        String result = "";
        int counter = 0;
        int ignoreCounterAt = -1;
        int counterSetMinusOne = -1;
        if (mathMatrixAccessOperatorSymbol.getMathMatrixAccessSymbols().size() == 2) {
            if (mathMatrixAccessOperatorSymbol.getMathMatrixAccessSymbols().get(0).isDoubleDot()) {
                ignoreCounterAt = 0;
                result += ".column";
                counterSetMinusOne = 1;
            } else if (mathMatrixAccessOperatorSymbol.getMathMatrixAccessSymbols().get(1).isDoubleDot()) {
                ignoreCounterAt = 1;
                result += ".row";
                counterSetMinusOne = 0;
            }
        }
        result += mathMatrixAccessOperatorSymbol.getAccessStartSymbol();


        for (MathMatrixAccessSymbol mathMatrixAccessSymbol : mathMatrixAccessOperatorSymbol.getMathMatrixAccessSymbols()) {
            if (counter == ignoreCounterAt) {
                ++counter;
            } else {
                if (counter == counterSetMinusOne) {
                    result += generateExecuteCode(mathMatrixAccessSymbol, includeStrings, true);
                } else
                    result += generateExecuteCode(mathMatrixAccessSymbol, includeStrings, setMinusOne);
                ++counter;
                if (counter < mathMatrixAccessOperatorSymbol.getMathMatrixAccessSymbols().size() && counter != ignoreCounterAt) {
                    result += ", ";
                }
            }
        }


        result += mathMatrixAccessOperatorSymbol.getAccessEndSymbol();
        return result;
    }

    public static String generateExecuteCode(MathMatrixAccessOperatorSymbol mathMatrixAccessOperatorSymbol, List<String> includeStrings) {
        String result = "";
        int counter = 0;
        int ignoreCounterAt = -1;
        int counterSetMinusOne = -1;
        if (mathMatrixAccessOperatorSymbol.getMathMatrixAccessSymbols().size() == 2) {
            if (mathMatrixAccessOperatorSymbol.getMathMatrixAccessSymbols().get(0).isDoubleDot()) {
                ignoreCounterAt = 0;
                result += ".column";
                counterSetMinusOne = 1;
            } else if (mathMatrixAccessOperatorSymbol.getMathMatrixAccessSymbols().get(1).isDoubleDot()) {
                ignoreCounterAt = 1;
                result += ".row";
                counterSetMinusOne = 0;
            }
        }
        result += mathMatrixAccessOperatorSymbol.getAccessStartSymbol();

        if (MathConverter.fixForLoopAccess(mathMatrixAccessOperatorSymbol.getMathMatrixNameExpressionSymbol(), currentBluePrint)) {
            for (MathMatrixAccessSymbol mathMatrixAccessSymbol : mathMatrixAccessOperatorSymbol.getMathMatrixAccessSymbols()) {
                if (counter == ignoreCounterAt) {
                    ++counter;
                } else {
                    result += generateExecuteCode(mathMatrixAccessSymbol, includeStrings, true);
                    ++counter;
                    if (counter < mathMatrixAccessOperatorSymbol.getMathMatrixAccessSymbols().size() && counter != ignoreCounterAt) {
                        result += ", ";
                    }
                }
            }
        } else {
            for (MathMatrixAccessSymbol mathMatrixAccessSymbol : mathMatrixAccessOperatorSymbol.getMathMatrixAccessSymbols()) {
                if (counter == ignoreCounterAt) {
                    ++counter;
                } else {
                    if (counter == counterSetMinusOne)
                        result += generateExecuteCode(mathMatrixAccessSymbol, includeStrings, true);
                    else
                        result += generateExecuteCode(mathMatrixAccessSymbol, includeStrings);
                    ++counter;
                    if (counter < mathMatrixAccessOperatorSymbol.getMathMatrixAccessSymbols().size() && counter != ignoreCounterAt) {
                        result += ", ";
                    }
                }
            }
        }
        result += mathMatrixAccessOperatorSymbol.getAccessEndSymbol();
        return result;
    }

    public static String generateExecuteCode(MathMatrixAccessSymbol mathMatrixAccessSymbol, List<String> includeStrings, boolean setMinusOne) {
        String result = "";

        if (mathMatrixAccessSymbol.isDoubleDot())
            result += ":";
        else
            result += generateExecuteCode(mathMatrixAccessSymbol.getMathExpressionSymbol().get(), includeStrings);
        if (setMinusOne)
            result += "-1";
        return result;
    }

    public static String generateExecuteCode(MathMatrixAccessSymbol mathMatrixAccessSymbol, List<String> includeStrings) {
        return generateExecuteCode(mathMatrixAccessSymbol, includeStrings, false);
    }
/*
    public static String generateExecuteCodeFixForLoopAccess(MathMatrixAccessSymbol mathMatrixAccessSymbol, List<String> includeStrings) {
        String result = "";

        if (mathMatrixAccessSymbol.isDoubleDot())
            result += ":";
        else {
            MathConverter.fixMathFunctions(mathMatrixAccessSymbol.getMathExpressionSymbol().get(), currentBluePrint);
            result += generateExecuteCode(mathMatrixAccessSymbol.getMathExpressionSymbol().get(), includeStrings);
            //result += "-1";
        }
        return result;
    }*/

    public static String generateIfConditionCode(MathConditionalExpressionSymbol mathConditionalExpressionSymbol, List<String> includeStrings) {

        String result = "";
        //condition
        if (mathConditionalExpressionSymbol.getCondition().isPresent()) {
            result += "if(" + generateExecuteCode(mathConditionalExpressionSymbol.getCondition().get(), includeStrings) + ")";
        }
        //body
        result += "{\n";
        for (MathExpressionSymbol mathExpressionSymbol : mathConditionalExpressionSymbol.getBodyExpressions())
            result += generateExecuteCode(mathExpressionSymbol, includeStrings);
        result += "}\n";

        return result;
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
