package de.monticore.lang.monticar.generator.cpp;

import de.monticore.lang.math.math._symboltable.expression.*;
import de.monticore.lang.math.math._symboltable.matrix.MathMatrixAccessSymbol;
import de.monticore.lang.math.math._symboltable.matrix.MathMatrixExpressionSymbol;
import de.monticore.lang.math.math._symboltable.matrix.MathMatrixNameExpressionSymbol;
import de.monticore.lang.monticar.generator.FileContent;
import de.monticore.lang.monticar.generator.Variable;
import de.monticore.lang.monticar.generator.cpp.converter.ComponentConverter;
import de.monticore.lang.monticar.generator.cpp.converter.ExecuteMethodGenerator;
import de.monticore.lang.monticar.generator.cpp.converter.MathConverter;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sascha Schneiders
 */
public class OctaveHelper {
    public static String fileName = "Helper";

    public static FileContent getOctaveHelperFileContent() {
        FileContent fileContent = new FileContent();
        fileContent.setFileName(fileName + ".h");
        String fileContentString = "#ifndef HELPER_H\n" +
                "#define HELPER_H\n" +
                "#define _GLIBCXX_USE_CXX11_ABI 0\n" +
                "#include <iostream>\n" +
                "#include <octave/oct.h>\n" +
                "#include <octave/octave.h>\n" +
                "#include <octave/parse.h>\n" +
                "#include <octave/interpreter.h>\n" +
                "#include <stdarg.h>\n" +
                "#include <initializer_list>\n" +
                "class Helper\n" +
                "{\n" +
                "public:\n" +
                "    static void init()\n" +
                "    {\n" +
                "        string_vector argv(2);\n" +
                "        argv(0) = \"embedded\";\n" +
                "        argv(1) = \"-q\";\n" +
                "        octave_main(2, argv.c_str_vec(), 1);\n" +
                "        //octave_debug=1;\n" +
                "        //feval (\"pkg\", ovl (\"load\", \"all\"), 0);\n" +
                "    }\n" +
                "\n" +
                "    static octave_value_list convertToOctaveValueList(double a)\n" +
                "    {\n" +
                "        octave_value_list in;\n" +
                "        in(0) = a;\n" +
                "\n" +
                "        return in;\n" +
                "    }\n" +
                "\n" +
                "    static octave_value_list convertToOctaveValueList(Matrix a)\n" +
                "    {\n" +
                "        octave_value_list in;\n" +
                "        in(0) = a;\n" +
                "\n" +
                "        return in;\n" +
                "    }\n" +
                "\n" +
                "    static octave_value_list convertToOctaveValueList(RowVector a)\n" +
                "    {\n" +
                "        octave_value_list in;\n" +
                "        in(0) = a;\n" +
                "\n" +
                "        return in;\n" +
                "    }\n" +
                "\n" +
                "    static octave_value_list convertToOctaveValueList(ColumnVector a)\n" +
                "    {\n" +
                "        octave_value_list in;\n" +
                "        in(0) = a;\n" +
                "\n" +
                "        return in;\n" +
                "    }\n" +
                "\n" +
                "    static octave_value_list convertToOctaveValueList(double a, double b)\n" +
                "    {\n" +
                "        octave_value_list in;\n" +
                "        in(0) = a;\n" +
                "        in(1) = b;\n" +
                "\n" +
                "        return in;\n" +
                "    }\n" +
                "\n" +
                "    static octave_value_list convertToOctaveValueList(std::initializer_list<double> args)\n" +
                "    {\n" +
                "        octave_value_list in;\n" +
                "        int counter = 0;\n" +
                "        for(double element : args) {\n" +
                "            in(counter) = octave_value(element);\n" +
                "            ++counter;\n" +
                "        }\n" +
                "\n" +
                "        return in;\n" +
                "    }\n" +
                "\n" +
                "    static octave_value_list convertToOctaveValueList(Matrix a, double b)\n" +
                "    {\n" +
                "        octave_value_list in;\n" +
                "        in(0) = a;\n" +
                "        in(1) = b;\n" +
                "        return in;\n" +
                "    }\n" +
                "\n" +
                "    static octave_value_list convertToOctaveValueList(RowVector a, double b)\n" +
                "    {\n" +
                "        octave_value_list in;\n" +
                "        in(0) = a;\n" +
                "        in(1) = b;\n" +
                "\n" +
                "        return in;\n" +
                "    }\n" +
                "\n" +
                "    static octave_value_list convertToOctaveValueList(ColumnVector a, double b)\n" +
                "    {\n" +
                "        octave_value_list in;\n" +
                "        in(0) = a;\n" +
                "        in(1) = b;\n" +
                "\n" +
                "        return in;\n" +
                "    }\n" +
                "\n" +
                "    static octave_value_list callOctaveFunction(octave_value_list in, std::string functionName,int argsOut)\n" +
                "    {\n" +
                "\n" +
                "        /*octave_idx_type n = 2;\n" +
                "        octave_value_list in;\n" +
                "\n" +
                "        for(octave_idx_type i = 0; i < n; i++)\n" +
                "            in(i) = octave_value(5 * (i + 2));\n" +
                "\n" +
                "        octave_value_list out = feval(\"gcd\", in, 1);\n" +
                "\n" +
                "        if(!error_state && out.length() > 0)\n" +
                "            std::cout << \"GCD of [\" << in(0).int_value() << \", \" << in(1).int_value() << \"] is \" << out(0).int_value()\n" +
                "                      << std::endl;\n" +
                "        else\n" +
                "            std::cout << \"invalid\\n\";\n" +
                "\n" +
                "        clean_up_and_exit(0);*/\n" +
                "  /*      if(functionName==\"eigs\")\n" +
                "            return feval(functionName, in, 2);\n" +
                "        else if(functionName==\"kmeans\")\n" +
                "            return feval(functionName, in, 2);\n" +
                "*/\n" +
                "        return feval(functionName, in, argsOut);\n" +
                "    }\n" +
                "\n" +
                "    static int callOctaveFunctionIntFirstResult(octave_value_list in, std::string functionName, int argsOut)\n" +
                "    {\n" +
                "        // printf(\"callOctaveFunctionIntFirstResult pre return functionName: %s\\n\",functionName.c_str());\n" +
                "        return callOctaveFunction(in, functionName,argsOut)(0).int_value();\n" +
                "    }\n" +
                "\n" +
                "    static double callOctaveFunctionDoubleFirstResult(octave_value_list in, std::string functionName, int argsOut)\n" +
                "    {\n" +
                "        // printf(\"callOctaveFunctionDoubleFirstResult pre return functionName: %s\\n\",functionName.c_str());\n" +
                "        return callOctaveFunction(in, functionName,argsOut)(0).double_value();\n" +
                "    }\n" +
                "\n" +
                "    static Matrix callOctaveFunctionMatrixFirstResult(octave_value_list in, std::string functionName, int argsOut)\n" +
                "    {\n" +
                "        return callOctaveFunction(in, functionName,argsOut)(0).matrix_value();\n" +
                "    }\n" +
                "    static ColumnVector callOctaveFunctionColumnVectorFirstResult(octave_value_list in, std::string functionName, int argsOut)\n" +
                "    {\n" +
                "        printf(\"pre Call %s\\n\", functionName.c_str());\n" +
                "        try {\n" +
                "            in=octave_value_list();\n" +
                "            octave_value_list list = callOctaveFunction(in, functionName,argsOut);\n" +
                "            printf(\"post Call %s\\n\", functionName.c_str());\n" +
                "            return list(0).array_value().as_column();\n" +
                "        } catch(const std::exception& e) {\n" +
                "            printf(\"%s\\n\", e.what());\n" +
                "        }\n" +
                "        return ColumnVector();\n" +
                "    }\n" +
                "\n" +
                "    static RowVector callOctaveFunctionRowVectorFirstResult(octave_value_list in, std::string functionName, int argsOut)\n" +
                "    {\n" +
                "        return callOctaveFunction(in, functionName,argsOut)(0).array_value().as_row();\n" +
                "    }\n" +
                "\n" +
                "    static int callOctaveFunctionIntSecondResult(octave_value_list in, std::string functionName, int argsOut)\n" +
                "    {\n" +
                "        return callOctaveFunction(in, functionName,argsOut)(1).int_value();\n" +
                "    }\n" +
                "\n" +
                "    static double callOctaveFunctionDoubleSecondResult(octave_value_list in, std::string functionName, int argsOut)\n" +
                "    {\n" +
                "        return callOctaveFunction(in, functionName,argsOut)(1).double_value();\n" +
                "    }\n" +
                "\n" +
                "    static Matrix callOctaveFunctionMatrixSecondResult(octave_value_list in, std::string functionName, int argsOut)\n" +
                "    {\n" +
                "        return callOctaveFunction(in, functionName,argsOut)(1).matrix_value();\n" +
                "    }\n" +
                "    static ColumnVector callOctaveFunctionColumnVectorSecondResult(octave_value_list in, std::string functionName, int argsOut)\n" +
                "    {\n" +
                "        return callOctaveFunction(in, functionName,argsOut)(1).array_value().as_column();\n" +
                "    }\n" +
                "\n" +
                "    static RowVector callOctaveFunctionRowVectorSecondResult(octave_value_list in, std::string functionName, int argsOut)\n" +
                "    {\n" +
                "        return callOctaveFunction(in, functionName,argsOut)(1).array_value().as_row();\n" +
                "    }\n" +
                "    \n" +
                "    static Matrix getMatrixFromOctaveListFirstResult(octave_value_list list){\n" +
                "        return list(0).matrix_value();\n" +
                "    }\n" +
                "    \n" +
                "    \n" +
                "    static RowVector getRowVectorFromOctaveListFirstResult(octave_value_list list){\n" +
                "        return list(0).array_value().as_row();\n" +
                "    }\n" +
                "    \n" +
                "    \n" +
                "    static ColumnVector getColumnVectorFromOctaveListFirstResult(octave_value_list list){\n" +
                "        return list(0).array_value().as_column();\n" +
                "    }\n" +
                "    \n" +
                "    \n" +
                "    static double getDoubleFromOctaveListFirstResult(octave_value_list list){\n" +
                "        return list(0).double_value();\n" +
                "    }\n" +
                "    \n" +
                "    \n" +
                "    static int getIntFromOctaveListFirstResult(octave_value_list list){\n" +
                "        return list(0).int_value();\n" +
                "    }\n" +
                "};\n" +
                "\n" +
                "#endif // HELPER_H";

        fileContent.setFileContent(fileContentString);
        return fileContent;
    }

