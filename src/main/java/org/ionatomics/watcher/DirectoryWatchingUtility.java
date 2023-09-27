package org.ionatomics.watcher;

import io.methvin.watcher.DirectoryWatcher;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class DirectoryWatchingUtility {

    private final Path directoryToWatch;
    private final DirectoryWatcher watcher;

    public DirectoryWatchingUtility(Path directoryToWatch) throws IOException {
        this.directoryToWatch = directoryToWatch;
        this.watcher = DirectoryWatcher.builder()
                .path(directoryToWatch) // or use paths(directoriesToWatch)
                .listener(event -> {
                    switch (event.eventType()) {
                        case CREATE: /* file created */; break;
                        case MODIFY: /* file modified */; break;
                        case DELETE: /* file deleted */; break;
                    }
                })
                // .fileHashing(false) // defaults to true
                // .logger(logger) // defaults to LoggerFactory.getLogger(DirectoryWatcher.class)
                // .watchService(watchService) // defaults based on OS to either JVM WatchService or the JNA macOS WatchService
                .build();
    }

    public void stopWatching() throws IOException {
        watcher.close();
    }

    public CompletableFuture<Void> watch() {
        // you can also use watcher.watch() to block the current thread
        return watcher.watchAsync();
    }
}