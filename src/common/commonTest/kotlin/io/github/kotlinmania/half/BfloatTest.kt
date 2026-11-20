// port-lint: tests bfloat.rs
package io.github.kotlinmania.half

import kotlin.math.E
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BfloatTest {
    // Optional `num-traits` trait methods (`as_primitive`, `to_primitive`, `from_primitive`) are
    // outside default Cargo features; byte conversions and quickcheck round-trips are covered below.

    @Test
    fun toAndFromBytes() {
        val two = BF16.fromF32(2.0f)
        assertContentEquals(byteArrayOf(0, 64), two.toLeBytes())
        assertEquals(two, BF16.fromLeBytes(byteArrayOf(0, 64)))
        assertContentEquals(byteArrayOf(64, 0), two.toBeBytes())
        assertEquals(two, BF16.fromBeBytes(byteArrayOf(64, 0)))
    }

    @Test
    fun testBf16ConstsFromF32() {
        val one = BF16.fromF32(1.0f)
        val zero = BF16.fromF32(0.0f)
        val negZero = BF16.fromF32(-0.0f)
        val negOne = BF16.fromF32(-1.0f)
        val inf = BF16.fromF32(Float.POSITIVE_INFINITY)
        val negInf = BF16.fromF32(Float.NEGATIVE_INFINITY)
        val nan = BF16.fromF32(Float.NaN)

        assertEquals(BF16.ONE, one)
        assertEquals(BF16.ZERO, zero)
        assertTrue(zero.isSignPositive())
        assertEquals(BF16.NEG_ZERO, negZero)
        assertTrue(negZero.isSignNegative())
        assertEquals(BF16.NEG_ONE, negOne)
        assertTrue(negOne.isSignNegative())
        assertEquals(BF16.INFINITY, inf)
        assertEquals(BF16.NEG_INFINITY, negInf)
        assertTrue(nan.isNan())
        assertTrue(BF16.NAN.isNan())

        assertEquals(BF16.E, BF16.fromF32(E.toFloat()))
        assertEquals(BF16.PI, BF16.fromF32(PI.toFloat()))
        assertEquals(BF16.FRAC_1_PI, BF16.fromF32((1.0 / PI).toFloat()))
        assertEquals(BF16.FRAC_1_SQRT_2, BF16.fromF32((1.0 / sqrt(2.0)).toFloat()))
        assertEquals(BF16.FRAC_2_PI, BF16.fromF32((2.0 / PI).toFloat()))
        assertEquals(BF16.FRAC_2_SQRT_PI, BF16.fromF32((2.0 / sqrt(PI)).toFloat()))
        assertEquals(BF16.FRAC_PI_2, BF16.fromF32((PI / 2.0).toFloat()))
        assertEquals(BF16.FRAC_PI_3, BF16.fromF32((PI / 3.0).toFloat()))
        assertEquals(BF16.FRAC_PI_4, BF16.fromF32((PI / 4.0).toFloat()))
        assertEquals(BF16.FRAC_PI_6, BF16.fromF32((PI / 6.0).toFloat()))
        assertEquals(BF16.FRAC_PI_8, BF16.fromF32((PI / 8.0).toFloat()))
        assertEquals(BF16.LN_10, BF16.fromF32(ln(10.0f)))
        assertEquals(BF16.LN_2, BF16.fromF32(ln(2.0f)))
        assertEquals(BF16.LOG10_E, BF16.fromF32(log10(E.toFloat())))
        assertEquals(BF16.LOG10_2, BF16.fromF32(log10(2.0f)))
        assertEquals(BF16.LOG2_E, BF16.fromF32(log2(E.toFloat())))
        assertEquals(BF16.LOG2_10, BF16.fromF32(log2(10.0f)))
        assertEquals(BF16.SQRT_2, BF16.fromF32(sqrt(2.0f)))
    }

    @Test
    fun testBf16ConstsFromF64() {
        val one = BF16.fromF64(1.0)
        val zero = BF16.fromF64(0.0)
        val negZero = BF16.fromF64(-0.0)
        val inf = BF16.fromF64(Double.POSITIVE_INFINITY)
        val negInf = BF16.fromF64(Double.NEGATIVE_INFINITY)
        val nan = BF16.fromF64(Double.NaN)

        assertEquals(BF16.ONE, one)
        assertEquals(BF16.ZERO, zero)
        assertTrue(zero.isSignPositive())
        assertEquals(BF16.NEG_ZERO, negZero)
        assertTrue(negZero.isSignNegative())
        assertEquals(BF16.INFINITY, inf)
        assertEquals(BF16.NEG_INFINITY, negInf)
        assertTrue(nan.isNan())
        assertTrue(BF16.NAN.isNan())

        assertEquals(BF16.E, BF16.fromF64(E))
        assertEquals(BF16.PI, BF16.fromF64(PI))
        assertEquals(BF16.FRAC_1_PI, BF16.fromF64(1.0 / PI))
        assertEquals(BF16.FRAC_1_SQRT_2, BF16.fromF64(1.0 / sqrt(2.0)))
        assertEquals(BF16.FRAC_2_PI, BF16.fromF64(2.0 / PI))
        assertEquals(BF16.FRAC_2_SQRT_PI, BF16.fromF64(2.0 / sqrt(PI)))
        assertEquals(BF16.FRAC_PI_2, BF16.fromF64(PI / 2.0))
        assertEquals(BF16.FRAC_PI_3, BF16.fromF64(PI / 3.0))
        assertEquals(BF16.FRAC_PI_4, BF16.fromF64(PI / 4.0))
        assertEquals(BF16.FRAC_PI_6, BF16.fromF64(PI / 6.0))
        assertEquals(BF16.FRAC_PI_8, BF16.fromF64(PI / 8.0))
        assertEquals(BF16.LN_10, BF16.fromF64(ln(10.0)))
        assertEquals(BF16.LN_2, BF16.fromF64(ln(2.0)))
        assertEquals(BF16.LOG10_E, BF16.fromF64(log10(E)))
        assertEquals(BF16.LOG10_2, BF16.fromF64(log10(2.0)))
        assertEquals(BF16.LOG2_E, BF16.fromF64(log2(E)))
        assertEquals(BF16.LOG2_10, BF16.fromF64(log2(10.0)))
        assertEquals(BF16.SQRT_2, BF16.fromF64(sqrt(2.0)))
    }

    @Test
    fun testNanConversionToSmaller() {
        val nan64 = Double.fromBits(0x7FF0_0000_0000_0001L)
        val negNan64 = Double.fromBits(-0x0010_0000_0000_0000L + 1L)
        val nan32 = Float.fromBits(0x7F80_0001)
        val negNan32 = Float.fromBits(-0x0080_0000 + 1)

        assertTrue(nan64.isNaN())
        assertTrue(negNan64.isNaN())
        assertTrue(nan32.isNaN())
        assertTrue(negNan32.isNaN())

        assertTrue(BF16.fromF64(nan64).isNan())
        assertTrue(BF16.fromF64(negNan64).isNan())
        assertTrue(BF16.fromF32(nan32).isNan())
        assertTrue(BF16.fromF32(negNan32).isNan())
    }

    @Test
    fun testNanConversionToLarger() {
        val nan16 = BF16.fromBits(0x7F81u)
        val negNan16 = BF16.fromBits(0xFF81u)

        assertTrue(nan16.isNan() && nan16.isSignPositive())
        assertTrue(negNan16.isNan() && negNan16.isSignNegative())

        assertTrue(nan16.toF32().isNaN())
        assertTrue(negNan16.toF32().isNaN())
        assertTrue(nan16.toF64().isNaN())
        assertTrue(negNan16.toF64().isNaN())
    }

    @Test
    fun testBf16ToF32() {
        val exact = BF16.fromF32(7.0f)
        assertEquals(7.0f, exact.toF32())

        val approx = BF16.fromF32(7.1f)
        val diff = abs(approx.toF32() - 7.1f)
        assertTrue(diff <= 4.0f * BF16.EPSILON.toF32())

        val tiny32 = Float.fromBits(0x0001_0000)
        assertEquals(tiny32, BF16.fromBits(0x0001u).toF32())
        assertEquals(5.0f * tiny32, BF16.fromBits(0x0005u).toF32())
        assertEquals(BF16.fromBits(0x0001u), BF16.fromF32(tiny32))
        assertEquals(BF16.fromBits(0x0005u), BF16.fromF32(5.0f * tiny32))
    }

    @Test
    fun testBf16ToF64() {
        val exact = BF16.fromF64(7.0)
        assertEquals(7.0, exact.toF64())

        val approx = BF16.fromF64(7.1)
        val diff = abs(approx.toF64() - 7.1)
        assertTrue(diff <= 4.0 * BF16.EPSILON.toF64())

        val tiny64 = 2.0.pow(-133)
        assertEquals(tiny64, BF16.fromBits(0x0001u).toF64())
        assertEquals(5.0 * tiny64, BF16.fromBits(0x0005u).toF64())
        assertEquals(BF16.fromBits(0x0001u), BF16.fromF64(tiny64))
        assertEquals(BF16.fromBits(0x0005u), BF16.fromF64(5.0 * tiny64))
    }

    @Test
    fun testComparisons() {
        val zero = BF16.fromF64(0.0)
        val one = BF16.fromF64(1.0)
        val negZero = BF16.fromF64(-0.0)
        val negOne = BF16.fromF64(-1.0)

        assertEquals(0, zero.compareTo(negZero))
        assertEquals(0, negZero.compareTo(zero))
        assertTrue(zero == negZero)
        assertTrue(negZero == zero)
        assertFalse(zero < negZero)
        assertTrue(zero <= negZero)
        assertFalse(zero > negZero)
        assertTrue(zero >= negZero)

        assertTrue(one.compareTo(negZero) > 0)
        assertTrue(negZero.compareTo(one) < 0)
        assertFalse(one == negZero)
        assertTrue(one > negZero)
        assertTrue(negZero < one)

        assertTrue(one.compareTo(negOne) > 0)
        assertTrue(negOne.compareTo(one) < 0)
        assertFalse(one == negOne)
        assertTrue(one > negOne)
        assertTrue(negOne < one)
    }

    @Test
    fun roundToEvenF32() {
        val minSub = BF16.fromBits(1u)
        val minSubF = 2.0f.pow(-133)
        assertEquals(minSub.toBits(), BF16.fromF32(minSubF).toBits())
        assertEquals(minSubF.toRawBits(), minSub.toF32().toRawBits())

        assertEquals(0u.toUShort(), BF16.fromF32(minSubF * 0.49f).toBits())
        assertEquals(0u.toUShort(), BF16.fromF32(minSubF * 0.50f).toBits())
        assertEquals(1u.toUShort(), BF16.fromF32(minSubF * 0.51f).toBits())

        assertEquals(1u.toUShort(), BF16.fromF32(minSubF * 1.49f).toBits())
        assertEquals(2u.toUShort(), BF16.fromF32(minSubF * 1.50f).toBits())
        assertEquals(2u.toUShort(), BF16.fromF32(minSubF * 1.51f).toBits())

        assertEquals(2u.toUShort(), BF16.fromF32(minSubF * 2.49f).toBits())
        assertEquals(2u.toUShort(), BF16.fromF32(minSubF * 2.50f).toBits())
        assertEquals(3u.toUShort(), BF16.fromF32(minSubF * 2.51f).toBits())

        assertEquals(BF16.fromF32(250.0f).toBits(), BF16.fromF32(250.49f).toBits())
        assertEquals(BF16.fromF32(250.0f).toBits(), BF16.fromF32(250.50f).toBits())
        assertEquals(BF16.fromF32(251.0f).toBits(), BF16.fromF32(250.51f).toBits())
        assertEquals(BF16.fromF32(251.0f).toBits(), BF16.fromF32(251.49f).toBits())
        assertEquals(BF16.fromF32(252.0f).toBits(), BF16.fromF32(251.50f).toBits())
        assertEquals(BF16.fromF32(252.0f).toBits(), BF16.fromF32(251.51f).toBits())
        assertEquals(BF16.fromF32(252.0f).toBits(), BF16.fromF32(252.49f).toBits())
        assertEquals(BF16.fromF32(252.0f).toBits(), BF16.fromF32(252.50f).toBits())
        assertEquals(BF16.fromF32(253.0f).toBits(), BF16.fromF32(252.51f).toBits())
    }

    @Test
    fun roundToEvenF64() {
        val minSub = BF16.fromBits(1u)
        val minSubF = 2.0.pow(-133)
        assertEquals(minSub.toBits(), BF16.fromF64(minSubF).toBits())
        assertEquals(minSubF.toRawBits(), minSub.toF64().toRawBits())

        assertEquals(0u.toUShort(), BF16.fromF64(minSubF * 0.49).toBits())
        assertEquals(0u.toUShort(), BF16.fromF64(minSubF * 0.50).toBits())
        assertEquals(1u.toUShort(), BF16.fromF64(minSubF * 0.51).toBits())

        assertEquals(1u.toUShort(), BF16.fromF64(minSubF * 1.49).toBits())
        assertEquals(2u.toUShort(), BF16.fromF64(minSubF * 1.50).toBits())
        assertEquals(2u.toUShort(), BF16.fromF64(minSubF * 1.51).toBits())

        assertEquals(2u.toUShort(), BF16.fromF64(minSubF * 2.49).toBits())
        assertEquals(2u.toUShort(), BF16.fromF64(minSubF * 2.50).toBits())
        assertEquals(3u.toUShort(), BF16.fromF64(minSubF * 2.51).toBits())

        assertEquals(BF16.fromF64(250.0).toBits(), BF16.fromF64(250.49).toBits())
        assertEquals(BF16.fromF64(250.0).toBits(), BF16.fromF64(250.50).toBits())
        assertEquals(BF16.fromF64(251.0).toBits(), BF16.fromF64(250.51).toBits())
        assertEquals(BF16.fromF64(251.0).toBits(), BF16.fromF64(251.49).toBits())
        assertEquals(BF16.fromF64(252.0).toBits(), BF16.fromF64(251.50).toBits())
        assertEquals(BF16.fromF64(252.0).toBits(), BF16.fromF64(251.51).toBits())
        assertEquals(BF16.fromF64(252.0).toBits(), BF16.fromF64(252.49).toBits())
        assertEquals(BF16.fromF64(252.0).toBits(), BF16.fromF64(252.50).toBits())
        assertEquals(BF16.fromF64(253.0).toBits(), BF16.fromF64(252.51).toBits())
    }

    @Test
    fun formatting() {
        // Kotlin common stdlib lacks Rust Display width/precision formatting specifiers; verify toString().
        val f = BF16.fromF32(0.1152344f)
        assertEquals(f.toF32().toString(), f.toString())
        assertTrue(f.toString().startsWith("0.1152"))
    }

    @Test
    fun roundtripBf16F32AndF64Exhaustive() {
        for (bits in 0..0xFFFF) {
            val f = BF16.fromBits(bits.toUShort())
            val rt32 = BF16.fromF32(f.toF32())
            val rt64 = BF16.fromF64(f.toF64())
            if (f.isNan()) {
                assertTrue(rt32.isNan())
                assertEquals(f.isSignNegative(), rt32.isSignNegative())
                assertTrue(rt64.isNan())
                assertEquals(f.isSignNegative(), rt64.isSignNegative())
            } else {
                assertEquals(f.rawBits, rt32.rawBits)
                assertEquals(f.rawBits, rt64.rawBits)
            }
        }
    }
}
