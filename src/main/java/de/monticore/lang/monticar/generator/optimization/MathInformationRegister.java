package de.monticore.lang.monticar.generator.optimization;

import de.monticore.lang.math.math._symboltable.expression.MathExpressionSymbol;
import de.monticore.lang.math.math._symboltable.expression.MathValueSymbol;
import de.monticore.lang.math.math._symboltable.matrix.MathMatrixAccessOperatorSymbol;
import de.monticore.lang.math.math._symboltable.matrix.MathMatrixAccessSymbol;
import de.monticore.lang.monticar.generator.BluePrint;
import de.monticore.lang.monticar.generator.Variable;
import de.monticore.lang.monticar.generator.cpp.BluePrintCPP;
import de.monticore.lang.monticar.generator.cpp.converter.PortConverter;
import de.monticore.lang.monticar.generator.cpp.converter.TypeConverter;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * This class stores information of already encountered math information
 *
 * @author Sascha Schneiders
 */
public class MathInformationRegister {
    protected List<Variable> variables = new ArrayList<>();//contains in implementation Math section declared Variables
    protected List<MathValueSymbol> mathValueSymbols = new ArrayList<>();
    protected BluePrint bluePrint;

    public MathInformationRegister(BluePrint bluePrint) {
        this.bluePrint = bluePrint;
    }

    public MathValueSymbol getMathValueSymbol(String name) {
        for (MathValueSymbol mathValueSymbol : mathValueSymbols) {
            if (mathValueSymbol.getName().equals(name)) {
                return mathValueSymbol;
            }
        }
        return null;
    }

    public int getAmountRows(String name) {
        MathValueSymbol mathValueSymbol = getMathValueSymbol(name);

        if (mathValueSymbol != null) {
            String numberString = mathValueSymbol.getType().getDimensions().get(1).getTextualRepresentation();
            try {
                return Integer.valueOf(numberString);
            } catch (Exception ex) {
                // TODO resolve name return bluePrint.
            }
        } else {
            Variable var = getVariable(name);

            if (var != null) {
                try {
                    return Integer.valueOf(var.getDimensionalInformation().get(1));
                } catch (Exception ex) {
                    // TODO resolve name return bluePrint.
                    Log.info(name,"Name:");
                    //ex.printStackTrace();
                    //Log.error(var.getDimensionalInformation().get(1));
                    return 1;
                }
            }
        }
        Log.info(name, "Not found:");
        return 0;
    }

    public int getAmountRows(String name, MathMatrixAccessOperatorSymbol mathMatrixAccessOperatorSymbol) {
        MathValueSymbol mathValueSymbol = getMathValueSymbol(name);
        boolean firstDoubleDot = false, secondDoubleDot = false;
        if (mathMatrixAccessOperatorSymbol.getMathMatrixAccessSymbols().size() == 2) {
            firstDoubleDot = mathMatrixAccessOperatorSymbol.getMathMatrixAccessSymbols().get(0).isDoubleDot();
            secondDoubleDot = mathMatrixAccessOperatorSymbol.getMathMatrixAccessSymbols().get(1).isDoubleDot();
        }

        if (firstDoubleDot)
            return 1;

        if (mathValueSymbol != null) {
            String numberString = mathValueSymbol.getType().getDimensions().get(1).getTextualRepresentation();
            try {
                return Integer.valueOf(numberString);
            } catch (Exception ex) {
                // TODO resolve name return bluePrint.
            }
        }else {
            Variable var = getVariable(name);

            if (var != null) {
                try {
                    return Integer.valueOf(var.getDimensionalInformation().get(1));
                } catch (Exception ex) {
                    // TODO resolve name return bluePrint.
                    Log.error(var.getDimensionalInformation().get(1));
                    return 1;
                }
            }
        }
        Log.info(name, "Not found:");
        return 0;
    }

    public int getAmountColumns(String name) {
        MathValueSymbol mathValueSymbol = getMathValueSymbol(name);

        if (mathValueSymbol != null) {
            String numberString = mathValueSymbol.getType().getDimensions().get(0).getTextualRepresentation();
            try {
                return Integer.valueOf(numberString);
            } catch (Exception ex) {
                // TODO resolve name return bluePrint.
            }
        }else {
            Variable var = getVariable(name);

            if (var != null) {
                try {
                    return Integer.valueOf(var.getDimensionalInformation().get(0));
                } catch (Exception ex) {
                    // TODO resolve name return bluePrint.
                    //Log.error(var.getDimensionalInformation().get(0));
                    return 1;
                }
            }
        }
        Log.info(name, "Not found:");
        return 0;
    }

    public int getAmountColumns(String name, MathMatrixAccessOperatorSymbol mathMatrixAccessOperatorSymbol) {
        MathValueSymbol mathValueSymbol = getMathValueSymbol(name);
        boolean firstDoubleDot = false, secondDoubleDot = false;
        if (mathMatrixAccessOperatorSymbol.getMathMatrixAccessSymbols().size() == 2) {
            firstDoubleDot = mathMatrixAccessOperatorSymbol.getMathMatrixAccessSymbols().get(0).isDoubleDot();
            secondDoubleDot = mathMatrixAccessOperatorSymbol.getMathMatrixAccessSymbols().get(1).isDoubleDot();
        }

        if (secondDoubleDot)
            return 1;

        if (mathValueSymbol != null) {
            String numberString = mathValueSymbol.getType().getDimensions().get(0).getTextualRepresentation();
            try {
                return Integer.valueOf(numberString);
            } catch (Exception ex) {
                // TODO resolve name return bluePrint.
            }
        }else {
            Variable var = getVariable(name);

            if (var != null) {
                try {
                    return Integer.valueOf(var.getDimensionalInformation().get(0));
                } catch (Exception ex) {
                    // TODO resolve name return bluePrint.
                    Log.error(var.getDimensionalInformation().get(0));
                    return 1;
                }
            }
        }
        Log.info(name, "Not found:");
        return 0;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    public void addVariable(Variable variable) {
        this.variables.add(variable);
    }

    public void addVariable(MathValueSymbol mathValueSymbol) {
        Variable var = new Variable(mathValueSymbol.getName(), Variable.VARIABLE);
        var.setTypeNameTargetLanguage(TypeConverter.getVariableTypeNameForMathLanguageTypeName(mathValueSymbol.getType()));
        for (MathExpressionSymbol dimension : mathValueSymbol.getType().getDimensions())
            var.addDimensionalInformation(dimension.getTextualRepresentation());
        this.variables.add(var);
    }



    public Variable getVariable(String name) {
        for (Variable v : variables)
            if (v.getName().equals(name))
                return v;
        for (Variable v : variables)
            if (v.getNameTargetLanguageFormat().equals(name))
                return v;
        return null;
    }

    public static String getVariableInitName(Variable v, BluePrintCPP bluePrint) {
        for (Variable v2 : bluePrint.getVariables()) {
            if (v2.isArray() && PortConverter.getPortNameWithoutArrayBracketPart(v2.getName()).equals(v.getName())) {
                if (!v.getName().endsWith("]")) {
                    return v.getName() + "[0]";
                }
            }
        }
        return v.getNameTargetLanguageFormat();
    }
}
