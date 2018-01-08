package de.monticore.lang.monticar.generator.optimization;

import de.monticore.lang.math.math._symboltable.MathStatementsSymbol;
import de.monticore.lang.math.math._symboltable.expression.MathExpressionSymbol;

import java.util.List;

/**
 * @author Sascha Schneiders
 */
public interface MathOptimizationRule {

    /*
     * Will explore all sub math operations and check whether the optimization rule can be applied.
     * If the rule can be applied, it will rewrite the expression.
     */
    void optimize(MathExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions);

    /*
     * Will explore all sub math operations and check whether the optimization rule can be applied.
     * If the rule can be applied, it will rewrite the expression.
     */
    void optimize(MathExpressionSymbol mathExpressionSymbol, List<MathExpressionSymbol> precedingExpressions, MathStatementsSymbol mathStatementsSymbol);

}
