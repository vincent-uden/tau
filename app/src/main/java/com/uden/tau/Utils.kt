package com.uden.tau

import android.util.Log

object ULog {
    fun d(msg: Any?, stackTraceLevels: Int = 1) {
        var prefix = ""
        for (i in (1..stackTraceLevels).reversed()) {
            val frame = Thread.currentThread().stackTrace.getOrNull(i+2)
            val frameName = frame?.fileName ?: ""
            val lineNumber = frame?.lineNumber.toString()

            prefix += "$frameName:$lineNumber->"
        }
        Log.d("ULog", "$prefix $msg")
    }
}
