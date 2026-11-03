// port-lint: source slice.rs
package io.github.kotlinmania.half

public sealed interface SealedHalfFloatSlice

public sealed interface SealedHalfBitsSlice

public fun Array<F16>.f16ArrayReinterpretCast(): UShortArray = UShortArray(size) { this[it].rawBits }

public fun List<F16>.f16ListReinterpretCast(): UShortArray = UShortArray(size) { this[it].rawBits }

public fun Array<F16>.f16ArrayReinterpretCastMut(): UShortArray = UShortArray(size) { this[it].rawBits }

public fun Array<F16>.f16ArrayConvertFromF32Slice(src: FloatArray) {
    require(size == src.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        this[i] = F16.fromF32(src[i])
    }
}

public fun Array<F16>.f16ArrayConvertFromF64Slice(src: DoubleArray) {
    require(size == src.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        this[i] = F16.fromF64(src[i])
    }
}

public fun Array<F16>.f16ArrayConvertToF32Slice(dst: FloatArray) {
    require(size == dst.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        dst[i] = this[i].toF32()
    }
}

public fun List<F16>.f16ListConvertToF32Slice(dst: FloatArray) {
    require(size == dst.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        dst[i] = this[i].toF32()
    }
}

public fun Array<F16>.f16ArrayConvertToF64Slice(dst: DoubleArray) {
    require(size == dst.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        dst[i] = this[i].toF64()
    }
}

public fun List<F16>.f16ListConvertToF64Slice(dst: DoubleArray) {
    require(size == dst.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        dst[i] = this[i].toF64()
    }
}

public fun Array<F16>.f16ArrayToF32Vec(): List<Float> = List(size) { this[it].toF32() }

public fun List<F16>.f16ListToF32Vec(): List<Float> = List(size) { this[it].toF32() }

public fun Array<F16>.f16ArrayToF64Vec(): List<Double> = List(size) { this[it].toF64() }

public fun List<F16>.f16ListToF64Vec(): List<Double> = List(size) { this[it].toF64() }

public fun Array<BF16>.bf16ArrayReinterpretCast(): UShortArray = UShortArray(size) { this[it].rawBits }

public fun List<BF16>.bf16ListReinterpretCast(): UShortArray = UShortArray(size) { this[it].rawBits }

public fun Array<BF16>.bf16ArrayReinterpretCastMut(): UShortArray = UShortArray(size) { this[it].rawBits }

public fun Array<BF16>.bf16ArrayConvertFromF32Slice(src: FloatArray) {
    require(size == src.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        this[i] = BF16.fromF32(src[i])
    }
}

public fun Array<BF16>.bf16ArrayConvertFromF64Slice(src: DoubleArray) {
    require(size == src.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        this[i] = BF16.fromF64(src[i])
    }
}

public fun Array<BF16>.bf16ArrayConvertToF32Slice(dst: FloatArray) {
    require(size == dst.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        dst[i] = this[i].toF32()
    }
}

public fun List<BF16>.bf16ListConvertToF32Slice(dst: FloatArray) {
    require(size == dst.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        dst[i] = this[i].toF32()
    }
}

public fun Array<BF16>.bf16ArrayConvertToF64Slice(dst: DoubleArray) {
    require(size == dst.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        dst[i] = this[i].toF64()
    }
}

public fun List<BF16>.bf16ListConvertToF64Slice(dst: DoubleArray) {
    require(size == dst.size) { "destination and source slices have different lengths" }
    for (i in indices) {
        dst[i] = this[i].toF64()
    }
}

public fun Array<BF16>.bf16ArrayToF32Vec(): List<Float> = List(size) { this[it].toF32() }

public fun List<BF16>.bf16ListToF32Vec(): List<Float> = List(size) { this[it].toF32() }

public fun Array<BF16>.bf16ArrayToF64Vec(): List<Double> = List(size) { this[it].toF64() }

public fun List<BF16>.bf16ListToF64Vec(): List<Double> = List(size) { this[it].toF64() }

public inline fun <reified H : SealedHalf> UShortArray.reinterpretCast(): Array<H> =
    when (H::class) {
        F16::class -> Array(size) { F16(this[it]) as H }
        BF16::class -> Array(size) { BF16(this[it]) as H }
        else -> error("Unsupported SealedHalf type")
    }

public inline fun <reified H : SealedHalf> UShortArray.reinterpretCastMut(): Array<H> =
    reinterpretCast()
