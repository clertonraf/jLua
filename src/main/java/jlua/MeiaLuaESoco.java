package jlua;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;

import jlua.codeGenerator.Action;
import jlua.codeGenerator.CodeGenerator;

/**
 * Compilador
 */
public class MeiaLuaESoco {

	private AnalisadorLexico lexico;
	private boolean codeGeneratorEnabled = true;
	private static Tabela tabela = createTable();
	private OutputStream output;
	private Config cfg;
	
	/**
	 * Lista das possíveis regras gramaticais.
	 */
	public static enum Regra implements Simbolo {
		Trecho,
		Comando,
		Pelse,
		UltimoComando,
		ListaDeIdentificador,
		TermoListaIdentificador,
		ListaExp,
		TermoListaExp,
		Exp,
		Exp1,
		ExpPrefixo,
		ChamadaDeFuncao,
		ChamadaDeFuncao1,
		Args,
		Funcao,
		CorpoDaFuncao,
		Opbin,
		ListaDeComandos,
		TermoListaDeComandos,
		Opunaria;

		public boolean isToken() {
			return false;
		}

		public boolean isAction() {
			return false;
		}
	}

	public MeiaLuaESoco(Reader input, Config cfg) throws IOException {
		lexico = new AnalisadorLexico(input);
		output = System.out;
		this.cfg = cfg;			
	}
	
	public MeiaLuaESoco(InputStream input, Config cfg) throws IOException {
		this(new InputStreamReader(input), cfg);
	}
	
	public MeiaLuaESoco(Reader input) throws IOException {
		this(input, new Config());
	}
	
	public MeiaLuaESoco(InputStream input) throws IOException {
		this(new InputStreamReader(input));
	}

	public void setOutput(OutputStream output) {
		this.output = output;
	}

