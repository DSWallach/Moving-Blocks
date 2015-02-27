import java.awt.*;
import java.util.Random;

import tester.*;

import javalib.funworld.*;
import javalib.colors.*;
import javalib.worldcanvas.*;
import javalib.worldimages.*;

class Block{
    Posn center;
    int height;
    int width;
    IColor col;

    Block(Posn center, int width, int height, IColor col){
	this.center = center;
	this.height = height;
	this.width = width;
	this.col = col;
    }
    WorldImage blockImage(){
	return new FromFileImage(this.center, "Images/shark.png").
	    overlayImages(new RectangleImage(this.center,
					     this.width,
					     this.height,
					     this.col));
    }
    public Block moveBlock(String ke){
        if (ke.equals("right")){
	    return new Block(new Posn(this.center.x + 5, this.center.y),
			     this.width, this.height, this.col);
	} else if (ke.equals("left")){
	    return new Block(new Posn(this.center.x - 5, this.center.y),
			     this.width, this.height, this.col);
	} else if (ke.equals("up")){
	    return new Block(new Posn(this.center.x, this.center.y - 5),
			     this.width, this.height, this.col);
	} else if (ke.equals("down")){
	    return new Block(new Posn(this.center.x, this.center.y + 5),
			     this.width, this.height, this.col);
	} else {   
	    return this;
	}
    }
}
class Game1 extends World {
    
    int width = 200;
    int height = 300;
    Block block;
    
    public Game1 (Block block){
	super();
	this.block = block;
    }
    public WorldImage gameArena = new RectangleImage(new Posn(100, 150),this.width,this.height, new Blue());
    public World onKeyEvent(String ke){
	if (ke.equals("x")){
	    return this.endOfWorld("Aidos");
	} else {
	    return new Game1 (this.block.moveBlock(ke));
	}
    }
    public World onTick(){
	return new Game1 (this.block.moveBlock("down"));
    }
    
    public WorldImage makeImage(){
	return new OverlayImages(this.gameArena, this.block.blockImage()); 
    }
    public WorldImage lastImage(String s){
	return new OverlayImages(this.makeImage(),
				 new TextImage(new Posn(100, 40), s, 
					       Color.red));
    }
    public static void main(String args[]){
	Game1 G = new Game1(new Block(new Posn(150, 100), 20, 20, new Red()));
	G.bigBang(200, 300, 0.3);
    }
}

