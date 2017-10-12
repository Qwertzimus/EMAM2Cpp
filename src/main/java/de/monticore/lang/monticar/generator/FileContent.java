package de.monticore.lang.monticar.generator;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ExpandedComponentInstanceSymbol;
import de.monticore.lang.monticar.generator.cpp.converter.ComponentConverter;

/**
 * @author Sascha Schneiders
 */
public class FileContent {
    String fileContent;
    String fileName;

    public FileContent() {

    }

    public FileContent(String fileContent, ExpandedComponentInstanceSymbol instanceSymbol) {
        this.fileContent = fileContent;
        fileName = ComponentConverter.getTargetLanguageComponentName(instanceSymbol.getFullName())+".h";
    }

    public FileContent(String fileContent, String fileName) {
        this.fileContent = fileContent;
        this.fileName = fileName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
