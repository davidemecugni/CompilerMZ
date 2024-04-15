package org.compiler;

import org.compiler.nodes.NodeProgram;
import org.compiler.token.Tokenizer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

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
    public static void main(String[] args) throws IOException {
        System.out.println("MZ Compiler by Davide Mecugni, Andrea Zanasi\n");


        // USE: CommandLine line = parser.parse(options, args);
        //Setting file in
        String fileIn;
        if (args.length < 1) {
            fileIn = "../../Risorse/fausto.mz";
        } else {
            fileIn = args[0];
        }
        File in = new File(fileIn);
        fileIn = in.getAbsolutePath();
        fileIn = correctExtension(fileIn, "mz");

        //Setting file out
        String fileOut = "../../Risorse/fausto.asm";
        if (args.length == 2) {
            fileOut = args[1];
        }
        File out = new File(fileOut);
        fileOut = out.getAbsolutePath();
        fileOut = correctExtension(fileOut, "asm");

        if( ! Arrays.asList(args).contains("--verbose") || ! Arrays.asList(args).contains("-v")) {
            callFullStack(fileIn, fileOut);
            return;
        }
        // Reading file
        String content;
        content = readFile(correctExtension(fileIn, "mz"));

        // Tokenizing
        Tokenizer tokenizer = new Tokenizer(content);
        System.out.println("1) Tokenizzato!");
        // for debugging
        // System.out.println(tokenizer.getTokens());

        // Parsing
        Parser parser = new Parser(tokenizer.getTokens());
        NodeProgram tree = parser.getTree();
        System.out.println("2) Parserizzato!");

        // Generating
        Generator generator = new Generator(tree);
        String res = generator.getGenerated();
        System.out.println("3) Generato ASM!");

        // for debugging
        // generator.printStmt();

        // Writing file
        writeFile(fileOut, res);
        System.out.println("4) File generato!");

        // Assembling
        callAssembler(fileOut);
        System.out.println("5) NASM: File assmeblato");

        // Linking
        String objectFilePath = fileOut.replace(".asm", ".o");
        callLinker(objectFilePath);
        System.out.println("6) ld: File linkato");

        // Executing
        String execFilePath = fileOut.replace(".asm", "");
        int returnCode = callExecutable(execFilePath);
        System.out.println("7) File eseguito!");
        System.out.println(fileIn + " -->>  " + fileOut);
        System.out.println("Return code: " + returnCode);
    }

    /**
     * Goes from .asm file to executable file in 5 steps: tokenize parse generate assemble link
     *
     * @param fileIn
     *            .mz file
     * @param fileASMOut
     *            .asm file used as intermediate step, uses same name for .o object, without extension for exe file
     *
     * @return exit code of executable file
     *
     * @throws IOException
     *             on any error through the whole process
     */
    public static int callFullStack(String fileIn, String fileASMOut) throws IOException {
        fileIn = correctExtension(fileIn, "mz");
        fileASMOut = correctExtension(fileASMOut, "asm");

        String content = readFile(fileIn);
        Tokenizer tokenizer = new Tokenizer(content);
        Parser parser = new Parser(tokenizer.getTokens());
        Generator generator = new Generator(parser.getTree());
        String outputASM = generator.getGenerated();
        writeFile(fileASMOut, outputASM);
        callAssembler(fileASMOut);
        String objectFilePath = fileASMOut.replace(".asm", ".o");
        callLinker(objectFilePath);
        String execFilePath = fileASMOut.replace(".asm", "");
        return callExecutable(execFilePath);
    }

    /**
     * Reads file as a string
     *
     * @param filePath
     *            file to be read
     *
     * @return string content of file
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
     * Calls external NASM assembler
     *
     * @param filePath
     *            .asm file to be assembled
     *
     * @throws FileNotFoundException
     *             on .asm file not found
     */
    private static void callAssembler(String filePath) throws FileNotFoundException {
        File asmFile = new File(filePath);

        if (!asmFile.exists() || !asmFile.isFile()) {
            throw new FileNotFoundException("Invalid file path: " + filePath);
        }

        String objFilePath = filePath.replace(".asm", ".o");
        ProcessBuilder nasmProcessBuilder = new ProcessBuilder("nasm", "-f", "elf64", "-o", objFilePath, filePath);
        runProcess(nasmProcessBuilder, "NASM assembler for elf x86_64 architecture");
    }

    /**
     * Calls the ld linker
     *
     * @param objectFilePath
     *            .o file to be linked
     *
     * @throws FileNotFoundException
     *             on object file not found
     */
    private static void callLinker(String objectFilePath) throws FileNotFoundException {
        File objectFile = new File(objectFilePath);

        if (!objectFile.exists() || !objectFile.isFile()) {
            throw new FileNotFoundException("Invalid file path: " + objectFilePath);
        }

        String execFilePath = objectFilePath.replace(".o", "");
        ProcessBuilder ldProcessBuilder = new ProcessBuilder("ld", "-o", execFilePath, objectFilePath);
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

    private static String correctExtension(String fileIn, String ext) {
        if (fileIn.endsWith(ext)) {
            return fileIn;
        }
        return fileIn + "." + ext;
    }
}