import java.awt.*;
import java.util.*;
import tester.*;
import javalib.funworld.*;
import javalib.colors.*;
import javalib.worldimages.*;

class Platform{
    
    Posn center;
    int speed;
    int width;
    String dir;
    
    Platform (Posn center, int speed,int width,String dir){
	this.center = center;
	this.speed = speed;
	this.width = width;
	this.dir = dir;
    }
    // Generates a new image representing a platform
    public WorldImage platformImage(){
	return new RectangleImage(this.center, this.width, 20, new Yellow());
    }
    // Moves a platform right or left depending on value of dir
    // If the Platform has come into contact with either side of the game
    // arena it reverses its direction
    public Platform movePlatform(){
	if ((420 <= this.center.x + (this.width / 2))){
	    return new Platform(new Posn(this.center.x - (this.speed), this.center.y),
				this.speed, this.width, "left");
	} else if (0 >= this.center.x - (this.width / 2)){
	    return new Platform(new Posn(this.center.x + (this.speed), this.center.y),
				this.speed, this.width, "right");
	} else if (this.dir.equals("right")){
	    return new Platform(new Posn(this.center.x + (this.speed), this.center.y),
				this.speed, this.width, "right");
	} else {
	    return new Platform(new Posn(this.center.x - (this.speed), this.center.y),
				this.speed, this.width, "left");
	}
    }
}
class Goal {
    
    int num;
    
    Goal (int num){
	this.num = num;
    }
    // Generates a new image representing the Goal
    public WorldImage goalImage(){
	return new RectangleImage(new Posn(this.num,590), 20, 20, new Green());
    }
    // Generates a new Goal at a random location along the bottom of the game arena
    public Goal newGoal(){
	Goal newG = this;
	for (int i=0; i<21; i++){
	    Random newRandom = new Random();
	    int randInt = newRandom.nextInt(100);
	    if (randInt > 95 || i == 21){
		newG = new Goal(10+(20*i));
	    }
	}
	return newG;
    }
}
class PBlock{
    
    Posn center;
    IColor col;

    PBlock(Posn center, IColor col){
	this.center = center;
	this.col = col;
    }
    // Generates an image of the player's block
    public WorldImage blockImage(){
	return new RectangleImage(this.center,
				  20, 20,
				  this.col);
    }
    // Moves the player's block based which arrow key is pressed
    public PBlock moveBlock(String ke){
	if (ke.equals("right") && (420 > this.center.x + 10)){
	    return new PBlock(new Posn(this.center.x + 20, this.center.y),
			      this.col);
	} else if (ke.equals("left") && (0 < this.center.x - 10)){
	    return new PBlock(new Posn(this.center.x - 20, this.center.y),
			      this.col);
	} else if (ke.equals("up") && (0 < this.center.y - 10)){
	    return new PBlock(new Posn(this.center.x, this.center.y - 10),
			      this.col);
	} else if (ke.equals("down")){
	    return new PBlock(new Posn(this.center.x, this.center.y + 20),
			      this.col);
	} else {   
	    return this;
	}
    }
}
class Game1 extends World {
    
    static int width = 420;
    static int height = 600;
    int score;
    PBlock block;
    Goal goal;
    Platform P1;
    Platform P2;
    Platform P3;
    WorldImage gameArena = new RectangleImage(new Posn((this.width / 2),
						       (this.height / 2)),
					      this.width,
					      this.height,
					      new Blue());
    
