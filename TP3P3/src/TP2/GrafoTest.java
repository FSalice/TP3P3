package TP2;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class GrafoTest
{
	// El grafo para todos los tests
	private Grafo _grafo;
	private Ciudad[] e;
	
	@Before
	public void inicializarGrafo()
	{
		_grafo = new Grafo();
		
		e = new Ciudad[6];
		for(int i = 0; i < e.length; i++){
			e[i] = new Ciudad(""+i, i, i);
			_grafo.agregarEstacion(e[i]);
		}
		_grafo.agregarArista(e[0], e[1],1);
		_grafo.agregarArista(e[1], e[2],1);
		_grafo.agregarArista(e[0], e[3],1);
		_grafo.agregarArista(e[2], e[3],1);
		_grafo.agregarArista(e[3], e[4],1);
	}
	
	@Test
	public void agregarAristaTest()
	{
		assertTrue( _grafo.existeArista(e[1], e[2]) );
		assertTrue( _grafo.existeArista(e[2], e[1]) );
		assertFalse( _grafo.existeArista(e[0], e[4]) );
		assertTrue( _grafo.existeArista(e[3], e[0]) );
		assertFalse( _grafo.existeArista(e[3], e[1]) );
	}
	
	@Test
	public void agregarAristaInvertidaTest()
	{
		assertTrue( _grafo.existeArista(e[1], e[2]) );
		assertTrue( _grafo.existeArista(e[2], e[1]) );
		assertFalse( _grafo.existeArista(e[0], e[4]) );
		assertTrue( _grafo.existeArista(e[3], e[0]) );
		assertFalse( _grafo.existeArista(e[3], e[1]) );
	}
	
	@Test
	public void eliminarAristaTest()
	{
		assertTrue( _grafo.existeArista(e[1], e[2]) );
		
		_grafo.eliminarArista(e[1], e[2]);
		
		assertFalse( _grafo.existeArista(e[2], e[1]) );
		assertTrue( _grafo.existeArista(e[2], e[3]));
		assertTrue( _grafo.existeArista(e[4], e[3]));
	}

	@Test
	public void vaciarGrafoTest()
	{
		_grafo.eliminarArista(e[3], e[4]);
		_grafo.eliminarArista(e[0], e[1]);
		_grafo.eliminarArista(e[1], e[2]);
		_grafo.eliminarArista(e[2], e[3]);
		_grafo.eliminarArista(e[0], e[3]);
		
		for(int i=0; i<5; ++i)
		for(int j=0; j<5; ++j) if( i != j )
			assertFalse( _grafo.existeArista(e[i], e[j]) );
	}
	
	@Test
	public void borrarAristaDosVecesTest()
	{
		assertTrue( _grafo.existeArista(e[3], e[0]) );

		_grafo.eliminarArista(e[3], e[0]);
		assertFalse( _grafo.existeArista(e[3], e[0]) );

		_grafo.eliminarArista(e[3], e[0]);
		assertFalse( _grafo.existeArista(e[3], e[0]) );
	}
	
	@Test
	public void agregarAristaDosVecesTest()
	{
		
		Grafo repetido = new Grafo();

		for(int i = 0; i < e.length; i++){
			repetido.agregarEstacion(e[i]);
		}
		repetido.agregarArista(e[0], e[1],1);
		repetido.agregarArista(e[2], e[1],1);
		repetido.agregarArista(e[3], e[0],1);
		repetido.agregarArista(e[1], e[0],1);
		repetido.agregarArista(e[2], e[3],1);
		repetido.agregarArista(e[3], e[4],1); 
		repetido.agregarArista(e[0], e[1],1);

		assertTrue( repetido.existeArista(e[1], e[0]) );
		assertTrue( repetido.existeArista(e[0], e[1]) );
		assertTrue( repetido.existeArista(e[3], e[0]) );
	}

	@Test(expected = IllegalArgumentException.class)
	public void verticeInexistenteTest()
	{
		_grafo.agregarArista(new Ciudad(null,1,2), e[3],1);
	}
	@Test(expected = IllegalArgumentException.class)
	
	public void aristaNegativaTest()
	{
		_grafo.agregarArista(e[2], e[3],-1);
	}
	
	@Test
	public void caminoMinimoPorPesoTest(){
		_grafo = grafoParaCaminos();
		for(int i = 0; i < 10; i++){
			assertEquals(_grafo.caminoMinimo(e[i], e[10], true),null);
			assertEquals(_grafo.caminoMinimo(e[i], e[10], false),null);
		}
		
		ArrayList<Integer> camino;
		int pesoCamino = 0;
		camino = _grafo.caminoMinimo(e[0], e[9], true); // deberia ser un camino de peso 5;
		
		for(int i = 0; i < camino.size()-1;i++){
			pesoCamino += _grafo.pesoArista(e[camino.get(i)], e[camino.get(i+1)]);
		}
		assertEquals(5, pesoCamino);
	}
	
	private Grafo grafoParaCaminos(){
			Grafo ret = new Grafo();
			e = new Ciudad[11];
			for(int i = 0; i < 10; i++){
				e[i] = new Ciudad("Estacion "+i, Math.random()*500, Math.random()*500);
			}
			e[10] = new Ciudad("Estacion 10", Math.random()*500, Math.random()*500);
			
			for(Ciudad e : e){
				ret.agregarEstacion(e);
			}
			
			for(int i = 0; i < 4; i++){
				ret.agregarArista(e[i*2], e[2+(i*2)], 25);
				ret.agregarArista(e[1+(i*2)], e[3+(i*2)], 25);
			}
			ret.agregarArista(e[0], e[3], 1);
			ret.agregarArista(e[3], e[4], 1);
			ret.agregarArista(e[4], e[7], 1);
			ret.agregarArista(e[7], e[8], 1);
			ret.agregarArista(e[8], e[9], 1);
			
			return ret;
		}
}
