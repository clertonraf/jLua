package jlua;

public class Config {
	private Arch arch;
	private String output;
	private CompilerMode mode;

	public Config() {
		arch = Arch.x86;
		output = "luac.out";
		mode = CompilerMode.PseudoAssembly;		
	}
	
	public Config(String[] args) {
		this();
		for(int i = 0; i < args.length ; i++) {
			if (args[i].startsWith("-")) {
				if (args[i].equals("-64")) {
					arch = Arch.x86_64;
				} else if (args[i].equals("-32")) {
					arch = Arch.x86;
				} else if (args[i].equals("-asm")) {
					mode = CompilerMode.Assembly;
				} else if (args[i].equals("-pseudoasm")) {
					mode = CompilerMode.PseudoAssembly;
				} 
			} else
				output = args[i];
		}
	}

	public Arch getArch() {
		return arch;
	}

	public String getOutput() {
		return output;
	}

	public CompilerMode getMode() {
		return mode;
	}
}
