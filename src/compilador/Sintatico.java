package compilador;

public class Sintatico {
    private Lexico lexico;
    private Token token;

    public Sintatico(Lexico lexico) {
        this.lexico = lexico;
    }

    public void S() { //S determina estado inicial
        this.token = this.lexico.nextToken();
        if (!token.getLexema().equals("main")) {
            throw new RuntimeException("Oxe, cadê main?");
        }

        this.token = this.lexico.nextToken();
        if (!token.getLexema().equals("(")) {
            throw new RuntimeException("Abre o parêntese do main cabra!");
        }

        this.token = this.lexico.nextToken();
        if (!token.getLexema().equals(")")) {
            throw new RuntimeException("Fechar o parêntese do main cabra!");
        }
        this.token = this.lexico.nextToken();

        this.B();
        if (this.token.getTipo() == Token.TIPO_FIM_CODIGO) {
            System.out.println("O Código tá massa! Arretado! Tu botou pra torar!");
        } else {
            throw new RuntimeException("Oxe, eu deu bronca preto do fim do programa.");
        }
    }

    private void B() {
        if (!this.token.getLexema().equals("{")) {
            throw new RuntimeException("Oxe, tave esperando um \"{\" pertinho de " + this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
        this.CS();
        if (!this.token.getLexema().equals("}")) {
            throw new RuntimeException("Oxe, tava esperando um \"}\" pertinho de " + this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
    }

    private void CS() {
        if ((this.token.getTipo() == Token.TIPO_IDENTIFICADOR) ||
                this.token.getLexema().equals("int") ||
                this.token.getLexema().equals("float")) {
            this.C();
            this.CS();
        } else if (this.token.getTipo() == Token.TIPO_OPERADOR_ARITMETICO) {
            this.E();
            this.CS();
        } else if (this.token.getTipo() == Token.TIPO_PALAVRA_RESERVADA) {
            this.PL();
            this.CS();
        } else if (this.token.getTipo() == Token.TIPO_INTEIRO) {
            this.T();
            this.CS();
        } else if (this.token.getLexema().equals("{")
                || this.token.getLexema().equals("}")
                || this.token.getLexema().equals(";")) {
            this.SC();
            this.CS();
        } else if (this.token.getLexema().equals("(")
                || this.token.getLexema().equals(")")) {
            this.CE();
            this.CS();
        }
    }

    private void C() {
        if (this.token.getTipo() == Token.TIPO_IDENTIFICADOR) {
            this.ATRIBUICAO();
        } else if (this.token.getLexema().equals("int") ||
                this.token.getLexema().equals("float")) {
            this.DECLARACAO();
        } else {
            throw new RuntimeException("Oxe, eu tava esperando tu "
                    + "declarar um comando pertinho de :" + this.token.getLexema());
        }
    }

    private void DECLARACAO() {
        if (this.token.getLexema().equals("{")
                || this.token.getLexema().equals("}")
                || this.token.getLexema().equals("(")
                || this.token.getLexema().equals(")")) {
            this.token = this.lexico.nextToken();
            return;
        }
        if (!(this.token.getLexema().equals("int") ||
                this.token.getLexema().equals("float"))) {
            throw new RuntimeException("Tu vacilou na delcaração de variável. "
                    + "Pertinho de: " + this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
        if (this.token.getTipo() != Token.TIPO_IDENTIFICADOR) {
            throw new RuntimeException("Tu vacilou na delcaração de variável. "
                    + "Pertinho de: " + this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
        if (this.token.getTipo() != Token.TIPO_OPERADOR_ATRIBUICAO
                && !this.token.getLexema().equals(";")) {
            throw new RuntimeException("Tu vacilou  na delcaração de variável. "
                    + "Pertinho de: " + this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
    }

    private void ATRIBUICAO() {
        if (this.token.getTipo() != Token.TIPO_IDENTIFICADOR) {
            throw new RuntimeException("Erro na atribuição. Pertinho de: " + this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
        if (this.token.getTipo() == Token.TIPO_OPERADOR_RELACIONAL) {
            this.token = this.lexico.nextToken();
            return;
        }
        if (this.token.getTipo() == Token.TIPO_CARACTER_ESPECIAL) {
            this.token = this.lexico.nextToken();
            return;
        }
        if (this.token.getTipo() == Token.TIPO_OPERADOR_ARITMETICO) {
            this.token = this.lexico.nextToken();
            return;
        }
        if (this.token.getTipo() == Token.TIPO_PALAVRA_RESERVADA) {
            this.token = this.lexico.nextToken();
            return;
        }
        if (this.token.getTipo() != Token.TIPO_OPERADOR_ATRIBUICAO) {
            throw new RuntimeException("Erro na atribuição. Pertinho de: " + this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
        this.E();
        this.token = this.lexico.nextToken();
    }

    private void E() {
        if (this.token.getTipo() == Token.TIPO_IDENTIFICADOR
                || this.token.getLexema().equals("int")
                || this.token.getLexema().equals("float")) {
            this.T();
        }
        if (this.token.getTipo() == Token.TIPO_OPERADOR_ARITMETICO) {
            this.El();
        }
    }

    private void CE() {
        this.token = this.lexico.nextToken();
        if (this.token.getLexema().equals("(")) {
            this.token = this.lexico.nextToken();
            this.CS();
            this.token = this.lexico.nextToken();
            if (!this.token.getLexema().equals(")")) {
                throw new RuntimeException("AA");
            } else {
                this.token = this.lexico.nextToken();
            }
        }
    }

    private void El() {
        if (this.token.getTipo() == Token.TIPO_OPERADOR_ARITMETICO) {
            this.OP();
            this.T();
            this.El();
        } else {
        }
    }

    private void T() {
        if (this.token.getTipo() == Token.TIPO_IDENTIFICADOR ||
                this.token.getTipo() == Token.TIPO_INTEIRO ||
                this.token.getTipo() == Token.TIPO_REAL) {
            this.token = this.lexico.nextToken();
        } else {
            throw new RuntimeException("Oxe, era para ser um identificador "
                    + "ou número pertinho de " + this.token.getLexema());
        }
    }

    private void OP() {
        if (this.token.getTipo() == Token.TIPO_OPERADOR_ARITMETICO) {
            this.token = this.lexico.nextToken();
        } else {
            throw new RuntimeException("Oxe, era para ser um operador "
                    + "aritmético (+,-,/,*) pertinho de " +
                    this.token.getLexema());
        }
    }

    private void SC() {
        if (this.token.getTipo() == Token.TIPO_CARACTER_ESPECIAL) {
            this.token = this.lexico.nextToken();
        } else {
            throw new RuntimeException("AAAAA");
        }
    }

    private void PL() {
        if (this.token.getLexema().equals("if")
                || this.token.getLexema().equals("while")) {
            this.CE();
        } else if (this.token.getTipo() == Token.TIPO_PALAVRA_RESERVADA) {
            this.token = this.lexico.nextToken();
        } else {
            throw new RuntimeException("Oxe, era para ser uma palavra reservada "
                    + "(main | if | else | int | float | char | do | for | while) pertinho de " +
                    this.token.getLexema());
        }
    }

}