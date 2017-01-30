package jlua.codeGenerator.pseudoInstruction;

import java.io.IOException;
import java.io.OutputStream;

import meialuaesoco.codeGenerator.GeneratorInfo;
import jlua.codeGenerator.IOUtil;
import jlua.codeGenerator.Label;
import jlua.codeGenerator.Operand;
import jlua.codeGenerator.TempVariable;

public class JIf extends PseudoInstruction {

	private Label label;
	private Operand op1;

	public JIf(Operand op1, Label label) {
		this.label = label;
		this.op1 = op1;
	}
	
	public String toASM() {
		return "jif "+op1+" "+label.getPseudoOffset();
	}

	public void writeLuaVMCode(OutputStream output, GeneratorInfo genInfo) throws IOException {
		assert label.isResolved();
		
		int r1 = loadOperand(op1, genInfo, output);
		IOUtil.writeInt32(toABC(OpCode.OP_TEST, r1, 0, 0), output);
		int jump = label.getOffset() - (genInfo.offset + getNumRawInstructions()+1);
		IOUtil.writeInt32(toAsBx(OpCode.OP_JMP, 0, jump), output);
	}

	public int getNumRawInstructions() {
		int total = 2;
		if (!(op1 instanceof TempVariable))
			total++;
		return total;
	}
	
}
