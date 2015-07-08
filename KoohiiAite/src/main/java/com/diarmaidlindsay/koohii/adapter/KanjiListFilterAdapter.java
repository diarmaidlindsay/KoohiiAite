package com.diarmaidlindsay.koohii.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.diarmaidlindsay.koohii.R;
import com.diarmaidlindsay.koohii.activity.KanjiListActivity;
import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

/**
 * Adapter for the spinner in the kanji list
 */
public class KanjiListFilterAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private LayoutInflater layoutInflater;
    private ViewHolderItem viewHolder;

    private FilterState joyoFilter = FilterState.UNSET;
    private FilterState keywordFilter = FilterState.UNSET;
    private FilterState storyFilter = FilterState.UNSET;

    public enum FilterState {
        UNSET(0),
        YES(1),
        NO(2);

        private final int stateNum;

        FilterState(int stateNum) {
            this.stateNum = stateNum;
        }

        public static FilterState getStateFor(int stateNum) {
            for (FilterState state : values()) {
                if (state.stateNum == stateNum) {
                    return state;
                }
            }
            Log.e("FilterState", "Couldn't find given filter state : " + stateNum);
            return UNSET;
        }

        public int getStateNum() {
            return stateNum;
        }
    }

    public KanjiListFilterAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        layoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    static class ViewHolderItem {
        MultiStateToggleButton joyoButton;
        MultiStateToggleButton keywordButton;
        MultiStateToggleButton storyButton;
    }

    @Override
    public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
        return getCustomView(position, cnvtView, prnt);
    }

    @Override
    public View getView(int pos, View cnvtView, ViewGroup prnt) {
        TextView text = new TextView(getContext());
        text.setText("Filter...");
        text.setTextColor(Color.WHITE);
        return text;
    }

    public View getCustomView(int position, View convertView,
                              ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.spinner_filter, parent, false);
            viewHolder = new ViewHolderItem();
            viewHolder.joyoButton = (MultiStateToggleButton) convertView.findViewById(R.id.toggle_joyo);
            viewHolder.keywordButton = (MultiStateToggleButton) convertView.findViewById(R.id.toggle_keyword);
            viewHolder.storyButton = (MultiStateToggleButton) convertView.findViewById(R.id.toggle_story);
            viewHolder.joyoButton.setValue(joyoFilter.getStateNum());
            viewHolder.keywordButton.setValue(keywordFilter.getStateNum());
            viewHolder.storyButton.setValue(storyFilter.getStateNum());

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        viewHolder.joyoButton.setOnValueChangedListener(
                new ToggleButton.OnValueChangedListener() {
                    @Override
                    public void onValueChanged(int i) {
                        joyoFilter = FilterState.getStateFor(i);
                        if (mContext instanceof KanjiListActivity) {
                            ((KanjiListActivity) mContext).notifyFilterChanged();
                        }
                    }
                });

        viewHolder.keywordButton.setOnValueChangedListener(
                new ToggleButton.OnValueChangedListener() {
                    @Override
                    public void onValueChanged(int i) {
                        keywordFilter = FilterState.getStateFor(i);
                        if (mContext instanceof KanjiListActivity) {
                            ((KanjiListActivity) mContext).notifyFilterChanged();
                        }
                    }
                }
        );

        viewHolder.storyButton.setOnValueChangedListener(
                new ToggleButton.OnValueChangedListener() {
                    @Override
                    public void onValueChanged(int i) {
                        storyFilter = FilterState.getStateFor(i);
                        if (mContext instanceof KanjiListActivity) {
                            ((KanjiListActivity) mContext).notifyFilterChanged();
                        }
                    }
                }
        );

        return convertView;
    }

    public FilterState getJoyoFilter() {
        return joyoFilter;
    }

    public FilterState getKeywordFilter() {
        return keywordFilter;
    }

    public FilterState getStoryFilter() {
        return storyFilter;
    }

    public void setJoyoFilter(Integer value) {
        joyoFilter = FilterState.getStateFor(value);
    }

    public void setKeywordFilter(Integer value) {
        keywordFilter = FilterState.getStateFor(value);
    }

    public void setStoryFilter(Integer value) {
        storyFilter = FilterState.getStateFor(value);
    }
}
