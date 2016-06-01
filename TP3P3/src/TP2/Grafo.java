package TP2;

import java.io.Serializable;
import java.util.ArrayList;

import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;

/**
 * Representa el grafo como una lista de Estacion y 
 * las herramientas para manipularlo y calcular 
 * Caminos minimos en base al costo o a la cantidad de trasbordos
 */
public class Grafo implements Serializable {
	private static final long serialVersionUID = 1L;
	// Representamos el grafo por listas de vecinos
	private ArrayList<Ciudad> estaciones;
	private boolean[] out;

	/**
	 * Se construye un grafo vacio
	 */
	public Grafo() {
		estaciones = new ArrayList<Ciudad>();
	}

	@Override
	public String toString() {
		return estaciones.toString();
	}

	// Agregar una arista
	public boolean agregarArista(int i, int j, Integer peso) {
		chequearArista(estaciones.get(i), estaciones.get(j),peso);

		estaciones.get(i).agregarArista(estaciones.get(j), peso);
		return estaciones.get(j).agregarArista(estaciones.get(i), peso);
	}

	public void agregarArista(Ciudad i, Ciudad j, Integer peso) {
		chequearArista(i, j,peso);
		
		i.agregarArista(j, peso);
		j.agregarArista(i, peso);
	}

	// Borrar una arista
	public void eliminarArista(Ciudad i, Ciudad j) {
		chequearArista(i, j,0);

		i.eliminarArista(j);
		j.eliminarArista(i);
	}

	// Consultar si existe una arista
	public boolean existeArista(Ciudad i, Ciudad j) {
		chequearArista(i, j,0);

		return i.conectado(j);
	}
	
	public boolean existeArista(int i, int j) {
		chequearArista(estaciones.get(i), estaciones.get(j),0);

		return estaciones.get(i).conectado(estaciones.get(j));
	}

	// Código defensivo: Chequea que los parámetros sean válidos
	private void chequearVertice(Ciudad i) {
		if (!estaciones.contains(i))
			throw new IllegalArgumentException("La estacion " + i
					+ " no esta contenida en el grafo!");
	}

	private void chequearArista(Ciudad i, Ciudad j, int peso) {
		chequearVertice(i);
		chequearVertice(j);

		if (i.equals(j))
			throw new IllegalArgumentException("No se pueden agregar loops!");
		if (peso<0)
			throw new IllegalArgumentException("No se pueden agregar aristas con peso negativo!");
	}

	public void agregarEstacion(Ciudad e) {
		estaciones.add(e);
	}

	public void eliminarEstacion(int i) {
		Ciudad aux = estaciones.get(i);
		estaciones.remove(i);
		for (Ciudad j : estaciones) {
			if (j.conectado(aux)) {
				j.eliminarArista(aux);
			}
		}
	}

	public void eliminarAristas(ICoordinate inicio, ICoordinate fin) {
		for (Ciudad i : estaciones) {
			i.eliminarArista(inicio, fin);
		}
	}

	public void editarArista(ICoordinate inicio, ICoordinate fin, int nuevoPeso) {
		for (Ciudad i : estaciones) {
			i.editarArista(inicio, fin, nuevoPeso);
		}
	}

	public int getAristas() {
		int aristas = 0;
		for (Ciudad i : estaciones)
			aristas = aristas + i.getAristas();
		return aristas / 2;
	}
	
	public int pesoArista(Ciudad a, Ciudad b){
		return a.pesoArista(b);
	}
	public int pesoArista(int a, int b){
		return estaciones.get(a).pesoArista(estaciones.get(b));
	}

	public Ciudad get(int i) {
		return estaciones.get(i);
	}

	public void editarEstacion(int i, String nuevoNombre) {
		estaciones.get(i).editar(nuevoNombre);
	}
	
	public ArrayList<Integer> caminoMinimo(Ciudad inicio,
			Ciudad destino, boolean porPeso){
		int indiceActual = estaciones.indexOf(inicio);
		int indiceDestino = estaciones.indexOf(destino);
		
		ArrayList<int[]> pesoYnodo = new ArrayList<int[]>();
		for (int i = 0; i < estaciones.size(); i++) {
			pesoYnodo.add(new int[] { Integer.MAX_VALUE, -1 });
		}
		
		pesoYnodo.set(indiceActual, new int[]{0,-1});
		
		out = new boolean[estaciones.size()];
		
		while(indiceActual!=indiceDestino && indiceActual != -1){
			dijkstra(estaciones.get(indiceActual), pesoYnodo);
			indiceActual = minimo(pesoYnodo);
		}
		
		out = null;
		if (pesoYnodo.get(indiceDestino)[1] == -1) {
			return null;
		} else {
			ArrayList<Integer> ret = new ArrayList<Integer>();
			int indice = indiceDestino;
			while (indice != -1) {
				ret.add(0, indice);
				indice = pesoYnodo.get(indice)[1];
			}
			return ret;
		}
	}
	
	private void dijkstra(Ciudad estacionActual, ArrayList<int[]> pesoYnodo){
		int indice = estaciones.indexOf(estacionActual);
		out[indice] = true;
		for(Ciudad e : estacionActual.getVecinos()){
			int indiceE = estaciones.indexOf(e);
			if(!out[indiceE]){
					if (pesoYnodo.get(indiceE)[0] > pesoYnodo.get(indice)[0] + estacionActual.pesoArista(e)) {
						pesoYnodo.get(indiceE)[0] = pesoYnodo.get(indice)[0] + estacionActual.pesoArista(e); // peso
						pesoYnodo.get(indiceE)[1] = indice; // nodo
				}
			}
		}
	}
	private int minimo(ArrayList<int[]> pesoYnodo){
		int ret = -1;
		int min = Integer.MAX_VALUE;
		for(int i = 0; i < pesoYnodo.size(); i++){
			if(!out[i] && pesoYnodo.get(i)[0]<min){
				ret = i;
				min = pesoYnodo.get(i)[0];
			}
		}
		return ret;
	}

	public ArrayList<Ciudad> getEstaciones() {
		return estaciones;
	}
	
	public int getSize(){
		return estaciones.size();
	}
	
	public Grafo clonar()
	{
		Grafo ret = new Grafo();
		for(Ciudad c : estaciones)
			ret.agregarEstacion(c.clonar());
		
		return ret;
	}
}