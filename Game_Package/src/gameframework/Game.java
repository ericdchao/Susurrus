package gameframework;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import java.util.Random;

/***
 * Actual Game object which
 * holds instances of the ball, goal, and mines, 
 * creating, updating, and drawing them
 * 
 * @author Eric Chao
 */

public class Game {
	
	/**
	 * Each game has a specified level
	 */
	final int level;
	
	/**
	 * Public Ball object, so Framework can change Ball Schema
	 */
	public Ball ball_1;
	
	/**
	 * Goal and Mines that determine level success
	 */
	private Goal goal;
	private Mine[] Mines;
	
	public int tutorialStage;
	public int actionStage;
	
	/**
	 * Stages of the Game: Either tutorial preparation (to learn each schema)
	 * or the action stages
	 */
	public static enum GameStage{PREPARATION, ACTION};
	public static GameStage gameStage;
	
	/**
	 * Used to determine the time taken to beat the game
	 */
	public long gameStartTime;
	public long actionStartTime;
	
	/**
	 *  Flags for completing each stage
	 */
	public boolean stage1Success;
	public boolean stage2Success;
	public boolean stage3Success;
	public boolean stage4Success;
	public boolean stage5Success;
	
	
	/**
	 * Images needed for the game itself
	 */
	private BufferedImage tutorialHeading1;
	private BufferedImage tutorialHeading2;
	private BufferedImage tutorialHeading3;
	private BufferedImage tutorialHeading4;
	private BufferedImage tutorialHeading5;
	private BufferedImage ballSuccess;
	private BufferedImage ballFailure;
	

    public Game(int level)
    {
        this.level = level;
    	Framework.gameState = Framework.GameState.GAME_CONTENT_LOADING;
        Thread threadForInitGame = new Thread() {
            @Override
            public void run(){
                // Sets variables and objects for the game.
                Initialize();
                // Load game files (images, sounds, ...)
                LoadContent();                
                Framework.gameState = Framework.GameState.PLAYING;
            }
        };
        threadForInitGame.start();
    }
    
    
   /**
     * Set variables and objects for the game.
     */
    private void Initialize()
    {
    	//Create a new ball at the center of the screen;
    	ball_1 = new Ball(450,300,this.level); 
    	ball_1.currentScheme = 1;
    	goal = new Goal(level,actionStage);	
    	this.gameStage = Game.GameStage.PREPARATION;
    	tutorialStage = 1;
    	actionStage = 0;
    	stage1Success = false;
    	stage2Success = false;
    	stage3Success = false;
    	stage4Success = false;
    	stage5Success = false;
    }
    