    public static String getOctaveValueListString(List<MathExpressionSymbol> expressionSymbolList) {
        String valueListString = "";
        int counter = 0;
        for (MathExpressionSymbol symbol : expressionSymbolList) {
            MathConverter.fixMathFunctions(symbol, ComponentConverter.currentBluePrint);
            valueListString += ExecuteMethodGenerator.generateExecuteCode(symbol, new ArrayList<String>());
            ++counter;
            if (counter < expressionSymbolList.size())
                valueListString += ",";
        }
        return valueListString;
    }

    public static String getCallBuiltInFunctionFirstResult(MathExpressionSymbol mathExpressionSymbol, String functionName, String valueListString, boolean useBraces, int argsOut) {
        return getCallBuiltInFunction(mathExpressionSymbol, functionName, valueListString, "FirstResult", useBraces, argsOut);
    }

    public static String getCallOctaveFunctionFirstResult(MathExpressionSymbol mathExpressionSymbol, String functionName, String valueListString, boolean useBraces) {
        return getCallOctaveFunction(mathExpressionSymbol, functionName, valueListString, "FirstResult", useBraces);
    }

    public static String getCallOctaveFunctionSecondResult(MathExpressionSymbol mathExpressionSymbol, String functionName, String valueListString, boolean useBraces) {
        return getCallOctaveFunction(mathExpressionSymbol, functionName, valueListString, "SecondResult", useBraces);
    }


