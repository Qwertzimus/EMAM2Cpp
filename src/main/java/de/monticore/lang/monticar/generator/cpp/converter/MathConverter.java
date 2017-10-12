package de.monticore.lang.monticar.generator.cpp.converter;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.PortSymbol;
import de.monticore.lang.math.math._symboltable.expression.*;
import de.monticore.lang.math.math._symboltable.matrix.*;
import de.monticore.lang.monticar.generator.MathCommand;
import de.monticore.lang.monticar.generator.TargetCodeInstruction;
import de.monticore.lang.monticar.generator.Variable;
import de.monticore.lang.monticar.generator.cpp.BluePrintCPP;
import de.monticore.lang.monticar.generator.cpp.symbols.MathChainedExpression;
import de.monticore.lang.monticar.generator.cpp.symbols.MathStringExpression;
import de.se_rwth.commons.logging.Log;

import java.lang.reflect.Type;

/**
 * @author Sascha Schneiders
 */
public class MathConverter {

    public static void fixMathFunctions(MathExpressionSymbol mathExpressionSymbol, BluePrintCPP bluePrintCPP) {
        boolean notHandled = true;
        if (mathExpressionSymbol.isAssignmentExpression()) {
            fixMathFunctions((MathAssignmentExpressionSymbol) mathExpressionSymbol, bluePrintCPP);
            notHandled = false;
        } else if (mathExpressionSymbol.isMatrixExpression()) {
            fixMathFunctions((MathMatrixExpressionSymbol) mathExpressionSymbol, bluePrintCPP);
            notHandled = false;
        } else if (mathExpressionSymbol.isArithmeticExpression()) {
            fixMathFunctions((MathArithmeticExpressionSymbol) mathExpressionSymbol, bluePrintCPP);
            notHandled = false;
        } else if (mathExpressionSymbol.isForLoopExpression()) {
            fixMathFunctions((MathForLoopExpressionSymbol) mathExpressionSymbol, bluePrintCPP);
            notHandled = false;
        } else if (mathExpressionSymbol.isCompareExpression()) {
            fixMathFunctions((MathCompareExpressionSymbol) mathExpressionSymbol, bluePrintCPP);
            notHandled = false;
        } else if (mathExpressionSymbol.isAssignmentDeclarationExpression()) {
            fixMathFunctions((MathValueSymbol) mathExpressionSymbol, bluePrintCPP);
            notHandled = false;
        } else if (mathExpressionSymbol.isParenthesisExpression()) {
            fixMathFunctions((MathParenthesisExpressionSymbol) mathExpressionSymbol, bluePrintCPP);
            notHandled = false;
        } else if (mathExpressionSymbol.isConditionalsExpression()) {
            fixMathFunctions((MathConditionalExpressionsSymbol) mathExpressionSymbol, bluePrintCPP);
            notHandled = false;
        } else if (mathExpressionSymbol.isConditionalExpression()) {
            fixMathFunctions((MathConditionalExpressionSymbol) mathExpressionSymbol, bluePrintCPP);
            notHandled = false;
        } else if (mathExpressionSymbol.isPreOperatorExpression()) {
            fixMathFunctions((MathPreOperatorExpressionSymbol) mathExpressionSymbol, bluePrintCPP);
            notHandled = false;
        } else if (mathExpressionSymbol.isValueExpression()) {
            MathValueExpressionSymbol mathValueExpressionSymbol = (MathValueExpressionSymbol) mathExpressionSymbol;
            if (mathValueExpressionSymbol.isNumberExpression()) {
                notHandled = false;
            } else if (((MathValueExpressionSymbol) mathExpressionSymbol).isNameExpression()) {
                notHandled = false;
            }
        } else if (mathExpressionSymbol.getExpressionID() == MathChainedExpression.ID) {
            fixMathFunctions((MathChainedExpression) mathExpressionSymbol, bluePrintCPP);
            notHandled = false;
        } else if (mathExpressionSymbol.getExpressionID() == MathStringExpression.ID) {
            notHandled = false;
            //MathStringExpressions should be correct
        }
        if (notHandled) {
            Log.info(mathExpressionSymbol.getClass().getName(), "Symbol name:");
            Log.info(mathExpressionSymbol.getTextualRepresentation(), "Symbol:");
            Log.error("Case not handled!");
        }
    }

