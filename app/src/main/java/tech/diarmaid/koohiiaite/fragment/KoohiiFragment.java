package tech.diarmaid.koohiiaite.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import tech.diarmaid.koohiiaite.R;

/**
 * For display of information retrieved from Koohii.
 * Top 20 stories.
 * Favourited stories (if signed in)
 */
public class KoohiiFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        final String kanji = args.getString("kanji");

        View view = inflater.inflate(R.layout.fragment_detail_koohii, container, false);
        Button koohiiButton = view.findViewById(R.id.button_open_koohii);
        String buttonString = String.format(getString(R.string.button_koohii), kanji);
        koohiiButton.setText(buttonString);

        koohiiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = String.format("http://kanji.koohii.com/study/kanji/%s", kanji);
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        return view;
    }
}
