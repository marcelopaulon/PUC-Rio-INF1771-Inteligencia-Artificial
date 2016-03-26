package program;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import pathfinding.AStar;
import pathfinding.Node;
import gfx.Assets;
import map.GameMap;
import map.MapLoader;
import map.MapLoaderException;
import map.MapPanel;

public final class Program
{

	private static Dimension defaultDimension = new Dimension(800, 600);

	public static JPanel toolsPanel;

	private GameMap map;
	private MapPanel mapPanel;
	
	private AStar astar = new AStar();

	private JFrame window;
	
	private File file = new File("defaultMap.mapsave");

	private void createToolsPanel()
	{
		toolsPanel = new JPanel();

		JButton loadMapButton = new JButton("Carregar Mapa");
		JButton exitButton = new JButton("Sair");

		loadMapButton.addActionListener(loadMapListener);
		exitButton.addActionListener(exitListener);

		toolsPanel.add(loadMapButton);
		toolsPanel.add(exitButton);

		window.add(toolsPanel, BorderLayout.PAGE_START);
	}

	private void createMapPanel()
	{
		mapPanel = new MapPanel();
		mapPanel.setSize(defaultDimension);

		window.add(mapPanel, BorderLayout.CENTER);
		
		try {
			map = MapLoader.tryLoadMap(file);
			mapPanel.loadMap(map);
		} catch (MapLoaderException e) {
			JOptionPane.showMessageDialog(Program.toolsPanel, "Erro ao carregar o mapa padr�o.", "Erro",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	public GameMap showLoadMapDialog() throws MapLoaderException
	{
		try
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(file);
			chooser.setFileFilter(new FileNameExtensionFilter("Apenas .mapsave", "mapsave"));
			int retorno = chooser.showOpenDialog(null);

			if (retorno == JFileChooser.APPROVE_OPTION)
			{
				return MapLoader.tryLoadMap(chooser.getSelectedFile());
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new MapLoaderException();
		}

		return null;
	}

	private ActionListener loadMapListener = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				map = showLoadMapDialog();

				if (map != null)
				{
					mapPanel.loadMap(map);
				}
			} catch (MapLoaderException exception)
			{
				JOptionPane.showMessageDialog(toolsPanel, "Erro ao carregar o mapa.", "Erro",
						JOptionPane.WARNING_MESSAGE);
			}
		}
	};

	private ActionListener exitListener = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			System.exit(0);
		}
	};

	public static void main(String[] args)
	{
		new Program().appStart();
	}

	public void appStart()
	{
		window = new JFrame("INF1771 - T1");

		Assets.init();

		createToolsPanel();
		createMapPanel();

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setMinimumSize(defaultDimension);
		window.pack();
		window.setSize(defaultDimension);
		window.setVisible(true);
		
		if(map != null)
		{
			List<Node> path = astar.FindPath(map, mapPanel);
			if(path != null)
			{
				mapPanel.renderPath(path);
			}
			else
			{
				// Alertar que n�o foi encontrado um caminho
			}
		}
	}
}
