package edu.gwu.zhihongliang.findacat.model.schema

import com.squareup.moshi.Json

/**
 * Created by Liang on 2018/10/19
 */

data class Header(@Json(name = "status") val status: Status)