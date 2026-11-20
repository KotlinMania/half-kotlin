// port-lint: tests slice.rs
package io.github.kotlinmania.half

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

class SliceTest {
    @Test
    fun testSliceConversionsF16() {
        val bits =
            ushortArrayOf(
                F16.E.toBits(),
                F16.PI.toBits(),
                F16.EPSILON.toBits(),
                F16.FRAC_1_SQRT_2.toBits(),
            )
        val numbers = arrayOf(F16.E, F16.PI, F16.EPSILON, F16.FRAC_1_SQRT_2)

        val fromBits = bits.reinterpretCast<F16>()
        assertContentEquals(numbers, fromBits)

        val toBits = fromBits.f16ArrayReinterpretCast()
        assertContentEquals(bits, toBits)
        assertContentEquals(bits, fromBits.toList().f16ListReinterpretCast())
    }

    @Test
    fun testMutabilityF16() {
        // Kotlin does not alias managed Array<F16> and UShortArray in place; verify mut-convert round-trips.
        val bits = ushortArrayOf(F16.PI.toBits())
        val numbers = bits.reinterpretCastMut<F16>()
        numbers[0] = F16.E
        val updated = numbers.f16ArrayReinterpretCastMut()
        assertContentEquals(ushortArrayOf(F16.E.toBits()), updated)
        updated[0] = F16.LN_2.toBits()
        assertContentEquals(ushortArrayOf(F16.LN_2.toBits()), updated)
    }

    @Test
    fun testSliceConversionsBf16() {
        val bits =
            ushortArrayOf(
                BF16.E.toBits(),
                BF16.PI.toBits(),
                BF16.EPSILON.toBits(),
                BF16.FRAC_1_SQRT_2.toBits(),
            )
        val numbers = arrayOf(BF16.E, BF16.PI, BF16.EPSILON, BF16.FRAC_1_SQRT_2)

        val fromBits = bits.reinterpretCast<BF16>()
        assertContentEquals(numbers, fromBits)

        val toBits = fromBits.bf16ArrayReinterpretCast()
        assertContentEquals(bits, toBits)
        assertContentEquals(bits, fromBits.toList().bf16ListReinterpretCast())
    }

    @Test
    fun testMutabilityBf16() {
        // Kotlin does not alias managed Array<BF16> and UShortArray in place; verify mut-convert round-trips.
        val bits = ushortArrayOf(BF16.PI.toBits())
        val numbers = bits.reinterpretCastMut<BF16>()
        numbers[0] = BF16.E
        val updated = numbers.bf16ArrayReinterpretCastMut()
        assertContentEquals(ushortArrayOf(BF16.E.toBits()), updated)
        updated[0] = BF16.LN_2.toBits()
        assertContentEquals(ushortArrayOf(BF16.LN_2.toBits()), updated)
    }

    @Test
    fun sliceConvertF16F32() {
        for (size in listOf(8, 9, 2)) {
            val vf32 = FloatArray(size) { (it + 1).toFloat() }
            val vf16 = Array(size) { F16.fromF32((it + 1).toFloat()) }
            val buf32 = vf32.copyOf()
            val buf16 = vf16.copyOf()

            vf16.f16ArrayConvertToF32Slice(buf32)
            assertContentEquals(vf32, buf32)

            buf16.f16ArrayConvertFromF32Slice(vf32)
            assertContentEquals(vf16, buf16)
        }
    }

    @Test
    fun sliceConvertBf16F32() {
        for (size in listOf(8, 9, 2)) {
            val vf32 = FloatArray(size) { (it + 1).toFloat() }
            val vf16 = Array(size) { BF16.fromF32((it + 1).toFloat()) }
            val buf32 = vf32.copyOf()
            val buf16 = vf16.copyOf()

            vf16.bf16ArrayConvertToF32Slice(buf32)
            assertContentEquals(vf32, buf32)

            buf16.bf16ArrayConvertFromF32Slice(vf32)
            assertContentEquals(vf16, buf16)
        }
    }

    @Test
    fun sliceConvertF16F64() {
        for (size in listOf(8, 9, 2)) {
            val vf64 = DoubleArray(size) { (it + 1).toDouble() }
            val vf16 = Array(size) { F16.fromF64((it + 1).toDouble()) }
            val buf64 = vf64.copyOf()
            val buf16 = vf16.copyOf()

            vf16.f16ArrayConvertToF64Slice(buf64)
            assertContentEquals(vf64, buf64)

            buf16.f16ArrayConvertFromF64Slice(vf64)
            assertContentEquals(vf16, buf16)
        }
    }

    @Test
    fun sliceConvertBf16F64() {
        for (size in listOf(8, 9, 2)) {
            val vf64 = DoubleArray(size) { (it + 1).toDouble() }
            val vf16 = Array(size) { BF16.fromF64((it + 1).toDouble()) }
            val buf64 = vf64.copyOf()
            val buf16 = vf16.copyOf()

            vf16.bf16ArrayConvertToF64Slice(buf64)
            assertContentEquals(vf64, buf64)

            buf16.bf16ArrayConvertFromF64Slice(vf64)
            assertContentEquals(vf16, buf16)
        }
    }

    @Test
    fun convertFromF32SliceLenMismatchFails() {
        val slice1 = Array(3) { F16.ZERO }
        val slice2 = FloatArray(4) { 0f }
        assertFailsWith<IllegalArgumentException> {
            slice1.f16ArrayConvertFromF32Slice(slice2)
        }
    }

    @Test
    fun convertFromF64SliceLenMismatchFails() {
        val slice1 = Array(3) { F16.ZERO }
        val slice2 = DoubleArray(4) { 0.0 }
        assertFailsWith<IllegalArgumentException> {
            slice1.f16ArrayConvertFromF64Slice(slice2)
        }
    }

    @Test
    fun convertToF32SliceLenMismatchFails() {
        val slice1 = Array(3) { F16.ZERO }
        val slice2 = FloatArray(4) { 0f }
        assertFailsWith<IllegalArgumentException> {
            slice1.f16ArrayConvertToF32Slice(slice2)
        }
    }

    @Test
    fun convertToF64SliceLenMismatchFails() {
        val slice1 = Array(3) { F16.ZERO }
        val slice2 = DoubleArray(4) { 0.0 }
        assertFailsWith<IllegalArgumentException> {
            slice1.f16ArrayConvertToF64Slice(slice2)
        }
    }
}
