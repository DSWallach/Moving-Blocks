import java.awt.*;
import java.util.*;
import java.lang.*;
import tester.*;
import javalib.funworld.*;
import javalib.colors.*;
import javalib.worldcanvas.*;
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
    public WorldImage platformImage(){
	return new RectangleImage(this.center, this.width, 20, new Yellow());
    }
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
class Ground{
    
    int num;
    
    Ground (int num){
	this.num = num;
    }
    public WorldImage groundImage(){
	return new RectangleImage(new Posn(210, 590), 420, 20, new Black());
    }
}
class Goal {
    
    int num;
    
    Goal (int num){
	this.num = num;
    }
    public WorldImage goalImage(){
	return new RectangleImage(new Posn(this.num,590), 20, 20, new Green());
    }
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
    int height;
    int width;
    IColor col;

    PBlock(Posn center, int width, int height, IColor col){
	this.center = center;
	this.height = height;
	this.width = width;
	this.col = col;
    }
    public WorldImage blockImage(){
	return new RectangleImage(this.center,
				  this.width,
				  this.height,
				  this.col);
    }
    public PBlock moveBlock(String ke){
	if (ke.equals("right") && (420 > this.center.x + (this.width / 2))){
	    return new PBlock(new Posn(this.center.x + 20, this.center.y),
			     this.width, this.height, this.col);
	} else if (ke.equals("left") && (0 < this.center.x - (this.width / 2))){
	    return new PBlock(new Posn(this.center.x - 20, this.center.y),
			     this.width, this.height, this.col);
	} else if (ke.equals("up") && (0 < this.center.y - (this.height / 2))){
	    return new PBlock(new Posn(this.center.x, this.center.y - 10),
			     this.width, this.height, this.col);
	} else if (ke.equals("down") && (600 > this.center.y + (this.width / 2))){
	    return new PBlock(new Posn(this.center.x, this.center.y + 20),
			     this.width, this.height, this.col);
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
    Ground ground;
    Goal goal;
    Platform P1;
    Platform P2;
    Platform P3;
    
    public Game1 (int score, PBlock block, Ground ground, Goal goal,
		  Platform P1, Platform P2, Platform P3){
	super();
	this.score = score;
	this.block = block;
	this.ground = ground;
	this.goal = goal;
	this.P1 = P1;
	this.P2 = P2;
	this.P3 = P3;
    }
    public WorldImage gameArena = new RectangleImage(new Posn((this.width / 2),
							      (this.height / 2)),
						     this.width,this.height, new Blue());
    public World onKeyEvent(String ke){
	if (ke.equals("x")){
	    return this.endOfWorld("Aidos");
	} else {
	    return new Game1 (this.score, this.block.moveBlock(ke),
			      this.ground, this.goal, this.P1, this.P2, this.P3);
	}
    }
    public World onTick(){
	if (this.block.center.x == this.goal.num && (580 < this.block.center.y)){
	    return new Game1 ((this.score + 100),
			      new PBlock(new Posn(210,10), 20, 20, new Red()),
			      this.ground,
			      this.goal.newGoal(),
			      new Platform(this.P1.center,
					   (this.P1.speed+3),
					   this.P1.width,
					   this.P1.dir).movePlatform(),
			      new Platform(this.P2.center,
					   (this.P2.speed+5),
					   this.P2.width,
					   this.P2.dir).movePlatform(),
			      new Platform(this.P3.center,
					   (this.P3.speed+5),
					   this.P3.width,
					   this.P3.dir).movePlatform());
	} else if(580 < this.block.center.y){
	    return new Game1 ((this.score - 50),
			      new PBlock(new Posn(210,10), 20, 20, new Red()),
			      this.ground,
			      this.goal,
			      this.P1.movePlatform(),
			      this.P2.movePlatform(),
			      this.P3.movePlatform());
	} else if ((Math.abs(this.block.center.y-this.P1.center.y)<20) &&
		  ((this.P1.width+this.block.width) /2) >= Math.abs(this.block.center.x - this.P1.center.x)){
	    return new Game1 ((this.score - 25),
			      new PBlock(new Posn(210,10), 20, 20, new Red()),
			      this.ground,
			      this.goal,
			      new Platform(this.P1.center,
					   this.P1.speed,
					   this.P1.width + 20,
					   this.P1.dir),
			      this.P2.movePlatform(),
			      this.P3.movePlatform());
	} else if((Math.abs(this.block.center.y-this.P2.center.y)<20) &&
		  ((this.P2.width+this.block.width) /2) >= Math.abs(this.block.center.x - this.P2.center.x)){
	    return new Game1 ((this.score - 25),
			      new PBlock(new Posn(210,10), 20, 20, new Red()),
			      this.ground,
			      this.goal,
			      this.P1.movePlatform(),      
			      new Platform(this.P2.center,
					   this.P2.speed,
					   this.P2.width + 20,
					   this.P2.dir),
			      this.P3.movePlatform());
	} else if((Math.abs(this.block.center.y-this.P3.center.y)<20) &&
		  ((this.P3.width+this.block.width) /2) >= Math.abs(this.block.center.x - this.P3.center.x)){
	    return new Game1 ((this.score - 25),
			      new PBlock(new Posn(210,10), 20, 20, new Red()),
			      this.ground,
			      this.goal,
			      this.P1.movePlatform(),
			      this.P2.movePlatform(),
			      new Platform(this.P3.center,
					   this.P3.speed,
					   this.P3.width + 20,
					   this.P3.dir));
	} else {
	    return new Game1 (this.score,
			      this.block.moveBlock("down"),
			      this.ground,
			      this.goal,
			      this.P1.movePlatform(),
			      this.P2.movePlatform(),
			      this.P3.movePlatform());
	}
    }
    public WorldImage makeImage(){
	return new OverlayImages(
	           new OverlayImages(
	               new OverlayImages(
	                   new OverlayImages(
		    	       new OverlayImages(
				   new OverlayImages(
				       new OverlayImages(
							this.gameArena,
							this.ground.groundImage()),
				       this.goal.goalImage()),
				   this.block.blockImage()),
			       this.P1.platformImage()),
			   this.P2.platformImage()),
		       this.P3.platformImage()),
		   new TextImage(new Posn(300, 20), "Your score is "+this.score,Color.red)); 
    }
    public WorldEnd worldEnds(){
	if (P1.width >= width || P2.width >= width || P3.width >= width){
	    return 
		new WorldEnd(true,
			     new OverlayImages(this.makeImage(),
					       new TextImage(new Posn(210, 300),
							     "GAME OVER: A Platform Grew Too Wide", 
							     Color.red)));
	} else {
	return new WorldEnd(false, this.makeImage());
	}
    }
    public static void main(String args[]){
	Game1 G = new Game1(0,
			    new PBlock(new Posn(210, 10),20, 20,new Red()),
			    new Ground(21),
			    new Goal(70),
			    new Platform (new Posn (310, 150), 10, 150, "left"),
			    new Platform (new Posn (110, 300), 10, 150, "right"),
			    new Platform (new Posn (210, 450), 10, 150, "left"));
	G.bigBang(width, height, 0.2);
    }
}

class GameExamples{
    
    // Platforms at standard starting positions
    Platform platform1 = new Platform (new Posn (310, 150), 10, 150, "left");
    Platform platform2 = new Platform (new Posn (110, 300), 10, 150, "right");
    Platform platform3 = new Platform (new Posn (210, 450), 10, 150, "left");
    Platform platformLeft = new Platform (new Posn (210, 450), 10, 150, "left");
    Platform platformRight = new Platform (new Posn (210, 450), 10, 150, "right");
    Platform movedRight = new Platform (new Posn (220, 450), 10, 150, "right");
    Platform movedLeft = new Platform (new Posn (200,450), 10, 150, "left");
    Platform leftEdge = new Platform (new Posn (75, 450), 10, 150, "left");
    Platform rightEdge = new Platform (new Posn (345, 450), 10, 150, "right");
    Platform switchRight = new Platform (new Posn (85, 450), 10, 150, "right");
    Platform switchLeft = new Platform (new Posn (335, 450), 10, 150, "left");

    //Standard Ground
    Ground standardGround = new Ground(21);
    
    //Standard Goal
    Goal standardGoal = new Goal(70);

    // Player Block at various starting positions 
    // Standard Player starting position
    PBlock SBlock = new PBlock (new Posn(210, 10),20, 20,new Red());
    
    // Block moved by one key entry
    PBlock upSBlock = new PBlock (new Posn(210, 10),20, 20,new Red());
    PBlock downSBlock = new PBlock (new Posn(210, 30),20, 20,new Red());
    PBlock leftSBlock = new PBlock (new Posn(190, 10),20, 20,new Red());
    PBlock rightSBlock = new PBlock (new Posn(230, 10),20, 20,new Red());
    
    // Player block stating in the first platform 
    PBlock p1DeathBlock = new PBlock (new Posn(310, 150),20, 20,new Red());
    // Player block starting in the second platform
    PBlock p2DeathBlock = new PBlock (new Posn(110, 300),20, 20,new Red());
    // Player block starting in the third platform
    PBlock p3DeathBlock = new PBlock (new Posn(210, 450),20, 20,new Red());

    //Example Game Worlds
    Game1 standardGame = new Game1(0,
				   SBlock,
				   standardGround,
				   standardGoal,
				   platform1,
				   platform2,
				   platform3);
    Game1 P1Death = new Game1(0,
			      p1DeathBlock,
			      standardGround,
			      standardGoal,
			      platform1,
			      platform2,
			      platform3);
    Game1 P2Death = new Game1(0,
			      p2DeathBlock,
			      standardGround,
			      standardGoal,
			      platform1,
			      platform2,
			      platform3);
    Game1 P3Death = new Game1(0,
			      p3DeathBlock,
			      standardGround,
			      standardGoal,
			      platform1,
			      platform2,
			      platform3);    

    // testing the method moveBlock in the Block class 
    boolean testMoveBlock(Tester t){
	return
	    t.checkExpect(this.SBlock.moveBlock("left"), 
			  this.leftSBlock, "test moveBlock - left " + "\n") &&
	    t.checkExpect(this.SBlock.moveBlock("right"), 
			  this.rightSBlock, "test moveBlock - right " + "\n") &&
	    t.checkExpect(this.SBlock.moveBlock("up"), 
			  this.upSBlock, "test moveBlock - up " + "\n") &&
	    t.checkExpect(this.SBlock.moveBlock("down"), 
			  this.downSBlock, "test moveBlock - down " + "\n");
    }
    // Testing the method movePlatform in the Platform class
	 boolean testMovePlatform(Tester t){
     	return
	    t.checkExpect(this.platformRight.movePlatform(),
			  this.movedRight, "test movePlatform - right " + "\n") &&
	    t.checkExpect(this.platformLeft.movePlatform(),
			  this.movedLeft, "test movePlatform - left " + "\n") &&
	    t.checkExpect(this.leftEdge.movePlatform(),
			  this.switchRight, "Test movePlatform - switch left to right" + "\n") &&
	    t.checkExpect(this.rightEdge.movePlatform(),
			  this.switchLeft, "Test movePlatform - swtich right to left" + "\n");
	
     }
    
    public static void main(String[] argv){

	// run the tests - showing only the failed test results
	GameExamples be = new GameExamples();
	Tester.runReport(be, false, false);
    }
}
