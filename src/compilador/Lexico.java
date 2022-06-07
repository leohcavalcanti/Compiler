package compilador;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Lexico {
	private char[] conteudo;
	private int indiceConteudo;
	private ArrayList<String> palavrasReservadas;

	public Lexico(String caminhoCodigoFonte) {

		try {

			String conteudoStr;
			conteudoStr = new String(Files.readAllBytes(Paths.get(caminhoCodigoFonte)));
			this.conteudo = conteudoStr.toCharArray();
			this.indiceConteudo = 0;

			criarPalavraReservadas();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	// Retorna próximo char
	private char nextChar() {
		return this.conteudo[this.indiceConteudo++];
	}

	// Verifica existe próximo char ou chegou ao final do código fonte
	private boolean hasNextChar() {
		return indiceConteudo < this.conteudo.length;
	}

	// Retrocede o índice que aponta para o "char da vez" em uma unidade
	private void back() {
		this.indiceConteudo--;
	}

	// Identificar se char é letra minúscula
	private boolean isLetra(char c) {
		return (c >= 'a') && (c <= 'z');
	}

	// Identificar se char é dígito
	private boolean isDigito(char c) {
		return (c >= '0') && (c <= '9');
	}

	// Caracteres de espaço em branco ASCII tradicionais
	private boolean isEspacoBranco(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}

	// Caracteres especiais
	private boolean isCharEspecial(char c) {
		return (c == ')' || c == '(' || c == '{' || c == '}' || c == ';' || c == ',');
	}

	// Operadores aritmeticos
	private boolean isOperadorAritmetico(char c) {
		return (c == '+' || c == '-' || c == '*' || c == '/');
	}

	// Lista de palavras reservadas:
	// main | if | else | while | do | for | int | float | char
	private void criarPalavraReservadas() {
		palavrasReservadas = new ArrayList<String>();
		palavrasReservadas.add("main");
		palavrasReservadas.add("if");
		palavrasReservadas.add("else");
		palavrasReservadas.add("while");
		palavrasReservadas.add("do");
		palavrasReservadas.add("for");
		palavrasReservadas.add("int");
		palavrasReservadas.add("float");
		palavrasReservadas.add("char");
	}

	// Método retorna próximo token válido ou retorna mensagem de erro.
	public Token nextToken() {

		Token token = null;
		char c;
		int estado = 0;

		StringBuffer lexema = new StringBuffer();

		while (this.hasNextChar()) {

			c = this.nextChar();
			switch (estado) {

			case 0:

				if (isEspacoBranco(c)) {
					estado = 0;

				} else if (this.isLetra(c) || c == '_') {
					lexema.append(c);
					estado = 1;

				} else if (this.isDigito(c)) {
					lexema.append(c);
					estado = 2;

				} else if (isCharEspecial(c)) {
					lexema.append(c);
					estado = 5;

				} else if (isOperadorAritmetico(c)) {
					lexema.append(c);
					estado = 6;

				} else if (c == 39) { // char 39 -> '
					lexema.append(c);
					estado = 7;

				} else if (c == '=') {
					lexema.append(c);
					estado = 10;

				} else if (c == '>' || c == '<') {
					lexema.append(c);
					estado = 11;

				} else if (c == '!') {
					lexema.append(c);
					estado = 12;

				} else if (c == '$') {
					lexema.append(c);
					estado = 99;
					this.back();
				
				} else {
					lexema.append(c);
					throw new RuntimeException("Erro: token inválido \"" + lexema.toString() + "\"");
				}
				
				break;

			case 1:
				if (this.isLetra(c) || this.isDigito(c) || c == '_') {
					lexema.append(c);
					estado = 1;
				} else {
					this.back();

					if (palavrasReservadas.contains(lexema.toString())) {
						return new Token(lexema.toString(), Token.TIPO_PALAVRA_RESERVADA);
					} else {
						return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
					}

				}
				break;

			case 2:
				if (this.isDigito(c)) {
					lexema.append(c);
					estado = 2;
				} else if (c == '.') {
					lexema.append(c);
					estado = 3;
				} else {
					this.back();
					return new Token(lexema.toString(), Token.TIPO_INTEIRO);
				}
				break;

			case 3:
				if (this.isDigito(c)) {
					lexema.append(c);
					estado = 4;
				} else {
					throw new RuntimeException("Erro: TIPO_REAL inválido \"" + lexema.toString() + "\"");
				}
				break;

			case 4:
				if (this.isDigito(c)) {
					lexema.append(c);
					estado = 4;
				} else {
					this.back();
					return new Token(lexema.toString(), Token.TIPO_REAL);
				}
				break;

			case 5:
				this.back();
				return new Token(lexema.toString(), Token.TIPO_CARACTER_ESPECIAL);

			case 6:
				if(c == '+'){
					lexema.append(c);
					return new Token(lexema.toString(), Token.TIPO_OPERADOR_INCREMENTA);
				}else if(c == '-'){
					lexema.append(c);
					return new Token(lexema.toString(), Token.TIPO_OPERADOR_DECREMENTA);
				}else{
					this.back();
					return new Token(lexema.toString(), Token.TIPO_OPERADOR_ARITMETICO);
				}
				

			case 7:
				if (this.isDigito(c) || this.isLetra(c)) {
					lexema.append(c);
					estado = 8;
				} else {
					throw new RuntimeException("Erro: TIPO_CHAR inválido \"" + lexema.toString() + "\"");
				}
				break;

			case 8:
				if (c == 39) { // char 39 -> '
					lexema.append(c);
					estado = 9;
				} else {
					throw new RuntimeException("Erro: TIPO_CHAR inválido \"" + lexema.toString() + "\"");
				}
				break;

			case 9:
				this.back();
				return new Token(lexema.toString(), Token.TIPO_CHAR);

			case 10:
				if (c == '=') {
					lexema.append(c);
					estado = 13;
				} else {
					return new Token(lexema.toString(), Token.TIPO_OPERADOR_ATRIBUICAO);
				}

			case 11:
				if (c == '=') {
					lexema.append(c);
					estado = 13;
				} else {
					this.back();
					return new Token(lexema.toString(), Token.TIPO_OPERADOR_RELACIONAL);
				}
				
			case 12:
				if (c == '=') {
					lexema.append(c);
					estado = 13;
				} else {
					throw new RuntimeException("Erro: OPERADOR_RELACIONAL inválido \"" + lexema.toString() + "\"");
				}	
				
			case 13:
				return new Token(lexema.toString(), Token.TIPO_OPERADOR_RELACIONAL);	

			case 14:
				
				//tocar?
			case 99:
				return new Token(lexema.toString(), Token.TIPO_FIM_CODIGO);
			}

		}

		return token;

	}

}