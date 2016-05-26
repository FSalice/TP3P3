package TP2;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class SolucionTest {
	
	@Test
	public void instanciaTest() 
	{
		Grafo g = generarInstancia("3 0,1,10 ; 1,2,10 ; 2,0,10");
		Solucion s = Solucion.recorridoGoloso(g);
		assertEquals(30,s.getLongitud());
	}
	
	//las aristas se definen como "6 0,5,45;0,2,23;"(tamañoDeGrafo inicio, destino, peso;)
	private Grafo generarInstancia(String aristas)
	{
		Pattern pat = Pattern.compile("\\d+");
		Matcher m = pat.matcher(aristas);
		//grafo vacio
		Grafo ret = new Grafo();
		//relleno con vertices truchos
		if(!m.find())
			throw new IllegalArgumentException("Grafo invalido!");
		int tamaño = Integer.parseInt(m.group());
		for(int i = 0; i < tamaño; i++)
		{
			ret.agregarEstacion(new Ciudad(""+i, 0, 0));
		}
		
		//creo aristas con los pesos indicados
		int inicio, destino, peso;
		while(m.find())
		{
			inicio = Integer.parseInt(m.group());
			if(m.find())
				destino = Integer.parseInt(m.group());
			else
				throw new IllegalArgumentException("Ultima arista incompleta!");
			if(m.find())
				peso = Integer.parseInt(m.group());
			else
				throw new IllegalArgumentException("Ultima arista incompleta!");
			ret.agregarArista(inicio, destino, peso);
		}
		return ret;
	}
}
