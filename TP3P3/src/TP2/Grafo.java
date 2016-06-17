package TP2;

import java.io.Serializable;
import java.util.ArrayList;

import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;

/**
 * Representa el grafo como una lista de ciudades y 
 * las herramientas para manipularlo y calcular 
 * Caminos minimos en base al costo o a la cantidad de trasbordos
 */
public class Grafo implements Serializable {
	private static final long serialVersionUID = 1L;
	// Representamos el grafo por listas de vecinos
	private ArrayList<Ciudad> ciudades;
	private boolean[] out;

	/**
	 * Se construye un grafo vacio
	 */
	public Grafo() {
		ciudades = new ArrayList<Ciudad>();
	}

	@Override
	public String toString() {
		return ciudades.toString();
	}

	// Agregar una arista
	public boolean agregarArista(int i, int j, Integer peso) 
	{
		chequearArista(ciudades.get(i), ciudades.get(j),peso);

		ciudades.get(i).agregarArista(ciudades.get(j), peso);
		boolean ret = ciudades.get(j).agregarArista(ciudades.get(i), peso);
		
		return ret;
	}

	public void agregarArista(Ciudad i, Ciudad j, Integer peso) 
	{
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
		chequearArista(ciudades.get(i), ciudades.get(j),0);

		return ciudades.get(i).conectado(ciudades.get(j));
	}

	// Código defensivo: Chequea que los parámetros sean válidos
	private void chequearVertice(Ciudad i) {
		if (!ciudades.contains(i))
			throw new IllegalArgumentException("La ciudad " + i
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

	public void agregarCiudad(Ciudad e) {
		ciudades.add(e);
	}

	public void eliminarCiudad(int i) {
		Ciudad aux = ciudades.get(i);
		ciudades.remove(i);
		for (Ciudad j : ciudades) {
			if (j.conectado(aux)) {
				j.eliminarArista(aux);
			}
		}
	}

	public void eliminarAristas(ICoordinate inicio, ICoordinate fin) {
		for (Ciudad i : ciudades) {
			i.eliminarArista(inicio, fin);
		}
	}

	public void editarArista(ICoordinate inicio, ICoordinate fin, int nuevoPeso) {
		for (Ciudad i : ciudades) {
			i.editarArista(inicio, fin, nuevoPeso);
		}
	}

	public int getAristas() {
		int aristas = 0;
		for (Ciudad i : ciudades)
			aristas = aristas + i.getAristas();
		return aristas / 2;
	}
	
	public int pesoArista(Ciudad a, Ciudad b){
		return a.pesoArista(b);
	}
	public int pesoArista(int a, int b){
		if(Math.max(a, b)>=ciudades.size())
			throw new IndexOutOfBoundsException("Indice "+Math.max(a, b)+ " fuera del limite");
		if(Math.min(a, b)<0)
			throw new IndexOutOfBoundsException("Indice "+Math.min(a, b)+ " fuera del limite");
		return ciudades.get(a).pesoArista(ciudades.get(b));
	}

	public Ciudad get(int i) {
		return ciudades.get(i);
	}

	public void editarCiudad(int i, String nuevoNombre) {
		ciudades.get(i).editar(nuevoNombre);
	}
	
	public ArrayList<Integer> caminoMinimo(Ciudad inicio,
			Ciudad destino, boolean porPeso){
		int indiceActual = ciudades.indexOf(inicio);
		int indiceDestino = ciudades.indexOf(destino);
		
		ArrayList<int[]> pesoYnodo = new ArrayList<int[]>();
		for (int i = 0; i < ciudades.size(); i++) {
			pesoYnodo.add(new int[] { Integer.MAX_VALUE, -1 });
		}
		
		pesoYnodo.set(indiceActual, new int[]{0,-1});
		
		out = new boolean[ciudades.size()];
		
		while(indiceActual!=indiceDestino && indiceActual != -1){
			dijkstra(ciudades.get(indiceActual), pesoYnodo);
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
	
	private void dijkstra(Ciudad ciudadActual, ArrayList<int[]> pesoYnodo){
		int indice = ciudades.indexOf(ciudadActual);
		out[indice] = true;
		for(Ciudad e : ciudadActual.getVecinos()){
			int indiceE = ciudades.indexOf(e);
			if(!out[indiceE]){
					if (pesoYnodo.get(indiceE)[0] > pesoYnodo.get(indice)[0] + ciudadActual.pesoArista(e)) {
						pesoYnodo.get(indiceE)[0] = pesoYnodo.get(indice)[0] + ciudadActual.pesoArista(e); // peso
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

	public ArrayList<Ciudad> getCiudades() {
		return ciudades;
	}
	
	public int getSize(){
		return ciudades.size();
	}
	
	public Grafo clonar()
	{
		Grafo ret = new Grafo();
		for(Ciudad c : ciudades)
			ret.agregarCiudad(c.clonar());
		
		for(int i = 0; i < ciudades.size(); i++) for(int j = i+1; j < ciudades.size(); j++)
			if(this.existeArista(i, j))
				ret.agregarArista(i, j, pesoArista(i, j));
		
		return ret;
	}
}