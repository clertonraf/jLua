package jlua.codeGenerator.pseudoInstruction;

import java.io.IOException;
import java.io.OutputStream;

import meialuaesoco.codeGenerator.GeneratorInfo;
import jlua.codeGenerator.IOUtil;
import jlua.codeGenerator.Operand;
import jlua.codeGenerator.Variable;

public class Mod extends PseudoInstruction {

Operand result, op1, op2;
	
	public Mod(Operand result, Operand op1, Operand op2) {
		this.result = result;
		this.op1 = op1;
		this.op2 = op2;
	}

	public String toASM() {
		return "mod "+result+" "+op2+" "+op1;
	}

	public void writeLuaVMCode(OutputStream output, GeneratorInfo genInfo) throws IOException {
		int r0 = genInfo.topOfStack;
		int r1 = loadOperandDirect(op1, genInfo, output);
		int r2 = loadOperandDirect(op2, genInfo, output);
		IOUtil.writeInt32(toABC(OpCode.OP_MOD, r0, r1, r2), output);
		result.setRegister(r0);
		genInfo.topOfStack = r0+1;
	}

	public int getNumRawInstructions() {
		int total = 1;
		if (op1 instanceof Variable)
			total++;
		if (op2 instanceof Variable)
			total++;
		return total;
	}	
}