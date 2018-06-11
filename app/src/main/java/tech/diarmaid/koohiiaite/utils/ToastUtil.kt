package tech.diarmaid.koohiiaite.utils

/**
 * Replacement for Android Toast class so I can programmatically apply themes
 */
object ToastUtil {
//    fun makeText(context: Context, text: String, duration: Int): Toast {
//        val inflater = LayoutInflater.from(context)
//        val activity = context as Activity
//        //TODO : This is broken, null pointer exception
//        val layout = inflater.inflate(R.layout.toast,
//                activity.findViewById<View>(R.id.custom_toast_layout) as ViewGroup)
//
//        // set a message
//        val textView = layout.findViewById<TextView>(R.id.toast_text)
//        textView.text = text
//
//        // Toast...
//        val toast = Toast(context)
//        toast.duration = duration
//        toast.view = layout
//        return toast
//    }
//
//    fun makeText(context: Context, resId: Int, duration: Int): Toast {
//        return makeText(context, context.resources.getText(resId).toString(), duration)
//    }
}
