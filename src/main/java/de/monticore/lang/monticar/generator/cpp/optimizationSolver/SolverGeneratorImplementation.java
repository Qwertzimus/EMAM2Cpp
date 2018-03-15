package de.monticore.lang.monticar.generator.cpp.optimizationSolver;

import de.monticore.lang.math.math._symboltable.expression.MathOptimizationExpressionSymbol;

/**
 * Implementation of the bridge pattern. Allows dynamic interchangeable implementations of solver generators
 *
 * @author Christoph Richter
 */
public interface SolverGeneratorImplementation {
    public abstract String generateSolverCode(MathOptimizationExpressionSymbol expression);
}
