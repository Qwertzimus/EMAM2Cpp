package de.monticore.lang.monticar.generator.cpp.optimizationSolver.solver.ipopt;

import de.monticore.lang.monticar.generator.cpp.optimizationSolver.problem.NLPProblem;
import de.monticore.lang.monticar.generator.cpp.viewmodel.ViewModelBase;

import java.util.List;
import java.util.Vector;

/**
 * Contains all necessary information needed to generate a freemarker template of IPOPT C++ code.
 *
 * @author Christoph Richter
 */
public class IpoptViewModel extends ViewModelBase {

    /**
     * Reservated in IPOPT calculations as optimization variable
     */
    private static final String IPOPT_OPTIMIZATION_VAR = "x";

    /**
     * Reservated in IPOPT calculations as objective variable
     */
    private static final String IPOPT_OBJECTIVE_VAR = "obj_value";

    /**
     * Name of the generated ipopt problem class name
     */
    private String nlpClassName;

    /**
     * Name of the generated ipopt execution class name
     */
    private String callIpoptName;

    /**
     * Name of the optimization variable
     */
    private String optimizationVariableName;

    /**
     * Name of the final objective value variable
     */
    private String objectiveVariableName;

    /**
     * Number of variables from x
     */
    private int numberVariables;

    /**
     * Number of constraints
     */
    private int numberConstraints;

    /**
     * Lower bound for x
     */
    private Vector<Double> xL;

    /**
     * Upper bound for x
     */
    private Vector<Double> xU;

    /**
     * Lower bound for g(x)
     */
    private Vector<Double> gL;

    /**
     * Upper bound for g(x)
     */
    private Vector<Double> gU;

    /**
     * starting point of the iteration for x
     */
    private Vector<Double> initX;

    /**
     * objective function, x and obj_value are resavated for optimization variable and objective value
     */
    private String objectiveFunction;

    /**
     * list of constraints
     */
    private Vector<String> constraintFunctions;

    // constructor

    /**
     * Generated a IPOPT view model from a optimization problem
     *
     * @param problem non linear optimization problem
     */
    public IpoptViewModel(NLPProblem problem) {
        this.numberVariables = problem.getN();
        this.numberConstraints = problem.getM();
        this.constraintFunctions = problem.getConstraintFunctions();
        this.gL = problem.getgL();
        this.gU = problem.getgU();
        this.xL = problem.getxL();
        this.xU = problem.getxU();
        this.objectiveFunction = problem.getObjectiveFunction();
        this.optimizationVariableName = problem.getOptimizationVariableName();
        this.objectiveVariableName = problem.getObjectiveValueVariable();

        this.callIpoptName = "CallIpopt" + problem.getId();
        this.nlpClassName = "NLP" + problem.getId();
        this.initX = calculateInitialX();

        resolveIpoptNameConflicts();
    }

    // getter setter methods

    public String getNlpClassName() {
        return nlpClassName;
    }

    public void setNlpClassName(String nlpClassName) {
        this.nlpClassName = nlpClassName;
    }

    public String getCallIpoptName() {
        return callIpoptName;
    }

    public void setCallIpoptName(String callIpoptName) {
        this.callIpoptName = callIpoptName;
    }

    public String getOptimizationVariableName() {
        return optimizationVariableName;
    }

    public void setOptimizationVariableName(String optimizationVariableName) {
        this.optimizationVariableName = optimizationVariableName;
    }

    public String getObjectiveVariableName() {
        return objectiveVariableName;
    }

    public void setObjectiveVariableName(String objectiveVariableName) {
        this.objectiveVariableName = objectiveVariableName;
    }

    public int getNumberVariables() {
        return numberVariables;
    }

    public void setNumberVariables(int numberVariables) {
        this.numberVariables = numberVariables;
    }

    public int getNumberConstraints() {
        return numberConstraints;
    }

    public void setNumberConstraints(int numberConstraints) {
        this.numberConstraints = numberConstraints;
    }

    public List<Double> getxL() {
        return xL;
    }

