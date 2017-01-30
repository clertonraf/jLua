package meialuaesoco.codeGenerator;

public class TempVariable extends Operand {

	private static int nextId;
	private int id;
	
	public TempVariable() {
		super(0);
		id = nextId++;
	}

	public String toString() {
		return "_t"+id;
	}
}
