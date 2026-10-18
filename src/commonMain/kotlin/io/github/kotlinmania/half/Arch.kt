// port-lint: source binary16/arch.rs
package io.github.kotlinmania.half

internal fun f32ToF16(f: Float): UShort = f32ToF16Fallback(f)

internal fun f64ToF16(f: Double): UShort = f64ToF16Fallback(f)

internal fun f16ToF32(i: UShort): Float = f16ToF32Fallback(i)

internal fun f16ToF64(i: UShort): Double = f16ToF64Fallback(i)

internal fun f32x4ToF16x4(f: FloatArray): UShortArray = f32x4ToF16x4Fallback(f)

internal fun f16x4ToF32x4(i: UShortArray): FloatArray = f16x4ToF32x4Fallback(i)

internal fun f64x4ToF16x4(f: DoubleArray): UShortArray = f64x4ToF16x4Fallback(f)

internal fun f16x4ToF64x4(i: UShortArray): DoubleArray = f16x4ToF64x4Fallback(i)

internal fun f32x8ToF16x8(f: FloatArray): UShortArray = f32x8ToF16x8Fallback(f)

internal fun f16x8ToF32x8(i: UShortArray): FloatArray = f16x8ToF32x8Fallback(i)

internal fun f64x8ToF16x8(f: DoubleArray): UShortArray = f64x8ToF16x8Fallback(f)

internal fun f16x8ToF64x8(i: UShortArray): DoubleArray = f16x8ToF64x8Fallback(i)

internal fun f32ToF16Slice(src: FloatArray, dst: UShortArray) {
    require(src.size == dst.size) { "destination and source slices have different lengths" }
    for (idx in src.indices) {
        dst[idx] = f32ToF16Fallback(src[idx])
    }
}

internal fun f16ToF32Slice(src: UShortArray, dst: FloatArray) {
    require(src.size == dst.size) { "destination and source slices have different lengths" }
    for (idx in src.indices) {
        dst[idx] = f16ToF32Fallback(src[idx])
    }
}

internal fun f64ToF16Slice(src: DoubleArray, dst: UShortArray) {
    require(src.size == dst.size) { "destination and source slices have different lengths" }
    for (idx in src.indices) {
        dst[idx] = f64ToF16Fallback(src[idx])
    }
}

internal fun f16ToF64Slice(src: UShortArray, dst: DoubleArray) {
    require(src.size == dst.size) { "destination and source slices have different lengths" }
    for (idx in src.indices) {
        dst[idx] = f16ToF64Fallback(src[idx])
    }
}

internal fun addF16(a: UShort, b: UShort): UShort = addF16Fallback(a, b)

internal fun subtractF16(a: UShort, b: UShort): UShort = subtractF16Fallback(a, b)

internal fun multiplyF16(a: UShort, b: UShort): UShort = multiplyF16Fallback(a, b)

internal fun divideF16(a: UShort, b: UShort): UShort = divideF16Fallback(a, b)

internal fun remainderF16(a: UShort, b: UShort): UShort = remainderF16Fallback(a, b)

internal fun productF16(iter: Iterator<UShort>): UShort = productF16Fallback(iter)

internal fun sumF16(iter: Iterator<UShort>): UShort = sumF16Fallback(iter)

internal fun f32ToF16Fallback(value: Float): UShort {
    val x: UInt = value.toRawBits().toUInt()

    val sign = x and 0x8000_0000u
    val exp = x and 0x7F80_0000u
    val man = x and 0x007F_FFFFu

    if (exp == 0x7F80_0000u) {
        val nanBit = if (man == 0u) 0u else 0x0200u
        return ((sign shr 16) or 0x7C00u or nanBit or (man shr 13)).toUShort()
    }

    val halfSign = sign shr 16
    val unbiasedExp = (exp shr 23).toInt() - 127
    val halfExp = unbiasedExp + 15

    if (halfExp >= 0x1F) {
        return (halfSign or 0x7C00u).toUShort()
    }

    if (halfExp <= 0) {
        if (14 - halfExp > 24) {
            return halfSign.toUShort()
        }
        val manWithHidden = man or 0x0080_0000u
        var halfMan = manWithHidden shr (14 - halfExp)
        val roundBit = 1u shl (13 - halfExp)
        if ((manWithHidden and roundBit) != 0u && (manWithHidden and (3u * roundBit - 1u)) != 0u) {
            halfMan += 1u
        }
        return (halfSign or halfMan).toUShort()
    }

    val rebiasedHalfExp = halfExp.toUInt() shl 10
    val halfMan = man shr 13
    val roundBit = 0x0000_1000u
    return if ((man and roundBit) != 0u && (man and (3u * roundBit - 1u)) != 0u) {
        ((halfSign or rebiasedHalfExp or halfMan) + 1u).toUShort()
    } else {
        (halfSign or rebiasedHalfExp or halfMan).toUShort()
    }
}

