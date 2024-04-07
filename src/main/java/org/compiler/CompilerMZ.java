package org.compiler;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.compiler.nodes.*;
import org.compiler.token.Tokenizer;

/**
 * The main class of the compiler
 */
public class CompilerMZ {
    /**
     * The main method of the compiler
     * @param args eventual input and output files, default fausto.mz and output.asm
     */
    public static void main(String[] args) {
        System.out.println("MZ Compiler by Davide Mecugni, Andrea Zanasi\n");

        String fileIn;
        if (args.length < 1) {
            fileIn = "Risorse/fausto.mz";
        }
        else{
            fileIn = args[0];
        }
        String content;
        try {
            content = readFile(fileIn);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Tokenizer tokenizer = new Tokenizer(content);
        System.out.println("1) Tokenizzato!");
        Parser parser = new Parser(tokenizer.getTokens());

        Exit tree = parser.parse();
        if (tree == null) {
            throw new RuntimeException("No exit statement found");
        }
        System.out.println("2) Parserizzato!");
        Generator generator = new Generator(tree);
        String res = generator.generate();
        System.out.println("3) Generato ASM!");
        String fileOut = "Risorse/output.asm";
        // Se un file di output è stato specificato
        if(args.length == 2){
            fileOut = args[1];
        }
        try {
            writeFile(fileOut, res);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("4) File generato!");
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