import Testing
import Half

@Suite("Half Swift Export Tests")
struct HalfExportTests {
    @Test("F16 conversions, classification, bytes, and arithmetic work across Swift Export")
    func f16Operations() {
        let two = F16.Companion.shared.fromF32(value: 2.0)
        #expect(two.toF32() == 2.0)
        #expect(two.toF64() == 2.0)
        #expect(two.toBits() == 0x4000)

        let fromBits = F16.Companion.shared.fromBits(bits: 0x3C00)
        #expect(fromBits == F16.Companion.shared.ONE)
        #expect(fromBits.classify() == FpCategory.Normal)
        #expect(!fromBits.isNan())
        #expect(!fromBits.isInfinite())
        #expect(F16.Companion.shared.NAN.isNan())
        #expect(F16.Companion.shared.INFINITY.isInfinite())
        #expect(F16.Companion.shared.PI.toBits() == 0x4248)

        let four = two + two
        #expect(four.toF32() == 4.0)
        #expect((four - fromBits).toF32() == 3.0)
        #expect((four * two).toF32() == 8.0)
        #expect((four / two).toF32() == 2.0)
        #expect(four > fromBits)

        let leBytes = two.toLeBytes()
        #expect(F16.Companion.shared.fromLeBytes(bytes: leBytes) == two)
    }

    @Test("BF16 conversions, classification, bytes, and arithmetic work across Swift Export")
    func bf16Operations() {
        let two = BF16.Companion.shared.fromF32(value: 2.0)
        #expect(two.toF32() == 2.0)
        #expect(two.toF64() == 2.0)
        #expect(two.toBits() == 0x4000)

        let one = BF16.Companion.shared.ONE
        #expect(one.classify() == FpCategory.Normal)
        #expect(!one.isNan())
        #expect(BF16.Companion.shared.NAN.isNan())
        #expect(BF16.Companion.shared.INFINITY.isInfinite())
        #expect(BF16.Companion.shared.PI.toBits() == 0x4049)

        let six = two * BF16.Companion.shared.fromF32(value: 3.0)
        #expect(six.toF32() == 6.0)
        #expect((six / two).toF32() == 3.0)
        #expect(six > one)

        let beBytes = two.toBeBytes()
        #expect(BF16.Companion.shared.fromBeBytes(bytes: beBytes) == two)
    }
}
