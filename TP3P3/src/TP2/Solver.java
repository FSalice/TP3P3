package TP2;


public class Solver implements Runnable {

	private boolean parar;
	private Grafo instancia;
	private GUIMAP interfaz;
	private int iteraciones, cantAleatorias;
	private Solucion mejorHastaAhora;
	private Thread t;
	public enum Algoritmo {busquedaLocal, evolutivo};

	public Solver(Grafo instancia, int solucionesIniciales, int cantidadAleatorias, GUIMAP interfaz)
	{
		this.interfaz = interfaz;
		parar = false;
		this.instancia = instancia;
		this.iteraciones = solucionesIniciales;
		this.cantAleatorias = cantidadAleatorias;
	}
	
	@Override
	public void run() 
	{
		mejorHastaAhora = Solucion.recorridoGoloso(instancia);
		System.out.println(mejorHastaAhora.getLongitud());
		Solucion aux;
		int iteracion = 0;
		for (int i = 0; i != iteraciones && !parar; i++)
		{
			aux = Solucion.recorridoGolosoAleatorizado(instancia, 0, cantAleatorias);
			while (aux != null && !parar) 
			{	
				iteracion++;
				interfaz.setAvance(iteracion, aux.getLongitud(), Algoritmo.busquedaLocal);
				if(aux.getLongitud()<mejorHastaAhora.getLongitud())
				{
					mejorHastaAhora = aux;
				}
				aux = aux.mejorarSwap();
			}
		}
	}
	
	public void start()
	{
		if(t==null){
		t = new Thread(this);
		t.start();
		}
	}
	
	public Solucion stop()
	{
		parar = true;
		return mejorHastaAhora;
	}
}
