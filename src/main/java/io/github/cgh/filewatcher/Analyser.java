package io.github.cgh.filewatcher;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class Analyser {

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
