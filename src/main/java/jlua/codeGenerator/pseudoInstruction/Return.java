package jlua.codeGenerator.pseudoInstruction;

import java.io.IOException;
import java.io.OutputStream;

import meialuaesoco.codeGenerator.GeneratorInfo;
import jlua.codeGenerator.IOUtil;

public class Return extends PseudoInstruction {

	public int getNumRawInstructions() {
		return 1;
	}

	public String toASM() {
		return "return 0 1";
	}

	public void writeLuaVMCode(OutputStream output, GeneratorInfo genInfo) throws IOException {
		int i = toABC(OpCode.OP_RETURN, 0, 1, 0);
		IOUtil.writeInt32(i, output);
	}

}
