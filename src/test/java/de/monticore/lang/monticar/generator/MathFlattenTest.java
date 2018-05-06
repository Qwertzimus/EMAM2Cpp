package de.monticore.lang.monticar.generator;


import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ComponentSymbol;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ExpandedComponentInstanceSymbol;
import de.monticore.lang.monticar.generator.optimization.MathFlatten;
import de.monticore.lang.tagging._symboltable.TaggingResolver;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static de.monticore.lang.monticar.generator.order.simulator.AbstractSymtab.createSymTabAndTaggingResolver;
import static org.junit.Assert.*;

public class MathFlattenTest {

    @Test
    public void mathFlattenTest() throws IOException {
        TaggingResolver symTab = createSymTabAndTaggingResolver("src/test/resources");
        ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>
                resolve("mathFlatten.matrixModifier", ExpandedComponentInstanceSymbol.KIND).orElse(null);
        assertNotNull(inst);

        String flattenedModel = MathFlatten.printMathFlattended(inst, symTab);
        String expected = new Scanner(new File("src/test/resources/mathFlatten/expected.emam")).useDelimiter("\\Z").next();
        assertEquals(expected, flattenedModel);
    }



}
