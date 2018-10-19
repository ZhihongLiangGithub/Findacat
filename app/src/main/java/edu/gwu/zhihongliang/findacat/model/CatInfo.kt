package edu.gwu.zhihongliang.findacat.model

import android.os.Parcelable
import edu.gwu.zhihongliang.findacat.model.schema.PetItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CatInfo(
        var id: String = "",
        var name: String = "",
        var sex: Sex = Sex.M,
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
                sex = if (petItem.sex.t == Sex.M.value) Sex.M else Sex.F
                petItem.media.photos?.let {
                    photo = it.photo.sortedBy { PhotoSize.valueOf(it.size.toUpperCase()).priority }[0].t
                } ?: return null
            }
        }
    }


    enum class Sex(val value: String) {
        M("M"), F("F")
    }

    enum class PhotoSize(val value: String, val priority: Int) {
        X("x", 1),
        PN("pn", 2),
        FPM("fpm", 3),
        PNT("pnt", 4),
        T("t", 5)
    }
}