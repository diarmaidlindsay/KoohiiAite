package tech.diarmaid.koohiiaite.model

/**
 * Represent an entry in an imported my_stories.csv File
 */
class CSVEntry(var id: String, var kanji: String, keyword: String, story: String) {
    //trim enclosing quotations from my_stories.csv
    var keyword: String = keyword.replace("^\"|\"$".toRegex(), "")
    var story: String = story.replace("^\"|\"$".toRegex(), "")
}