    public static void fixMathFunctions(MathPreOperatorExpressionSymbol mathExpressionSymbol, BluePrintCPP bluePrintCPP) {
        fixMathFunctions(mathExpressionSymbol.getMathExpressionSymbol(), bluePrintCPP);
    }

    public static void fixMathFunctions(MathChainedExpression mathChainedExpression, BluePrintCPP bluePrintCPP) {
        if (mathChainedExpression.getFirstExpressionSymbol().isMatrixExpression()) {
            MathMatrixExpressionSymbol mathMatrixExpressionSymbol = (MathMatrixExpressionSymbol) mathChainedExpression.getFirstExpressionSymbol();
            if (mathMatrixExpressionSymbol.isMatrixAccessExpression()) {
                if (mathChainedExpression.getSecondExpressionSymbol().getExpressionID() == MathStringExpression.ID) {
                    MathStringExpression mathStringExpression = (MathStringExpression) mathChainedExpression.getSecondExpressionSymbol();
                    if (mathStringExpression.getText().equals("-1"))
                        return;
                }
            }
        }
        fixMathFunctions(mathChainedExpression.getFirstExpressionSymbol(), bluePrintCPP);
    }

    public static void fixMathFunctions(MathConditionalExpressionsSymbol mathExpressionSymbol, BluePrintCPP bluePrintCPP) {
        fixMathFunctions(mathExpressionSymbol.getIfConditionalExpression(), bluePrintCPP);
        for (MathConditionalExpressionSymbol mathConditionalExpressionSymbol : mathExpressionSymbol.getIfElseConditionalExpressions())
            fixMathFunctions(mathConditionalExpressionSymbol, bluePrintCPP);
        if (mathExpressionSymbol.getElseConditionalExpression().isPresent())
            fixMathFunctions(mathExpressionSymbol.getElseConditionalExpression().get(), bluePrintCPP);
    }

    public static void fixMathFunctions(MathConditionalExpressionSymbol mathExpressionSymbol, BluePrintCPP bluePrintCPP) {
        if (mathExpressionSymbol.getCondition().isPresent())
            fixMathFunctions(mathExpressionSymbol.getCondition().get(), bluePrintCPP);
        for (MathExpressionSymbol bodyExpression : mathExpressionSymbol.getBodyExpressions())
            fixMathFunctions(bodyExpression, bluePrintCPP);
    }

    public static void fixMathFunctions(MathValueSymbol mathExpressionSymbol, BluePrintCPP bluePrintCPP) {
        bluePrintCPP.getMathInformationRegister().addVariable(mathExpressionSymbol);
        if (mathExpressionSymbol.getValue() != null)
            fixMathFunctions(mathExpressionSymbol.getValue(), bluePrintCPP);
    }

    public static void fixMathFunctions(MathAssignmentExpressionSymbol mathExpressionSymbol, BluePrintCPP bluePrintCPP) {
        if (mathExpressionSymbol.getMathMatrixAccessOperatorSymbol() != null && fixForLoopAccess(mathExpressionSymbol.getNameOfMathValue(), bluePrintCPP)) {
            for (MathMatrixAccessSymbol mathMatrixAccessSymbol : mathExpressionSymbol.getMathMatrixAccessOperatorSymbol().getMathMatrixAccessSymbols())
                //fixMathFunctionsForLoopAccess(mathMatrixAccessSymbol, bluePrintCPP);
                fixMathFunctions(mathMatrixAccessSymbol, bluePrintCPP);

        }
        fixMathFunctions(mathExpressionSymbol.getExpressionSymbol(), bluePrintCPP);
    }

    //TODO find function which does not pass fixMathFunction/debug MathSumCommand
    public static void fixMathFunctions(MathMatrixExpressionSymbol mathExpressionSymbol, BluePrintCPP bluePrintCPP) {
        if (mathExpressionSymbol.isMatrixNameExpression()) {
            fixMathFunctions((MathMatrixNameExpressionSymbol) mathExpressionSymbol, bluePrintCPP);
        } else if (mathExpressionSymbol.isMatrixVectorExpression()) {
            fixMathFunctions((MathMatrixVectorExpressionSymbol) mathExpressionSymbol, bluePrintCPP);
        } else if (mathExpressionSymbol.isMatrixAccessExpression()) {
            fixMathFunctions((MathMatrixAccessSymbol) mathExpressionSymbol, bluePrintCPP);
        } else if (mathExpressionSymbol.isMatrixArithmeticExpression()) {
            fixMathFunctions((MathMatrixArithmeticExpressionSymbol) mathExpressionSymbol, bluePrintCPP);
        } else if (mathExpressionSymbol.isValueExpression()) {
            //Nothing to do here
        } else {
            Log.info(mathExpressionSymbol.getClass().getName(), "Symbol name:");
            Log.info(mathExpressionSymbol.getTextualRepresentation(), "Symbol:");
            Log.error("MathConverter Case not handled!");
        }
    }