    /*public static String getCallOctaveFunctionThirdResult(MathExpressionSymbol mathExpressionSymbol, String functionName, String valueListString, boolean useBraces) {
        return getCallOctaveFunction(mathExpressionSymbol, functionName, valueListString, "ThirdResult", useBraces);
    }

    public static String getCallOctaveFunctionFourthResult(MathExpressionSymbol mathExpressionSymbol, String functionName, String valueListString, boolean useBraces) {
        return getCallOctaveFunction(mathExpressionSymbol, functionName, valueListString, "FourthResult", useBraces);
    }*/

    /*public static String getCallOctaveFunction(MathExpressionSymbol mathExpressionSymbol, String functionName, String valueListString, String numberResultString) {

        String functionTypeName = OctaveHelper.getOctaveFunctionTypeName(mathExpressionSymbol);
        return getCallOctaveFunction(mathExpressionSymbol, functionName, functionTypeName, valueListString, numberResultString);
    }*/

    public static String getCallOctaveFunction(MathExpressionSymbol mathExpressionSymbol, String functionName, String valueListString, String numberResultString, boolean useBraces) {

        String functionTypeName = OctaveHelper.getOctaveFunctionTypeName(mathExpressionSymbol);
        return getCallOctaveFunction(mathExpressionSymbol, functionName, functionTypeName, valueListString, numberResultString, useBraces);
    }

