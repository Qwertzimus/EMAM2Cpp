package de.monticore.lang.monticar.generator.cpp;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ExpandedComponentInstanceSymbol;
import de.monticore.lang.monticar.generator.cpp.resolver.Resolver;
import de.monticore.lang.monticar.generator.order.simulator.AbstractSymtab;
import de.monticore.lang.tagging._symboltable.TaggingResolver;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Usage example:
 * java -cp emam2cpp.jar \
 * de.monticore.lang.monticar.generator.cpp.GeneratorCppCli \
 * --models-dir=C:\Users\vpupkin\proj\src\emam \
 * --root-model=de.rwth.vpupkin.modeling.autopilot.autopilot \
 * --output-dir=C:\Users\vpupkin\proj\target\cpp-gen\autopilot \
 * --flag-generate-tests \
 * --flag-use-armadillo-backend
 * --check-model-dir
 */
public final class GeneratorCppCli {

    public static final Option OPTION_MODELS_PATH = Option.builder("m")
            .longOpt("models-dir")
            .desc("full path to directory with EMAM models e.g. C:\\Users\\vpupkin\\proj\\MyAwesomeAutopilot\\src\\main\\emam")
            .hasArg(true)
            .required(true)
            .build();

    public static final Option OPTION_ROOT_MODEL = Option.builder("r")
            .longOpt("root-model")
            .desc("fully qualified name of the root model e.g. de.rwth.vpupkin.modeling.mySuperAwesomeAutopilotComponent")
            .hasArg(true)
            .required(true)
            .build();

    public static final Option OPTION_OUTPUT_PATH = Option.builder("o")
            .longOpt("output-dir")
            .desc("full path to output directory for tests e.g. C:\\Users\\vpupkin\\proj\\MyAwesomeAutopilot\\target\\gen-cpp")
            .hasArg(true)
            .required(true)
            .build();

    public static final Option OPTION_FLAG_TESTS = Option.builder("t")
            .longOpt("flag-generate-tests")
            .desc("optional flag indicating if tests generation is needed")
            .hasArg(false)
            .required(false)
            .build();

    public static final Option OPTION_FLAG_ARMADILLO = Option.builder("a")
            .longOpt("flag-use-armadillo-backend")
            .desc("optional flag indicating if Armadillo library should be used as backend")
            .hasArg(false)
            .required(false)
            .build();

    public static final Option OPTION_FLAG_ALGEBRAIC = Option.builder("a")
            .longOpt("flag-use-algebraic")
            .desc("optional flag indicating if algebraic optimizations should be on")
            .hasArg(false)
            .required(false)
            .build();


    public static final Option OPTION_FLAG_THREADING = Option.builder("a")
            .longOpt("flag-use-threading")
            .desc("optional flag indicating if threading optimizations should be on")
            .hasArg(false)
            .required(false)
            .build();


    public static final Option OPTION_FLAG_AUTOPILOT_ADAPTER = Option.builder()
            .longOpt("flag-generate-autopilot-adapter")
            .desc("optional flag indicating if autopilot adapter should be generated")
            .hasArg(false)
            .required(false)
            .build();

    public static final Option OPTION_FLAG_CHECK_MODEL_DIR = Option.builder()
            .longOpt("check-model-dir")
            .desc("optional flag indicating if model dir should be checked for creation of component and stream list")
            .hasArg(false)
            .required(false)
            .build();

    public static final Option OPTION_FLAG_SERVER_WRAPPER = Option.builder()
            .longOpt("flag-generate-server-wrapper")
            .desc("optional flag indicating if model should be wrapped into a server")
            .hasArg(false)
            .required(false)
            .build();

    private GeneratorCppCli() {
    }

    public static void main(String[] args) {
        Options options = getOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine cliArgs = parseArgs(options, parser, args);
        if (cliArgs != null) {
            runGenerator(cliArgs);
        }
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption(OPTION_MODELS_PATH);
        options.addOption(OPTION_ROOT_MODEL);
        options.addOption(OPTION_OUTPUT_PATH);
        options.addOption(OPTION_FLAG_TESTS);
        options.addOption(OPTION_FLAG_ARMADILLO);
        options.addOption(OPTION_FLAG_AUTOPILOT_ADAPTER);
        options.addOption(OPTION_FLAG_CHECK_MODEL_DIR);
        options.addOption(OPTION_FLAG_SERVER_WRAPPER);
        options.addOption(OPTION_FLAG_ALGEBRAIC);
        options.addOption(OPTION_FLAG_THREADING);
        return options;
    }

    private static CommandLine parseArgs(Options options, CommandLineParser parser, String[] args) {
        CommandLine cliArgs;
        try {
            cliArgs = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("argument parsing exception: " + e.getMessage());
            System.exit(1);
            return null;
        }
        return cliArgs;
    }

    private static void runGenerator(CommandLine cliArgs) {
        Path modelsDirPath = Paths.get(cliArgs.getOptionValue(OPTION_MODELS_PATH.getOpt()));
        String rootModelName = cliArgs.getOptionValue(OPTION_ROOT_MODEL.getOpt());
        String outputPath = cliArgs.getOptionValue(OPTION_OUTPUT_PATH.getOpt());
        TaggingResolver symTab = AbstractSymtab.createSymTabAndTaggingResolver(modelsDirPath.toString());
        Resolver resolver = new Resolver(symTab);
        ExpandedComponentInstanceSymbol componentSymbol = resolveSymbol(resolver, rootModelName);

        GeneratorCPP g = new GeneratorCPP();
        g.setUseAlgebraicOptimizations(false);
        g.setUseThreadingOptimization(false);
        g.setModelsDirPath(modelsDirPath);
        g.setGenerationTargetPath(outputPath);
        g.setGenerateTests(cliArgs.hasOption(OPTION_FLAG_TESTS.getOpt()));
        if (cliArgs.hasOption(OPTION_FLAG_ARMADILLO.getOpt())) {
            g.useArmadilloBackend();
        }
        g.setCheckModelDir(cliArgs.hasOption(OPTION_FLAG_CHECK_MODEL_DIR.getLongOpt()));
        g.setGenerateServerWrapper(cliArgs.hasOption(OPTION_FLAG_SERVER_WRAPPER.getLongOpt()));
        g.setGenerateAutopilotAdapter(cliArgs.hasOption(OPTION_FLAG_AUTOPILOT_ADAPTER.getLongOpt()));

        g.setUseAlgebraicOptimizations(cliArgs.hasOption(OPTION_FLAG_ALGEBRAIC.getLongOpt()));
        g.setUseThreadingOptimization(cliArgs.hasOption(OPTION_FLAG_THREADING.getLongOpt()));
        try {
            if (componentSymbol != null) {
                g.generateFiles(componentSymbol, symTab);
            } else {
                g.saveFilesToDisk(g.handleTestAndCheckDir(symTab));
            }
        } catch (IOException e) {
            Log.error("error during generation", e);
            System.exit(1);
        }

    }

    private static ExpandedComponentInstanceSymbol resolveSymbol(Resolver resolver, String rootModelName) {
        ExpandedComponentInstanceSymbol componentSymbol = resolver.getExpandedComponentInstanceSymbol(rootModelName).orElse(null);
        if (componentSymbol == null) {
            Log.error("could not resolve component " + rootModelName);
            //System.exit(1);
            return null;
        }
        return componentSymbol;
    }
}
