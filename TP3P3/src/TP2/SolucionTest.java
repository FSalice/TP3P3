package TP2;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class SolucionTest {
	
	@Test
	public void test() 
	{
		fail("Not yet implemented");
	}
	
	//las aristas se definen como "0,5,45;0,2,23;"(inicio, destino, peso)
	private Grafo generarInstancia(int vertices, String aristas)
	{
		//grafo vacio
		Grafo ret = new Grafo();
		//relleno con vertices truchos
		for(int i = 0; i < vertices; i++)
		{
			ret.agregarEstacion(new Ciudad("", 0, 0));
		}
		
		//creo aristas con los pesos indicados
		Pattern pat = Pattern.compile("\\d+");
		Matcher m = pat.matcher(aristas);
		int inicio, destino, peso;
		while(m.find())
		{
			inicio = Integer.parseInt(m.group());
			if(m.find())
				destino = Integer.parseInt(m.group());
			else
				throw new IllegalArgumentException("Ultima arista incompleta");
			if(m.find())
				peso = Integer.parseInt(m.group());
			else
				throw new IllegalArgumentException("Ultima arista incompleta");
			
			ret.agregarArista(inicio, destino, peso);
		}
		return ret;
	}
}
