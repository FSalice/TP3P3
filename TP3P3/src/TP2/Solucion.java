package TP2;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Solucion {

	private Grafo instancia;
	// Recorrido representado en indices
	private int[] recorrido;
	// Longitud del recorrido
	private int longitud;
	// variable global usada para verificar que no se repitan vertices en un recorrido
	private Set<Integer> usados;

	// solucion "vacia"
	private Solucion(Grafo g) 
	{
		instancia = g;
		recorrido = new int[instancia.getSize()];
		if (recorrido.length < 3)
			throw new IllegalArgumentException("Tama�o de instancia invalido");
		for (int i = 0; i < recorrido.length; i++)
			recorrido[i] = -1;
		longitud = 0;
	}

	// Recorre todas las ciudades yendo a la mas cercana en cada iteracion
	public static Solucion recorridoGoloso(Grafo instancia, int inicial, int cantidadAleatorias) 
	{
		Solucion ret = new Solucion(instancia);

		ret.recorrido[0] = inicial;
		ret.usados = new HashSet<Integer>();
		ret.usados.add(inicial);

		for (int i = 1; i < ret.recorrido.length; i++) {
			int cercana = ret.ciudadMasCercana(i-1, cantidadAleatorias);
			ret.usados.add(cercana);
			ret.recorrido[i] = cercana;
			ret.longitud += instancia.pesoArista(ret.recorrido[i - 1],
					ret.recorrido[i]);
		}

		ret.longitud += instancia.pesoArista(
				ret.recorrido[ret.recorrido.length - 1], ret.recorrido[0]);

		return ret;
	}

	// Retorna la ciudad mas cercana no utilizada antes
	private int ciudadMasCercana(int desde, int cantidadAleatorias) 
	{

		if (cantidadAleatorias < 1)
			throw new IllegalArgumentException(
					"La cantidad de minimos aleatorios especificada es invalida:"
							+ cantidadAleatorias);

		int[] elegidos = new int[cantidadAleatorias];
		int[] distanciasMinimas = new int[cantidadAleatorias];

		for (int i = 0; i < cantidadAleatorias; i++) 
		{
			elegidos[i] = -1;
			distanciasMinimas[i] = Integer.MAX_VALUE;
		}

		for (int i = 0; i < instancia.getSize(); i++) 
		{
			int distanciaCiudadActual = instancia.pesoArista(desde, i);

			if (!usados.contains(i))
				for (int j = 0; j < cantidadAleatorias; j++)
					if (distanciaCiudadActual <= distanciasMinimas[j]) 
					{
						distanciasMinimas[j] = distanciaCiudadActual;
						elegidos[j] = i;
						j = cantidadAleatorias;// TODO: esto es un asco
					}

		}
		int indiceMaximo = 0;
		for (int i = 0; i < cantidadAleatorias; i++)
			if (elegidos[i] != -1)
				indiceMaximo = i;

		Random r = new Random();

		return elegidos[r.nextInt(indiceMaximo + 1)];
	}
	
	//TODO: necesitamos testear
	public Solucion mejorSwap() {
		Solucion ret = clone();
		Solucion aux;
		for (int i = 0; i < recorrido.length; i++)
			for (int j = i; j < recorrido.length; j++) {
				aux = clone();
				aux.swap(i, j);
				if (ret.getLongitud() > aux.getLongitud())
					ret = aux;
			}
		return ret.getLongitud() < getLongitud() ? ret : null;
	}

	void swap(int i, int j) {
		chequearIndice(i);
		chequearIndice(j);

		longitud -= calcularLongitud(i);
		longitud -= calcularLongitud(j);

		int aux = recorrido[i];
		recorrido[i] = recorrido[j];
		recorrido[j] = aux;

		longitud += calcularLongitud(i);
		longitud += calcularLongitud(j);
	}

	private void chequearIndice(int i) {
		if (i >= recorrido.length || i < 0)
			throw new IndexOutOfBoundsException(
					"indice i fuera de los limites: " + i);
	}

	private int calcularLongitud(int indice) {
		int ret = 0;

		if (indice == 0)
			ret += instancia.pesoArista(recorrido[recorrido.length - 1],
					recorrido[indice]);
		else
			ret += instancia.pesoArista(recorrido[indice - 1],
					recorrido[indice]);

		if (indice == recorrido.length - 1)
			ret += instancia.pesoArista(recorrido[0], recorrido[indice]);
		else
			ret += instancia.pesoArista(recorrido[indice + 1],
					recorrido[indice]);

		return ret;
	}

	@Override
	public Solucion clone() {
		Solucion ret = new Solucion(instancia);

		for (int i = 0; i < recorrido.length; i++) {
			ret.recorrido[i] = recorrido[i];
		}
		ret.longitud = longitud;
		return ret;
	}

	public int[] getRecorrido() {
		return recorrido;
	}

	public int getLongitud() {
		return longitud;
	}
}
