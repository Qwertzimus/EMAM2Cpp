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
    private String objectiveFunction = "";

    /**
     * list of constraints
     */
    private Vector<String> constraintFunctions = new Vector<>();

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

        this.callIpoptName = "NLP" + problem.getId();
        this.nlpClassName += "CallIpopt" + problem.getId();
        this.initX = calculateInitialX();
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
}
