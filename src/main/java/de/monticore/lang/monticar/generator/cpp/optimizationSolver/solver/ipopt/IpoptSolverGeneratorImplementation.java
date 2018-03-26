package de.monticore.lang.monticar.generator.cpp.optimizationSolver.solver.ipopt;

import de.monticore.lang.monticar.generator.FileContent;
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
    private static final Template TEMPLATE_NLP_HPP;
    private static final Template TEMPLATE_NLP_CPP;

    static {
        Configuration conf = new Configuration(Configuration.VERSION_2_3_23);
        conf.setDefaultEncoding("UTF-8");
        conf.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
        conf.setLogTemplateExceptions(false);
        conf.setClassForTemplateLoading(AllTemplates.class, "/template/optimizationSolver/ipopt/");
        try {
            CALL_IPOPT_HPP = conf.getTemplate("CallIpoptTemplate_HPP.ftl");
            CALL_IPOPT_CPP = conf.getTemplate("CallIpoptTemplate_CPP.ftl");
            TEMPLATE_NLP_HPP = conf.getTemplate("TemplateNLP_HPP.ftl");
            TEMPLATE_NLP_CPP = conf.getTemplate("TemplateNLP_CPP.ftl");
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
    public String generateSolverCode(Problem optimizationProblem, List<FileContent> auxillaryFiles) {
        String result = "";
        if (optimizationProblem instanceof NLPProblem) {
            NLPProblem nlpOptimizationProblem = (NLPProblem) optimizationProblem;
            setNlpProblem(nlpOptimizationProblem);
            // create view model from problem class
            IpoptViewModel vm = new IpoptViewModel(nlpOptimizationProblem);
            // generate templates by view model
            generateIpoptTemplates(vm, auxillaryFiles);
            necessaryIncludes.add(vm.getCallIpoptName());
            // set execute command
            result = String.format("solveOptimizationProblemIpOpt(%s, %s)", vm.getOptimizationVariableName(), vm.getObjectiveVariableName());

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

            sw = new StringWriter();
            TEMPLATE_NLP_HPP.process(dataForTemplate, sw);
            auxillaryFiles.add(new FileContent(sw.toString(), "/" + viewModel.getNlpClassName() + ".hpp"));

            sw = new StringWriter();
            TEMPLATE_NLP_CPP.process(dataForTemplate, sw);
            auxillaryFiles.add(new FileContent(sw.toString(), "/" + viewModel.getNlpClassName() + ".cpp"));

        } catch (TemplateException | IOException e) {
            Log.error("Ipopt template generation failed. ", e);
        }
    }
}
