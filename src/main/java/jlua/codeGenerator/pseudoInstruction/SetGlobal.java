package jlua.codeGenerator.pseudoInstruction;

import java.io.IOException;
import java.io.OutputStream;

import jlua.codeGenerator.Constant;
import meialuaesoco.codeGenerator.GeneratorInfo;
import jlua.codeGenerator.IOUtil;
import jlua.codeGenerator.Operand;
import jlua.codeGenerator.Variable;

public class SetGlobal extends PseudoInstruction {
	
	Variable dest;
	Operand source;
	
	public SetGlobal(Variable dest, Operand source) {
		this.dest = dest;
		this.source = source;
	}

	public String toASM() {
		return "mov "+dest+" "+source;
	}

	public void writeLuaVMCode(OutputStream output, GeneratorInfo genInfo) throws IOException {
		int r1 = loadOperand(source, genInfo, output);
		IOUtil.writeInt32(toABx(OpCode.OP_SETGLOBAL, r1, dest.getName().register()), output);
		genInfo.topOfStack--;
	}

	public int getNumRawInstructions() {
		int total = 1;
		if (source instanceof Variable || source instanceof Constant)
			total++;
		return total;
	}

}
