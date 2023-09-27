package org.ionatomics.watcher;

import io.methvin.watcher.DirectoryWatcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        System.out.println("Dark Matter Watching...");
        Path dir = Paths.get("src/main/cfscript");
        try {
            DirectoryWatcher watcher = DirectoryWatcher.builder()
                    .path(dir)  // or .paths(dir1, dir2, ...)
                    .listener(event -> {
                        Path changedPath = event.path();
                        if (changedPath != null &&
                                (changedPath.toString().endsWith(".cfc") || changedPath.toString().endsWith(".dms"))) {
                            System.out.println(event.eventType() + " " + event.path());
                            runMavenCommand();
                        }
                    })
                    .build();
            watcher.watch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void runMavenCommand() {
        try {
            Process process = new ProcessBuilder("mvn", "process-resources").start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Darkmatter command failed with exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
