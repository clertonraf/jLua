package jlua.codeGenerator.pseudoInstruction;

import java.io.IOException;
import java.io.OutputStream;

import meialuaesoco.codeGenerator.GeneratorInfo;
import jlua.codeGenerator.IOUtil;
import jlua.codeGenerator.Operand;
import jlua.codeGenerator.TempVariable;

public class Unm extends PseudoInstruction{
	Operand result, op1;
	
	public Unm(Operand result, Operand op1) {
		this.result = result;
		this.op1 = op1;
	}

	public String toASM() {
		return "unm "+ result + " " + op1;
	}

	public void writeLuaVMCode(OutputStream output, GeneratorInfo genInfo) throws IOException {
		int r0 = genInfo.topOfStack;
		int r1 = loadOperand(op1, genInfo, output);
		IOUtil.writeInt32(toABC(OpCode.OP_UNM, r0, r1, 0), output);
		result.setRegister(r0);
		genInfo.topOfStack = r0+1;
	}

	public int getNumRawInstructions() {
		return (op1 instanceof TempVariable) ? 1 : 2;
	}
}
