package com.demo.adslibsss.mylib.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.ads.adslib.R

class BannerAdView : LinearLayout {
    private var attrs: AttributeSet? = null
    private var mContext: Context? = null
    private var styleAttr = 0
    private var view: View? = null

    constructor(context: Context?) : super(context) {
        mContext = context
        initView()
    }
    constructor(context: Context?, attributeSet: AttributeSet?) :
            super(context, attributeSet) {
        mContext = context
        attrs = attributeSet
        initView()
    }
    constructor(context: Context?, attributeSet: AttributeSet?, styleAttr: Int) :
            super(context, attributeSet, styleAttr) {
        mContext = context
        attrs = attributeSet
        this.styleAttr = styleAttr
        initView()
    }

    private fun initView() {
        view = this
        inflate(mContext, R.layout.view_banner_ad, this)
    }
}
