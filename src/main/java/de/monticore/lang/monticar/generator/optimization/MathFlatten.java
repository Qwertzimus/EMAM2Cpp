package de.monticore.lang.monticar.generator.optimization;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ComponentSymbol;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ConnectorSymbol;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ExpandedComponentInstanceSymbol;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarcmath._visitor.EmbeddedMontiArcMathVisitor;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarcmath.helper.SymbolPrinter;

import de.monticore.lang.math.math._ast.*;
import de.monticore.lang.math.math._symboltable.MathStatementsSymbol;

import de.monticore.lang.monticar.generator.helper.PortSymbolsPrinter;
import de.monticore.lang.monticar.generator.order.ImplementExecutionOrder;
import de.monticore.lang.monticar.helper.IndentPrinter;

import de.monticore.lang.tagging._symboltable.TaggingResolver;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Ferdinand Mehlan
 */
public class MathFlatten implements EmbeddedMontiArcMathVisitor {

    protected TaggingResolver symTab;
    private ExpandedComponentInstanceSymbol topSymbol;
    private ExpandedComponentInstanceSymbol currentSymbol;
    private IndentPrinter printer;
    private Map<String, String> mathVariables;
    private int variableCounter;
    private MathFlatten realThis;

    public MathFlatten(ExpandedComponentInstanceSymbol inst, TaggingResolver symTab) {
        topSymbol = inst;
        printer = new IndentPrinter();
        this.symTab = symTab;
        mathVariables = new HashMap<>();
        variableCounter = 1;
        realThis = this;
    }

    public static String printMathFlattended(ExpandedComponentInstanceSymbol inst, TaggingResolver symTab) {
        MathFlatten instance = new MathFlatten(inst, symTab);
        instance.handleComponent();
        return instance.printer.getContent();
    }

    private void handleComponent() {
        printer.print("component " + topSymbol.getName() + "{\n").indent();

        PortSymbolsPrinter.printPorts(topSymbol.getPorts(), printer);
        printer.println();
        handleConnectors();
        handleBehaviours();

        printer.unindent().print("}\n");
    }

    private void handleConnectors() {
        Collection<ConnectorSymbol> connectors = topSymbol.getConnectors();
        for(ConnectorSymbol c : connectors) {
            if(c.getTargetPort().isIncoming())
                mathVariables.put(c.getTarget(), c.getSource());
            else
                mathVariables.put(c.getSource(), c.getTarget());
        }
    }

    private void handleBehaviours() {
        printer.print("implementation Math{\n").indent();
        List<ExpandedComponentInstanceSymbol> exOrder = ImplementExecutionOrder.exOrder(symTab, topSymbol);
        exOrder.forEach(this::handleBehaviour);
        printer.unindent().print("}\n");
    }

    private void handleBehaviour(ExpandedComponentInstanceSymbol inst) {
        currentSymbol = inst;
        ComponentSymbol comp = inst.getComponentType().getReferencedSymbol();
        MathStatementsSymbol maths = comp.getSpannedScope().
                <MathStatementsSymbol>resolve("MathStatements", MathStatementsSymbol.KIND).orElse(null);
        if(maths!=null){
            ((ASTMathStatements)maths.getAstNode().get()).accept(realThis);
        }
    }

    private void handleVariable(String var) {
        var = currentSymbol.getName() + "." + var;
        if(mathVariables.containsKey(var)) {
            printer.print(getTransitiveVarName(var));
        } else {
            String variable = "h" + variableCounter;
            printer.print(variable);
            variableCounter++;
            mathVariables.put(var, variable);
        }
    }

    private String getTransitiveVarName(String var) {
        if(mathVariables.containsKey(var))
            return getTransitiveVarName(mathVariables.get(var));
        else
            return var;
    }


    /**
     * Visitor pattern on the MathExpression part begins here
     */
    @Override
    public void visit(ASTMathAssignmentExpression node) {
        if(node.nameIsPresent())
            handleVariable(node.getName().get());
        else if(node.mathMatrixNameExpressionIsPresent())
            handleVariable(node.getMathMatrixNameExpression().get().getName().get());

    }

    @Override
    public void endVisit(ASTMathAssignmentExpression node) {
        printer.print(";\n");
    }

    @Override
    public void visit(ASTMathAssignmentOperator node) {
        printer.print(" " + node.getOperator().get() + " ");
    }

    /**
     *  traverse used here, because the visit order is broken
     */
    @Override
    public void traverse(ASTMathArithmeticMultiplicationExpression node) {
        node.getMathArithmeticExpressions().get(0).accept(realThis);
        printer.print(" * ");
        node.getMathArithmeticExpressions().get(1).accept(realThis);
    }

    @Override
    public void visit(ASTMathNameExpression node) {
        handleVariable(node.getName());
    }


}
