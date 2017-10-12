package de.monticore.lang.monticar.generator;


import de.monticore.lang.math.math._symboltable.expression.MathExpressionSymbol;

/**
 * @author Sascha Schneiders.
 */
public abstract class MathCommand {
    protected String mathCommandName;

    public MathCommand() {

    }

    public MathCommand(String mathCommandName) {
        this.mathCommandName = mathCommandName;
    }

    public String getMathCommandName() {
        return mathCommandName;
    }

    public void setMathCommandName(String mathCommandName) {
        this.mathCommandName = mathCommandName;
    }

    public abstract void convert(MathExpressionSymbol mathExpressionSymbol, BluePrint bluePrint);
}
