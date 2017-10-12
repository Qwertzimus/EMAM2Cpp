package de.monticore.lang.monticar.generator.cpp.converter;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTPort;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ExpandedComponentInstanceSymbol;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.PortSymbol;
import de.monticore.lang.math.math._ast.ASTAssignmentType;
import de.monticore.lang.math.math._ast.ASTDimension;
import de.monticore.lang.math.math._symboltable.expression.MathExpressionSymbol;
import de.monticore.lang.math.math._symboltable.expression.MathParenthesisExpressionSymbol;
import de.monticore.lang.math.math._symboltable.expression.MathValueExpressionSymbol;
import de.monticore.lang.math.math._symboltable.expression.MathValueType;
import de.monticore.lang.math.math._symboltable.matrix.MathMatrixArithmeticValueSymbol;
import de.monticore.lang.monticar.generator.cpp.GeneralHelperMethods;
import de.monticore.lang.monticar.types2._ast.ASTType;
import de.monticore.lang.monticar.common2._ast.ASTCommonDimensionElement;
import de.monticore.lang.monticar.common2._ast.ASTCommonMatrixType;
import de.monticore.lang.monticar.generator.Variable;
import de.monticore.lang.monticar.generator.VariableType;
import de.monticore.lang.monticar.si._symboltable.SIUnitRangesSymbol;
import de.monticore.lang.monticar.si._symboltable.SIUnitRangesSymbolReference;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;

import java.util.*;

/**
 * This is used to convert port types to their cpp equivalent
 *
 * @author Sascha Schneiders
 */
public class TypeConverter {
    private static List<VariableType> nonPrimitiveVariableTypes = new ArrayList<>();

    public static String getTypeNameTargetLanguage(JTypeReference<? extends JTypeSymbol> portType) {
        for (VariableType variableType : nonPrimitiveVariableTypes) {
            if (variableType.getTypeNameMontiCar().equals(portType.getName()))
                return variableType.getTypeNameTargetLanguage();
        }
        Log.info(portType.getName(), "Unknown Type:");
        Log.error("0xUNPOTYFOBYGE Unknown Port Type found by generator");
        return "";
    }

    public static boolean isNonPrimitiveVariableTypeName(String typeName) {
        return nonPrimitiveVariableTypes.contains(typeName);
    }

    public static void addNonPrimitiveVariableType(String typeNameMontiCar, String typeNameTargetLanguage, String includeName) {
        nonPrimitiveVariableTypes.add(new VariableType(typeNameMontiCar, typeNameTargetLanguage, includeName));
    }

    public static void addNonPrimitiveVariableType(VariableType variableType) {
        nonPrimitiveVariableTypes.add(variableType);
    }

    public static String getVariableTypeNameForMathLanguageTypeName(MathValueType mathValueType) {
        if (mathValueType.getDimensions().size() == 0) {
            if(mathValueType.getType().isIsWholeNumberNumber()){
                return "int";//use int for now add range check if bigger number is required
            }
            return "double";
        }
        else if (mathValueType.getDimensions().size() == 2) {
            return "Matrix";
        }
        Log.error("TypeConverter Case not handled!");
        return null;
    }

    public static Optional<VariableType> getVariableTypeForMontiCarTypeName(String typeNameMontiCar) {
        for (VariableType variableType : nonPrimitiveVariableTypes) {
            if (variableType.getTypeNameMontiCar().equals(typeNameMontiCar))
                return Optional.of(variableType);
        }
        Log.info(typeNameMontiCar, "Unknown Type:");
        //Log.error("0xUNPOTYFOBYGE Unknown Port Type found by generator");
        return Optional.empty();
    }

    public static Optional<VariableType> getVariableTypeForMontiCarTypeName(String typeNameMontiCar, Variable variable, ASTType astType) {
        for (VariableType variableType : nonPrimitiveVariableTypes) {
            if (variableType.getTypeNameMontiCar().equals(typeNameMontiCar)) {
                if (typeNameMontiCar.equals("CommonMatrixType")) {
                    ASTCommonMatrixType astCommonMatrixType = (ASTCommonMatrixType) astType;
                    for (ASTCommonDimensionElement astCommonDimensionElement :
                            astCommonMatrixType.getCommonDimension().getCommonDimensionElements()) {
                        if (astCommonDimensionElement.getName().isPresent())
                            variable.addDimensionalInformation(astCommonDimensionElement.getName().get());
                        else if (astCommonDimensionElement.getUnitNumber().isPresent())
                            variable.addDimensionalInformation(astCommonDimensionElement.getUnitNumber().get().getNumber().get().getDividend() + "");
                        else {
                            Log.error("Case not handled;");
                        }
                    }
                } else if (typeNameMontiCar.equals("AssignmentType")) {//TODO Add MatrixProperties to MathInformation
                    ASTAssignmentType astAssignmentType = (ASTAssignmentType) astType;
                    if (astAssignmentType.getDim().isPresent()) {
                        ASTDimension astDimension = astAssignmentType.getDim().get();
                        variable.addDimensionalInformation(((MathExpressionSymbol) astDimension.getMathArithmeticExpressions().get(0).getSymbol().get()).getTextualRepresentation());
                        variable.addDimensionalInformation(((MathExpressionSymbol) astDimension.getMathArithmeticExpressions().get(1).getSymbol().get()).getTextualRepresentation());

                    }

                }
                return Optional.of(variableType);
            }
        }
        Log.info(typeNameMontiCar, "Unknown Type:");
        //Log.error("0xUNPOTYFOBYGE Unknown Port Type found by generator");
        return Optional.empty();
    }

