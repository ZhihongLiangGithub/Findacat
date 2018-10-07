package edu.gwu.zhihongliang.findacat

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import edu.gwu.zhihongliang.findacat.model.schema.adapter.ObjectAsListJsonAdapterFactory

class MoshiManager {
    companion object {
        fun getInstance(): Moshi {
            return Moshi.Builder().add(KotlinJsonAdapterFactory())
                    .add(ObjectAsListJsonAdapterFactory())
                    .build()
        }
    }
}