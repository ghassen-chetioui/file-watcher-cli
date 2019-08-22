package io.github.cgh;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Runner {
    public static void main(String[] args) {
        Path path = Paths.get(args[0]);
        new FileWatcher(path).run();
    }
}
