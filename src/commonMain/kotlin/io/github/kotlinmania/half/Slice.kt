// port-lint: source slice.rs
package io.github.kotlinmania.half

import kotlin.jvm.JvmName

public sealed interface SealedHalfFloatSlice

public sealed interface SealedHalfBitsSlice

@JvmName("f16ArrayReinterpretCast")
public fun Array<F16>.reinterpretCast(): UShortArray = UShortArray(size) { this[it].rawBits }

@JvmName("f16ListReinterpretCast")
public fun List<F16>.reinterpretCast(): UShortArray = UShortArray(size) { this[it].rawBits }

@JvmName("f16ArrayReinterpretCastMut")
public fun Array<F16>.reinterpretCastMut(): UShortArray = UShortArray(size) { this[it].rawBits }

@JvmName("f16ArrayConvertFromF32Slice")
public fun Array<F16>.convertFromF32Slice(src: FloatArray) {
    require(size == src.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        this[i] = F16.fromF32(src[i])
    }
}

@JvmName("f16ListConvertFromF32Slice")
public fun MutableList<F16>.convertFromF32Slice(src: FloatArray) {
    require(size == src.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        this[i] = F16.fromF32(src[i])
    }
}

@JvmName("f16ListConvertFromF32List")
public fun MutableList<F16>.convertFromF32Slice(src: List<Float>) {
    require(size == src.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        this[i] = F16.fromF32(src[i])
    }
}

@JvmName("f16ArrayConvertFromF64Slice")
public fun Array<F16>.convertFromF64Slice(src: DoubleArray) {
    require(size == src.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        this[i] = F16.fromF64(src[i])
    }
}

@JvmName("f16ListConvertFromF64Slice")
public fun MutableList<F16>.convertFromF64Slice(src: DoubleArray) {
    require(size == src.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        this[i] = F16.fromF64(src[i])
    }
}

@JvmName("f16ArrayConvertToF32Slice")
public fun Array<F16>.convertToF32Slice(dst: FloatArray) {
    require(size == dst.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        dst[i] = this[i].toF32()
    }
}

@JvmName("f16ListConvertToF32Slice")
public fun List<F16>.convertToF32Slice(dst: FloatArray) {
    require(size == dst.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        dst[i] = this[i].toF32()
    }
}

@JvmName("f16ListConvertToF32List")
public fun List<F16>.convertToF32Slice(dst: MutableList<Float>) {
    require(size == dst.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        dst[i] = this[i].toF32()
    }
}

@JvmName("f16ArrayConvertToF64Slice")
public fun Array<F16>.convertToF64Slice(dst: DoubleArray) {
    require(size == dst.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        dst[i] = this[i].toF64()
    }
}

@JvmName("f16ListConvertToF64Slice")
public fun List<F16>.convertToF64Slice(dst: DoubleArray) {
    require(size == dst.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        dst[i] = this[i].toF64()
    }
}

@JvmName("f16ArrayToF32Vec")
public fun Array<F16>.toF32Vec(): MutableList<Float> = MutableList(size) { this[it].toF32() }

@JvmName("f16ListToF32Vec")
public fun List<F16>.toF32Vec(): MutableList<Float> = MutableList(size) { this[it].toF32() }

@JvmName("f16ArrayToF64Vec")
public fun Array<F16>.toF64Vec(): MutableList<Double> = MutableList(size) { this[it].toF64() }

@JvmName("f16ListToF64Vec")
public fun List<F16>.toF64Vec(): MutableList<Double> = MutableList(size) { this[it].toF64() }

@JvmName("bf16ArrayReinterpretCast")
public fun Array<BF16>.reinterpretCast(): UShortArray = UShortArray(size) { this[it].rawBits }

@JvmName("bf16ListReinterpretCast")
public fun List<BF16>.reinterpretCast(): UShortArray = UShortArray(size) { this[it].rawBits }

@JvmName("bf16ArrayReinterpretCastMut")
public fun Array<BF16>.reinterpretCastMut(): UShortArray = UShortArray(size) { this[it].rawBits }

@JvmName("bf16ArrayConvertFromF32Slice")
public fun Array<BF16>.convertFromF32Slice(src: FloatArray) {
    require(size == src.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        this[i] = BF16.fromF32(src[i])
    }
}

@JvmName("bf16ListConvertFromF32Slice")
public fun MutableList<BF16>.convertFromF32Slice(src: FloatArray) {
    require(size == src.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        this[i] = BF16.fromF32(src[i])
    }
}

@JvmName("bf16ListConvertFromF32List")
public fun MutableList<BF16>.convertFromF32Slice(src: List<Float>) {
    require(size == src.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        this[i] = BF16.fromF32(src[i])
    }
}

@JvmName("bf16ArrayConvertFromF64Slice")
public fun Array<BF16>.convertFromF64Slice(src: DoubleArray) {
    require(size == src.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        this[i] = BF16.fromF64(src[i])
    }
}

@JvmName("bf16ListConvertFromF64Slice")
public fun MutableList<BF16>.convertFromF64Slice(src: DoubleArray) {
    require(size == src.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        this[i] = BF16.fromF64(src[i])
    }
}

@JvmName("bf16ArrayConvertToF32Slice")
public fun Array<BF16>.convertToF32Slice(dst: FloatArray) {
    require(size == dst.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        dst[i] = this[i].toF32()
    }
}

@JvmName("bf16ListConvertToF32Slice")
public fun List<BF16>.convertToF32Slice(dst: FloatArray) {
    require(size == dst.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        dst[i] = this[i].toF32()
    }
}

@JvmName("bf16ListConvertToF32List")
public fun List<BF16>.convertToF32Slice(dst: MutableList<Float>) {
    require(size == dst.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        dst[i] = this[i].toF32()
    }
}

@JvmName("bf16ArrayConvertToF64Slice")
public fun Array<BF16>.convertToF64Slice(dst: DoubleArray) {
    require(size == dst.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        dst[i] = this[i].toF64()
    }
}

@JvmName("bf16ListConvertToF64Slice")
public fun List<BF16>.convertToF64Slice(dst: DoubleArray) {
    require(size == dst.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        dst[i] = this[i].toF64()
    }
}

@JvmName("bf16ArrayToF32Vec")
public fun Array<BF16>.toF32Vec(): MutableList<Float> = MutableList(size) { this[it].toF32() }

@JvmName("bf16ListToF32Vec")
public fun List<BF16>.toF32Vec(): MutableList<Float> = MutableList(size) { this[it].toF32() }

@JvmName("bf16ArrayToF64Vec")
public fun Array<BF16>.toF64Vec(): MutableList<Double> = MutableList(size) { this[it].toF64() }

@JvmName("bf16ListToF64Vec")
public fun List<BF16>.toF64Vec(): MutableList<Double> = MutableList(size) { this[it].toF64() }

public inline fun <reified H : SealedHalf> UShortArray.reinterpretCast(): Array<H> =
    when (H::class) {
        F16::class -> Array(size) { F16(this[it]) as H }
        BF16::class -> Array(size) { BF16(this[it]) as H }
        else -> error("Unsupported SealedHalf type")
    }

public inline fun <reified H : SealedHalf> UShortArray.reinterpretCastMut(): Array<H> =
    reinterpretCast()
