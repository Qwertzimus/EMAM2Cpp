package de.monticore.lang.monticar.generator.cpp.optimizationSolver.problem;

import de.monticore.lang.math.math._symboltable.expression.*;
import de.monticore.lang.monticar.generator.cpp.converter.ExecuteMethodGenerator;
import de.monticore.lang.monticar.generator.cpp.converter.TypeConverter;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.math3.geometry.euclidean.oned.Interval;

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

    protected static Interval getBoundsAndExpressionFromConstraint(NLPProblem nlp, MathCompareExpressionSymbol constraint, MathExpressionSymbol expr) {
        double lowerBound = LOWER_BOUND_INF;
        double upperBound = UPPER_BOUND_INF;

        MathNumberExpressionSymbol value = null;
        expr = null;
        String op = constraint.getCompareOperator();
        if (getNumber(constraint.getLeftExpression()) != null) {
            value = getNumber(constraint.getLeftExpression());
        } else if (getNumber(constraint.getRightExpression()) != null) {
            value = getNumber(constraint.getRightExpression());
        }
        if (op.contains("==")) {
            lowerBound = value.getValue().getRealNumber().doubleValue();
            upperBound = value.getValue().getRealNumber().doubleValue();
        } else if (getNumber(constraint.getLeftExpression()) != null) {
            if (op.contains("<")) {
                lowerBound = value.getValue().getRealNumber().doubleValue();
            } else if (op.contains(">")) {
                upperBound = value.getValue().getRealNumber().doubleValue();
            }
        } else if (getNumber(constraint.getRightExpression()) != null) {
            if (op.contains(">")) {
                lowerBound = value.getValue().getRealNumber().doubleValue();
            } else if (op.contains("<")) {
                upperBound = value.getValue().getRealNumber().doubleValue();
            }
        }

        return new Interval(lowerBound, upperBound);
    }

    protected static boolean isConstraintOnOptVar(NLPProblem nlp, MathExpressionSymbol expr) {
        return expr.getTextualRepresentation().contentEquals(nlp.getOptimizationVariableName());
    }

    protected static void mergeBoundsInX(NLPProblem nlp, Vector<Double> xL, Vector<Double> xU, MathExpressionSymbol expr, Interval mergeBounds) {
        for (int i = 0; i < nlp.getN(); i++) {
            if (mergeBounds.getInf() > xL.get(i))
                xL.set(i, mergeBounds.getInf());
            if (mergeBounds.getSup() < xU.get(i))
                xU.set(i, mergeBounds.getSup());
        }
    }

    private static void setBoundsOnXFromTypeDeclaration(MathValueType type, Vector<Double> xL, Vector<Double> xU, int n) {
        double lowerBoundX = LOWER_BOUND_INF;
        double upperBoundX = UPPER_BOUND_INF;
        if (type.getType().getRange().isPresent()) {
            lowerBoundX = type.getType().getRange().get().getStartValue().doubleValue();
            upperBoundX = type.getType().getRange().get().getEndValue().doubleValue();
        }
        for (int i = 0; i < n; i++) {
            xL.add(lowerBoundX);
            xU.add(upperBoundX);
        }
    }

    protected static void setNLPConstraintsFromSymbol(NLPProblem nlp, MathOptimizationExpressionSymbol symbol) {
        Vector<String> g = new Vector<>();
        Vector<Double> gL = new Vector<>();
        Vector<Double> gU = new Vector<>();
        Vector<Double> xL = new Vector<>();
        Vector<Double> xU = new Vector<>();

        setBoundsOnXFromTypeDeclaration(symbol.getOptimizationVariable().getType(), xL, xU, nlp.getN());

        for (MathCompareExpressionSymbol constraint : symbol.getSubjectToExpressions()) {
            // find function
            MathExpressionSymbol expr = null;
            if (getNumber(constraint.getLeftExpression()) != null) {
                expr = constraint.getRightExpression();
            } else if (getNumber(constraint.getRightExpression()) != null) {
                expr = constraint.getLeftExpression();
            }
            Interval bounds = getBoundsAndExpressionFromConstraint(nlp, constraint, expr);
            if (isConstraintOnOptVar(nlp, expr)) {
                mergeBoundsInX(nlp, xL, xU, expr, bounds);
            } else {
                g.add(ExecuteMethodGenerator.generateExecuteCode(expr, new ArrayList<String>()));
                gL.add(bounds.getInf());
                gU.add(bounds.getSup());
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
