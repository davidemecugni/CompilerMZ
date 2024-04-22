package org.compiler;

import org.apache.commons.cli.*;
import org.compiler.nodes.NodeProgram;
import org.compiler.token.Tokenizer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The main class of the compiler
 */
public class CompilerMZ {
    /**
     * The main method of the compiler
     *
     * @param args
     *            eventual input and output files, default fausto.mz and output.asm
     */
    public static void main(String[] args) throws IOException, ParseException {
        CommandLine cmd = getCmd(args);
        long start = 0;
        if (cmd.hasOption("t")) {
            start = System.currentTimeMillis();
        }
        if (cmd.hasOption("version")) {
            System.out.println(
                    "0.4.0-Beta Dialects and Operations\nMZ Compiler by Davide Mecugni, Andrea Zanasi\n(C) 2024");
            return;
        }
        if (cmd.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            System.out.println("MZ Compiler by Davide Mecugni, Andrea Zanasi\n(C) 2024\n");
            formatter.printHelp("CompilerMZ", getOptions());
            return;
        }
        String dialect = cmd.getOptionValue("d", "default_dialect");
        String fileIn = getCmdFileOption(cmd, "i", "", ".mz");
        String fileOut = getCmdFileOption(cmd, "o", removeExtension(fileIn, ".mz"), ".asm");
        String fileObj = getCmdFileOption(cmd, "O", removeExtension(fileOut, ".asm"), ".o");
        String fileExe = getCmdFileOption(cmd, "e", removeExtension(fileObj, ".o"), "");
        if (!cmd.hasOption("v") && !cmd.hasOption("c")) {
            callFullStack(fileIn, fileOut, fileObj, fileExe, dialect);
            if (cmd.hasOption("t")) {
                long end = System.currentTimeMillis();
                System.out.println("Time: " + (end - start) + "ms");
            }
            return;
        }
        if (!cmd.hasOption("v") && cmd.hasOption("c")) {
            makeAssembly(fileIn, fileOut, dialect);
            if (cmd.hasOption("t")) {
                long end = System.currentTimeMillis();
                System.out.println("Time: " + (end - start) + "ms");
            }
            return;
        }

        // Reading file
        String content;
        content = readFile(fileIn);

        // Tokenizing
        Tokenizer tokenizer = new Tokenizer(content, dialect);
        System.out.println("1) Tokenized!");
        // for debugging
        System.out.println(tokenizer.getTokens());

        // Parsing
        Parser parser = new Parser(tokenizer.getTokens());
        NodeProgram tree = parser.getTree();
        System.out.println("2) Parsed!");

        // Generating
        Generator generator = new Generator(tree);
        String res = generator.getGenerated();
        System.out.println("3) Generated assembly!");

        // for debugging
        // generator.printStmt();

        // Writing file
        writeFile(fileOut, res);
        System.out.println("4) Generated file!");
        if (cmd.hasOption("c")) {
            if (cmd.hasOption("t")) {
                long end = System.currentTimeMillis();
                System.out.println("Time: " + (end - start) + "ms");
            }
            return;
        }
        // Assembling
        callAssembler(fileOut, fileObj);
        System.out.println("5) NASM: assembled file!");

        // Linking
        callLinker(fileObj, fileExe);
        System.out.println("6) ld: linked file!");

        // Executing
        int returnCode = callExecutable(fileExe);
        System.out.println("7) Executed file!");
        System.out.println("In:  " + fileIn + " \n-->> " + fileOut + "\n-->> " + fileObj + "\n-->> " + fileExe);
        System.out.println("Return code of exe: " + returnCode);

        if (cmd.hasOption("t")) {
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    /**
     * Goes from .asm file to executable file in 5 steps: tokenize parse generate assemble link
     *
     * @param fileIn
     *            .mz file
     * @param fileASMOut
     *            .asm file used as intermediate step, uses same name for .o object, without extension for exe file
     *
     * @throws IOException
     *             on any error through the whole process
     */
    public static void makeAssembly(String fileIn, String fileASMOut, String dialect) throws IOException {
        String content = readFile(fileIn);
        Tokenizer tokenizer = new Tokenizer(content, dialect);
        Parser parser = new Parser(tokenizer.getTokens());
        Generator generator = new Generator(parser.getTree());
        String outputASM = generator.getGenerated();
        writeFile(fileASMOut, outputASM);
    }

    /**
     * Goes from .mz file to exe file
     *
     * @param fileIn
     *            .mz file
     * @param fileOut
     *            .asm assembly file where compiled .mz goes
     * @param fileObj
     *            .o assembled object file made from .asm
     * @param fileExe
     *            executable final file
     *
     * @throws IOException
     *             On any problem related to IO on files
     */
    public static void callFullStack(String fileIn, String fileOut, String fileObj, String fileExe, String dialect)
            throws IOException {
        makeAssembly(fileIn, fileOut, dialect);
        callAssembler(fileOut, fileObj);
        callLinker(fileObj, fileExe);
        callExecutable(fileExe);
    }

    public static void callFullStack(String fileIn, String fileOut, String fileObj, String fileExe) throws IOException {
        makeAssembly(fileIn, fileOut, "default_dialect");
        callAssembler(fileOut, fileObj);
        callLinker(fileObj, fileExe);
        callExecutable(fileExe);
    }

    /**
     * Goes from .mz file to exe file
     *
     * @param fileIn
     *            .mz file
     * @param fileOut
     *            .asm assembly file where compiled .mz goes
     * @param fileObj
     *            .o assembled object file made from .asm
     * @param fileExe
     *            executable final file
     *
     * @return int Return code of the program
     *
     * @throws IOException
     *             On any problem related to IO on files
     */
    public static int callFullStackWithReturnCode(String fileIn, String fileOut, String fileObj, String fileExe)
            throws IOException {
        makeAssembly(fileIn, fileOut, "default_dialect");
        callAssembler(fileOut, fileObj);
        callLinker(fileObj, fileExe);
        return callExecutable(fileExe);
    }

    /**
     * Reads file as a string
     *
     * @param filePath
     *            file to be read
     *
     * @return String content of file
     *
     * @throws IOException
     *             on problems while reading
     */
    private static String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    /**
     * Writes string to file
     *
     * @param filePath
     *            path of file
     * @param content
     *            string to be written
     *
     * @throws IOException
     *             On problems while writing
     */
    private static void writeFile(String filePath, String content) throws IOException {
        FileWriter writer = new FileWriter(filePath);
        writer.write(content);
        writer.close();
    }

    /**
     * Calls NASM assembler on 64 bit assembly
     *
     * @param fileOut
     *            .asm file
     * @param fileObj
     *            .o compiled file
     *
     * @throws FileNotFoundException
     *             is .asm file is not present
     */
    private static void callAssembler(String fileOut, String fileObj) throws FileNotFoundException {
        File asmFile = new File(fileOut);

        if (!asmFile.exists() || !asmFile.isFile()) {
            throw new FileNotFoundException("Invalid file path: " + fileOut);
        }

        ProcessBuilder nasmProcessBuilder = new ProcessBuilder("nasm", "-f", "elf64", "-o", fileObj, fileOut);
        runProcess(nasmProcessBuilder, "NASM assembler for elf x86_64 architecture");
    }

    /**
     * Calls ld default linux linker
     *
     * @param fileObj
     *            .o object file generated by NASM
     * @param fileExe
     *            executable file
     *
     * @throws FileNotFoundException
     *             if input file is not present
     */
    private static void callLinker(String fileObj, String fileExe) throws FileNotFoundException {
        File objectFile = new File(fileObj);

        if (!objectFile.exists() || !objectFile.isFile()) {
            throw new FileNotFoundException("Invalid file path: " + fileObj);
        }

        ProcessBuilder ldProcessBuilder = new ProcessBuilder("ld", "-o", fileExe, fileObj);
        runProcess(ldProcessBuilder, "ld linker");
    }

    /**
     * Calls the final executable output
     *
     * @param filePath
     *            file path of exe file
     *
     * @return exit code of the function
     *
     * @throws FileNotFoundException
     *             on file not found
     */
    private static int callExecutable(String filePath) throws FileNotFoundException {
        File executableFile = new File(filePath);

        if (!executableFile.exists() || !executableFile.isFile()) {
            throw new FileNotFoundException("Invalid file path: " + filePath);
        }

        String command = executableFile.getAbsolutePath();
        ProcessBuilder execProcessBuilder = new ProcessBuilder(command);
        return runCustomProcess(execProcessBuilder, "Executable file", true);
    }

    /**
     * Runs code, throws error on exit code != 0
     *
     * @param process
     *            process to be run
     * @param description
     *            description to be shown on error
     */
    private static void runProcess(ProcessBuilder process, String description) {
        runCustomProcess(process, description, false);
    }

    /**
     * Run process
     *
     * @param process
     *            process to run
     * @param description
     *            text to be shown on error
     * @param customReturn
     *            if false throws error if return code != 0
     *
     * @return return code of process
     */
    private static int runCustomProcess(ProcessBuilder process, String description, boolean customReturn) {
        try {
            process.redirectErrorStream(true);
            Process runningProcess = process.start();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(runningProcess.getInputStream()));
            String line;
            int exitCode = runningProcess.waitFor();
            while ((line = errorReader.readLine()) != null) {
                System.out.println(line);
            }
            if (!customReturn && exitCode != 0) {
                throw new RuntimeException("Error: " + description + " failed.");
            }
            return exitCode;
        } catch (IOException | InterruptedException e) {
            System.out.println("Error running process" + description + ": " + e.getMessage());
            throw new RuntimeException("Process error: " + description);
        }
    }

    /**
     * Adds an extension if not present
     *
     * @param fileIn
     *            String path of input file
     * @param ext
     *            Correct extension
     *
     * @return Path with extension
     */
    private static String correctExtension(String fileIn, String ext) {
        if (fileIn.endsWith(ext)) {
            return fileIn;
        }
        return fileIn + ext;
    }

    /**
     * Removes an extension if present
     *
     * @param fileIn
     *            String path of input file
     * @param ext
     *            Correct extension
     *
     * @return Path without extension
     */
    private static String removeExtension(String fileIn, String ext) {
        if (fileIn.endsWith(ext)) {
            return fileIn.substring(0, fileIn.length() - ext.length());
        }
        return fileIn;
    }

    /**
     * Generates a Command line parser with options
     *
     * @param args
     *            Default main options
     *
     * @return a CommandLine object to be used for checking flags and parameters
     *
     * @throws ParseException
     *             If the arguments are not correct
     */
    private static CommandLine getCmd(String[] args) throws ParseException {
        Options options = getOptions();
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    /**
     * Gets a command line option for file
     *
     * @param cmd
     *            CommandLine object to work with
     * @param option
     *            Flag to be checked
     * @param def
     *            Default path option
     * @param ext
     *            Correct extension to be used for file
     *
     * @return Returns the option chosen with correct extension
     */
    private static String getCmdFileOption(CommandLine cmd, String option, String def, String ext) {
        String s = cmd.getOptionValue(option, def);
        File f = new File(s);
        if (!f.isAbsolute()) {
            s = f.getAbsolutePath();
        }
        s = correctExtension(s, ext);
        return s;
    }

    /**
     * All CLI flag options
     *
     * @return an option object
     */
    private static Options getOptions() {
        Options options = new Options();
        options.addRequiredOption("i", "input", true, "input .mz manz file");
        options.addOption("o", "output", true, "output .asm assembly file");
        options.addOption("O", "object", true, ".o object file(assembled .asm file)");
        options.addOption("e", "executable", true, "final executable file");
        options.addOption("d", "dialect", true, "dialect to be used");
        options.addOption("v", "verbose", false, "verbose output");
        options.addOption("c", "compile", false, "compile only, no assembly and linking");
        options.addOption("t", "time", false, "print time for given procedure");
        options.addOption("V", "version", false, "print version");
        options.addOption("h", "help", false, "print this message");
        // Conflict handling could be implemented
        return options;
    }
}