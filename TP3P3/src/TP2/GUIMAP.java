package TP2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Polygon;
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

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerCircle;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

import javax.swing.border.BevelBorder;
import java.awt.Dimension;
import javax.swing.JProgressBar;

public class GUIMAP {

	private GUIMAP gui;
	private JFrame frame;
	private JPanel panelEditarEstacion;
	private JMapViewer mapa = new JMapViewer();
	private Grafo grafo = FileManager.cargarGrafo();
	private boolean agregandoEstacion, inicioArista, finArista;
	private int indiceInicioArista, indiceFinArista, contadorSeleccionados;
	private ArrayList<Coordinate> aristaAux;

	private List<MapMarker> estaciones = new ArrayList<MapMarker>();
	private List<MapPolygon> aristas = new ArrayList<MapPolygon>();
	private List<MapMarker> pesos = new ArrayList<MapMarker>();
	private ArrayList<Boolean> estacionesSeleccionadas = new ArrayList<Boolean>();
	private ArrayList<Boolean> aristasSeleccionadas = new ArrayList<Boolean>();
	private JTextField nombreEstacion, editarNombreEstacion;
	private JButton btnNuevaEstacion;
	private JButton btnEliminar;
	private JLabel lblEstaciones;
	private JToolBar barraDeEstado;
	private JLabel lblTramos;
	private JLabel lblLatitud;
	private JLabel lblLongitud;
	private JButton btnNuevoTramo;
	private JSpinner inputPeso;
	private JToolBar toolBar;
	private Timer t;
	private final int radioTolerancia = 6;
	private final Color camino = Color.red;
	private final Color noSelec = Color.green;
	private final Color selec = Color.blue;
	private JLabel lblMensaje;
	private JPanel panelMensaje;
	private ArrayList<Integer> caminoMinimoInt;
	private MapPolygonImpl caminoMinimo;
	private JButton btnCaminoMinimoPorTrasbordos;
	private JButton btnCaminoMinimoPorPeso;
	private JProgressBar progressBar;
	private Worker worker;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIMAP window = new GUIMAP();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public GUIMAP() {
		gui = this;
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1081, 496);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				FileManager.guardarGrafo(grafo);
				frame.dispose();
			}
		});
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(mapa);

		mapa.getMapMarkerList().clear();
		for (MapMarker m : estaciones)
			mapa.addMapMarker(m);
		mapa.getMapPolygonList().clear();
		for (MapPolygon p : aristas)
			mapa.addMapPolygon(p);

		toolBar = new JToolBar();
		toolBar.setBorder(null);
		toolBar.setFloatable(false);
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);

		btnNuevaEstacion = new JButton("Nueva Estacion");
		btnNuevaEstacion.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		btnNuevaEstacion.setBorder(new BevelBorder(BevelBorder.RAISED,
				UIManager.getColor("Button.background"), UIManager
						.getColor("Button.background"), null, null));
		btnNuevaEstacion.setToolTipText("Agregar una nueva estacion(Alt+E)");
		btnNuevaEstacion.setFocusable(false);
		toolBar.add(btnNuevaEstacion);

		nombreEstacion = new JTextField();
		nombreEstacion.setMaximumSize(new Dimension(600, 2384762));
		nombreEstacion.setMinimumSize(new Dimension(90, 123123));
		nombreEstacion.setBorder(new BevelBorder(BevelBorder.RAISED, UIManager
				.getColor("Button.background"), UIManager
				.getColor("Button.background"), null, null));
		nombreEstacion.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		nombreEstacion.setFocusable(false);
		toolBar.add(nombreEstacion);
		nombreEstacion.setColumns(6);

		editarNombreEstacion = new JTextField();
		editarNombreEstacion.setColumns(10);

		btnNuevoTramo = new JButton("Nuevo Tramo");
		btnNuevoTramo.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		btnNuevoTramo.setBorder(new BevelBorder(BevelBorder.RAISED, UIManager
				.getColor("Button.background"), UIManager
				.getColor("Button.background"), null, null));
		btnNuevoTramo.setFocusable(false);
		btnNuevoTramo
				.setToolTipText("Agregar un nuevo tramo entre dos estaciones(Click derecho sobre "
						+ "la estacion inicial/Alt+T)");
		toolBar.add(btnNuevoTramo);

		inputPeso = new JSpinner();
		inputPeso.setMinimumSize(new Dimension(60, 123123));
		inputPeso.setMaximumSize(new Dimension(61, 2384762));
		inputPeso.setBorder(new BevelBorder(BevelBorder.RAISED, UIManager
				.getColor("Button.background"), UIManager
				.getColor("Button.background"), null, null));
		inputPeso.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		inputPeso.setRequestFocusEnabled(false);
		inputPeso.setFocusTraversalKeysEnabled(false);
		for (Component i : inputPeso.getEditor().getComponents())
			i.setFocusable(false);
		inputPeso.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
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
		btnEliminar
				.setToolTipText("Eliminar todos los elementos seleccionados(Supr/Delete)");
		toolBar.add(btnEliminar);
		btnEliminar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = estaciones.size() - 1; i >= 0; i--) {
					if (estacionesSeleccionadas.get(i)) {
						estacionesSeleccionadas.remove(i);
						for (int j = aristas.size() - 1; j >= 0; j--) {

							if ((aristas.get(j).getPoints().get(0).getLat() == estaciones
									.get(i).getCoordinate().getLat() && aristas
									.get(j).getPoints().get(0).getLon() == estaciones
									.get(i).getCoordinate().getLon())
									|| (aristas.get(j).getPoints().get(1)
											.getLat() == estaciones.get(i)
											.getCoordinate().getLat() && aristas
											.get(j).getPoints().get(1).getLon() == estaciones
											.get(i).getCoordinate().getLon())) {
								if (!aristasSeleccionadas.get(j))
									contadorSeleccionados++;
								aristasSeleccionadas.set(j, true);
							}
						}
						eliminarMarker(i);

						grafo.eliminarEstacion(i);
					}
				}
				for (int i = aristas.size() - 1; i >= 0; i--) {
					if (aristasSeleccionadas.get(i)) {
						aristasSeleccionadas.remove(i);
						grafo.eliminarAristas(
								aristas.get(i).getPoints().get(0),
								aristas.get(i).getPoints().get(1));
						eliminarArista(i);
					}
				}
				actualizarEstado();
				btnCaminoMinimoPorPeso.getActionListeners()[0]
						.actionPerformed(null);
				mostrarMensaje(contadorSeleccionados
						+ " objetos han sido eliminados.");
				contadorSeleccionados = 0;
			}
		});

		JButton btnEditar = new JButton("Editar");
		btnEditar.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		btnEditar.setBorder(new BevelBorder(BevelBorder.RAISED, UIManager
				.getColor("Button.background"), UIManager
				.getColor("Button.background"), null, null));
		btnEditar.setFocusable(false);
		toolBar.add(btnEditar);

		btnCaminoMinimoPorPeso = new JButton("Camino de menor costo");
		btnCaminoMinimoPorPeso.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		btnCaminoMinimoPorPeso.setBorder(new BevelBorder(BevelBorder.RAISED,
				UIManager.getColor("Button.background"), UIManager
						.getColor("Button.background"), null, null));
		btnCaminoMinimoPorPeso.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				caminoMinimo(true);
			}
		});
		toolBar.add(btnCaminoMinimoPorPeso);
		btnCaminoMinimoPorPeso
				.setToolTipText("Muestra un ejemplo de un camino optimo considerando el costo del viaje (F1)");
		btnCaminoMinimoPorPeso.setFocusable(false);

		btnCaminoMinimoPorTrasbordos = new JButton("Camino de menos trasbordos");
		btnCaminoMinimoPorTrasbordos.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		btnCaminoMinimoPorTrasbordos.setBorder(new BevelBorder(
				BevelBorder.RAISED, UIManager.getColor("Button.background"),
				UIManager.getColor("Button.background"), null, null));
		btnCaminoMinimoPorTrasbordos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				caminoMinimo(false);
			}
		});
		toolBar.add(btnCaminoMinimoPorTrasbordos);
		btnCaminoMinimoPorTrasbordos
				.setToolTipText("Muestra un ejemplo de un camino optimo considerando la cantidad de trasbordos (F2)");
		btnCaminoMinimoPorTrasbordos.setFocusable(false);

		btnEditar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < aristas.size(); i++) {
					if (aristasSeleccionadas.get(i)) {
						try {
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
						} catch (NumberFormatException ex) {
							mostrarMensaje("El peso ingresado es invalido!");
						}
					}
				}
				for (int i = 0; i < estaciones.size(); i++) {
					if (estacionesSeleccionadas.get(i)) {
						int eleccion = JOptionPane.showConfirmDialog(null,
								panelEditarEstacion, "Editar estacion",
								JOptionPane.OK_CANCEL_OPTION);
						if (eleccion == JOptionPane.OK_OPTION) {
							grafo.editarEstacion(i,
									editarNombreEstacion.getText());
							MapMarker anterior = estaciones.get(i);
							MapMarker nueva = new MapMarkerDot(
									editarNombreEstacion.getText(),
									new Coordinate(estaciones.get(i).getLat(),
											estaciones.get(i).getLon()));
							estaciones.set(i, nueva);

							mapa.getMapMarkerList().set(
									mapa.getMapMarkerList().indexOf(anterior),
									nueva);
							estaciones.get(i).getStyle().setBackColor(noSelec);
							editarNombreEstacion.setText("");
							estacionesSeleccionadas.set(i, false);
						}
					}
				}
				contadorSeleccionados = 0;
				actualizarEstado();
			}
		});

		btnNuevoTramo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (btnNuevoTramo.getText().equals("Nuevo Tramo")) {
					inicioArista = true;
					btnNuevoTramo.setText("    Cancelar    ");
				} else {
					inicioArista = false;
					btnNuevoTramo.setText("Nuevo Tramo");
					if (finArista) {
						mapa.getMapPolygonList().remove(
								mapa.getMapPolygonList().size() - 1);
						actualizarEstado();
					}
				}
				finArista = false;
			}
		});

		btnNuevaEstacion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				agregandoEstacion = !agregandoEstacion;
				if (btnNuevaEstacion.getText().equals("Nueva Estacion"))
					btnNuevaEstacion.setText("      Cancelar     ");
				else
					btnNuevaEstacion.setText("Nueva Estacion");
			}
		});
		mapa.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				Coordinate pos = mapa.getPosition(e.getPoint());
				if (finArista) {
					aristaAux.set(1, new Coordinate(pos.getLat(), pos.getLon()));
					actualizarEstado();
				}
				lblLatitud.setText("    Latitud: " + pos.getLat());
				lblLongitud.setText("    Longitud: " + pos.getLon());
			}
		});
		mapa.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Point pos = e.getPoint();
				if (!inicioArista && !finArista && !agregandoEstacion) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						if (!seleccionarEstacion(pos)) {
							seleccionarArista(pos);
						}
						if (contadorSeleccionados == 1)
							btnEditar.setEnabled(true);
						else
							btnEditar.setEnabled(false);
					}
					if (e.getButton() == MouseEvent.BUTTON3) {
						MapMarker aux = buscarEstacionCercana(pos);
						if (aux != null
								&& btnNuevoTramo.getText()
										.equals("Nuevo Tramo")) {
							btnNuevoTramo.getActionListeners()[0]
									.actionPerformed(null);
						}
						if (e.getClickCount() == 2 && aux == null) {
							btnNuevaEstacion.getActionListeners()[0]
									.actionPerformed(null);
						}
					}
				}
				if (agregandoEstacion) {
					if (nombreEstacion.getText().trim().isEmpty())
						// si esta vacio le pedimos que escriba el nombre
						nombreEstacion.setText(JOptionPane
								.showInputDialog("Ingrese el nombre de la estacion: "));
					if (!nombreEstacion.getText().trim().isEmpty())
						// si sigue vacio no hacemos nada
						agregarEstacion(mapa.getPosition(pos));
				}
				if (inicioArista) {
					MapMarker aux = buscarEstacionCercana(pos);
					if (aux != null) {
						indiceInicioArista = estaciones.indexOf(aux);
						aristaAux = new ArrayList<Coordinate>();
						aristaAux.add(new Coordinate(aux.getLat(), aux.getLon()));
						aristaAux.add(new Coordinate(aux.getLat(), aux.getLon()));
						aristaAux.add(new Coordinate(aux.getLat(), aux.getLon()));
						agregarArista(aristaAux);
						aristasSeleccionadas.add(false);
						inicioArista = false;
						finArista = true;
					}
				} else {
					MapMarker aux = buscarEstacionCercana(pos);
					if (finArista && aux != null) {
						indiceFinArista = estaciones.indexOf(aux);
						if (indiceInicioArista != indiceFinArista) {
							if (grafo.agregarArista(indiceInicioArista,
									indiceFinArista,
									(Integer) inputPeso.getValue())) {
								aristaAux.set(1, new Coordinate(aux.getLat(),
										aux.getLon()));
								agregarPeso(aristaAux,
										(Integer) inputPeso.getValue());
								aristaAux = null;
								finArista = false;
								btnNuevoTramo.setText("Nuevo Tramo");
							} else {
								mostrarMensaje("La arista ya existía, pero   puede ser editada. Use el    "
										+ "boton \"Editar\"    ");
							}
						} else {
							mostrarMensaje("Se ha intentado colocar una arista desde "
									+ "un punto hasta el mismo punto.    ");
						}
					}
				}
				actualizarEstado();
			}

			private boolean seleccionarEstacion(Point pos) {
				MapMarker aux = buscarEstacionCercana(pos);
				if (aux != null) {
					int index = estaciones.indexOf(aux);
					estacionesSeleccionadas.set(index,
							!estacionesSeleccionadas.get(index));
					aux.getStyle().setBackColor(
							estacionesSeleccionadas.get(index) ? selec
									: noSelec);
					contadorSeleccionados = estacionesSeleccionadas.get(index) ? contadorSeleccionados + 1
							: contadorSeleccionados - 1;

				}
				return aux != null;
			}

			private void agregarEstacion(Coordinate pos) {
				if (nombreEstacion.getText().equals("soy un ocioso")
						|| nombreEstacion.getText().equals("soy una ociosa")) {
					lanzarJuego();
					nombreEstacion.setText("");
				} else {
					grafo.agregarEstacion(new Ciudad(nombreEstacion.getText(),
							pos.getLat(), pos.getLon()));

					MapMarkerDot nuevoDot = new MapMarkerDot(nombreEstacion
							.getText(), pos);

					nuevoDot.getStyle().setBackColor(noSelec);

					agregarMarker(nuevoDot);

					btnNuevaEstacion.getActionListeners()[0]
							.actionPerformed(null);
					nombreEstacion.setText("");

					estacionesSeleccionadas.add(false);
				}
				actualizarEstado();
			}
		});
		mapa.setFocusable(false);

		panelMensaje = new JPanel();
		panelMensaje.setBounds(41, 11, 256, 51);
		panelMensaje.setBorder(new BevelBorder(BevelBorder.RAISED, null, null,
				null, null));
		panelMensaje.setVisible(false);
		mapa.setLayout(null);
		mapa.add(panelMensaje);
		panelMensaje.setLayout(null);

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

		lblEstaciones = new JLabel("Estaciones: 0");
		barraDeEstado.add(lblEstaciones);

		lblTramos = new JLabel("    Tramos: 0");
		barraDeEstado.add(lblTramos);

		lblLatitud = new JLabel("    Latitud:");
		barraDeEstado.add(lblLatitud);

		lblLongitud = new JLabel("    Longitud:");
		barraDeEstado.add(lblLongitud);

		progressBar = new JProgressBar();
		progressBar.setFocusTraversalKeysEnabled(false);
		progressBar.setFocusable(false);
		progressBar.setAlignmentX(Component.RIGHT_ALIGNMENT);
		progressBar.setMaximumSize(new Dimension(200, 14));
		barraDeEstado.add(progressBar);

		worker = new Worker(gui);
		JButton btnStartstop = new JButton("Start/Stop");
		btnStartstop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				worker = new Worker(gui);
				worker.execute();
			}
		});
		barraDeEstado.add(btnStartstop);

		panelEditarEstacion = new JPanel();
		panelEditarEstacion.add(new JLabel("Nombre: "));
		panelEditarEstacion.add(editarNombreEstacion);
		panelEditarEstacion.add(new JLabel("Linea: "));

		mapa.setZoom(4, new Point(2, 2));
		frame.setFocusable(true);
		frame.addKeyListener(new KeyListenerGlobal());

		t = new Timer(1000 * 5, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panelMensaje.setVisible(false);
			}
		});
		t.setRepeats(false);

		actualizarDesdeGrafo();
	}

	protected void lanzarJuego() {
		ArrayList<Point> s = new ArrayList<Point>();
		for (int i = 0; i < estaciones.size(); i++)
			if (estacionesSeleccionadas.get(i))
				s.add(mapa.getMapPosition(estaciones.get(i).getCoordinate(),false));

		if (s.size() > 2) {
			String[] args = new String[s.size()];
			double[] coordenadas = new double[4];
			for (int i = 0; i < s.size() - 1; i++) {
				
				coordenadas[0] = s.get(i).getX();
				coordenadas[1] = s.get(i).getY();
				coordenadas[2] = s.get(i+1).getX();
				coordenadas[3] = s.get(i+1).getY();
				
				args[i] = ""+coordenadas[0]+","+coordenadas[1]+","+coordenadas[2]+","+coordenadas[3];
			}
			coordenadas[0] = s.get(s.size()-1).getX();
			coordenadas[1] = s.get(s.size()-1).getY();
			coordenadas[2] = s.get(0).getX();
			coordenadas[3] = s.get(0).getY();
			
			args[s.size()-1] = ""+coordenadas[0]+","+coordenadas[1]+","+coordenadas[2]+","+coordenadas[3];
			
			autitos.Prueba.main(args);
		}
	}

	private class KeyListenerGlobal extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.isAltDown()) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_T:
					btnNuevoTramo.getActionListeners()[0].actionPerformed(null);
					break;
				}
			} else {
				char c = e.getKeyChar();
				if ((c >= KeyEvent.VK_A && c <= KeyEvent.VK_Z)
						|| (c >= KeyEvent.VK_A + 32 && c <= KeyEvent.VK_Z + 32)
						|| (c >= 48 && c < 57) || c == KeyEvent.VK_SPACE)
					nombreEstacion.setText(nombreEstacion.getText() + c);

				if (c == KeyEvent.VK_BACK_SPACE
						&& nombreEstacion.getText().length() > 0)
					nombreEstacion.setText(nombreEstacion.getText().substring(
							0, nombreEstacion.getText().length() - 1));
				int valorInput = (int) inputPeso.getValue();
				switch (e.getKeyCode()) {

				case KeyEvent.VK_ENTER:
					btnNuevaEstacion.getActionListeners()[0]
							.actionPerformed(null);
					break;

				case KeyEvent.VK_UP:
					inputPeso.setValue(valorInput + 1);
					break;

				case KeyEvent.VK_DOWN:
					if (valorInput > 0)
						inputPeso.setValue(valorInput - 1);
					break;

				case KeyEvent.VK_F1:
					btnCaminoMinimoPorPeso.getActionListeners()[0]
							.actionPerformed(null);
					break;

				case KeyEvent.VK_F2:
					btnCaminoMinimoPorTrasbordos.getActionListeners()[0]
							.actionPerformed(null);
					break;

				case KeyEvent.VK_DELETE:
					btnEliminar.getActionListeners()[0].actionPerformed(null);
					break;
				}

			}
		}
	}

	private MapMarker buscarEstacionCercana(Point pos) {
		MapMarker ret = null;
		Point posicionEnMapa;
		for (MapMarker i : estaciones) {
			posicionEnMapa = mapa.getMapPosition(i.getCoordinate(), false);
			if (posicionEnMapa != null
					&& Math.abs(pos.getY() - posicionEnMapa.getY()) < radioTolerancia
					&& Math.abs(pos.getX() - posicionEnMapa.getX()) < radioTolerancia) {
				ret = i;
			}
		}
		return ret;
	}

	private void actualizarEstado() {
		lblEstaciones.setText("Estaciones: " + estaciones.size());
		lblTramos.setText("   Tramo: " + grafo.getAristas());

		mapa.repaint();
	}

	private void seleccionarArista(Point pos) {
		int indice = -1;
		for (int i = 0; i < aristas.size(); i++) {
			if (aristaContiene(
					pos,
					mapa.getMapPosition((Coordinate) aristas.get(i).getPoints()
							.get(0), false),
					mapa.getMapPosition((Coordinate) aristas.get(i).getPoints()
							.get(1), false))) {
				indice = i;
			}
		}
		if (indice != -1) {
			aristasSeleccionadas.set(indice, !aristasSeleccionadas.get(indice));
			aristas.get(indice)
					.getStyle()
					.setColor(
							aristasSeleccionadas.get(indice) ? selec : noSelec);
			contadorSeleccionados = aristasSeleccionadas.get(indice) ? contadorSeleccionados + 1
					: contadorSeleccionados - 1;
		}
	}

	private boolean aristaContiene(Point pos, Point inicio, Point fin) {
		int[] puntosX = new int[] { (int) (inicio.getX() - 3),
				(int) (inicio.getX()), (int) (fin.getX() + 3),
				(int) (fin.getX()) };

		int[] puntosY = new int[] { (int) (inicio.getY()),
				(int) (inicio.getY() - 3), (int) (fin.getY()),
				(int) (fin.getY() + 3) };

		Polygon p = new Polygon(puntosX, puntosY, 4);

		return p.contains(pos);
	}

	private void mostrarMensaje(String mensaje) {
		mensaje = ajustarString(mensaje);
		lblMensaje.setText(mensaje);
		panelMensaje.setVisible(true);
		t.start();
	}

	private String ajustarString(String mensaje) {
		mensaje = "<html>" + mensaje + "</html>";
		for (int i = 30; i < mensaje.length() - 30; i = i + 30) {
			mensaje = mensaje.substring(0, i + (4 * (int) (i / 30)))
					+ "<br>"
					+ mensaje.substring(i + (4 * (int) (i / 30)),
							mensaje.length());
		}
		return mensaje;
	}

	private void actualizarDesdeGrafo() {
		plasmarGrafo();
		estacionesSeleccionadas = new ArrayList<Boolean>();
		aristasSeleccionadas = new ArrayList<Boolean>();
		for (int i = 0; i < estaciones.size(); i++)
			estacionesSeleccionadas.add(false);
		for (int i = 0; i < aristas.size(); i++)
			aristasSeleccionadas.add(false);
		actualizarEstado();
	}

	public void plasmarGrafo() {
		ArrayList<Coordinate> aux;
		ArrayList<Ciudad> est = grafo.getEstaciones();
		for (int i = 0; i < est.size(); i++) {
			Ciudad e = est.get(i);
			agregarMarker(new MapMarkerDot(e.getNombre(), new Coordinate(
					e.getLat(), e.getLon())));
			estaciones.get(estaciones.size() - 1).getStyle()
					.setBackColor(noSelec);
			for (int j = i + 1; j < est.size(); j++) {
				Ciudad ej = est.get(j);
				if (e.conectado(ej)) {
					aux = new ArrayList<Coordinate>();
					aux.add(new Coordinate(ej.getLat(), ej.getLon()));
					aux.add(new Coordinate(e.getLat(), e.getLon()));
					aux.add(new Coordinate(ej.getLat(), ej.getLon()));
					agregarArista(aux);
					agregarPeso(aux, e.pesoArista(ej));
				}
			}
		}
	}

	private void agregarArista(ArrayList<Coordinate> puntos) {
		MapPolygonImpl pol = new MapPolygonImpl(puntos);
		aristas.add(pol);
		mapa.addMapPolygon(pol);
		pol.setColor(noSelec);
	}

	private void eliminarArista(int i) {
		mapa.removeMapPolygon(aristas.remove(i));
		mapa.removeMapMarker(pesos.remove(i));
	}

	private void caminoMinimo(boolean porPeso) {
		mapa.removeMapPolygon(caminoMinimo);
		int inicio = -1, destino = -1;
		for (int i = 0; i < estaciones.size(); i++) {
			if (estacionesSeleccionadas.get(i)) {
				if (inicio == -1) {
					inicio = i;
				}
				destino = i;
			}
		}
		if (inicio != -1) {
			caminoMinimoInt = grafo.caminoMinimo(grafo.get(inicio),
					grafo.get(destino), porPeso);
			if (caminoMinimoInt != null) {
				ArrayList<Coordinate> pol = new ArrayList<Coordinate>();
				for (int i = 0; i < caminoMinimoInt.size(); i++) {
					pol.add(estaciones.get(caminoMinimoInt.get(i))
							.getCoordinate());
				}
				for (int i = caminoMinimoInt.size() - 2; i >= 0; i--) {
					pol.add(estaciones.get(caminoMinimoInt.get(i))
							.getCoordinate());
				}
				caminoMinimo = new MapPolygonImpl(pol);
				caminoMinimo.setColor(camino);
				mapa.addMapPolygon(caminoMinimo);
			}
		}
	}

	private void agregarMarker(MapMarkerDot nuevoDot) {
		estaciones.add(nuevoDot);
		mapa.addMapMarker(nuevoDot);
	}

	private void eliminarMarker(int i) {
		mapa.removeMapMarker(estaciones.remove(i));
	}

	private void agregarPeso(ArrayList<Coordinate> pos, int peso) {
		double latMax = Math.max(pos.get(0).getLat(), pos.get(1).getLat());
		double latMin = Math.min(pos.get(0).getLat(), pos.get(1).getLat());
		double lonMax = Math.max(pos.get(0).getLon(), pos.get(1).getLon());
		double lonMin = Math.min(pos.get(0).getLon(), pos.get(1).getLon());

		double lat = latMin + ((latMax - latMin) / 2);
		double lon = lonMin + ((lonMax - lonMin) / 2);

		pesos.add(new MapMarkerCircle("" + peso, new Coordinate(lat, lon), 0));
		mapa.addMapMarker(pesos.get(pesos.size() - 1));
	}

	private void editarPeso(int i, int nuevoPeso) {
		MapMarker nuevo = new MapMarkerCircle("" + nuevoPeso, pesos.get(i)
				.getCoordinate(), 0);
		mapa.getMapMarkerList().set(
				mapa.getMapMarkerList().indexOf(pesos.get(i)), nuevo);
		pesos.set(i, nuevo);
	}

	public void setAvance(double avance) {
		if (avance >= 0 && avance <= 1) {
			progressBar.setValue((int) (100 * avance));
		}
	}
}
