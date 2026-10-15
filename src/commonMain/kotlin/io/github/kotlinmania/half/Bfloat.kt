// port-lint: source bfloat.rs
package io.github.kotlinmania.half

import kotlin.jvm.JvmName
import kotlinx.serialization.Serializable

/**
 * A 16-bit floating point type implementing the `bfloat16` format.
 *
 * The `bfloat16` floating point format is a truncated 16-bit version of the IEEE 754 standard
 * `binary32`, a.k.a `Float`. [BF16] has approximately the same dynamic range as `Float` by
 * having a lower precision than [F16].
 */
@Serializable
public data class BF16(public val rawBits: UShort = 0u) : Comparable<BF16>, SealedHalf {
    public val bits: Short
        get() = rawBits.toShort()

    /**
     * Converts a [BF16] into the underlying bit representation.
     */
    public fun toBits(): UShort = rawBits

    /**
     * Returns the memory representation of the underlying bit representation as a byte array in
     * little-endian byte order.
     */
    public fun toLeBytes(): ByteArray = byteArrayOf(
        (rawBits.toInt() and 0xFF).toByte(),
        ((rawBits.toInt() ushr 8) and 0xFF).toByte(),
    )

    /**
     * Returns the memory representation of the underlying bit representation as a byte array in
     * big-endian (network) byte order.
     */
    public fun toBeBytes(): ByteArray = byteArrayOf(
        ((rawBits.toInt() ushr 8) and 0xFF).toByte(),
        (rawBits.toInt() and 0xFF).toByte(),
    )

    /**
     * Returns the memory representation of the underlying bit representation as a byte array in
     * native byte order.
     */
    public fun toNeBytes(): ByteArray = toLeBytes()

    /**
     * Converts a [BF16] value into a `Float` value.
     */
    public fun toF32(): Float = toF32Const()

    public fun toFloat(): Float = toF32()

    /**
     * Converts a [BF16] value into a `Float` value without hardware intrinsics.
     */
    public fun toF32Const(): Float = bf16ToF32(rawBits)

    /**
     * Converts a [BF16] value into a `Double` value.
     */
    public fun toF64(): Double = toF64Const()

    public fun toDouble(): Double = toF64()

    /**
     * Converts a [BF16] value into a `Double` value without hardware intrinsics.
     */
    public fun toF64Const(): Double = bf16ToF64(rawBits)

    /**
     * Returns `true` if this value is NaN and `false` otherwise.
     */
    public fun isNan(): Boolean = (rawBits.toUInt() and 0x7FFFu) > 0x7F80u

    public fun isNaN(): Boolean = isNan()

    /**
     * Returns `true` if this value is ±∞ and `false` otherwise.
     */
    public fun isInfinite(): Boolean = (rawBits.toUInt() and 0x7FFFu) == 0x7F80u

    /**
     * Returns `true` if this number is neither infinite nor NaN.
     */
    public fun isFinite(): Boolean = (rawBits.toUInt() and 0x7F80u) != 0x7F80u

    /**
     * Returns `true` if the number is neither zero, infinite, subnormal, or NaN.
     */
    public fun isNormal(): Boolean {
        val exp = rawBits.toUInt() and 0x7F80u
        return exp != 0x7F80u && exp != 0u
    }

    /**
     * Returns the floating point category of the number.
     */
    public fun classify(): FpCategory {
        val exp = rawBits.toUInt() and 0x7F80u
        val man = rawBits.toUInt() and 0x007Fu
        return when {
            exp == 0u && man == 0u -> FpCategory.Zero
            exp == 0u -> FpCategory.Subnormal
            exp == 0x7F80u && man == 0u -> FpCategory.Infinite
            exp == 0x7F80u -> FpCategory.Nan
            else -> FpCategory.Normal
        }
    }