    public static Optional<VariableType> getVariableTypeForMontiCarTypeName(String typeNameMontiCar, Variable variable, PortSymbol portSymbol) {
        for (VariableType variableType : nonPrimitiveVariableTypes) {
            if (variableType.getTypeNameMontiCar().equals(typeNameMontiCar)) {
                if (typeNameMontiCar.equals("CommonMatrixType")) {
                    ASTCommonMatrixType astCommonMatrixType = (ASTCommonMatrixType) ((ASTPort) portSymbol.getAstNode().get()).getType();
                    for (ASTCommonDimensionElement astCommonDimensionElement :
                            astCommonMatrixType.getCommonDimension().getCommonDimensionElements()) {
                        if (astCommonDimensionElement.getName().isPresent())
                            variable.addDimensionalInformation(astCommonDimensionElement.getName().get());
                        else if (astCommonDimensionElement.getUnitNumber().isPresent())
                            variable.addDimensionalInformation(astCommonDimensionElement.getUnitNumber().get().getNumber().get().getDividend() + "");
                        else {
                            Log.error("Case not handled;");
                        }
                    }
                } else if (typeNameMontiCar.equals("AssignmentType")) {//TODO Add MatrixProperties to MathInformation
                    ASTAssignmentType astAssignmentType = (ASTAssignmentType) ((ASTPort) portSymbol.getAstNode().get()).getType();
                    if (astAssignmentType.getDim().isPresent()) {
                        ASTDimension astDimension = astAssignmentType.getDim().get();
                        variable.addDimensionalInformation(((MathExpressionSymbol) astDimension.getMathArithmeticExpressions().get(0).getSymbol().get()).getTextualRepresentation());
                        variable.addDimensionalInformation(((MathExpressionSymbol) astDimension.getMathArithmeticExpressions().get(1).getSymbol().get()).getTextualRepresentation());

                    }

                }
                return Optional.of(variableType);
            }
        }
        Log.info(typeNameMontiCar, "Unknown Type:");
        //Log.error("0xUNPOTYFOBYGE Unknown Port Type found by generator");
        return Optional.empty();
    }

    public static VariableType getVariableTypeForMontiCarInstance(ExpandedComponentInstanceSymbol instanceSymbol) {
        VariableType type = TypeConverter.getVariableTypeForMontiCarTypeName(instanceSymbol.getFullName()).orElse(null);
        if (type != null)
            return type;
        type = new VariableType();
        type.setTypeNameMontiCar(instanceSymbol.getFullName());
        type.setTypeNameTargetLanguage(GeneralHelperMethods.getTargetLanguageComponentName(instanceSymbol.getFullName()));
        type.setIncludeName(type.getTypeNameTargetLanguage());
        TypeConverter.addNonPrimitiveVariableType(type);
        return type;
    }
    public static String getTypeName(MathMatrixArithmeticValueSymbol mathExpressionSymbol){
        if(mathExpressionSymbol.getVectors().size()>1){
            if(mathExpressionSymbol.getVectors().get(0).getMathMatrixAccessSymbols().size()>1)
                return "Matrix";
            else{
                return "ColumnVector";
            }
        }else{
            return "RowVector";
        }
    }

    public static VariableType getVariableTypeForTargetLanguageTypeName(String targetLanguageTypeName){
        VariableType type=new VariableType();
        type.setTypeNameTargetLanguage(targetLanguageTypeName);
        type.setIncludeName("octave/oct");
        return type;
    }
    static {
        addNonPrimitiveVariableType("SIUnitRangesType", "double", "");
        addNonPrimitiveVariableType("ElementType", "double", "");
        addNonPrimitiveVariableType("Boolean", "bool", "");
        addNonPrimitiveVariableType("UnitNumberResolution", "double", "");
        addNonPrimitiveVariableType("CommonMatrixType", "Matrix", "octave/oct");
        addNonPrimitiveVariableType("AssignmentType", "Matrix", "octave/oct");

    }
}