internal fun f64ToF16Fallback(value: Double): UShort {
    val valueBits: ULong = value.toRawBits().toULong()
    val x = (valueBits shr 32).toUInt()

    val sign = x and 0x8000_0000u
    val exp = x and 0x7FF0_0000u
    val man = x and 0x000F_FFFFu

    if (exp == 0x7FF0_0000u) {
        val nanBit =
            if (man == 0u && valueBits.toUInt() == 0u) {
                0u
            } else {
                0x0200u
            }
        return ((sign shr 16) or 0x7C00u or nanBit or (man shr 10)).toUShort()
    }

    val halfSign = sign shr 16
    val unbiasedExp = (exp shr 20).toLong() - 1023L
    val halfExp = unbiasedExp + 15L

    if (halfExp >= 0x1FL) {
        return (halfSign or 0x7C00u).toUShort()
    }

    if (halfExp <= 0L) {
        if (10L - halfExp > 21L) {
            return halfSign.toUShort()
        }
        val manWithHidden = man or 0x0010_0000u
        var halfMan = manWithHidden shr (11L - halfExp).toInt()
        val roundBit = 1u shl (10L - halfExp).toInt()
        if ((manWithHidden and roundBit) != 0u && (manWithHidden and (3u * roundBit - 1u)) != 0u) {
            halfMan += 1u
        }
        return (halfSign or halfMan).toUShort()
    }

    val rebiasedHalfExp = halfExp.toUInt() shl 10
    val halfMan = man shr 10
    val roundBit = 0x0000_0200u
    return if ((man and roundBit) != 0u && (man and (3u * roundBit - 1u)) != 0u) {
        ((halfSign or rebiasedHalfExp or halfMan) + 1u).toUShort()
    } else {
        (halfSign or rebiasedHalfExp or halfMan).toUShort()
    }
}

internal fun f16ToF32Fallback(i: UShort): Float {
    if ((i.toUInt() and 0x7FFFu) == 0u) {
        return Float.fromBits((i.toUInt() shl 16).toInt())
    }

    val halfSign = i.toUInt() and 0x8000u
    val halfExp = i.toUInt() and 0x7C00u
    val halfMan = i.toUInt() and 0x03FFu

    if (halfExp == 0x7C00u) {
        return if (halfMan == 0u) {
            Float.fromBits(((halfSign shl 16) or 0x7F80_0000u).toInt())
        } else {
            Float.fromBits(((halfSign shl 16) or 0x7FC0_0000u or (halfMan shl 13)).toInt())
        }
    }

    val sign = halfSign shl 16
    val unbiasedExp = (halfExp.toInt() shr 10) - 15

    if (halfExp == 0u) {
        val e = leadingZerosU16(halfMan.toUShort()) - 6u
        val exp = (127u - 15u - e) shl 23
        val man = (halfMan shl (14 + e.toInt())) and 0x7F_FFFFu
        return Float.fromBits((sign or exp or man).toInt())
    }

    val exp = (unbiasedExp + 127).toUInt() shl 23
    val man = (halfMan and 0x03FFu) shl 13
    return Float.fromBits((sign or exp or man).toInt())
}

internal fun f16ToF64Fallback(i: UShort): Double {
    if ((i.toUInt() and 0x7FFFu) == 0u) {
        return Double.fromBits((i.toULong() shl 48).toLong())
    }

    val halfSign = (i.toUInt() and 0x8000u).toULong()
    val halfExp = (i.toUInt() and 0x7C00u).toULong()
    val halfMan = (i.toUInt() and 0x03FFu).toULong()

    if (halfExp == 0x7C00uL) {
        return if (halfMan == 0uL) {
            Double.fromBits(((halfSign shl 48) or 0x7FF0_0000_0000_0000uL).toLong())
        } else {
            Double.fromBits(((halfSign shl 48) or 0x7FF8_0000_0000_0000uL or (halfMan shl 42)).toLong())
        }
    }

    val sign = halfSign shl 48
    val unbiasedExp = (halfExp.toLong() shr 10) - 15L

    if (halfExp == 0uL) {
        val e = leadingZerosU16(halfMan.toUShort()) - 6u
        val exp = (1023uL - 15uL - e.toULong()) shl 52
        val man = (halfMan shl (43 + e.toInt())) and 0xF_FFFF_FFFF_FFFFuL
        return Double.fromBits((sign or exp or man).toLong())
    }

    val exp = (unbiasedExp + 1023L).toULong() shl 52
    val man = (halfMan and 0x03FFuL) shl 42
    return Double.fromBits((sign or exp or man).toLong())
}