    public static void fixMathFunctions(MathMatrixArithmeticExpressionSymbol mathExpressionSymbol, BluePrintCPP bluePrintCPP) {
        fixMathFunctions(mathExpressionSymbol.getLeftExpression(), bluePrintCPP);
        if (mathExpressionSymbol.getLeftExpression() != null)
            fixMathFunctions(mathExpressionSymbol.getRightExpression(), bluePrintCPP);
    }

    public static void fixMathFunctions(MathMatrixNameExpressionSymbol mathExpressionSymbol, BluePrintCPP bluePrintCPP) {
        String name = PortSymbol.getNameWithoutArrayBracketPart(mathExpressionSymbol.getNameToAccess());
        Variable variable = bluePrintCPP.getVariable(name).orElse(null);
        //change () to [] if it is a variable and no function
        if (variable != null && variable.isArray()) {
            Log.info(name, "Fixing variable array access:");
            mathExpressionSymbol.getMathMatrixAccessOperatorSymbol().setAccessStartSymbol("[");
            mathExpressionSymbol.getMathMatrixAccessOperatorSymbol().setAccessEndSymbol("]");
            for (MathMatrixAccessSymbol mathMatrixAccessSymbol : mathExpressionSymbol.getMathMatrixAccessOperatorSymbol().getMathMatrixAccessSymbols()) {
                fixMathFunctionsForLoopAccess(mathMatrixAccessSymbol, bluePrintCPP);
            }

        } else {

            MathCommand mathCommand = bluePrintCPP.getMathCommandRegister().getMathCommand(mathExpressionSymbol.getNameToAccess());

            if (mathCommand != null) {
                bluePrintCPP.addAdditionalIncludeString("Helper");
                mathCommand.convert(mathExpressionSymbol, bluePrintCPP);
            }
            if (fixForLoopAccess(mathExpressionSymbol, variable, bluePrintCPP)) {
                for (MathMatrixAccessSymbol mathMatrixAccessSymbol : mathExpressionSymbol.getMathMatrixAccessOperatorSymbol().getMathMatrixAccessSymbols()) {
                    //fixMathFunctionsForLoopAccess(mathMatrixAccessSymbol, bluePrintCPP);
                    fixMathFunctions(mathMatrixAccessSymbol, bluePrintCPP);
                }

            } else {
                for (MathMatrixAccessSymbol mathMatrixAccessSymbol : mathExpressionSymbol.getMathMatrixAccessOperatorSymbol().getMathMatrixAccessSymbols()) {
                    fixMathFunctions(mathMatrixAccessSymbol, bluePrintCPP);
                }
            }
        }
    }

    public static void fixMathFunctions(MathMatrixAccessSymbol mathExpressionSymbol, BluePrintCPP bluePrintCPP) {
        if (mathExpressionSymbol.getMathExpressionSymbol().isPresent()) {
            fixMathFunctions(mathExpressionSymbol.getMathExpressionSymbol().get(), bluePrintCPP);
            MathExpressionSymbol mathExp = mathExpressionSymbol.getMathExpressionSymbol().get();
            /*if (mathExp.getExpressionID() != MathChainedExpression.ID && mathExp.getExpressionID() != MathStringExpression.ID) {

                mathExpressionSymbol.setMathExpressionSymbol(new MathChainedExpression(mathExp, new MathStringExpression("-1")));
            }*/

            //FOR LOOP VARIABLE SUBTRACTION
              /*  Variable var = bluePrintCPP.getMathInformationRegister().getVariable(mathExp.getTextualRepresentation());
                if (var != null)
                    Log.info(var.getName(), "fixMathFunctions VAR:");
                else
                    Log.info(mathExp.getTextualRepresentation(), "VAR not found:");
                if (var != null && var.isForLoopVariable())
                */


        }

    }

