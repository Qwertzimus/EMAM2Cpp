package de.monticore.lang.monticar.generator.cpp.optimizationSolver;

import de.monticore.lang.math.math._symboltable.expression.MathOptimizationExpressionSymbol;

import java.util.List;

/**
 * Abstract generator for executable code of optimization solvers.
 * Implements the bridge pattern to exchange the concrete solver generator at runtime.
 *
 * @author Christoph Richter
 */
public abstract class SolverGenerator {

    // fields
    private SolverGeneratorImplementation implementationHook;

    private String generationTargetPath = "./target/generated-sources-cpp/";

    // getter setter

    public String getGenerationTargetPath() {
        return generationTargetPath;
    }

    public void setGenerationTargetPath(String generationTargetPath) {
        this.generationTargetPath = generationTargetPath;
    }

    protected SolverGeneratorImplementation getImplementationHook() {
        return implementationHook;
    }

    protected void setImplementationHook(SolverGeneratorImplementation implementationHook) {
        this.implementationHook = implementationHook;
    }

    // methods

    /**
     * Generates code from a MathOptimizationExpressionSymbol to solve the described optimization problem.
     *
     * @param expression     MathOptimizationExpressionSymbol which describes the minimization problem
     * @param includeStrings Needed dependencies as string
     * @return Executable code instruction as string
     */
    public String generateSolverInstruction(MathOptimizationExpressionSymbol expression, List<String> includeStrings) {
        return implementationHook.generateSolverCode(expression);
    }
}
