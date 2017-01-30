package meialuaesoco.codeGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;

import meialuaesoco.Arch;
import meialuaesoco.CompilerMode;
import meialuaesoco.Token;
import meialuaesoco.codeGenerator.pseudoInstruction.Add;
import meialuaesoco.codeGenerator.pseudoInstruction.And;
import meialuaesoco.codeGenerator.pseudoInstruction.Call;
import meialuaesoco.codeGenerator.pseudoInstruction.Concat;
import meialuaesoco.codeGenerator.pseudoInstruction.Div;
import meialuaesoco.codeGenerator.pseudoInstruction.Equal;
import meialuaesoco.codeGenerator.pseudoInstruction.InitIO;
import meialuaesoco.codeGenerator.pseudoInstruction.JIf;
import meialuaesoco.codeGenerator.pseudoInstruction.Jmp;
import meialuaesoco.codeGenerator.pseudoInstruction.LessEqual;
import meialuaesoco.codeGenerator.pseudoInstruction.LessThan;
import meialuaesoco.codeGenerator.pseudoInstruction.Mod;
import meialuaesoco.codeGenerator.pseudoInstruction.Mul;
import meialuaesoco.codeGenerator.pseudoInstruction.Not;
import meialuaesoco.codeGenerator.pseudoInstruction.NotEqual;
import meialuaesoco.codeGenerator.pseudoInstruction.Or;
import meialuaesoco.codeGenerator.pseudoInstruction.Pow;
import meialuaesoco.codeGenerator.pseudoInstruction.PseudoInstruction;
import meialuaesoco.codeGenerator.pseudoInstruction.Return;
import meialuaesoco.codeGenerator.pseudoInstruction.SetGlobal;
import meialuaesoco.codeGenerator.pseudoInstruction.Sub;
import meialuaesoco.codeGenerator.pseudoInstruction.Unm;
public class CodeGenerator {

	// aonde o code generator vai cuspir a saída.
	private OutputStream output;
	// regitro de todas as ações
	private HashMap<Action, Method> actions;
	// arquitetura para o qual será gerado o código
	private Arch arch;
	// modo que o gerador de código irá trabalhar
	private CompilerMode mode;

	// tabela de constantes
	private ConstTable constTable;
	// pilha de operandos
	private LinkedList<Operand> pco;
	// pilha de contador de argumentos para funções
	private LinkedList<Integer> argCounter;
	// pilha de labels
	private LinkedList<Label> pcl;
	
	// lista de pseudo instrucoes geradas
	private LinkedList<LinkedList<PseudoInstruction>> pseudoInstructions;
	
	public CodeGenerator(OutputStream output) throws Exception {
		this.output = output;
		arch = Arch.x86;
		mode = CompilerMode.Assembly;
		constTable = new ConstTable();
		pco = new LinkedList<Operand>();
		pcl = new LinkedList<Label>();
		argCounter = new LinkedList<Integer>();
		actions = new HashMap<Action, Method>();
		pseudoInstructions = new LinkedList<LinkedList<PseudoInstruction>>();
		pseudoInstructions.add(new LinkedList<PseudoInstruction>());
 		
		// registra as ações
		for (Action action : Action.values()) {
			registerAction(action, "doAction"+action.name());
		}

		Constant read = constTable.addConstant("read");
		Constant io = constTable.addConstant("io");
		addInstruction(new InitIO(read, io));
	}

	public void setArch(Arch arch) {
		this.arch = arch;
	}
	
	public void setMode(CompilerMode mode) {
		this.mode = mode;
	}
	
	public void finish() throws IOException {
		// Adiciona return final.
		addInstruction(new Return());
		if (mode == CompilerMode.Assembly) {
			writeHeader();
			writeFunctionBlock(pseudoInstructions.peek(), output);
		} else {
			LinkedList<PseudoInstruction> list = pseudoInstructions.peek();
			int i = 0;
			for (PseudoInstruction instr : list) {
				System.out.println("["+(i++)+"] "+instr.toASM());
			}
		}
		output.close();
	}
	
