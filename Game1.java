package javalib.soundworld;
import java.awt.Color;
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
	//super();
	this.block = block;
    }
    @Override
    public World onKeyEvent(String ke){
	if (ke.equals("x")){
	    return this.endOfWorld("Aidos");
	    } else {
	    return new Game1 (this.block.moveBlock(ke));
	    }
    }
    @Override
    public World onTick(){
	return new Game1 (this.block.moveBlock("down"));
    }
    
    public WorldImage makeImage(){
		return new OverlayImages(this.blackHole, this.blob.blobImage()); 
	}
      public WorldImage lastImage(String s){
    return new OverlayImages(this.makeImage(),
        new TextImage(new Posn(100, 40), s, 
            Color.red));
  }
}