    /**
     * Returns a number that represents the sign of `this`.
     */
    public fun signum(): BF16 = when {
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
    public fun copysign(sign: BF16): BF16 =
        BF16(((sign.rawBits.toUInt() and 0x8000u) or (rawBits.toUInt() and 0x7FFFu)).toUShort())

    /**
     * Returns the maximum of the two numbers.
     */
    public fun max(other: BF16): BF16 =
        if (!other.isNan() && (isNan() || other > this)) other else this

    /**
     * Returns the minimum of the two numbers.
     */
    public fun min(other: BF16): BF16 =
        if (!other.isNan() && (isNan() || other < this)) other else this

    /**
     * Restrict a value to a certain interval unless it is NaN.
     */
    public fun clamp(min: BF16, max: BF16): BF16 {
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
    public fun totalCmp(other: BF16): Int {
        var left = toBits().toShort().toInt()
        var right = other.toBits().toShort().toInt()
        left = left xor (((left shr 15) and 0xFFFF) ushr 1)
        right = right xor (((right shr 15) and 0xFFFF) ushr 1)
        return left.compareTo(right)
    }

    public operator fun unaryMinus(): BF16 = BF16((rawBits.toUInt() xor 0x8000u).toUShort())

    public operator fun plus(rhs: BF16): BF16 = fromF32(toF32() + rhs.toF32())

    public operator fun minus(rhs: BF16): BF16 = fromF32(toF32() - rhs.toF32())

    public operator fun times(rhs: BF16): BF16 = fromF32(toF32() * rhs.toF32())

    public operator fun div(rhs: BF16): BF16 = fromF32(toF32() / rhs.toF32())

    public operator fun rem(rhs: BF16): BF16 = fromF32(toF32() % rhs.toF32())

    override fun equals(other: Any?): Boolean {
        if (other !is BF16) return false
        if (isNan() || other.isNan()) return false
        return (rawBits == other.rawBits) || (((rawBits.toUInt() or other.rawBits.toUInt()) and 0x7FFFu) == 0u)
    }

    override fun hashCode(): Int = rawBits.hashCode()

    override fun compareTo(other: BF16): Int {
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
        @JvmName("invokeShort")
        public operator fun invoke(bits: Short): BF16 = BF16(bits.toUShort())

        /**
         * Constructs a [BF16] value from the raw bits.
         */
        public fun fromBits(bits: UShort): BF16 = BF16(bits)

        @JvmName("fromShortBits")
        public fun fromBits(bits: Short): BF16 = BF16(bits.toUShort())

        /**
         * Constructs a [BF16] value from a 32-bit floating point value.
         */
        public fun fromF32(value: Float): BF16 = fromF32Const(value)

        public fun fromFloat(value: Float): BF16 = fromF32(value)

        /**
         * Constructs a [BF16] value from a 32-bit floating point value without intrinsics.
         */
        public fun fromF32Const(value: Float): BF16 = BF16(f32ToBf16(value))

        /**
         * Constructs a [BF16] value from a 64-bit floating point value.
         */
        public fun fromF64(value: Double): BF16 = fromF64Const(value)

        public fun fromDouble(value: Double): BF16 = fromF64(value)

        /**
         * Constructs a [BF16] value from a 64-bit floating point value without intrinsics.
         */
        public fun fromF64Const(value: Double): BF16 = BF16(f64ToBf16(value))

        /**
         * Creates a floating point value from its representation as a byte array in little endian.
         */
        public fun fromLeBytes(bytes: ByteArray): BF16 {
            require(bytes.size >= 2)
            val bits = ((bytes[0].toInt() and 0xFF) or ((bytes[1].toInt() and 0xFF) shl 8)).toUShort()
            return fromBits(bits)
        }

        /**
         * Creates a floating point value from its representation as a byte array in big endian.
         */
        public fun fromBeBytes(bytes: ByteArray): BF16 {
            require(bytes.size >= 2)
            val bits = (((bytes[0].toInt() and 0xFF) shl 8) or (bytes[1].toInt() and 0xFF)).toUShort()
            return fromBits(bits)
        }

        /**
         * Creates a floating point value from its representation as a byte array in native endian.
         */
        public fun fromNeBytes(bytes: ByteArray): BF16 = fromLeBytes(bytes)

        public fun from(x: Byte): BF16 = fromF32(x.toFloat())

        public fun from(x: UByte): BF16 = fromF32(x.toFloat())

        public fun fromStr(src: String): BF16 = fromF32(src.toFloat())

        public fun product(iter: Iterable<BF16>): BF16 {
            var acc = 1.0f
            for (x in iter) {
                acc *= x.toF32()
            }
            return fromF32(acc)
        }

        public fun sum(iter: Iterable<BF16>): BF16 {
            var acc = 0.0f
            for (x in iter) {
                acc += x.toF32()
            }
            return fromF32(acc)
        }

        public const val DIGITS: UInt = 2u
        public val EPSILON: BF16 = BF16(0x3C00u)
        public val INFINITY: BF16 = BF16(0x7F80u)
        public const val MANTISSA_DIGITS: UInt = 8u
        public val MAX: BF16 = BF16(0x7F7Fu)
        public const val MAX_10_EXP: Int = 38
        public const val MAX_EXP: Int = 128
        public val MIN: BF16 = BF16(0xFF7Fu)
        public const val MIN_10_EXP: Int = -37
        public const val MIN_EXP: Int = -125
        public val MIN_POSITIVE: BF16 = BF16(0x0080u)
        public val NAN: BF16 = BF16(0x7FC0u)
        public val NEG_INFINITY: BF16 = BF16(0xFF80u)
        public const val RADIX: UInt = 2u

        public val MIN_POSITIVE_SUBNORMAL: BF16 = BF16(0x0001u)
        public val MAX_SUBNORMAL: BF16 = BF16(0x007Fu)

        public val ONE: BF16 = BF16(0x3F80u)
        public val ZERO: BF16 = BF16(0x0000u)
        public val NEG_ZERO: BF16 = BF16(0x8000u)
        public val NEG_ONE: BF16 = BF16(0xBF80u)

        public val E: BF16 = BF16(0x402Eu)
        public val PI: BF16 = BF16(0x4049u)
        public val FRAC_1_PI: BF16 = BF16(0x3EA3u)
        public val FRAC_1_SQRT_2: BF16 = BF16(0x3F35u)
        public val FRAC_2_PI: BF16 = BF16(0x3F23u)
        public val FRAC_2_SQRT_PI: BF16 = BF16(0x3F90u)
        public val FRAC_PI_2: BF16 = BF16(0x3FC9u)
        public val FRAC_PI_3: BF16 = BF16(0x3F86u)
        public val FRAC_PI_4: BF16 = BF16(0x3F49u)
        public val FRAC_PI_6: BF16 = BF16(0x3F06u)
        public val FRAC_PI_8: BF16 = BF16(0x3EC9u)
        public val LN_10: BF16 = BF16(0x4013u)
        public val LN_2: BF16 = BF16(0x3F31u)
        public val LOG10_E: BF16 = BF16(0x3EDEu)
        public val LOG10_2: BF16 = BF16(0x3E9Au)
        public val LOG2_E: BF16 = BF16(0x3FB9u)
        public val LOG2_10: BF16 = BF16(0x4055u)
        public val SQRT_2: BF16 = BF16(0x3FB5u)
    }
}

public typealias bf16 = BF16
public typealias Bf16 = BF16
public typealias BFloat16 = BF16
