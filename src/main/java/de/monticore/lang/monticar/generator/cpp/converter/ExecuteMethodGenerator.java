package de.monticore.lang.monticar.generator.cpp.converter;

import de.monticore.lang.math.math._symboltable.MathForLoopHeadSymbol;
import de.monticore.lang.math.math._symboltable.expression.*;
import de.monticore.lang.math.math._symboltable.matrix.*;
import de.monticore.lang.monticar.generator.Variable;
import de.monticore.lang.monticar.generator.cpp.MathFunctionFixer;
import de.monticore.lang.monticar.generator.cpp.OctaveHelper;
import de.monticore.lang.monticar.generator.cpp.symbols.MathChainedExpression;
import de.monticore.lang.monticar.generator.cpp.symbols.MathStringExpression;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sascha Schneiders
 */
public class ExecuteMethodGenerator {
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

            ComponentConverter.currentBluePrint.addVariable(var);
        } else {
            result += generateExecuteCode(mathValueSymbol.getType(), includeStrings);
            result += " " + mathValueSymbol.getName();
            if (mathValueSymbol.getValue() != null) {
                result += " = " + generateExecuteCode(mathValueSymbol.getValue(), includeStrings);
            }
            result += ";\n";
        }
        ComponentConverter.currentBluePrint.getMathInformationRegister().addVariable(mathValueSymbol);
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
            if (MathFunctionFixer.fixForLoopAccess(mathAssignmentExpressionSymbol.getNameOfMathValue(), ComponentConverter.currentBluePrint)) {

                String result = mathAssignmentExpressionSymbol.getNameOfMathValue();
                result += generateExecuteCode(mathAssignmentExpressionSymbol.getMathMatrixAccessOperatorSymbol(), includeStrings, true) + " ";
                result += mathAssignmentExpressionSymbol.getAssignmentOperator().getOperator() + " ";
                result += generateExecuteCode(mathAssignmentExpressionSymbol.getExpressionSymbol(), includeStrings) + ";\n";

                return result;

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
        result += ExecuteMethodGeneratorHelper.generateIfConditionCode(mathConditionalExpressionsSymbol.getIfConditionalExpression(), includeStrings);
        //else if condition
        for (MathConditionalExpressionSymbol mathConditionalExpressionSymbol : mathConditionalExpressionsSymbol.getIfElseConditionalExpressions())
            result += "else " + ExecuteMethodGeneratorHelper.generateIfConditionCode(mathConditionalExpressionSymbol, includeStrings);
        //else block
        if (mathConditionalExpressionsSymbol.getElseConditionalExpression().isPresent()) {
            result += "else " + ExecuteMethodGeneratorHelper.generateIfConditionCode(mathConditionalExpressionsSymbol.getElseConditionalExpression().get(), includeStrings);
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
        } else {

            result += /*"(" +*/ generateExecuteCode(mathMatrixArithmeticExpressionSymbol.getLeftExpression(), includeStrings) + " " + mathMatrixArithmeticExpressionSymbol.getMathOperator();

            if (mathMatrixArithmeticExpressionSymbol.getRightExpression() != null)
                result += " " + generateExecuteCode(mathMatrixArithmeticExpressionSymbol.getRightExpression(), includeStrings);
        }
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

        ExecuteMethodGeneratorHelper.handleForLoopAccessFix(mathMatrixAccessOperatorSymbol, counter, ignoreCounterAt, counterSetMinusOne, includeStrings);
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
}
