package tech.diarmaid.koohiiaite.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

import tech.diarmaid.koohiiaite.R;
import tech.diarmaid.koohiiaite.activity.KanjiListActivity;

/**
 * Adapter for the spinner in the kanji list
 */
public class KanjiListFilterAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private LayoutInflater layoutInflater;

    private FilterState joyoFilter = FilterState.UNSET;
    private FilterState keywordFilter = FilterState.UNSET;
    private FilterState storyFilter = FilterState.UNSET;

    public KanjiListFilterAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        layoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public View getDropDownView(int position, View cnvtView, @NonNull ViewGroup prnt) {
        return getCustomView(position, cnvtView, prnt);
    }

    @Override
    public View getView(int pos, View cnvtView, @NonNull ViewGroup prnt) {
        TextView text = new TextView(getContext());
        text.setText(R.string.filter_hint);
        return text;
    }

    public View getCustomView(int position, View convertView,
                              ViewGroup parent) {
        ViewHolderItem viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.spinner_filter, parent, false);
            viewHolder = new ViewHolderItem();
            viewHolder.joyoButton = convertView.findViewById(R.id.toggle_joyo);
            viewHolder.keywordButton = convertView.findViewById(R.id.toggle_keyword);
            viewHolder.storyButton = convertView.findViewById(R.id.toggle_story);
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

    public void setJoyoFilter(Integer value) {
        joyoFilter = FilterState.getStateFor(value);
    }

    public FilterState getKeywordFilter() {
        return keywordFilter;
    }

    public void setKeywordFilter(Integer value) {
        keywordFilter = FilterState.getStateFor(value);
    }

    public FilterState getStoryFilter() {
        return storyFilter;
    }

    public void setStoryFilter(Integer value) {
        storyFilter = FilterState.getStateFor(value);
    }

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

    static class ViewHolderItem {
        MultiStateToggleButton joyoButton;
        MultiStateToggleButton keywordButton;
        MultiStateToggleButton storyButton;
    }
}
