package org.smojol.cli;

import com.google.common.collect.ImmutableList;
import org.smojol.analysis.LanguageDialect;
import org.smojol.flowchart.GraphDBTasks;
import org.smojol.interpreter.FlowchartGenerationStrategy;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import static org.smojol.flowchart.GraphTask.*;

@Command(name = "graph", mixinStandardHelpOptions = true, version = "graph 0.1",
        description = "Injects the AST into a GraphDB")
public class GraphDBCommand implements Callable<Integer> {

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
            description = "Copybook directories (repeatable)")
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
            description = "The flowchart generation strategy. Valid values are SECTION, PROGRAM, and NODRAW")
    private String flowchartGenerationStrategy;

    @Override
    public Integer call() throws IOException {
        // Convert the String[] to File[]. See above.
        File[] copyBookPaths = Arrays.stream(copyBookDirs).map(File::new).toArray(File[]::new);
        new GraphDBTasks(sourceDir, reportRootDir, copyBookPaths, dialectJarPath, LanguageDialect.dialect(dialect), FlowchartGenerationStrategy.strategy(flowchartGenerationStrategy)).generateForPrograms(programNames, ImmutableList.of(INJECT_INTO_NEO4J, EXPORT_TO_GRAPHML, WRITE_RAW_AST, DRAW_FLOWCHART));
        return 0;
    }
}
