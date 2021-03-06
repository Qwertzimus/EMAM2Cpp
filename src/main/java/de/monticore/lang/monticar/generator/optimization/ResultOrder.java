package de.monticore.lang.monticar.generator.optimization;

/**
 * @author Sascha Schneiders
 */
public class ResultOrder {
    public int matrixIndexLeft;
    public int matrixIndexRight;
    public boolean isResultLeft;
    public boolean isResultRight;

    public ResultOrder(int matrixIndexLeft, boolean isResultLeft, int matrixIndexRight, boolean isResultRight) {
        this.matrixIndexLeft = matrixIndexLeft;
        this.matrixIndexRight = matrixIndexRight;
        this.isResultLeft = isResultLeft;
        this.isResultRight = isResultRight;
    }
}
