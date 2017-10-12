package de.monticore.lang.monticar.generator.cpp.symbols;

import de.monticore.lang.math.math._symboltable.expression.MathExpressionSymbol;

/**
 * @author Sascha Schneiders
 */
public class MathStringExpression extends MathExpressionSymbol {
    public static int ID = 10001;

    protected String text;

    public MathStringExpression() {
        setID(ID);
    }

    public MathStringExpression(String text) {
        this.text = text;
        setID(ID);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getTextualRepresentation() {
        return text;
    }
}
