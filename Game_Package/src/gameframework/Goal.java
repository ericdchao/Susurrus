package gameframework;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Goal Object
 * 
 * The goals get smaller with each action stage, increasing the difficulty
 * 
 * @author Eric Chao
 */

public class Goal {
	
	/**
	 * Coordinates of the Goal
	 */
	public int x;
	public int y;
	
	/**
	 * Level of the game containing the goal
	 */
	public int level;
	
	/**
	 * Buffered Images for each goal type
	 */
	public BufferedImage goalImage;
	public BufferedImage goalImage_2;
	public BufferedImage goalImage_3;
	public int goalImageWidth;
	public int goalImageHeight;
	
	/**
	 * Parameter of the goal to determine its trajectory
	 */
	public double xSpeed;
	public double ySpeed;
	public double xAccel;
	public double yAccel;
	public int stopSpeed;

	/**
	 * Action Stage of the game containing the goal
	 */
	public int actionStage;

	
	
	public Goal(int level, int actionStage) {
		this.level = level;
		this.actionStage = actionStage;
		Initialize();
		LoadContent();
		}	
	
    	/**
    	 * 
    	 * Initializes variables
    	 */
	    private void Initialize()
	    {   
	        // X coordinate of the landing area is at 46% frame width.
	        x = (int)(Framework.frameWidth * 0.46);
	        // Y coordinate of the landing area is at 86% frame height.
	        y = (int)(Framework.frameHeight * 0.88);
	        
	        xSpeed = 0;
	        xAccel = 0.5;
	        ySpeed = 0;
	        yAccel = 0.3;
	    }
	    
	    /**
	     * Load some images
	     */
	    private void LoadContent()
	    {
	        try
	        {
	        		goalImage = ImageIO.read(this.getClass().getResource("/gameframework/resources/images/goal.png"));
	        		goalImageWidth = goalImage.getWidth();
	        		goalImageHeight = goalImage.getHeight();
	        		goalImage_2 = ImageIO.read(this.getClass().getResource("/gameframework/resources/images/goal_2.png"));
	        		goalImage_3 = ImageIO.read(this.getClass().getResource("/gameframework/resources/images/goal_3.png"));         
	        }
	        catch (IOException ex) {
	            Logger.getLogger(Goal.class.getName()).log(Level.SEVERE, null, ex);
	        }
	    }
	    
	    /**
	     * Update the goal's coordinates depending on the action Stage of the game
	     * 
	     */
	    public void Update(){
	    	if(actionStage == 2){
	    		// Ensure that the goal always accelerates back onto the Frame
	    		if(x>Framework.frameWidth/2){
	    		 xSpeed -= xAccel;
	    		}
	    		else if(x<Framework.frameWidth/2){
	    			xSpeed +=xAccel;
	    		}
	    	}
	    	if(actionStage == 3){
	    		if(x>Framework.frameWidth/2){
		    		 xSpeed -= xAccel;
		    		}
		    		else if(x<Framework.frameWidth/2){
		    			xSpeed +=xAccel;
		    		}
	    		if(y>Framework.frameHeight/2){
		    		 ySpeed -= yAccel;
		    		}
		    		else if(y<Framework.frameWidth/2){
		    			ySpeed +=yAccel;
		    		}
	    		//Prevent goal from hitting starting ball position
	    		if(( x>250&&x<600)&&(y>200 &&y<350)){
	    			xSpeed = -xSpeed;
	    		}
	    	}
	    	//Update the Cartesian coordinates
	    	x+=xSpeed;
	    	y+=ySpeed;
	    }
	    
	    /**
	     * Draw the goal
	     * @param g2d
	     */
	    public void Draw(Graphics2D g2d)
	    {	 		
	    	switch(actionStage){
        	case 1:	        		
        		g2d.drawImage(goalImage, x, y, null);
        		break;
        	case 2:
        		g2d.drawImage(goalImage_2, x, y, null);
	            break;
        	case 3:        	
        		g2d.drawImage(goalImage_3, x, y, null);
	            break;
        	}
	    }

}