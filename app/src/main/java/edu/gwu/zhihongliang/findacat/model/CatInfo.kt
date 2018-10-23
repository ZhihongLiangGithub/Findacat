package edu.gwu.zhihongliang.findacat.model

import android.os.Parcelable
import edu.gwu.zhihongliang.findacat.Const
import edu.gwu.zhihongliang.findacat.model.schema.PetItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CatInfo(
        var id: String = "",
        var name: String = "",
        var sex: String = "",
        val breeds: MutableList<String> = mutableListOf(),
        var zip: String = "",
        var description: String = "",
        var photo: String = "",
        var email: String = ""
) : Parcelable {

    companion object {
        @JvmStatic
        fun adaptedFrom(petItem: PetItem): CatInfo? {
            return CatInfo().apply {
                id = petItem.id.t ?: return null
                name = petItem.name.t ?: return null
                description = petItem.description?.t ?: return null
                petItem.breeds.breed.forEach {
                    it.t?.let { breeds.add(it) } ?: return null
                }
                zip = petItem.contact.zip.t ?: return null
                email = petItem.contact.email.t ?: return null
                sex = petItem.sex.t ?: return null
                petItem.media.photos?.let {
                    photo = it.photo.sortedBy { Const.photoSizePriority[it.size] }[0].t
                } ?: return null
            }
        }
    }
}