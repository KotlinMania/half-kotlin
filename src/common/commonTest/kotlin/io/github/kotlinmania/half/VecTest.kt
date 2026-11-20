// port-lint: tests vec.rs
package io.github.kotlinmania.half

import kotlin.test.Test
import kotlin.test.assertEquals

class VecTest {
    // Optional `rand_distr` feature tests (`test_sample_f16`, `test_sample_bf16`) are excluded under default features.
    @Test
    fun testVecConversionsF16() {
        val numbers = listOf(F16.E, F16.PI, F16.EPSILON, F16.FRAC_1_SQRT_2)
        val bits =
            listOf(
                F16.E.toBits(),
                F16.PI.toBits(),
                F16.EPSILON.toBits(),
                F16.FRAC_1_SQRT_2.toBits(),
            )
        val bitsCloned = bits.toList()

        val fromBits = bits.reinterpretInto<F16>()
        assertEquals(numbers, fromBits)

        val toBits = fromBits.f16ReinterpretInto()
        assertEquals(bitsCloned, toBits)

        assertEquals(numbers, f16VecFromF32Slice(FloatArray(numbers.size) { numbers[it].toF32() }))
        assertEquals(numbers, f16VecFromF64Slice(DoubleArray(numbers.size) { numbers[it].toF64() }))
    }

    @Test
    fun testVecConversionsBf16() {
        val numbers = listOf(BF16.E, BF16.PI, BF16.EPSILON, BF16.FRAC_1_SQRT_2)
        val bits =
            listOf(
                BF16.E.toBits(),
                BF16.PI.toBits(),
                BF16.EPSILON.toBits(),
                BF16.FRAC_1_SQRT_2.toBits(),
            )
        val bitsCloned = bits.toList()

        val fromBits = bits.reinterpretInto<BF16>()
        assertEquals(numbers, fromBits)

        val toBits = fromBits.bf16ReinterpretInto()
        assertEquals(bitsCloned, toBits)

        assertEquals(numbers, bf16VecFromF32Slice(FloatArray(numbers.size) { numbers[it].toF32() }))
        assertEquals(numbers, bf16VecFromF64Slice(DoubleArray(numbers.size) { numbers[it].toF64() }))
    }
}
