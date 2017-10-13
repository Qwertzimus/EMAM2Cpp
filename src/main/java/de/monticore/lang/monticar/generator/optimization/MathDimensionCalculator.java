package de.monticore.lang.monticar.generator.optimization;

import de.monticore.lang.math.math._symboltable.expression.*;
import de.monticore.lang.math.math._symboltable.matrix.MathMatrixArithmeticExpressionSymbol;
import de.monticore.lang.math.math._symboltable.matrix.MathMatrixExpressionSymbol;
import de.monticore.lang.math.math._symboltable.matrix.MathMatrixNameExpressionSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * @author Sascha Schneiders
 */
public class MathDimensionCalculator {

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
            MathExpressionSymbol realLeftExpression =MathOptimizer.getCurrentAssignment(mathExpressionSymbol.getLeftExpression(), precedingExpressions);
            MathExpressionSymbol realRightExpression =MathOptimizer.getCurrentAssignment(mathExpressionSymbol.getRightExpression(), precedingExpressions);

            return getMatrixColumns(realLeftExpression, precedingExpressions);
        } else if (mathExpressionSymbol.getMathOperator().equals("*")) {
            MathExpressionSymbol realLeftExpression =MathOptimizer.getCurrentAssignment(mathExpressionSymbol.getLeftExpression(), precedingExpressions);
            MathExpressionSymbol realRightExpression =MathOptimizer.getCurrentAssignment(mathExpressionSymbol.getRightExpression(), precedingExpressions);

            return getMatrixColumns(realLeftExpression, precedingExpressions);
        }
        Log.info(mathExpressionSymbol.getClass().getName(), "Not handled:");
        return 0;
    }


    public static int getMatrixColumns(MathArithmeticExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {

        if (mathExpressionSymbol.getMathOperator().equals("+")) {
            MathExpressionSymbol realLeftExpression =MathOptimizer.getCurrentAssignment(mathExpressionSymbol.getLeftExpression(), precedingExpressions);
            MathExpressionSymbol realRightExpression =MathOptimizer.getCurrentAssignment(mathExpressionSymbol.getRightExpression(), precedingExpressions);
            if (getMatrixRows(realRightExpression, precedingExpressions) == 1) {
                return getMatrixColumns(realLeftExpression, precedingExpressions);
            }
            return getMatrixColumns(realLeftExpression, precedingExpressions);
        } else if (mathExpressionSymbol.getMathOperator().equals("*")) {
            MathExpressionSymbol realLeftExpression =MathOptimizer.getCurrentAssignment(mathExpressionSymbol.getLeftExpression(), precedingExpressions);
            MathExpressionSymbol realRightExpression =MathOptimizer.getCurrentAssignment(mathExpressionSymbol.getRightExpression(), precedingExpressions);
            if (getMatrixRows(realRightExpression, precedingExpressions) == 1) {
                return getMatrixColumns(realLeftExpression, precedingExpressions);
            }
            return getMatrixColumns(realLeftExpression, precedingExpressions);
        }
        Log.info(mathExpressionSymbol.getClass().getName(), "Not handled:");
        return 0;
    }


    public static int getMatrixColumns(MathMatrixNameExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {
        MathExpressionSymbol currentAssignment =MathOptimizer.getCurrentAssignment(mathExpressionSymbol, precedingExpressions);
        if (mathExpressionSymbol.equals(currentAssignment)) {
            return MathOptimizer.currentBluePrint.getMathInformationRegister().getAmountColumns(mathExpressionSymbol.getNameToAccess(), mathExpressionSymbol.getMathMatrixAccessOperatorSymbol());
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
        MathExpressionSymbol currentExpression =MathOptimizer.getCurrentAssignment(mathExpressionSymbol, precedingExpressions);
        if (currentExpression.equals(mathExpressionSymbol)) {


            return MathOptimizer.currentBluePrint.getMathInformationRegister().getAmountColumns(mathExpressionSymbol.getNameToResolveValue());
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
            MathExpressionSymbol realLeftExpression = MathOptimizer.getCurrentAssignment(mathExpressionSymbol.getLeftExpression(), precedingExpressions);
            MathExpressionSymbol realRightExpression = MathOptimizer.getCurrentAssignment(mathExpressionSymbol.getRightExpression(), precedingExpressions);

            return getMatrixRows(realLeftExpression, precedingExpressions);
        } else if (mathExpressionSymbol.getMathOperator().equals("*")) {
            MathExpressionSymbol realLeftExpression =MathOptimizer.getCurrentAssignment(mathExpressionSymbol.getLeftExpression(), precedingExpressions);
            MathExpressionSymbol realRightExpression =MathOptimizer.getCurrentAssignment(mathExpressionSymbol.getRightExpression(), precedingExpressions);

            return getMatrixRows(realLeftExpression, precedingExpressions);
        }
        Log.info(mathExpressionSymbol.getClass().getName(), "Not handled:");
        return 0;
    }

    public static int getMatrixRows(MathArithmeticExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {

        if (mathExpressionSymbol.getMathOperator().equals("+")) {
            MathExpressionSymbol realLeftExpression =MathOptimizer.getCurrentAssignment(mathExpressionSymbol.getLeftExpression(), precedingExpressions);
            MathExpressionSymbol realRightExpression =MathOptimizer.getCurrentAssignment(mathExpressionSymbol.getRightExpression(), precedingExpressions);
            if (getMatrixColumns(realRightExpression, precedingExpressions) == 1) {
                return getMatrixRows(realLeftExpression, precedingExpressions);
            }
            return getMatrixRows(realRightExpression, precedingExpressions);
        } else if (mathExpressionSymbol.getMathOperator().equals("*")) {
            MathExpressionSymbol realLeftExpression =MathOptimizer.getCurrentAssignment(mathExpressionSymbol.getLeftExpression(), precedingExpressions);
            MathExpressionSymbol realRightExpression =MathOptimizer.getCurrentAssignment(mathExpressionSymbol.getRightExpression(), precedingExpressions);
            if (getMatrixColumns(realRightExpression, precedingExpressions) == 1) {
                return getMatrixRows(realLeftExpression, precedingExpressions);
            }
            return getMatrixRows(realRightExpression, precedingExpressions);
        }
        Log.info(mathExpressionSymbol.getClass().getName(), "Not handled:");
        return 0;
    }

    public static int getMatrixRows(MathMatrixNameExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions) {
        MathExpressionSymbol currentAssignment =MathOptimizer.getCurrentAssignment(mathExpressionSymbol, precedingExpressions);
        if (mathExpressionSymbol.equals(currentAssignment)) {
            return MathOptimizer.currentBluePrint.getMathInformationRegister().getAmountRows(mathExpressionSymbol.getNameToAccess(), mathExpressionSymbol.getMathMatrixAccessOperatorSymbol());
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
        MathExpressionSymbol currentExpression =MathOptimizer.getCurrentAssignment(mathExpressionSymbol, precedingExpressions);
        if (currentExpression.equals(mathExpressionSymbol)) {

            return MathOptimizer.currentBluePrint.getMathInformationRegister().getAmountRows(mathExpressionSymbol.getNameToResolveValue());
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
}
