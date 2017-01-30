package jlua.codeGenerator.pseudoInstruction;

import java.io.IOException;
import java.io.OutputStream;

import jlua.codeGenerator.Constant;
import jlua.codeGenerator.IOUtil;
import meialuaesoco.codeGenerator.GeneratorInfo;
import jlua.codeGenerator.Operand;
import jlua.codeGenerator.Variable;

public abstract class PseudoInstruction {

	public abstract void writeLuaVMCode(OutputStream output, GeneratorInfo genInfo) throws IOException;

	public abstract String toASM();
	
	public abstract int getNumRawInstructions();

	protected int getGlobal(Constant c, int topOfStack) {
		return toABx(OpCode.OP_GETGLOBAL, topOfStack, c.register());
	}
	
	protected int loadK(Constant c, int topOfStack) {
		return toABx(OpCode.OP_LOADK, topOfStack, c.register());
	}
	
	protected int loadOperandDirect(Operand op, GeneratorInfo topOfStack, OutputStream output) throws IOException {
		if (op instanceof Variable) {
			Variable v = (Variable) op;
			IOUtil.writeInt32(getGlobal(v.getName(), topOfStack.topOfStack), output);
		} else if (op instanceof Constant) {
			Constant c = (Constant) op;
			return c.register()+256;
		} else
			return op.register();
		return topOfStack.topOfStack++;
	}

	protected int loadOperand(Operand op, GeneratorInfo topOfStack, OutputStream output) throws IOException {
		if (op instanceof Variable) {
			Variable v = (Variable) op;
			IOUtil.writeInt32(getGlobal(v.getName(), topOfStack.topOfStack), output);
		} else if (op instanceof Constant) {
			Constant c = (Constant) op;
			IOUtil.writeInt32(loadK(c, topOfStack.topOfStack), output);
		} else
			return op.register();
		return topOfStack.topOfStack++;
	}
	
	public static int toABC(OpCode opcode, int a, int b, int c) {
		int instr = 0;
		instr |= opcode.ordinal();
		instr |= a << 6;
		instr |= c << (6+8);
		instr |= b << (6+8+9);
		printInstruction(instr, opcode, a, b, c);
		return instr;
	}

	public static int toABx(OpCode opcode, int a, int bx) {
		int instr = 0;
		instr |= opcode.ordinal();
		instr |= a << 6;
		instr |= bx << (6+8);	
		printInstruction(instr, opcode, a, bx);
		return instr;
	}
	
	public static int toAsBx(OpCode opcode, int a, int bx) {
		int instr = 0;
		instr |= opcode.ordinal();
		instr |= a << 6;
		instr |= (bx+131071) << (6+8);
		printInstruction(instr, opcode, a, bx);
		return instr;
	}
	
	private static void printInstruction(int instr, OpCode opcode, Integer ... params) {
//		System.out.printf("%08X", instr);		
		System.out.print("\t"+opcode+" ");
		for(Integer i : params)
			System.out.print(i+" ");
		System.out.println();
	}
}
