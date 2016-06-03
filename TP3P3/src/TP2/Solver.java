package TP2;


public class Solver implements Runnable {

	private boolean parar;
	private Grafo instancia;
	private GUIMAP interfaz;
	private int soluciones, cantAleatorias;
	private Solucion mejorHastaAhora;
	private Thread t;
	public Solver(Grafo instancia, int solucionesIniciales, int cantidadAleatorias, GUIMAP interfaz)
	{
		this.interfaz = interfaz;
		parar = false;
		this.instancia = instancia;
		this.soluciones = solucionesIniciales;
		this.cantAleatorias = cantidadAleatorias;
	}
	
	@Override
	public void run() 
	{
		mejorHastaAhora = Solucion.recorridoGoloso(instancia, 0, 1);
		Solucion aux = null;
		for (int i = 0; i < soluciones && !parar; i++)
		{
			aux = Solucion.recorridoGoloso(instancia, 0, cantAleatorias);
			while (aux != null && !parar) 
			{
				if(aux.getLongitud()<mejorHastaAhora.getLongitud())
				{
					mejorHastaAhora = aux;
					interfaz.setAvance(mejorHastaAhora.getLongitud());
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
