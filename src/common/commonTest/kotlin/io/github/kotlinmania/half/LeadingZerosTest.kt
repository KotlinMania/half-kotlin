// port-lint: tests leading_zeros.rs
package io.github.kotlinmania.half

import kotlin.test.Test
import kotlin.test.assertEquals

class LeadingZerosTest {
    @Test
    fun leadingZerosU16FallbackMatchesBuiltin() {
        for (x in listOf(44u, 97u, 304u, 1179u, 23571u)) {
            val v = x.toUShort()
            assertEquals(v.countLeadingZeroBits().toUInt(), leadingZerosU16Fallback(v))
            assertEquals(leadingZerosU16(v), leadingZerosU16Fallback(v))
        }
        assertEquals(16u, leadingZerosU16Fallback(0u))
        assertEquals(0u, leadingZerosU16Fallback(0x8000u))
    }
}
