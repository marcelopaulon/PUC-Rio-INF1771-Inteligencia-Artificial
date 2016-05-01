package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import gfx.Assets;
import logic.Action;
import logic.CaptureGold;
import mapcell.*;

/**
 * Panel where the map is rendered
 */
public class MapPanel extends JPanel {
	/**
	 * Class serialization version identifier
	 * @see <a href=http://blog.caelum.com.br/entendendo-o-serialversionuid/>Entendendo o serialVersionUID</a>
	 */
	private static final long serialVersionUID = -8607607402685333888L;

	/**
	 * GameMap saved
	 */
	private GameMap savedMap;
	
	/**
	 * GameMap to be rendered
	 */
	private GameMap loadedMap;
	
	private class Coordinate
	{
		public int X;
		public int Y;
		
		public Coordinate(int x, int y)
		{
			X = x;
			Y = y;
		}
		
		@Override
	    public boolean equals(Object obj) {
	       if (!(obj instanceof Coordinate))
	            return false;
	       if (obj == this)
	            return true;
	       
	       Coordinate c = (Coordinate) obj;
	       if(c.X == this.X && c.Y == this.Y) 
	    	   return true;
	       
	       return false;
		}
	}
	
	private List<Coordinate> visitedCells; 
	
	private List<Coordinate> visibleCells; 

	/**
	 * Graphics object
	 */
	private Graphics g;

	private int _timeSlice = 50;  // update every 50 milliseconds
	
	private Timer _timer = new Timer(_timeSlice, new ActionListener() {
	    /**
	     * The timer "ticks" by calling this method every _timeslice milliseconds
	     */
	    public void actionPerformed (ActionEvent e) {
	        MapPanel.this.repaint();
	    }
	});
	
	private long lastActionTime;
		
	private int curAgentX, curAgentY, curAgentEnergy;
	
	private char curAgentOrientation; // U -> Up D -> Down L -> Left R -> Right
	
	private boolean gameEnded;
	
	public boolean agentView = false;
	
	public int actionTimeIntervalMs = 100;
	
	public boolean showPath = true;
	
	private AgentCell agentCell = new AgentCell('A');
	
	private CaptureGold captureGold;
	
	/**
	 * MapPanel constructor
	 */
	public MapPanel() {
		setBorder(BorderFactory.createLineBorder(Color.black));
		
		captureGold = new CaptureGold();
	}
	
	public void drawMapStatus(int startX, int startY)
	{
		g.setColor(Color.WHITE);
		g.drawString("Energia: " + curAgentEnergy, startX, startY);
		g.setColor(Color.BLACK);
	}
	
	/**
	 * Draws map on screen
	 * @return true
	 */
	public boolean renderMap() {
		int width = getWidth();
		int height = getHeight();

		int numcols = loadedMap.getNColumns(), numrows = loadedMap.getNRows();
		
		_timer.stop();
		
		// Clear the panel
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);

		double rectWidth = Assets.TILE_WIDTH;
		double rectHeight = Assets.TILE_HEIGHT;

		if (numrows * Assets.TILE_HEIGHT > height) {
			rectHeight = (height / numrows);
		}

		if (numcols * Assets.TILE_WIDTH > width) {
			rectWidth = (width / numcols);
		}
		
		// Draw the grid
		for (int i = 1; i <= numrows; i++) {
			for (int j = 1; j <= numcols; j++) {
				int y = (int) ((i - 1) * rectHeight);
				int x = (int) ((j - 1) * rectWidth);
				
				if(!agentView || visibleCells.contains(new Coordinate(j-1, i-1)))
				{
					MapCell.Cells et = loadedMap.getValue(i, j);
					MapCell cell = MapUtils.getTile(et);
					
					if(et != MapCell.Cells.START && visitedCells.contains(new Coordinate(j-1, i-1)) && showPath == true)
					{
						cell = MapUtils.getTile(MapCell.Cells.FLOORVISITED);
					}
					
					cell.render(g, x, y, (int) rectWidth, (int) rectHeight);
				}
			}
		}
		
		int y = (int) ((curAgentY) * rectHeight);
		int x = (int) ((curAgentX) * rectWidth);
		agentCell.render(g, x, y, (int) rectWidth, (int) rectHeight);
		
		int statusY = (int) ((numrows + 1) * rectHeight);
		int statusX = (int) ((numcols - 1)/2 * rectWidth);

