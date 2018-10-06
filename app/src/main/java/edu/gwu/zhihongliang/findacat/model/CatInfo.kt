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
        fun adaptedFrom(petItem: PetItem): CatInfo {
            val catInfo = CatInfo()
            catInfo.id = petItem.id.t
            catInfo.name = petItem.name.t
            catInfo.description = petItem.description.t
            catInfo.breeds.addAll(petItem.breeds.breed.map { it.t })
            catInfo.zip = petItem.contact.zip.t
            catInfo.email = petItem.contact.email.t
            catInfo.sex = if (petItem.sex.t == Sex.M.value) Sex.M else Sex.F
            catInfo.photo = petItem.media.photos.photo.find { it.size == PhotoSize.X.value }?.t
                    ?: petItem.media.photos.photo.find { it.size == PhotoSize.PN.value }?.t
                    ?: petItem.media.photos.photo.find { it.size == PhotoSize.FPM.value }?.t
                    ?: petItem.media.photos.photo.find { it.size == PhotoSize.PNT.value }?.t
                    ?: petItem.media.photos.photo.find { it.size == PhotoSize.T.value }?.t
                    ?: petItem.media.photos.photo[0].t
            return catInfo
        }
    }


    enum class Sex(val value: String) {
        M("M"), F("F")
    }

    enum class PhotoSize(val value: String) {
        PNT("pnt"), FPM("fpm"), X("x"), PN("pn"), T("t")
    }
}