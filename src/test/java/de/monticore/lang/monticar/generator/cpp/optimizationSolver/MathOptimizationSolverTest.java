package de.monticore.lang.monticar.generator.cpp.optimizationSolver;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ExpandedComponentInstanceSymbol;
import de.monticore.lang.monticar.generator.AbstractSymtabTest;
import de.monticore.lang.monticar.generator.cpp.GeneratorCPP;
import de.monticore.lang.tagging._symboltable.TaggingResolver;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Some unit test for generating code for a optimization problem.
 *
 * @author Christoph Richter
 */
public class MathOptimizationSolverTest extends AbstractSymtabTest {

    /**
     * Simple quadratic problem min x^2-2x+1 s.t. x >= 0
     */
    @Test
    public void testSimpleOptimizationTest() throws IOException {
        TaggingResolver symtab = createSymTabAndTaggingResolver("src/test/resources");
        ExpandedComponentInstanceSymbol componentSymbol = symtab.<ExpandedComponentInstanceSymbol>resolve("test.math.optimization.simpleOptimizationTest", ExpandedComponentInstanceSymbol.KIND).orElse(null);
        assertNotNull(componentSymbol);
        GeneratorCPP generatorCPP = new GeneratorCPP();
        generatorCPP.useArmadilloBackend();
        generatorCPP.setGenerationTargetPath("./target/generated-sources-cpp/testMath/optimizationSolver");
        List<File> files = generatorCPP.generateFiles(symtab, componentSymbol, symtab);
        // TODO: create reference solution
        // String restPath = "testMath/optimizationSolver/";
        // testFilesAreEqual(files, restPath);
    }
}
