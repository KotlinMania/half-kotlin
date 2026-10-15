// port-lint: source vec.rs
package io.github.kotlinmania.half

import kotlin.jvm.JvmName

@JvmName("f16ListReinterpretInto")
public fun List<F16>.reinterpretInto(): MutableList<UShort> =
    MutableList(size) { this[it].rawBits }

@JvmName("bf16ListReinterpretInto")
public fun List<BF16>.reinterpretInto(): MutableList<UShort> =
    MutableList(size) { this[it].rawBits }

public inline fun <reified H : SealedHalf> List<UShort>.reinterpretInto(): MutableList<H> =
    when (H::class) {
        F16::class -> MutableList(size) { F16(this[it]) as H }
        BF16::class -> MutableList(size) { BF16(this[it]) as H }
        else -> error("Unsupported SealedHalf type")
    }

public fun f16VecFromF32Slice(slice: FloatArray): MutableList<F16> =
    MutableList(slice.size) { F16.fromF32(slice[it]) }

public fun f16VecFromF64Slice(slice: DoubleArray): MutableList<F16> =
    MutableList(slice.size) { F16.fromF64(slice[it]) }

public fun bf16VecFromF32Slice(slice: FloatArray): MutableList<BF16> =
    MutableList(slice.size) { BF16.fromF32(slice[it]) }

public fun bf16VecFromF64Slice(slice: DoubleArray): MutableList<BF16> =
    MutableList(slice.size) { BF16.fromF64(slice[it]) }
