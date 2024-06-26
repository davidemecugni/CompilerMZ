package org.compiler;

import org.compiler.errors.TokenError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCompilerMZ {

    @ParameterizedTest
    @MethodSource("provideTestCases")
    public void testCompilerMZ(String inputFile, int expectedExitCode) throws IOException, TokenError {
        String baseDir = "src/test/java/org/compiler/testCompilerMZResources/exits/";
        String outDir = "src/test/java/org/compiler/testCompilerMZResources/";
        int exitCode = CompilerMZ.callFullStackWithReturnCode(baseDir + inputFile, outDir + "out.asm", outDir + "out.o",
                outDir + "out");
        assertEquals(expectedExitCode, exitCode);
    }

    private static Stream<Object[]> provideTestCases() {
        return Stream.of(new Object[] { "exits1.mz", 1 }, new Object[] { "exits2.mz", 2 },
                new Object[] { "exits3.mz", 3 }, new Object[] { "exits4.mz", 4 }, new Object[] { "exits5.mz", 5 },
                new Object[] { "exits6.mz", 6 }, new Object[] { "exits10.mz", 10 }, new Object[] { "exits11.mz", 11 },
                new Object[] { "exits15.mz", 15 }, new Object[] { "exits42.mz", 42 }, new Object[] { "exits45.mz", 45 },
                new Object[] { "exits69.mz", 69 }, new Object[] { "exits100.mz", 100 },
                new Object[] { "exits111.mz", 111 }, new Object[] { "exits130.mz", 130 },
                new Object[] { "exits251.mz", 251 });
    }

    @ParameterizedTest
    @MethodSource("providePrintTestCases")
    public void testPrints(String inputFile, String expectedOutput) throws IOException, TokenError {
        String baseDir = "src/test/java/org/compiler/testCompilerMZResources/prints/";
        String outDir = "src/test/java/org/compiler/testCompilerMZResources/";
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        CompilerMZ.callFullStackWithReturnCode(baseDir + inputFile, outDir + "out.asm", outDir + "out.o",
                outDir + "out");

        Assertions.assertEquals(expectedOutput, outContent.toString());
    }

    private static Stream<Object[]> providePrintTestCases() {
        return Stream.of(new Object[] { "prints100.mz", "100\n" }, new Object[] { "printsCiao.mz", "Ciao\n" },
                new Object[] { "prints1.mz", "1\n" }, new Object[] { "prints-10000000000.mz", "-10000000000\n" },
                new Object[] { "printsEmpty.mz", "\n" },
                new Object[] { "printsLimits.mz", "9223372036854775807\n-9223372036854775808\n" });
    }
}