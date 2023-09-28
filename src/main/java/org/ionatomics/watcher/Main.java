package org.ionatomics.watcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;

public class Main {

    public static final String DARK_MATTER_WATCHING = "Dark Matter Watching...";

    public static void main(String[] args) {
        String srcPath = "src/main/cfscript";
        String mvnComm = "process-resources";
        // Src Path
        if (args.length > 0) {
            srcPath = args[0];
        }
        // Mvn Command to run
        if (args.length > 1) {
            mvnComm = args[1];
        }
        System.out.println(DARK_MATTER_WATCHING);
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path dir = Paths.get(srcPath);
            Files.walk(dir)
                .filter(path -> Files.isDirectory(path))
                .forEach(path -> {
                    try {
                        path.register(watchService,
                                StandardWatchEventKinds.ENTRY_CREATE,
                                StandardWatchEventKinds.ENTRY_DELETE,/* This can be handled better - delete the java file*/
                                StandardWatchEventKinds.ENTRY_MODIFY);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    Path context = (Path) event.context();
                    Path fullPath = ((Path) key.watchable()).resolve(context);

                    if (!Files.isDirectory(fullPath)) {
                        System.out.println("File "+ event.kind().name() +  ": " + fullPath);
                        if (mvnComm.length() > 0) {
                            ProcessBuilder builder = new ProcessBuilder("mvn", mvnComm);
                            builder.directory(new File(System.getProperty("user.dir")));
                            builder.redirectErrorStream(true);
                            Process process = builder.start();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                System.out.println(line);
                            }
                            System.out.println(DARK_MATTER_WATCHING);
                        }
                    }
                }
                key.reset();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