	/**
	 * Preenche a tabela do analisador sintatico.
	 */
	private static Tabela createTable() {
		Tabela tabela = new Tabela();

		//trecho
		tabela.usingList(Regra.ListaDeComandos, Regra.UltimoComando);
		tabela.set(Regra.Trecho, Token.Atribuicao);
		tabela.set(Regra.Trecho, Token.Identificador);
		tabela.set(Regra.Trecho, Token.PontoVirgula);
		tabela.set(Regra.Trecho, Token.Do);
		tabela.set(Regra.Trecho, Token.While);
		tabela.set(Regra.Trecho, Token.If);
		tabela.set(Regra.Trecho, Token.Return);
		tabela.set(Regra.Trecho, Token.Break);
		tabela.usingEmptyList();
		tabela.set(Regra.Trecho, Token.End);
		tabela.set(Regra.Trecho, Token.FimDeArquivo);
		tabela.set(Regra.Trecho, Token.Until);
		
		
		//comando
		tabela.set(Regra.Comando, Token.Identificador, Regra.ChamadaDeFuncao);
		tabela.set(Regra.Comando, Token.PontoVirgula, Token.PontoVirgula, Regra.Comando);
		tabela.set(Regra.Comando, Token.Do, Token.Do, Regra.Trecho, Token.End);
		tabela.set(Regra.Comando, Token.While, Token.While, Action.While,Regra.Exp, Token.Do,Action.If, Regra.Trecho,Action.EndWhile,Token.End);
		

		tabela.set(Regra.Comando, Token.If, Token.If, Regra.Exp, Action.If, Token.Then, Regra.Trecho, Regra.Pelse, Action.EndIf, Token.End);
		tabela.usingEmptyList();
		tabela.set(Regra.Comando, Token.Return);
		tabela.set(Regra.Comando, Token.Break);
		tabela.set(Regra.Comando, Token.Else);
		tabela.set(Regra.Comando, Token.End);
		tabela.set(Regra.Comando, Token.FimDeArquivo);
		
		//pelse
		tabela.set(Regra.Pelse, Token.Else, Action.Else, Token.Else, Regra.Trecho);
		tabela.set(Regra.Pelse, Token.End);
		
		//ultimocomando
		tabela.set(Regra.UltimoComando, Token.Return, Token.Return, Regra.Exp, Token.PontoVirgula);
		tabela.set(Regra.UltimoComando, Token.Break, Token.Break, Token.PontoVirgula);
		tabela.set(Regra.UltimoComando, Token.PontoVirgula);
		tabela.set(Regra.UltimoComando, Token.FimDeArquivo);
		tabela.set(Regra.UltimoComando, Token.Else);
		tabela.set(Regra.UltimoComando, Token.End);
		tabela.set(Regra.UltimoComando, Token.Until);
				
		//listadeidentificador
		tabela.set(Regra.ListaDeIdentificador, Token.Identificador, Token.Identificador, Regra.TermoListaIdentificador);
		tabela.set(Regra.ListaDeIdentificador, Token.ParentesesFechado);
				
		//termolistaidentificador
		tabela.set(Regra.TermoListaIdentificador, Token.Virgula, Token.Virgula, Token.Identificador, Regra.TermoListaIdentificador);
		tabela.set(Regra.TermoListaIdentificador, Token.ParentesesFechado);
				
		//listaexp
		tabela.usingList(Regra.Exp, Action.IncrementArgCounter, Regra.TermoListaExp);
		tabela.set(Regra.ListaExp, Token.Identificador);
		tabela.set(Regra.ListaExp, Token.Nil);
		tabela.set(Regra.ListaExp, Token.False);
		tabela.set(Regra.ListaExp, Token.True);
		tabela.set(Regra.ListaExp, Token.Digito);
		tabela.set(Regra.ListaExp, Token.String);
		tabela.set(Regra.ListaExp, Token.ParentesesAberto);
		tabela.set(Regra.ListaExp, Token.Function);
		tabela.set(Regra.ListaExp, Token.OperadorSubtracao);
		tabela.usingEmptyList();
		tabela.set(Regra.ListaExp, Token.ParentesesFechado);
				
		//termolistaexp
		tabela.set(Regra.TermoListaExp, Token.Virgula, Token.Virgula, Regra.Exp, Action.IncrementArgCounter, Regra.TermoListaExp);
		tabela.usingEmptyList();
		tabela.set(Regra.TermoListaExp, Token.ParentesesFechado);
		
		//exp
		tabela.set(Regra.Exp, Token.Identificador, Regra.ChamadaDeFuncao, Regra.Exp1);
		tabela.set(Regra.Exp, Token.Nil, Action.Operand, Token.Nil, Regra.Exp1);
		tabela.set(Regra.Exp, Token.False, Action.Operand, Token.False, Regra.Exp1);
		tabela.set(Regra.Exp, Token.True, Action.Operand, Token.True, Regra.Exp1);
		tabela.set(Regra.Exp, Token.Digito, Action.Operand, Token.Digito, Regra.Exp1);
		tabela.set(Regra.Exp, Token.String, Action.Operand, Token.String, Regra.Exp1);
		tabela.set(Regra.Exp, Token.ParentesesAberto, Regra.ChamadaDeFuncao, Regra.Exp1);
		tabela.set(Regra.Exp, Token.Function, Regra.Funcao);
		tabela.set(Regra.Exp, Token.OperadorSubtracao, Regra.Opunaria, Regra.Exp, Action.Unaria);
		tabela.set(Regra.Exp, Token.Not, Regra.Opunaria, Regra.Exp, Action.Not);
		
		
		
		//exp1
		tabela.set(Regra.Exp1, Token.Do);
		tabela.set(Regra.Exp1, Token.Then);
		tabela.set(Regra.Exp1, Token.Return);
		tabela.set(Regra.Exp1, Token.Break);
		tabela.set(Regra.Exp1, Token.Virgula);
//		tabela.usingList(Regra.Opbin, Regra.Exp);
		tabela.set(Regra.Exp1, Token.OperadorSoma, Regra.Opbin, Regra.Exp, Action.Add);
		tabela.set(Regra.Exp1, Token.OperadorSubtracao, Regra.Opbin, Regra.Exp, Action.Sub);
		tabela.set(Regra.Exp1, Token.OperadorMultiplicacao, Regra.Opbin, Regra.Exp, Action.Mul);
		tabela.set(Regra.Exp1, Token.OperadorDivisao, Regra.Opbin, Regra.Exp, Action.Div);
		tabela.set(Regra.Exp1, Token.OperadorExponenciacao, Regra.Opbin, Regra.Exp, Action.Pow);
		tabela.set(Regra.Exp1, Token.OperadorResto, Regra.Opbin, Regra.Exp, Action.Mod);
		tabela.set(Regra.Exp1, Token.PontoPonto, Regra.Opbin,Regra.Exp, Action.Concat);
		tabela.set(Regra.Exp1, Token.OperadorMenorQue, Regra.Opbin, Regra.Exp, Action.LessThen);
		tabela.set(Regra.Exp1, Token.OperadorMenorIgual, Regra.Opbin, Regra.Exp, Action.LessEqual);
		tabela.set(Regra.Exp1, Token.OperadorMaiorQue, Regra.Opbin, Regra.Exp, Action.GreaterThen);
		tabela.set(Regra.Exp1, Token.OperadorMaiorIgual, Regra.Opbin, Regra.Exp, Action.GreaterEqual);
		tabela.set(Regra.Exp1, Token.OperadorIgual, Regra.Opbin, Regra.Exp, Action.Equal);
		tabela.set(Regra.Exp1, Token.OperadorDiferenca, Regra.Opbin, Regra.Exp, Action.NotEqual);
		tabela.set(Regra.Exp1, Token.And, Regra.Opbin, Regra.Exp, Action.And);
		tabela.set(Regra.Exp1, Token.Or, Regra.Opbin, Regra.Exp, Action.Or);
		tabela.usingEmptyList();
		tabela.set(Regra.Exp1, Token.PontoVirgula);
		tabela.set(Regra.Exp1, Token.End);
		tabela.set(Regra.Exp1, Token.ParentesesFechado);
		tabela.set(Regra.Exp1, Token.FimDeArquivo);
		tabela.set(Regra.Exp1, Token.Until);

		//chamadadefuncao
		tabela.set(Regra.ChamadaDeFuncao, Token.Identificador, Action.Operand, Token.Identificador, Regra.ChamadaDeFuncao1);
				
		//chamadadefuncao1
		tabela.set(Regra.ChamadaDeFuncao1, Token.Atribuicao, Token.Atribuicao, Regra.Exp, Action.Attribution);
		tabela.set(Regra.ChamadaDeFuncao1, Token.Do);
		tabela.set(Regra.ChamadaDeFuncao1, Token.Then);
		tabela.set(Regra.ChamadaDeFuncao1, Token.Return);
		tabela.set(Regra.ChamadaDeFuncao1, Token.Break);
		tabela.set(Regra.ChamadaDeFuncao1, Token.Virgula);
		tabela.set(Regra.ChamadaDeFuncao1, Token.ParentesesAberto, Regra.Args, Regra.ChamadaDeFuncao1);
		tabela.set(Regra.ChamadaDeFuncao1, Token.OperadorSoma, Regra.Opbin, Regra.Exp, Action.Add);
		tabela.set(Regra.ChamadaDeFuncao1, Token.OperadorSubtracao, Regra.Opbin, Regra.Exp, Action.Sub);
		tabela.set(Regra.ChamadaDeFuncao1, Token.OperadorMultiplicacao, Regra.Opbin, Regra.Exp, Action.Mul);
		tabela.set(Regra.ChamadaDeFuncao1, Token.OperadorDivisao, Regra.Opbin, Regra.Exp, Action.Div);
		tabela.set(Regra.ChamadaDeFuncao1, Token.OperadorExponenciacao, Regra.Opbin, Regra.Exp, Action.Pow);
		tabela.set(Regra.ChamadaDeFuncao1, Token.OperadorResto, Regra.Opbin, Regra.Exp, Action.Mod);
		tabela.set(Regra.ChamadaDeFuncao1, Token.PontoPonto, Regra.Opbin, Regra.Exp, Action.Concat);
		tabela.set(Regra.ChamadaDeFuncao1, Token.OperadorMenorQue, Regra.Opbin, Regra.Exp, Action.LessThen);
		tabela.set(Regra.ChamadaDeFuncao1, Token.OperadorMenorIgual, Regra.Opbin, Regra.Exp, Action.LessEqual);
		tabela.set(Regra.ChamadaDeFuncao1, Token.OperadorMaiorQue, Regra.Opbin, Regra.Exp, Action.GreaterThen);
		tabela.set(Regra.ChamadaDeFuncao1, Token.OperadorMaiorIgual, Regra.Opbin, Regra.Exp, Action.GreaterEqual);
		tabela.set(Regra.ChamadaDeFuncao1, Token.OperadorIgual, Regra.Opbin, Regra.Exp, Action.Equal);
		tabela.set(Regra.ChamadaDeFuncao1, Token.OperadorDiferenca, Regra.Opbin, Regra.Exp);
		tabela.set(Regra.ChamadaDeFuncao1, Token.And, Regra.Opbin, Regra.Exp, Action.And);
		tabela.set(Regra.ChamadaDeFuncao1, Token.Or, Regra.Opbin, Regra.Exp, Action.Or);
		tabela.usingEmptyList();
		tabela.set(Regra.ChamadaDeFuncao1, Token.PontoVirgula);
		tabela.set(Regra.ChamadaDeFuncao1, Token.FimDeArquivo);
		tabela.set(Regra.ChamadaDeFuncao1, Token.ParentesesFechado);

		
		//args
		tabela.set(Regra.Args, Token.Atribuicao);
		tabela.set(Regra.Args, Token.ParentesesAberto, Token.ParentesesAberto, Action.StartArgCounter, Regra.ListaExp, Token.ParentesesFechado, Action.Call);
		//tabela.set(Regra.Args, Token.ParentesesAberto, Token.ParentesesAberto);
		
		//funcao
		tabela.set(Regra.Funcao, Token.Function, Token.Function, Regra.CorpoDaFuncao);
		
		//corpodafuncao
		tabela.set(Regra.CorpoDaFuncao, Token.ParentesesAberto, Token.ParentesesAberto, Regra.ListaDeIdentificador,Token.ParentesesFechado, Regra.Trecho, Token.End);
		
		//opbin
		tabela.set(Regra.Opbin, Token.OperadorSoma, Token.OperadorSoma);
		tabela.set(Regra.Opbin, Token.OperadorSubtracao, Token.OperadorSubtracao);
		tabela.set(Regra.Opbin, Token.OperadorMultiplicacao, Token.OperadorMultiplicacao);
		tabela.set(Regra.Opbin, Token.OperadorDivisao, Token.OperadorDivisao);
		tabela.set(Regra.Opbin, Token.OperadorExponenciacao, Token.OperadorExponenciacao);
		tabela.set(Regra.Opbin, Token.OperadorResto, Token.OperadorResto);
		tabela.set(Regra.Opbin, Token.PontoPonto, Token.PontoPonto);
		tabela.set(Regra.Opbin, Token.OperadorMenorQue, Token.OperadorMenorQue);
		tabela.set(Regra.Opbin, Token.OperadorMenorIgual, Token.OperadorMenorIgual);
		tabela.set(Regra.Opbin, Token.OperadorMaiorQue, Token.OperadorMaiorQue);
		tabela.set(Regra.Opbin, Token.OperadorMaiorIgual, Token.OperadorMaiorIgual);
		tabela.set(Regra.Opbin, Token.OperadorIgual, Token.OperadorIgual);
		tabela.set(Regra.Opbin, Token.OperadorDiferenca, Token.OperadorDiferenca);
		tabela.set(Regra.Opbin, Token.And, Token.And);
		tabela.set(Regra.Opbin, Token.Or, Token.Or);
		
		//opunaria
		tabela.set(Regra.Opunaria, Token.OperadorSubtracao, Token.OperadorSubtracao);
		tabela.set(Regra.Opunaria, Token.Not, Token.Not);
		
		//listadecomandos
		tabela.usingList(Regra.Comando, Regra.TermoListaDeComandos);
		tabela.set(Regra.ListaDeComandos, Token.Identificador);
		tabela.set(Regra.ListaDeComandos, Token.PontoVirgula);
		tabela.set(Regra.ListaDeComandos, Token.Do);
		tabela.set(Regra.ListaDeComandos, Token.While);
		tabela.set(Regra.ListaDeComandos, Token.If);
		tabela.set(Regra.ListaDeComandos, Token.Return);
		tabela.set(Regra.ListaDeComandos, Token.Break);
		
		//termolistadecomandos
		tabela.usingEmptyList();
		tabela.set(Regra.TermoListaDeComandos, Token.PontoVirgula, Token.PontoVirgula, Regra.Comando, Regra.TermoListaDeComandos);
		tabela.set(Regra.TermoListaDeComandos, Token.Return);
		tabela.set(Regra.TermoListaDeComandos, Token.Break);
		tabela.set(Regra.TermoListaDeComandos, Token.Else);
		tabela.set(Regra.TermoListaDeComandos, Token.End);
		tabela.set(Regra.TermoListaDeComandos, Token.FimDeArquivo);
		tabela.set(Regra.TermoListaDeComandos, Token.Until);
		return tabela;
	}
	

