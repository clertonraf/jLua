package meialuaesoco.codeGenerator;


public class Constant extends Operand {
	
	// java e seus enums inuteis onde se precisa digitar uma penca de coisas para fazer quase nada.
	enum Type {
		NIL(0), BOOLEAN(1), NUMBER(3), STRING(4);
		
		private final int v;

		private Type(int v) {
			this.v = v;
		}
		
		public int getValue() {
			return v;
		}
	};
	
	private Type type;
	private Object value;
	
	public Constant(boolean value, int register) {
		this(Type.BOOLEAN, new Boolean(value), register);
	}

	public Constant(double value, int register) {
		this(Type.NUMBER, new Double(value), register);
	}
	
	public Constant(String value, int register) {
		this(value == null ? Type.NIL : Type.STRING, value, register);
	}
	
	private Constant(Type type, Object value, int register) {
		super(register);
		this.type = type;
		this.value = value;
	}
	
	public Type getType() {
		return type;
	}
	
	public Object getValue() {
		return value;
	}
	
	public String toString() {
		if (type == Type.NIL)
			return "nil";
		else
			return type == Type.STRING ? "\""+value.toString()+"\"" : value.toString();
	}

	public String getName() {
		if (value == null)
			return "__null";
		else
			return type == Type.STRING ? value.toString() : "__"+value.toString();
	}
	
	public void setRegister(int register) {
		assert false : "O registrador de uma constante nao pode ser alterado";
	}
}
