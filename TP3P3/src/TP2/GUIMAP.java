package TP2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerCircle;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

import javax.swing.JCheckBox;

public class GUIMAP {

	private GUIMAP _this;
	private JFrame frame;
	private JPanel panelEditarCiudad;
	private JMapViewer mapa = new JMapViewer();
	private Grafo grafo = FileManager.cargarGrafo();
	private boolean agregandoCiudad, inicioArista, finArista;
	private int indiceInicioArista, indiceFinArista, contadorSeleccionados;
	private ArrayList<Coordinate> aristaAux;

	private List<MapMarker> ciudades = new ArrayList<MapMarker>();
	private List<MapPolygon> aristas = new ArrayList<MapPolygon>();
	private List<MapMarker> pesos = new ArrayList<MapMarker>();
	private ArrayList<Boolean> ciudadesSeleccionadas = new ArrayList<Boolean>();
	private ArrayList<Boolean> aristasSeleccionadas = new ArrayList<Boolean>();
	private JTextField nombreCiudad, editarNombreCiudad;
	private JButton btnNuevaCiudad;
	private JButton btnEliminar;
	private JLabel lblCiudades;
	private JToolBar barraDeEstado;
	private JLabel lblTramos;
	private JLabel lblLatitud;
	private JLabel lblLongitud;
	private JButton btnNuevoTramo;
	private JSpinner inputPeso;
	private JToolBar toolBar;
	private Timer timerMensaje;
	private final int radioTolerancia = 6;
	private final Color camino = Color.red;
	private final Color noSelec = Color.green;
	private final Color selec = Color.blue;
	private JPanel panelMensaje;
	private JLabel lblMensaje;
	private JPanel panelGrafico;
	private JPanel chart;
	private JFreeChart grafico;
	private JButton btnEditar;
	private ArrayList<Integer> solucionActual = new ArrayList<Integer>();
	private Solver solver;
	private JButton btnBLocal;
	private JButton btnStop;
	private JLabel lblActual;
	private XYSeries busquedaLocal;
	private XYSeries algoritmoGoloso;
	private XYSeries mejorSolucion;
	private MapPolygonImpl recorrido;
	private JButton btnContinuar;
	private JButton btnMostrar;
	private JCheckBox chckbxActualizarMapa;
	private boolean grande = false;
	private JButton btnO;
	private JButton btnX;
	
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					GUIMAP window = new GUIMAP();
					window.frame.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	public GUIMAP() 
	{
		initialize();
	}

	private void initialize() 
	{
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (Exception e1) 
		{
			e1.printStackTrace();
		}
		_this = this;
		frame = new JFrame();
		frame.setBounds(100, 100, 1081, 496);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() 
		{
			@Override
			public void windowClosing(WindowEvent e) 
			{
				cerrar();
			}
		});
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(mapa);

