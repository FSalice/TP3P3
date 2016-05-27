package TP2;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class SolucionTest 
{
	
	@Test
	public void swapTest()
	{
		chequearSwap("0,3,2,1","4 0,1,10 ; 1,2,10 ; 2,3,10 ; 3,0,10 ; 0,2,1 ; 3,1,1",1,2);
		chequearSwap("3,2,0,1","4 0,1,10 ; 1,2,10 ; 2,3,10 ; 3,0,10 ; 0,2,1 ; 3,1,1",0,2);
		chequearSwap("0,2,1,3","4 0,1,10 ; 1,2,10 ; 2,3,10 ; 3,0,10 ; 0,2,1 ; 3,1,1",3,2);
		chequearSwap("0,2,1,3","4 0,1,10 ; 1,2,10 ; 2,3,10 ; 3,0,10 ; 0,2,1 ; 3,1,1",2,3);
		chequearSwap("1,2,3,0","4 0,1,10 ; 1,2,10 ; 2,3,10 ; 3,0,10 ; 0,2,1 ; 3,1,1",0,3);
		chequearSwap("0,2,3,1","4 0,1,10 ; 1,2,10 ; 2,3,10 ; 3,0,10 ; 0,2,1 ; 3,1,1",2,2);
	}
	private void chequearSwap(String resultadoEsperado, String grafo, int i, int j)
	{
		int[] esperado = recorrido(resultadoEsperado);
		Grafo g = generarInstancia(grafo);
		Solucion s = Solucion.recorridoGoloso(g);
		s.swap(i, j);
		for(int indice = 0; indice < s.getRecorrido().length; indice++)
			assertEquals(s.getRecorrido()[indice], esperado[indice]);
	}
	
	@Test
	public void longitudGolosoTest() 
	{
		chequearLongitudGoloso(30, "3 0,1,10 ; 1,2,10 ; 2,0,10");
		chequearLongitudGoloso(40, "4 0,1,10 ; 1,2,10 ; 2,3,10; 3,0,10");
		chequearLongitudGoloso(22, "4 0,1,10 ; 1,2,10 ; 2,3,10 ; 3,0,10 ; 0,2,1 ; 3,1,1" );
		chequearLongitudGoloso(4, "4 0,1,1 ; 1,2,1 ; 2,3,1 ; 3,0,13 ; 0,2,1 ; 3,1,1" );
	}
	
	private void chequearLongitudGoloso(int longitudEsperada, String grafo)
	{
		Grafo g = generarInstancia(grafo);
		Solucion s = Solucion.recorridoGoloso(g);
		assertEquals(longitudEsperada,s.getLongitud());
	}
	
	@Test
	public void recorridoGolosoTest() 
	{
		chequearRecorridoGoloso("0,2,1,0", "3 0,1,10 ; 1,2,10 ; 2,0,10");
		chequearRecorridoGoloso("0,3,2,1,0", "4 0,1,10 ; 1,2,10 ; 2,3,10; 3,0,10");
		chequearRecorridoGoloso("0,2,3,1,0", "4 0,1,10 ; 1,2,10 ; 2,3,10 ; 3,0,10 ; 0,2,1 ; 3,1,1" );
	}
	private void chequearRecorridoGoloso(String recorridoEsperado, String grafo)
	{
		Grafo g = generarInstancia(grafo);
		Solucion s = Solucion.recorridoGoloso(g);
		int[] esperado = recorrido(recorridoEsperado);
		int[] obtenido = s.getRecorrido();
		
		for(int i = 0; i < obtenido.length; i++)
			assertEquals(esperado[i],obtenido[i]);
	}
	
	
	//las aristas se definen como "6 : 0,5,45;0,2,23;"(tamañoDeGrafo : inicio, destino, peso;)
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
	
	private int[] recorrido(String a)
	{
		Pattern pat = Pattern.compile("\\d");
		Matcher m = pat.matcher(a);
		ArrayList<Integer> aux = new ArrayList<Integer>();
		
		while(m.find())
			aux.add(Integer.parseInt(m.group()));
		
		int[] ret = new int[aux.size()];
		
		for(int i = 0; i < aux.size(); i++)
			ret[i] = aux.get(i);
		
		return ret;
	}
}
