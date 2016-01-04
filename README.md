# Susurrus
A basic Java Game, utilizing the Java JFrame and Draw Libraries


Introduction to the game:
  
Susurrus is a game that uses the W, A, S, and D keys to move a ball into a goal. The ball must avoid the sides of the screens and any Mines that are on the screen. The game generates random "schemes" that flip the directions that the W, A, S, and D keys take the ball. When a game is started, the player is first given 10 seconds per scheme to explore the new directions, known as the preparation stages. Higher levels will introduce more schemes, and thus a longer prearation period. After all of the schemes are presented, the game enters its action stage, and a randomly selected scheme will be used at any given time to control the ball. The player must now maneuver the ball into the goal while avoiding the mines and walls. Hitting either of these will cause a three second penalty delay, and return the ball to its starting position. There are three distinct action stages, that occur in sequential order after each one is defeated. These stages increase in difficulty, and the mines and goal both obtain movement. The schemes are given to switch during the action stages, and switch with increasing frequency as the stages progress. The game is completed at the conclusion of the third action stage, and the time is displayed on the screen.
  
The game itself was created with the intention of training short-term memory. There are however, two ways to play the game: one way is to study the patterns in the preparation stage of the game, and then respond to the displayed scheme during the action stages to expertly control the ball. This way calls for rapid memorization and recall of schemes in order to ascertain which direction the ball will travel in. This becomes increasingly important in higher levels, where there are more schemes. The second way to play the game is to ignore the preparation stages, and try to adapt to the new schema as they appear. This method is much more haphazard, and trains quick-reflexes and instinctive muscle memory per scheme. The goal of the game is to minimize the time taken to complete each level.

  This game was created as a brief foray into creating a Java game from the ground up. The game runs entirely on a while(true) loop scenario, and sets flags throughout the tracked game time to manage certain scenarios. This method is outdated and memory-inefficient, and the new Java.Swing.Timer class should be used instead. However, this game is simple enough to not require this.
  

Note that the UpdateGame and DrawGame methods in Game.java still take in a Point mousePosition that is unused. For future development, the mouse position could be used to add in further features for the game (a gun shooter, or a shield, or even another ball, etc.).

All of the images were created by the author (Eric Chao) in Inkscape as PNG images. The template for this game was taken from www.game-tutorial.net, and there is no copyright infringement intended. All other logic and code was written by the author.


File Architecture:

Susurrus.jar is the executable java file that automatically runs the game (Off of the Run Configurations in Window.java)

Game_Package contains the important java files for the game:

  bin/gameframework
      resources
        images
          contains all images for the game
        sounds
          empty folder for sounds
    Ball.class
      Ball Object 
    Canvas.class
      Canvas for Java JFrame
    Framework.class	
      Framework for Game
    Game.class
      Game Object
    Goal.class	
      Goal Object
    Mine.class	
      Mine Object
    Window.class
      Window for Java JFrame


