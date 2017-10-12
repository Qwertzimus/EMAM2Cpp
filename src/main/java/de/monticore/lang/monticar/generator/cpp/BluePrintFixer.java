package de.monticore.lang.monticar.generator.cpp;

import de.monticore.lang.monticar.generator.BluePrint;
import de.monticore.lang.monticar.generator.Variable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sascha Schneiders
 */
public class BluePrintFixer {
    public static void fixBluePrintVariableArrays(BluePrint bluePrint) {
        List<Variable> newVars = new ArrayList<>();
        for (Variable variable : bluePrint.getVariables()) {
            String currentArrayName = variable.getNameWithoutArrayNamePart();
            boolean add = true;
            for (Variable newVar : newVars) {
                if (currentArrayName.equals(newVar.getNameWithoutArrayNamePart())) {
                    newVar.setName(newVar.getNameWithoutArrayNamePart());
                    add = false;
                    newVar.setArraySize(newVar.getArraySize() + 1);
                }
            }
            if (add)
                newVars.add(variable);
        }
        bluePrint.setVariables(newVars);
    }
}
