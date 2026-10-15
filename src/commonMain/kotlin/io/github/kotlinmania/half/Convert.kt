// port-lint: source bfloat/convert.rs
package io.github.kotlinmania.half

internal fun f32ToBf16(value: Float): UShort {
    val x: UInt = value.toRawBits().toUInt()

    // check for NaN
    if ((x and 0x7FFF_FFFFu) > 0x7F80_0000u) {
        // Keep high part of current mantissa but also set most significant mantissa bit
        return ((x shr 16) or 0x0040u).toUShort()
    }

    // round and shift
    val roundBit = 0x0000_8000u
    return if ((x and roundBit) != 0u && (x and (3u * roundBit - 1u)) != 0u) {
        ((x shr 16).toUShort() + 1u).toUShort()
    } else {
        (x shr 16).toUShort()
    }
}

internal fun f64ToBf16(value: Double): UShort {
    val valueBits: ULong = value.toRawBits().toULong()
    val x = (valueBits shr 32).toUInt()

    // Extract IEEE754 components
    val sign = x and 0x8000_0000u
    val exp = x and 0x7FF0_0000u
    val man = x and 0x000F_FFFFu

    // Check for all exponent bits being set, which is Infinity or NaN
    if (exp == 0x7FF0_0000u) {
        val nanBit = if (man == 0u && valueBits.toUInt() == 0u) {
            0u
        } else {
            0x0040u
        }
        return ((sign shr 16) or 0x7F80u or nanBit or (man shr 13)).toUShort()
    }

    // The number is normalized, start assembling half precision version
    val halfSign = sign shr 16
    // Unbias the exponent, then bias for bfloat16 precision
    val unbiasedExp = (exp shr 20).toLong() - 1023L
    val halfExp = unbiasedExp + 127L

    // Check for exponent overflow, return +infinity
    if (halfExp >= 0xFFL) {
        return (halfSign or 0x7F80u).toUShort()
    }

    // Check for underflow
    if (halfExp <= 0L) {
        if (7L - halfExp > 21L) {
            return halfSign.toUShort()
        }
        val manWithHidden = man or 0x0010_0000u
        var halfMan = manWithHidden shr (14L - halfExp).toInt()
        val roundBit = 1u shl (13L - halfExp).toInt()
        if ((manWithHidden and roundBit) != 0u && (manWithHidden and (3u * roundBit - 1u)) != 0u) {
            halfMan += 1u
        }
        return (halfSign or halfMan).toUShort()
    }

    // Rebias the exponent
    val rebiasedHalfExp = halfExp.toUInt() shl 7
    val halfMan = man shr 13
    val roundBit = 0x0000_1000u
    return if ((man and roundBit) != 0u && (man and (3u * roundBit - 1u)) != 0u) {
        ((halfSign or rebiasedHalfExp or halfMan) + 1u).toUShort()
    } else {
        (halfSign or rebiasedHalfExp or halfMan).toUShort()
    }
}

internal fun bf16ToF32(i: UShort): Float {
    return if ((i.toUInt() and 0x7FFFu) > 0x7F80u) {
        Float.fromBits(((i.toUInt() or 0x0040u) shl 16).toInt())
    } else {
        Float.fromBits((i.toUInt() shl 16).toInt())
    }
}

internal fun bf16ToF64(i: UShort): Double {
    if ((i.toUInt() and 0x7FFFu) == 0u) {
        return Double.fromBits((i.toULong() shl 48).toLong())
    }

    val halfSign = (i.toUInt() and 0x8000u).toULong()
    val halfExp = (i.toUInt() and 0x7F80u).toULong()
    val halfMan = (i.toUInt() and 0x007Fu).toULong()

    if (halfExp == 0x7F80uL) {
        return if (halfMan == 0uL) {
            Double.fromBits(((halfSign shl 48) or 0x7FF0_0000_0000_0000uL).toLong())
        } else {
            Double.fromBits(((halfSign shl 48) or 0x7FF8_0000_0000_0000uL or (halfMan shl 45)).toLong())
        }
    }

    val sign = halfSign shl 48
    val unbiasedExp = (halfExp.toLong() shr 7) - 127L

    if (halfExp == 0uL) {
        val e = leadingZerosU16(halfMan.toUShort()) - 9u
        val exp = (1023uL - 127uL - e.toULong()) shl 52
        val man = (halfMan shl (46 + e.toInt())) and 0xF_FFFF_FFFF_FFFFuL
        return Double.fromBits((sign or exp or man).toLong())
    }

    val exp = (unbiasedExp + 1023L).toULong() shl 52
    val man = (halfMan and 0x007FuL) shl 45
    return Double.fromBits((sign or exp or man).toLong())
}
