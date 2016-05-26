package TP2;

public class Solucion {

	private Grafo instancia;
	//Recorrido representado en indices
	private int[] recorrido;
	//Longitud del recorrido
	private int longitud;
	
	//solucion "vacia"
	private Solucion(Grafo g){
		instancia = g;
		recorrido = new int[instancia.getSize()];
		longitud = Integer.MAX_VALUE;
	}
}
