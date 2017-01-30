package meialuaesoco.codeGenerator;

public class Variable extends Operand {
	private Constant name;
	
	public Variable(Constant name, int register) {
		super(register);
		this.name = name;
	}

	public Constant getName() {
		return name;
	}
	
	public String toString() {
		return name.getValue().toString();
	}
}
