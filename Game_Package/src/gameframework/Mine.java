package gameframework;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;


/***
 * Mines Object
 * 
 * @author Eric Chao
 */

public class Mine{
	
	/**
	 * Coordinates of the mine and size
	 */
	public int x;
	public int y;
	public int size;
	
	/**
	 * Action stage of the game containing the mines
	 */
	public int actionStage;
	
	/**
	 * Buffered Images of different Mine sizes
	 */
	public BufferedImage spikeSmall;
	public BufferedImage spikeMedium;
	public BufferedImage spikeLarge;
	public int mineImageWidth;
	public int mineImageHeight;
	
	/**
	 * Parameters of the mines trajectory
	 */
	public double xSpeed;
	public double ySpeed;
	public double xAccel;
	public double yAccel;
	public int stopSpeed;


	
	public Mine(int size, int actionStage, int startingXSpeed, int startingYSpeed, double xAccel, double yAccel) {
		this.size = size;
		this.actionStage = actionStage;
		this.xSpeed = startingXSpeed;
		this.ySpeed = startingYSpeed;
		this.xAccel = xAccel;
		this.yAccel = yAccel;
		Initialize();
		LoadContent();
		}	
	
    	/**
    	 * Initialize the position of the mines
    	 */
	    private void Initialize()
	    {   
	    	/**Generate random starting positions for the mines
	    	 * 
	    	 * Note that this may generate some mines that will be stuck on the border 
	    	 * of the Window Frame, but should not coincide with the starting ball or goal position
	    	 */
	    	
	        x = (int)(Framework.frameWidth * Math.random());
	        y = (int)(Framework.frameHeight * Math.random());
	       
	        //Ensure that the mines are not too close to the ball's starting position
	        while(( x>250&&x<600)||(y>200 &&y<350)){
	        	x = (int)(Framework.frameWidth * Math.random());
	  	        y = (int)(Framework.frameHeight * Math.random());
	        }
	       
	    }
	    
	    /**
	     * Load the images of the Mine based on its size
	     */
	    private void LoadContent()
	    {
	        try
	        {
	        	switch(size){
	        	case 1:
	        		spikeSmall = ImageIO.read(this.getClass().getResource("/gameframework/resources/images/spike_small.png"));
	        		mineImageWidth = spikeSmall.getWidth();
	        		mineImageHeight = spikeSmall.getHeight();
	        		break;
	        	case 2:
	        		spikeMedium = ImageIO.read(this.getClass().getResource("/gameframework/resources/images/medium_spike.png"));
	        		mineImageWidth = spikeMedium.getWidth();
	        		mineImageHeight = spikeMedium.getHeight();
	        		break;
	        	case 3:
	        		spikeLarge = ImageIO.read(this.getClass().getResource("/gameframework/resources/images/spike_large.png"));
	        		mineImageWidth = spikeLarge.getWidth();
	        		mineImageHeight = spikeLarge.getHeight();
	        		break;
	        	}
		         
	        }
	        catch (IOException ex) {
	            Logger.getLogger(Goal.class.getName()).log(Level.SEVERE, null, ex);
	        }
	    }
	    
	    /**
	     * Update the Mine's position
	     */
	    public void Update(){
	    	//Ensure that it stays on screen, bouncing off of walls
	    	if(x<0 || x + mineImageWidth>900){
	    		xSpeed = -xSpeed;
	    	}
	    	if(y<0 || y + mineImageHeight>600){
	    		ySpeed = -ySpeed;
	    	}
	    	
	    	
	    	if(actionStage == 2){
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
	    	}
	    	
	    	//Update the Mine's Cartesian coordinates
	    	x+=xSpeed;
	    	y+=ySpeed;
	    }
	    
	    /**
	     * Draw the Mine on the Frame
	     * @param g2d
	     */
	    public void Draw(Graphics2D g2d)
	    {
	    	switch(this.size){
        	case 1:
        		g2d.drawImage(spikeSmall, x, y, null);
        		break;
        	case 2:
        		g2d.drawImage(spikeMedium, x, y, null);
        		break;
        	case 3:
        		g2d.drawImage(spikeLarge, x, y, null);
        		break;
        	}		
	    }
	    
}