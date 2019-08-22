package io.github.cgh.filewatcher;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

class ReporterTest implements WithAssertions {

    @Test
    void should_detect_created_files() {
        Map<Path, String> previous = new HashMap<Path, String>() {{
            put(Paths.get("file1"), "1");
        }};

        Map<Path, String> current = new HashMap<Path, String>() {{
            put(Paths.get("file1"), "1");
            put(Paths.get("file2"), "2");
        }};

        assertThat(Reporter.created(previous, current)).containsExactly(Paths.get("file2"));
    }

    @Test
    void moved_files_should_not_be_detected_as_created() {
        Map<Path, String> previous = new HashMap<Path, String>() {{
            put(Paths.get("file1"), "1");
        }};

        Map<Path, String> current = new HashMap<Path, String>() {{
            put(Paths.get("dir/file1"), "1");
        }};

        assertThat(Reporter.created(previous, current)).isEmpty();
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

        assertThat(Reporter.deleted(previous, current)).containsExactly(Paths.get("file2"));
    }

    @Test
    void moved_files_should_not_be_detected_as_deleted() {
        Map<Path, String> previous = new HashMap<Path, String>() {{
            put(Paths.get("file1"), "1");
        }};

        Map<Path, String> current = new HashMap<Path, String>() {{
            put(Paths.get("dir/file1"), "1");
        }};

        assertThat(Reporter.deleted(previous, current)).isEmpty();
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

        assertThat(Reporter.modified(previous, current)).containsExactly(Paths.get("file1"));
    }

    @Test
    void should_detect_moved_files() {
        Map<Path, String> previous = new HashMap<Path, String>() {{
            put(Paths.get("file1"), "1");
        }};
        Map<Path, String> current = new HashMap<Path, String>() {{
            put(Paths.get("dir/file1"), "1");
        }};

        assertThat(Reporter.moved(previous, current)).hasSize(1);
        Map.Entry<Path, Path> entry = Reporter.moved(previous, current).entrySet().iterator().next();
        assertThat(entry.getKey()).isEqualTo(Paths.get("dir/file1"));
        assertThat(entry.getValue()).isEqualTo(Paths.get("file1"));
    }
}