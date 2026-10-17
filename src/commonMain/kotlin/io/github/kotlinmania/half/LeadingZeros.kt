// port-lint: source leading_zeros.rs
package io.github.kotlinmania.half

internal fun leadingZerosU16(x: UShort): UInt =
    x.countLeadingZeroBits().toUInt()

internal fun leadingZerosU16Fallback(xInput: UShort): UInt {
    var x = xInput.toInt() and 0xFFFF
    var c = 0u
    val msb = 1 shl 15
    for (i in 0 until 16) {
        if ((x and msb) == 0) {
            c += 1u
        } else {
            return c
        }
        if (i < 15) {
            x = (x shl 1) and 0xFFFF
        }
    }
    return c
}
