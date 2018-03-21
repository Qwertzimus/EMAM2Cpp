package de.monticore.lang.monticar.generator.cpp.optimizationSolver.problem;

import de.monticore.lang.math.math._symboltable.expression.MathOptimizationExpressionSymbol;

/**
 * Analyses a MathOptimizationExpressionSymbol for its problem class.
 *
 * @author Christoph Richter
 */
public class OptimizationProblemClassification {

    // fields
    private MathOptimizationExpressionSymbol symbol;

    private Problem problemType;

    // constructor
    public OptimizationProblemClassification(MathOptimizationExpressionSymbol symbol) {
        this.symbol = symbol;
        this.problemType = classifySymbol(symbol);
    }

    // methods
    public MathOptimizationExpressionSymbol getSymbol() {
        return symbol;
    }

    public Problem getProblemType() {
        return problemType;
    }

    /**
     * Classifies MathOptimizationExpression to a optimization problem class
     * @param symbol MathOptimizationExpressionSymbol which should be classified
     * @return Optimization problem type
     */
    public static Problem classifySymbol(MathOptimizationExpressionSymbol symbol) {
        // TODO really analyse problem type instead of just saying NLP
        return new NLPProblem();
    }
}
