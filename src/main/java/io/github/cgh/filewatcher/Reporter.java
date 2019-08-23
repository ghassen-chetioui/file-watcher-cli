package io.github.cgh.filewatcher;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

class Reporter {

    static void created(List<Path> created) {
        created.forEach(it -> System.out.printf("[created] %s\n", it.toString()));
    }

    static void modified(List<Path> modified) {
        modified.forEach(it -> System.out.printf("[modified] %s\n", it.toString()));
    }

    static void deleted(List<Path> deleted) {
        deleted.forEach(it -> System.out.printf("[deleted] %s\n", it.toString()));
    }

    static void moved(Map<Path, Path> moved) {
        moved.forEach((c, p) -> System.out.printf("[moved] %s to %s\n", p.toString(), c.toString()));
    }

}
