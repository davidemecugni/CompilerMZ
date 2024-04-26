package org.compiler;

import org.compiler.errors.TokenError;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCompilerMZ {

    @ParameterizedTest
    @MethodSource("provideTestCases")
    public void testCompilerMZ(String inputFile, int expectedExitCode) throws IOException, TokenError {
        String baseDir = "src/test/java/org/compiler/testCompilerMZResources/";
        int exitCode = CompilerMZ.callFullStackWithReturnCode(baseDir + inputFile, baseDir + "out.asm",
                baseDir + "out.o", baseDir + "out");
        assertEquals(expectedExitCode, exitCode);
    }

    private static Stream<Object[]> provideTestCases() {
        return Stream.of(new Object[] { "exits1.mz", 1 }, new Object[] { "exits2.mz", 2 },
                new Object[] { "exits3.mz", 3 }, new Object[] { "exits4.mz", 4 }, new Object[] { "exits5.mz", 5 },
                new Object[] { "exits10.mz", 10 }, new Object[] { "exits11.mz", 11 }, new Object[] { "exits15.mz", 15 },
                new Object[] { "exits42.mz", 42 }, new Object[] { "exits45.mz", 45 }, new Object[] { "exits69.mz", 69 },
                new Object[] { "exits100.mz", 100 }, new Object[] { "exits111.mz", 111 },
                new Object[] { "exits130.mz", 130 }, new Object[] { "exits251.mz", 251 });
    }
}