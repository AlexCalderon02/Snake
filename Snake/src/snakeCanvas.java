import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JOptionPane;

public class snakeCanvas extends Canvas implements Runnable, KeyListener{
	
	private final int Box_Height = 15;
	private final int Box_Width = 15;
	private final int Grid_Width = 25;
	private final int Grid_Height = 25;
	
	private LinkedList<Point> snake;
	private Point fruit;
	private int direction = Direction.NO_DIRECTION;
	
	private Thread runThread;
	private Graphics globalGraphics;
	private int score = 0;
	private String highScore = "";
	
	private Image menuImage;
	private boolean isInMenu = true;
	
	public void init() {
		
	}
	
	public void paint(Graphics g) { 
		if (isInMenu) {
			DrawMenu(g);
		}
		else {
			DrawFruit(g);
			DrawGrid(g);
			DrawSnake(g);
			DrawScore(g);
		}
		snake = new LinkedList<Point>();
		GenerateDefaultSnake();
		snake.add(new Point(3, 1));
		snake.add(new Point(3, 2));
		snake.add(new Point(3, 3));
		PlaceFruit();
		g.fillRect(0, 0, 10, 10);
		globalGraphics = g.create();
		this.addKeyListener(this);
		if (snake == null) {
			snake = new LinkedList<Point>();
			GenerateDefaultSnake();
			PlaceFruit();
		}
		if (runThread == null) {
			this.setPreferredSize(new Dimension(640, 480));
			runThread = new Thread(this);
			runThread.start();
		}
		if (highScore.equals("")) {
		highScore = this.GetHighScore();
		}
	}
	
	public void DrawMenu(Graphics g) {
		if (this.menuImage == null) {
			try {
				URL imagePath = snakeCanvas.class.getResource("SnakeMenu.png");
				this.menuImage = Toolkit.getDefaultToolkit().getImage(imagePath);
			}
			catch (Exception e) {
				e.printStackTrace();
			}	
		}

		g.drawImage(menuImage, 0, 0, 640, 480, this);
	}
	
	public void GenerateDefaultSnake() {
		score = 0;
		snake.clear();
		
		snake.add(new Point(0, 2));
		snake.add(new Point(0, 1));
		snake.add(new Point(0, 0));
		direction = Direction.NO_DIRECTION;
		
	}
	
	public void Draw(Graphics g) {
		
		// Create a new image.
		BufferedImage buffer = new BufferedImage(Box_Width * Grid_Width, Box_Height * Grid_Height + 20, BufferedImage.TYPE_INT_ARGB);
		Graphics bufferGraphics = buffer.getGraphics();
		bufferGraphics.fillRect(0, 0, this.getSize().width, this.getSize().height); 
		
		DrawFruit(bufferGraphics);
		DrawGrid(bufferGraphics);
		DrawSnake(bufferGraphics);
		DrawScore(bufferGraphics);
		
		// Flip 
		g.drawImage(buffer, 0, 0, Box_Width * Grid_Width, Box_Height * Grid_Height + 20, this);
	}
	
	public void Move() {
		Point head = snake.peekFirst();
		Point newPoint = head;
		switch (direction) {
		case Direction.NORTH:
			newPoint = new Point(head.x, head.y -1);
			break;
		case Direction.SOUTH:
			newPoint = new Point(head.x, head.y + 1);
			break;
		case Direction.WEST:
			newPoint = new Point(head.x - 1, head.y);
			break;
		case Direction.EAST:
			newPoint = new Point (head.x + 1, head.y);
			break;
		}
	
	snake.remove(snake.peekLast());
	
	if (newPoint.equals(fruit)) {
		// Snake Eats Fruit
		score += 1;
		Point addPoint = (Point) newPoint.clone();
		
		switch (direction) {
		case Direction.NORTH:
			newPoint = new Point(head.x, head.y -1);
			break;
		case Direction.SOUTH:
			newPoint = new Point(head.x, head.y + 1);
			break;
		case Direction.WEST:
			newPoint = new Point(head.x - 1, head.y);
			break;
		case Direction.EAST:
			newPoint = new Point (head.x + 1, head.y);
			break;
		}
		snake.push(addPoint);
		PlaceFruit();
		
	}
	else if (newPoint.x < 0 || newPoint.x > Grid_Width - 1) {
		// Ran out of bounds; reset game
		CheckScore();
		GenerateDefaultSnake();
		return;

	}
	
	else if (newPoint.y < 0 || newPoint.y > Grid_Height - 1) {
		// Ran out of bounds; reset game
		CheckScore();
		GenerateDefaultSnake();
		return;

	}
	
	else if (snake.contains(newPoint)) {
		// Ran into Snake; reset game
		CheckScore();
		GenerateDefaultSnake();
		return;


	}
	
	snake.push(newPoint);
	
	
}

