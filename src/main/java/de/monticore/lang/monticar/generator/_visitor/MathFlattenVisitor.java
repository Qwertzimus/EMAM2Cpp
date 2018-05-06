package de.monticore.lang.monticar.generator._visitor;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTComponent;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTPort;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTSubComponent;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTSubComponentInstance;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarcbehavior._ast.ASTBehaviorImplementation;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarcmath._visitor.EmbeddedMontiArcMathVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.Scope;


public class MathFlattenVisitor implements EmbeddedMontiArcMathVisitor {

    protected MathFlattenVisitor realThis;
    protected IndentPrinter printer;
    protected Scope componentScope;

    public MathFlattenVisitor(Scope scope) {
        printer = new IndentPrinter();
        realThis = this;
        componentScope = scope;
    }

    public static String flattenMath(ASTComponent node) {
        MathFlattenVisitor visitor = new MathFlattenVisitor(node.getSpannedScope().get());
        node.accept(visitor);
        return visitor.printer.getContent();
    }

    @Override
    public void visit(ASTBehaviorImplementation node) {
        printer.print("{}");
    }


    @Override
    public void traverse(ASTComponent node) {
        printer.print("component " + node.getName() + "{\n");
        printer.indent();

        if (!node.getPorts().isEmpty()) {
            printer.print("ports\n");
            printer.indent();
            node.getPorts().forEach(p -> p.accept(realThis));
            printer.unindent();
        }

        printer.print("implementation Math{\n");
        printer.indent();
        node.getSubComponents().forEach(c -> c.accept(realThis));
        printer.unindent();

        printer.unindent();
        printer.print("}\n");
    }

    @Override
    public void traverse(ASTPort node){
        if(node.isIncoming())
            printer.print("in ");
        else
            printer.print("out ");
        printer.print("[[Fix type printing]]");
        printer.print(" " + node.getName().orElse("") + ",\n");
    }


    @Override
    public void traverse(ASTSubComponent node) {
        node.getType();
        //node.getInstances().forEach(i -> i.accept(realThis));
    }

    @Override
    public void traverse(ASTSubComponentInstance node) {
        printer.print(node.getName() +"\n");

    }

    //List<ExpandedComponentInstanceSymbol> exOrder = ImplementExecutionOrder.exOrder(symTab, inst);

}
