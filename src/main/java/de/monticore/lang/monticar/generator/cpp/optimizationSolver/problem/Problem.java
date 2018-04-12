package de.monticore.lang.monticar.generator.cpp.optimizationSolver.problem;

import de.monticore.lang.math.math._symboltable.MathOptimizationType;

import java.util.Vector;

/**
 * Represents an abstract optimization problem of the general form
 *
 * @author Christoph Richter
 */
public abstract class Problem {

    /**
     * identification number for problem
     */
    private int id = this.hashCode();

    /**
     * defines whether we do minimization or maximization
     */
    private MathOptimizationType optimizationProblemType;

    // fields
    /**
     * dimension of the optimization variable
     */
    private int n;

    /**
     * name of the optimization variable
     */
    private String optimizationVariableName;

    /**
     * data type of the optimization variable
     */
    private String optimizationVariableType;

    /**
     * dimensions of the optimization variable
     */
    private Vector<Integer> optimizationVariableDimensions;

    /**
     * variable which contains the objective value
     */
    private String objectiveValueVariable;

    /**
     * objective function
     */
    private String objectiveFunction;

    // getter setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        if (id <= 0) {
            this.id = this.hashCode();
        }
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public String getOptimizationVariableName() {
        return optimizationVariableName;
    }

    public void setOptimizationVariableName(String optimizationVariableName) {
        this.optimizationVariableName = optimizationVariableName;
    }

    public String getObjectiveValueVariable() {
        return objectiveValueVariable;
    }

    public void setObjectiveValueVariable(String objectiveValueVariable) {
        this.objectiveValueVariable = objectiveValueVariable;
    }

    public String getObjectiveFunction() {
        return objectiveFunction;
    }

    public void setObjectiveFunction(String objectiveFunction) {
        this.objectiveFunction = objectiveFunction;
    }

    public String getOptimizationVariableType() {
        return optimizationVariableType;
    }

    public void setOptimizationVariableType(String optimizationVariableType) {
        this.optimizationVariableType = optimizationVariableType;
    }

    public Vector<Integer> getOptimizationVariableDimensions() {
        return optimizationVariableDimensions;
    }

    public void setOptimizationVariableDimensions(Vector<Integer> optimizationVariableDimensions) {
        this.optimizationVariableDimensions = optimizationVariableDimensions;
    }

    public MathOptimizationType getOptimizationProblemType() {
        return optimizationProblemType;
    }

    public void setOptimizationProblemType(MathOptimizationType optimizationProblemType) {
        this.optimizationProblemType = optimizationProblemType;
    }
}