	public void DrawScore(Graphics g) {
		g.drawString("Score: " + score, 0, Box_Height * Grid_Height + 10);
		g.drawString("HighScore: " + highScore,  0, Box_Height * Grid_Height + 20);
	}
	
	public void CheckScore () {
		if (highScore.equals(""))
			return;
		if (score > Integer.parseInt((highScore.split(":")[1]))) {
			System.out.println(highScore);
			String name = JOptionPane.showInputDialog("You set a new highscore, what is your name?");
			highScore = name + ":" + score;
			
			File scoreFile = new File("highscore.dat");
			if (!scoreFile.exists())
				try {
					scoreFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			FileWriter writeFile = null;
			BufferedWriter writer = null;
			try {
				writeFile = new FileWriter(scoreFile);
				writer = new BufferedWriter(writeFile);
				writer.write(this.highScore);
			}
			catch (Exception e) {
			}
			finally {
				try {
				if(writer != null)
					writer.close();
				}
			
			catch (Exception e) {
			}
		}
		}
	}
		
	
	public void DrawGrid(Graphics g) { 
		g.drawRect(0,  0,  Grid_Width * Box_Width, Grid_Height * Box_Height);
		for (int x = Box_Width; x < Grid_Width * Box_Width; x+=Box_Width) {
			g.drawLine(x, 0,  x,  Box_Height * Grid_Height);
		}
		for (int y = Box_Height; y < Grid_Height * Box_Height; y+=Box_Height) {
			g.drawLine(0,  y,  Grid_Width * Box_Width,  y);
		}
	}
	
	public void DrawSnake(Graphics g) {
		g.setColor(Color.GREEN);
		for (Point p : snake) {
			g.fillRect(p.x * Box_Width, p.y * Box_Height, Box_Width, Box_Height);
		}
		g.setColor(Color.BLACK);
	}
	


	public void DrawFruit(Graphics g) {
		String fruitcolor = null;
		Color please = Color.BLACK;
		g.setColor(Color.getColor(fruitcolor, please));
		g.fillOval(fruit.x * Box_Width, fruit.y * Box_Height, Box_Width, Box_Height);
		g.setColor(Color.BLACK);
	}
	
	public void PlaceFruit() {
		Random rand = new Random();
		int randomX = rand.nextInt(Grid_Width);
		int randomY = rand.nextInt(Grid_Height);
		Point randomPoint = new Point(randomX, randomY);
		while (snake.contains(randomPoint)) {
			randomX = rand.nextInt(Grid_Width);
			randomY = rand.nextInt(Grid_Height);
			randomPoint = new Point(randomX, randomY);
		}
		fruit = randomPoint; 
	}
	
	

	@Override
	public void run() {
		while (true) {
			Move();
			Draw(globalGraphics);
			
			try {
				Thread.currentThread(); // If this is turned off game will run at light speed!
				Thread.sleep(100);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public String GetHighScore() {
		
		FileReader readFile = null;
		BufferedReader reader = null;
		try {
		readFile = new FileReader("highscore.dat");
		reader = new BufferedReader(readFile);
		return reader.readLine();
		}
		
		catch (Exception e) {
			return "Nobody:0";
		}
		
		finally { 
			try {
				if (reader != null)
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		// VK stands for Virtual Keyboard
			case KeyEvent.VK_UP: 
				if (direction != Direction.SOUTH)
				direction = Direction.NORTH;
				break;
			case KeyEvent.VK_DOWN:
				if (direction != Direction.NORTH)
				direction = Direction.SOUTH;
				break;
			case KeyEvent.VK_RIGHT:
				if (direction != Direction.WEST)
				direction = Direction.EAST;
				break;
			case KeyEvent.VK_LEFT:
				if (direction != Direction.EAST)
				direction = Direction.WEST;
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
