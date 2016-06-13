package TP2;


public class Solver implements Runnable {

	private boolean parar;
	private Grafo instancia;
	private GUIMAP interfaz;
	private int iteraciones, cantAleatorias;
	private Solucion mejorHastaAhora;
	private Thread t;
	private int iteracion;
	private Solucion solucionActual;

	public Solver(Grafo instancia, int solucionesIniciales, int cantidadAleatorias, GUIMAP interfaz)
	{
		this.interfaz = interfaz;
		parar = false;
		this.instancia = instancia;
		this.iteraciones = solucionesIniciales;
		this.cantAleatorias = cantidadAleatorias;
		mejorHastaAhora = Solucion.recorridoGoloso(instancia);
		iteracion = 0;
		solucionActual = mejorHastaAhora;
	}
	
	@Override
	public void run() 
	{
		for (int i = 0; i != iteraciones && !parar; i++)
		{	
			while (solucionActual != null && !parar) 
			{	
				if(solucionActual.getLongitud()<mejorHastaAhora.getLongitud())
				{
					mejorHastaAhora = solucionActual;
				}
				iteracion++;
				interfaz.setAvance(iteracion, solucionActual.getLongitud());
				solucionActual = solucionActual.mejorarSwap();
			}
			solucionActual = Solucion.recorridoGolosoAleatorizado(instancia, 0, cantAleatorias);
		}
	}
	
	public void start()
	{
		parar = false;
		if(t!=null)
		{
			t.interrupt();
		}
		t = new Thread(this);
		t.start();
	}
	
	public Solucion getMejorHastaAhora()
	{
		return mejorHastaAhora.clone();
	}
	
	public Solucion stop()
	{
		parar = true;
		t.interrupt();
		return getMejorHastaAhora();
	}
	
	public boolean stopped()
	{
		return parar;
	}
}
