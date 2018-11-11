package tech.diarmaid.koohiiaite.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import tech.diarmaid.koohiiaite.R

/**
 * For display of information retrieved from Koohii.
 * Top 20 stories.
 * Favourited stories (if signed in)
 */
class KoohiiFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val kanji = arguments?.getString("kanji")

        val view = inflater.inflate(R.layout.fragment_detail_koohii, container, false)
        val koohiiButton = view.findViewById<Button>(R.id.button_open_koohii)
        val buttonString = String.format(getString(R.string.button_koohii), kanji)
        koohiiButton.text = buttonString

        koohiiButton.setOnClickListener {
            val url = String.format("http://kanji.koohii.com/study/kanji/%s", kanji)
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        return view
    }
}
