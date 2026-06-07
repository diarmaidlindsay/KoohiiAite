# KoohiiAite
Offline Android companion for kanji.koohii.com

For Western Students of Japanese, by far the most difficult obstacle to mastery is kanji.
Japanese students learn these through years of repetition, but this requires a significant time investment for
an adult non-native speaker. Besides that, it is not an interesting way to study, and similar looking kanji can
easily be confused.

James Heisig created a method for learning to recognise kanji by breaking them down into "keywords" and "primitives",
and using these to create kanji "stories". He documented this method in his bestselling book
"Remembering the Kanji", which has since undergone numerous revisions.

Of course, a student is free to create kanji stories of their own which assist them in memorisation. A website
called kanji.koohii.com exists to help students share kanji stories, even relabel keywords and primitives.

I personally use this website daily as part of my studies, but often when attempting to read or recognise kanji
I do not have access to a PC. I might be on a train or on the street in Japan, and truthfully the kanji.koohii.com
website was not created for mobile browsing, so it is quite inconvenient to lookup kanji on a smartphone.
Besides that, the website is lacking features such as the ability to look up kanji by its components.

This has motivated me to create a companion application, KoohiiAite, to address these shortcomings, and make it
simple for Heisig students to quickly lookup kanji and read/write stories wherever they happen to be.

## Features

### 1. Kanji List and Search

- View all kanji in a scrollable list
- Search functionality supporting:
  - Primitives
  - Keywords
  - Kanji characters
  - Frame numbers (Heisig Index)
- Advanced filtering options:
  - Jōyō kanji filter
  - Custom keyword filter
  - Story filter

### 2. Kanji Detail View

The app provides detailed information about each kanji character through four main tabs:

#### Story Tab

- Displays the kanji character and Heisig frame number
- Custom keyword support with dialog editing
- View and edit your custom stories for remembering kanji
- Rich text formatting with `*italic*`, `**bold**`, and `[[kanji]]` links
- Clickable keyword links to other kanji detail pages

#### Dictionary Tab

- Displays On'yomi (音読み) readings
- Displays Kun'yomi (訓読み) readings
- Shows various meanings of the kanji
- Includes frequency information

#### Sample Words Tab

- Example vocabulary words containing the kanji
- Shows kanji reading, hiragana, English meaning, and frequency

#### Koohii Tab

- Direct integration with Koohii.com
- Opens the kanji page on kanji.koohii.com for community stories

### 3. Primitive Management

- Grid view of all primitive elements
- View primitive images
- Custom naming/labeling of primitives
- Reference system linking primitives to kanji

### 4. Data Import/Export

- Import stories from CSV files
- Preview imported data before confirming
- Progress tracking for import operations
- Supports Koohii.com CSV export format

### 5. Database Features

The app uses a local SQLite database (Room) with the following main entities:

- HeisigKanji: Base kanji information and Jōyō status
- Primitive: Fundamental elements used in kanji
- Keyword: Default keywords for kanji
- UserKeyword: Custom user-defined keywords
- Story: User-created memory stories
- Reading: On'yomi and Kun'yomi readings
- Meaning: Various meanings of kanji
- SampleWord: Example words using the kanji
- KanjiFrequency: Usage frequency data

## Technical Details

### Implementation

- Written in Kotlin
- Jetpack Compose with Material 3 for UI
- Single Activity architecture with Navigation Compose
- Room database with `createFromAsset()` for pre-populated DB
- Hilt for dependency injection
- Coroutines and Flow for asynchronous operations
- Kotlin Serialization for type-safe navigation arguments
- Coil for image loading

### Database Structure

- Pre-populated database with kanji information
- Support for user-generated content (stories, custom keywords)
- Foreign key relationships maintaining data integrity
- Efficient querying for search and filtering operations

## Usage

1. **Main Screen**: Browse through the kanji list or use the search bar to find specific characters
2. **Detail View**: Tap any kanji to view detailed information
3. **Story Creation**: Add your own stories to help remember kanji
4. **Primitive Reference**: Access the primitive list from the main menu to study basic elements
5. **Import Stories**: Use the import feature to bulk-load stories from CSV files
