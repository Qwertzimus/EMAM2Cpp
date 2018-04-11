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
     * Default value if no lower bound is set
     */
    public final static String LOWER_BOUND_INF = "-1E+19";
    /**
     * Default value if no upper bound is set
     */
    public final static String UPPER_BOUND_INF = "1E+19";
    /**
     * number of constraints in function g
     */
    private int m;
    /**
     * function g: R^n -> R^m
     */
    private Vector<String> constraintFunctions = new Vector<>();
    /**
     * lower bound of x
     */
    private Vector<String> xL = new Vector<>();

    /**
     * upper bound of x
     */
    private Vector<String> xU = new Vector<>();

    /**
     * lower bound of g
     */
    private Vector<String> gL = new Vector<>();

    /**
     * upper bound of g
     */
    private Vector<String> gU = new Vector<>();

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

    public Vector<String> getxL() {
        return xL;
    }

    public void setxL(Vector<String> xL) {
        this.xL = xL;
    }

    public Vector<String> getxU() {
        return xU;
    }

    public void setxU(Vector<String> xU) {
        this.xU = xU;
    }

    public Vector<String> getgL() {
        return gL;
    }

    public void setgL(Vector<String> gL) {
        this.gL = gL;
    }

    public Vector<String> getgU() {
        return gU;
    }

    public void setgU(Vector<String> gU) {
        this.gU = gU;
    }
}