		drawMapStatus(statusX, statusY);
		
		_timer.start();
				
		return true;
	}

	/**
	 * Loads GameMap on screen
	 * @param map GameMap representing map to be drawn on screen
	 */
	public void loadMap(GameMap map) 
	{
		savedMap = map;
		resetGame();
	}
	
	private void visitCell(int x, int y)
	{
		Coordinate c1, c2, c3, c4, c5;
		c1 = new Coordinate(x, y);
		if(!visibleCells.contains(c1))
		{
			visibleCells.add(c1);
		}
		
		if(!visitedCells.contains(c1))
		{
			visitedCells.add(c1);
		}
		
		c2 = new Coordinate(x+1, y);
		if(!visibleCells.contains(c2))
		{
			visibleCells.add(c2);
		}
		
		c3 = new Coordinate(x-1, y);
		if(!visibleCells.contains(c3))
		{
			visibleCells.add(c3);
		}
		
		c4 = new Coordinate(x, y+1);
		if(!visibleCells.contains(c4))
		{
			visibleCells.add(c4);
		}
		
		c5 = new Coordinate(x, y-1);
		if(!visibleCells.contains(c5))
		{
			visibleCells.add(c5);
		}
		
	}
	
	public void resetGame()
	{
		_timer.stop();
		
		loadedMap = new GameMap(savedMap);
		
		visitedCells = new ArrayList<Coordinate>();
		visibleCells = new ArrayList<Coordinate>();
		visitCell(1, 1);
		
		gameEnded = false;
		
		try {
			captureGold.resetGame(loadedMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		lastActionTime = System.currentTimeMillis();
		
		curAgentX = 1;
		curAgentY = 1;
		curAgentEnergy = 100;
		curAgentOrientation = 'D';
		agentCell.setOrientation(curAgentOrientation);
		
		this.repaint();
	}
	
	private void handleAction(Action action)
	{
		if(action == Action.ROTATE)
		{
			if(curAgentOrientation == 'U') curAgentOrientation = 'R';
			else if(curAgentOrientation == 'R') curAgentOrientation = 'D';
			else if(curAgentOrientation == 'D') curAgentOrientation = 'L';
			else if(curAgentOrientation == 'L') curAgentOrientation = 'U';
			agentCell.setOrientation(curAgentOrientation);
		}
		if(action == Action.ROTATELEFT)
		{
			if(curAgentOrientation == 'U') curAgentOrientation = 'L';
			else if(curAgentOrientation == 'L') curAgentOrientation = 'D';
			else if(curAgentOrientation == 'D') curAgentOrientation = 'R';
			else if(curAgentOrientation == 'R') curAgentOrientation = 'U';
			agentCell.setOrientation(curAgentOrientation);
		}
		else if(action == Action.WALK)
		{
			if(curAgentOrientation == 'U') curAgentY--;
			else if(curAgentOrientation == 'R') curAgentX++;
			else if(curAgentOrientation == 'D') curAgentY++;
			else if(curAgentOrientation == 'L') curAgentX--;
			
			visitCell(curAgentX, curAgentY);
		}
		else if(action == Action.ATTACK)
		{
			CaptureGold.Position p = captureGold.getCurPosition();
			curAgentX = p.X;
			curAgentY = p.Y;
			visitCell(curAgentX, curAgentY);
			loadedMap.setValue(curAgentY+1, curAgentX+1, MapCell.Cells.FLOOR);
		}
		else if(action == Action.PICKGOLD)
		{
			loadedMap.setValue(curAgentY+1, curAgentX+1, MapCell.Cells.FLOOR);
		}
		else if(action == Action.END)
		{
			if(!gameEnded)
			{
				gameEnded = true;
				JOptionPane.showMessageDialog(null, "Mapa completado!! Parab�ns!");
			}
		}
		else if(action == Action.GAMEOVER)
		{
			if(!gameEnded)
			{
				gameEnded = true;
				JOptionPane.showMessageDialog(null, "GAME OVER - Pikachu n�o sobreviveu ao mapa");
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.g = g;
		
		if(loadedMap != null)
		{
			renderMap();
			
			if(!gameEnded && System.currentTimeMillis() - lastActionTime > actionTimeIntervalMs)
			{
				handleAction(captureGold.getNextMove());
				curAgentEnergy = captureGold.getCurEnergy();
				lastActionTime = System.currentTimeMillis();
			}
		}
	}
}
