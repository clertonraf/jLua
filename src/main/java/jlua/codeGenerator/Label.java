package jlua.codeGenerator;

public class Label {
	private boolean resolved;
	private int id;
	private static int nextId;

	private int offset;
	private int pseudoOffset;

	public Label() {
		id = ++nextId;
	}
	
	public boolean isResolved() {
		return resolved;
	}

	public void setOffset(int offset) {
		this.offset = offset;
		resolved = true;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public String toString() {
		return "LABEL"+id;
	}
	
	public void setPseudoOffset(int value) {
		pseudoOffset = value;
	}
	
	public int getPseudoOffset() {
		return pseudoOffset;
	}
}
