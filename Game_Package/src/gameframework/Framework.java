package gameframework;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import java.util.Random;

/***
 * Game Framework that controls Game.java, creating, updating, and drawing it on the screen
 * @author Eric Chao
 */

public class Framework extends Canvas {
    
    /**
     * Width of the frame.
     */
    public static int frameWidth;
    /**
     * Height of the frame.
     */
    public static int frameHeight;

    /**
     * Time of one second in nanoseconds.
     * 1 second = 1 000 000 000 nanoseconds
     */
    public static final long secInNanosec = 1000000000L;
    
    /**
     * Time of one millisecond in nanoseconds.
     * 1 millisecond = 1 000 000 nanoseconds
     */
    public static final long milisecInNanosec = 1000000L;
    
    /**
     * FPS - Frames per second
     * How many times per second the game should update?
     */
    private final int GAME_FPS = 60;
    /**
     * Pause between updates. It is in nanoseconds.
     */
    private final long GAME_UPDATE_PERIOD = secInNanosec / GAME_FPS;
    
    /**
     * Possible states of the game
     */
    public static enum GameState{STARTING, VISUALIZING, GAME_CONTENT_LOADING, TUTORIAL, TUTORIAL_2, MAIN_MENU, OPTIONS, PLAYING, GAMEOVER, DESTROYED}
    /**
     * Current state of the game
     */
    public static GameState gameState;
    
    /**
     * Elapsed game time in nanoseconds.
     */
    private long gameTime;
    /**
     *  It is used for calculating elapsed time.
     */
    private long lastTime;
    
    /**
     *  The actual game
     */
    private Game game;
   
    /**
     * Random number generator
     */
	private Random randomGen = new Random();
	int random;
	int counter;
    
    /**
     * Buffered images for Navigation
     */
    private BufferedImage gameTitle;
    private BufferedImage gameMenu;
    private BufferedImage tutorialImage;
    private BufferedImage tutorialImage2;
    private BufferedImage nextTutorial;
    private BufferedImage mainMenuReturn;
    
    public Framework ()
    {
        super();
        
        gameState = GameState.VISUALIZING;
        
        //We start game in new thread.
        Thread gameThread = new Thread() {
            @Override
            public void run(){
                GameLoop();
            }
        };
        gameThread.start();
    }
    
    
   /**
     * Set variables and objects.
     * This method is intended to set the variables and objects for this class, variables and objects for the actual game can be set in Game.java.
     */
    private void Initialize()
    {
    	frameWidth = 900;
    	frameHeight = 600;
    	counter = 0;
    }
    