	private void writeFunctionBlock(LinkedList<PseudoInstruction> instructions, OutputStream output) throws IOException {
		// nome da função... pode ser null
		IOUtil.writeString(null, output, arch);
		// linhas onde começa e termina a definição da função, acho que podemos ignorar
		IOUtil.writeInt32(0, output);
		IOUtil.writeInt32(0, output);
		// número de upvalues
		output.write(0);
		// número de parametros
		output.write(0);
		// var_arg
		output.write(2);
		// tamanho max. da pilha.
		// isso deveria ser calculado pelo compilador...
		// mas vamos definir uma constante grande o bastante :-)
		output.write(100);
		
		// Lista de instruções
		int instrCount = 0;
		for (PseudoInstruction i : instructions)
			instrCount += i.getNumRawInstructions();
		IOUtil.writeInt32(instrCount, output);
		System.out.println("; Número de instruções: "+instrCount);
		
		GeneratorInfo genInfo = new GeneratorInfo();
		genInfo.offset = 0;
		for (PseudoInstruction i : instructions) {
			System.out.println("; "+i.toASM());
			i.writeLuaVMCode(output, genInfo);
			genInfo.offset += i.getNumRawInstructions();
		}
		
		// Lista de constantes
		System.out.println("; Tabela de Constantes");
		IOUtil.writeInt32(constTable.getNumConstants(), output);
		for (Constant c : constTable.getConstants()) {
			System.out.println("; ["+c.register()+"]: "+c.getValue());
			output.write(c.getType().getValue());
			if (c.getType() == Constant.Type.STRING) {
				IOUtil.writeString((String) c.getValue(), output, arch);
			} else if (c.getType() == Constant.Type.NUMBER) {
				IOUtil.writeInt64(Double.doubleToLongBits((Double)c.getValue()), output);
			} else if (c.getType() == Constant.Type.BOOLEAN) {
				output.write(((Boolean)c.getValue()).booleanValue() ? 1 : 0);
			}
		}
		
		// function prototypes...
		// por enquanto porra nenhuma...
		IOUtil.writeInt32(0, output);
		// coisas opcionais para debug...
		IOUtil.writeInt32(0, output); // sizelineinfo
		IOUtil.writeInt32(0, output); // sizelocvars
		IOUtil.writeInt32(0, output); // sizeupvalues
	}

	private void registerAction(Action action, String method) throws Exception {
		actions.put(action, CodeGenerator.class.getMethod(method, Token.class));
	}
	
	private void addInstruction(PseudoInstruction i) {
		pseudoInstructions.peekLast().add(i);
	}

	private void writeHeader() throws IOException {
		byte[] signature = {0x1B, 0x4C, 0x75, 0x61};
		output.write(signature);
		output.write(0x51); // lua version
		output.write(0); // formato original
		output.write(1); // littleendian
		output.write(4); // size of int
		output.write(arch.getSizeOfSize_t()); // size of size_t
		output.write(4); // size of instructions
		output.write(8); // size of number
		output.write(0); // type of number, 0 = double, 1 = int
	}

	private void resolvLabel(Label label) {
		int offset = 1;
		LinkedList<PseudoInstruction> list = pseudoInstructions.peekLast();
		for (PseudoInstruction instr : list)
			offset += instr.getNumRawInstructions();
		label.setOffset(offset);
		label.setPseudoOffset(list.size());
//		System.out.println("RESOLVENDO LABEL "+label+" offset: "+offset);
	}
	
	public void doAction(Action action, Token token) throws Exception {
		actions.get(action).invoke(this, token);
	}

	public void doActionIncrementArgCounter(Token token) {
		argCounter.push(argCounter.pop()+1);
	}

	public void doActionStartArgCounter(Token token) {
		argCounter.push(0);		
	}

	public void doActionCall(Token token) {
		int nargs = argCounter.pop();
		LinkedList<Operand> args = new LinkedList<Operand>();
		for (int i = 0; i < nargs; ++i)
			args.add(pco.pop());
		Variable funcName = (Variable)pco.pop();
		TempVariable returnValue = new TempVariable();
		addInstruction(new Call((Variable)funcName, returnValue, args));
		pco.push(returnValue);
	}

	public void doActionAdd(Token token) {
		Operand op1 = pco.pop();
		Operand op2 = pco.pop();
		TempVariable result = new TempVariable();
		addInstruction(new Add(result, op2, op1));
		pco.push(result);
	}

	public void doActionSub(Token token) {
		Operand op1 = pco.pop();
		Operand op2 = pco.pop();
		TempVariable result = new TempVariable();
		addInstruction(new Sub(result, op2, op1));
		pco.push(result);
	}

	public void doActionMul(Token token) {
		Operand op1 = pco.pop();
		Operand op2 = pco.pop();
		TempVariable result = new TempVariable();
		addInstruction(new Mul(result, op2, op1));
		pco.push(result);
	}

	public void doActionDiv(Token token) {
		Operand op1 = pco.pop();
		Operand op2 = pco.pop();
		TempVariable result = new TempVariable();
		addInstruction(new Div(result, op2, op1));
		pco.push(result);
	}

	public void doActionPow(Token token) {
		Operand op1 = pco.pop();
		Operand op2 = pco.pop();
		TempVariable result = new TempVariable();
		addInstruction(new Pow(result, op2, op1));
		pco.push(result);
	}

