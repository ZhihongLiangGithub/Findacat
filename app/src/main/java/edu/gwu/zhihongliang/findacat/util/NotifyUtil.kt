package edu.gwu.zhihongliang.findacat.util

import android.content.Context
import android.widget.Toast
import edu.gwu.zhihongliang.findacat.R

object NotifyUtil {

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun internetNotConnected(context: Context) {
        Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
    }
}