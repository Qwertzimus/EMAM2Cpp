package de.monticore.lang.monticar.generator.cpp.converter;

import de.monticore.lang.math.math._symboltable.expression.MathOptimizationExpressionSymbol;
import de.monticore.lang.math.math._symboltable.expression.MathValueSymbol;
import de.monticore.lang.monticar.generator.FileContent;
import de.monticore.lang.monticar.generator.cpp.BluePrintCPP;
import de.monticore.lang.monticar.generator.cpp.GeneratorCPP;
import de.monticore.lang.monticar.generator.cpp.optimizationSolver.problem.OptimizationProblemClassification;
import de.monticore.lang.monticar.generator.cpp.optimizationSolver.problem.Problem;
import de.monticore.lang.monticar.generator.cpp.optimizationSolver.solver.SolverGenerator;
import de.monticore.lang.monticar.generator.cpp.optimizationSolver.solver.SolverGeneratorFactory;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the conversion from a MathOptimizationSymbol to executable C++ code.
 *
 * @author Christoph Richter
 * @since 09th March 2018
 */
public class OptimizationSolverConverter {

    // singleton implementation
    private static OptimizationSolverConverter ourInstance = new OptimizationSolverConverter();

    public static OptimizationSolverConverter getInstance() {
        return ourInstance;
    }

    private OptimizationSolverConverter() {
    }

    // methods

    /**
     * Generates code solving command including auxiliary code files.
     *
     * @param symbol         MathExpressionSymbol from which code should be generated
     * @param includeStrings Additinal include strings which are nessesary to execute the code
     * @return Code line which will execute the optimization solver on the optimization problem
     */
    public static String getOptimizationExpressionCode(MathOptimizationExpressionSymbol symbol, List<String> includeStrings, BluePrintCPP bluePrint) {

        // declare needed variables
        String result = "";
        result += ExecuteMethodGenerator.generateExecuteCode(symbol.getOptimizationVariable(), new ArrayList<>());
        if (symbol.getObjectiveExpression() instanceof MathValueSymbol) {
            MathValueSymbol expr = (MathValueSymbol) symbol.getObjectiveExpression();
            MathValueSymbol decl = new MathValueSymbol(expr.getName());
            decl.setType(expr.getType());
            result += ExecuteMethodGenerator.generateExecuteCode(decl, new ArrayList<>());
        }

        // first step: decide for correct solver for problem class
        OptimizationProblemClassification problemClassification = new OptimizationProblemClassification(symbol);
        Problem problemType = problemClassification.getProblemType();

        // second step: decide for implementation
        SolverGenerator solverGenerator = SolverGeneratorFactory.getInstance().createDefaultSolverForProblem(problemType);

        // third step: generate code from solver generator
        ArrayList<FileContent> auxiliaryFiles = new ArrayList<FileContent>();
        result += solverGenerator.generateSolverInstruction(problemType, auxiliaryFiles);

        // also generate auxiliaryFiles
        if (bluePrint.getGenerator() instanceof GeneratorCPP) {
            try {
                ((GeneratorCPP) bluePrint.getGenerator()).saveFilesToDisk(auxiliaryFiles);
            } catch (IOException e) {
                Log.error(e.toString());
            }
        }

        // do not forget to add include strings
        bluePrint.additionalIncludeStrings.addAll(solverGenerator.getNecessaryIncludes());

        return result;
    }

}
