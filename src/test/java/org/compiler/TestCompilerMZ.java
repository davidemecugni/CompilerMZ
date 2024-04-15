package org.compiler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

public class TestCompilerMZ {
    @Test
    public void testCompilerMZ() throws IOException {
        int exitCode = CompilerMZ.callFullStackWithReturnCode(
                "src/test/java/org/compiler/testCompilerMZResources/exits100.mz",
                "src/test/java/org/compiler/testCompilerMZResources/exits100.asm",
                "src/test/java/org/compiler/testCompilerMZResources/exits42.o",
                "src/test/java/org/compiler/testCompilerMZResources/exits42");
        assertEquals(100, exitCode);
        exitCode = CompilerMZ.callFullStackWithReturnCode(
                "src/test/java/org/compiler/testCompilerMZResources/exits42.mz",
                "src/test/java/org/compiler/testCompilerMZResources/exits42.asm",
                "src/test/java/org/compiler/testCompilerMZResources/exits42.o",
                "src/test/java/org/compiler/testCompilerMZResources/exits42");
        assertEquals(42, exitCode);
        assertThrows(NoSuchFileException.class,
                () -> CompilerMZ.callFullStack("src/test/null.mz",
                        "src/test/java/org/compiler/testCompilerMZResources/exits42.asm",
                        "src/test/java/org/compiler/testCompilerMZResources/exits42.o",
                        "src/test/java/org/compiler/testCompilerMZResources/exits42"));

    }
}
