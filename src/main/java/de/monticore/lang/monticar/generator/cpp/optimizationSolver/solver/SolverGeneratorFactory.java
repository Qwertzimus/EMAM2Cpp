package de.monticore.lang.monticar.generator.cpp.optimizationSolver.solver;

import de.monticore.lang.monticar.generator.cpp.optimizationSolver.problem.NLPProblem;
import de.monticore.lang.monticar.generator.cpp.optimizationSolver.problem.Problem;
import de.monticore.lang.monticar.generator.cpp.optimizationSolver.solver.ipopt.IpoptSolverGeneratorImplementation;
import de.se_rwth.commons.logging.Log;

/**
 * Factory to produce solver generators together with their implementation
 *
 * @author Christoph Richter
 */
public class SolverGeneratorFactory {
    private static SolverGeneratorFactory ourInstance = new SolverGeneratorFactory();

    public static SolverGeneratorFactory getInstance() {
        return ourInstance;
    }

    private SolverGeneratorFactory() {
    }

    public SolverGenerator createDefaultSolverForProblem(Problem problem) {
        SolverGenerator result = null;
        if (problem instanceof NLPProblem) {
            NLPSolverGeneratorImplementation impl = new IpoptSolverGeneratorImplementation((NLPProblem)problem);
            result = new NLPSolverGenerator(impl);
        } else {
            Log.error(String.format("No solver found for problem class %s", problem.getClass().toString()));
        }
        return result;
    }
}
