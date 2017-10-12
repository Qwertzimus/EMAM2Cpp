package de.monticore.lang.monticar.generator.cpp;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ExpandedComponentInstanceSymbol;
import de.monticore.lang.monticar.generator.AbstractSymtabTest;
import de.monticore.lang.monticar.generator.cpp.GeneratorCPP;
import de.monticore.lang.monticar.generator.cpp.converter.MathConverter;
import de.monticore.lang.monticar.generator.optimization.ThreadingOptimizer;
import de.monticore.symboltable.Scope;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * @author Sascha Schneiders
 */
public class ThreadingOptimizerTest extends AbstractSymtabTest {
    @Test
    public void testMathUnitThreading() throws IOException {
        MathConverter.resetIDs();
        ThreadingOptimizer.resetID();
        Scope symtab = createSymTab("src/test/resources");

        ExpandedComponentInstanceSymbol componentSymbol = symtab.<ExpandedComponentInstanceSymbol>resolve("paper.mathUnit", ExpandedComponentInstanceSymbol.KIND).orElse(null);
        assertNotNull(componentSymbol);
        GeneratorCPP generatorCPP = new GeneratorCPP();
        generatorCPP.setUseThreadingOptimization(true);
        generatorCPP.setGenerationTargetPath("./target/generated-sources-cpp/paperMatrixModifier/l2");
        List<File> files = generatorCPP.generateFiles(componentSymbol, symtab);
        String restPath = "paperMatrixModifier/l2/";
        testFilesAreEqual(files, restPath);
    }
}
