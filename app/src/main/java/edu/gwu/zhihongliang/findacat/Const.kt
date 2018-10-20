package edu.gwu.zhihongliang.findacat

object Const {
    const val CATFACTS_URL = "https://catfact.ninja"
    const val PETFINDER_URL = "https://api.petfinder.com"
    const val PETFINDER_API_KEY = "b93c0a99c51866c16cc8e333eaa14af0"
    const val PETFINDER_RESPONSE_FORMAT = "json"
    const val LIST_IMAGE_SIZE = 500
    const val DETAIL_IMAGE_SIZE = 1000

    val photoSizePriority = hashMapOf(
            "x" to 1,
            "pn" to 2,
            "fpm" to 3,
            "pnt" to 4,
            "t" to 5
    )
}