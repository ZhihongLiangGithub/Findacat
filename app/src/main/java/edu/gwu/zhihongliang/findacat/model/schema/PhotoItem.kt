package edu.gwu.zhihongliang.findacat.model.schema

import com.squareup.moshi.Json

data class PhotoItem(
        @Json(name = "@size") val size: String,
        @Json(name = "\$t") val t: String,
        @Json(name = "@id") val id: String
)