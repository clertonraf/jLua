import java.util.LinkedList;

import junit.framework.TestCase;
import meialuaesoco.Token;


public class TokenTest extends TestCase {

	
/*	public void testGetTokenList() {
		Collection<Token> list = Token.getTokenList();
		
		Token[] tokens = {Token.And, Token.Break, Token.Do, Token.Else, Token.End, Token.False, Token.Function, Token.If,
						  Token.Nil, Token.Not, Token.Or, Token.Repeat, Token.Return, Token.Then, Token.True, Token.Until, Token.While,
						  Token.OperadorSoma, Token.OperadorSubtracao, Token.OperadorDivisao, Token.OperadorMultiplicacao, Token.OperadorResto, Token.OperadorIgual,
						  Token.OperadorMaiorIgual, Token.OperadorMaiorQue, Token.OperadorMenorQue, Token.OperadorMenorIgual, Token.OperadorDiferenca,
						  Token.OperadorExponenciacao, Token.Atribuicao, Token.String, Token.Digito, Token.Virgula, Token.PontoVirgula, Token.PontoPonto, Token.ParentesesAberto, Token.ParentesesFechado, Token.Identificador, Token.FimDeArquivo};
		int i = 0;
		for (Token t : list) {
			assertEquals(t, tokens[i++]);
		}
	}*/
	
	public void testParseSimpleToken() throws Exception {
		LinkedList<Token> and = new LinkedList<Token>();
		and.add(Token.And);
	}

	public void testParseMultipleTokens() throws Exception {
		LinkedList<Token> tokens = new LinkedList<Token>();
		tokens.add(Token.Identificador);
		tokens.add(Token.Atribuicao);
		tokens.add(Token.Digito);
	}
}