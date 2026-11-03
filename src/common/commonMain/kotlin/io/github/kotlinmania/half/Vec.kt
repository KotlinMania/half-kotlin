// port-lint: source vec.rs
package io.github.kotlinmania.half

public fun List<F16>.f16ReinterpretInto(): List<UShort> =
    List(size) { this[it].rawBits }

public fun List<BF16>.bf16ReinterpretInto(): List<UShort> =
    List(size) { this[it].rawBits }

public inline fun <reified H : SealedHalf> List<UShort>.reinterpretInto(): List<H> =
    when (H::class) {
        F16::class -> List(size) { F16(this[it]) as H }
        BF16::class -> List(size) { BF16(this[it]) as H }
        else -> error("Unsupported SealedHalf type")
    }

public fun f16VecFromF32Slice(slice: FloatArray): List<F16> =
    List(slice.size) { F16.fromF32(slice[it]) }

public fun f16VecFromF64Slice(slice: DoubleArray): List<F16> =
    List(slice.size) { F16.fromF64(slice[it]) }

public fun bf16VecFromF32Slice(slice: FloatArray): List<BF16> =
    List(slice.size) { BF16.fromF32(slice[it]) }

public fun bf16VecFromF64Slice(slice: DoubleArray): List<BF16> =
    List(slice.size) { BF16.fromF64(slice[it]) }