    public void setxL(Vector<Double> xL) {
        this.xL = xL;
    }

    public List<Double> getxU() {
        return xU;
    }

    public void setxU(Vector<Double> xU) {
        this.xU = xU;
    }

    public List<Double> getgL() {
        return gL;
    }

    public void setgL(Vector<Double> gL) {
        this.gL = gL;
    }

    public List<Double> getgU() {
        return gU;
    }

    public void setgU(Vector<Double> gU) {
        this.gU = gU;
    }

    public List<Double> getInitX() {
        return initX;
    }

    public void setInitX(Vector<Double> initX) {
        this.initX = initX;
    }

    public String getObjectiveFunction() {
        return objectiveFunction;
    }

    public void setObjectiveFunction(String objectiveFunction) {
        this.objectiveFunction = objectiveFunction;
    }

    public Vector<String> getConstraintFunctions() {
        return constraintFunctions;
    }

    public void setConstraintFunctions(Vector<String> constraintFunctions) {
        this.constraintFunctions = constraintFunctions;
    }

    // methods

    /**
     * Calculates a starting point for variable x.
     * Calculation is done by taking the middle between xL and xU
     *
     * @return initial starting point for x
     */
    protected Vector<Double> calculateInitialX() {
        Vector<Double> result = new Vector<>(getNumberVariables());
        for (int i = 0; i < getNumberVariables(); i++) {
            result.add(xL.get(i) + (xU.get(i) - xL.get(i)) / 2);
        }
        return result;
    }

    private void resolveIpoptNameConflicts() {

        // first replace variables which are reservated but not optimization var
        if (!optimizationVariableName.contentEquals(IPOPT_OPTIMIZATION_VAR)) {
            if (containsVariable(IPOPT_OPTIMIZATION_VAR)) {
                String replacementVar = findRelpacementVariable(IPOPT_OPTIMIZATION_VAR);
                replaceVariable(IPOPT_OPTIMIZATION_VAR, replacementVar);
            }
            // then replace optimization var by reservated optimization var
            replaceVariable(optimizationVariableName, IPOPT_OPTIMIZATION_VAR);
        }
        // also replace reservated objective variable
        if (containsVariable(IPOPT_OBJECTIVE_VAR)) {
            String replacementVar = findRelpacementVariable(IPOPT_OBJECTIVE_VAR);
            replaceVariable(IPOPT_OBJECTIVE_VAR, replacementVar);
        }
    }

    private static String replaceVariableInExpr(String expr, String var, String replacementVar) {
        String result = expr;

        String sepVar1 = " " + var + " ";
        String sepVar2 = " " + var + "[";

        String sepRepVar1 = " " + replacementVar + " ";
        String sepRepVar2 = " " + replacementVar + "[";

        result = result.replace(sepVar1, sepRepVar1);
        result = result.replace(sepVar2, sepRepVar2);
        return result;
    }

    private void replaceVariable(String var, String replacementVar) {
        objectiveFunction = replaceVariableInExpr(objectiveFunction, var, replacementVar);
        for (int i = 0; i < constraintFunctions.size(); i++) {
            constraintFunctions.set(i, replaceVariableInExpr(constraintFunctions.get(i), var, replacementVar));
        }
    }

    private boolean containsVariable(String variable) {
        Boolean result = false;
        if (exprContainsVar(objectiveFunction, variable)) {
            result = true;
        }
        for (String s : constraintFunctions) {
            if (exprContainsVar(s, variable)) {
                result = true;
            }
        }
        return result;
    }

    private boolean exprContainsVar(String expr, String variable) {

        String sepVar1 = " " + variable + " ";
        String sepVar2 = " " + variable + "[";

        Boolean matches1 = expr.contains(sepVar1);
        Boolean matches2 = expr.contains(sepVar2);

        return matches1 || matches2;
    }

    private String findRelpacementVariable(String variable) {
        String replacementVar = variable;
        while (containsVariable(replacementVar)) {
            replacementVar += "Tmp";
        }
        return replacementVar;
    }
}