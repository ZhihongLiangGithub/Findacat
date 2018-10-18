package edu.gwu.zhihongliang.findacat.ui.fragment

import org.junit.Assert
import org.junit.Test

class FindFragmentTest {

    @Test
    fun regexMatching() {
        val address = "320 23rd St S, Arlington, VA 22202美国"
        val regex = """
            \b[A-Z]{2}\s+\d{5}(-\d{4})?
        """.trimIndent().toRegex()
        val result = regex.find(address)?.value
        Assert.assertNotNull(result)
    }
}