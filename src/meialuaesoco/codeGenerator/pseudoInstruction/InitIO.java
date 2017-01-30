package meialuaesoco.codeGenerator.pseudoInstruction;

import java.io.IOException;
import java.io.OutputStream;

import meialuaesoco.codeGenerator.Constant;
import meialuaesoco.codeGenerator.GeneratorInfo;
import meialuaesoco.codeGenerator.IOUtil;

public class InitIO extends PseudoInstruction {

	private final Constant read;
	private final Constant io;

	public InitIO(Constant read, Constant io) {
		this.read = read;
		this.io = io;
	}

	public String toASM() {
		return "InitIO";
	}

	public void writeLuaVMCode(OutputStream output, GeneratorInfo genInfo) throws IOException {
		assert genInfo.offset == 0;
		IOUtil.writeInt32(getGlobal(io, 0), output);
		IOUtil.writeInt32(toABC(OpCode.OP_GETTABLE, 0, 0, read.register() | 0x100), output);
		IOUtil.writeInt32(toABx(OpCode.OP_SETGLOBAL, 0, read.register()), output);
	}

	public int getNumRawInstructions() {
		return 3;
	}

}