    public static String getCallBuiltInFunction(MathExpressionSymbol mathExpressionSymbol, String functionName, String valueListString, String numberResultString, boolean useBraces, int argsOut) {

        String functionTypeName = OctaveHelper.getOctaveFunctionTypeName(mathExpressionSymbol);
        return getCallBuiltInFunction(functionName, functionTypeName, valueListString, numberResultString, argsOut);
    }


    public static String getCallBuiltInFunction(String functionName, String functionType, String valueListString, String numberResultString, int argsOut) {
        String result = "";
        result += "Helper::get";
        //TODO calc type of function to call
        result += functionType;
        result += "FromOctaveList";
        result += numberResultString;
        result += "(";
        result += functionName;
        result += "(Helper::convertToOctaveValueList";
        result += valueListString;
        result+=","+argsOut;
        result += "))";
        // result += ",\"" + functionName + "\")";
        return result;
    }

    public static String getCallOctaveFunction(MathExpressionSymbol mathExpressionSymbol, String functionName, String functionType, String valueListString, String numberResultString, boolean useBraces) {
        return getCallOctaveFunction(mathExpressionSymbol, functionName, functionType, valueListString, numberResultString, useBraces, 1);
    }

    public static String getCallOctaveFunction(MathExpressionSymbol mathExpressionSymbol, String functionName, String functionType, String valueListString, String numberResultString, boolean useBraces, int argsOut) {
        String result = "";
        result += "Helper::callOctaveFunction";
        //TODO calc type of function to call
        result += functionType;
        result += numberResultString;
        result += "(Helper::convertToOctaveValueList";
        if (useBraces)
            result += "({";
        result += valueListString;
        if (useBraces)
            result += "})";
        result += "";
        result += ",\"" + functionName + "\"";
        result += "," + argsOut;
        result += ")";
        return result;
    }

    /*public static String getCallOctaveFunction(MathExpressionSymbol mathExpressionSymbol, String functionName, String functionType, String valueListString, String numberResultString) {
        return getCallOctaveFunction(mathExpressionSymbol, functionName, functionType, valueListString, numberResultString, true);
    }*/

