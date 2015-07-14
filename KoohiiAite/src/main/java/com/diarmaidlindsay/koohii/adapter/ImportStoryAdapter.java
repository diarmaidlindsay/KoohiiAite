package com.diarmaidlindsay.koohii.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.diarmaidlindsay.koohii.R;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the List View in the Activity which displays imported CSV
 */
public class ImportStoryAdapter extends BaseAdapter {
    private ViewHolderItem viewHolder;
    private Context mContext;

    List<CSVEntry> stories = new ArrayList<>();

    private LayoutInflater layoutInflater;

    public ImportStoryAdapter(Context context) {
        this.mContext = context;
        layoutInflater = LayoutInflater.from(context);
    }

    private class CSVEntry {
        String id;
        String kanji;
        String keyword;
        String publicFlag;
        String lastEdited;
        String story;

        public CSVEntry(String id, String kanji, String keyword, String publicFlag, String lastEdited, String story) {
            this.id = id;
            this.kanji = kanji;
            this.keyword = keyword;
            this.publicFlag = publicFlag;
            this.lastEdited = lastEdited;
            this.story = story;
        }
    }

    static class ViewHolderItem {
        TextView id;
        TextView kanji;
        TextView keyword;
        TextView story;
    }

    @Override
    public int getCount() {
        return stories.size();
    }

    @Override
    public Object getItem(int position) {
        return stories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_import_story, parent, false);
            viewHolder = new ViewHolderItem();
            viewHolder.id = (TextView) convertView.findViewById(R.id.csv_id);
            viewHolder.kanji = (TextView) convertView.findViewById(R.id.csv_kanji);
            viewHolder.keyword = (TextView) convertView.findViewById(R.id.csv_keyword);
            viewHolder.story = (TextView) convertView.findViewById(R.id.csv_story);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        CSVEntry entry = (CSVEntry) getItem(position);

        viewHolder.id.setText(entry.id);
        viewHolder.kanji.setText(entry.kanji);
        viewHolder.keyword.setText(entry.keyword);
        viewHolder.story.setText(entry.story);

        return convertView;
    }

    /**
     * Populate adapter table with the given my_stories.csv
     *
     * @return true if file successfully read
     */
    public boolean readCSVFile(File csvFile) {
        BufferedReader br = null;
        String line;
        String csvSplitBy = ",";

        stories.clear();
        try {

            br = new BufferedReader(new FileReader(csvFile));

            while ((line = br.readLine()) != null) {

                //match maximum of 5 commas
                String[] row = line.split(csvSplitBy, 5);
                if(row.length == 6) {
                    stories.add(new CSVEntry(row[0], row[1], row[2], row[3], row[4], row[5]));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    //noinspection ReturnInsideFinallyBlock
                    return false;
                }
            }
        }
        return true;
    }
}
