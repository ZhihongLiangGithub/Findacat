package edu.gwu.zhihongliang.findacat

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import edu.gwu.zhihongliang.findacat.model.CatInfo


class PersistenceManager(private val context: Context) {

    private val TAG = "PersistenceManager"

    private val FAVOURITE = "favourite"
    private val ZIP = "zip"
    private val ZIP_DEFAULT = "22202"


    private val sharedPreferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun findAllFavouriteCats(): MutableList<CatInfo> {
        val catsJson = sharedPreferences.getString(FAVOURITE, "")
        return when (catsJson.isEmpty()) {
            true -> mutableListOf()
            else -> {
                val type = Types.newParameterizedType(MutableList::class.java, CatInfo::class.java)
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val adapter: JsonAdapter<MutableList<CatInfo>> = moshi.adapter(type)
                return adapter.fromJson(catsJson) as MutableList<CatInfo>
            }
        }
    }

    fun saveFavouriteCats(catInfoSet: MutableList<CatInfo>) {
        val type = Types.newParameterizedType(MutableList::class.java, CatInfo::class.java)
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val adapter: JsonAdapter<MutableList<CatInfo>> = moshi.adapter(type)
        val json = adapter.toJson(catInfoSet)
        sharedPreferences.edit().putString(FAVOURITE, json).apply()
    }

    fun saveZip(zip: String) {
        sharedPreferences.edit().putString(ZIP, zip).apply()
    }

    fun getZip(): String {
        var zip = sharedPreferences.getString(ZIP, "")
        return if (zip.isEmpty()) {
            Log.e(TAG, "unable to get last zip, return default: $ZIP_DEFAULT")
            return ZIP_DEFAULT
        } else zip
    }
}