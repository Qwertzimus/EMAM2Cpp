package de.monticore.lang.monticar.generator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sascha Schneiders
 */
public abstract class MathCommandRegister {
    public List<MathCommand> mathCommands = new ArrayList<>();

    public MathCommandRegister(){
        init();
    }

    public void registerMathCommand(MathCommand mathCommand) {
        mathCommands.add(mathCommand);
    }

    public MathCommand getMathCommand(String functionName) {
        for (MathCommand mathCommand : mathCommands) {
            if (mathCommand.getMathCommandName().equals(functionName))
                return mathCommand;
        }
        return null;
    }
    protected abstract void init();

}
