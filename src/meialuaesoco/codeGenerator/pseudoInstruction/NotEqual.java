package meialuaesoco.codeGenerator.pseudoInstruction;

import java.io.IOException;
import java.io.OutputStream;

import meialuaesoco.codeGenerator.IOUtil;
import meialuaesoco.codeGenerator.GeneratorInfo;
import meialuaesoco.codeGenerator.Operand;
import meialuaesoco.codeGenerator.Variable;

public class NotEqual extends PseudoInstruction {

	Operand result, op1, op2;
	
	public NotEqual(Operand result, Operand op1, Operand op2) {
		this.result = result;
		this.op1 = op1;
		this.op2 = op2;
	}

	public String toASM() {
		return "eq "+result+" "+op2+" "+op1;
	}

	public void writeLuaVMCode(OutputStream output, GeneratorInfo genInfo) throws IOException {
		int r0 = genInfo.topOfStack;
		int r1 = loadOperandDirect(op1, genInfo, output);
		int r2 = loadOperandDirect(op2, genInfo, output);
		IOUtil.writeInt32(toABC(OpCode.OP_EQ, 0, r1, r2), output);
		// putarias constantes
		IOUtil.writeInt32(toAsBx(OpCode.OP_JMP, 0, 1), output);
		IOUtil.writeInt32(toABC(OpCode.OP_LOADBOOL, r0, 0, 1), output);
		IOUtil.writeInt32(toABC(OpCode.OP_LOADBOOL, r0, 1, 0), output);

		result.setRegister(r0);
		genInfo.topOfStack = r0+1;
	}

	public int getNumRawInstructions() {
		int total = 4;
		if (op1 instanceof Variable)
			total++;
		if (op2 instanceof Variable)
			total++;
		return total;
	}
}