	public boolean compile() throws Exception {
		CodeGenerator codeGenerator = new CodeGenerator(output);
		codeGenerator.setArch(cfg.getArch());
		codeGenerator.setMode(cfg.getMode());
		
		ErrorReporter er = ErrorReporter.getInstance();
		LinkedList<Simbolo> pilha = new LinkedList<Simbolo>();
		pilha.push(Token.FimDeArquivo);
		pilha.push(Regra.Trecho);
		
		Token nextToken = lexico.nextToken();
		while(pilha.peek() != Token.FimDeArquivo) {
			Simbolo simbolo = pilha.peek();
			if (simbolo.isAction()) {
				if (codeGeneratorEnabled && er.getNumErrors() == 0)
					codeGenerator.doAction((Action)simbolo, nextToken);
				pilha.pop();
			} else if (simbolo.isToken()) {
				if (simbolo.equals(nextToken)) {
					pilha.pop();
					nextToken = lexico.nextToken();
				} else  {
					er.error(lexico.getCurrentLine(), "Esperava token "+simbolo.toString()+", veio "+simbolo.toString());
					return false;
				}
			} else if (tabela.get((Regra) simbolo, nextToken) != null) {
				LinkedList<Simbolo> simbolos = (LinkedList<Simbolo>) tabela.get((Regra) simbolo, nextToken);
				pilha.pop();
				Iterator<Simbolo> it = simbolos.descendingIterator();
				while(it.hasNext()) {
					pilha.addFirst(it.next());
				}
			} else {
				er.error(lexico.getCurrentLine(), "Esperava alguns dos tokens "+tabela.getTokensFor((Regra)simbolo).toString()+", veio "+nextToken.toString());
				return false;
			}
		}
		codeGenerator.finish();
		return er.getNumErrors() == 0;
	}

	public void disableCodeGeneration() {
		codeGeneratorEnabled  = false;
		
	}
}
