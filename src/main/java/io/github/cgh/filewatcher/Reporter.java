package io.github.cgh.filewatcher;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class Reporter {

    static void report(final Map<Path, String> previousMd5ByPath, final Map<Path, String> currentMd5ByPath) {
        created(previousMd5ByPath, currentMd5ByPath).forEach(it -> System.out.printf("[created] %s\n", it.toString()));
        modified(previousMd5ByPath, currentMd5ByPath).forEach(it -> System.out.printf("[modified] %s\n", it.toString()));
        moved(previousMd5ByPath, currentMd5ByPath).forEach((c, p) -> System.out.printf("[moved] %s to %s\n", p.toString(), c.toString()));
        deleted(previousMd5ByPath, currentMd5ByPath).forEach(it -> System.out.printf("[deleted] %s\n", it.toString()));
    }

    static List<Path> created(final Map<Path, String> previousMd5ByPath, final Map<Path, String> currentMd5ByPath) {
        return currentMd5ByPath.entrySet().stream()
                .filter(it -> !previousMd5ByPath.containsKey(it.getKey()))
                .filter(it -> !previousMd5ByPath.containsValue(it.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    static List<Path> deleted(final Map<Path, String> previousMd5ByPath, final Map<Path, String> currentMd5ByPath) {
        return created(currentMd5ByPath, previousMd5ByPath);
    }

    static List<Path> modified(final Map<Path, String> previousMd5ByPath, final Map<Path, String> currentMd5ByPath) {
        return currentMd5ByPath.entrySet().stream()
                .filter(it -> {
                    String previousChecksum = previousMd5ByPath.get(it.getKey());
                    return previousChecksum != null && !it.getValue().equals(previousChecksum);
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    static Map<Path, Path> moved(final Map<Path, String> previousMd5ByPath, final Map<Path, String> currentMd5ByPath) {
        return currentMd5ByPath.entrySet().stream()
                .filter(it -> !previousMd5ByPath.containsKey(it.getKey()))
                .filter(it -> previousMd5ByPath.containsValue(it.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        it -> previousMd5ByPath.entrySet().stream().filter(pit -> pit.getValue().equals(it.getValue()))
                                .findFirst().orElseThrow(IllegalStateException::new).getKey())
                );
    }
}
