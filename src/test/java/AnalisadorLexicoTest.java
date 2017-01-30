import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import meialuaesoco.AnalisadorLexico;
import meialuaesoco.ErrorReporter;
import meialuaesoco.Token;

public class AnalisadorLexicoTest extends TestCase {

	public void setUp() {
		ErrorReporter.getInstance().reset();
	}
	
	public void testNormalInput1() throws Exception {
		InputStream input = new FileInputStream(new File("src/tests/normal1.lua"));
		AnalisadorLexico lexico = new AnalisadorLexico(input);
		
		Token[] list = {Token.Identificador, Token.Atribuicao, Token.Function, Token.ParentesesAberto, Token.Identificador,
						Token.ParentesesFechado, Token.If, Token.Identificador, Token.OperadorIgual, Token.Digito,
						Token.Then, Token.Identificador, Token.Atribuicao, Token.String, Token.End, Token.End, Token.FimDeArquivo};
		for (int i = 0; i < list.length; ++i) {
			assertEquals(list[i], lexico.nextToken());
		}
	}
	
	public void testBizarreInput1() throws Exception {
		InputStream input = new FileInputStream(new File("src/tests/bizarre1.lua"));
		AnalisadorLexico lexico = new AnalisadorLexico(input);

		Token[] list = {Token.Identificador, Token.Atribuicao, Token.Function, Token.ParentesesAberto, Token.Identificador,
						Token.ParentesesFechado, Token.If, Token.Identificador, Token.OperadorIgual, Token.Digito,
						Token.Then, Token.Identificador, Token.Atribuicao, Token.String, Token.FimDeArquivo};
		for (int i = 0; i < list.length; ++i) {
			assertEquals(list[i], lexico.nextToken());
		}
		assertEquals(1, ErrorReporter.getInstance().getNumErrors());
	}

	public void testBizarreInput2() throws Exception {
		InputStream input = new FileInputStream(new File("src/tests/bizarre2.lua"));
		AnalisadorLexico lexico = new AnalisadorLexico(input);

		Token[] list = {Token.Identificador, Token.Atribuicao, Token.Function, Token.ParentesesAberto, Token.Identificador,
				Token.ParentesesFechado, Token.If, Token.Identificador, Token.OperadorIgual, Token.Digito,
				Token.Then, Token.Identificador, Token.Atribuicao, Token.String, Token.End, Token.End, Token.FimDeArquivo};
		for (int i = 0; i < list.length; ++i) {
			assertEquals(list[i], lexico.nextToken());
		}
		ErrorReporter errorReporter = ErrorReporter.getInstance();
		assertEquals(1 , errorReporter.getNumErrors());
	}	
}