internal fun f16x4ToF32x4Fallback(v: UShortArray): FloatArray =
    floatArrayOf(
        f16ToF32Fallback(v[0]),
        f16ToF32Fallback(v[1]),
        f16ToF32Fallback(v[2]),
        f16ToF32Fallback(v[3]),
    )

internal fun f32x4ToF16x4Fallback(v: FloatArray): UShortArray =
    ushortArrayOf(
        f32ToF16Fallback(v[0]),
        f32ToF16Fallback(v[1]),
        f32ToF16Fallback(v[2]),
        f32ToF16Fallback(v[3]),
    )

internal fun f16x4ToF64x4Fallback(v: UShortArray): DoubleArray =
    doubleArrayOf(
        f16ToF64Fallback(v[0]),
        f16ToF64Fallback(v[1]),
        f16ToF64Fallback(v[2]),
        f16ToF64Fallback(v[3]),
    )

internal fun f64x4ToF16x4Fallback(v: DoubleArray): UShortArray =
    ushortArrayOf(
        f64ToF16Fallback(v[0]),
        f64ToF16Fallback(v[1]),
        f64ToF16Fallback(v[2]),
        f64ToF16Fallback(v[3]),
    )

internal fun f16x8ToF32x8Fallback(v: UShortArray): FloatArray =
    floatArrayOf(
        f16ToF32Fallback(v[0]),
        f16ToF32Fallback(v[1]),
        f16ToF32Fallback(v[2]),
        f16ToF32Fallback(v[3]),
        f16ToF32Fallback(v[4]),
        f16ToF32Fallback(v[5]),
        f16ToF32Fallback(v[6]),
        f16ToF32Fallback(v[7]),
    )

internal fun f32x8ToF16x8Fallback(v: FloatArray): UShortArray =
    ushortArrayOf(
        f32ToF16Fallback(v[0]),
        f32ToF16Fallback(v[1]),
        f32ToF16Fallback(v[2]),
        f32ToF16Fallback(v[3]),
        f32ToF16Fallback(v[4]),
        f32ToF16Fallback(v[5]),
        f32ToF16Fallback(v[6]),
        f32ToF16Fallback(v[7]),
    )

internal fun f16x8ToF64x8Fallback(v: UShortArray): DoubleArray =
    doubleArrayOf(
        f16ToF64Fallback(v[0]),
        f16ToF64Fallback(v[1]),
        f16ToF64Fallback(v[2]),
        f16ToF64Fallback(v[3]),
        f16ToF64Fallback(v[4]),
        f16ToF64Fallback(v[5]),
        f16ToF64Fallback(v[6]),
        f16ToF64Fallback(v[7]),
    )

internal fun f64x8ToF16x8Fallback(v: DoubleArray): UShortArray =
    ushortArrayOf(
        f64ToF16Fallback(v[0]),
        f64ToF16Fallback(v[1]),
        f64ToF16Fallback(v[2]),
        f64ToF16Fallback(v[3]),
        f64ToF16Fallback(v[4]),
        f64ToF16Fallback(v[5]),
        f64ToF16Fallback(v[6]),
        f64ToF16Fallback(v[7]),
    )

internal fun addF16Fallback(a: UShort, b: UShort): UShort =
    f32ToF16(f16ToF32(a) + f16ToF32(b))

internal fun subtractF16Fallback(a: UShort, b: UShort): UShort =
    f32ToF16(f16ToF32(a) - f16ToF32(b))

internal fun multiplyF16Fallback(a: UShort, b: UShort): UShort =
    f32ToF16(f16ToF32(a) * f16ToF32(b))

internal fun divideF16Fallback(a: UShort, b: UShort): UShort =
    f32ToF16(f16ToF32(a) / f16ToF32(b))

internal fun remainderF16Fallback(a: UShort, b: UShort): UShort =
    f32ToF16(f16ToF32(a) % f16ToF32(b))

internal fun productF16Fallback(iter: Iterator<UShort>): UShort {
    var acc = 1.0f
    while (iter.hasNext()) {
        acc *= f16ToF32(iter.next())
    }
    return f32ToF16(acc)
}

internal fun sumF16Fallback(iter: Iterator<UShort>): UShort {
    var acc = 0.0f
    while (iter.hasNext()) {
        acc += f16ToF32(iter.next())
    }
    return f32ToF16(acc)
}
