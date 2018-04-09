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
     * symbol table as static class variable so it must be created only once
     */
    private static TaggingResolver symtab = createSymTabAndTaggingResolver("src/test/resources");

    /**
     * helper method to generate optimization models in CPP code
     *
     * @param modelName
     * @return
     * @throws IOException
     */
    protected static List<File> doGenerateOptimizationModel(String modelName) throws IOException {
        ExpandedComponentInstanceSymbol componentSymbol = symtab.<ExpandedComponentInstanceSymbol>resolve(String.format("test.math.optimization.%s", modelName), ExpandedComponentInstanceSymbol.KIND).orElse(null);
        assertNotNull(componentSymbol);
        GeneratorCPP generatorCPP = new GeneratorCPP();
        generatorCPP.useArmadilloBackend();
        generatorCPP.setGenerationTargetPath("./target/generated-sources-cpp/testMath/optimizationSolver");
        List<File> files = generatorCPP.generateFiles(symtab, componentSymbol, symtab);
        return files;
    }

    /**
     * Simple quadratic problem min x^2-2x+1 s.t. x >= 0
     */
    @Test
    public void testScalarOptimizationTest() throws IOException {
        List<File> files = doGenerateOptimizationModel("scalarOptimizationTest");
        // TODO: create reference solution
        // String restPath = "testMath/optimizationSolver/";
        // testFilesAreEqual(files, restPath);
    }

    /**
     * example problem, number 71 from the Hock-Schittkowsky test suite
     * min_{x \in \R^4}	 x_1 x_4 (x_1 + x_2 + x_3) + x_3
     * s.t.
     * x_1 x_2 x_3 x_4 \ge 25
     * x_1^2 + x_2^2 + x_3^2 + x_4^2 = 40
     * 1 \leq x_1, x_2, x_3, x_4 \leq 5
     */
    @Test
    public void testStandardIpoptOptimizationTest() throws IOException {
        List<File> files = doGenerateOptimizationModel("standardIpoptOptimizationTest");
        // TODO: create reference solution
        // String restPath = "testMath/optimizationSolver/";
        // testFilesAreEqual(files, restPath);
    }

    /**
     * instance of the transportation problem (linear)
     * see https://www.gams.com/products/simple-example/
     */
    @Test
    public void testLPOptimizationTest() throws IOException {
        List<File> files = doGenerateOptimizationModel("lPOptimizationTest");
        // TODO: create reference solution
        // String restPath = "testMath/optimizationSolver/";
        // testFilesAreEqual(files, restPath);
    }
}
