package edu.gwu.zhihongliang.findacat.util

object AddressUtil {
    fun extractZipCodeFromAddress(address: String): String {
        val regex1 = """
            \b[A-Z]{2}\s+\d{5}(-\d{4})?
        """.trimIndent().toRegex() //match "VA 22202"
        val regex2 = """
            \d{5}(-\d{4})?
        """.trimIndent().toRegex() //match "22202"
        val stateCodeAndZipCode = regex1.find(address)
        if (stateCodeAndZipCode != null) {
            val zipCode = regex2.find(stateCodeAndZipCode.value)
            return zipCode?.value ?: ""
        } else return ""
    }
}