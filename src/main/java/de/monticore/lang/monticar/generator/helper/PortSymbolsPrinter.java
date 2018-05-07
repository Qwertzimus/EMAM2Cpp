package de.monticore.lang.monticar.generator.helper;

import de.monticore.expressions.prettyprint.JavaClassExpressionsPrettyPrinter;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.PortSymbol;
import de.monticore.lang.monticar.common2._ast.ASTCommonMatrixType;
import de.monticore.lang.monticar.helper.IndentPrinter;
import de.monticore.lang.monticar.ts.references.MCASTTypeSymbolReference;
import de.monticore.lang.monticar.ts.references.MCTypeReference;
import de.monticore.symboltable.types.references.ActualTypeArgument;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Ferdinand Mehlan on 07.05.2018.
 */
public class PortSymbolsPrinter {

    public static void printPorts(Collection<PortSymbol> ports, IndentPrinter ip) {
        if (!ports.isEmpty()) {
            ip.println("ports");
            ip.indent();
            int i = 0;
            int s = ports.size();
            for (PortSymbol p : ports) {
                printPort(p, ip);
                if (i == s - 1) {
                    ip.println(";");
                } else {
                    ip.println(",");
                }
                i++;
            }
            ip.unindent();
        }
    }

    private static void printPort(PortSymbol port, IndentPrinter ip) {
        if (port.isIncoming()) {
            ip.print("in ");
        } else {
            ip.print("out ");
        }
        String type = port.getTypeReference().getName();

        if(type.equals("CommonMatrixType")) {
            ASTCommonMatrixType astType = (ASTCommonMatrixType)((MCASTTypeSymbolReference)port.getTypeReference()).getAstType();
            CommonMatrixPrettyPrinter.prettyprint(astType, ip);
        } else {
            ip.print(type);
            ip.print(printTypeParameters(port.getTypeReference().getActualTypeArguments()));
        }

        ip.print(" ");
        ip.print(port.getName());
    }

    private static String printTypeParameters(List<ActualTypeArgument> arg) {
        if (arg == null || arg.isEmpty())
            return "";
        return "<" + arg.stream().map(a -> printWildCardPrefix(a) + printTypeParameters(a) + printArrayDimensions(a)).
                collect(Collectors.joining(",")) + ">";
    }

    private static String printTypeParameters(ActualTypeArgument arg) {
        String ret = arg.getType().getReferencedSymbol().getFullName();
        if (arg.getType().getActualTypeArguments() != null && !arg.getType().getActualTypeArguments().isEmpty()) {
            ret += "<" + arg.getType().getActualTypeArguments().stream().
                    map(a -> printWildCardPrefix(a) + printTypeParameters(a) + printArrayDimensions(a)).collect(Collectors.joining(",")) + ">";
        }
        return ret;
    }

    private static String printWildCardPrefix(ActualTypeArgument a) {
        if (a.isLowerBound()) {
            return "? super ";
        } else if (a.isUpperBound()) {
            return "? extends ";
        }
        return "";
    }

    private static String printArrayDimensions(ActualTypeArgument a) {
        if (a.getType() instanceof MCTypeReference) {
            int dim = ((MCTypeReference) a.getType()).getDimension();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < dim; i++) {
                sb.append("[]");
            }
            return sb.toString();
        }
        return "";
    }
}
