package TP2;

import java.util.HashSet;
import java.util.Set;

public class Solucion {

	private Grafo instancia;
	// Recorrido representado en indices
	private int[] recorrido;
	// Longitud del recorrido
	private int longitud;
	// blah TODO
	private Set<Integer> usados;

	// solucion "vacia"
	private Solucion(Grafo g) {
		instancia = g;
		recorrido = new int[instancia.getSize()];
		if (recorrido.length < 3)
			throw new IllegalArgumentException("Tamaño de instancia invalido");
		for (int i = 0; i < recorrido.length; i++)
			recorrido[i] = -1;
		longitud = 0;
	}

	// Recorre todas las ciudades yendo a la mas cercana en cada iteracion
	public static Solucion recorridoGoloso(Grafo instancia) {
		Solucion ret = new Solucion(instancia);

		ret.recorrido[0] = 0;
		ret.usados = new HashSet<Integer>();
		ret.usados.add(0);

		for (int i = 1; i < ret.recorrido.length; i++) {
			ret.recorrido[i] = ret.ciudadMasCercana(i - 1);
			ret.longitud += instancia.pesoArista(ret.recorrido[i - 1],
					ret.recorrido[i]);
		}

		ret.longitud += instancia.pesoArista(
				ret.recorrido[ret.recorrido.length - 1], ret.recorrido[0]);

		return ret;
	}

	// Retorna la ciudad mas cercana no utilizada antes
	private int ciudadMasCercana(int desde) {
		int ret = -1;
		int distanciaMinima = Integer.MAX_VALUE;
		for (int i = 0; i < instancia.getSize(); i++) {
			int distanciaConI = instancia.pesoArista(desde, i);

			if (!usados.contains(i) && distanciaConI <= distanciaMinima) {
				distanciaMinima = distanciaConI;
				ret = i;
			}
		}
		usados.add(ret);
		return ret;
	}

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
