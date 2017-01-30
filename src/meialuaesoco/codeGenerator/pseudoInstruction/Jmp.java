package meialuaesoco.codeGenerator.pseudoInstruction;

import java.io.IOException;
import java.io.OutputStream;

import meialuaesoco.codeGenerator.IOUtil;
import meialuaesoco.codeGenerator.GeneratorInfo;
import meialuaesoco.codeGenerator.Label;

public class Jmp extends PseudoInstruction {

	private Label label;
	
	public Jmp(Label label) {
		this.label = label;
	}
	
	public String toASM() {
		return "jmp "+label.getPseudoOffset();
	}

	public void writeLuaVMCode(OutputStream output, GeneratorInfo genInfo) throws IOException {
		assert label.isResolved();
		System.out.println("JUMP "+genInfo.offset+" jump to "+label.getOffset());
		IOUtil.writeInt32(toAsBx(OpCode.OP_JMP, 0, label.getOffset() - genInfo.offset - 2), output);		
	}

	public int getNumRawInstructions() {
		return 1;
	}

}