    /**
     * Load game files - images and sounds
     */
    private void LoadContent()
    {
    	try {
			ballSuccess = ImageIO.read(this.getClass().getResource("/gameframework/resources/images/Ball_success.png"));
			ballFailure = ImageIO.read(this.getClass().getResource("/gameframework/resources/images/Ball_failure.png"));
			tutorialHeading1 = ImageIO.read(this.getClass().getResource("/gameframework/resources/images/Scheme_1.png"));
			tutorialHeading2 = ImageIO.read(this.getClass().getResource("/gameframework/resources/images/Scheme_2.png"));
			tutorialHeading3 = ImageIO.read(this.getClass().getResource("/gameframework/resources/images/Scheme_3.png"));
			tutorialHeading4 = ImageIO.read(this.getClass().getResource("/gameframework/resources/images/Scheme_4.png"));
			tutorialHeading5 = ImageIO.read(this.getClass().getResource("/gameframework/resources/images/Scheme_5.png"));	
		} catch (IOException e) {
			 Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, e);
		} 	
    }   
    
    /**
     * Restart game - reset some variables.
     */
    public void RestartGame()
    {
    	 ball_1.Reset();
    	 tutorialStage = 0;
    	 changeActionStage(0);
    	 gameStage= GameStage.PREPARATION;
    	 gameStartTime = 0;
    	 actionStartTime = 0;
    	 stage1Success = false;
    	 stage2Success = false;
    	 stage3Success = false;
    	 stage4Success = false;
    	 stage5Success = false;
    }
    
    /**
     * Update game logic. Called from Framework.java
     * 
     * @param gameTime gameTime of the game.
     * @param mousePosition current mouse position.
     */
    public void UpdateGame(long gameTime, Point mousePosition)
    {
    	//Update the Ball and Goal 
    	ball_1.Update();
    	goal.Update();
    	
    	if(this.gameStage == GameStage.ACTION){
        	for(int i = 0; i < Mines.length; i++){
        		//Update the Mines
        		Mines[i].Update();
        	}
        	//Check if the ball successfully reached the goal
        	if( (ball_1.y + 20 > goal.y - goal.goalImageHeight) && (ball_1.y < goal.y)){     	 
        		if((ball_1.x + 20> goal.x) && (ball_1.x < goal.x + goal.goalImageWidth ))
                {        			
        			ball_1.success = true;		
        			switch(actionStage){
        				case 1:
        					stage1Success = true;
        					break;
        				case 2:
        					stage2Success = true;
        					break;
        				case 3:
        					stage3Success = true;
        					break;
        				}
                	}
        		}    
        	//Did the ball hit the sides of the screen?
        	if(ball_1.y > 530|| ball_1.y < 5|| ball_1.x > 854|| ball_1.x < 5){
        		ball_1.isCrashed = true;
        	}
        	else{
        		//Did the ball hit a mine?
        		for(int i = 0 ; i < Mines.length ; i++){
        			if(isInRange(Mines[i].x,ball_1.x+35,ball_1.x,Mines[i].x+Mines[i].mineImageWidth)&&
        			isInRange(Mines[i].y,ball_1.y+35,ball_1.y,Mines[i].y+Mines[i].mineImageHeight)){
        				ball_1.isCrashed = true;
        				break;
        			}
        		}
        	}
        }
    }
    
    /**
     * Draw the game to the screen.
     * 
     * @param g2d Graphics2D
     * @param mousePosition current mouse position.
     */
    public void DrawGame(Graphics2D g, Point mousePosition,int frameWidth,int frameHeight)
    {
    	if(this.gameStage == GameStage.PREPARATION){
    		g.setColor(Color.WHITE);
            g.fillRect(0,0,frameWidth,frameHeight);
            ball_1.Draw(g);
            switch(tutorialStage){
    			case 1:
    				g.drawImage(tutorialHeading1,50,50,null);
    				break;
    			case 2:
    				g.drawImage(tutorialHeading2,50, 50,null);
    				break;
    			case 3:
    				g.drawImage(tutorialHeading3,50, 50,null);
    				break;
    			case 4:
    				g.drawImage(tutorialHeading4,50, 50,null);
    				break;
    			case 5:
    				g.drawImage(tutorialHeading5,50, 50,null);
    				break;
            	}    	
            }
    	 else if(this.gameStage == GameStage.ACTION){
    		 if(ball_1.success == true){
    			 ball_1.drawWin(g);
    			
    		 }
    		 else if(ball_1.isCrashed == true){
    			 ball_1.drawLose(g);
    		 }
    		 else{
        		 g.setColor(Color.WHITE);
                 g.fillRect(0,0,frameWidth,frameHeight);	 
                 //Draw the current Schema
                 switch(ball_1.currentScheme+1){
                 	case 1:
 						g.drawImage(tutorialHeading1,50,50,null);
 						break;
 					case 2:
 						g.drawImage(tutorialHeading2,50, 50,null);
 						break;
 					case 3:
 						g.drawImage(tutorialHeading3,50, 50,null);
 						break;
 					case 4:
 						g.drawImage(tutorialHeading4,50, 50,null);
 						break;
 					case 5:
 						g.drawImage(tutorialHeading5,50, 50,null);
 						break;
    		 		}
        //Draw the Mines, Ball, and Goal
                 for(int i = 0; i<Mines.length; i++){
                 	Mines[i].Draw(g);
                 }
        ball_1.Draw(g);
    	goal.Draw(g);
    		 	}
    	 	}	
    	}
    
    /**
     * This method changes between the action stages of the action portion of the game,
     * switching the ball schema, setting the goal parameters, and generating 
     * the mines based on the level of the game
     * 
     */
    public void changeActionStage(int stage){
    	this.actionStage = stage;
    	goal.actionStage = stage;
    	Random randomGen = new Random();
    	ball_1.currentScheme = randomGen.nextInt(ball_1.Schemes.length);
    	switch(actionStage){
    	case 1:
    		//Generate random number of mines (at least one)
			int random1 = randomGen.nextInt(this.level)+ 5;
			Mines = new Mine[random1];
			for(int i = 0; i<random1; i++){
				int randomSize = randomGen.nextInt(3) + 1;
				Mines[i] = new Mine(randomSize, actionStage, 0, 0, 0, 0);
			}
			//Set new goal width and height
    		goal.goalImageWidth = goal.goalImage.getWidth();
    		goal.goalImageHeight = goal.goalImage.getHeight();
    		break;
    	case 2:
    		int random2 = randomGen.nextInt(this.level)+ 3;
			Mines = new Mine[random2];
			for(int i = 0; i<random2; i++){
				int randomSize = randomGen.nextInt(3) + 1;
				Mines[i] = new Mine(randomSize, actionStage, randomGen.nextInt(5), randomGen.nextInt(5), 0, 0);
			}
    		goal.goalImageWidth = goal.goalImage_2.getWidth();
    		goal.goalImageHeight = goal.goalImage_2.getHeight();
    		goal.xSpeed = 19;
            break;
    	case 3:        	
    		int random3 = randomGen.nextInt(this.level)+ 3;
			Mines = new Mine[random3];
			for(int i = 0; i<random3; i++){
				int randomSize = randomGen.nextInt(3) + 1;
				Mines[i] = new Mine(randomSize, actionStage, randomGen.nextInt(4), randomGen.nextInt(4), Math.random()/10, Math.random()/10);
			}
    		goal.goalImageWidth = goal.goalImage_3.getWidth();
    		goal.goalImageHeight = goal.goalImage_3.getHeight();
    		goal.xSpeed = 0;
    		goal.ySpeed = 3;
            break;
    	}
    
    }
    
    /**
     * Double Inequality Method
     * 
     * No need for longs because there is no gameTime comparison (takes places in Framework)
     */
    public boolean isInRange(int a, int b, int c, int d){
    	if(a<b && c < d) return true;
    	return false;
    }
    
}
    

