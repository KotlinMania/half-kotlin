// port-lint: source binary16.rs
package io.github.kotlinmania.half

/**
 * Floating-point category matching Rust `core::num::FpCategory`.
 */
public enum class FpCategory {
    Nan,
    Infinite,
    Zero,
    Subnormal,
    Normal,
}

/**
 * A 16-bit floating point type implementing the IEEE 754-2008 standard `binary16` a.k.a "half"
 * format.
 *
 * This 16-bit floating point type is intended for efficient storage where the full range and
 * precision of a larger floating point value is not required.
 */
public data class F16(
    public val rawBits: UShort = 0u,
) : Comparable<F16>,
    SealedHalf {
    public val bits: Short
        get() = rawBits.toShort()

    /**
     * Converts a [F16] into the underlying bit representation.
     */
    public fun toBits(): UShort = rawBits

    /**
     * Returns the memory representation of the underlying bit representation as a byte array in
     * little-endian byte order.
     */
    public fun toLeBytes(): ByteArray =
        byteArrayOf(
            (rawBits.toInt() and 0xFF).toByte(),
            ((rawBits.toInt() ushr 8) and 0xFF).toByte(),
        )

    /**
     * Returns the memory representation of the underlying bit representation as a byte array in
     * big-endian (network) byte order.
     */
    public fun toBeBytes(): ByteArray =
        byteArrayOf(
            ((rawBits.toInt() ushr 8) and 0xFF).toByte(),
            (rawBits.toInt() and 0xFF).toByte(),
        )

    /**
     * Returns the memory representation of the underlying bit representation as a byte array in
     * native byte order.
     */
    public fun toNeBytes(): ByteArray = toLeBytes()

    /**
     * Converts a [F16] value into a `Float` value.
     */
    public fun toF32(): Float = f16ToF32(rawBits)

    public fun toFloat(): Float = toF32()

    /**
     * Converts a [F16] value into a `Float` value without hardware intrinsics.
     */
    public fun toF32Const(): Float = f16ToF32Fallback(rawBits)

    /**
     * Converts a [F16] value into a `Double` value.
     */
    public fun toF64(): Double = f16ToF64(rawBits)

    public fun toDouble(): Double = toF64()

    /**
     * Converts a [F16] value into a `Double` value without hardware intrinsics.
     */
    public fun toF64Const(): Double = f16ToF64Fallback(rawBits)

    /**
     * Returns `true` if this value is `NaN` and `false` otherwise.
     */
    public fun isNan(): Boolean = (rawBits.toUInt() and 0x7FFFu) > 0x7C00u

    public fun isNaN(): Boolean = isNan()

    /**
     * Returns `true` if this value is ±∞ and `false` otherwise.
     */
    public fun isInfinite(): Boolean = (rawBits.toUInt() and 0x7FFFu) == 0x7C00u

    /**
     * Returns `true` if this number is neither infinite nor `NaN`.
     */
    public fun isFinite(): Boolean = (rawBits.toUInt() and 0x7C00u) != 0x7C00u

    /**
     * Returns `true` if the number is neither zero, infinite, subnormal, or `NaN`.
     */
    public fun isNormal(): Boolean {
        val exp = rawBits.toUInt() and 0x7C00u
        return exp != 0x7C00u && exp != 0u
    }

    /**
     * Returns the floating point category of the number.
     */
    public fun classify(): FpCategory {
        val exp = rawBits.toUInt() and 0x7C00u
        val man = rawBits.toUInt() and 0x03FFu
        return when {
            exp == 0u && man == 0u -> FpCategory.Zero
            exp == 0u -> FpCategory.Subnormal
            exp == 0x7C00u && man == 0u -> FpCategory.Infinite
            exp == 0x7C00u -> FpCategory.Nan
            else -> FpCategory.Normal
        }
    }

    /**
     * Returns a number that represents the sign of `this`.
     */
    public fun signum(): F16 =
        when {
            isNan() -> this
            (rawBits.toUInt() and 0x8000u) != 0u -> NEG_ONE
            else -> ONE
        }

    /**
     * Returns `true` if and only if `this` has a positive sign.
     */
    public fun isSignPositive(): Boolean = (rawBits.toUInt() and 0x8000u) == 0u

    /**
     * Returns `true` if and only if `this` has a negative sign.
     */
    public fun isSignNegative(): Boolean = (rawBits.toUInt() and 0x8000u) != 0u

    /**
     * Returns a number composed of the magnitude of `this` and the sign of `sign`.
     */
    public fun copysign(sign: F16): F16 =
        F16(((sign.rawBits.toUInt() and 0x8000u) or (rawBits.toUInt() and 0x7FFFu)).toUShort())

    /**
     * Returns the maximum of the two numbers.
     */
    public fun max(other: F16): F16 =
        if (!other.isNan() && (isNan() || other > this)) other else this

    /**
     * Returns the minimum of the two numbers.
     */
    public fun min(other: F16): F16 =
        if (!other.isNan() && (isNan() || other < this)) other else this

    /**
     * Restrict a value to a certain interval unless it is NaN.
     */
    public fun clamp(min: F16, max: F16): F16 {
        require(!min.isNan() && !max.isNan() && min <= max)
        if (isNan()) return this
        var x = this
        if (x < min) x = min
        if (x > max) x = max
        return x
    }

    /**
     * Returns the total ordering between `this` and `other`.
     */
    public fun totalCmp(other: F16): Int {
        var left = toBits().toShort().toInt()
        var right = other.toBits().toShort().toInt()
        left = left xor (((left shr 15) and 0xFFFF) ushr 1)
        right = right xor (((right shr 15) and 0xFFFF) ushr 1)
        return left.compareTo(right)
    }

    public operator fun unaryMinus(): F16 = F16((rawBits.toUInt() xor 0x8000u).toUShort())

    public operator fun plus(rhs: F16): F16 = F16(addF16(rawBits, rhs.rawBits))

    public operator fun minus(rhs: F16): F16 = F16(subtractF16(rawBits, rhs.rawBits))

    public operator fun times(rhs: F16): F16 = F16(multiplyF16(rawBits, rhs.rawBits))

    public operator fun div(rhs: F16): F16 = F16(divideF16(rawBits, rhs.rawBits))

    public operator fun rem(rhs: F16): F16 = F16(remainderF16(rawBits, rhs.rawBits))

    override fun equals(other: Any?): Boolean {
        if (other !is F16) return false
        if (isNan() || other.isNan()) return false
        return (rawBits == other.rawBits) || (((rawBits.toUInt() or other.rawBits.toUInt()) and 0x7FFFu) == 0u)
    }

    override fun hashCode(): Int = rawBits.hashCode()

    override fun compareTo(other: F16): Int {
        val neg = (rawBits.toUInt() and 0x8000u) != 0u
        val otherNeg = (other.rawBits.toUInt() and 0x8000u) != 0u
        return when {
            !neg && !otherNeg -> rawBits.compareTo(other.rawBits)
            !neg && otherNeg -> if (((rawBits.toUInt() or other.rawBits.toUInt()) and 0x7FFFu) == 0u) 0 else 1
            neg && !otherNeg -> if (((rawBits.toUInt() or other.rawBits.toUInt()) and 0x7FFFu) == 0u) 0 else -1
            else -> other.rawBits.compareTo(rawBits)
        }
    }

    override fun toString(): String = toF32().toString()

    public companion object {
        public operator fun invoke(bits: UShort): F16 = F16(bits)

        public fun invokeShort(bits: Short): F16 = F16(bits.toUShort())

        /**
         * Constructs a 16-bit floating point value from the raw bits.
         */
        public fun fromBits(bits: UShort): F16 = F16(bits)

        public fun fromShortBits(bits: Short): F16 = F16(bits.toUShort())

        /**
         * Constructs a 16-bit floating point value from a 32-bit floating point value.
         */
        public fun fromF32(value: Float): F16 = F16(f32ToF16(value))

        public fun fromFloat(value: Float): F16 = fromF32(value)

        /**
         * Constructs a 16-bit floating point value from a 32-bit floating point value without intrinsics.
         */
        public fun fromF32Const(value: Float): F16 = F16(f32ToF16Fallback(value))

        /**
         * Constructs a 16-bit floating point value from a 64-bit floating point value.
         */
        public fun fromF64(value: Double): F16 = F16(f64ToF16(value))

        public fun fromDouble(value: Double): F16 = fromF64(value)

        /**
         * Constructs a 16-bit floating point value from a 64-bit floating point value without intrinsics.
         */
        public fun fromF64Const(value: Double): F16 = F16(f64ToF16Fallback(value))

        /**
         * Creates a floating point value from its representation as a byte array in little endian.
         */
        public fun fromLeBytes(bytes: ByteArray): F16 {
            require(bytes.size >= 2)
            val bits = ((bytes[0].toInt() and 0xFF) or ((bytes[1].toInt() and 0xFF) shl 8)).toUShort()
            return fromBits(bits)
        }

        /**
         * Creates a floating point value from its representation as a byte array in big endian.
         */
        public fun fromBeBytes(bytes: ByteArray): F16 {
            require(bytes.size >= 2)
            val bits = (((bytes[0].toInt() and 0xFF) shl 8) or (bytes[1].toInt() and 0xFF)).toUShort()
            return fromBits(bits)
        }

        /**
         * Creates a floating point value from its representation as a byte array in native endian.
         */
        public fun fromNeBytes(bytes: ByteArray): F16 = fromLeBytes(bytes)

        public fun from(x: Byte): F16 = fromF32(x.toFloat())

        public fun from(x: UByte): F16 = fromF32(x.toFloat())

        public fun fromStr(src: String): F16 = fromF32(src.toFloat())

        public fun product(iter: Iterable<F16>): F16 =
            F16(productF16(iter.map { it.toBits() }.iterator()))

        public fun sum(iter: Iterable<F16>): F16 =
            F16(sumF16(iter.map { it.toBits() }.iterator()))

        public const val DIGITS: UInt = 3u
        public val EPSILON: F16 = F16(0x1400u)
        public val INFINITY: F16 = F16(0x7C00u)
        public const val MANTISSA_DIGITS: UInt = 11u
        public val MAX: F16 = F16(0x7BFFu)
        public const val MAX_10_EXP: Int = 4
        public const val MAX_EXP: Int = 16
        public val MIN: F16 = F16(0xFBFFu)
        public const val MIN_10_EXP: Int = -4
        public const val MIN_EXP: Int = -13
        public val MIN_POSITIVE: F16 = F16(0x0400u)
        public val NAN: F16 = F16(0x7E00u)
        public val NEG_INFINITY: F16 = F16(0xFC00u)
        public const val RADIX: UInt = 2u

        public val MIN_POSITIVE_SUBNORMAL: F16 = F16(0x0001u)
        public val MAX_SUBNORMAL: F16 = F16(0x03FFu)

        public val ONE: F16 = F16(0x3C00u)
        public val ZERO: F16 = F16(0x0000u)
        public val NEG_ZERO: F16 = F16(0x8000u)
        public val NEG_ONE: F16 = F16(0xBC00u)

        public val E: F16 = F16(0x4170u)
        public val PI: F16 = F16(0x4248u)
        public val FRAC_1_PI: F16 = F16(0x3518u)
        public val FRAC_1_SQRT_2: F16 = F16(0x39A8u)
        public val FRAC_2_PI: F16 = F16(0x3918u)
        public val FRAC_2_SQRT_PI: F16 = F16(0x3C83u)
        public val FRAC_PI_2: F16 = F16(0x3E48u)
        public val FRAC_PI_3: F16 = F16(0x3C30u)
        public val FRAC_PI_4: F16 = F16(0x3A48u)
        public val FRAC_PI_6: F16 = F16(0x3830u)
        public val FRAC_PI_8: F16 = F16(0x3648u)
        public val LN_10: F16 = F16(0x409Bu)
        public val LN_2: F16 = F16(0x398Cu)
        public val LOG10_E: F16 = F16(0x36F3u)
        public val LOG10_2: F16 = F16(0x34D1u)
        public val LOG2_E: F16 = F16(0x3DC5u)
        public val LOG2_10: F16 = F16(0x42A5u)
        public val SQRT_2: F16 = F16(0x3DA8u)
    }
}
