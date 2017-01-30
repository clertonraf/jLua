import meialuaesoco.codeGenerator.IOUtil;
import junit.framework.TestCase;

public class IOUtilTest extends TestCase {

	public void testToLittleEndian32() {
		int value = 0x0A1B2C3D;
		assertEquals("3d2c1b0a", Integer.toHexString(IOUtil.toLittleEndian(value)));

		// com numeros negativos... pq a bosta do java nao tem tipos unsigned e isso muda
		// o comportamento dos operadores << e >>
		value = 0x8A7B6C5D;
		assertEquals("5d6c7b8a", Integer.toHexString(IOUtil.toLittleEndian(value)));
	}

	public void testToLittleEndian64() {
		long value = 0x0A1B2C3D4E5F6879L;
	/*
	 * 0A
	 * 1B
	 * 2C
	 * 3D
	 * 4E
	 * 5F
	 * 68
	 * 79
	 * 
	 */
		assertEquals("79685f4e3d2c1b0a", Long.toHexString(IOUtil.toLittleEndian(value)));
	}

}
