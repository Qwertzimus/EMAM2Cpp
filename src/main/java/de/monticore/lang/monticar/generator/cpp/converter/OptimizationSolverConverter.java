package de.monticore.lang.monticar.generator.cpp.converter;

import de.monticore.lang.math.math._symboltable.expression.MathOptimizationExpressionSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * Manages the conversion from a MathOptimizationSymbol to executable C++ code.
 *
 * @author Christoph Richter
 * @since 09th March 2018
 */
public class OptimizationSolverConverter {

    /**
     * Generates code solving command including auxiliary code files.
     *
     * @param symbol         MathExpressionSymbol from which code should be generated
     * @param includeStrings Additinal include strings which are nessesary to execute the code
     * @return Code line which will execute the optimization solver on the optimization problem
     */
    public static String getOptimizationExpressionCode(MathOptimizationExpressionSymbol symbol, List<String> includeStrings) {
        Log.warn("Optimization Solver expression not implemented yet");
        String result = "// optimization not implemented yet";
        return result;
    }

}
