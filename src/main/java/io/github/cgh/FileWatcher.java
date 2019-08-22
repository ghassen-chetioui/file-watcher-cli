package io.github.cgh;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FileWatcher {

    private final ScheduledExecutorService executor;
    private final Path path;

    public FileWatcher(Path path) {
        this.executor = new ScheduledThreadPoolExecutor(1);
        this.path = path;
    }

    public void run() {
        executor.scheduleAtFixedRate(this::watch, 0, 1, TimeUnit.SECONDS);
    }

    private void watch() {
        try {
            Files.walk(this.path).filter(Files::isRegularFile).map(this::calculateMd5).forEach(System.out::println);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private FileMd5Checksum calculateMd5(Path path) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(Files.readAllBytes(path));
            return new FileMd5Checksum(path, new String(digest.digest()));
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

        @Override
        public String toString() {
            return "[path = " + path + ", md5 = " + checksum + "]";
        }
    }
}
