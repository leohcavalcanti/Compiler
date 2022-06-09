package compilador;

public class Compilador {

    public static void main(String[] args) {
        Lexico lexico = new Lexico("src\\compilador\\codigo.txt");
        Sintatico sintatico = new Sintatico(lexico);
        Token t = null;
        sintatico.S();
        while ((t = lexico.nextToken()) != null) {
            System.out.println(t.toString());
        }

    }

}
