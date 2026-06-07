package tech.diarmaid.koohiiaite.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.StringReader

class CsvParserTest {

    private val parser = CsvParser()

    @Test
    fun `parse simple CSV`() {
        val csv = """
            framenr,kanji,keyword,public,last_edited,story
            1,一,one,0,2014-01-01,simple story
        """.trimIndent()
        val result = parser.parse(BufferedReader(StringReader(csv)))
        assertTrue(result.isSuccess)
        val entries = result.getOrThrow()
        assertEquals(1, entries.size)
        assertEquals("1", entries[0].id)
        assertEquals("一", entries[0].kanji)
        assertEquals("one", entries[0].keyword)
        assertEquals("simple story", entries[0].story)
    }

    @Test
    fun `parse CSV with quoted fields`() {
        val csv = """
            framenr,kanji,keyword,public,last_edited,story
            1,一,"one",0,2014-01-01,"a story with ""quotes"" inside"
            2,二,"two",0,2014-01-01,"comma, in story"
        """.trimIndent()
        val result = parser.parse(BufferedReader(StringReader(csv)))
        assertTrue(result.isSuccess)
        val entries = result.getOrThrow()
        assertEquals(2, entries.size)
        assertEquals("a story with \"quotes\" inside", entries[0].story)
        assertEquals("comma, in story", entries[1].story)
    }

    @Test
    fun `parse CSV with multiline story`() {
        val csv = """
            framenr,kanji,keyword,public,last_edited,story
            8,八,"eight",0,2014-05-04,"Hachi begins with ha.
            Second line of story.
            Third line."
            9,九,nine,0,2014-01-01,short story
        """.trimIndent()
        val result = parser.parse(BufferedReader(StringReader(csv)))
        assertTrue(result.isSuccess)
        val entries = result.getOrThrow()
        assertEquals(2, entries.size)
        assertEquals("Hachi begins with ha.\nSecond line of story.\nThird line.", entries[0].story)
        assertEquals("short story", entries[1].story)
    }

    @Test
    fun `parse CSV with formatting markers`() {
        val csv = """
            framenr,kanji,keyword,public,last_edited,story
            14,田,"rice field",0,2014-01-01,"A farmer uses his *brain* to measure a #rice field#."
            22,晶,crystal,0,2014-01-01,"A #crystal# sparkles like 3 *suns*."
        """.trimIndent()
        val result = parser.parse(BufferedReader(StringReader(csv)))
        assertTrue(result.isSuccess)
        val entries = result.getOrThrow()
        assertEquals(2, entries.size)
        assertEquals("A farmer uses his *brain* to measure a #rice field#.", entries[0].story)
        assertEquals("A #crystal# sparkles like 3 *suns*.", entries[1].story)
    }
}
