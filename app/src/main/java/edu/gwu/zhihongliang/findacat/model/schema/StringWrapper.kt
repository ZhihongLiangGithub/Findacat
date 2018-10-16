package edu.gwu.zhihongliang.findacat.model.schema

import com.squareup.moshi.Json

data class StringWrapper(@Json(name = "\$t") val t: String = "")