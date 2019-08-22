package io.github.cgh.filewatcher;

import com.beust.jcommander.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Runner {

    static public class PathConverter implements IStringConverter<Path> {
        @Override
        public Path convert(String value) {
            return Paths.get(value);
        }
    }

    static public class PathValidator implements IValueValidator<Path> {

        @Override
        public void validate(String name, Path value) throws ParameterException {
            File file = value.toFile();
            if (!file.exists()) throw new ParameterException("Invalid path");
            if (!file.isDirectory()) throw new ParameterException("Path is not a directory");
        }
    }

    @Parameter(
            names = {"-p", "--path"},
            description = "Path of the directory to watch",
            converter = PathConverter.class,
            validateValueWith = PathValidator.class
    )
    private Path path = Paths.get(".");

    private void run() {
        new FileWatcher(path).run();
    }

    public static void main(String[] args) {
        Runner runner = new Runner();
        JCommander.newBuilder().addObject(runner).build().parse(args);
        runner.run();
    }


}
