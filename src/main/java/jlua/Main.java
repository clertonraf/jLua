
package jlua;

import java.io.File;
import java.io.FileOutputStream;

public class Main {
	
	public static final int SUCCESS = 0; 
	public static final int ERROR = 1; 
	
	/**
	 * Ponto de entrada do programa.
	 * <p>O código deve ser provido pela entrada padrão, o código compilado é enviado
	 * para a saída padrão e todas as mensagens de erro são enviadas para a saída
	 * padrão de erro.</p>
	 * 
	 * <p>O programa retorna 0 em caso de sucesso e 1 em caso de falha.</p> 
	 */
	public static void main(String[] args) {
		if (args.length < 1)
			System.exit(1);
		
		int returnCode = ERROR;
		try {
			Config cfg = new Config(args);
			MeiaLuaESoco compilador = new MeiaLuaESoco(System.in, cfg);
			FileOutputStream output = new FileOutputStream(new File(cfg.getOutput()));
			compilador.setOutput(output);
			returnCode = compilador.compile() ? SUCCESS : ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			returnCode = ERROR;
		}	
		System.exit(returnCode);
	}
}
