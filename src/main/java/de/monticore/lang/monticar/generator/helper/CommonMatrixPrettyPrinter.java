package de.monticore.lang.monticar.generator.helper;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.PortSymbol;
import de.monticore.lang.monticar.common2._ast.ASTCommonDimension;
import de.monticore.lang.monticar.common2._ast.ASTCommonDimensionElement;
import de.monticore.lang.monticar.common2._ast.ASTCommonMatrixType;
import de.monticore.lang.monticar.common2._visitor.Common2Visitor;
import de.monticore.lang.monticar.helper.IndentPrinter;
import de.monticore.lang.monticar.types2._ast.ASTElementType;
import de.monticore.lang.numberunit._ast.ASTUnitNumber;

/**
 * Created by kt on 07.05.2018.
 */
public class CommonMatrixPrettyPrinter implements Common2Visitor {

    protected CommonMatrixPrettyPrinter realThis;
    protected IndentPrinter ip;

    public CommonMatrixPrettyPrinter(IndentPrinter ip) {
        this.ip = ip;
        this.realThis = this;
    }

    public static void prettyprint(ASTCommonMatrixType node, IndentPrinter ip) {
        CommonMatrixPrettyPrinter printer = new CommonMatrixPrettyPrinter(ip);
        node.accept(printer);
    }

    @Override
    public void visit(ASTElementType node) {
        ip.print(node.getTElementType().get());
    }

    @Override
    public void traverse(ASTCommonDimension node) {
        ip.print("^{");

        int i = 0;
        int s = node.getCommonDimensionElements().size();
        for (ASTCommonDimensionElement e  : node.getCommonDimensionElements()) {
            e.accept(realThis);
            if (i != s - 1) {
                ip.print(", ");
            }
            i++;
        }

        ip.print("}");
    }

    @Override
    public void traverse(ASTCommonDimensionElement node) {
        if(node.nameIsPresent()) {
            ip.print(node.getName().get());
        }
        if(node.unitNumberIsPresent()) {
            node.getUnitNumber().get().accept(realThis);
        }
    }

    @Override
    public void traverse(ASTUnitNumber node) {
        if(node.tUnitNumberIsPresent()) {
            ip.print(node.getTUnitNumber().get());
        }
    }
}
