package tech.diarmaid.koohiiaite.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import tech.diarmaid.koohiiaite.R
import java.io.IOException

/**
 * For Primitives Grid accessed from Main Activity Settings menu
 */
class PrimitiveGridAdapter(private val mContext: Context) : BaseAdapter() {

    private var filenames: Array<String>? = null
    private val layoutInflater: LayoutInflater

    private val imageFolder = "primitive_images"

    internal class ViewHolderItem {
        var primitiveImage: ImageView? = null
        var primitiveLabel: TextView? = null
    }

    init {
        scanForImages()
        layoutInflater = LayoutInflater.from(mContext)
    }

    private fun scanForImages() {
        try {
            if (filenames == null) {
                filenames = mContext.assets.list(imageFolder)
            }
        } catch (e: IOException) {
            Log.e("PrimitiveGridAdapter", "scanForImages - Couldn't get images from primitive images folder")
        }

    }

    private fun getLabel(aFileName: String): String {
        var filename = aFileName
        //remove number at beginning and file extension
        filename = filename.substring(filename.indexOf("-") + 1, filename.lastIndexOf("."))

        val parts = filename.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (parts.size == 0)
            return splitCamelCase(filename)

        val sb = StringBuilder()

        for (part in parts) {
            sb.append(splitCamelCase(part))
            sb.append(", ")
        }
        //remove last comma space
        sb.delete(sb.length - 2, sb.length)
        return sb.toString()
    }

    //PartOfTheBody -> Part of the body
    private fun splitCamelCase(text: String): String {
        val split = text.split("(?=\\p{Upper})".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        if (split.isEmpty()) {
            return text
        }
        val sb = StringBuilder()
        for (s in split) {
            sb.append(s)
            sb.append(" ")
        }

        var spaced = sb.toString().trim { it <= ' ' }.toLowerCase()
        val uppercase = spaced[0].toString()
        spaced = spaced.replaceFirst(uppercase.toRegex(), uppercase.toUpperCase())
        return spaced
    }

    @Throws(IOException::class)
    private fun getImage(fileName: String): Drawable {
        // get input stream
        val ims = mContext.assets.open("$imageFolder/$fileName")
        // load image as Drawable
        val drawable = Drawable.createFromStream(ims, null)
        ims.close()
        return drawable
    }

    override fun getCount(): Int {
        return if (filenames != null) {
            filenames!!.size
        } else 0
    }

    override fun getItem(position: Int): Any {
        return filenames!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, aConvertView: View?, parent: ViewGroup): View {
        var convertView = aConvertView
        val viewHolder: ViewHolderItem
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_item_primitive, parent, false)
            viewHolder = ViewHolderItem()
            viewHolder.primitiveImage = convertView!!.findViewById(R.id.primitive_image)
            viewHolder.primitiveLabel = convertView.findViewById(R.id.primitive_label)

            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolderItem
        }

        val fileName = getItem(position) as String
        val label = getLabel(fileName)
        var image: Drawable? = null
        try {
            image = getImage(fileName)
        } catch (e: IOException) {
            Log.e("PrimitiveGridAdapter", "Couldn't set image into ImageView")
        }

        viewHolder.primitiveImage!!.setImageDrawable(image)
        viewHolder.primitiveLabel!!.text = label

        return convertView
    }
}
