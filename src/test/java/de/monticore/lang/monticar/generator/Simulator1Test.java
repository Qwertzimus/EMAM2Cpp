package de.monticore.lang.monticar.generator;

        import de.monticore.lang.monticar.generator.order.simulator.AbstractSymtab;
        import de.monticore.lang.tagging._symboltable.TaggingResolver;
        import org.junit.Assert;
        import org.junit.Before;
        import org.junit.Ignore;
        import org.junit.Test;
        import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ExpandedComponentInstanceSymbol;

public class Simulator1Test extends AbstractSymtab{

    @Test
    public void resolveMainController() {
        TaggingResolver symtab = createSymTabAndTaggingResolver("src/test/resources");
        ExpandedComponentInstanceSymbol instanceSymbol = symtab.<ExpandedComponentInstanceSymbol>resolve("simulator1.constantVelocity",
                ExpandedComponentInstanceSymbol.KIND).orElse(null);

        Assert.assertNotNull(instanceSymbol);
    }

    @Test
    public void resolveModelsSteeringControl() {
        TaggingResolver symtab = createSymTabAndTaggingResolver("src/test/resources");
        ExpandedComponentInstanceSymbol instanceSymbol = symtab.<ExpandedComponentInstanceSymbol>resolve("simulator1.steeringControl",
                ExpandedComponentInstanceSymbol.KIND).orElse(null);

        Assert.assertNotNull(instanceSymbol);
    }

    @Test
    public void resolveModelGameOverTrigger() {
        TaggingResolver symtab = createSymTabAndTaggingResolver("src/test/resources");
        ExpandedComponentInstanceSymbol instanceSymbol = symtab.<ExpandedComponentInstanceSymbol>resolve("simulator1.gameOverTrigger",
                ExpandedComponentInstanceSymbol.KIND).orElse(null);

        Assert.assertNotNull(instanceSymbol);
    }

    @Test
    public void resolveModel() {
        TaggingResolver symtab = createSymTabAndTaggingResolver("src/test/resources");
        ExpandedComponentInstanceSymbol instanceSymbol = symtab.<ExpandedComponentInstanceSymbol>resolve("simulator1.mainController",
                ExpandedComponentInstanceSymbol.KIND).orElse(null);

        Assert.assertNotNull(instanceSymbol);
    }
}