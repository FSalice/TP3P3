package TP2;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Random;

public class Solucion implements Comparable<Solucion>{

	private Grafo instancia;
	// Recorrido representado en indices
	private int[] recorrido;
	// Longitud del recorrido
	private double longitud;
	// variable global usada para verificar que no se repitan vertices en un recorrido
	private boolean[] usados;

	// solucion "vacia"
	private Solucion(Grafo g) 
	{
		instancia = g.clonar();
		
		ArrayList<Ciudad> ciudades = instancia.getEstaciones();
		for(int i = 0; i < ciudades.size(); i++)
			for(int j = i+1; j < ciudades.size(); j++ )
				if(!instancia.existeArista(i, j))
				{
					double c_1 = ciudades.get(i).getLat()-ciudades.get(j).getLat();
					double c_2 = ciudades.get(i).getLon()-ciudades.get(j).getLon();
					double calculo = (Math.sqrt((c_1*c_1) + (c_2*c_2))); 
					instancia.agregarArista(i, j, (int) (calculo*111));
				}
		
		recorrido = new int[instancia.getSize()];
		if (recorrido.length < 3)
			throw new IllegalArgumentException("Tamaño de instancia invalido");
		for (int i = 0; i < recorrido.length; i++)
			recorrido[i] = -1;
		longitud = 0;
	}

	// Recorre todas las ciudades yendo a una de las ciudades mas cercanas en cada iteracion
	public static Solucion recorridoGolosoAleatorizado(Grafo instancia, int inicial, int cantidadAleatorias) 
	{
		Solucion ret = new Solucion(instancia);

		ret.recorrido[0] = inicial;
		ret.usados = new boolean[instancia.getSize()];
		ret.usados[inicial]=true;

		for (int i = 1; i < ret.recorrido.length; i++) 
		{	
			int cercana = ret.ciudadMasCercana(ret.recorrido[i-1], cantidadAleatorias);
			ret.usados[cercana]=true;
			ret.recorrido[i] = cercana;
			ret.longitud += ret.instancia.pesoArista(ret.recorrido[i - 1],
					ret.recorrido[i]);
		}
		
		ret.longitud += ret.instancia.pesoArista(
				ret.recorrido[ret.recorrido.length - 1], ret.recorrido[0]);
		return ret;
	}

	public static Solucion recorridoGoloso(Grafo instancia){
		return recorridoGolosoAleatorizado(instancia, 0 ,1);
	}
	// Retorna la ciudad mas cercana no utilizada antes
	private int ciudadMasCercana(int desde)
	{
 		int ret = -1;
 		int distanciaMinima = Integer.MAX_VALUE;
 		for(int i = 0; i < recorrido.length; i++)
 		{
 			int distanciaConI = instancia.pesoArista(desde, i);
 			
 			if(!usados[i] && distanciaConI<distanciaMinima){
 				distanciaMinima = distanciaConI;
 				ret = i;
 			}
 		}
 		return ret;		
	}

	private int ciudadMasCercana(int desde, int cantidadAleatorias) 
	{
		if (cantidadAleatorias < 1)
			throw new IllegalArgumentException(
					"La cantidad de minimos aleatorios especificada es invalida:"
							+ cantidadAleatorias);

		int[] elegidos = new int[cantidadAleatorias];

		for (int i = 0; i < cantidadAleatorias; i++) 
		{
			elegidos[i] = ciudadMasCercana(desde);
			if(elegidos[i]!=-1)
				usados[elegidos[i]]=true;
		}
		
		int indiceMaximo = 0;
		for (int i = 0; i < cantidadAleatorias; i++)
			if (elegidos[i] != -1)
				indiceMaximo = i;

		Random r = new Random();
		int ret = elegidos[r.nextInt(indiceMaximo+1)];
		
		for (int i = 0; i < cantidadAleatorias; i++) 
		{
			if(elegidos[i]!=-1)
				usados[elegidos[i]]=false;
		}
		
		return ret;
	}



	
	public Solucion mejorSwap(){
		Solucion ret = this;
		Solucion aux = this;
		while(aux!=null)
		{
			ret = aux;
			aux = aux.mejorarSwap();
		}
		return ret;
	}
	
	public Solucion mejorarSwap() 
	{
		Solucion ret = clone();
		Solucion aux;
		for (int i = 0; i < recorrido.length; i++)
			for (int j = i; j < recorrido.length; j++) {
				aux = clone();
				aux.swap(i, j);
				if (ret.getLongitud() > aux.getLongitud())
				{
					ret = aux;
					i=j=recorrido.length;
				}
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
	
	public ArrayList<Integer> getRecorridoList() {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for(int i : recorrido)
			ret.add(i);
		ret.add(recorrido[0]);
		return ret;
	}

	public double getLongitud() {
		return longitud;
	}

	@Override
	public int compareTo(Solucion o) {
		if(getLongitud()>o.getLongitud()) return 1;
		if(getLongitud()<o.getLongitud()) return 0;
		return -1;
	}
	
	@Override
	public String toString()
	{
		String ret = "[";
		for(int i : recorrido)
			ret += i;
		ret+="]";
		
		ret+= " - " + getLongitud();
		
		return ret;
	}
	
	public Solucion mejorarCruzados()
	{
		Solucion ret = clone();
		for(int i = 0; i < recorrido.length-1; i++)
			for(int j = 0; j < recorrido.length-1; j++)
				if(Math.abs(i-j) > 1 && Math.abs(i-j) < recorrido.length 
				&& cruzados(ret.recorrido[i], ret.recorrido[i+1], ret.recorrido[j], ret.recorrido[j+1]))
				{
					eliminarCruzamiento(i, i+1, j, j+1);
				}
		//TODO: faltan casos de borde
		return ret;
	}
	
	
	private boolean cruzados(int a_1, int a_2, int b_1, int b_2)
	{
		Line2D.Double arista_1 = new Line2D.Double(instancia.get(a_1).getLat(),
				instancia.get(a_1).getLon(),
				instancia.get(a_2).getLat(),
				instancia.get(a_2).getLon());
		Line2D.Double arista_2 = new Line2D.Double(instancia.get(b_1).getLat(),
				instancia.get(b_1).getLon(),
				instancia.get(b_2).getLat(),
				instancia.get(b_2).getLon());
		return arista_1.intersectsLine(arista_2);
	}
	
	private Solucion eliminarCruzamiento(int a_1, int a_2, int b_1, int b_2)
	{
		Solucion mejor;
		
		int inda_1 = -1, indb_1 = -1, indb_2 = -1;
		for(int i = 0; i < recorrido.length; i++)
		{
			if(recorrido[i]==a_1) inda_1 = i;
			if(recorrido[i]==b_1) indb_1 = i;
			if(recorrido[i]==b_2) indb_2 = i; 
		}
		
		Solucion aux = descruzar(inda_1,indb_1);
		mejor = this.longitud>aux.longitud ? aux : this;
		
		aux = descruzar(indb_1, inda_1);
		mejor = mejor.longitud>aux.longitud ? aux : mejor;
		
		aux = descruzar(inda_1, indb_2);
		mejor = mejor.longitud>aux.longitud ? aux : mejor;
		
		aux = descruzar(indb_2, inda_1);
		mejor = mejor.longitud>aux.longitud ? aux : mejor;
		
		return mejor;
	}

	private Solucion descruzar(int i, int j) {
		Solucion ret = clone();
		while(Math.abs(i-j)>1)
		{
			if(j<0) j = recorrido.length-1;
			if(i == recorrido.length) i = 0;
			
			ret.swap(i, j);
			
			i++;
			j--;
		}
		return ret;
	}
}
