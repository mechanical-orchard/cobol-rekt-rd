package org.smojol.analysis.visualisation;

import org.smojol.analysis.LanguageDialect;
import org.smojol.flowchart.FlowchartTasks;
import org.smojol.interpreter.FlowchartGenerationStrategy;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "flowchart", mixinStandardHelpOptions = true, version = "flowchart 0.1",
        description = "Builds the flowcharts")
public class FlowchartCommand implements Callable<Integer> {

    @Option(names = {"d", "--dialectJarPath"},
            defaultValue = "che-che4z-lsp-for-cobol-integration/server/dialect-idms/target/dialect-idms.jar",
            description = "Path to dialect .JAR")
    private String dialectJarPath;

    @Option(names = {"-s", "--srcDir"},
            required = true,
            description = ".cbl source directory")
    private String sourceDir;

    @CommandLine.Parameters(index = "0..*",
            description = "Program names")
    private List<String> programNames;

    // Can be replaced with a File[] (and the later conversion removed) if we skip default arguments.
    @Option(names = {"-c", "--copyBooksDir"},
            required = true,
            description = ".cpy source directory (repeatable)")
    private String[] copyBookDirs;

    @Option(names = {"-r", "--reportDir"},
            required = true,
            description = "Output report directory")
    private String reportRootDir;

    @Option(names = {"-x", "--dialect"},
            required = false,
            defaultValue = "COBOL",
            description = "The COBOL dialect")
    private String dialect;

    @Option(names = {"-g", "--generation"},
            required = false,
            defaultValue = "PROGRAM",
            description = "The flowchart generation strategy")
    private String flowchartGenerationStrategy;

    @Override
    public Integer call() throws IOException, InterruptedException {
        // Convert the String[] to File[]. See above.
        File[] copyBookPaths = Arrays.stream(copyBookDirs).map(File::new).toArray(File[]::new);
        new FlowchartTasks(sourceDir, reportRootDir, copyBookPaths, dialectJarPath).generateForPrograms(programNames, FlowchartGenerationStrategy.strategy(flowchartGenerationStrategy), LanguageDialect.dialect(dialect));
        return 0;
    }
}