    public Game1 (int score, PBlock block, Goal goal,
		  Platform P1, Platform P2, Platform P3){
	this.score = score;
	this.block = block;
	this.goal = goal;
	this.P1 = P1;
	this.P2 = P2;
	this.P3 = P3;
    }
    // Controls what happens in the game when a key is pressed
    public World onKeyEvent(String ke){
	// If the key "x" is pressed the game world ends
	if (ke.equals("x")){
	    return this.endOfWorld("Aidos");
	} else {
	    // If any other key is pressed feed that key into .moveBlock()
	    // and run it on the current player block
	    return new Game1 (this.score, this.block.moveBlock(ke),
			      this.goal, this.P1, this.P2, this.P3);
	}
    }
    // Controls what happens on each tick of the game world
    public World onTick(){
	// If the player's block reaches the goal generate a new player block,
	// a new goal, increase the current score, and increase the speed of the platforms
	if (this.block.center.x == this.goal.num && (570 < this.block.center.y)){
	    return new Game1 ((this.score + 100),
			      new PBlock(new Posn(210,10), new Red()),
			      this.goal.newGoal(),
			      new Platform(this.P1.center,
					   this.P1.speed+3,
					   this.P1.width,
					   this.P1.dir).movePlatform(),
			      new Platform(this.P2.center,
					   this.P2.speed+5,
					   this.P2.width,
					   this.P2.dir).movePlatform(),
			      new Platform(this.P3.center,
					   this.P3.speed+5,
					   this.P3.width,
					   this.P3.dir).movePlatform());
	    // If the player's block hits the ground anywhere other than the goal
	    // generate a new player block and decrease the score
	} else if(570 < this.block.center.y){
	    return new Game1 ((this.score - 50),
			      new PBlock(new Posn(210,10), new Red()),
			      this.goal,
			      this.P1.movePlatform(),
			      this.P2.movePlatform(),
			      this.P3.movePlatform());
	    // If the player hits a platform increase the size of that platform,
	    // generate a new player block, and decrease the score
	} else if ((Math.abs(this.block.center.y-this.P1.center.y)<=20) &&
		   ((this.P1.width+20) /2) >= Math.abs(this.block.center.x - this.P1.center.x)){
	    return new Game1 ((this.score - 25),
			      new PBlock(new Posn(210,10), new Red()),
			      this.goal,
			      new Platform(this.P1.center,
					   this.P1.speed,
					   this.P1.width + 20,
					   this.P1.dir),
			      this.P2.movePlatform(),
			      this.P3.movePlatform());
	} else if((Math.abs(this.block.center.y-this.P2.center.y)<=20) &&
		  ((this.P2.width+20) /2) >= Math.abs(this.block.center.x - this.P2.center.x)){
	    return new Game1 ((this.score - 25),
			      new PBlock(new Posn(210,10), new Red()),
			      this.goal,
			      this.P1.movePlatform(),      
			      new Platform(this.P2.center,
					   this.P2.speed,
					   this.P2.width + 20,
					   this.P2.dir),
			      this.P3.movePlatform());
	} else if((Math.abs(this.block.center.y-this.P3.center.y)<=20) &&
		  ((this.P3.width+20) /2) >= Math.abs(this.block.center.x - this.P3.center.x)){
	    return new Game1 ((this.score - 25),
			      new PBlock(new Posn(210,10), new Red()),
			      this.goal,
			      this.P1.movePlatform(),
			      this.P2.movePlatform(),
			      new Platform(this.P3.center,
					   this.P3.speed,
					   this.P3.width + 20,
					   this.P3.dir));
	    // Otherwise move the player's block down to simulate falling
	} else {
	    return new Game1 (this.score,
			      this.block.moveBlock("down"),
			      this.goal,
			      this.P1.movePlatform(),
			      this.P2.movePlatform(),
			      this.P3.movePlatform());
	}
    }
    // Overlays the images of each of the game objects 
    public WorldImage makeImage(){
	return new OverlayImages(
		   new OverlayImages(
		       new OverlayImages(
		           new OverlayImages(
			       new OverlayImages(
				   new OverlayImages(
				       new OverlayImages(
				           this.gameArena,
					   // This rectangle provides a visual for the
					   // ground everywhere other than the goal
					   new RectangleImage(new Posn(210,590), 420, 20, new Black())),
				         this.goal.goalImage()),
				    this.block.blockImage()),
			       this.P1.platformImage()),
			   this.P2.platformImage()),
		       this.P3.platformImage()),
		   new TextImage(new Posn(300, 20), "Your score is " + this.score,Color.red)); 
    }
    // Determines under what conditions the game world ends
    public WorldEnd worldEnds(){
	// If the width of any of the platforms is greater than or equal to that
	// of the game arena end the game
	if (P1.width >= width || P2.width >= width || P3.width >= width){
	    return 
		new WorldEnd(true,
			     new OverlayImages(this.makeImage(),
					       new TextImage(new Posn(210, 300),
							     "GAME OVER: A Platform Grew Too Wide", 
							     Color.red)));
	} else {
	    // Otherwise don't end the game
	    return new WorldEnd(false, this.makeImage());
	}
    }
    // Defines the initial setup of the game world and begins the game
    public static void main(String args[]){
	Game1 G = new Game1(0,
			    new PBlock(new Posn(210, 10), new Red()),
			    new Goal(70),
			    new Platform (new Posn (310, 150), 10, 150, "left"),
			    new Platform (new Posn (110, 300), 10, 150, "right"),
			    new Platform (new Posn (210, 450), 10, 150, "left"));
	G.bigBang(width, height, 0.2);
    }
}
// Contains tests for each feature of the Game World
class GameTest{
    
