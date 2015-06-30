package com.diarmaidlindsay.koohii.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.diarmaidlindsay.koohii.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * For Primitives Grid accessed from Main Activity Settings menu
 */
public class PrimitiveGridAdapter extends BaseAdapter {

    public String[] filenames;
    private ViewHolderItem viewHolder;
    private LayoutInflater layoutInflater;
    private Context mContext;

    private final String IMAGE_FOLDER = "primitive_images";

    static class ViewHolderItem {
        ImageView primitiveImage;
        TextView primitiveLabel;
    }

    public PrimitiveGridAdapter(Context context) {
        mContext = context;
        scanForImages();
        layoutInflater = LayoutInflater.from(context);
    }

    private void scanForImages() {
        try {
            if (filenames != null) {
                filenames = mContext.getAssets().list(IMAGE_FOLDER);
            }
        } catch (IOException e) {
            Log.e("PrimitiveGridAdapter", "scanForImages - Couldn't get images from primitive images folder");
        }
    }

    private String getLabel(String filename) {
        //remove number at beginning and file extension
        filename = filename.substring(filename.indexOf("-") + 1, filename.lastIndexOf("."));

        String[] parts = filename.split("-");
        if (parts.length == 0)
            return splitCamelCase(filename);

        StringBuilder sb = new StringBuilder();

        for (String part : parts) {
            sb.append(splitCamelCase(part));
            sb.append(", ");
        }
        //remove last comma space
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }

    //PartOfTheBody -> Part of the body
    private String splitCamelCase(String text) {
        String[] split =
                text.split("(?=\\p{Upper})");

        if (split.length == 0) {
            return text;
        }
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(s);
            sb.append(" ");
        }

        String spaced = sb.toString().trim().toLowerCase();
        String uppercase = String.valueOf(spaced.charAt(0));
        spaced = spaced.replaceFirst(uppercase, uppercase.toUpperCase());
        return spaced;
    }

    private Drawable getImage(String fileName) throws IOException {
        // get input stream
        InputStream ims = mContext.getAssets().open(IMAGE_FOLDER + "/" + fileName);
        // load image as Drawable
        Drawable drawable = Drawable.createFromStream(ims, null);
        ims.close();
        return drawable;
    }

    @Override
    public int getCount() {
        return filenames.length;
    }

    @Override
    public Object getItem(int position) {
        return filenames[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_item_primitive, parent, false);
            viewHolder = new ViewHolderItem();
            viewHolder.primitiveImage = (ImageView) convertView.findViewById(R.id.primitive_image);
            viewHolder.primitiveLabel = (TextView) convertView.findViewById(R.id.primitive_label);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        String fileName = (String) getItem(position);
        String label = getLabel(fileName);
        Drawable image = null;
        try {
            image = getImage(fileName);
        } catch (IOException e) {
            Log.e("PrimitiveGridAdapter", "Couldn't set image into ImageView");
        }
        viewHolder.primitiveImage.setImageDrawable(image);
        viewHolder.primitiveLabel.setText(label);

        return convertView;
    }
}
