package compilador;

public class Compilador {

	public static void main(String[] args) {
		Lexico lexico = new Lexico("src\\compilador\\codigo.txt");
        Token t = null;
        while((t = lexico.nextToken()) != null){
            System.out.println(t.toString());
        }


	}

}