    // TEST PLATFORMS
    Platform platform1 = new Platform (new Posn (310, 150), 10, 150, "left");
    Platform platform2 = new Platform (new Posn (110, 300), 10, 150, "right");
    Platform platform3 = new Platform (new Posn (210, 450), 10, 150, "left");
    Platform p1end = new Platform (new Posn (310, 150), 10, 420, "left");
    Platform p2end = new Platform (new Posn (110, 300), 10, 420, "right");
    Platform p3end = new Platform (new Posn (210, 450), 10, 420, "left");
    Platform platformLeft = new Platform (new Posn (210, 450), 10, 150, "left");
    Platform platformRight = new Platform (new Posn (210, 450), 10, 150, "right");
    Platform movedRight = new Platform (new Posn (220, 450), 10, 150, "right");
    Platform movedLeft = new Platform (new Posn (200,450), 10, 150, "left");
    Platform leftEdge = new Platform (new Posn (75, 450), 10, 150, "left");
    Platform rightEdge = new Platform (new Posn (345, 450), 10, 150, "right");
    Platform switchRight = new Platform (new Posn (85, 450), 10, 150, "right");
    Platform switchLeft = new Platform (new Posn (335, 450), 10, 150, "left");
    
    // STANDARD GOAL
    Goal standardGoal = new Goal(70);

    // TEST PLAYER BLOCKS 
    PBlock SBlock = new PBlock (new Posn(210, 10), new Red());
    PBlock Goal = new PBlock (new Posn (70, 600), new Red());
    PBlock noGoal = new PBlock (new Posn (50, 600), new Red());
    PBlock upSBlock = new PBlock (new Posn(210, 10), new Red());
    PBlock downSBlock = new PBlock (new Posn(210, 30), new Red());
    PBlock leftSBlock = new PBlock (new Posn(190, 10), new Red());
    PBlock rightSBlock = new PBlock (new Posn(230, 10), new Red());
    PBlock p1DeathBlock = new PBlock (new Posn(310, 150), new Red());
    PBlock p2DeathBlock = new PBlock (new Posn(110, 300), new Red());
    PBlock p3DeathBlock = new PBlock (new Posn(210, 450), new Red());

    // TEST GAME WORLDS
    Game1 standardGame = new Game1(0,
				   SBlock,
				   standardGoal,
				   platform1,
				   platform2,
				   platform3);
    Game1 goalGame = new Game1(0,
			       Goal,
			       standardGoal,
			       platform1,
			       platform2,
			       platform3);
    Game1 noGoalGame = new Game1(0,
				 noGoal,
				 standardGoal,
				 platform1,
				 platform2,
				 platform3);
    Game1 P1Death = new Game1(0,
			      p1DeathBlock,
			      standardGoal,
			      platform1,
			      platform2,
			      platform3);
    Game1 P2Death = new Game1(0,
			      p2DeathBlock,
			      standardGoal,
			      platform1,
			      platform2,
			      platform3);
    Game1 P3Death = new Game1(0,
			      p3DeathBlock,
			      standardGoal,
			      platform1,
			      platform2,
			      platform3);
    Game1 P1End = new Game1(0,
			    SBlock,
			    standardGoal,
			    p1end,
			    platform2,
			    platform3);
    Game1 P2End = new Game1(0,
			    SBlock,
			    standardGoal,
			    platform1,
			    p2end,
			    platform3);
    Game1 P3End = new Game1(0,
			    SBlock,
			    standardGoal,
			    platform1,
			    platform2,
			    p3end);

