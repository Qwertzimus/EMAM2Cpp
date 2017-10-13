package de.monticore.lang.monticar.generator.cpp.converter;

import de.monticore.lang.math.math._symboltable.expression.MathConditionalExpressionSymbol;
import de.monticore.lang.math.math._symboltable.expression.MathExpressionSymbol;
import de.monticore.lang.math.math._symboltable.matrix.MathMatrixAccessOperatorSymbol;
import de.monticore.lang.math.math._symboltable.matrix.MathMatrixAccessSymbol;
import de.monticore.lang.monticar.generator.cpp.MathFunctionFixer;

import java.util.List;

/**
 * @author Sascha Schneiders
 */
public class ExecuteMethodGeneratorHelper {
    public static String generateIfConditionCode(MathConditionalExpressionSymbol mathConditionalExpressionSymbol, List<String> includeStrings) {

        String result = "";
        //condition
        if (mathConditionalExpressionSymbol.getCondition().isPresent()) {
            result += "if(" + ExecuteMethodGenerator.generateExecuteCode(mathConditionalExpressionSymbol.getCondition().get(), includeStrings) + ")";
        }
        //body
        result += "{\n";
        for (MathExpressionSymbol mathExpressionSymbol : mathConditionalExpressionSymbol.getBodyExpressions())
            result += ExecuteMethodGenerator.generateExecuteCode(mathExpressionSymbol, includeStrings);
        result += "}\n";

        return result;
    }

    public static String handleForLoopAccessFix(MathMatrixAccessOperatorSymbol mathMatrixAccessOperatorSymbol, int counter, int ignoreCounterAt, int counterSetMinusOne, List<String> includeStrings) {
        String result = "";
        if (MathFunctionFixer.fixForLoopAccess(mathMatrixAccessOperatorSymbol.getMathMatrixNameExpressionSymbol(), ComponentConverter.currentBluePrint)) {
            for (MathMatrixAccessSymbol mathMatrixAccessSymbol : mathMatrixAccessOperatorSymbol.getMathMatrixAccessSymbols()) {
                if (counter == ignoreCounterAt) {
                    ++counter;
                } else {
                    result += ExecuteMethodGenerator.generateExecuteCode(mathMatrixAccessSymbol, includeStrings, true);
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
                        result += ExecuteMethodGenerator.generateExecuteCode(mathMatrixAccessSymbol, includeStrings, true);
                    else
                        result += ExecuteMethodGenerator.generateExecuteCode(mathMatrixAccessSymbol, includeStrings);
                    ++counter;
                    if (counter < mathMatrixAccessOperatorSymbol.getMathMatrixAccessSymbols().size() && counter != ignoreCounterAt) {
                        result += ", ";
                    }
                }
            }
        }
        return result;
    }

}
