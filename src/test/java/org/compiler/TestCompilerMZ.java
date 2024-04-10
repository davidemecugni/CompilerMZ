package org.compiler;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

public class TestCompilerMZ {
    @Test
    public void testCompilerMZ() {
        ProcessBuilder processBuilder = new ProcessBuilder("./tester.sh");
        processBuilder.redirectErrorStream(true);
        int exitCode = -1;

        try {
            Process process = processBuilder.start();
            exitCode = process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // It this gives error, code in fausto.mz is wrong
        assertEquals(0, exitCode);
    }
}
