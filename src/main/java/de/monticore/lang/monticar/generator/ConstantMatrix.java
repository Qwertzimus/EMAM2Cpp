package de.monticore.lang.monticar.generator;

import de.monticore.lang.math.math._symboltable.expression.MathExpressionSymbol;

/**
 * @author Sascha Schneiders
 */
public class ConstantMatrix {
    public static int LASTID = 0;
    protected String name;
    protected MathExpressionSymbol mathExpressionSymbol;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MathExpressionSymbol getMathExpressionSymbol() {
        return mathExpressionSymbol;
    }

    public void setMathExpressionSymbol(MathExpressionSymbol mathExpressionSymbol) {
        this.mathExpressionSymbol = mathExpressionSymbol;
    }
}
