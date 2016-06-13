package TP2;

import java.io.Serializable;
import java.util.ArrayList;

import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;

public class Ciudad implements Serializable {//cambiaria estacion por ciudad la
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nombre; //eliminaria linea y agregaria private boolean CiudadInicial;
	private double lat, lon;
	private ArrayList<Ciudad> vecinos;
	private ArrayList<Integer> pesos;

	// private static boolean llegue;

	Ciudad(String nombre, double lat, double lon) {
		this.nombre = nombre;
		this.lat = lat;
		this.lon = lon;
		vecinos = new ArrayList<Ciudad>();
		pesos = new ArrayList<Integer>();
	}

	ArrayList<Ciudad> getVecinos() {
		return vecinos;
	}

	@Override
	public String toString() {
		String ret = lat + "/" + lon + "vecinos: " + vecinos.size();
		for (Integer i : pesos) {
			ret = ret + "\n" + i;
		}
		return ret;
	}

	@Override
	public boolean equals(Object otro) {
		boolean ret = true;
		if (otro instanceof Ciudad) {

			if (nombre != null)
				ret = ret && nombre.equals(((Ciudad) otro).nombre);
			else
				ret = ret && ((Ciudad) otro).nombre == null;
			
			ret = ret && lat == ((Ciudad) otro).lat;
			ret = ret && lon == ((Ciudad) otro).lon;
		} else
			ret = false;
		return ret;
	}

	
	public void editar(String nombre){
		this.nombre = nombre;
	}

	public boolean conectado(Ciudad otro) {
		return vecinos.contains(otro);
	}

	public int pesoArista(Ciudad otro) {
		if (vecinos.contains(otro))
			return pesos.get(vecinos.indexOf(otro));
		else
			return Integer.MAX_VALUE;
	}

	public boolean agregarArista(Ciudad j, Integer peso) 
	{
		boolean ret = !vecinos.contains(j);
		if(ret)
		{
		vecinos.add(j);
		pesos.add(peso);
		}
		return ret;
	}

	public void eliminarArista(Ciudad j) {
		int aux = vecinos.indexOf(j);
		if (aux != -1) {
			pesos.remove(aux);
			vecinos.remove(j);
		}
	}

	public void eliminarArista(ICoordinate inicio, ICoordinate fin) {
		double tole = 0.00001;
		if ((Math.abs(this.lat - inicio.getLat()) < tole && Math.abs(this.lon
				- inicio.getLon()) < tole)) {
			for (int i = vecinos.size() - 1; i >= 0; i--) {
				if ((Math.abs(vecinos.get(i).lat - fin.getLat()) < tole && Math
						.abs(vecinos.get(i).lon - fin.getLon()) < tole)) {
					vecinos.remove(i);
					pesos.remove(i);
				}
			}
		}
		if ((Math.abs(this.lat - fin.getLat()) < tole && Math.abs(this.lon
				- fin.getLon()) < tole)) {
			for (int i = vecinos.size() - 1; i >= 0; i--) {
				if ((Math.abs(vecinos.get(i).lat - inicio.getLat()) < tole && Math
						.abs(vecinos.get(i).lon - inicio.getLon()) < tole)) {
					vecinos.remove(i);
					pesos.remove(i);
				}
			}
		}
	}

	public void editarArista(ICoordinate inicio, ICoordinate fin, int nuevoPeso) {
		double tole = 0.00001;
		if ((Math.abs(this.lat - inicio.getLat()) < tole && Math.abs(this.lon
				- inicio.getLon()) < tole)) {
			for (int i = vecinos.size() - 1; i >= 0; i--) {
				if ((Math.abs(vecinos.get(i).lat - fin.getLat()) < tole && Math
						.abs(vecinos.get(i).lon - fin.getLon()) < tole)) {
					pesos.set(i, nuevoPeso);
				}
			}
		}
		if ((Math.abs(this.lat - fin.getLat()) < tole && Math.abs(this.lon
				- fin.getLon()) < tole)) {
			for (int i = vecinos.size() - 1; i >= 0; i--) {
				if ((Math.abs(vecinos.get(i).lat - inicio.getLat()) < tole && Math
						.abs(vecinos.get(i).lon - inicio.getLon()) < tole)) {
					pesos.set(i, nuevoPeso);
				}
			}
		}
	}

	public int getAristas() {
		return vecinos.size();
	}

	public String getNombre() {
		return nombre;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public Ciudad clonar() {
		Ciudad ret = new Ciudad(getNombre(),getLat(),getLon());
		
		return ret;
	}
}