    /**
     * Load files - images, sounds, ...
     * This method is intended to load files for this class, files for the actual game can be loaded in Game.java.
     */
    private void LoadContent()
    {
    	try {
			gameTitle = ImageIO.read(this.getClass().getResource("/gameframework/resources/images/Game_Title.png"));
			gameMenu = ImageIO.read(this.getClass().getResource("/gameframework/resources/images/Game_Menu.png"));
			tutorialImage = ImageIO.read(this.getClass().getResource("/gameframework/resources/images/Tutorial.png"));
			mainMenuReturn = ImageIO.read(this.getClass().getResource("/gameframework/resources/images/mainMenuReturn.png"));
			nextTutorial = ImageIO.read(this.getClass().getResource("/gameframework/resources/images/next.png"));		
			tutorialImage2 = ImageIO.read(this.getClass().getResource("/gameframework/resources/images/Tutorial_2.png"));
		} catch (IOException e) {
			 Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, e);
		}
    }
    
    /**
     * In specific intervals of time (GAME_UPDATE_PERIOD) the game/logic is updated and then the game is drawn on the screen.
     */
    private void GameLoop()
    {
        // two variables are used in VISUALIZING state of the game. We used them to wait some time so that we get correct frame/window resolution.
        long visualizingTime = 0, lastVisualizingTime = System.nanoTime();    
        // variables are used for calculating the time that defines for how long we should put threat to sleep to meet the GAME_FPS.
        long beginTime, timeTaken, timeLeft;
        while(true)
        {
            beginTime = System.nanoTime();
            
            switch (gameState)
            {
                case PLAYING:
                    gameTime += System.nanoTime() - lastTime;
                    //the first increments of 10 seconds per game are going to be dedicated to the tutorial preparation stages
                    if(isInRange(game.gameStartTime,gameTime,game.gameStartTime + 10*secInNanosec)){
                    	game.tutorialStage = 1;
                    	game.ball_1.currentScheme = game.tutorialStage-1;
                    }
                    else if(isInRange(game.gameStartTime+10*secInNanosec,gameTime,game.gameStartTime+20*secInNanosec)){
                    	game.tutorialStage = 2;
                    	game.ball_1.currentScheme = game.tutorialStage-1;
                    }
                    else if(game.level > 1 && isInRange(game.gameStartTime+20*secInNanosec,gameTime,game.gameStartTime+30*secInNanosec)){
                        	   game.tutorialStage = 3;
                        	   game.ball_1.currentScheme = game.tutorialStage-1;
                    }
                    else if(game.level == 3 && isInRange(game.gameStartTime+30*secInNanosec,gameTime,game.gameStartTime+40*secInNanosec)){            
                            	game.tutorialStage = 4;
                            	game.ball_1.currentScheme = game.tutorialStage-1;                  	
                    }
                    else{
                    	//Game is now leaving scheme tutorials, and entering action stages
                    	if(game.gameStage == game.gameStage.PREPARATION){
                    		game.tutorialStage = 0;
                    		game.ball_1.Reset();
                    		//Start the action stages, and the timer! 
                    		game.changeActionStage(1);
                    		game.gameStage = game.gameStage.ACTION;
                    		game.actionStartTime = gameTime;
                    	}
                    	if(game.stage1Success && game.actionStage == 1){//Change to stage 2
                    		if(game.ball_1.stageSuccessTimer == -1){
                    			game.ball_1.stageSuccessTimer = gameTime;
                    		}
                    		else if(isInRange(game.ball_1.stageSuccessTimer, gameTime, game.ball_1.stageSuccessTimer + 3*secInNanosec)){
                    			repaint();
                    			}
                    		else if(gameTime > game.ball_1.stageSuccessTimer + 3*secInNanosec){
                    			game.ball_1.stageSuccessTimer = -1;
                    			game.ball_1.Reset();
                        		game.changeActionStage(2);
                    		}
                    	}
                    	else if(game.stage2Success && game.actionStage == 2){ //Change to stage 3
                    		if(game.ball_1.stageSuccessTimer == -1){
                    			game.ball_1.stageSuccessTimer = gameTime;
                    		}
                    		else if(isInRange(game.ball_1.stageSuccessTimer, gameTime, game.ball_1.stageSuccessTimer + 3*secInNanosec)){
                    			repaint();
                    			}
                    		else if(gameTime > game.ball_1.stageSuccessTimer + 3*secInNanosec){
                    			game.ball_1.stageSuccessTimer = -1;
                    			game.ball_1.Reset();
                        		game.changeActionStage(3);
                    		}
                    	}
                    	else if(game.stage3Success && game.actionStage == 3){ //Completed level!
                    		if(game.ball_1.stageSuccessTimer == -1){
                    			game.ball_1.stageSuccessTimer = gameTime;
                    		}
                    		else if(isInRange(game.ball_1.stageSuccessTimer, gameTime, game.ball_1.stageSuccessTimer + 3*secInNanosec)){
                    			repaint();
                    			}
                    		else if(gameTime > game.ball_1.stageSuccessTimer){
                    			game.ball_1.stageSuccessTimer = -1;
                    			game.ball_1.Reset();
                    			gameState = GameState.GAMEOVER;
                    		}
                    	}
                    	else if(game.ball_1.isCrashed){
                    		if(game.ball_1.stageSuccessTimer == -1){
                    			game.ball_1.stageSuccessTimer = gameTime;
                    		}
                    		else if(isInRange(game.ball_1.stageSuccessTimer, gameTime, game.ball_1.stageSuccessTimer + 3*secInNanosec)){
                    			repaint();
                    			}
                    		else if(gameTime > game.ball_1.stageSuccessTimer){
                    			game.ball_1.stageSuccessTimer = -1;
                    			game.ball_1.Reset();	
                    		}
                    	}
                    //Switch the Schema of the ball in increasingly frequent intervals (per actionstage)
                    else if(game.actionStage != 0){ 
                    	switch(game.actionStage){
                    	case 1:
                    		if(counter > 450){
                    			counter = 0;
                    			game.ball_1.currentScheme = randomGen.nextInt(game.ball_1.Schemes.length);
                    			}
                    		break;
                    	case 2:
                    		if(counter > 250){
                        		counter = 0;
                        		game.ball_1.currentScheme = randomGen.nextInt(game.ball_1.Schemes.length);
                        	}
                    		break;
                    	case 3:
                    		if(counter > 150){
                        		counter = 0;
                        		game.ball_1.currentScheme = randomGen.nextInt(game.ball_1.Schemes.length);
                        	}
                    		break;
                    	}
                    	counter++;
                    	}
                    } 	
                    game.UpdateGame(gameTime, mousePosition());
                    lastTime = System.nanoTime();
                break;
                case STARTING:
                    // Sets variables and objects.
                    Initialize();
                    // Load files - images, sounds, ...
                    LoadContent();
                    // When all things that are called above finished, we change game status to main menu.
                    gameState = GameState.MAIN_MENU;
                break;
                case VISUALIZING:
                    // On Ubuntu OS (when I tested on my old computer) this.getWidth() method doesn't return the correct value immediately (eg. for frame that should be 800px width, returns 0 than 790 and at last 798px). 
                    // So we wait one second for the window/frame to be set to its correct size. Just in case we
                    // also insert 'this.getWidth() > 1' condition in case when the window/frame size wasn't set in time,
                    // so that we although get approximately size.
                    if(this.getWidth() > 1 && visualizingTime > secInNanosec)
                    {
                        frameWidth = this.getWidth();
                        frameHeight = this.getHeight();

                        // When we get size of frame we change status.
                        gameState = GameState.STARTING;
                    }
                    else
                    {
                        visualizingTime += System.nanoTime() - lastVisualizingTime;
                        lastVisualizingTime = System.nanoTime();
                    }
                break;
            }
            // Repaint the screen.
            repaint();
            // Here we calculate the time that defines for how long we should put threat to sleep to meet the GAME_FPS.
            timeTaken = System.nanoTime() - beginTime;
            timeLeft = (GAME_UPDATE_PERIOD - timeTaken) / milisecInNanosec; // In milliseconds
            // If the time is less than 10 milliseconds, then we will put thread to sleep for 10 millisecond so that some other thread can do some work.
            if (timeLeft < 10) 
                timeLeft = 10; //set a minimum
            try {
                 //Provides the necessary delay and also yields control so that other thread can do work.
                 Thread.sleep(timeLeft);
            } catch (InterruptedException ex) { }
        }
    }
    
    /**
     * Draw the game to the screen. It is called through repaint() method in GameLoop() method.
     */
    @Override
    public void Draw(Graphics2D g)
    {
        switch (gameState)
        {
            case PLAYING:
                game.DrawGame(g, mousePosition(),frameWidth,frameHeight);
            break;
            case GAMEOVER:
                g.setColor(Color.WHITE);
                g.fillRect(0,0,frameWidth,frameHeight);
                g.setColor(Color.BLACK);
                g.drawString("Your final time was " + ((gameTime-game.actionStartTime)/secInNanosec) + " seconds \n" + " Press R to Retry Level, and Enter to return to Main Menu", frameWidth / 2 - 140, frameHeight / 2 - 30);
               
            break;
            case MAIN_MENU:
                g.setColor(Color.WHITE);
                g.fillRect(0,0,frameWidth,frameHeight);
                g.setColor(Color.BLACK);
                g.drawImage(gameTitle, frameWidth/11, frameHeight/13, null);
                g.drawImage(gameMenu, frameWidth*11/20, frameHeight*30/100, null);
                g.drawString(" Coordinates of Mouse: " + mousePosition().toString(),17, 560);
            break;
            case TUTORIAL:
            	g.setColor(Color.WHITE);
            	g.fillRect(0,0, frameWidth,frameHeight);
            	g.drawImage(tutorialImage, frameWidth/4	,frameHeight/5, null);
            	g.drawImage(mainMenuReturn, 17,500,null);
            	g.drawImage(nextTutorial, 750,500,null);
            	g.setColor(Color.BLACK);
                g.drawString(" Coordinates of Mouse: " + mousePosition().toString(),17, 560);// For Designing Purposes
            	break;
            case TUTORIAL_2:
            	g.setColor(Color.WHITE);
            	g.fillRect(0,0, frameWidth,frameHeight);
            	g.drawImage(tutorialImage2, frameWidth/5,frameHeight/5, null);
            	g.drawImage(mainMenuReturn, 17,500,null);
            	g.setColor(Color.BLACK);
                g.drawString(" Coordinates of Mouse: " + mousePosition().toString(),17, 560);// For Designing Purposes
            	break;
        }
    }
    
    
    /**
     * Starts new game.
     */
    private void newGame(int level)
    {
        // We set gameTime to zero and lastTime to current time for later calculations.
        gameTime = 0;
        lastTime = System.nanoTime();
        game = new Game(level);
        game.gameStartTime = gameTime;
    }
    
    /**
     *  Restart game - reset game time and call RestartGame() method of game object so that reset some variables.
     */
    private void restartGame()
    {
        // We set gameTime to zero and lastTime to current time for later calculations.
        gameTime = 0;
        lastTime = System.nanoTime();        
        game.RestartGame();
        // We change game status so that the game can start.
        gameState = GameState.PLAYING;
       
    }
    
    
    /**
     * Returns the position of the mouse pointer in game frame/window.
     * If mouse position is null than this method return 0,0 coordinate.
     * 
     * @return Point of mouse coordinates.
     */
    private Point mousePosition()
    {
        try
        {
            Point mp = this.getMousePosition();
            
            if(mp != null)
                return this.getMousePosition();
            else
                return new Point(0, 0);
        }
        catch (Exception e)
        {
            return new Point(0, 0);
        }
    }
    
    
    /**
     * This method is called when keyboard key is released.
     * 
     * @param e KeyEvent
     */
    @Override
    public void keyReleasedFramework(KeyEvent e)
    {
    	//Pressed ESC
    	if (e.getKeyCode() == KeyEvent.VK_ESCAPE) System.exit(0);
    	switch(gameState)
         {
             case GAMEOVER:
                 if(e.getKeyCode() == KeyEvent.VK_R)
                     restartGame();
                 if(e.getKeyCode() == KeyEvent.VK_ENTER)
                	 this.gameState = GameState.MAIN_MENU;
             break;
             case MAIN_MENU:  
             break;
         }
    }
    
    /**
     * This method is called when mouse button is clicked.
     * 
     * @param e MouseEvent
     */
    @Override
    public void mouseClicked(MouseEvent e)
    {
    	switch(gameState)
    	{
    		case MAIN_MENU:
    			if(isInRange(492,e.getX(),733) && isInRange(180,e.getY(),208)) gameState = GameState.TUTORIAL;
    			//Level 1
    			if(isInRange(495,e.getX(),631) && isInRange(280,e.getY(),308)){
    				newGame(1);
    			}
    			//Level 2
    			else if(isInRange(495,e.getX(),631) && isInRange(381,e.getY(),408)){
    				newGame(2);
    			}
    			//Level 3
    			else if(isInRange(495,e.getX(),631) && isInRange(500,e.getY(),510)){
    				newGame(3);
    			}
    			
    		break;
    		case TUTORIAL:
    			//Back to Main Menu
    			if(isInRange(18,e.getX(),177)&& isInRange(502,e.getY(),515)) gameState = GameState.MAIN_MENU;
    			//Next Tutorial Screen
        		else if(isInRange(751,e.getX(),789)&& isInRange(500,e.getY(),513)) gameState = GameState.TUTORIAL_2;
    		break;
    		case TUTORIAL_2:
    			//Back to Main Menu
    			if(isInRange(18,e.getX(),177)&& isInRange(502,e.getY(),515)) gameState = GameState.MAIN_MENU;
    	}	
    }
    
  //Chained Inequality Method for a<b<c
    public boolean isInRange(long a, long b, long c){
    	if(a<b && b < c) return true;
    	return false;
    }
    
}
