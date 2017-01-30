package jlua.codeGenerator.pseudoInstruction;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;

import jlua.codeGenerator.IOUtil;
import meialuaesoco.codeGenerator.GeneratorInfo;
import jlua.codeGenerator.Operand;
import jlua.codeGenerator.TempVariable;
import jlua.codeGenerator.Variable;

public class Call extends PseudoInstruction {

	private Variable funcName;
	private LinkedList<Operand> args;
	private int numRawInstructions;
	private final TempVariable returnValue;
	
	public Call(Variable funcName, TempVariable returnValue, LinkedList<Operand> args) {
		this.funcName = funcName;
		this.returnValue = returnValue;
		this.args = args;
		numRawInstructions = 2+args.size();
	}
	
	public String toASM() {
		return "call "+funcName+" "+returnValue+" "+args;
	}

	public void writeLuaVMCode(OutputStream output, GeneratorInfo genInfo) throws IOException {
		IOUtil.writeInt32(getGlobal(funcName.getName(), genInfo.topOfStack), output);
		int funcNameRegister = genInfo.topOfStack;
		genInfo.topOfStack++;
		
		Iterator<Operand> i = args.descendingIterator();
		while (i.hasNext()) {
			Operand op = i.next();
			if (op instanceof TempVariable)
				IOUtil.writeInt32(toABC(OpCode.OP_MOVE, genInfo.topOfStack++, op.register(), 0), output);
			else
				loadOperand(op, genInfo, output);
		}		
		IOUtil.writeInt32(toABC(OpCode.OP_CALL, funcNameRegister, args.size()+1, 2), output);
		returnValue.setRegister(funcNameRegister);
	}

	public int getNumRawInstructions() {
		return numRawInstructions;
	}
}
