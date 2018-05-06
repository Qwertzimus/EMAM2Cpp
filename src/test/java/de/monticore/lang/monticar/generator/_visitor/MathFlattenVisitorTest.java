package de.monticore.lang.monticar.generator._visitor;


import de.monticore.ModelingLanguageFamily;
import de.monticore.io.paths.ModelPath;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTComponent;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ComponentSymbol;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ExpandedComponentInstanceSymbol;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarcmath._ast.ASTEMAMCompilationUnit;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarcmath._parser.EmbeddedMontiArcMathParser;
import de.monticore.lang.tagging._symboltable.TaggingResolver;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static de.monticore.lang.monticar.generator.order.simulator.AbstractSymtab.createSymTabAndTaggingResolver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MathFlattenVisitorTest {

    @Test
    public void mathFlattenTest() throws IOException {
        TaggingResolver symTab = createSymTabAndTaggingResolver("src/test/resources");
        ComponentSymbol comp = symTab.<ComponentSymbol>
                resolve("mathFlatten.MatrixModifier", ComponentSymbol.KIND).orElse(null);
        assertNotNull(comp);

        ComponentSymbol comp2 = symTab.<ComponentSymbol>resolve("mathFlatten.Mul", ComponentSymbol.KIND).orElse(null);

        assertTrue(comp.getAstNode().isPresent());
        ASTComponent astComponent = (ASTComponent)comp.getAstNode().get();

        String flattenedModel = MathFlattenVisitor.flattenMath(astComponent);
        String expected = new Scanner(new File("src/test/resources/mathFlatten/expected.emam")).useDelimiter("\\Z").next();
        assertEquals(expected, flattenedModel);
    }



}