    // Testing the method moveBlock in the Block class 
    boolean testMoveBlock(Tester t){
	return
	    t.checkExpect(SBlock.moveBlock("left"), 
			  leftSBlock, "test moveBlock - left ") &&
	    t.checkExpect(SBlock.moveBlock("right"), 
			  rightSBlock, "test moveBlock - right ") &&
	    t.checkExpect(SBlock.moveBlock("up"), 
			  upSBlock, "test moveBlock - up ") &&
	    t.checkExpect(SBlock.moveBlock("down"), 
			  downSBlock, "test moveBlock - down ") &&
	    t.checkExpect(SBlock.moveBlock("t"),
			  SBlock, "test moveBlock - t (a invalid input)");
    }
    // Testing the method movePlatform in the Platform class
    boolean testMovePlatform(Tester t){
     	return
	    t.checkExpect(platformRight.movePlatform(),
			  movedRight, "test movePlatform - right ") &&
	    t.checkExpect(platformLeft.movePlatform(),
			  movedLeft, "test movePlatform - left ") &&
	    t.checkExpect(leftEdge.movePlatform(),
			  switchRight, "Test movePlatform - switch left to right") &&
	    t.checkExpect(rightEdge.movePlatform(),
			  switchLeft, "Test movePlatform - swtich right to left");
    }
    // Testing the conditions for the game to end
    boolean testWorldEnd(Tester t){
	return
	    t.checkExpect(P1End.worldEnds(),
			  new WorldEnd(true,
				       new OverlayImages(P1End.makeImage(),
							 new TextImage(new Posn(210, 300),
								       "GAME OVER: A Platform Grew Too Wide",
								       Color.red))),
			  "Test worldEnd - Platform 1") &&
	    t.checkExpect(P2End.worldEnds(),
			  new WorldEnd(true,
				       new OverlayImages(P2End.makeImage(),
							 new TextImage(new Posn(210, 300),
								       "GAME OVER: A Platform Grew Too Wide",
								       Color.red))),
			  "Test worldEnd - Platform 2") &&
	    t.checkExpect(P3End.worldEnds(),
			  new WorldEnd(true,
				       new OverlayImages(P3End.makeImage(),
							 new TextImage(new Posn(210, 300),
								       "GAME OVER: A Platform Grew Too Wide",
								       Color.red))),
			  "Test worldEnd - Platform 3") &&
	    t.checkExpect(standardGame.worldEnds(),
			  new WorldEnd(false, standardGame.makeImage()),
			  "Test worldEnd - World shouldn't end");
    }
    // Testing the game features controlled by onTick()
    boolean testonTick(Tester t){
	Game1 goalGameTick = (Game1)goalGame.onTick();
	return
	    // Tests for scoring a Goal
	    t.checkExpect(goalGameTick.block,
			  SBlock,
			  "Test Goal - New Block Location") &&
	    t.checkExpect(goalGameTick.score,
			  goalGame.score + 100,
			  "Test Goal - Score") &&
	    t.checkExpect(goalGameTick.P1,
			  new Platform (goalGame.P1.center,
					goalGame.P1.speed + 3,
					goalGame.P1.width,
					goalGame.P1.dir).movePlatform(),
			  "Test Goal - Platform 1") &&
	    t.checkExpect(goalGameTick.P2,
			  new Platform (goalGame.P2.center,
					goalGame.P2.speed + 5,
					goalGame.P2.width,
					goalGame.P2.dir).movePlatform(),
			  "Test Goal - Platform 2") &&
	    t.checkExpect(goalGameTick.P3,
			  new Platform (goalGame.P3.center,
					goalGame.P3.speed + 5,
					goalGame.P3.width,
					goalGame.P3.dir).movePlatform(),
			  "Test Goal - Platform 3") &&
	    // Test for hitting the ground
	    t.checkExpect(noGoalGame.onTick(),
			  new Game1 (-50,
				     SBlock,
				     noGoalGame.goal,
				     noGoalGame.P1.movePlatform(),
				     noGoalGame.P2.movePlatform(),
				     noGoalGame.P3.movePlatform()),
			  "Test ground functionality") &&
	    // Tests for colliding with platforms
	    t.checkExpect(P1Death.onTick(),
			  new Game1 ((P1Death.score - 25),
				     new PBlock(new Posn(210,10), new Red()),
				     P1Death.goal,
				     new Platform(P1Death.P1.center,
						  P1Death.P1.speed,
						  P1Death.P1.width + 20,
						  P1Death.P1.dir),
				     P1Death.P2.movePlatform(),
				     P1Death.P3.movePlatform()),
			  "Test Platform Collision - Platform 1") &&
	    t.checkExpect(P2Death.onTick(),
			  new Game1 ((P2Death.score - 25),
				     new PBlock(new Posn(210,10), new Red()),
				     P2Death.goal,
				     P2Death.P1.movePlatform(),
				     new Platform(P2Death.P2.center,
						  P2Death.P2.speed,
						  P2Death.P2.width + 20,
						  P2Death.P2.dir),
				     P2Death.P3.movePlatform()),
			  "Test Platform Collision - Platform 2") &&
	    t.checkExpect(P3Death.onTick(),
			  new Game1 ((P3Death.score - 25),
				     new PBlock(new Posn(210,10), new Red()),
				     P3Death.goal,
				     P3Death.P1.movePlatform(),
				     P3Death.P2.movePlatform(),
				     new Platform(P3Death.P3.center,
						  P3Death.P3.speed,
						  P3Death.P3.width + 20,
						  P3Death.P3.dir)),
			  "Test Platform Collision - Platform 3") &&
	    // Test for avoiding platforms
	    t.checkExpect(standardGame.onTick(),
			  new Game1(standardGame.score,
				    standardGame.block.moveBlock("down"),
				    standardGame.goal,
				    standardGame.P1.movePlatform(),
				    standardGame.P2.movePlatform(),
				    standardGame.P3.movePlatform()),
			  "Test Platform Collision - No Collision");
    }
    public static void main(String[] args){
	// run the tests - showing only the failed test results
	GameTest be = new GameTest();
	Tester.runReport(be, false, false);
    }
}
