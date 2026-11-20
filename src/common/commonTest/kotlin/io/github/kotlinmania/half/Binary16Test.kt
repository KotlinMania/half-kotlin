// port-lint: tests binary16.rs
package io.github.kotlinmania.half

import kotlin.math.E
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.floor
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

class Binary16Test {
    // Optional `num-traits` trait methods (`as_primitive`, `to_primitive`, `from_primitive`) are
    // outside default Cargo features; byte conversions and quickcheck round-trips are covered below.

    @Test
    fun toAndFromBytes() {
        val two = F16.fromF32(2.0f)
        assertContentEquals(byteArrayOf(0, 64), two.toLeBytes())
        assertEquals(two, F16.fromLeBytes(byteArrayOf(0, 64)))
        assertContentEquals(byteArrayOf(64, 0), two.toBeBytes())
        assertEquals(two, F16.fromBeBytes(byteArrayOf(64, 0)))
    }

    @Test
    fun testF16Consts() {
        val digits = floor((F16.MANTISSA_DIGITS.toFloat() - 1.0f) * log10(2.0f)).toUInt()
        assertEquals(F16.DIGITS, digits)

        val one = F16.fromF32(1.0f)
        val onePlusEpsilon = F16.fromBits((one.toBits().toUInt() + 1u).toUShort())
        val epsilon = F16.fromF32(onePlusEpsilon.toF32() - 1.0f)
        assertEquals(F16.EPSILON, epsilon)

        val max = F16.fromBits((F16.INFINITY.toBits().toUInt() - 1u).toUShort())
        val min = F16.fromBits((F16.NEG_INFINITY.toBits().toUInt() - 1u).toUShort())
        val minPos = F16.fromF32(2.0f.pow(F16.MIN_EXP - 1))
        assertEquals(F16.MAX, max)
        assertEquals(F16.MIN, min)
        assertEquals(F16.MIN_POSITIVE, minPos)

        val tenToMin = 10.0f.pow(F16.MIN_10_EXP)
        assertTrue(tenToMin / 10.0f < F16.MIN_POSITIVE.toF32())
        assertTrue(tenToMin > F16.MIN_POSITIVE.toF32())
        val tenToMax = 10.0f.pow(F16.MAX_10_EXP)
        assertTrue(tenToMax < F16.MAX.toF32())
        assertTrue(tenToMax * 10.0f > F16.MAX.toF32())
    }

    @Test
    fun testF16ConstsFromF32() {
        val one = F16.fromF32(1.0f)
        val zero = F16.fromF32(0.0f)
        val negZero = F16.fromF32(-0.0f)
        val negOne = F16.fromF32(-1.0f)
        val inf = F16.fromF32(Float.POSITIVE_INFINITY)
        val negInf = F16.fromF32(Float.NEGATIVE_INFINITY)
        val nan = F16.fromF32(Float.NaN)

        assertEquals(F16.ONE, one)
        assertEquals(F16.ZERO, zero)
        assertTrue(zero.isSignPositive())
        assertEquals(F16.NEG_ZERO, negZero)
        assertTrue(negZero.isSignNegative())
        assertEquals(F16.NEG_ONE, negOne)
        assertTrue(negOne.isSignNegative())
        assertEquals(F16.INFINITY, inf)
        assertEquals(F16.NEG_INFINITY, negInf)
        assertTrue(nan.isNan())
        assertTrue(F16.NAN.isNan())

        assertEquals(F16.E, F16.fromF32(E.toFloat()))
        assertEquals(F16.PI, F16.fromF32(PI.toFloat()))
        assertEquals(F16.FRAC_1_PI, F16.fromF32((1.0 / PI).toFloat()))
        assertEquals(F16.FRAC_1_SQRT_2, F16.fromF32((1.0 / sqrt(2.0)).toFloat()))
        assertEquals(F16.FRAC_2_PI, F16.fromF32((2.0 / PI).toFloat()))
        assertEquals(F16.FRAC_2_SQRT_PI, F16.fromF32((2.0 / sqrt(PI)).toFloat()))
        assertEquals(F16.FRAC_PI_2, F16.fromF32((PI / 2.0).toFloat()))
        assertEquals(F16.FRAC_PI_3, F16.fromF32((PI / 3.0).toFloat()))
        assertEquals(F16.FRAC_PI_4, F16.fromF32((PI / 4.0).toFloat()))
        assertEquals(F16.FRAC_PI_6, F16.fromF32((PI / 6.0).toFloat()))
        assertEquals(F16.FRAC_PI_8, F16.fromF32((PI / 8.0).toFloat()))
        assertEquals(F16.LN_10, F16.fromF32(ln(10.0f)))
        assertEquals(F16.LN_2, F16.fromF32(ln(2.0f)))
        assertEquals(F16.LOG10_E, F16.fromF32(log10(E.toFloat())))
        assertEquals(F16.LOG10_2, F16.fromF32(log10(2.0f)))
        assertEquals(F16.LOG2_E, F16.fromF32(log2(E.toFloat())))
        assertEquals(F16.LOG2_10, F16.fromF32(log2(10.0f)))
        assertEquals(F16.SQRT_2, F16.fromF32(sqrt(2.0f)))
    }

