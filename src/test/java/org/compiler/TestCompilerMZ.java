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
                "src/test/java/org/compiler/testCompilerMZResources/out.asm",
                "src/test/java/org/compiler/testCompilerMZResources/out.o",
                "src/test/java/org/compiler/testCompilerMZResources/out");
        assertEquals(100, exitCode);
        exitCode = CompilerMZ.callFullStackWithReturnCode(
                "src/test/java/org/compiler/testCompilerMZResources/exits42.mz",
                "src/test/java/org/compiler/testCompilerMZResources/out.asm",
                "src/test/java/org/compiler/testCompilerMZResources/out.o",
                "src/test/java/org/compiler/testCompilerMZResources/out");
        assertEquals(42, exitCode);
        assertThrows(NoSuchFileException.class,
                () -> CompilerMZ.callFullStack("src/test/null.mz",
                        "src/test/java/org/compiler/testCompilerMZResources/out.asm",
                        "src/test/java/org/compiler/testCompilerMZResources/out.o",
                        "src/test/java/org/compiler/testCompilerMZResources/out"));
        exitCode = CompilerMZ.callFullStackWithReturnCode(
                "src/test/java/org/compiler/testCompilerMZResources/exits45.mz",
                "src/test/java/org/compiler/testCompilerMZResources/out.asm",
                "src/test/java/org/compiler/testCompilerMZResources/out.o",
                "src/test/java/org/compiler/testCompilerMZResources/out");
        assertEquals(45, exitCode);
        exitCode = CompilerMZ.callFullStackWithReturnCode(
                "src/test/java/org/compiler/testCompilerMZResources/exits15.mz",
                "src/test/java/org/compiler/testCompilerMZResources/out.asm",
                "src/test/java/org/compiler/testCompilerMZResources/out.o",
                "src/test/java/org/compiler/testCompilerMZResources/out");
        assertEquals(15, exitCode);
        exitCode = CompilerMZ.callFullStackWithReturnCode(
                "src/test/java/org/compiler/testCompilerMZResources/exits10.mz",
                "src/test/java/org/compiler/testCompilerMZResources/out.asm",
                "src/test/java/org/compiler/testCompilerMZResources/out.o",
                "src/test/java/org/compiler/testCompilerMZResources/out");
        assertEquals(10, exitCode);
        exitCode = CompilerMZ.callFullStackWithReturnCode(
                "src/test/java/org/compiler/testCompilerMZResources/exits1.mz",
                "src/test/java/org/compiler/testCompilerMZResources/out.asm",
                "src/test/java/org/compiler/testCompilerMZResources/out.o",
                "src/test/java/org/compiler/testCompilerMZResources/out");
        assertEquals(1, exitCode);
        exitCode = CompilerMZ.callFullStackWithReturnCode(
                "src/test/java/org/compiler/testCompilerMZResources/exits2.mz",
                "src/test/java/org/compiler/testCompilerMZResources/out.asm",
                "src/test/java/org/compiler/testCompilerMZResources/out.o",
                "src/test/java/org/compiler/testCompilerMZResources/out");
        assertEquals(2, exitCode);
        exitCode = CompilerMZ.callFullStackWithReturnCode(
                "src/test/java/org/compiler/testCompilerMZResources/exits69.mz",
                "src/test/java/org/compiler/testCompilerMZResources/out.asm",
                "src/test/java/org/compiler/testCompilerMZResources/out.o",
                "src/test/java/org/compiler/testCompilerMZResources/out");
        assertEquals(69, exitCode);
        exitCode = CompilerMZ.callFullStackWithReturnCode(
                "src/test/java/org/compiler/testCompilerMZResources/exits111.mz",
                "src/test/java/org/compiler/testCompilerMZResources/out.asm",
                "src/test/java/org/compiler/testCompilerMZResources/out.o",
                "src/test/java/org/compiler/testCompilerMZResources/out");
        assertEquals(111, exitCode);
        exitCode = CompilerMZ.callFullStackWithReturnCode(
                "src/test/java/org/compiler/testCompilerMZResources/exits130.mz",
                "src/test/java/org/compiler/testCompilerMZResources/out.asm",
                "src/test/java/org/compiler/testCompilerMZResources/out.o",
                "src/test/java/org/compiler/testCompilerMZResources/out");
        assertEquals(130, exitCode);

    }
}
