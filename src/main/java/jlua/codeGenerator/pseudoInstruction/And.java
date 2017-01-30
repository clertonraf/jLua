package jlua.codeGenerator.pseudoInstruction;

import java.io.IOException;
import java.io.OutputStream;

import meialuaesoco.codeGenerator.GeneratorInfo;
import jlua.codeGenerator.Operand;
import jlua.codeGenerator.TempVariable;

public class And extends PseudoInstruction {

	private TempVariable result;
	private Operand op1;
	private Operand op2;

	public And(TempVariable result, Operand op1, Operand op2) {
		this.result = result;
		this.op1 = op1;
		this.op2 = op2;
	}

	public String toASM() {
		return "and "+result+" "+op1+" "+op2;
	}

	public void writeLuaVMCode(OutputStream output, GeneratorInfo genInfo)
			throws IOException {
	}

	public int getNumRawInstructions() {
		return 0;
	}
}