    @Test
    fun testF16ConstsFromF64() {
        val one = F16.fromF64(1.0)
        val zero = F16.fromF64(0.0)
        val negZero = F16.fromF64(-0.0)
        val inf = F16.fromF64(Double.POSITIVE_INFINITY)
        val negInf = F16.fromF64(Double.NEGATIVE_INFINITY)
        val nan = F16.fromF64(Double.NaN)

        assertEquals(F16.ONE, one)
        assertEquals(F16.ZERO, zero)
        assertTrue(zero.isSignPositive())
        assertEquals(F16.NEG_ZERO, negZero)
        assertTrue(negZero.isSignNegative())
        assertEquals(F16.INFINITY, inf)
        assertEquals(F16.NEG_INFINITY, negInf)
        assertTrue(nan.isNan())
        assertTrue(F16.NAN.isNan())

        assertEquals(F16.E, F16.fromF64(E))
        assertEquals(F16.PI, F16.fromF64(PI))
        assertEquals(F16.FRAC_1_PI, F16.fromF64(1.0 / PI))
        assertEquals(F16.FRAC_1_SQRT_2, F16.fromF64(1.0 / sqrt(2.0)))
        assertEquals(F16.FRAC_2_PI, F16.fromF64(2.0 / PI))
        assertEquals(F16.FRAC_2_SQRT_PI, F16.fromF64(2.0 / sqrt(PI)))
        assertEquals(F16.FRAC_PI_2, F16.fromF64(PI / 2.0))
        assertEquals(F16.FRAC_PI_3, F16.fromF64(PI / 3.0))
        assertEquals(F16.FRAC_PI_4, F16.fromF64(PI / 4.0))
        assertEquals(F16.FRAC_PI_6, F16.fromF64(PI / 6.0))
        assertEquals(F16.FRAC_PI_8, F16.fromF64(PI / 8.0))
        assertEquals(F16.LN_10, F16.fromF64(ln(10.0)))
        assertEquals(F16.LN_2, F16.fromF64(ln(2.0)))
        assertEquals(F16.LOG10_E, F16.fromF64(log10(E)))
        assertEquals(F16.LOG10_2, F16.fromF64(log10(2.0)))
        assertEquals(F16.LOG2_E, F16.fromF64(log2(E)))
        assertEquals(F16.LOG2_10, F16.fromF64(log2(10.0)))
        assertEquals(F16.SQRT_2, F16.fromF64(sqrt(2.0)))
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

        assertTrue(F16.fromF64(nan64).isNan())
        assertTrue(F16.fromF64(negNan64).isNan())
        assertTrue(F16.fromF32(nan32).isNan())
        assertTrue(F16.fromF32(negNan32).isNan())
    }