    public static String getOctaveFunctionTypeName(MathExpressionSymbol mathExpressionSymbol) {
        if (mathExpressionSymbol.isMatrixExpression()) {
            return getOctaveFunctionTypeName((MathMatrixExpressionSymbol) mathExpressionSymbol);
        } else if (mathExpressionSymbol.isArithmeticExpression()) {
            return getOctaveFunctionTypeName((MathArithmeticExpressionSymbol) mathExpressionSymbol);
        } else if (mathExpressionSymbol.isValueExpression()) {
            return getOctaveFunctionTypeName((MathValueExpressionSymbol) mathExpressionSymbol);
        } else if (mathExpressionSymbol.isParenthesisExpression()) {
            return getOctaveFunctionTypeName(((MathParenthesisExpressionSymbol) mathExpressionSymbol).getMathExpressionSymbol());
        } else {
            Log.info(mathExpressionSymbol.getClass().getName(), "Symbol Name:");
            Log.info(mathExpressionSymbol.getTextualRepresentation(), "Symbol:");
            Log.error("Octave function type name not handled");
            return null;
        }
    }

    public static String getOctaveFunctionTypeName(MathValueExpressionSymbol mathExpressionSymbol) {
        if (mathExpressionSymbol.isNumberExpression())
            return "Double";
        else if (mathExpressionSymbol.isNameExpression()) {
            Variable variable = ComponentConverter.currentBluePrint.getMathInformationRegister().getVariable(((MathNameExpressionSymbol) mathExpressionSymbol).getNameToResolveValue());
            return variable.getVariableType().getTypeNameTargetLanguage();
        } else {
            Log.error("OctaveHelper Case not handled!");
        }
        return null;
    }

    public static String getOctaveFunctionTypeName(MathArithmeticExpressionSymbol mathExpressionSymbol) {
        return getOctaveFunctionTypeName(mathExpressionSymbol.getLeftExpression());
    }

    public static String getOctaveFunctionTypeName(MathMatrixExpressionSymbol mathExpressionSymbol) {
        if (mathExpressionSymbol.isMatrixNameExpression()) {
            return getOctaveFunctionTypeName((MathMatrixNameExpressionSymbol) mathExpressionSymbol);
        } else {
            Log.error("Case not handeld!");
            return null;
        }
    }


    public static String getOctaveFunctionTypeName(MathMatrixNameExpressionSymbol mathExpressionSymbol) {
        String result = "";

        if (mathExpressionSymbol.getMathMatrixAccessOperatorSymbol().getMathMatrixAccessSymbols().size() == 2) {
            MathMatrixAccessSymbol firstAccessSymbol = mathExpressionSymbol.getMathMatrixAccessOperatorSymbol().getMathMatrixAccessSymbols().get(0);
            MathMatrixAccessSymbol secondAccessSymbol = mathExpressionSymbol.getMathMatrixAccessOperatorSymbol().getMathMatrixAccessSymbols().get(1);


            if (firstAccessSymbol.isDoubleDot()) {
                result += "ColumnVector";
            } else if (secondAccessSymbol.isDoubleDot()) {
                result += "RowVector";
            } else {
                result += "Double";
            }


        } else if (mathExpressionSymbol.getMathMatrixAccessOperatorSymbol().getMathMatrixAccessSymbols().size() == 1) {

        } else {
            Log.info(mathExpressionSymbol.getTextualRepresentation(), "Symbol:");
            Log.error("FunctionTypeName not handled!");
        }

        return result;
    }

    public static String getCallBuiltInFunction(MathExpressionSymbol mathExpressionSymbol, String functionName, String functionType, String valueListString, String numberResult, boolean b, int argsOut) {
        return getCallBuiltInFunction(functionName, functionType, valueListString, numberResult, argsOut);
    }
}
