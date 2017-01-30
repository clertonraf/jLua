	package jlua;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Collection;
import java.util.regex.Matcher;


/**
 * Analisador Lexico
 */
public class AnalisadorLexico {

	// Todo o código fonte é atochado nessa variavel como se todo computador do mundo tivesse memória infinita
	private String code;
	// Linha atual
	private int currentLine;
	
	private Token currentToken;

	/**
	 * Cria um analisador léxico para uma InputStream, a InputStream será lida por completa antes que começe o processo de reconhecimento dos tokens.
	 * @throws IOException Caso algum erro bizarro de IO ocorra.
	 */
	public AnalisadorLexico(Reader input) throws IOException {
		BufferedReader reader = new BufferedReader(input);
		StringBuffer buffer = new StringBuffer();
		String line;
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
			buffer.append('\n');
		}
		code = buffer.toString().trim();
		currentLine = 1;
	}

	public AnalisadorLexico(InputStream input) throws IOException {
		this(new InputStreamReader(input));
	}
	
	public int getCurrentLine() {
		return currentLine;
	}

	public Token peekToken(){
		if (currentToken == null)
			currentToken = nextToken();
		return currentToken;
	}
	/**
	 * Retorna o próximo token ou null se não há tokens
	 * @throws UnknowTokenException Caso um token desconhecido seja encontrado.
	 */
	public Token nextToken() {
		Collection<Token> allTokens = Token.getTokenList();
		ErrorReporter errorReporter = ErrorReporter.getInstance();
		while(true){
			for(Token t : allTokens){
				if (code.isEmpty())
					return Token.FimDeArquivo;
				Matcher matcher = t.getRegex().matcher(code);
				if (matcher.find()) {
					if (t == Token.FimDeArquivo){
						errorReporter.error(currentLine, "haha, não compila! Achei um token Desconhecido!");
						code = eliminatesNextToken(code);
						continue;
					}
					code = code.substring(matcher.end());
					if (t == Token.String)
						t.setValue(parseString());
					else {
						currentLine += countLineFeeds(matcher.group(1));
						t.setValue(matcher.group(2));
				}
					currentToken = t;
					return t;
				}
			}
		}
		//assert false : "Token desconhecido tem que reconhecer sempre";
		//return null;
	}
	
	private String eliminatesNextToken(String string){
		StringCharacterIterator it = new StringCharacterIterator(string);
		int cont = 0;
		for(char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
			if (c == '\n' || c == '\t' || c == ' ')
				cont++;
			else
				break;
		}
		return string.substring(++cont);
	}
	
	private String parseString() {
		StringBuffer string = new StringBuffer();
		
		StringCharacterIterator it = new StringCharacterIterator(code);
		for(char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
			switch (c) {
			case '\n':
				currentLine++;
				break;
			case '\\':
				c = it.next();
				break;
			case '\"':
				code = code.substring(it.getIndex()+1);
				return string.toString();
			}
			string.append(c);
		}
		ErrorReporter.getInstance().error(currentLine, "String infinita!");
		code = code.substring(it.getIndex());
		return string.toString();
	}


	private int countLineFeeds(String code) {
		if (code == null)
			return 0;
		int total = 0;
		StringCharacterIterator it = new StringCharacterIterator(code);
		for(char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
			if (c == '\n')
				total++;
		}
		return total;
	}
}




