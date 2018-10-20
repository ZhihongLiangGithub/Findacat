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
                id = petItem.id.t
                name = petItem.name.t
                description = petItem.description?.t ?: return null
                breeds.addAll(petItem.breeds.breed.map { it.t })
                zip = petItem.contact.zip.t
                email = petItem.contact.email.t
                sex = petItem.sex.t
                petItem.media.photos?.let {
                    photo = it.photo.sortedBy { Const.photoSizePriority[it.size] }[0].t
                } ?: return null
            }
        }
    }
}