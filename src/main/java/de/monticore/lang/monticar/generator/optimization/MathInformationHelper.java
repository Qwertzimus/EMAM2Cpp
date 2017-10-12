package de.monticore.lang.monticar.generator.optimization;

import de.monticore.lang.math.math._symboltable.matrix.MathMatrixArithmeticExpressionSymbol;
import de.monticore.lang.math.math._symboltable.matrix.MathMatrixExpressionSymbol;
import de.monticore.lang.math.math._symboltable.matrix.MathMatrixNameExpressionSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * @author Sascha Schneiders
 */
public class MathInformationHelper {

    public static int getMatrixRowInformation(MathMatrixExpressionSymbol mathMatrixExpressionSymbol, MathInformationRegister mathInformationRegister) {
        if (mathMatrixExpressionSymbol.isMatrixArithmeticExpression()) {
            return getMatrixRowInformation((MathMatrixArithmeticExpressionSymbol) mathMatrixExpressionSymbol, mathInformationRegister);
        } else if (mathMatrixExpressionSymbol.isMatrixNameExpression()) {
            return getMatrixRowInformation((MathMatrixNameExpressionSymbol) mathMatrixExpressionSymbol, mathInformationRegister);
        }
        Log.info(mathMatrixExpressionSymbol.getTextualRepresentation(), "No row information");
        return 0;
    }

    public static int getMatrixRowInformation(MathMatrixNameExpressionSymbol mathMatrixExpressionSymbol, MathInformationRegister mathInformationRegister) {
        return mathInformationRegister.getAmountRows(mathMatrixExpressionSymbol.getNameToAccess());
    }


    public static int getMatrixRowInformation(MathMatrixArithmeticExpressionSymbol mathMatrixExpressionSymbol, MathInformationRegister mathInformationRegister) {
        if (mathMatrixExpressionSymbol.getMathOperator().equals("+")) {
            return getMatrixRowInformation((MathMatrixExpressionSymbol) mathMatrixExpressionSymbol.getLeftExpression(), mathInformationRegister);
        } else if (mathMatrixExpressionSymbol.getMathOperator().equals("-")) {
            return getMatrixRowInformation((MathMatrixExpressionSymbol) mathMatrixExpressionSymbol.getLeftExpression(), mathInformationRegister);
        } else if (mathMatrixExpressionSymbol.getMathOperator().equals("*")) {
            return getMatrixRowInformation((MathMatrixExpressionSymbol) mathMatrixExpressionSymbol.getLeftExpression(), mathInformationRegister);
        }
        return 0;
    }
}