    public static void fixMathFunctionsForLoopAccess(MathMatrixAccessSymbol mathExpressionSymbol, BluePrintCPP bluePrintCPP) {
        if (mathExpressionSymbol.getMathExpressionSymbol().isPresent()) {
            fixMathFunctions(mathExpressionSymbol.getMathExpressionSymbol().get(), bluePrintCPP);
            MathExpressionSymbol mathExp = mathExpressionSymbol.getMathExpressionSymbol().get();
            mathExpressionSymbol.setMathExpressionSymbol(new MathChainedExpression(mathExp, new MathStringExpression("-1")));

            /*if (mathExp.getExpressionID() != MathChainedExpression.ID && mathExp.getExpressionID() != MathStringExpression.ID) {

                mathExpressionSymbol.setMathExpressionSymbol(new MathChainedExpression(mathExp, new MathStringExpression("-1")));
            }*/

            //FOR LOOP VARIABLE SUBTRACTION
              /*  Variable var = bluePrintCPP.getMathInformationRegister().getVariable(mathExp.getTextualRepresentation());
                if (var != null)
                    Log.info(var.getName(), "fixMathFunctions VAR:");
                else
                    Log.info(mathExp.getTextualRepresentation(), "VAR not found:");
                if (var != null && var.isForLoopVariable())
                */


        }

    }


    public static void fixMathFunctions(MathParenthesisExpressionSymbol mathExpressionSymbol, BluePrintCPP bluePrintCPP) {
        fixMathFunctions(mathExpressionSymbol.getMathExpressionSymbol(), bluePrintCPP);
    }

    public static void fixMathFunctions(MathArithmeticExpressionSymbol mathExpressionSymbol, BluePrintCPP bluePrintCPP) {
        fixMathFunctions(mathExpressionSymbol.getLeftExpression(), bluePrintCPP);
        fixMathFunctions(mathExpressionSymbol.getRightExpression(), bluePrintCPP);
    }

    public static void fixMathFunctions(MathForLoopExpressionSymbol mathExpressionSymbol, BluePrintCPP bluePrintCPP) {
        ComponentConverter.currentBluePrint.getMathInformationRegister().addVariable(new Variable(mathExpressionSymbol.getForLoopHead().getNameLoopVariable(), Variable.FORLOOPINFO));
        fixMathFunctions(mathExpressionSymbol.getForLoopHead().getMathExpression(), bluePrintCPP);
        for (MathExpressionSymbol mathExpressionSymbol1 : mathExpressionSymbol.getForLoopBody())
            fixMathFunctions(mathExpressionSymbol1, bluePrintCPP);
    }

    public static void fixMathFunctions(MathCompareExpressionSymbol mathExpressionSymbol, BluePrintCPP bluePrintCPP) {
        fixMathFunctions(mathExpressionSymbol.getLeftExpression(), bluePrintCPP);
        fixMathFunctions(mathExpressionSymbol.getRightExpression(), bluePrintCPP);
    }

    public static void fixMathFunctions(MathMatrixVectorExpressionSymbol mathExpressionSymbol, BluePrintCPP bluePrintCPP) {
        fixMathFunctions(mathExpressionSymbol.getStart(), bluePrintCPP);
        if (mathExpressionSymbol.getStep().isPresent())
            fixMathFunctions(mathExpressionSymbol.getStep().get(), bluePrintCPP);
        fixMathFunctions(mathExpressionSymbol.getEnd(), bluePrintCPP);
    }

   /* public static void fixMathFunctions(MathExpressionSymbol mathExpressionSymbol, BluePrintCPP bluePrintCPP) {

    }*/

    public static boolean fixForLoopAccess(MathMatrixNameExpressionSymbol mathExpressionSymbol, BluePrintCPP bluePrintCPP) {
        Variable variable = getVariableFromBluePrint(mathExpressionSymbol, bluePrintCPP);
        return fixForLoopAccess(mathExpressionSymbol, variable, bluePrintCPP);
    }

    public static boolean fixForLoopAccess(String nameToAccess, BluePrintCPP bluePrintCPP) {
        Variable variable = getVariableFromBluePrint(nameToAccess, bluePrintCPP);
        if (variable == null)
            variable = bluePrintCPP.getMathInformationRegister().getVariable(nameToAccess);
        return fixForLoopAccess(nameToAccess, variable, bluePrintCPP);
    }

    public static boolean fixForLoopAccess(MathMatrixNameExpressionSymbol mathExpressionSymbol, Variable variable, BluePrintCPP bluePrintCPP) {
        return fixForLoopAccess(mathExpressionSymbol.getNameToAccess(), variable, bluePrintCPP);
    }

