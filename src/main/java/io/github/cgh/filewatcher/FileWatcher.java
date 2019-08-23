package io.github.cgh.filewatcher;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

class FileWatcher {

    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);
    private final Map<Path, String> md5ByPath = new HashMap<>();
    private final Path path;

    FileWatcher(Path path) {
        this.path = path;
    }

    void run() {
        this.md5ByPath.putAll(currentMd5ByFile());
        System.out.println("Watching " + this.path.toAbsolutePath());
        this.executor.scheduleAtFixedRate(this::watch, 0, 1, TimeUnit.SECONDS);
    }

    private void watch() {
        Map<Path, String> currentMd5ByPath = currentMd5ByFile();
        Reporter.created(Analyser.created(this.md5ByPath, currentMd5ByPath));
        Reporter.modified(Analyser.modified(this.md5ByPath, currentMd5ByPath));
        Reporter.moved(Analyser.moved(this.md5ByPath, currentMd5ByPath));
        Reporter.deleted(Analyser.deleted(this.md5ByPath, currentMd5ByPath));
        md5ByPath.clear();
        md5ByPath.putAll(currentMd5ByPath);
    }

    private Map<Path, String> currentMd5ByFile() {
        try {
            return Files.walk(this.path)
                    .filter(Files::isRegularFile)
                    .map(this::calculateMd5)
                    .collect(Collectors.toMap(it -> it.path, it -> it.checksum));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private FileMd5Checksum calculateMd5(Path path) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(Files.readAllBytes(path));
            return new FileMd5Checksum(path, String.format("%032X", new BigInteger(1, hash)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static class FileMd5Checksum {
        final Path path;
        final String checksum;

        FileMd5Checksum(Path path, String checksum) {
            this.path = path;
            this.checksum = checksum;
        }
    }
}
