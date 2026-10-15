#if canImport(Testing)
import Testing
import Half

@Suite("Half Swift Export Tests")
struct HalfExportTests {
    @Test("Half swift module imported cleanly")
    func testSwiftModuleLoads() throws {
        #expect(Bool(true), "Half swift module imported cleanly")
    }
}
#elseif canImport(XCTest)
import XCTest
import Half

final class HalfExportTests: XCTestCase {
    func testSwiftModuleLoads() throws {
        XCTAssertTrue(true, "Half swift module imported cleanly")
    }
}
#endif
