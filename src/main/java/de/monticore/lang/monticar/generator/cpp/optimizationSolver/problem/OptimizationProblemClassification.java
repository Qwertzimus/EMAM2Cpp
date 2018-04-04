package de.monticore.lang.monticar.generator.cpp.optimizationSolver.problem;

import de.monticore.lang.math.math._symboltable.expression.*;
import de.monticore.lang.monticar.generator.cpp.converter.ExecuteMethodGenerator;
import de.monticore.lang.monticar.generator.cpp.converter.TypeConverter;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static de.monticore.lang.monticar.generator.cpp.optimizationSolver.problem.NLPProblem.LOWER_BOUND_INF;
import static de.monticore.lang.monticar.generator.cpp.optimizationSolver.problem.NLPProblem.UPPER_BOUND_INF;

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
     *
     * @param symbol MathOptimizationExpressionSymbol which should be classified
     * @return Optimization problem type
     */
    public static Problem classifySymbol(MathOptimizationExpressionSymbol symbol) {

        Problem result = null;
        // TODO not only check for NLP
        if (checkIfNLP(symbol)) {
            result = getNLPFromSymbol(symbol);
        } else {
            Log.error(String.format("Can not classify %s: %s", symbol.getClass().toString(), symbol.getFullName()));
        }
        return result;
    }

    protected static boolean checkIfNLP(MathOptimizationExpressionSymbol symbol) {
        // TODO really analyse problem type instead of just saying NLP
        return true;
    }

    protected static NLPProblem getNLPFromSymbol(MathOptimizationExpressionSymbol symbol) {
        NLPProblem nlp = new NLPProblem();
        nlp.setId(symbol.getExpressionID());
        // assign all properties
        setNLPOptimizationVariableFromSymbol(nlp, symbol);
        setNLPObjectiveFunctionFromSymbol(nlp, symbol);
        setNLPConstraintsFromSymbol(nlp, symbol);
        return nlp;
    }

    protected static void setNLPOptimizationVariableFromSymbol(NLPProblem nlp, MathOptimizationExpressionSymbol symbol) {
        nlp.setN(getOptimizationVarDimension(symbol));
        nlp.setOptimizationVariableName(getOptimizationVarName(symbol));
        nlp.setOptimizationVariableType(getOptimizationVarType(symbol));
    }

    protected static void setNLPObjectiveFunctionFromSymbol(NLPProblem nlp, MathOptimizationExpressionSymbol symbol) {
        nlp.setObjectiveValueVariable(getObjectiveValueVarName(symbol));
        nlp.setObjectiveFunction(getObjectiveFunctionAsCode(symbol));
    }

    protected static void setNLPConstraintsFromSymbol(NLPProblem nlp, MathOptimizationExpressionSymbol symbol) {
        Vector<String> g = new Vector<>();
        Vector<Double> gL = new Vector<>();
        Vector<Double> gU = new Vector<>();
        Vector<Double> xL = new Vector<>();
        Vector<Double> xU = new Vector<>();
        for (MathCompareExpressionSymbol constraint : symbol.getSubjectToExpressions()) {
            MathNumberExpressionSymbol value = null;
            MathExpressionSymbol expr = null;
            if (getNumber(constraint.getLeftExpression()) != null) {
                value = getNumber(constraint.getLeftExpression());
                expr = constraint.getRightExpression();
            } else if (getNumber(constraint.getRightExpression()) != null) {
                value = getNumber(constraint.getRightExpression());
                expr = constraint.getLeftExpression();
            }
            if ((value != null) && (expr != null)) {
                // TODO check if only bound for x
                xL.add(LOWER_BOUND_INF);
                xU.add(UPPER_BOUND_INF);
                g.add(ExecuteMethodGenerator.generateExecuteCode(expr, new ArrayList<String>()));
                String op = constraint.getCompareOperator();
                if (op.contains("==")) {
                    gL.add(value.getValue().getRealNumber().doubleValue());
                    gU.add(value.getValue().getRealNumber().doubleValue());
                } else if (getNumber(constraint.getLeftExpression()) != null) {
                    if (op.contains("<")) {
                        gL.add(value.getValue().getRealNumber().doubleValue());
                        gU.add(UPPER_BOUND_INF);
                    } else if (op.contains(">")) {
                        gU.add(value.getValue().getRealNumber().doubleValue());
                        gL.add(LOWER_BOUND_INF);
                    } else {
                        Log.error(String.format("Unexpected compare operator \"%s\" in optimization expression", op));
                    }
                } else if (getNumber(constraint.getRightExpression()) != null) {
                    if (op.contains("<")) {
                        gU.add(value.getValue().getRealNumber().doubleValue());
                        gL.add(UPPER_BOUND_INF);
                    } else if (op.contains(">")) {
                        gL.add(value.getValue().getRealNumber().doubleValue());
                        gU.add(UPPER_BOUND_INF);
                    } else {
                        Log.error(String.format("Unexpected compare operator \"%s\" in optimization expression", op));
                    }
                }
            } else {
                Log.error("Cannot parse constraint in problem classification"); // TODO better error message
            }
        }
        nlp.setM(g.size());
        nlp.setConstraintFunctions(g);
        nlp.setgL(gL);
        nlp.setgU(gU);
        nlp.setxL(xL);
        nlp.setxU(xU);
    }

    private static MathNumberExpressionSymbol getNumber(MathExpressionSymbol symbol) {
        MathNumberExpressionSymbol numberExpr = null;
        if (symbol.isValueExpression()) {
            if (((MathValueExpressionSymbol) symbol).isNumberExpression()) {
                numberExpr = ((MathNumberExpressionSymbol) symbol);
            }
        }
        return numberExpr;
    }

    protected static int getOptimizationVarDimension(MathOptimizationExpressionSymbol symbol) {
        int n = 1;
        List<MathExpressionSymbol> dims = symbol.getOptimizationVariable().getType().getDimensions();
        for (MathExpressionSymbol d : dims) {
            if (getNumber(d) != null) {
                n *= getNumber(d).getValue().getRealNumber().intValue();
            }
        }
        return n;
    }

    protected static String getOptimizationVarName(MathOptimizationExpressionSymbol symbol) {
        return symbol.getOptimizationVariable().getName();
    }

    protected static String getOptimizationVarType(MathOptimizationExpressionSymbol symbol) {
        return TypeConverter.getVariableTypeNameForMathLanguageTypeName(symbol.getOptimizationVariable().getType());
    }

    protected static String getObjectiveValueVarName(MathOptimizationExpressionSymbol symbol) {
        String objValueVar = "objectiveValue";
        if ((symbol.getObjectiveExpression().isAssignmentDeclarationExpression()) && (!symbol.getObjectiveExpression().getName().isEmpty())) {
            objValueVar = symbol.getObjectiveExpression().getName();
        }
        return objValueVar;
    }

    protected static String getObjectiveFunctionAsCode(MathOptimizationExpressionSymbol symbol) {
        return ExecuteMethodGenerator.generateExecuteCode(symbol.getObjectiveExpression().getAssignedMathExpressionSymbol(), new ArrayList<>());
    }
}
