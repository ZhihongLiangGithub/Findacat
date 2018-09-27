package edu.gwu.zhihongliang.findacat

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object MoshiManager {
    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory())
            .add(ObjectAsListJsonAdapterFactory())
            .build()

    fun getInstance() = moshi
}