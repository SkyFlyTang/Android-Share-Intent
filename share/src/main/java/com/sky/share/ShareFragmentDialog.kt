package com.sky.share

import android.app.Dialog
import android.app.DialogFragment
import android.app.FragmentManager
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class ShareFragmentDialog : DialogFragment() {

    private var shareItem: ShareItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shareItem = arguments.getParcelable(EX_INTENT)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val bottomSheetDialog = BottomSheetDialog(activity)
        val view = View.inflate(activity, R.layout.bottom_dialog, null)
        val gvContent = view.findViewById<GridView>(R.id.bs_gv_content)
        lateinit var list: List<ResolveInfo>
        shareItem?.let {
            list = activity.packageManager.queryIntentActivities(it.intent, 0)
            gvContent.adapter = ShareAdapter(activity, list)
            gvContent.setOnItemClickListener { parent, view, position, id ->
                it.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                it.intent.setClassName(list[position].activityInfo.applicationInfo.packageName,
                        list[position].activityInfo.name)
                activity.startActivity(it.intent)
            }
        }
        bottomSheetDialog.setContentView(view)
        if (list.isEmpty()) {
            dismiss()
            Toast.makeText(activity, R.string.toast_failure, Toast.LENGTH_SHORT).show()
        }

        return bottomSheetDialog
    }

    companion object {

        private const val EX_INTENT = "INTENT"
        private const val TAG = "Bottom_dialog"
        private fun newInstance(shareItem: ShareItem): ShareFragmentDialog {
            val args = Bundle()
            args.putParcelable(EX_INTENT, shareItem)
            val fragment = ShareFragmentDialog()
            fragment.arguments = args
            return fragment
        }

        fun shareIntent(fragmentManager: FragmentManager, intent: Intent) {
            val dialog = ShareFragmentDialog.newInstance(ShareItem(intent))
            dialog.show(fragmentManager, TAG)
        }
    }

    inner class ShareAdapter(private val context: Context, private val data: List<ResolveInfo>) : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

            lateinit var viewHolder: ViewHolder
            var view = convertView
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false)
                viewHolder = ViewHolder(view.findViewById(R.id.bs_list_image), view.findViewById(R.id.bs_list_title))
                view.tag = viewHolder
            } else {
                viewHolder = view.tag as ViewHolder
            }
            viewHolder.imageView.setImageDrawable(data[position].loadIcon(context.packageManager))
            viewHolder.textView.text = data[position].loadLabel(context.packageManager)
            return view
        }

        override fun getItem(position: Int): Any {
            return data[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return data.size
        }
    }

    inner class ViewHolder(var imageView: ImageView, var textView: TextView)
}

data class ShareItem(val intent: Intent) : Parcelable {
    constructor(source: Parcel) : this(
            source.readParcelable<Intent>(Intent::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(intent, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ShareItem> = object : Parcelable.Creator<ShareItem> {
            override fun createFromParcel(source: Parcel): ShareItem = ShareItem(source)
            override fun newArray(size: Int): Array<ShareItem?> = arrayOfNulls(size)
        }
    }
}




