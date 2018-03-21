package de.monticore.lang.monticar.generator.cpp.optimizationSolver.problem;

import java.util.Vector;

/**
 * Represents a continious non linear optimization problem of the following form:
 * min     f(x)
 * x in R^n
 * s.t.         g_L <= g(x) <= g_U
 * .            x_L <=  x   <= x_U
 * where f and g are nonlinear functions, x in R^n, f(x) in R, g(x) in R^m with
 * n dimension of x
 * m number of constraints
 * A. Wächter and L. T. Biegler, ​On the Implementation of a Primal-Dual Interior Point Filter Line Search Algorithm for Large-Scale Nonlinear Programming, Mathematical Programming 106(1), pp. 25-57, 2006
 *
 * @author Christoph Richter
 */
public class NLPProblem extends GeneralNLPProblem {

    /**
     * number of constraints in function g
     */
    private int m;

    /**
     * function g: R^n -> R^m
     */
    private Vector<String> constraintFunctions = new Vector<>();

    /**
     * Default value if no lower bound is set
     */
    public final static double LOWER_BOUND_INF = -1E+19;

    /**
     * Default value if no upper bound is set
     */
    public final static double UPPER_BOUND_INF = 1E+19;

    /**
     * lower bound of x
     */
    private Vector<Double> xL = new Vector<>();

    /**
     * upper bound of x
     */
    private Vector<Double> xU = new Vector<>();

    /**
     * lower bound of g
     */
    private Vector<Double> gL = new Vector<>();

    /**
     * upper bound of g
     */
    private Vector<Double> gU = new Vector<>();

    // getter setter

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public Vector<String> getConstraintFunctions() {
        return constraintFunctions;
    }

    public void setConstraintFunctions(Vector<String> constraintFunctions) {
        this.constraintFunctions = constraintFunctions;
    }

    public Vector<Double> getxL() {
        return xL;
    }

    public void setxL(Vector<Double> xL) {
        this.xL = xL;
    }

    public Vector<Double> getxU() {
        return xU;
    }

    public void setxU(Vector<Double> xU) {
        this.xU = xU;
    }

    public Vector<Double> getgL() {
        return gL;
    }

    public void setgL(Vector<Double> gL) {
        this.gL = gL;
    }

    public Vector<Double> getgU() {
        return gU;
    }

    public void setgU(Vector<Double> gU) {
        this.gU = gU;
    }
}

