package jlua.codeGenerator;

/**
 * Operando de uma operação qualquer.
 */
public class Operand {
	
	private int register;
	
	public Operand(int register) {
		this.register = register;
	}
	
	/**
	 * Retorna o número do registrador onde será colocado o operando.
	 * O registrador não é realmente um registrador... na verdade é
	 * uma pilha de operandos... porém seu uso se parece com o de registradores. 
	 */
	public int register() {
		return register;
	}
	
	public void setRegister(int value) {
		register = value;
	}

}