    @Test
    fun testNanConversionToLarger() {
        val nan16 = F16.fromBits(0x7C01u)
        val negNan16 = F16.fromBits(0xFC01u)

        assertTrue(nan16.isNan() && nan16.isSignPositive())
        assertTrue(negNan16.isNan() && negNan16.isSignNegative())

        assertTrue(nan16.toF32().isNaN())
        assertTrue(negNan16.toF32().isNaN())
        assertTrue(nan16.toF64().isNaN())
        assertTrue(negNan16.toF64().isNaN())
    }

    @Test
    fun testF16ToF32() {
        val exact = F16.fromF32(7.0f)
        assertEquals(7.0f, exact.toF32())

        val approx = F16.fromF32(7.1f)
        val diff = abs(approx.toF32() - 7.1f)
        assertTrue(diff <= 4.0f * F16.EPSILON.toF32())

        assertEquals(2.0f.pow(-24), F16.fromBits(0x0001u).toF32())
        assertEquals(5.0f * 2.0f.pow(-24), F16.fromBits(0x0005u).toF32())
        assertEquals(F16.fromBits(0x0001u), F16.fromF32(2.0f.pow(-24)))
        assertEquals(F16.fromBits(0x0005u), F16.fromF32(5.0f * 2.0f.pow(-24)))
    }

    @Test
    fun testF16ToF64() {
        val exact = F16.fromF64(7.0)
        assertEquals(7.0, exact.toF64())

        val approx = F16.fromF64(7.1)
        val diff = abs(approx.toF64() - 7.1)
        assertTrue(diff <= 4.0 * F16.EPSILON.toF64())

        assertEquals(2.0.pow(-24), F16.fromBits(0x0001u).toF64())
        assertEquals(5.0 * 2.0.pow(-24), F16.fromBits(0x0005u).toF64())
        assertEquals(F16.fromBits(0x0001u), F16.fromF64(2.0.pow(-24)))
        assertEquals(F16.fromBits(0x0005u), F16.fromF64(5.0 * 2.0.pow(-24)))
    }

    @Test
    fun testComparisons() {
        val zero = F16.fromF64(0.0)
        val one = F16.fromF64(1.0)
        val negZero = F16.fromF64(-0.0)
        val negOne = F16.fromF64(-1.0)

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
        val minSub = F16.fromBits(1u)
        val minSubF = 2.0f.pow(-24)
        assertEquals(minSub.toBits(), F16.fromF32(minSubF).toBits())
        assertEquals(minSubF.toRawBits(), minSub.toF32().toRawBits())

        assertEquals(0u.toUShort(), F16.fromF32(minSubF * 0.49f).toBits())
        assertEquals(0u.toUShort(), F16.fromF32(minSubF * 0.50f).toBits())
        assertEquals(1u.toUShort(), F16.fromF32(minSubF * 0.51f).toBits())

        assertEquals(1u.toUShort(), F16.fromF32(minSubF * 1.49f).toBits())
        assertEquals(2u.toUShort(), F16.fromF32(minSubF * 1.50f).toBits())
        assertEquals(2u.toUShort(), F16.fromF32(minSubF * 1.51f).toBits())

        assertEquals(2u.toUShort(), F16.fromF32(minSubF * 2.49f).toBits())
        assertEquals(2u.toUShort(), F16.fromF32(minSubF * 2.50f).toBits())
        assertEquals(3u.toUShort(), F16.fromF32(minSubF * 2.51f).toBits())

        assertEquals(F16.fromF32(2000.0f).toBits(), F16.fromF32(2000.49f).toBits())
        assertEquals(F16.fromF32(2000.0f).toBits(), F16.fromF32(2000.50f).toBits())
        assertEquals(F16.fromF32(2001.0f).toBits(), F16.fromF32(2000.51f).toBits())
        assertEquals(F16.fromF32(2001.0f).toBits(), F16.fromF32(2001.49f).toBits())
        assertEquals(F16.fromF32(2002.0f).toBits(), F16.fromF32(2001.50f).toBits())
        assertEquals(F16.fromF32(2002.0f).toBits(), F16.fromF32(2001.51f).toBits())
        assertEquals(F16.fromF32(2002.0f).toBits(), F16.fromF32(2002.49f).toBits())
        assertEquals(F16.fromF32(2002.0f).toBits(), F16.fromF32(2002.50f).toBits())
        assertEquals(F16.fromF32(2003.0f).toBits(), F16.fromF32(2002.51f).toBits())
    }

