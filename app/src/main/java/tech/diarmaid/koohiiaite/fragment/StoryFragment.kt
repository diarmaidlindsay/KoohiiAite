package tech.diarmaid.koohiiaite.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.TextAppearanceSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_detail_story.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.diarmaid.koohiiaite.R
import tech.diarmaid.koohiiaite.activity.KanjiDetailActivity
import tech.diarmaid.koohiiaite.database.AppDatabase
import tech.diarmaid.koohiiaite.database.entity.HeisigKanji
import tech.diarmaid.koohiiaite.database.entity.Keyword
import tech.diarmaid.koohiiaite.database.entity.UserKeyword
import tech.diarmaid.koohiiaite.utils.Utils
import tech.diarmaid.koohiiaite.viewmodel.KanjiDetailViewModel
import java.util.*
import java.util.regex.Pattern
import kotlin.coroutines.CoroutineContext

/**
 * For display of the Story related to the heisig id
 * which was provided. Also allow editing of story.
 * Follow links from stories to other Kanji detail pages.
 */
class StoryFragment() : Fragment(), CoroutineScope {
    private var heisigIdInt: Int = 0
    private var userKeyword: String? = null
    private var originalKeyword: String? = null
    private var viewModel: KanjiDetailViewModel? = null
    private var mContext: MutableLiveData<Context> = MutableLiveData()
    private var keywordDialog: Dialog? = null
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_detail_story, container, false)
        mContext.observe(viewLifecycleOwner, Observer { context ->
            keywordDialog = Dialog(context as AppCompatActivity)
            keywordDialog?.setContentView(R.layout.dialog_box_keyword)
            keywordDialog?.setTitle(R.string.dialog_title_edit_keyword)
        })

        viewModel = ViewModelProvider(parentFragment as KanjiDetailFragment).get(KanjiDetailViewModel::class.java)
        viewModel?.kanji?.observe(viewLifecycleOwner, Observer {
            it?.let {
                kanji_detail?.text = it
            }
        })
        viewModel?.keyword?.observe(viewLifecycleOwner, Observer {
            originalKeyword = it
        })
        viewModel?.userKeyword?.observe(viewLifecycleOwner, Observer {
            userKeyword = it
        })
        viewModel?.heisigId?.observe(viewLifecycleOwner, Observer {
            it?.let {
                heisigIdInt = it
                heisig_id_detail?.text = HeisigKanji.getHeisigIdAsString(heisigIdInt)
            }
        })

        viewModel?.let { viewModel ->
            val allDataReady = {
                Log.d("StoryFragment", "Kanji : ${viewModel.kanji.value} Keyword : ${viewModel.keyword.value} UserKeyword : ${viewModel.userKeyword.value} HeisigId : ${viewModel.heisigId.value} Context : ${mContext.value}")
                arrayOf(viewModel.kanji.value, viewModel.keyword.value, viewModel.userKeyword.value, viewModel.heisigId.value, mContext.value)
                        .takeIf { theList -> theList.all { theValue -> theValue != null } }?.apply {
                            updateKeywordButton()
                            getStoryFromDatabase(heisigIdInt).observe(viewLifecycleOwner, Observer { story ->
                                story?.let {
                                    var formattedStory = SpannableString("")
                                    launch(Dispatchers.IO) {
                                        formattedStory = formatStory(story)
                                    }.invokeOnCompletion {
                                        (context as? AppCompatActivity)?.runOnUiThread(Runnable {
                                            story_detail?.setText(formattedStory, TextView.BufferType.SPANNABLE)
                                        })
                                    }
                                }
                            })
                        }
            }
            //If using RX in future, switch to using Observable.zip, since its much neater than this
            MediatorLiveData<String>().apply {
                addSource(viewModel.kanji) {
                    allDataReady()
                }
                addSource(viewModel.keyword) {
                    allDataReady()
                }
                addSource(viewModel.userKeyword) {
                    allDataReady()
                }
                addSource(viewModel.heisigId) {
                    allDataReady()
                }
                addSource(mContext) {
                    allDataReady()
                }
            }.observe(viewLifecycleOwner, Observer {
                // don't have to do anything with the last string
            })
        }

        view.findViewById<Button>(R.id.keyword_detail).setOnClickListener {
            val keywordEditText = keywordDialog?.findViewById<EditText>(R.id.keyword_dialog_edittext)
            val submitButton = keywordDialog?.findViewById<Button>(R.id.keyword_dialog_submit_button)
            val buttonDefault = keywordDialog?.findViewById<Button>(R.id.keyword_dialog_default_button)
            val cancelButton = keywordDialog?.findViewById<Button>(R.id.keyword_dialog_cancel_button)

            keywordEditText?.setSelectAllOnFocus(true)
            buttonDefault?.isEnabled = userKeyword.isNullOrBlank().not()

            submitButton?.setOnClickListener {
                //only submit if user actually changes the keyword from original
                if (keywordEditText?.text.toString() != originalKeyword) {
                    //if userKeyword not null that means there was a previous user keyword entered and we're updating it
                    submitKeyword(heisigIdInt, keywordEditText?.text.toString(), userKeyword.isNullOrBlank().not())
                }
                keywordDialog?.dismiss()
            }

            buttonDefault?.setOnClickListener {
                deleteKeyword(heisigIdInt)
                keywordDialog?.dismiss()
            }

            cancelButton?.setOnClickListener { keywordDialog?.dismiss() }

            keywordDialog?.show()
            keywordDialog?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            keywordDialog?.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }

        story_detail?.movementMethod = LinkMovementMethod.getInstance()
        return view
    }

    /**
     * Replace hash and asterix words with bolded and italisised versions of those words
     * as with kanji.koohii.com
     */
    private fun formatStory(aStoryText: String): SpannableString {
        val db = context?.let { AppDatabase.getDatabase(it) }
        var storyText = aStoryText
        val keywordDao = db?.keywordDao()
        val userKeywordDao = db?.userKeywordDao()
        val heisigKanjiDao = db?.heisigKanjiDao()
        var formattedStory = SpannableString(storyText)
        val italicSpanStarts = ArrayList<StoryFormat>()
        val italicSpanEnds = ArrayList<StoryFormat>()
        val boldSpanStarts = ArrayList<StoryFormat>()
        val boldSpanEnds = ArrayList<StoryFormat>()
        val braceSpanStarts = ArrayList<StoryFormat>()
        val braceSpanEnds = ArrayList<StoryFormat>()
        val kanjiToHeisigId = HashMap<String, Int>()

        //remove double quotes in stories ("")
        storyText = storyText.replace("\"\"".toRegex(), "\"")

        val storyChars = storyText.toCharArray()
        var isItalic = false
        var isBold = false
        var order = 0
        for (i in storyChars.indices) {
            val c = storyChars[i]
            if (c == '*') {
                //found italic
                if (!isItalic) {
                    //italic span start
                    isItalic = true
                    italicSpanStarts.add(StoryFormat(i, order++))
                } else {
                    isItalic = false
                    italicSpanEnds.add(StoryFormat(i, order++))
                }
            } else if (c == '#') {
                if (!isBold) {
                    isBold = true
                    boldSpanStarts.add(StoryFormat(i, order++))
                } else {
                    isBold = false
                    boldSpanEnds.add(StoryFormat(i, order++))
                }
            } else if (c == '{') {
                braceSpanStarts.add(StoryFormat(i, order++))
            } else if (c == '}') {
                /*  subtract last braceSpanStarts from i (which is a brace end)..
                    {不} (kanji)
                    567 = 2 (7-5)
                    order += 1; (the last brace)
                    {675} (heisigId)
                    56789 = 4 (9-5)
                    order += 3; (7, 5 and the last brace)
                 */
                //everything within the braces will be reduced to a length of 1 (a single kanji) we must account for the missing indices
                val orderMod = i - braceSpanStarts[braceSpanStarts.size - 1].index - 1
                order += orderMod
                braceSpanEnds.add(StoryFormat(i, order))
            }
        }

        //if malformatted, return original text
        if (italicSpanEnds.size != italicSpanStarts.size || boldSpanStarts.size != boldSpanEnds.size) {
            return formattedStory
        }

        //go through all braces and if it contains a heisigId, replace with the kanji, else remove the brackts
        if (braceSpanStarts.size > 0) {
            for (i in braceSpanStarts.indices) {
                val start = braceSpanStarts[i]
                //if it's a heisigId, look up the kanji
                if (!Utils.isKanji(storyText[start.index + 1])) {
                    val heisigId = Integer.parseInt(storyText.substring(start.index + 1, braceSpanEnds[i].index))
                    heisigKanjiDao?.getKanjiFor(heisigId)?.let {
                        kanjiToHeisigId[it.kanji] = it.heisigId
                    }

                } else {
                    //it's already a kanji, presumably, so just take away the brackets around it
                    val kanjiString = storyText[start.index + 1].toString()
                    heisigKanjiDao?.getHeisigFor(kanjiString)?.let {
                        kanjiToHeisigId[it.kanji] = it.heisigId
                    }
                }
            }
        }

        for (kanji in kanjiToHeisigId.keys) {
            storyText = storyText.replaceFirst(kanjiToHeisigId[kanji].toString().toRegex(), kanji)
            storyText = storyText.replaceFirst("\\{$kanji\\}".toRegex(), kanji)
        }

        //remove hashes and asterixes, string will shorten, we must take this into account with the indexes
        storyText = storyText.replace("\\*".toRegex(), "")
        storyText = storyText.replace("#".toRegex(), "")

        //any time the keyword is mentioned in the story, mark it as bold
        //we can use plain integer array to hold indexes instead of StoryFormat objects because indexes don't need adjustment
        val keywordSpanStarts = ArrayList<Int>()
        val keywordPattern = Pattern.compile((if (userKeyword.isNullOrBlank()) originalKeyword else userKeyword)!!.toLowerCase(Locale.getDefault()))
        val keywordMatcher = keywordPattern.matcher(storyText.toLowerCase(Locale.getDefault()))
        while (keywordMatcher.find()) {
            keywordSpanStarts.add(keywordMatcher.start())
        }

        //any time there is all uppercase words in the story, try to match with a keyword or user keywork and make it clickable if so
        val uppercaseSpanStarts = ArrayList<Int>()
        val uppercaseSpanEnds = ArrayList<Int>()
        val regEx = "[A-Z]+"
        val uppercasePattern = Pattern.compile(regEx)
        val uppercaseMatcher = uppercasePattern.matcher(storyText)
        while (uppercaseMatcher.find()) {
            uppercaseSpanStarts.add(uppercaseMatcher.start())
            uppercaseSpanEnds.add(uppercaseMatcher.end())
        }

        formattedStory = SpannableString(storyText)

        //mark italics
        for (i in italicSpanStarts.indices) {
            val indexStarts = italicSpanStarts[i].index - italicSpanStarts[i].order
            val indexEnds = italicSpanEnds[i].index - italicSpanEnds[i].order
            context?.let {
                formattedStory.setSpan(TextAppearanceSpan(context, R.style.storyAsterix),
                        indexStarts, indexEnds, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        //mark bolds
        for (i in boldSpanStarts.indices) {
            val indexStarts = boldSpanStarts[i].index - boldSpanStarts[i].order
            val indexEnds = boldSpanEnds[i].index - boldSpanEnds[i].order
            if (indexEnds - indexStarts < 2) {
                //ignore words of length 1 for example "I"
                continue
            }
            //if the text to be bolded is a keyword, we'll do that in the next loop instead
            if (!keywordSpanStarts.contains(indexStarts) && context != null) {
                formattedStory.setSpan(TextAppearanceSpan(context, R.style.storyHash),
                        indexStarts, indexEnds, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        //after first processing for multi-length keywords
        val uppercaseSpanStartsNoMLK = ArrayList(uppercaseSpanStarts)
        val uppercaseSpanEndsNoMLK = ArrayList(uppercaseSpanEnds)
        //TODO : Match multiple word keywords first - eg, "SORT OF THING" in 0510
        for (i in 0 until uppercaseSpanStarts.size - 1) {
            val start = uppercaseSpanStarts[i]
            var end = uppercaseSpanEnds[i]
            var uppercaseWord = storyText.substring(start, end)

            var matchingKeyword: Keyword? = null
            var matchingUserKeyword: UserKeyword? = null

            //start from the next word
            for (j in i + 1 until uppercaseSpanStarts.size) {
                val startNext = uppercaseSpanStarts[j]
                val endNext = uppercaseSpanEnds[j]
                val adjacentWord = storyText.substring(startNext, endNext)

                //look at the next uppercase word eg... uppercaseWord = RICE
                //adjacentWord = PLANT
                // RICE PLANT = match!

                if (keywordDao?.getKeywordStartingWith("$uppercaseWord $adjacentWord") == null && userKeywordDao?.getKeywordStartingWith("$uppercaseWord $adjacentWord") == null) {
                    //don't wipe out the non null matching keywords if the next adjacent word would cause a non-match
                    break
                }

                matchingKeyword = keywordDao?.getKeywordStartingWith("$uppercaseWord $adjacentWord")
                matchingUserKeyword = userKeywordDao?.getKeywordStartingWith("$uppercaseWord $adjacentWord")

                if (matchingKeyword != null || matchingUserKeyword != null) {
                    uppercaseWord = "$uppercaseWord $adjacentWord"
                    //remove the indexes of the matched multi-length keywords, and move onto the next
                    end = endNext
                    uppercaseSpanStartsNoMLK[j] = -1
                    uppercaseSpanEndsNoMLK[j] = -1
                } else {
                    //no match was found, move onto next
                    break
                }
            }

            //we found a multi-length keyword
            if (uppercaseWord.contains(" ")) {
                uppercaseSpanStartsNoMLK[i] = -1
                uppercaseSpanEndsNoMLK[i] = -1
                var span: ClickableSpan? = null
                //prioritise user keyword
                if (matchingUserKeyword != null) {
                    span = getSpanForKeyword(matchingUserKeyword)
                } else if (matchingKeyword != null) {
                    span = getSpanForKeyword(matchingKeyword)

                }
                if (span != null) {
                    formattedStory.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }

        //make uppercase which match a keyword or user keyword clickable
        for (i in uppercaseSpanStartsNoMLK.indices) {
            val start = uppercaseSpanStartsNoMLK[i]
            if (start == -1) {
                //this means an index which was removed because it was a multi word keyword
                continue
            }
            val end = uppercaseSpanEndsNoMLK[i]
            val uppercaseWord = storyText.substring(start, end)
            if (uppercaseWord.length < 2) {
                //ignore words of length 1 for example "I"
                continue
            }
            val matchingKeyword = keywordDao?.getKeywordMatching(uppercaseWord)
            val matchingUserKeyword = userKeywordDao?.getKeywordMatching(uppercaseWord)
            var span: ClickableSpan? = null
            //prioritise user keyword
            if (matchingUserKeyword != null) {
                span = getSpanForKeyword(matchingUserKeyword)
            } else if (matchingKeyword != null) {
                span = getSpanForKeyword(matchingKeyword)

            }
            if (span != null) {
                formattedStory.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        //make all kanji which were surrounded by braces clickable
        for (braceSpanStart in braceSpanStarts) {
            val kanjiIndex = braceSpanStart.index - braceSpanStart.order
            val kanji = storyText[kanjiIndex].toString()
            if (kanjiToHeisigId.containsKey(kanji)) {
                val heisigId = kanjiToHeisigId[kanji]
                val span = heisigId?.let { getSpanForHeisigId(it) }
                formattedStory.setSpan(span, kanjiIndex, kanjiIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        val keywordLength = if (userKeyword.isNullOrBlank()) originalKeyword!!.length else userKeyword!!.length
        //mark keyword occurances as bold
        for (start in keywordSpanStarts) {
            if (keywordLength < 2) {
                //ignore words of length 1 for example "I"
                continue
            }
            context?.let {
                formattedStory.setSpan(TextAppearanceSpan(context, R.style.storyHash),
                        start, start + keywordLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return formattedStory
    }

    private fun getSpanForHeisigId(heisigId: Int): ClickableSpan {
        return object : ClickableSpan() {
            override fun onClick(view: View) {
                val intent = Intent(activity, KanjiDetailActivity::class.java)
                intent.putExtra("filteredListIndex", 0)
                intent.putExtra("filteredIdList", arrayOf(heisigId.toString()))
                //TODO : startActivityForResult just like in KanjiListAdapter so that any changes made is passed back to KanjiListActivity
                activity?.startActivity(intent)
            }
        }
    }

    private fun getSpanForKeyword(matchingKeyword: Keyword): ClickableSpan {
        return getSpanForHeisigId(matchingKeyword.heisigId)
    }

    private fun getSpanForKeyword(matchingKeyword: UserKeyword): ClickableSpan {
        return getSpanForHeisigId(matchingKeyword.heisigId)
    }

    private fun submitKeyword(heisigId: Int, keywordText: String, update: Boolean) {
        val dataSource = context?.let { AppDatabase.getDatabase(it).userKeywordDao() }
        val keyword = UserKeyword(heisigId, keywordText)

        launch(Dispatchers.IO) {
            dataSource?.insertKeywords(keyword)
        }

        userKeyword = keywordText
        updateKeywordButton()
        updateParentActivity()
    }

    private fun deleteKeyword(heisigId: Int) {
        val dataSource = context?.let { AppDatabase.getDatabase(it).userKeywordDao() }
        launch(Dispatchers.IO) {
            if (dataSource?.deleteKeyword(heisigId) != null) {
                userKeyword = null
                launch(Dispatchers.Main) {
                    updateKeywordButton()
                    updateParentActivity()
                    Toast.makeText(context, "Keyword Reset", Toast.LENGTH_SHORT).show()
                }
            } else {
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Error! Keyword not Changed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getStoryFromDatabase(heisigId: Int): LiveData<String?> {
        val data = MutableLiveData<String>()
        launch(Dispatchers.IO) {
            val dataSource = context?.let { AppDatabase.getDatabase(it).storyDao() }
            data.postValue(dataSource?.getStoryForHeisigKanjiId(heisigId)?.storyText)
        }
        return data
    }

    private fun updateKeywordButton() {
        if (userKeyword.isNullOrBlank()) {
            keyword_detail?.text = originalKeyword
        } else {
            val keywordText = SpannableString("$userKeyword ($originalKeyword)")
            keywordText.setSpan(TextAppearanceSpan(activity, R.style.GreyItalicSmallText), userKeyword!!.length, keywordText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            keyword_detail.setText(keywordText, TextView.BufferType.SPANNABLE)
        }
    }

    /**
     * Tell the KanjiListActivity that user changed keyword(s) and it should update dataset of its Adapter
     */
    private fun updateParentActivity() {
        if (activity is KanjiDetailActivity) {
            if (userKeyword.isNullOrBlank()) {
                (activity as KanjiDetailActivity).setResult(heisigIdInt, originalKeyword!!)
            } else {
                (activity as KanjiDetailActivity).setResult(heisigIdInt, userKeyword!!)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext.postValue(context)
    }

    private inner class StoryFormat
    //string will shorten when hashes and asterixes are removed
    //so use the order to adjust the index afterwards

    internal constructor(internal var index: Int, internal var order: Int)
}
