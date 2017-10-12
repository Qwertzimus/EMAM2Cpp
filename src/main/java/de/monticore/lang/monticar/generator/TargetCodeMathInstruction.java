package de.monticore.lang.monticar.generator;

import de.monticore.lang.math.math._symboltable.expression.MathExpressionSymbol;

/**
 * @author Sascha Schneiders
 */
public class TargetCodeMathInstruction implements Instruction {
    protected String targetCode;
    protected MathExpressionSymbol mathExpressionSymbol;

    public TargetCodeMathInstruction(String targetCode, MathExpressionSymbol mathExpressionSymbol) {
        this.targetCode = targetCode;
        this.mathExpressionSymbol = mathExpressionSymbol;
    }

    public MathExpressionSymbol getMathExpressionSymbol() {
        return mathExpressionSymbol;
    }

    public void setMathExpressionSymbol(MathExpressionSymbol mathExpressionSymbol) {
        this.mathExpressionSymbol = mathExpressionSymbol;
    }

    public String getTargetCode() {
        return targetCode;
    }

    public void setTargetCode(String targetCode) {
        this.targetCode = targetCode;
    }

    @Override
    public String getTargetLanguageInstruction() {
        return targetCode;
    }

    @Override
    public boolean isConnectInstruction() {
        return false;
    }

    @Override
    public boolean isTargetCodeInstruction() {
        return true;
    }
}
