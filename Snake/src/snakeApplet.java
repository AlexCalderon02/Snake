import java.applet.*;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.LinkedList; 


public class snakeApplet extends Applet{


	private snakeCanvas c;
	
	public void init() {


		// Construct the button
	     Button Bcolor = new Button("Change fruit color to blue.");

	     // add the button to the layout
	     this.add(Bcolor); 
	     


		c = new snakeCanvas();
		c.setPreferredSize(new Dimension(640,480));
		c.setVisible(true);
		c.setFocusable(true);
		this.add(c);
		this.setVisible(true);
		this.setSize(new Dimension(640, 480));
	}
	
	public void paint(Graphics g) {
		this.setSize( new Dimension(640, 480));
	}
}
