package tech.diarmaid.koohiiaite.util

import tech.diarmaid.koohiiaite.domain.model.CsvEntry
import java.io.BufferedReader

class CsvParser {

    fun parse(reader: BufferedReader): Result<List<CsvEntry>> {
        return try {
            // Skip header row
            reader.readLine()

            val entries = mutableListOf<CsvEntry>()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val currentLine = line ?: break
                if (currentLine.isBlank()) continue

                val fields = parseCsvLine(currentLine, reader)

                // Expect 6 fields: framenr,kanji,keyword,public,last_edited,story
                if (fields.size == 6) {
                    val framenr = fields[0].trim()
                    if (framenr.toIntOrNull() != null) {
                        entries.add(
                            CsvEntry(
                                id = framenr,
                                kanji = fields[1].trim(),
                                keyword = fields[2].trim(),
                                story = fields[5].trim()
                            )
                        )
                    }
                }
            }
            Result.success(entries)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Parse a single CSV line, handling quoted fields that may contain
     * commas, newlines, and escaped double-quotes ("").
     */
    private fun parseCsvLine(firstLine: String, reader: BufferedReader): List<String> {
        val fields = mutableListOf<String>()
        val currentField = StringBuilder()
        var inQuotes = false

        var line = firstLine

        while (true) {
            var i = 0
            while (i < line.length) {
                val c = line[i]

                if (inQuotes) {
                    if (c == '"') {
                        // Check for escaped double-quote ("")
                        if (i + 1 < line.length && line[i + 1] == '"') {
                            currentField.append('"')
                            i += 2
                            continue
                        }
                        // End of quoted field
                        inQuotes = false
                        i++
                    } else {
                        currentField.append(c)
                        i++
                    }
                } else {
                    if (c == '"') {
                        inQuotes = true
                        i++
                    } else if (c == ',') {
                        fields.add(currentField.toString())
                        currentField.clear()
                        i++
                    } else {
                        currentField.append(c)
                        i++
                    }
                }
            }

            // If we're still inside a quoted field, read the next line
            if (inQuotes) {
                currentField.append('\n')
                val nextLine = reader.readLine() ?: break
                line = nextLine
            } else {
                break
            }
        }

        // Add the last field
        fields.add(currentField.toString())

        return fields
    }
}
