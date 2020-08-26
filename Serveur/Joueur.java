import java.net.*;
import java.io.*;
import java.util.*;

public class Joueur extends Entite implements Runnable{

    private Partie partie;
    public String id;
    public int score;
    public int posx;
    public int posy;
    public Serveur serv;
    public int ready;

    private Socket sock;
    private InputStreamReader reader;
    private PrintWriter pw;
    public int portUDP;
    
    public Joueur (Socket s, Serveur serv) {
	this.sock = s;
	this.serv = serv;
	this.ready = 0;
	this.fant=false;
	try {
	    this.reader = new InputStreamReader (this.sock.getInputStream());
	    this.pw = new PrintWriter (new OutputStreamWriter (this.sock.getOutputStream()));
	}
	catch (Exception e) {
	    System.out.println(e);
	    e.printStackTrace();
	}
    }

    
    //Fonction run du thread
    public void run () {
	try {
	    envoieListParti();
	    ecouteLobby();
	    jeu();
	}
	catch (Exception e) {
	    System.out.println(e);
	    e.printStackTrace();
	}
    }
    

    //FONCTIONS BOUCLE D'ECOUTE
    private void ecouteLobby() {
	try {
	    String[] messRecu = receptionMess();
	    while(!messRecu[0].equals("START") || this.partie == null)
		{
		    if (messRecu[0].equals("GAMES?"))
			envoieListParti();
		    
		    else if (messRecu[0].equals("REG"))
			connectionPartie (Integer.parseInt(messRecu[3]), messRecu[1], Integer.parseInt(messRecu[2]));

		    else if (messRecu[0].equals("NEW"))
			connectionPartie (this.serv.creerPartie(), messRecu[1], Integer.parseInt(messRecu[2]));
		    
		    else if (messRecu[0].equals("UNREG"))
			deconnectionPartie();

		    else if (messRecu[0].equals("SIZE?")) {
			int p = Integer.parseInt(messRecu[1]);
			if (p<this.serv.listeP.size()) {
			    int [] size = this.serv.listeP.get(p).getSizeLab();
			    envoieMess("SIZE! "+p+" "+size[0]+" "+size[1]+"***");
			} else 
			    envoieMess("DUNNO***");
		    }
		    
		    else if (messRecu[0].equals("LIST?")) {
			int p = Integer.parseInt(messRecu[1]);
			if (p<this.serv.listeP.size()) {
			    String[]joueurs = this.serv.listeP.get(p).getJoueurs();
			    envoieMess("LIST! "+p+" "+joueurs.length+"***");
			    for (int i=0; i<joueurs.length; i++)
				envoieMess("PLAYER "+joueurs[i]+"***");
			} else
			    envoieMess("DUNNO ***");
		    }
		    
		    else
			envoieMess("ERROR***");
		    messRecu = receptionMess();
		    
		}
	    this.ready=1;
	    if(!this.partie.allReady())
		this.serv.mettre_en_attente(this, null);
	    
	}
	catch (Exception e) {
	    System.out.println(e);
	    e.printStackTrace();
	    envoieMess("ERROR***");
	}
    }

    private void jeu(){
	try {
	    int [] sizeLab = this.partie.getSizeLab();
	    envoieMess("WELCOME "+this.serv.listePartieEnCours.indexOf(this.partie)+" "+sizeLab[0]+" "+sizeLab[1]+" "+this.partie.getNbrFant()+" "+this.partie.IPMultiDiff+" "+this.partie.PortMultiDiff+"***");
	    envoieMess("POS "+this.id+" "+this.posx+" "+this.posy+"***");
	    String[]messRecu;
	    while(!this.partie.finit)
		{
		    messRecu = receptionMess();
			    
		    if(messRecu[0].equals("UP")){
			System.out.println(messRecu[1]);
			if(this.partie.deplacementJ (this, 0, Integer.parseInt(messRecu[1])))
			    envoieMessDepFant();
			else
			    envoieMessDep();
		    }
		    
		    else if(messRecu[0].equals("RIGHT")){
			if(this.partie.deplacementJ (this, 1, Integer.parseInt(messRecu[1])))
			    envoieMessDepFant();
			else
			    envoieMessDep();
		    }
		    
		    else if(messRecu[0].equals("DOWN")){
			if(this.partie.deplacementJ (this, 2, Integer.parseInt(messRecu[1])))
			    envoieMessDepFant();
			else
			    envoieMessDep();
		    }
		    
		    else if(messRecu[0].equals("LEFT")){
			if(this.partie.deplacementJ (this, 3, Integer.parseInt(messRecu[1])))
			    envoieMessDepFant();
			else
			    envoieMessDep();
		    }

		    else if(messRecu[0].equals("GLIST?")){
			List<Joueur> joueur = this.partie.listeJ;
			envoieMess("GLIST! "+joueur.size()+"***");
			for(int i=0; i<joueur.size(); i++)
			    envoieMess("GPLAYER "+joueur.get(i).id+" "+joueur.get(i).posx+" "+joueur.get(i).posy+" "+joueur.get(i).score+"***");
		    }

		    else if(messRecu[0].equals("ALL?")){
			String s ="";
			for (int i = 1; i<messRecu.length; i++)
			    s=s+messRecu[i]+" ";
			this.partie.diffMess("MESA "+" "+this.id+" "+s+"+++");
			envoieMess("ALL!***");
		    }

		    else if(messRecu[0].equals("SEND?")){
			String s = "";
			for (int i = 2; i<messRecu.length; i++)
			    s+=messRecu[i]+" ";
			if(this.partie.envoieMess(messRecu[1], "MESP "+this.id+" "+s+"+++"))
			    envoieMess("SEND!***");
			else
			    envoieMess("NOSEND***");
		    }
		    else if(messRecu[0].equals("QUIT"))
			break;
		    
		    else
			envoieMess("ERROR***");
		    
		}
	    fermer();
	}
	catch (Exception e){	
	    /*System.out.println(e);
	    e.printStackTrace();
	    envoieMess("ERROR***");*/
	}
	
    }