		toolBar = new JToolBar();
		toolBar.setBorder(null);
		toolBar.setFloatable(false);
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);

		btnNuevaCiudad = new JButton("Nueva Ciudad");
		btnNuevaCiudad.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		btnNuevaCiudad.setBorder(new BevelBorder(BevelBorder.RAISED,
				UIManager.getColor("Button.background"), UIManager
						.getColor("Button.background"), null, null));
		btnNuevaCiudad.setToolTipText("Agregar una nueva ciudad(Alt+E)");
		btnNuevaCiudad.setFocusable(false);
		toolBar.add(btnNuevaCiudad);

		nombreCiudad = new JTextField();
		nombreCiudad.setMaximumSize(new Dimension(600, 2384762));
		nombreCiudad.setMinimumSize(new Dimension(90, 123123));
		nombreCiudad.setBorder(new BevelBorder(BevelBorder.RAISED, UIManager
				.getColor("Button.background"), UIManager
				.getColor("Button.background"), null, null));
		nombreCiudad.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		nombreCiudad.setFocusable(false);
		toolBar.add(nombreCiudad);
		nombreCiudad.setColumns(6);

		editarNombreCiudad = new JTextField();
		editarNombreCiudad.setColumns(10);

		btnNuevoTramo = new JButton("Nuevo Tramo");
		btnNuevoTramo.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		btnNuevoTramo.setBorder(new BevelBorder(BevelBorder.RAISED, UIManager
				.getColor("Button.background"), UIManager
				.getColor("Button.background"), null, null));
		btnNuevoTramo.setFocusable(false);
		btnNuevoTramo
				.setToolTipText("Agregar un nuevo tramo entre dos ciudades(Click derecho sobre "
						+ "la ciudad inicial/Alt+T)");
		toolBar.add(btnNuevoTramo);

		inputPeso = new JSpinner();
		inputPeso.setMinimumSize(new Dimension(60, 123123));
		inputPeso.setMaximumSize(new Dimension(61, 2384762));
		inputPeso.setBorder(new BevelBorder(BevelBorder.RAISED, UIManager
				.getColor("Button.background"), UIManager
				.getColor("Button.background"), null, null));
		inputPeso.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		inputPeso.setRequestFocusEnabled(false);
		
		frame.addFocusListener(new FocusAdapter() 
		{
			@Override
			public void focusLost(FocusEvent e) 
			{
				frame.requestFocus();
			}
		});
		toolBar.add(inputPeso);
		inputPeso.setModel(new SpinnerNumberModel(new Integer(0),
				new Integer(0), null, new Integer(1)));
		
		
		btnEliminar = new JButton("Eliminar");
		btnEliminar.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		btnEliminar.setBorder(new BevelBorder(BevelBorder.RAISED, UIManager
				.getColor("Button.background"), UIManager
				.getColor("Button.background"), null, null));
		btnEliminar.setFocusable(false);
		btnEliminar.setToolTipText("Eliminar todos los elementos seleccionados(Supr/Delete)");
		toolBar.add(btnEliminar);
		btnEliminar.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				eliminar();
			}
		});

		btnEditar = new JButton("Editar");
		btnEditar.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		btnEditar.setBorder(new BevelBorder(BevelBorder.RAISED, UIManager
				.getColor("Button.background"), UIManager
				.getColor("Button.background"), null, null));
		btnEditar.setFocusable(false);
		toolBar.add(btnEditar);
		
		JButton btnAGoloso = new JButton("A. Goloso");
		btnAGoloso.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				solucionActual = Solucion.recorridoGoloso(grafo).getRecorridoList();
				mostrarSolucion();
			}
		});
		btnAGoloso.setFocusable(false);
		btnAGoloso.setBorder(new BevelBorder(BevelBorder.RAISED, UIManager
						.getColor("Button.background"), UIManager
						.getColor("Button.background"), null, null));
		btnAGoloso.setAlignmentY(1.0f);
		toolBar.add(btnAGoloso);
		
		btnBLocal = new JButton("B. Local");
		btnBLocal.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{	
				resetSolver();
				solver.start();
			}
		});
		btnBLocal.setFocusable(false);
		btnBLocal.setBorder(new BevelBorder(BevelBorder.RAISED, UIManager
								.getColor("Button.background"), UIManager
								.getColor("Button.background"), null, null));
		btnBLocal.setAlignmentY(1.0f);
		toolBar.add(btnBLocal);

		btnEditar.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				editar();
			}
		});
		btnNuevoTramo.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				nuevoTramo();
			}
		});
		btnNuevaCiudad.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				nuevaCiudad();
			}
		});
		mapa.addMouseMotionListener(new MouseAdapter() 
		{
			@Override
			public void mouseMoved(MouseEvent e) 
			{
				actualizarEstado(mapa.getPosition(e.getPoint()));
			}
		});
		mapa.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mousePressed(MouseEvent e) 
			{
				Point pos = e.getPoint();
				MapMarker ciudadCercana = buscarCiudadCercana(pos);
				if (!inicioArista && !finArista && !agregandoCiudad) 
				{
					if (e.getButton() == MouseEvent.BUTTON1) 
					{
						if (!seleccionarCiudad(pos)) seleccionarArista(pos);	
						btnEditar.setEnabled(contadorSeleccionados == 1);
					}
					
					if (e.getButton() == MouseEvent.BUTTON3) 
					{
						if (ciudadCercana != null) 
							nuevoTramo();
						else if (e.getClickCount() == 2) 
							nuevaCiudad();
					}
				}
				if (agregandoCiudad) 
				{
					agregarCiudad(mapa.getPosition(pos));
				}
				if (inicioArista) 
				{
					if (ciudadCercana != null) 
					{
						indiceInicioArista = ciudades.indexOf(ciudadCercana);
						aristaAux = new ArrayList<Coordinate>();
						aristaAux.add(new Coordinate(ciudadCercana.getLat(), ciudadCercana.getLon()));
						aristaAux.add(new Coordinate(ciudadCercana.getLat(), ciudadCercana.getLon()));
						aristaAux.add(new Coordinate(ciudadCercana.getLat(), ciudadCercana.getLon()));
						agregarArista(aristaAux);
						aristasSeleccionadas.add(false);
						inicioArista = false;
						finArista = true;
					}
				} 
				else if (finArista && ciudadCercana != null) 
					{
						indiceFinArista = ciudades.indexOf(ciudadCercana);
						if (indiceInicioArista != indiceFinArista) 
						{
							if (grafo.agregarArista(indiceInicioArista, indiceFinArista, (Integer) inputPeso.getValue())) 
							{
								aristaAux.set(1, new Coordinate(ciudades.get(indiceFinArista).getLat(),
										ciudades.get(indiceFinArista).getLon()));
								agregarPeso(aristaAux,
										(Integer) inputPeso.getValue());
								aristaAux = null;
								finArista = false;
								btnNuevoTramo.setText("Nuevo Tramo");
							}
							else mostrarMensaje("La arista ya existía, pero   puede ser editada. Use el    "
										+ "boton \"Editar\"    ");
						}
						else mostrarMensaje("Se ha intentado colocar una arista desde "
									+ "un punto hasta el mismo punto.    ");
					}
				actualizarEstado();
			}
		});
		mapa.setFocusable(false);

		panelMensaje = new JPanel();
		panelMensaje.setBounds(50, 11, 256, 51);
		panelMensaje.setBorder(new BevelBorder(BevelBorder.RAISED, null, null,
				null, null));
		panelMensaje.setVisible(false);
		mapa.setLayout(null);
		mapa.add(panelMensaje);
		panelMensaje.setLayout(null);
		

		grafico = org.jfree.chart.ChartFactory.createXYLineChart("Búsqueda Local", "iteración", "longitud", createDataset(), PlotOrientation.VERTICAL, true, true, false);
		
		
		XYPlot plot = grafico.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		plot.setRenderer(renderer);
		mapa.setLayout(null);
		
		
		
		chart = new ChartPanel(grafico);
		chart.setBounds(2, 20, 252, 156);
		chart.setBorder(null);
		chart.setVisible(true);
		
		
		panelGrafico = new JPanel();
		panelGrafico.setBounds(50, 73, 256, 225);
		panelGrafico.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		panelGrafico.setVisible(false);
		mapa.setLayout(null);
		mapa.add(panelGrafico);
		panelGrafico.setLayout(null);
		panelGrafico.add(chart);
		
		btnStop = new JButton("Stop");
		btnStop.setBounds(1, 175, 36, 29);
		panelGrafico.add(btnStop);
		btnStop.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				solucionActual = solver.stop().getRecorridoList();
				mostrarSolucion();
			}
		});
		
		btnStop.setFocusable(false);
		btnStop.setBorder(new BevelBorder(BevelBorder.RAISED, UIManager
										.getColor("Button.background"), UIManager
										.getColor("Button.background"), null, null));
		btnStop.setAlignmentY(1.0f);
		
		btnContinuar = new JButton("Continuar");
		btnContinuar.setFocusable(false);
		btnContinuar.setBorder(new BevelBorder(BevelBorder.RAISED, UIManager
										.getColor("Button.background"), UIManager
										.getColor("Button.background"), null, null));
		btnContinuar.setAlignmentY(1.0f);
		btnContinuar.setBounds(36, 175, 59, 29);
		panelGrafico.add(btnContinuar);
		
		btnMostrar = new JButton("Mostrar");
		btnMostrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				solucionActual = solver.getMejorHastaAhora().getRecorridoList();
				mostrarSolucion();
			}
		});
		btnMostrar.setFocusable(false);
		btnMostrar.setBorder(new BevelBorder(BevelBorder.RAISED, UIManager
												.getColor("Button.background"), UIManager
												.getColor("Button.background"), null, null));
		btnMostrar.setAlignmentY(1.0f);
		btnMostrar.setBounds(93, 175, 52, 29);
		panelGrafico.add(btnMostrar);
		
		chckbxActualizarMapa = new JCheckBox("Actualizar Mapa");
		chckbxActualizarMapa.setSelected(true);
		chckbxActualizarMapa.setBounds(4, 205, 119, 18);
		panelGrafico.add(chckbxActualizarMapa);
		
		btnX = new JButton("");
		btnX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panelGrafico.setVisible(false);
			}
		});
		btnX.setBounds(223, 2, 29, 17);
		btnX.setIcon(UIManager.getIcon("InternalFrame.closeIcon"));
		panelGrafico.add(btnX);
		
		btnO = new JButton("");
		btnO.setIcon(UIManager.getIcon("InternalFrame.maximizeIcon"));
		btnO.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				grande = !grande;
				if(grande){
					frame.setExtendedState(Frame.MAXIMIZED_BOTH);
					panelGrafico.setBounds(300, 23, 856, 641);
					chart.setBounds(2, 20, 852, 556);
					btnStop.setBounds(2, 588, 36, 29);
					btnContinuar.setBounds(37, 588, 59, 29);
					btnMostrar.setBounds(95, 588, 52, 29);
					chckbxActualizarMapa.setBounds(5, 618, 119, 18);
					btnO.setBounds(794, 2, 29, 17);
					btnX.setBounds(823, 2, 29, 17);
					btnO.setIcon(UIManager.getIcon("InternalFrame.minimizeIcon"));
				}else{
					panelGrafico.setBounds(50, 73, 256, 225);
					chart.setBounds(2, 20, 252, 156);
					btnStop.setBounds(1, 175, 36, 29);
					btnContinuar.setBounds(36, 175, 59, 29);
					btnMostrar.setBounds(95, 175, 52, 29);
					chckbxActualizarMapa.setBounds(4, 205, 119, 18);
					btnO.setBounds(194, 2, 29, 17);
					btnX.setBounds(223, 2, 29, 17);
					btnO.setIcon(UIManager.getIcon("InternalFrame.maximizeIcon"));
				}
			}
		});
		btnO.setBounds(194, 2, 29, 17);
		panelGrafico.add(btnO);
		
		
		btnContinuar.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				if(solver.stopped())
					solver.start();
			}
		});
		
		grafico.setBackgroundPaint(UIManager.getColor("Button.background"));
		
		
		JLabel lblIconoInfo = new JLabel("");
		lblIconoInfo.setBounds(7, 8, 32, 32);
		lblIconoInfo.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
		panelMensaje.add(lblIconoInfo);

		lblMensaje = new JLabel("sin mensaje");
		lblMensaje.setBounds(46, 4, 178, 42);
		panelMensaje.add(lblMensaje);

		barraDeEstado = new JToolBar();
		barraDeEstado.setFloatable(false);
		frame.getContentPane().add(barraDeEstado, BorderLayout.SOUTH);
		
		lblActual = new JLabel("Actual: 0");
		barraDeEstado.add(lblActual);

		lblCiudades = new JLabel("Ciudades: 0");
		barraDeEstado.add(lblCiudades);

		lblTramos = new JLabel("    Tramos: 0");
		barraDeEstado.add(lblTramos);

		lblLatitud = new JLabel("    Latitud:");
		barraDeEstado.add(lblLatitud);

		lblLongitud = new JLabel("    Longitud:");
		barraDeEstado.add(lblLongitud);

		panelEditarCiudad = new JPanel();
		panelEditarCiudad.add(new JLabel("Nombre: "));
		panelEditarCiudad.add(editarNombreCiudad);
		panelEditarCiudad.add(new JLabel("Linea: "));

		mapa.setZoom(4, new Point(2, 2));
		frame.setFocusable(true);
		frame.addKeyListener(new KeyListenerGlobal());

		timerMensaje = new Timer(1000 * 5, new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				panelMensaje.setVisible(false);
			}
		});
		timerMensaje.setRepeats(false);
		
		actualizarMapa();
	}
	
	//Agrega una ciudad en la ubicacion especificada
	private void agregarCiudad(Coordinate pos) 
	{	
		// si esta vacio le pedimos que escriba el nombre
		if (nombreCiudad.getText().trim().isEmpty())
			nombreCiudad.setText(JOptionPane
					.showInputDialog("Ingrese el nombre de la ciudad: "));

		// si sigue vacio no hacemos nada
		if (!nombreCiudad.getText().trim().isEmpty())
		{
			grafo.agregarCiudad(new Ciudad(nombreCiudad.getText(),
					pos.getLat(), pos.getLon()));
			
			agregarMarker(nombreCiudad.getText(),pos.getLat(),pos.getLon());

			nuevaCiudad();
			nombreCiudad.setText("");

			ciudadesSeleccionadas.add(false);
		}
		actualizarEstado();
		
		resetSolver();
	}

	private class KeyListenerGlobal extends KeyAdapter 
	{
		@Override
		public void keyPressed(KeyEvent e) 
		{
			char c = e.getKeyChar();
			if ((c >= KeyEvent.VK_A && c <= KeyEvent.VK_Z)
					|| (c >= KeyEvent.VK_A + 32 && c <= KeyEvent.VK_Z + 32)
					|| (c >= 48 && c < 57) || c == KeyEvent.VK_SPACE)
				nombreCiudad.setText(nombreCiudad.getText() + c);

			String actual = nombreCiudad.getText();
			if (c == KeyEvent.VK_BACK_SPACE && actual.length() > 0)
				nombreCiudad.setText(actual.substring(0, actual.length() - 1));
			
			int valorInput = (int) inputPeso.getValue();
			switch (e.getKeyCode()) 
			{
			case KeyEvent.VK_ENTER:
				nuevaCiudad();
				break;
			case KeyEvent.VK_UP:
				inputPeso.setValue(valorInput + 1);
				break;
			case KeyEvent.VK_DOWN:
				if (valorInput > 0)
					inputPeso.setValue(valorInput - 1);
				break;
			case KeyEvent.VK_DELETE:
				eliminar();
				break;
			}
		}
	}
	
	//Devuelve la ciudad mas cercana al punto dado
	private MapMarker buscarCiudadCercana(Point pos)
	{
		MapMarker ret = null;
		Point posicionEnMapa;
		for (MapMarker i : ciudades) 
		{
			posicionEnMapa = mapa.getMapPosition(i.getCoordinate(), false);
			if (posicionEnMapa != null
					&& Math.abs(pos.getY() - posicionEnMapa.getY()) < radioTolerancia
					&& Math.abs(pos.getX() - posicionEnMapa.getX()) < radioTolerancia) 
			{
				ret = i;
			}
		}
		return ret;
	}
	
	//Actualiza la barra de estado y los graficos del mapa
	private void actualizarEstado(Coordinate pos)
	{
		if (finArista)
			aristaAux.set(1, new Coordinate(pos.getLat(), pos.getLon()));
		lblLatitud.setText("    Latitud: " + pos.getLat());
		lblLongitud.setText("    Longitud: " + pos.getLon());
		actualizarEstado();
	}
	private void actualizarEstado() 
	{
		lblCiudades.setText("Ciudades: " + ciudades.size());
		lblTramos.setText("   Tramo: " + grafo.getAristas());
		mapa.repaint();
	}

	//Marca/Desmarca la ciudad como seleccionada en el mapa
	private boolean seleccionarCiudad(Point pos) 
	{
		MapMarker aux = buscarCiudadCercana(pos);
		if (aux != null) 
		{
			int index = ciudades.indexOf(aux);
			ciudadesSeleccionadas.set(index,!ciudadesSeleccionadas.get(index));
			aux.getStyle().setBackColor(ciudadesSeleccionadas.get(index) ? selec : noSelec);
			contadorSeleccionados += ciudadesSeleccionadas.get(index) ? 1 : -1;
		}
		return aux != null;
	}
	//Marca/Desmarca la arista como seleccionada en el mapa
	private void seleccionarArista(Point pos) 
	{
		int indice = -1;
		for (int i = 0; i < aristas.size(); i++) if (aristaContiene(pos, aristas.get(i))) 
			indice = i;
		
		if (indice != -1) seleccionarArista(indice);
	}
	private void seleccionarArista(int i)
	{
		aristasSeleccionadas.set(i, !aristasSeleccionadas.get(i));
		aristas.get(i).getStyle().setColor(aristasSeleccionadas.get(i) ? selec : noSelec);
		contadorSeleccionados += aristasSeleccionadas.get(i) ? 1 : -1;
	}
	//Devuelve true si y solo si el punto pos esta a menos de 5 pixeles de algun punto de la linea
	private boolean aristaContiene(Point pos, MapPolygon arista)
	{
		Point inicio = mapa.getMapPosition((Coordinate) arista.getPoints()
				.get(0), false);
		Point fin = mapa.getMapPosition((Coordinate) arista.getPoints()
				.get(1), false);
		return aristaContiene(pos, inicio, fin);
	}
	private boolean aristaContiene(Point pos, Point inicio, Point fin) 
	{
		Line2D.Double arista = new Line2D.Double(inicio, fin);
		return arista.ptSegDist(pos)<radioTolerancia;
	}
	
	//Muestra el mensaje en el panel de mensajes y lo hace visible por unos segundos
	private void mostrarMensaje(String mensaje) 
	{
		mensaje = ajustarString(mensaje);
		lblMensaje.setText(mensaje);
		panelMensaje.setVisible(true);
		timerMensaje.start();
	}
	private String ajustarString(String mensaje) 
	{
		mensaje = "<html>" + mensaje + "</html>";
		for (int i = 30; i < mensaje.length() - 30; i = i + 30) 
		{
			mensaje = mensaje.substring(0, i + (4 * (int) (i / 30)))
					+ "<br>"
					+ mensaje.substring(i + (4 * (int) (i / 30)),
							mensaje.length());
		}
		return mensaje;
	}

	//Actualiza el estado del grafo en el mapa
	private void actualizarMapa() 
	{	
		mapa.getMapMarkerList().clear();
		mapa.getMapPolygonList().clear();
		ArrayList<Ciudad> est = grafo.getCiudades();
		for (int i = 0; i < est.size(); i++) 
		{
			Ciudad ciudad_1 = est.get(i);
			agregarMarker(ciudad_1);
			for (int j = i + 1; j < est.size(); j++) 
			{
				Ciudad ciudad_2 = est.get(j);
				if (ciudad_1.conectado(ciudad_2)) agregarArista(ciudad_1, ciudad_2);
			}
		}
		actualizarEstado();
	}
	
	//Agrega una linea(poligono) entre los puntos dados
	private void agregarArista(Ciudad ciudad_1, Ciudad ciudad_2) 
	{
		ArrayList<Coordinate> puntos = new ArrayList<Coordinate>();
		puntos.add(new Coordinate(ciudad_2.getLat(), ciudad_2.getLon()));
		puntos.add(new Coordinate(ciudad_1.getLat(), ciudad_1.getLon()));
		puntos.add(new Coordinate(ciudad_2.getLat(), ciudad_2.getLon()));
		agregarArista(puntos, ciudad_1.pesoArista(ciudad_2));
	}
	private void agregarArista(ArrayList<Coordinate> puntos, int peso) 
	{
		agregarArista(puntos);
		agregarPeso(puntos, peso);
	}
	private void agregarArista(ArrayList<Coordinate> puntos) 
	{
		MapPolygonImpl pol = new MapPolygonImpl(puntos);
		aristas.add(pol);
		mapa.addMapPolygon(pol);
		pol.setColor(noSelec);
		aristasSeleccionadas.add(false);
	}
	//Elimina una linea(poligono)
	private void eliminarArista(int i) 
	{
		mapa.removeMapPolygon(aristas.remove(i));
		mapa.removeMapMarker(pesos.remove(i));
	}

	//Agrega/Elimina un marker de la lista del mapa y de la lista "ciudades
	private void agregarMarker(String nombre, double lat, double lon) 
	{	
		MapMarkerDot nuevoDot = new MapMarkerDot(nombre, new Coordinate(lat, lon));
		agregarMarker(nuevoDot);
	}
	private void agregarMarker(Ciudad c) 
	{	
		MapMarkerDot nuevoDot = new MapMarkerDot(c.getNombre(), new Coordinate(c.getLat(), c.getLon()));
		agregarMarker(nuevoDot);
	}
	private void agregarMarker(MapMarkerDot nuevoDot) 
	{
		nuevoDot.getStyle().setBackColor(noSelec);
		ciudades.add(nuevoDot);
		mapa.addMapMarker(nuevoDot);
		ciudadesSeleccionadas.add(false);
	}
	private void eliminarMarker(int i) 
	{
		mapa.removeMapMarker(ciudades.remove(i));
	}	

	//Agrega un marker sin imagen, pero que muestra el peso en medio de la arista
	private void agregarPeso(ArrayList<Coordinate> pos, int peso) 
	{
		double latMax = Math.max(pos.get(0).getLat(), pos.get(1).getLat());
		double latMin = Math.min(pos.get(0).getLat(), pos.get(1).getLat());
		double lonMax = Math.max(pos.get(0).getLon(), pos.get(1).getLon());
		double lonMin = Math.min(pos.get(0).getLon(), pos.get(1).getLon());

		double lat = latMin + ((latMax - latMin) / 2);
		double lon = lonMin + ((lonMax - lonMin) / 2);

		pesos.add(new MapMarkerCircle("" + peso, new Coordinate(lat, lon), 0));
		mapa.addMapMarker(pesos.get(pesos.size() - 1));
	}
	//Modifica el marker en la lista del mapa y en la lista "pesos"
	private void editarPeso(int i, int nuevoPeso) 
	{
		MapMarker nuevo = new MapMarkerCircle("" + nuevoPeso, pesos.get(i)
				.getCoordinate(), 0);
		mapa.getMapMarkerList().set(
				mapa.getMapMarkerList().indexOf(pesos.get(i)), nuevo);
		pesos.set(i, nuevo);
	}


	//Elimina todas las ciudades seleccionadas y las aristas afectadas o seleccionadas
	private void eliminar() 
	{
		for (int i = ciudades.size() - 1; i >= 0; i--) if (ciudadesSeleccionadas.get(i)) 
			{
				for (int j = aristas.size() - 1; j >= 0; j--) if (esAristaAfectada(ciudades.get(i), aristas.get(j))) 
					{
						if (!aristasSeleccionadas.get(j))
							contadorSeleccionados++;
						aristasSeleccionadas.set(j, true);
					}
				ciudadesSeleccionadas.remove(i);
				eliminarMarker(i);
				grafo.eliminarCiudad(i);
			}
		
		for (int i = aristas.size() - 1; i >= 0; i--) if (aristasSeleccionadas.get(i)) 
			{
				aristasSeleccionadas.remove(i);
				grafo.eliminarAristas(
						aristas.get(i).getPoints().get(0),
						aristas.get(i).getPoints().get(1));
				eliminarArista(i);
			}
		actualizarEstado();
		mostrarMensaje(contadorSeleccionados
				+ " objetos han sido eliminados.");
		contadorSeleccionados = 0;

		resetSolver();
	}
	//Devuelve true si la ciudad es parte de la arista
	private boolean esAristaAfectada(MapMarker ciudad, MapPolygon arista) 
	{
				//la ciudad es el inicio de la arista
		return  (arista.getPoints().get(0).getLat() == ciudad.getCoordinate().getLat() &&
				 arista.getPoints().get(0).getLon() == ciudad.getCoordinate().getLon())|| 
				//la ciudad es el fin de la arista
				(arista.getPoints().get(1).getLat() == ciudad.getCoordinate().getLat() && 
				 arista.getPoints().get(1).getLon() == ciudad.getCoordinate().getLon());
	}
	
	//Verifica que haya un elemento para editar e invoca el cuadro de dialogo correspondiente
	private void editar() 
	{	
		if(contadorSeleccionados==1)
		{
			for (int i = 0; i < aristas.size(); i++) if (aristasSeleccionadas.get(i))
					editarArista(i);
			
			for (int i = 0; i < ciudades.size(); i++) if (ciudadesSeleccionadas.get(i)) 
					editarCiudad(i);
			
			contadorSeleccionados = 0;
			actualizarEstado();
		}
	}
	//Invoca un cuadro de dialogo para editar la ciudad
	private void editarCiudad(int i) 
	{
		int eleccion = JOptionPane.showConfirmDialog(null,
				panelEditarCiudad, "Editar ciudad",
				JOptionPane.OK_CANCEL_OPTION);
		if (eleccion == JOptionPane.OK_OPTION) 
		{
			grafo.editarCiudad(i,
					editarNombreCiudad.getText());
			MapMarker anterior = ciudades.get(i);
			MapMarker nueva = new MapMarkerDot(
					editarNombreCiudad.getText(),
					new Coordinate(ciudades.get(i).getLat(),
							ciudades.get(i).getLon()));
			ciudades.set(i, nueva);

			mapa.getMapMarkerList().set(
					mapa.getMapMarkerList().indexOf(anterior),
					nueva);
			ciudades.get(i).getStyle().setBackColor(noSelec);
			editarNombreCiudad.setText("");
			ciudadesSeleccionadas.set(i, false);
		}
	}
	//Invoca un cuadro de dialogo para editar la arista
	private void editarArista(int i) 
	{
		try 
		{
			int nuevoPeso = Integer.parseInt(JOptionPane
					.showInputDialog(null,
							"Ingrese el Peso del tramo: ",
							"Editar tramo",
							JOptionPane.PLAIN_MESSAGE));
			if (nuevoPeso < 0)
				throw new NumberFormatException();
			grafo.editarArista(aristas.get(i).getPoints()
					.get(0), aristas.get(i).getPoints().get(1),
					nuevoPeso);
			editarPeso(i, nuevoPeso);
			aristasSeleccionadas.set(i, false);
			aristas.get(i).getStyle().setColor(noSelec);
		}
		catch (NumberFormatException ex) 
		{
			mostrarMensaje("El peso ingresado es invalido!");
		}
	}

	//Inicia o cancela el proceso de agregar una ciudad/arista
	private void nuevoTramo() 
	{
		if (btnNuevoTramo.getText().equals("Nuevo Tramo")) 
		{
			inicioArista = true;
			btnNuevoTramo.setText("    Cancelar    ");
		} 
		else 
		{
			inicioArista = false;
			btnNuevoTramo.setText("Nuevo Tramo");
			if (finArista) 
			{
				mapa.getMapPolygonList().remove(mapa.getMapPolygonList().size() - 1);
				actualizarEstado();
			}
		}
		finArista = false;
	}
	private void nuevaCiudad() 
	{
		agregandoCiudad = !agregandoCiudad;
		btnNuevaCiudad.setText(agregandoCiudad? "      Cancelar     ":"Nueva Ciudad");
	}
	
	//Serializa el grafo y lo guarda en un archivo
	private void cerrar() 
	{
		FileManager.guardarGrafo(grafo);
		if(solver!=null)
			solver.stop();
		frame.dispose();
	}

	public void setAvance(int iteracion, double y)
	{
		panelGrafico.setIgnoreRepaint(true);
		double goloso = Solucion.recorridoGoloso(grafo).getLongitud();
		
		algoritmoGoloso.add(0, goloso);
		algoritmoGoloso.add(iteracion, goloso);
		
		double minimo = Math.min(goloso, busquedaLocal.getMinY());
		
		mejorSolucion.add(0, minimo);
		mejorSolucion.add(iteracion, minimo);
		
		busquedaLocal.add(iteracion, y);
		
		panelGrafico.setVisible(true);
		panelGrafico.setIgnoreRepaint(false);
		
		if(chckbxActualizarMapa.isSelected())
		{
			solucionActual = solver.getMejorHastaAhora().getRecorridoList();
			mostrarSolucion();
		}
	}
	private void clearDataset()
	{
		algoritmoGoloso.clear();
		busquedaLocal.clear();
		mejorSolucion.clear();
	}
	private XYDataset createDataset()
	   {
		algoritmoGoloso = new XYSeries("A. Goloso");
		busquedaLocal = new XYSeries("B. Local");
		mejorSolucion = new XYSeries("Mejor Recorrido");
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(busquedaLocal);
		dataset.addSeries(algoritmoGoloso);
		dataset.addSeries(mejorSolucion);
		return dataset;
	   }

	private void mostrarSolucion() {
		mapa.removeMapPolygon(recorrido);
		List<Coordinate> lista = new ArrayList<Coordinate>();
		for(Integer i : solucionActual)
			lista.add(new Coordinate(grafo.get(i).getLat(),grafo.get(i).getLon()));
		for(int i = solucionActual.size()-2; i>=0; i--)
			lista.add(new Coordinate(grafo.get(solucionActual.get(i)).getLat(),grafo.get(solucionActual.get(i)).getLon()));
		recorrido = new MapPolygonImpl(lista);
		recorrido.setColor(camino);
		mapa.addMapPolygon(recorrido);
	}

	private void resetSolver() {
		if(solver != null)
			solver.stop();
		solver = new Solver(grafo, 100, 5, _this);
		clearDataset();
	}
}
