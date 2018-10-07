package edu.gwu.zhihongliang.findacat

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import edu.gwu.zhihongliang.findacat.model.CatInfo


class PersistenceManager(private val context: Context) {

    private val KEY = "favourite"

    private val sharedPreferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun findAllFavouriteCats(): MutableList<CatInfo> {
        val catsJson = sharedPreferences.getString(KEY, "")
        return when (catsJson.isEmpty()) {
            true -> mutableListOf()
            else -> {
                val type = Types.newParameterizedType(MutableList::class.java, CatInfo::class.java)
                val adapter: JsonAdapter<MutableList<CatInfo>> = MoshiManager.getInstance().adapter(type)
                return adapter.fromJson(catsJson) as MutableList<CatInfo>
            }
        }
    }

    fun saveFavouriteCats(catInfoSet: MutableList<CatInfo>) {
        val type = Types.newParameterizedType(MutableList::class.java, CatInfo::class.java)
        val adapter: JsonAdapter<MutableList<CatInfo>> = MoshiManager.getInstance().adapter(type)
        val json = adapter.toJson(catInfoSet)
        sharedPreferences.edit().putString(KEY, json).apply()
    }
}