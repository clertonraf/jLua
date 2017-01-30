package meialuaesoco.codeGenerator;

import meialuaesoco.Simbolo;

public enum Action implements Simbolo {
	Operand,
	Add, 
	Sub, 
	Mul,
	Div,
	Pow, 
	Mod, 
	Concat,
	Equal,
	NotEqual,
	LessThen,
	LessEqual,
	GreaterThen,
	GreaterEqual,
	Attribution,
	Call,
	StartArgCounter,
	IncrementArgCounter,
	If,
	Else,
	EndIf,
	While,
	EndWhile,
	Unaria,
	Not,
	And,
	Or;

	public boolean isToken() {
		return false;
	}

	public boolean isAction() {
		return true;
	}
	
	public String toString() {
		return "<"+super.toString()+">";
	}

}