    //FONCTIONS DE CONNECTION/DECONNECTION AU PARTIE            
    //Permet d'éssayer de se connecter à la partie numéro p			
    private void connectionPartie (int p, String id, int portUDP) {
	try {
	    List<Partie> lp = this.serv.listeP;
	    if (p<lp.size() && this.partie==null && lp.get(p).connectionPartie(this)) {
		this.partie = lp.get(p);
		if (id.length()>8)
		    this.id = id.substring(0, 7);
		else
		    this.id = id;
		this.portUDP = portUDP;
		envoieMess("REGOK "+p+"***");
	    }
	    else
		envoieMess("REGNO***");
	}
	catch (Exception e) {
	    System.out.println(e);
	    e.printStackTrace();
	    envoieMess("ERROR***");
	}
    }
    
    //Permet d'essayer de se déconnecter de la partie
    private void deconnectionPartie () {
	try {
	    if (this.partie!=null && this.partie.deconnection(this)) {
		envoieMess("UNREGOK "+this.serv.listeP.indexOf(this.partie)+"***");
		if(this.partie.isEmpty())
		    this.serv.fermerPartie(this.partie);
		this.partie = null;
	    }
	    else 
		envoieMess("DUNNO***");
	} catch (Exception e) {
	    System.out.println(e);
	    e.printStackTrace();
	    envoieMess("ERROR***");
	}
    }
        


    //FONCTION D'ENVOIE ET DE RECEPTION DES MESSAGES TCP
    public boolean envoieMess (String s) {
	try {
	    this.pw.print(s);
	    this.pw.flush();
	    this.serv.pause(this, 500, null);
	    return true;
	}
	catch (Exception e) {
	    System.out.println(e);
	    e.printStackTrace();
	    return false;
	}
    }

    private void envoieListParti() {
	List<Partie> l = this.serv.listeP;
	envoieMess("GAMES "+l.size()+"***");
	for (int i=0; i<l.size(); i++)
	    envoieMess ("GAME "+i+" "+l.get(i).getNbrJoueur()+"***");
    }
    
    public boolean envoieMessDepFant() {
	return envoieMess("MOF "+this.posx+" "+this.posy+" "+this.score+"***");
    }

    public boolean envoieMessDep() {
	return envoieMess("MOV "+this.posx+" "+this.posy+"***");
    }
        
    public String[] receptionMess(){
	try {
	    String s = "";
	    char c0 = (char)reader.read();
	    char c1 = (char)reader.read();
	    char c2 = (char)reader.read();
	    while(c0!='*' || c1!='*' || c2!='*') {
		s = s+c0;
		c0 = c1;
		c1 = c2;
		c2 = (char)reader.read();
	    }
	    System.out.println(s);
	    return s.split(" ");
	}
	catch (Exception e) {
	    System.out.println(e);
	    e.printStackTrace();
	}
	String[] res = new String[0];
	return res;
    }


    //FONCTIONS POUR SET LES COORDONNEES ET LE SCORE DU JOUEUR
    public void setPosScore(int x, int y, int score) {
	this.posx = x;
	this.posy = y;
	this.score = score;
    }

    public void setPos (int x, int y) {
	this.posx = x;
	this.posy = y;
    }

    public void setScore (int s) {
	this.score += s;
    }


    public InetAddress getIP(){
	return this.sock.getInetAddress();
    }
    
    public void fermer () {
	try {
	    envoieMess("BYE***");
	    this.reader.close();
	    this.pw.close();
	    this.sock.close();
	}
	catch (Exception e) {
	    System.out.println(e);
	    e.printStackTrace();
	}
    }

	
}
