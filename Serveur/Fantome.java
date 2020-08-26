import java.net.*;
import java.io.*;
import java.util.*;

public class Fantome extends Entite implements Runnable {
    
    public int id;
    public int point;
    private int sleepTime;
    public int posx;
    public int posy;
    public boolean alive;
    public Partie p;

    public Fantome (int id, Partie p) {
	this.id = id;
	this.sleepTime = 20000;
	this.point = 10;
	this.fant=true;
	this.alive = true;
	this.p = p;
    }
    
    public void run (){
	while(alive){
	    this.p.deplacementF(this);
	    this.p.serv.pause(this, this.sleepTime, this.p);
	}
    }
    
    public void setPos (int x, int y) {
	this.posx = x;
	this.posy = y;
    }
}
