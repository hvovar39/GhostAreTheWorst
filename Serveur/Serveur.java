import java.net.*;
import java.io.*;
import java.util.*;

public class Serveur{

    private static List<Joueur> listeJ;
    public static List<Partie> listeP;
    public static List<Partie> listePartieEnCours;
    private static List<Thread> listeThread;
    
    //private List<Labyrinthe> listeLab;
    
    public Serveur(){
	try {
	    listeJ = new ArrayList<Joueur> ();
	    listeP = new ArrayList<Partie> ();
	    listePartieEnCours = new ArrayList<Partie> ();
	    listeThread = new ArrayList<Thread> ();
	    ServerSocket serv = new ServerSocket (4242);
	    while (true)
		{
		    Socket s = serv.accept();
		    Joueur j = new Joueur (s, this);
		    listeJ.add(j);
		    Thread th = new Thread (j);
		    listeThread.add(th);
		    th.start();
		}
	}
	catch (Exception e) {
	    System.out.println(e);
	    e.printStackTrace();
	}
    }


    //FONCTION DE GESTION DES THREADS
    public void pause (Entite e, long s, Partie p){
	try {
	    Thread th;
	    if (p==null)
		th = listeThread.get(listeJ.indexOf(e));
	    else
		th = p.getThFant((Fantome)e); 
	    th.sleep(s);
	}catch(Exception ex) {
	    System.out.println(ex);
	    ex.printStackTrace();
	}
    }

    public void mettre_en_attente (Entite e, Partie p) {
	try{
	    Thread th;
	    if (p==null)
		th = listeThread.get(listeJ.indexOf(e));
	    else
		th = p.getThFant((Fantome)e);
	    synchronized(th){
		th.wait();
	    }
	}catch(Exception ex) {
	    System.out.println(ex);
	    ex.printStackTrace();
	}
    }

    public void reveiller (Entite e, Partie p) {
	try{
	    Thread th;
	    if (p==null)
		th = listeThread.get(listeJ.indexOf(e));
	    else
		th = p.getThFant((Fantome)e);
	    synchronized(th){
		if(th.getState()==Thread.State.WAITING)
		    th.notify();
	    }
	}catch(Exception ex) {
	    System.out.println(ex);
	    ex.printStackTrace();
	}
    }

    
    //FONCTION DE GESTION DES PARTIES
    public void fermerPartie (Partie p) {
	p.fermer();
	this.listeP.remove(p);
    }

    public int creerPartie () {
	Partie p = new Partie(this);
	listeP.add(p);
	return listeP.indexOf(p);
    }
    
    public void partie_lancer(Partie p) {
	this.listeP.remove(p);
	this.listePartieEnCours.add(p);
    }
    
}
