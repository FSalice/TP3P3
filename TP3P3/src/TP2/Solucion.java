package TP2;

import java.util.HashSet;
import java.util.Set;

public class Solucion {

	private Grafo instancia;
	// Recorrido representado en indices
	private int[] recorrido;
	// Longitud del recorrido
	private int longitud;
	private Set<Integer> usados;

	// solucion "vacia"
	private Solucion(Grafo g) 
	{
		instancia = g;
		recorrido = new int[instancia.getSize()];
		if(recorrido.length<3)	throw new IllegalArgumentException("Tamaño de instancia invalido");
		for(int i = 0; i < recorrido.length; i++)
			recorrido[i] = -1;
		longitud = Integer.MAX_VALUE;
	}
	
	//Recorre todas las ciudades yendo a la mas cercana en cada iteracion
	public Solucion recorridoGoloso(Grafo instancia)
	{
		Solucion ret = new Solucion(instancia);
		
		recorrido[0] = 0;
		usados = new HashSet<Integer>();
		usados.add(0);
		
		for(int i = 1; i < recorrido.length-1; i++)
		{
			recorrido[i] = ciudadMasCercana(i-1);
		}
		recorrido[recorrido.length-1] = 0;
		
		return ret;
	}
	
	//Retorna la ciudad mas cercana no utilizada antes
	private int ciudadMasCercana(int desde){
		int ret = -1;
		int distanciaMinima = Integer.MAX_VALUE;
		for(int i = 0; i < recorrido.length; i++)
		{
			int distanciaConI = instancia.pesoArista(desde, i);
			
			if(!usados.contains(i) && distanciaConI<distanciaMinima){
				distanciaMinima = distanciaConI;
				ret = i;
			}
		}
		usados.add(ret);
		return ret;
	}
	
	public int[] getRecorrido() 
	{
		return recorrido;
	}

	public int getLongitud() 
	{
		return longitud;
	}
}
