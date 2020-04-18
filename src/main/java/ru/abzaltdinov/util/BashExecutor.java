package ru.abzaltdinov.util;

import java.io.IOException;

public class BashExecutor {
    public static void exec(String[] cmd, boolean printOutput) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        if (printOutput) {
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
        }
        Process process = processBuilder.start();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void exec(String cmd, String param, boolean printOutput) throws IOException {
        exec(new String[]{cmd, param}, printOutput);
    }

    public static void exec(String cmd) throws IOException {
        exec(new String[]{cmd}, false);
    }

    public static void exec(String cmd, String param) throws IOException {
        exec(cmd, param, false);
    }
}
