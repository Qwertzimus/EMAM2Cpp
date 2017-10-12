package de.monticore.lang.monticar.generator.cpp;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ExpandedComponentInstanceSymbol;
import de.monticore.lang.montiarc.stream._symboltable.NamedStreamSymbol;
import de.monticore.lang.monticar.generator.FileContent;
import de.monticore.lang.monticar.generator.cpp.converter.ComponentConverter;
import de.monticore.lang.monticar.streamunits._ast.ASTComponentStreamUnits;
import de.monticore.lang.monticar.streamunits._ast.ASTNamedStreamUnits;
import de.monticore.lang.monticar.streamunits._symboltable.ComponentStreamUnitsSymbol;
import de.monticore.lang.monticar.streamunits._symboltable.NamedStreamUnitsSymbol;

/**
 * @author Sascha Schneiders
 */
public class TestConverter {

    public static FileContent generateMainTestFile(ComponentStreamUnitsSymbol symbol, ExpandedComponentInstanceSymbol instanceSymbol) {
        FileContent fileContent = new FileContent();
        fileContent.setFileName("main_" + GeneralHelperMethods.getTargetLanguageComponentName(symbol.getFullName()) + ".cpp");
        String fileContentString = "";
        fileContentString += "#ifndef M_PI\n" +
                "#define M_PI 3.14159265358979323846\n" +
                "#endif\n" +
                "#include \"Helper.h\"\n";

        ASTComponentStreamUnits ast = (ASTComponentStreamUnits) symbol.getAstNode().get();
        fileContentString += "#include \"" + GeneralHelperMethods.getTargetLanguageComponentName(symbol.getPackageName() + "." + Character.toLowerCase(ast.getComponentName().charAt(0)) + ast.getComponentName().substring(1)) + ".h\"\n";
        fileContentString += "int main(int argc, char** argv)\n" +
                "{\n";
        fileContentString += "Helper::init();\n";
        fileContentString += "";
        fileContentString += GeneralHelperMethods.getTargetLanguageComponentName(symbol.getPackageName() + "." + Character.toLowerCase(ast.getComponentName().charAt(0)) + ast.getComponentName().substring(1));
        fileContentString += " testInstance;\n";
        fileContentString += "testInstance.init();\n";

        int executionCounter = 0;
        for (ASTNamedStreamUnits astNamedStreamUnit : ast.getNamedStreamUnitss()) {
            String portName = astNamedStreamUnit.getName();
            if (instanceSymbol.getPort(portName).get().isIncoming()) {
                if (astNamedStreamUnit.getStream().getPrecisionNumbers().size() > 0) {
                    fileContentString += "testInstance." + portName + "=" + astNamedStreamUnit.getStream().getPrecisionNumbers().get(executionCounter).getUnitNumber().getNumber().get() + ";";
                }
            }
        }
        fileContentString += "testInstance.execute();\n";
        for (ASTNamedStreamUnits astNamedStreamUnit : ast.getNamedStreamUnitss()) {
            String portName = astNamedStreamUnit.getName();
            if (!instanceSymbol.getPort(portName).get().isIncoming()) {
                if (astNamedStreamUnit.getStream().getPrecisionNumbers().size() > 0) {
                    fileContentString += "if(testInstance." + portName + "!=" + astNamedStreamUnit.getStream().getPrecisionNumbers().get(executionCounter).getUnitNumber().getNumber().get() + "){";
                    fileContentString += "printf(\"Mismatch at executionStep "+executionCounter+"\");\n";
                    fileContentString += "octave_quit();\n";
                    fileContentString += "}\n";
                }
            }
        }

        fileContentString+= "octave_quit();\n";
        fileContentString+= "printf(\"Execution ended successfully!\\n\");\n";
        fileContentString += "}\n";

        fileContent.setFileContent(fileContentString);
        return fileContent;
    }
}
