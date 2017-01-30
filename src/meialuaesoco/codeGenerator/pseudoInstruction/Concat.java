package meialuaesoco.codeGenerator.pseudoInstruction;

import java.io.IOException;
import java.io.OutputStream;

import meialuaesoco.codeGenerator.GeneratorInfo;
import meialuaesoco.codeGenerator.IOUtil;
import meialuaesoco.codeGenerator.Operand;
import meialuaesoco.codeGenerator.TempVariable;

public class Concat extends PseudoInstruction {

Operand result, op1, op2;
	
	public Concat(Operand result, Operand op1, Operand op2) {
		this.result = result;
		this.op1 = op1;
		this.op2 = op2;
	}

	public String toASM() {
		return "concat "+result+" "+op2+" "+op1;
	}

	public void writeLuaVMCode(OutputStream output, GeneratorInfo genInfo) throws IOException {
		int r0 = genInfo.topOfStack;
		
		if (op1 instanceof TempVariable)
			IOUtil.writeInt32(toABC(OpCode.OP_MOVE, genInfo.topOfStack++, op1.register(), 0), output);
		else		
			loadOperand(op1, genInfo, output);
		if (op2 instanceof TempVariable)
			IOUtil.writeInt32(toABC(OpCode.OP_MOVE, genInfo.topOfStack++, op2.register(), 0), output);
		else		
			loadOperand(op2, genInfo, output);
		IOUtil.writeInt32(toABC(OpCode.OP_CONCAT, r0, r0, r0+1), output);
		result.setRegister(r0);
		genInfo.topOfStack = r0+1;
	}

	public int getNumRawInstructions() {
		return 3;
	}
}
