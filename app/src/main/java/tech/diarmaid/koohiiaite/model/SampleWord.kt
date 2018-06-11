package tech.diarmaid.koohiiaite.model

/**
 * Represent an entry in the sample_word table
 */
class SampleWord(val id: Int, val heisigId: Int, val kanjiWord: String, val hiraganaReading: String, val englishMeaning: String, val category: String, val frequency: Int)
