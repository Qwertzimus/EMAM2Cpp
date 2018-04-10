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

    protected static String[] getBoundsAndExpressionFromConstraint(NLPProblem nlp, MathCompareExpressionSymbol constraint, MathExpressionSymbol expr) {
        String lowerBound = LOWER_BOUND_INF;
        String upperBound = UPPER_BOUND_INF;

        MathExpressionSymbol value = null;
        String op = constraint.getCompareOperator();
        if (constraint.getLeftExpression() == expr) {
            value = constraint.getRightExpression();
        } else if (constraint.getRightExpression() == expr) {
            value = constraint.getLeftExpression();
        }
        if (op.contains("==")) {
            lowerBound = ExecuteMethodGenerator.generateExecuteCode(value, new ArrayList<String>());
            upperBound = ExecuteMethodGenerator.generateExecuteCode(value, new ArrayList<String>());
        } else if (constraint.getRightExpression() == expr) {
            if (op.contains("<")) {
                lowerBound = ExecuteMethodGenerator.generateExecuteCode(value, new ArrayList<String>());
            } else if (op.contains(">")) {
                upperBound = ExecuteMethodGenerator.generateExecuteCode(value, new ArrayList<String>());
            }
        } else if (constraint.getLeftExpression() == expr) {
            if (op.contains(">")) {
                lowerBound = ExecuteMethodGenerator.generateExecuteCode(value, new ArrayList<String>());
            } else if (op.contains("<")) {
                upperBound = ExecuteMethodGenerator.generateExecuteCode(value, new ArrayList<String>());
            }
        }
        return new String[]{lowerBound, upperBound};
    }

    protected static boolean isConstraintOnOptVar(NLPProblem nlp, MathExpressionSymbol expr) {
        return expr.getTextualRepresentation().contentEquals(nlp.getOptimizationVariableName());
    }

    protected static void mergeBoundsInX(NLPProblem nlp, Vector<String> xL, Vector<String> xU, MathExpressionSymbol expr, String currXL, String currXU) {
        for (int i = 0; i < nlp.getN(); i++) {
            xL.set(i, String.format("std::fmax(%s, %s)", xL.get(i), currXL));
            xU.set(i, String.format("std::fmin(%s, %s)", xU.get(i), currXU));
        }
    }

    private static void setBoundsOnXFromTypeDeclaration(MathValueType type, Vector<String> xL, Vector<String> xU, int n) {
        String lowerBoundX = LOWER_BOUND_INF;
        String upperBoundX = UPPER_BOUND_INF;
        if (type.getType().getRange().isPresent()) {
            lowerBoundX = Double.toString(type.getType().getRange().get().getStartValue().doubleValue());
            upperBoundX = Double.toString(type.getType().getRange().get().getEndValue().doubleValue());
        }
        for (int i = 0; i < n; i++) {
            xL.add(lowerBoundX);
            xU.add(upperBoundX);
        }
    }

    private static MathExpressionSymbol getGFromContraint(NLPProblem nlp,  MathCompareExpressionSymbol constraint) {
        MathExpressionSymbol left = constraint.getLeftExpression();
        MathExpressionSymbol right = constraint.getRightExpression();
        MathExpressionSymbol g = null;
        if (left.getTextualRepresentation().contains(nlp.getOptimizationVariableName()))
            g = left;
        else if (right.getTextualRepresentation().contains(nlp.getOptimizationVariableName()))
            g = right;
        return g;
    }

    protected static void setNLPConstraintsFromSymbol(NLPProblem nlp, MathOptimizationExpressionSymbol symbol) {
        Vector<String> g = new Vector<>();
        Vector<String> gL = new Vector<>();
        Vector<String> gU = new Vector<>();
        Vector<String> xL = new Vector<>();
        Vector<String> xU = new Vector<>();

        setBoundsOnXFromTypeDeclaration(symbol.getOptimizationVariable().getType(), xL, xU, nlp.getN());

        for (MathCompareExpressionSymbol constraint : symbol.getSubjectToExpressions()) {
            // find function
            MathExpressionSymbol expr = getGFromContraint(nlp, constraint);
            String[] bounds = getBoundsAndExpressionFromConstraint(nlp, constraint, expr);
            if (isConstraintOnOptVar(nlp, expr)) {
                mergeBoundsInX(nlp, xL, xU, expr, bounds[0], bounds[1]);
            } else {
                g.add(ExecuteMethodGenerator.generateExecuteCode(expr, new ArrayList<String>()));
                gL.add(bounds[0]);
                gU.add(bounds[1]);
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
