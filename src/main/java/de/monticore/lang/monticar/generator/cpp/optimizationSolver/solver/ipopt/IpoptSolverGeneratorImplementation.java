package de.monticore.lang.monticar.generator.cpp.optimizationSolver.solver.ipopt;

import de.monticore.lang.monticar.generator.FileContent;
import de.monticore.lang.monticar.generator.cpp.BluePrintCPP;
import de.monticore.lang.monticar.generator.cpp.optimizationSolver.problem.NLPProblem;
import de.monticore.lang.monticar.generator.cpp.optimizationSolver.problem.Problem;
import de.monticore.lang.monticar.generator.cpp.optimizationSolver.solver.NLPSolverGeneratorImplementation;
import de.monticore.lang.monticar.generator.cpp.template.AllTemplates;
import de.monticore.lang.monticar.generator.cpp.template.TemplateHelper;
import de.se_rwth.commons.logging.Log;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Generates Ipopt C++ code to solve a given problem
 */
public class IpoptSolverGeneratorImplementation extends NLPSolverGeneratorImplementation {

    private static final Template CALL_IPOPT_HPP;
    private static final Template CALL_IPOPT_CPP;

    static {
        Configuration conf = new Configuration(Configuration.VERSION_2_3_23);
        conf.setDefaultEncoding("UTF-8");
        conf.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
        conf.setLogTemplateExceptions(false);
        conf.setClassForTemplateLoading(AllTemplates.class, "/template/optimizationSolver/ipopt/");
        conf.setNumberFormat("0.####E0");
        try {
            CALL_IPOPT_HPP = conf.getTemplate("CallIpoptTemplate_HPP.ftl");
            CALL_IPOPT_CPP = conf.getTemplate("CallIpoptTemplate_CPP.ftl");
        } catch (IOException e) {
            String msg = "could not load ipopt templates";
            Log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    // fields
    private List<String> necessaryIncludes = new ArrayList<>();

    // constructor
    public IpoptSolverGeneratorImplementation() {
        super();
    }

    public IpoptSolverGeneratorImplementation(NLPProblem problem) {
        super(problem);
    }

    @Override
    public String generateSolverCode(Problem optimizationProblem, List<FileContent> auxillaryFiles, BluePrintCPP bluePrint) {
        String result = "";
        if (optimizationProblem instanceof NLPProblem) {
            NLPProblem nlpOptimizationProblem = (NLPProblem) optimizationProblem;
            setNlpProblem(nlpOptimizationProblem);
            // create view model from problem class
            IpoptViewModel vm = new IpoptViewModel(nlpOptimizationProblem);
            // set execute command
            vm.setKnownVariablesFromBluePrint(bluePrint);
            String knownVariables = ", ";
            for (String s : vm.getKnownVariables()) {
                knownVariables += s + ", ";
            }
            if (knownVariables.length() >= 2) {
                knownVariables = knownVariables.substring(0, knownVariables.length() - 2);
            }
            result = String.format("%s::solveOptimizationProblemIpOpt(%s, %s%s);\n",vm.getCallIpoptName(), vm.getOptimizationVariableName(), vm.getObjectiveVariableName(), knownVariables);
            // generate templates by view model
            vm.resolveIpoptNameConflicts();
            generateIpoptTemplates(vm, auxillaryFiles);
            necessaryIncludes.add(vm.getCallIpoptName());
        } else {
            Log.error(String.format("Ipopt can not solve problemes of type %s", optimizationProblem.getClass().toString()));
        }
        return result;
    }

    // methods

    @Override
    public List<String> getNecessaryIncludes() {
        return necessaryIncludes;
    }

    protected void generateIpoptTemplates(IpoptViewModel viewModel, List<FileContent> auxillaryFiles) {

        Map<String, Object> dataForTemplate = TemplateHelper.getDataForTemplate(viewModel);

        try {
            StringWriter sw = new StringWriter();
            CALL_IPOPT_HPP.process(dataForTemplate, sw);
            auxillaryFiles.add(new FileContent(sw.toString(), "/" + viewModel.getCallIpoptName() + ".h"));

            sw = new StringWriter();
            CALL_IPOPT_CPP.process(dataForTemplate, sw);
            auxillaryFiles.add(new FileContent(sw.toString(), "/" + viewModel.getCallIpoptName() + ".cpp"));

        } catch (TemplateException | IOException e) {
            Log.error("Ipopt template generation failed. ", e);
        }
    }
}
