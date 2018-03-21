package de.monticore.lang.monticar.generator.cpp.optimizationSolver.solver;

import de.monticore.lang.monticar.generator.cpp.optimizationSolver.problem.NLPProblem;

/**
 * Interface for Non linear optimization problem solvers.
 *
 * @author Christoph Richter
 */
public abstract class NLPSolverGeneratorImplementation implements SolverGeneratorImplementation {

    private NLPProblem nlpProblem;

    // constructor
    public NLPSolverGeneratorImplementation() {
    }

    public NLPSolverGeneratorImplementation(NLPProblem nlpProblem) {
        this.nlpProblem = nlpProblem;
    }

    // methods
    public NLPProblem getNlpProblem() {
        return nlpProblem;
    }

    public void setNlpProblem(NLPProblem nlpProblem) {
        this.nlpProblem = nlpProblem;
    }
}
