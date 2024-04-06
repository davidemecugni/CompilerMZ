package org.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CompilerMZ {
    public static void main(String[] args) {
        if(args.length != 1){
            throw new IllegalArgumentException("Incorrect usage, provide a .mz file as an argument.");
        }
        String filePath = args[0];
        String content = null;
        try {
            content = readFile(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Tokenizer tokenizer = new Tokenizer(content);
        System.out.println(tokenizer.getTokens());
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
}