	public void doActionMod(Token token) {
		Operand op1 = pco.pop();
		Operand op2 = pco.pop();
		TempVariable result = new TempVariable();
		addInstruction(new Mod(result, op2, op1));
		pco.push(result);
	}

	public void doActionConcat(Token token) {
		Operand op1 = pco.pop();
		Operand op2 = pco.pop();
		TempVariable result = new TempVariable();
		addInstruction(new Concat(result, op2, op1));
		pco.push(result);
	}
	
	public void doActionAttribution(Token token) {
		Operand op1 = pco.pop();
		Operand op2 = pco.pop();
		addInstruction(new SetGlobal((Variable)op2, op1));
	}

	public void doActionOperand(Token token) {
		Operand op = null;
		if (token.equals(Token.Identificador)) {
			Constant cto = constTable.addConstant(token.getValue());
			op = new Variable(cto, pco.size());
		} else if (token.equals(Token.String))
			op = constTable.addConstant(token.getValue());
		else if (token.equals(Token.Digito)) {
			op = constTable.addConstant(Double.parseDouble(token.getValue()));
		} else if (token.equals(Token.Nil))
			op = constTable.addConstant(null);
		else if (token.equals(Token.True))
			op = constTable.addConstant(true);
		else if (token.equals(Token.False))
			op = constTable.addConstant(false);

		assert op != null : "token errado para ação Operando: "+token;
		pco.push(op);
	}

	public void doActionIf(Token token) {
		Label label = new Label();
		pcl.push(label);
		addInstruction(new JIf(pco.pop(), label));
	}
	
	public void doActionElse(Token token) {
		Label label1 = pcl.pop();
		Label label2 = new Label();
		pcl.push(label2);
		addInstruction(new Jmp(label2));
		resolvLabel(label1);
	}

	public void doActionEndIf(Token token) {
		resolvLabel(pcl.pop());
	}
	
	//ciclo de inicio do while
	public void doActionWhile(Token token) {
		Label label1 = new Label();
		resolvLabel(label1);
		pcl.push(label1);
	}
	
	//fim do while
	public void doActionEndWhile(Token token) {
		Label label1 = pcl.pop();
		Label label2 = pcl.pop();
		addInstruction(new Jmp(label2));
		resolvLabel(label1);
	}
	
	//operacao binaria de comparacao: igualdade
	public void doActionEqual(Token token) {
		Operand op1 = pco.pop();
		Operand op2 = pco.pop();
		TempVariable result = new TempVariable();
		addInstruction(new Equal(result, op2, op1));
		pco.push(result);
	}
	
	//operacao binaria de comparacao: Menor que.
	public void doActionLessThen(Token token) {
		Operand op1 = pco.pop();
		Operand op2 = pco.pop();
		TempVariable result = new TempVariable();
		addInstruction(new LessThan(result, op2, op1));
		pco.push(result);
	}
	
	//operacao binaria de comparacao: Menor igual.
	public void doActionLessEqual(Token token) {
		Operand op1 = pco.pop();
		Operand op2 = pco.pop();
		TempVariable result = new TempVariable();
		addInstruction(new LessEqual(result, op2, op1));
		pco.push(result);
	}
	
	public void doActionGreaterThen(Token token) {
		Operand op1 = pco.pop();
		Operand op2 = pco.pop();
		TempVariable result = new TempVariable();
		addInstruction(new LessThan(result, op1, op2));
		pco.push(result);
	}
	
	public void doActionGreaterEqual(Token token) {
		Operand op1 = pco.pop();
		Operand op2 = pco.pop();
		TempVariable result = new TempVariable();
		addInstruction(new LessEqual(result, op1, op2));
		pco.push(result);
	}
	
	public void doActionUnaria(Token token){
		Operand op1 = pco.pop();
		TempVariable result = new TempVariable();
		addInstruction(new Unm(result, op1));
		pco.push(result);
	}
	
	public void doActionNot(Token token){
		Operand op1 = pco.pop();
		TempVariable result = new TempVariable();
		addInstruction(new Not(result, op1));
		pco.push(result);
	}
	
	public void doActionAnd(Token token) {
		Operand op1 = pco.pop();
		Operand op2 = pco.pop();
		TempVariable result = new TempVariable();
		addInstruction(new And(result, op1, op2));
		pco.push(result);
	}
	
	public void doActionOr(Token token) {
		Operand op1 = pco.pop();
		Operand op2 = pco.pop();
		TempVariable result = new TempVariable();
		addInstruction(new Or(result, op1, op2));
		pco.push(result);		
	}

	public void doActionNotEqual(Token token) {
		Operand op1 = pco.pop();
		Operand op2 = pco.pop();
		TempVariable result = new TempVariable();
		addInstruction(new NotEqual(result, op2, op1));
		pco.push(result);
	}
}
