package de.monticore.lang.monticar.generator.optimization;

import de.monticore.lang.math.math._symboltable.expression.*;
import de.monticore.lang.math.math._symboltable.matrix.MathMatrixArithmeticExpressionSymbol;
import de.monticore.lang.math.math._symboltable.matrix.MathMatrixExpressionSymbol;
import de.monticore.lang.math.math._symboltable.matrix.MathMatrixNameExpressionSymbol;
import de.monticore.lang.monticar.generator.cpp.BluePrintCPP;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sascha Schneiders
 */
public class MathOptimizer {
    public static BluePrintCPP currentBluePrint;
    public static List<MathOptimizationRule> optimizationRules = new ArrayList<>();

    public static void addOptimizationRule(MathOptimizationRule mathOptimizationRule) {
        optimizationRules.add(mathOptimizationRule);
    }

    public static MathExpressionSymbol applyOptimizations(MathExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {
        MathExpressionSymbol currentMathExpressionSymbol = mathExpressionSymbol;
        for (MathOptimizationRule mathOptimizationRule : optimizationRules) {
            mathOptimizationRule.optimize(currentMathExpressionSymbol, precedingExpressions);
        }
        return currentMathExpressionSymbol;
    }

    public static MathExpressionSymbol getCurrentAssignment(MathExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressionSymbols) {
        if (mathExpressionSymbol == null)
            return mathExpressionSymbol;
        if (mathExpressionSymbol.isValueExpression()) {
            if (((MathValueExpressionSymbol) mathExpressionSymbol).isNameExpression()) {
                return getCurrentAssignment((MathNameExpressionSymbol) mathExpressionSymbol, precedingExpressionSymbols);
            }
        } else if (mathExpressionSymbol.isMatrixExpression()) {
            if (((MathMatrixExpressionSymbol) mathExpressionSymbol).isMatrixNameExpression()) {
                return MathOptimizer.getCurrentAssignment((MathMatrixNameExpressionSymbol) mathExpressionSymbol, precedingExpressionSymbols);
            }
        } else if (mathExpressionSymbol.isParenthesisExpression()) {
            return getCurrentAssignment(((MathParenthesisExpressionSymbol) mathExpressionSymbol).getMathExpressionSymbol(), precedingExpressionSymbols);
        }

        return mathExpressionSymbol;
    }

    public static MathExpressionSymbol getCurrentAssignment(MathMatrixNameExpressionSymbol mathMatrixNameExpressionSymbol, List<MathExpressionSymbol> precedingExpressionSymbols) {
        MathExpressionSymbol currentAssignment = mathMatrixNameExpressionSymbol;

        for (MathExpressionSymbol expressionSymbol : precedingExpressionSymbols) {
            if (expressionSymbol.isAssignmentDeclarationExpression()) {
                MathValueSymbol mathValueSymbol = (MathValueSymbol) expressionSymbol;
                if (mathValueSymbol.getName().equals(mathMatrixNameExpressionSymbol.getNameToAccess())) {
                    currentAssignment = getCurrentAssignment(mathValueSymbol.getValue(), precedingExpressionSymbols);
                }
            } else if (expressionSymbol.isAssignmentExpression()) {
                MathAssignmentExpressionSymbol mathAssignmentExpressionSymbol = (MathAssignmentExpressionSymbol) expressionSymbol;
                if (mathAssignmentExpressionSymbol.getAssignmentOperator().getOperator().equals("=") && mathAssignmentExpressionSymbol.getNameOfMathValue().equals(mathMatrixNameExpressionSymbol.getNameToAccess())) {
                    currentAssignment = getCurrentAssignment(mathAssignmentExpressionSymbol.getExpressionSymbol(), precedingExpressionSymbols);
                }
            }
        }
        return currentAssignment;
    }

    public static MathExpressionSymbol getCurrentAssignment(MathNameExpressionSymbol mathNameExpressionSymbol, List<MathExpressionSymbol> precedingExpressionSymbols) {
        MathExpressionSymbol currentAssignment = mathNameExpressionSymbol;

        for (MathExpressionSymbol expressionSymbol : precedingExpressionSymbols) {
            if (expressionSymbol.isAssignmentDeclarationExpression()) {
                MathValueSymbol mathValueSymbol = (MathValueSymbol) expressionSymbol;
                if (mathValueSymbol.getName().equals(mathNameExpressionSymbol.getNameToResolveValue())) {
                    currentAssignment = getCurrentAssignment(mathValueSymbol.getValue(), precedingExpressionSymbols);
                }
            } else if (expressionSymbol.isAssignmentExpression()) {
                MathAssignmentExpressionSymbol mathAssignmentExpressionSymbol = (MathAssignmentExpressionSymbol) expressionSymbol;
                if (mathAssignmentExpressionSymbol.getAssignmentOperator().getOperator().equals("=") && mathAssignmentExpressionSymbol.getNameOfMathValue().equals(mathNameExpressionSymbol.getNameToResolveValue())) {
                    currentAssignment = getCurrentAssignment(mathAssignmentExpressionSymbol.getExpressionSymbol(), precedingExpressionSymbols);
                }
            }
        }
        return currentAssignment;
    }


    public static int getMatrixColumns(MathExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {
        if (mathExpressionSymbol.isMatrixExpression()) {
            return getMatrixColumns((MathMatrixExpressionSymbol) mathExpressionSymbol, precedingExpressions);
        } else if (mathExpressionSymbol.isArithmeticExpression()) {
            return getMatrixColumns((MathArithmeticExpressionSymbol) mathExpressionSymbol, precedingExpressions);
        } else if (mathExpressionSymbol.isValueExpression()) {
            return getMatrixColumns((MathValueExpressionSymbol) mathExpressionSymbol, precedingExpressions);
        } else if (mathExpressionSymbol.isParenthesisExpression()) {
            return getMatrixColumns((MathParenthesisExpressionSymbol) mathExpressionSymbol, precedingExpressions);
        } else if (mathExpressionSymbol.isPreOperatorExpression()) {
            return getMatrixColumns(((MathPreOperatorExpressionSymbol) mathExpressionSymbol).getMathExpressionSymbol(), precedingExpressions);
        }
        Log.info(mathExpressionSymbol.getClass().getName(), "Not handled:");
        return 0;
    }

    public static int getMatrixColumns(MathMatrixExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {
        if (mathExpressionSymbol.isMatrixNameExpression()) {
            return getMatrixColumns((MathMatrixNameExpressionSymbol) mathExpressionSymbol, precedingExpressions);
        } else if (mathExpressionSymbol.isMatrixArithmeticExpression()) {
            return getMatrixColumns((MathMatrixArithmeticExpressionSymbol) mathExpressionSymbol, precedingExpressions);
        }
        Log.info(mathExpressionSymbol.getClass().getName(), "Not handled:");
        return 0;
    }


    public static int getMatrixColumns(MathMatrixArithmeticExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {

        if (mathExpressionSymbol.getMathOperator().equals("+")) {
            MathExpressionSymbol realLeftExpression = getCurrentAssignment(mathExpressionSymbol.getLeftExpression(), precedingExpressions);
            MathExpressionSymbol realRightExpression = getCurrentAssignment(mathExpressionSymbol.getRightExpression(), precedingExpressions);

            return getMatrixColumns(realLeftExpression, precedingExpressions);
        } else if (mathExpressionSymbol.getMathOperator().equals("*")) {
            MathExpressionSymbol realLeftExpression = getCurrentAssignment(mathExpressionSymbol.getLeftExpression(), precedingExpressions);
            MathExpressionSymbol realRightExpression = getCurrentAssignment(mathExpressionSymbol.getRightExpression(), precedingExpressions);

            return getMatrixColumns(realLeftExpression, precedingExpressions);
        }
        Log.info(mathExpressionSymbol.getClass().getName(), "Not handled:");
        return 0;
    }


    public static int getMatrixColumns(MathArithmeticExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {

        if (mathExpressionSymbol.getMathOperator().equals("+")) {
            MathExpressionSymbol realLeftExpression = getCurrentAssignment(mathExpressionSymbol.getLeftExpression(), precedingExpressions);
            MathExpressionSymbol realRightExpression = getCurrentAssignment(mathExpressionSymbol.getRightExpression(), precedingExpressions);
            if (getMatrixRows(realRightExpression, precedingExpressions) == 1) {
                return getMatrixColumns(realLeftExpression, precedingExpressions);
            }
            return getMatrixColumns(realLeftExpression, precedingExpressions);
        } else if (mathExpressionSymbol.getMathOperator().equals("*")) {
            MathExpressionSymbol realLeftExpression = getCurrentAssignment(mathExpressionSymbol.getLeftExpression(), precedingExpressions);
            MathExpressionSymbol realRightExpression = getCurrentAssignment(mathExpressionSymbol.getRightExpression(), precedingExpressions);
            if (getMatrixRows(realRightExpression, precedingExpressions) == 1) {
                return getMatrixColumns(realLeftExpression, precedingExpressions);
            }
            return getMatrixColumns(realLeftExpression, precedingExpressions);
        }
        Log.info(mathExpressionSymbol.getClass().getName(), "Not handled:");
        return 0;
    }


    public static int getMatrixColumns(MathMatrixNameExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {
        MathExpressionSymbol currentAssignment = getCurrentAssignment(mathExpressionSymbol, precedingExpressions);
        if (mathExpressionSymbol.equals(currentAssignment)) {
            return currentBluePrint.getMathInformationRegister().getAmountColumns(mathExpressionSymbol.getNameToAccess(), mathExpressionSymbol.getMathMatrixAccessOperatorSymbol());
        }
        return getMatrixColumns(currentAssignment, precedingExpressions);
    }

    public static int getMatrixColumns(MathValueExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {
        if (mathExpressionSymbol.isNameExpression()) {
            return getMatrixColumns((MathNameExpressionSymbol) mathExpressionSymbol, precedingExpressions);
        } else if (mathExpressionSymbol.isNumberExpression()) {
            return 1;
        } else if (mathExpressionSymbol.isValueExpression()) {
            return getMatrixColumns((MathValueSymbol) mathExpressionSymbol, precedingExpressions);
        }
        Log.info(mathExpressionSymbol.getClass().getName(), "Not handled:");
        return 0;
    }

    public static int getMatrixColumns(MathNameExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {
        MathExpressionSymbol currentExpression = getCurrentAssignment(mathExpressionSymbol, precedingExpressions);
        if (currentExpression.equals(mathExpressionSymbol)) {


            return currentBluePrint.getMathInformationRegister().getAmountColumns(mathExpressionSymbol.getNameToResolveValue());
        } else {
            return getMatrixColumns(currentExpression, precedingExpressions);
        }
    }

    public static int getMatrixColumns(MathValueSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {
        return getMatrixColumns(mathExpressionSymbol.getValue(), precedingExpressions);
    }

    public static int getMatrixColumns(MathParenthesisExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {
        return getMatrixColumns(mathExpressionSymbol.getMathExpressionSymbol(), precedingExpressions);
    }

    public static int getMatrixRows(MathExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {
        if (mathExpressionSymbol.isMatrixExpression()) {
            return getMatrixRows((MathMatrixExpressionSymbol) mathExpressionSymbol, precedingExpressions);
        } else if (mathExpressionSymbol.isArithmeticExpression()) {
            return getMatrixRows((MathArithmeticExpressionSymbol) mathExpressionSymbol, precedingExpressions);
        } else if (mathExpressionSymbol.isValueExpression()) {
            return getMatrixRows((MathValueExpressionSymbol) mathExpressionSymbol, precedingExpressions);
        } else if (mathExpressionSymbol.isParenthesisExpression()) {
            return getMatrixRows((MathParenthesisExpressionSymbol) mathExpressionSymbol, precedingExpressions);
        } else if (mathExpressionSymbol.isPreOperatorExpression()) {
            return getMatrixRows(((MathPreOperatorExpressionSymbol) mathExpressionSymbol).getMathExpressionSymbol(), precedingExpressions);
        }
        Log.info(mathExpressionSymbol.getClass().getName(), "Not handled:");
        return 0;
    }

    public static int getMatrixRows(MathMatrixExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {
        if (mathExpressionSymbol.isMatrixNameExpression()) {
            return getMatrixRows((MathMatrixNameExpressionSymbol) mathExpressionSymbol, precedingExpressions);
        } else if (mathExpressionSymbol.isMatrixArithmeticExpression()) {
            return getMatrixRows((MathMatrixArithmeticExpressionSymbol) mathExpressionSymbol, precedingExpressions);
        }
        Log.info(mathExpressionSymbol.getClass().getName(), "Not handled:");
        return 0;
    }


    public static int getMatrixRows(MathMatrixArithmeticExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {

        if (mathExpressionSymbol.getMathOperator().equals("+")) {
            MathExpressionSymbol realLeftExpression = getCurrentAssignment(mathExpressionSymbol.getLeftExpression(), precedingExpressions);
            MathExpressionSymbol realRightExpression = getCurrentAssignment(mathExpressionSymbol.getRightExpression(), precedingExpressions);

            return getMatrixRows(realLeftExpression, precedingExpressions);
        } else if (mathExpressionSymbol.getMathOperator().equals("*")) {
            MathExpressionSymbol realLeftExpression = getCurrentAssignment(mathExpressionSymbol.getLeftExpression(), precedingExpressions);
            MathExpressionSymbol realRightExpression = getCurrentAssignment(mathExpressionSymbol.getRightExpression(), precedingExpressions);

            return getMatrixRows(realLeftExpression, precedingExpressions);
        }
        Log.info(mathExpressionSymbol.getClass().getName(), "Not handled:");
        return 0;
    }

    public static int getMatrixRows(MathArithmeticExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {

        if (mathExpressionSymbol.getMathOperator().equals("+")) {
            MathExpressionSymbol realLeftExpression = getCurrentAssignment(mathExpressionSymbol.getLeftExpression(), precedingExpressions);
            MathExpressionSymbol realRightExpression = getCurrentAssignment(mathExpressionSymbol.getRightExpression(), precedingExpressions);
            if (getMatrixColumns(realRightExpression, precedingExpressions) == 1) {
                return getMatrixRows(realLeftExpression, precedingExpressions);
            }
            return getMatrixRows(realRightExpression, precedingExpressions);
        } else if (mathExpressionSymbol.getMathOperator().equals("*")) {
            MathExpressionSymbol realLeftExpression = getCurrentAssignment(mathExpressionSymbol.getLeftExpression(), precedingExpressions);
            MathExpressionSymbol realRightExpression = getCurrentAssignment(mathExpressionSymbol.getRightExpression(), precedingExpressions);
            if (getMatrixColumns(realRightExpression, precedingExpressions) == 1) {
                return getMatrixRows(realLeftExpression, precedingExpressions);
            }
            return getMatrixRows(realRightExpression, precedingExpressions);
        }
        Log.info(mathExpressionSymbol.getClass().getName(), "Not handled:");
        return 0;
    }

    public static int getMatrixRows(MathMatrixNameExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {
        MathExpressionSymbol currentAssignment = getCurrentAssignment(mathExpressionSymbol, precedingExpressions);
        if (mathExpressionSymbol.equals(currentAssignment)) {
            return currentBluePrint.getMathInformationRegister().getAmountRows(mathExpressionSymbol.getNameToAccess(), mathExpressionSymbol.getMathMatrixAccessOperatorSymbol());
        }
        return getMatrixRows(currentAssignment, precedingExpressions);
    }

    public static int getMatrixRows(MathValueExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {
        if (mathExpressionSymbol.isNameExpression()) {
            return getMatrixRows((MathNameExpressionSymbol) mathExpressionSymbol, precedingExpressions);
        } else if (mathExpressionSymbol.isNumberExpression()) {
            return 1;
        } else if (mathExpressionSymbol.isValueExpression()) {
            return getMatrixRows((MathValueSymbol) mathExpressionSymbol, precedingExpressions);
        }
        Log.info(mathExpressionSymbol.getClass().getName(), "Not handled:");
        return 0;
    }

    public static int getMatrixRows(MathNameExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {
        MathExpressionSymbol currentExpression = getCurrentAssignment(mathExpressionSymbol, precedingExpressions);
        if (currentExpression.equals(mathExpressionSymbol)) {

            return currentBluePrint.getMathInformationRegister().getAmountRows(mathExpressionSymbol.getNameToResolveValue());
        } else {
            return getMatrixRows(currentExpression, precedingExpressions);
        }
    }

    public static int getMatrixRows(MathValueSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {
        return getMatrixRows(mathExpressionSymbol.getValue(), precedingExpressions);
    }

    public static int getMatrixRows(MathParenthesisExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {
        return getMatrixRows(mathExpressionSymbol.getMathExpressionSymbol(), precedingExpressions);
    }

    public static int getEstimatedOperations(MathExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {
        if (mathExpressionSymbol.isArithmeticExpression()) {
            return getEstimatedOperations((MathArithmeticExpressionSymbol) mathExpressionSymbol, precedingExpressions);
        } else if (mathExpressionSymbol.isMatrixExpression()) {
            return getEstimatedOperations((MathMatrixArithmeticExpressionSymbol) mathExpressionSymbol, precedingExpressions);
        } else if (mathExpressionSymbol.isParenthesisExpression()) {
            return getEstimatedOperations(((MathParenthesisExpressionSymbol) mathExpressionSymbol).getMathExpressionSymbol(), precedingExpressions);
        }
        // Log.error("A");
        return -1;
    }

    public static int getEstimatedOperations(MathArithmeticExpressionSymbol mathArithmeticExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {
        if (mathArithmeticExpressionSymbol.getMathOperator().equals("*") && mathArithmeticExpressionSymbol.getLeftExpression() != null && mathArithmeticExpressionSymbol.getRightExpression() != null) {
            MathExpressionSymbol realLeftExpressionSymbol = getCurrentAssignment(mathArithmeticExpressionSymbol.getLeftExpression().getRealMathExpressionSymbol(), precedingExpressions);
            MathExpressionSymbol realRightExpressionSymbol = getCurrentAssignment(mathArithmeticExpressionSymbol.getRightExpression().getRealMathExpressionSymbol(), precedingExpressions);
            int n = getMatrixColumns(realLeftExpressionSymbol, precedingExpressions);
            int m = getMatrixRows(realLeftExpressionSymbol, precedingExpressions);
            int p = getMatrixRows(realRightExpressionSymbol, precedingExpressions);
            int previousOperations = 0;
            if (isArithmeticExpression(realLeftExpressionSymbol, "*", precedingExpressions)) {
                previousOperations += getEstimatedOperations(realLeftExpressionSymbol, precedingExpressions);
            } else if (isArithmeticExpression(realLeftExpressionSymbol, "+", precedingExpressions)) {
                previousOperations += getEstimatedOperations(realLeftExpressionSymbol, precedingExpressions);
            }
            if (isArithmeticExpression(realRightExpressionSymbol, "*", precedingExpressions)) {
                previousOperations += getEstimatedOperations(realRightExpressionSymbol, precedingExpressions);
            } else if (isArithmeticExpression(realRightExpressionSymbol, "+", precedingExpressions)) {
                previousOperations += getEstimatedOperations(realRightExpressionSymbol, precedingExpressions);
            }
            Log.info(mathArithmeticExpressionSymbol.getTextualRepresentation(), "Current Equation:");
            Log.info(n + "", "n:");
            Log.info(m + "", "m:");
            Log.info(p + "", "p:");
            Log.info("" + (previousOperations), "Previous Operations:");
            Log.info("" + (n * m * p), "New Operations:");
            Log.info("" + (previousOperations + n * m * p), "Total Operations:");
            return previousOperations + n * m * p;

        } else if (mathArithmeticExpressionSymbol.getMathOperator().equals("+")) {
            MathExpressionSymbol realLeftExpressionSymbol = getCurrentAssignment(mathArithmeticExpressionSymbol.getLeftExpression().getRealMathExpressionSymbol(), precedingExpressions);
            MathExpressionSymbol realRightExpressionSymbol = getCurrentAssignment(mathArithmeticExpressionSymbol.getRightExpression().getRealMathExpressionSymbol(), precedingExpressions);
            int previousOperations = 0;
            int n = getMatrixColumns(realLeftExpressionSymbol, precedingExpressions);
            int m = getMatrixRows(realRightExpressionSymbol, precedingExpressions);
            if (isArithmeticExpression(realLeftExpressionSymbol, "*", precedingExpressions)) {
                previousOperations += getEstimatedOperations(realLeftExpressionSymbol, precedingExpressions);
            } else if (isArithmeticExpression(realLeftExpressionSymbol, "+", precedingExpressions)) {
                previousOperations += getEstimatedOperations(realLeftExpressionSymbol, precedingExpressions);
            }
            if (isArithmeticExpression(realRightExpressionSymbol, "*", precedingExpressions)) {
                previousOperations += getEstimatedOperations(realRightExpressionSymbol, precedingExpressions);
            } else if (isArithmeticExpression(realRightExpressionSymbol, "+", precedingExpressions)) {
                previousOperations += getEstimatedOperations(realRightExpressionSymbol, precedingExpressions);
            }
            Log.info(mathArithmeticExpressionSymbol.getTextualRepresentation(), "Current Equation:");
            Log.info(n + "", "n:");
            Log.info(m + "", "m:");
            Log.info("" + (previousOperations), "Previous Operations:");
            Log.info("" + (n * m), "New Operations:");
            Log.info("" + (previousOperations + n * m), "Total Operations:");
            return previousOperations + getMatrixColumns(realLeftExpressionSymbol, precedingExpressions) * getMatrixRows(realRightExpressionSymbol, precedingExpressions);
        }
        Log.info(mathArithmeticExpressionSymbol.getClass().getName() + " operator: " + mathArithmeticExpressionSymbol.getMathOperator(), "Not handled:");
        return 0;
    }


    public static int getEstimatedOperations(MathMatrixArithmeticExpressionSymbol mathArithmeticExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {
        if (mathArithmeticExpressionSymbol.getMathOperator().equals("*")) {
            MathExpressionSymbol realLeftExpressionSymbol = getCurrentAssignment(mathArithmeticExpressionSymbol.getLeftExpression().getRealMathExpressionSymbol(), precedingExpressions);
            MathExpressionSymbol realRightExpressionSymbol = getCurrentAssignment(mathArithmeticExpressionSymbol.getRightExpression().getRealMathExpressionSymbol(), precedingExpressions);

            return getMatrixColumns(realLeftExpressionSymbol, precedingExpressions) * getMatrixRows(realLeftExpressionSymbol, precedingExpressions) * getMatrixRows(realRightExpressionSymbol, precedingExpressions);

        } else if (mathArithmeticExpressionSymbol.getMathOperator().equals("+")) {
            MathExpressionSymbol realLeftExpressionSymbol = getCurrentAssignment(mathArithmeticExpressionSymbol.getLeftExpression().getRealMathExpressionSymbol(), precedingExpressions);
            MathExpressionSymbol realRightExpressionSymbol = getCurrentAssignment(mathArithmeticExpressionSymbol.getRightExpression().getRealMathExpressionSymbol(), precedingExpressions);

            return getMatrixColumns(realLeftExpressionSymbol, precedingExpressions) * getMatrixRows(realRightExpressionSymbol, precedingExpressions);
        }
        Log.info(mathArithmeticExpressionSymbol.getClass().getName() + " operator: " + mathArithmeticExpressionSymbol.getMathOperator(), "Not handled:");
        return 0;
    }

    /**
     * Returns true if the expression is any kind of arithmetic expressions which has the provided operator as operator.
     */
    public static boolean isArithmeticExpression(MathExpressionSymbol mathExpressionSymbol, String operator, List<MathExpressionSymbol> precedingExpressionSymbols) {
        MathExpressionSymbol currentMathExpressionSymbol = mathExpressionSymbol;
        if (currentMathExpressionSymbol.isValueExpression()) {
            MathValueExpressionSymbol mathValueExpressionSymbol = (MathValueExpressionSymbol) currentMathExpressionSymbol;
            if (mathValueExpressionSymbol.isNameExpression()) {
                currentMathExpressionSymbol = MathOptimizer.getCurrentAssignment((MathNameExpressionSymbol) mathValueExpressionSymbol, precedingExpressionSymbols);
            }
        }
        if (currentMathExpressionSymbol.isArithmeticExpression()) {

            return ((MathArithmeticExpressionSymbol) currentMathExpressionSymbol).getMathOperator().equals(operator);
        } else if (currentMathExpressionSymbol.isMatrixExpression()) {
            MathMatrixExpressionSymbol mathMatrixExpressionSymbol = (MathMatrixExpressionSymbol) currentMathExpressionSymbol;
            if (mathMatrixExpressionSymbol.isMatrixArithmeticExpression())
                return ((MathMatrixArithmeticExpressionSymbol) mathMatrixExpressionSymbol).getMathOperator().equals(operator);
        }
        return false;
    }

    public static boolean isArithmeticMatrixExpression(MathExpressionSymbol mathExpressionSymbol, String operator, List<MathExpressionSymbol> precedingExpressionSymbols) {
        if (mathExpressionSymbol.isMatrixExpression()) {
            MathMatrixExpressionSymbol mathMatrixExpressionSymbol = (MathMatrixExpressionSymbol) mathExpressionSymbol;
            if (mathMatrixExpressionSymbol.isMatrixArithmeticExpression())
                return ((MathMatrixArithmeticExpressionSymbol) mathMatrixExpressionSymbol).getMathOperator().equals(operator);
        }
        return false;
    }

    public static MathExpressionSymbol getLeftExpressionSymbol(MathExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {
        Log.info(mathExpressionSymbol.getClass().getName() + " " + mathExpressionSymbol.getTextualRepresentation(), "Class of Symbol:");
        if (mathExpressionSymbol.isArithmeticExpression() && ((MathArithmeticExpressionSymbol) mathExpressionSymbol).getLeftExpression() != null) {
            return MathOptimizer.getCurrentAssignment(((MathArithmeticExpressionSymbol) mathExpressionSymbol).getLeftExpression(), precedingExpressions);
        } else if (mathExpressionSymbol.isMatrixExpression()) {
            if (((MathMatrixExpressionSymbol) mathExpressionSymbol).isMatrixArithmeticExpression()) {
                return MathOptimizer.getCurrentAssignment(((MathMatrixArithmeticExpressionSymbol) mathExpressionSymbol).getLeftExpression(), precedingExpressions);
            }
        }

        return null;
    }

    public static MathExpressionSymbol getRightExpressionSymbol(MathExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {
        Log.info(mathExpressionSymbol.getClass().getName() + " " + mathExpressionSymbol.getTextualRepresentation(), "Class of Symbol:");
        if (mathExpressionSymbol.isArithmeticExpression()) {
            return MathOptimizer.getCurrentAssignment(((MathArithmeticExpressionSymbol) mathExpressionSymbol).getRightExpression(), precedingExpressions);
        } else if (mathExpressionSymbol.isMatrixExpression()) {
            if (((MathMatrixExpressionSymbol) mathExpressionSymbol).isMatrixArithmeticExpression()) {
                return MathOptimizer.getCurrentAssignment(((MathMatrixArithmeticExpressionSymbol) mathExpressionSymbol).getRightExpression(), precedingExpressions);
            }
        }

        return null;
    }

    static {
        addOptimizationRule(new MathMultiplicationAddition());
        addOptimizationRule(new MathMatrixMultiplicationOrder());
    }


}
