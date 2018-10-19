package edu.gwu.zhihongliang.findacat.model.schema

import com.squareup.moshi.Json

/**
 * Created by Liang on 2018/10/19
 */

data class Status(@Json(name = "message") val message: Message,
                  @Json(name = "code") val code: StringWrapper)