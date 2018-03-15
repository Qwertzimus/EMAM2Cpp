package de.monticore.lang.monticar.generator.cpp.optimizationSolver;

/**
 * Generates solver code which solves non linear optimization problems
 */
public class NLPSolverGenerator extends SolverGenerator {

    // constructor
    public NLPSolverGenerator(NLPSolverGeneratorImplementation impl) {
        this.setImplementationHook(impl);
    }
}
