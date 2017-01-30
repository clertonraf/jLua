package jlua;

import java.io.PrintStream;

public class ErrorReporter {

	private static ErrorReporter self;
	private PrintStream output;
	private int errorCount;
	
	private ErrorReporter(PrintStream output) {
		this.output = output;
	}
	
	public static ErrorReporter getInstance() {
		if (self == null)
			self = new ErrorReporter(System.err);
		return self;
	}
	
	public void setOutput(PrintStream output) {
		this.output = output;
	}

	public void error(int line, String msg) {
		output.println("ERRO, linha "+line+": "+msg);
		errorCount++;
	}
	
	public int getNumErrors() {
		return errorCount;
	}

	public void reset() {
		errorCount = 0;
	}
}
