package com.demo.mydemo

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
class xgcvgc(var f8267a: Context) {
    var f8268b: SharedPreferences
    init {
        f8268b = PreferenceManager.getDefaultSharedPreferences(f8267a)
        f8268b = f8267a.getSharedPreferences(MyPREFERENCES, 0)
    }
    fun getIntSharedPreferences(str: String?): Int {
        return f8268b.getInt(str, 0)
    }
    companion object {
        var MyPREFERENCES = "MyPrefs"
    }
}
