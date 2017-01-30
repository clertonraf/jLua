package jlua;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import meialuaesoco.MeiaLuaESoco.Regra;

/**
 * Tabela de Regras e tokens usada pelo Análisador Sintático.
 * O valor de cada célula é uma lista de Simbolos.
 */
public class Tabela {
	private HashMap<Regra, HashMap<Token, LinkedList<Simbolo>>> tabela;
	private static LinkedList<Simbolo> emptyList = new LinkedList<Simbolo>();
	private LinkedList<Simbolo> currentList;
	
	public Tabela(){
		tabela = new HashMap<Regra, HashMap<Token, LinkedList<Simbolo>>>();
		usingEmptyList();
	}
	
	/**
	 * Usa uma lista vazia como valor default para a célula da tabela.
	 */
	public void usingEmptyList() {
		currentList = emptyList;
	}
	
	/**
	 * Usa a lista <code>list</code> como valor default da célula da tabela.
	 */
	public void usingList(Simbolo ... list) {
		currentList = new LinkedList<Simbolo>();
		for (Simbolo simbolo : list)
			currentList.add(simbolo);
	}
	
	/**
	 * Retorna o valor da célula para a Regra regra e o Token token.
	 */
	public LinkedList<Simbolo> get(Regra regra, Token token) {
		HashMap<Token, LinkedList<Simbolo>> subtabela = tabela.get(regra);
		if (subtabela != null)
			return subtabela.get(token);
		return null;
	}

	/**
	 * Retorna todos os tokens esperados para uma determinada regra.
	 */
	public Set<Token> getTokensFor(Regra regra) {
		return tabela.get(regra).keySet();
	}
	
	/**
	 * Define o valor de uma célula na tabela.
	 */
	public void set(MeiaLuaESoco.Regra regra, Token token, Simbolo ... simbolos){
		LinkedList<Simbolo> list = new LinkedList<Simbolo>();
		for (Simbolo simbolo : simbolos)
			list.add(simbolo);
		set(regra, token, list);
	}
	
	/**
	 * Define o valor de uma célula na tabela para o valor default.
	 */
	public void set(MeiaLuaESoco.Regra regra, Token token){
		set(regra, token, currentList);
	}

	public void set(MeiaLuaESoco.Regra regra, Token token, LinkedList<Simbolo> simbolos) {
		HashMap<Token, LinkedList<Simbolo>> subtabela = tabela.get(regra);
		if (subtabela == null) {
			subtabela = new HashMap<Token, LinkedList<Simbolo>>();
			tabela.put(regra, subtabela);
		}
		subtabela.put(token, simbolos);
	}
}
