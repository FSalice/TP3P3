package TP2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class FileManager {

	static Grafo cargarGrafo() {
		File archivo = new File("src/guardado");
		Grafo ret = null;
		if (archivo.exists()) {
			FileInputStream fis;
			ObjectInputStream in;
			try {
				fis = new FileInputStream(archivo);
				in = new ObjectInputStream(fis);
				ret = (Grafo) in.readObject();
				in.close();
				fis.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		

		if(ret == null) ret = new Grafo();
		
		return ret;
	}
	
	static void guardarGrafo(Grafo grafo) {
		File archivo = new File("src/guardado");
		if(!archivo.exists()){
			new File("src").mkdirs();
		}
		try {
			FileOutputStream fos = new FileOutputStream(archivo);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(grafo);
			fos.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