    public static boolean fixForLoopAccess(String nameToAccess, Variable variable, BluePrintCPP bluePrintCPP) {
        MathCommand mathCommand = bluePrintCPP.getMathCommandRegister().getMathCommand(nameToAccess);

        if (mathCommand == null) {
            if (variable != null && variable.getVariableType() != null && variable.getVariableType().getTypeNameTargetLanguage() != null && variable.getVariableType().getTypeNameTargetLanguage().equals("Matrix")) {
                return true;
            }
        }
        return false;
    }

    public static Variable getVariableFromBluePrint(MathMatrixNameExpressionSymbol mathExpressionSymbol, BluePrintCPP bluePrintCPP) {
        return getVariableFromBluePrint(mathExpressionSymbol.getNameToAccess(), bluePrintCPP);
    }

    public static Variable getVariableFromBluePrint(String namey, BluePrintCPP bluePrintCPP) {
        String name = PortSymbol.getNameWithoutArrayBracketPart(namey);
        Variable variable = bluePrintCPP.getVariable(name).orElse(null);
        return variable;
    }

    public static String getConstantConversion(MathExpressionSymbol mathExpressionSymbol) {
        if (mathExpressionSymbol.isMatrixExpression()) {
            MathMatrixExpressionSymbol matrixExpressionSymbol = (MathMatrixExpressionSymbol) mathExpressionSymbol;
            //TODO handle matrix/rowvector/columnvector conversion
            //mathExpressionSymbol.is
            if (matrixExpressionSymbol.isValueExpression()) {
                return getConstantConversion((MathMatrixArithmeticValueSymbol) matrixExpressionSymbol);
            }
            return "";
        } else {
            return mathExpressionSymbol.getTextualRepresentation();
        }

    }

    public static String getConstantConversion(MathMatrixArithmeticValueSymbol mathExpressionSymbol) {
        String constantName = "CONSTANTCONSTANTVECTOR" + getNextConstantConstantVectorID();

        String instructionString = getInstructionStringConstantVectorExpression(mathExpressionSymbol, constantName, TypeConverter.getTypeName(mathExpressionSymbol));

        TargetCodeInstruction instruction = new TargetCodeInstruction(instructionString);

        ComponentConverter.currentBluePrint.addInstructionToMethod(instruction, "init");

        Variable variable = new Variable();
        variable.setName(constantName);
        variable.setVariableType(TypeConverter.getVariableTypeForTargetLanguageTypeName(TypeConverter.getTypeName(mathExpressionSymbol)));

        ComponentConverter.currentBluePrint.addVariable(variable);

        return constantName;
    }

    public static String getInstructionStringConstantVectorExpression(MathMatrixArithmeticValueSymbol mathExpressionSymbol, String matrixName, String typeName) {
        String result = "";
        int column = 0;
        for (MathMatrixAccessOperatorSymbol symbol : mathExpressionSymbol.getVectors()) {
            System.out.println(symbol.getTextualRepresentation());
            int row = 0;
            for (MathMatrixAccessSymbol symbolAccess : symbol.getMathMatrixAccessSymbols()) {
                System.out.println(symbolAccess.getTextualRepresentation());
                result += matrixName + "(" + column + "," + row + ") = ";
                result += symbolAccess.getTextualRepresentation();
                result += ";\n";
                ++row;
            }
            ++column;
        }
        String firstPart = matrixName+" = " + typeName;
        if (typeName.equals("RowVector")) {
            firstPart += "(" + mathExpressionSymbol.getVectors().get(0).getMathMatrixAccessSymbols().size() + ");\n";
        } else if (typeName.equals("ColumnVector")) {
            firstPart += "(" + mathExpressionSymbol.getVectors().size() + ");\n";
        } else if (typeName.equals("Matrix")) {

            firstPart += "(" + mathExpressionSymbol.getVectors().get(0).getMathMatrixAccessSymbols().size() + "," + mathExpressionSymbol.getVectors().size() + ");\n";
        }
        return firstPart + result;
    }


    public static int CONSTANTCONSTANTVECTORID = 0;

    public static void resetIDs() {
        CONSTANTCONSTANTVECTORID = 0;
    }

    public static int getNextConstantConstantVectorID() {
        return CONSTANTCONSTANTVECTORID++;
    }
}