    @Test
    fun roundToEvenF64() {
        val minSub = F16.fromBits(1u)
        val minSubF = 2.0.pow(-24)
        assertEquals(minSub.toBits(), F16.fromF64(minSubF).toBits())
        assertEquals(minSubF.toRawBits(), minSub.toF64().toRawBits())

        assertEquals(0u.toUShort(), F16.fromF64(minSubF * 0.49).toBits())
        assertEquals(0u.toUShort(), F16.fromF64(minSubF * 0.50).toBits())
        assertEquals(1u.toUShort(), F16.fromF64(minSubF * 0.51).toBits())

        assertEquals(1u.toUShort(), F16.fromF64(minSubF * 1.49).toBits())
        assertEquals(2u.toUShort(), F16.fromF64(minSubF * 1.50).toBits())
        assertEquals(2u.toUShort(), F16.fromF64(minSubF * 1.51).toBits())

        assertEquals(2u.toUShort(), F16.fromF64(minSubF * 2.49).toBits())
        assertEquals(2u.toUShort(), F16.fromF64(minSubF * 2.50).toBits())
        assertEquals(3u.toUShort(), F16.fromF64(minSubF * 2.51).toBits())

        assertEquals(F16.fromF64(2000.0).toBits(), F16.fromF64(2000.49).toBits())
        assertEquals(F16.fromF64(2000.0).toBits(), F16.fromF64(2000.50).toBits())
        assertEquals(F16.fromF64(2001.0).toBits(), F16.fromF64(2000.51).toBits())
        assertEquals(F16.fromF64(2001.0).toBits(), F16.fromF64(2001.49).toBits())
        assertEquals(F16.fromF64(2002.0).toBits(), F16.fromF64(2001.50).toBits())
        assertEquals(F16.fromF64(2002.0).toBits(), F16.fromF64(2001.51).toBits())
        assertEquals(F16.fromF64(2002.0).toBits(), F16.fromF64(2002.49).toBits())
        assertEquals(F16.fromF64(2002.0).toBits(), F16.fromF64(2002.50).toBits())
        assertEquals(F16.fromF64(2003.0).toBits(), F16.fromF64(2002.51).toBits())
    }

    @Test
    fun arithmetic() {
        assertEquals(F16.fromF32(2.0f), F16.ONE + F16.ONE)
        assertEquals(F16.ZERO, F16.ONE - F16.ONE)
        assertEquals(F16.ONE, F16.ONE * F16.ONE)
        assertEquals(F16.fromF32(4.0f), F16.fromF32(2.0f) * F16.fromF32(2.0f))
        assertEquals(F16.ONE, F16.ONE / F16.ONE)
        assertEquals(F16.fromF32(2.0f), F16.fromF32(4.0f) / F16.fromF32(2.0f))
        assertEquals(F16.fromF32(1.0f), F16.fromF32(4.0f) % F16.fromF32(3.0f))
    }

    @Test
    fun formatting() {
        // Kotlin common stdlib lacks Rust Display width/precision formatting specifiers; verify toString().
        val f = F16.fromF32(0.1152344f)
        assertEquals(f.toF32().toString(), f.toString())
        assertTrue(f.toString().startsWith("0.1152"))
    }

    @Test
    fun roundtripF16F32AndF64Exhaustive() {
        for (bits in 0..0xFFFF) {
            val f = F16.fromBits(bits.toUShort())
            val rt32 = F16.fromF32(f.toF32())
            val rt64 = F16.fromF64(f.toF64())
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
