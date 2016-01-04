package gameframework;
import java.util.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import java.awt.event.*;

/***
 * Ball Object for the game
 * 
 * @author Eric Chao
 *
 */
public class Ball {
	
		/**
		 * x and y coordinates
		 */
		public int x;
		public int y;
		
		/**
		 *  x and y velocity and acceleration
		 */
		public int xSpeed;
		public int ySpeed;
		public int xAccel;
		public int yAccel;
		public int stopSpeed;
		public int level;
		
		/**
		 * Timer to set success or failure screen for ball (used in Framework.java)
		 */
		public long stageSuccessTimer;
		
		/**
		 * Success and Crash flags
		 */
		public boolean isCrashed;
		public boolean success;
	
		
		/**
		 * Schemes Array
		 */
		public int[][] Schemes;
		public int currentScheme;
		
		/**
		 * Buffered Images of the Ball
		 */
		public int ImageHeight;
		public int ImageWidth;
		private BufferedImage ball;
		private BufferedImage ball_win;
		private BufferedImage ball_lose;
		
	public Ball(int xCoord, int yCoord, int level) {		
		this.x = xCoord;
		this.y = yCoord;
		stageSuccessTimer = -1;
		this.level = level;
		//We want Two schemes for level 1, 3 for level 2, etc.
		this.Schemes = new int[level+1][] ;
		success = false;
		isCrashed = false;
		Initialize();
		LoadContent();
		
	}

	
	/**
	 * Initialize some variables
	 */
	private void Initialize(){
		Reset();
		xAccel = 2;
		yAccel = 2;
		stopSpeed = 1;
		
		//Create a random set of directions for each scheme
		for(int i = 0; i<(level+1); i++){
			Set<Integer> generDirec = new HashSet<Integer>();
			int[] schema = new int[4];
			for(int j = 0 ; j < 4 ; j ++){
				Random randomGen = new Random();
				int random = randomGen.nextInt(4);
				//create an unique random number from 1 to 4
				while(generDirec.contains(random)){
					random = randomGen.nextInt(4);
				}
				generDirec.add(random);
				schema[j] = random;	
			}
			this.Schemes[i]= schema;
		}
	}
	
	/**
	 * Load the images of the ball
	 */
	private void LoadContent(){
		try
        {
			
	        ball = ImageIO.read(this.getClass().getResource("/gameframework/resources/images/Ball.png"));
	        ImageHeight = ball.getHeight();
	        ImageWidth = ball.getWidth();
	        ball_lose = ImageIO.read(this.getClass().getResource("/gameframework/resources/images/Ball_failure.png"));
            ball_win = ImageIO.read(this.getClass().getResource("/gameframework/resources/images/Ball_success.png"));
        }
        catch (IOException ex) {
            Logger.getLogger(Ball.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
	
	/**
	 * Update the balls position based on its current scheme
	 *
	 * Note that W = 1, A = 0, S = 2, D = 3;
	 * 
	 */
	public void Update(){
		
				int[] directions = 	 this.Schemes[currentScheme];
				if(!isCrashed && !success){
					if(Canvas.keyboardKeyState(KeyEvent.VK_W))
					{	
						switch(directions[0]){
							case 0:
								ySpeed += yAccel;
								break;
							case 1:
							 	xSpeed -= xAccel;
							 	break;
							case 2:
								xSpeed += xAccel;
								break;
							case 3:
								ySpeed -= yAccel;
								break;
						}
					}
				
					if(Canvas.keyboardKeyState(KeyEvent.VK_D))
					{	
						switch(directions[1]){
							case 0:
							ySpeed += yAccel;
							break;
						case 1:
							 xSpeed -= xAccel;
							break;
						case 2:
							xSpeed += xAccel;
							break;
						case 3:
							ySpeed -= yAccel;
							break;
						}
					}		
					 if(Canvas.keyboardKeyState(KeyEvent.VK_A))
					 {
						switch(directions[2]){
							case 0:
								ySpeed += yAccel;
								break;
							case 1:
								xSpeed -= xAccel;
								break;
							case 2:
								xSpeed += xAccel;
								break;
							case 3:
								ySpeed -= yAccel;
								break;
						}
					 }
					 if(Canvas.keyboardKeyState(KeyEvent.VK_S))
					{
						switch(directions[3]){
							case 0:
								ySpeed += yAccel;
								break;
							case 1:
								 xSpeed -= xAccel;
								break;
							case 2:
								xSpeed += xAccel;									
								break;
							case 3:
								ySpeed -= yAccel;
								break;
							}
					}
					
					//Have the ball slow down inherently (friction) 
					if(xSpeed < 0) 
						xSpeed += stopSpeed;
					else if (xSpeed > 0)
						xSpeed -= stopSpeed;
					if(ySpeed < 0) 
						ySpeed += stopSpeed;
					else if (ySpeed > 0)
						ySpeed -= stopSpeed;
				
			//Update the ball's Cartesian Coordinates
	        x += xSpeed;
	        y += ySpeed;
	        
	        //The ball stops when it hits the edges of the screen
	        //(in action stages, the ball would crash)
	        if(x>855){ 
	        	x = 855;
	        }
	        else if (x<0){
	        	x=0;
	        }
	        if(y>531){
	        	y = 531;
	        }
	        else if (y<0){
	        	y = 0;
	        }
		}
	}
	
	/**
	 * Resets the Ball and its variables
	 */
	public void Reset(){
		success = false;
		isCrashed = false;
		stageSuccessTimer = -1;
		x = 450;
		y = 300;
		xSpeed = 0;
		ySpeed = 0;
	}
	
	/**
	 * Draw the Ball on the Frame
	 * @param g
	 */
	public void Draw(Graphics2D g){
	       g.setColor(Color.BLACK);
	       g.drawString("Ballular coordinates: " + x + " : " + y, 5, 15);
			g.drawImage(ball,x,y,null);
	}
	
	/**
	 * Draw the Success Ball
	 * @param g
	 */
	public void drawWin(Graphics2D g){
		g.drawImage(ball_win,x,y,null);      		
				   g.setColor(Color.WHITE);
			       g.drawString("Level Passed!" +" : " , 5, 15);
		
	}
	
	/**
	 * Draw the Crashed Ball
	 * @param g
	 */
	public void drawLose(Graphics2D g){		
			g.drawImage(ball_lose,x,y,null);			
		}
		
	}
	
	

