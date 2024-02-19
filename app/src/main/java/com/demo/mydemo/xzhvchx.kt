package com.demo.mydemo

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


class xzhvchx private constructor() {
    private var mContext: Context? = null
    private var mPref: SharedPreferences? = null
    fun getString(str: String?): String? {
        return mPref!!.getString(str, "")
    }

    fun init(context: Context?) {
        if (mContext == null) {
            mContext = context
        }
        if (mPref == null) {
            mPref = PreferenceManager.getDefaultSharedPreferences(mContext)
        }
    }

    fun getString(str: String?, str2: String?): String? {
        return mPref!!.getString(str, str2)
    }

    companion object {
        @Volatile
        private var mInstance: xzhvchx? = null
        val instance: xzhvchx?
            get() {
                if (mInstance == null) {
                    synchronized(xzhvchx::class.java) {
                        if (mInstance == null) {
                            mInstance = xzhvchx()
                        }
                    }
                }
                return mInstance
            }
    }
}