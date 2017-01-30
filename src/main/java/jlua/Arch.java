package jlua;

public enum Arch {
	x86(4),
	x86_64(8);
	
	private byte size_t;
	private Arch(int size_t) {
		this.size_t = (byte)size_t; 
	}
	
	public byte getSizeOfSize_t() {
		return size_t;
	}
}
