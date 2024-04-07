package org.compiler;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.compiler.nodes.*;
public class CompilerMZ {
    public static void main(String[] args) {
        if(args.length != 1){
            throw new IllegalArgumentException("Incorrect usage, provide a .mz file as an argument.");
        }
        String fileIn = args[0];
        String content = null;
        try {
            content = readFile(fileIn);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Tokenizer tokenizer = new Tokenizer(content);

        Parser parser = new Parser(tokenizer.getTokens());

        Exit tree = parser.parse();
        if(tree == null){
            throw new RuntimeException("No exit statement found");
        }
        Generator generator = new Generator(tree);
        String res = generator.generate();
        String fileOut = "output.asm";
        try {
            writeFile(fileOut, res);
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Define the commands to execute
        String nasmCommand = "nasm";
        String ldCommand = "ld";
        String outputFile = "output";

        // Create the commands as an array of strings
        String[] nasmArgs = {nasmCommand, "-f elf64", fileOut, "-o", "output.o"};
        String[] ldArgs = {ldCommand, "output.o", "-o", outputFile};
        String[] execArgs = {"./" + outputFile};

        try {
            // Execute nasm command
            Process execProcess = getProcess(nasmArgs, ldArgs, execArgs);
            execProcess.waitFor();

            System.out.println("Execution completed.");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Execute the commands
     * @param nasmArgs the arguments for the nasm command
     * @param ldArgs the arguments for the ld command
     * @param execArgs the arguments for the executable
     * @return Process
     * @throws IOException if an I/O error occurs
     */
    private static Process getProcess(String[] nasmArgs, String[] ldArgs, String[] execArgs) throws IOException, InterruptedException {
        ProcessBuilder nasmProcessBuilder = new ProcessBuilder(nasmArgs);
        Process nasmProcess = nasmProcessBuilder.start();
        nasmProcess.waitFor();

        // Execute ld command
        ProcessBuilder ldProcessBuilder = new ProcessBuilder(ldArgs);
        Process ldProcess = ldProcessBuilder.start();
        // ldProcess.waitFor();

        // Execute output
        // ProcessBuilder execProcessBuilder = new ProcessBuilder(execArgs);
        return ldProcess;
    }



    /**
     * Read a file and return its content as a string
     * @param filePath the path of the file to read
     * @return String
     * @throws IOException if an I/O error occurs reading from the file or a malformed or unmappable byte sequence is read
     */
    public static String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    /**
     * Write content to a file
     * @param filePath the path of the file to write to
     * @param content the content to write to the file
     * @throws IOException if an I/O error occurs writing to the file or the file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any other reason
     */
    public static void writeFile(String filePath, String content) throws IOException {
        FileWriter writer = new FileWriter(filePath);
        writer.write(content);
        writer.close();
    }
}