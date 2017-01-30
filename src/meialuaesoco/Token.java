package meialuaesoco;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.regex.Pattern;

/**
 * Tokens da linguagem, cada token possui uma expressão regular que o idêntifica no texto e um valor semântico ({@link #getValue()}) associado.
 * 
 * A ordem dos itens desta enumeração é importante! Pois ela é usada na hora de identificar os tokens, uma coleção com todos os tokens em ordem pode ser adquirida através do método estático {{@link #getTokenList()}.
 */
public enum Token implements Simbolo {
	
	// Todos os tokens por ordem de prioridade

	// Palavras reservadas
	And("and"),
	Break("break"),
	Do("do"),
	Else("else"),
	End("end"),
	False("false"),
	Function("function"),
	If("if"),
	Nil("nil"),
	Not("not"),
	Or("or"),
	Return("return"),
	Then("then"),
	True("true"),
	Until("until"),
	While("while"),
	
	// Operadores
	OperadorSoma("\\+"),
	OperadorSubtracao("-"),
	OperadorDivisao("/"),
	OperadorMultiplicacao("\\*"),
	OperadorResto("%"),
	OperadorIgual("=="),
	OperadorMaiorIgual(">="),
	OperadorMaiorQue(">"),
	OperadorMenorIgual("<="),
	OperadorMenorQue("<"),
	OperadorDiferenca("~="),
	OperadorExponenciacao("\\^"),

	// outros
	Atribuicao("="),
	String("\""),
	Digito("\\d+\\.?\\d*"),
	Virgula(","),
	PontoVirgula(";"),
	PontoPonto("\\.\\."),
	ParentesesAberto("\\("),
	ParentesesFechado("\\)"),
	// identificador
	Identificador("[a-zA-Z_]+([0-9]*[a-zA-Z_]*)*"),
	
	FimDeArquivo("");
	
	private Pattern regex;
	private String value;
	private static Collection<Token> tokens;
	
	static {
		EnumMap<Token, Token> tokenMap = new EnumMap<Token, Token>(Token.class);
		for (Token t : EnumSet.allOf(Token.class))
			tokenMap.put(t, t);
		tokens = tokenMap.values();
	}
	
	private Token(String regex) {
		this.regex = Pattern.compile("^(\\s)*("+regex+")");
	}
	
	public boolean isToken() {
		return true;
	}

	/**
	 * Retorna uma lista com todos os tokens.
	 */
	public static Collection<Token> getTokenList() {
		return tokens;
	}

	/**
	 * Retorna a expressão regular que reconhece este token.
	 */
	public Pattern getRegex() {
		return regex;
	}

	/**
	 * Altera o valor semântico deste token.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Retorna o valor semântico do token.
	 */
	public String getValue() {
		return value;
	}

	public boolean isAction() {
		return false;
	}
}
