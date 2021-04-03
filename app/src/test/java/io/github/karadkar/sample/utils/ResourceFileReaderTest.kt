package io.github.karadkar.sample.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ResourceFileReaderTest {
    @Test
    fun `reading test json file`() {
        val reader = ResourceFileReader("test.json")
        assertThat(reader.content).isEqualTo("{\"key\": \"value\"}")
    }

    @Test
    fun `invalid file path returns empty string`() {
        val reader = ResourceFileReader("invalid-file.json")
        assertThat(reader.content).isEqualTo("")
    }
}