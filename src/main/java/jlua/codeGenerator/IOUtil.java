package jlua.codeGenerator;

import java.io.IOException;
import java.io.OutputStream;

import jlua.Arch;

public class IOUtil {

	public static int toLittleEndian(int value) {
		return  (value >>> 24) | // byte 1 (MSB)
				((value & 0x00FF0000) >>> 8) | // byte 2
				((value & 0x0000FF00) << 8) | // byte 3
				(value << 24); // byte 4 (LSB)
	}

	public static long toLittleEndian(long value) {
		return  (value >>> 56) | // byte 1 (MSB)
				((value & 0x00FF000000000000L) >>> 40) | // byte 2
				((value & 0x0000FF0000000000L) >>> 24) | // byte 3
				((value & 0x000000FF00000000L) >>> 8) | // byte 4
				((value & 0x00000000FF000000L) << 8) | // byte 5
				((value & 0x0000000000FF0000L) << 24) | // byte 6
				((value & 0x000000000000FF00L) << 40) | // byte 7
				(value << 56); // byte 8 (LSB)
	}

	public static void writeInt32(int dword, OutputStream output) throws IOException {
		dword = toLittleEndian(dword);
		for (int i = 3; i >= 0; --i)
			output.write(dword >>> (i*8));
	}

	public static void writeInt64(long dlword, OutputStream output) throws IOException {
		dlword = toLittleEndian(dlword);
		for (int i = 7; i >= 0; --i)
			output.write((int) (dlword >>> (i*8)));
	}

	public static void writeString(String str, OutputStream output, Arch arch) throws IOException {
		int stringSize = str == null ? 0 : str.length()+1;
		if (arch.equals(Arch.x86))
			writeInt32(stringSize, output);
		else
			writeInt64(stringSize, output);

		if (str != null) {
			output.write(str.getBytes());
			output.write(0);
		}
	}

}
