package io.github.cgh.filewatcher;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

class AnalyserTest implements WithAssertions {

    @Test
    void should_detect_created_files() {
        Map<Path, String> previous = new HashMap<Path, String>() {{
            put(Paths.get("file1"), "1");
        }};

        Map<Path, String> current = new HashMap<Path, String>() {{
            put(Paths.get("file1"), "1");
            put(Paths.get("file2"), "2");
        }};

        assertThat(Analyser.created(previous, current)).containsExactly(Paths.get("file2"));
    }

    @Test
    void moved_files_should_not_be_detected_as_created() {
        Map<Path, String> previous = new HashMap<Path, String>() {{
            put(Paths.get("file1"), "1");
        }};

        Map<Path, String> current = new HashMap<Path, String>() {{
            put(Paths.get("dir/file1"), "1");
        }};

        assertThat(Analyser.created(previous, current)).isEmpty();
    }

    @Test
    void should_detect_deleted_files() {
        Map<Path, String> previous = new HashMap<Path, String>() {{
            put(Paths.get("file1"), "1");
            put(Paths.get("file2"), "2");
        }};
        Map<Path, String> current = new HashMap<Path, String>() {{
            put(Paths.get("file1"), "1");
        }};

        assertThat(Analyser.deleted(previous, current)).containsExactly(Paths.get("file2"));
    }

    @Test
    void moved_files_should_not_be_detected_as_deleted() {
        Map<Path, String> previous = new HashMap<Path, String>() {{
            put(Paths.get("file1"), "1");
        }};

        Map<Path, String> current = new HashMap<Path, String>() {{
            put(Paths.get("dir/file1"), "1");
        }};

        assertThat(Analyser.deleted(previous, current)).isEmpty();
    }

    @Test
    void should_detect_modified_files() {
        Map<Path, String> previous = new HashMap<Path, String>() {{
            put(Paths.get("file1"), "1");
            put(Paths.get("file2"), "2");
        }};
        Map<Path, String> current = new HashMap<Path, String>() {{
            put(Paths.get("file1"), "2");
            put(Paths.get("file3"), "2");
        }};

        assertThat(Analyser.modified(previous, current)).containsExactly(Paths.get("file1"));
    }

    @Test
    void should_detect_moved_files() {
        Map<Path, String> previous = new HashMap<Path, String>() {{
            put(Paths.get("file1"), "1");
        }};
        Map<Path, String> current = new HashMap<Path, String>() {{
            put(Paths.get("dir/file1"), "1");
        }};

        assertThat(Analyser.moved(previous, current)).hasSize(1);
        Map.Entry<Path, Path> entry = Analyser.moved(previous, current).entrySet().iterator().next();
        assertThat(entry.getKey()).isEqualTo(Paths.get("dir/file1"));
        assertThat(entry.getValue()).isEqualTo(Paths.get("file1"));
    }
}