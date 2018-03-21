package de.monticore.lang.monticar.generator.cpp.optimizationSolver.solver;

import de.monticore.lang.monticar.generator.cpp.optimizationSolver.problem.Problem;

import java.util.ArrayList;
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

    private List<String> necessaryIncludes = new ArrayList<>();

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

    public List<String> getNecessaryIncludes() {
        return necessaryIncludes;
    }

    // methods

    /**
     * Generates code from a MathOptimizationExpressionSymbol to solve the described optimization problem.
     *
     * @param optimizationProblem optimization problem which should be solved
     * @return Executable code instruction as string
     */
    public String generateSolverInstruction(Problem optimizationProblem) {
        return implementationHook.generateSolverCode(optimizationProblem);
    }
}
