import java.net.*;
import java.io.*;
import java.util.*;

public class Partie {

    public List<Joueur> listeJ;
    private Labyrinthe lab;
    private Entite[][] posEnt;
    private List<Fantome> listeFant;
    private List<Thread> liste_thread_fant;
    public Serveur serv;
    public boolean finit;
    private int nbrMaxJ = 10;
    
    public int PortMultiDiff;
    public String IPMultiDiff;
    private DatagramSocket dataSock;

    public Partie (Serveur s) {
	try{
	    this.serv = s;
	    this.listeJ = new ArrayList<Joueur>();
	    this.listeFant = new ArrayList<Fantome>();
	    this.lab = new Labyrinthe(20, 20);
	    this.dataSock = new DatagramSocket();
    	    this.PortMultiDiff=4343;
	    this.IPMultiDiff = "225.0.0.1";
	}
	catch(Exception e) {
	    System.out.println(e);
	    e.printStackTrace();
	}
    }

    public Partie (Serveur s, int nbrJoueur) {
	try{
	    this.serv = s;
	    this.nbrMaxJ = nbrJoueur;
	    this.listeJ = new ArrayList<Joueur>();
	    this.listeFant = new ArrayList<Fantome>();
	    this.lab = new Labyrinthe(20, 20);
	    this.dataSock = new DatagramSocket();
	    this.PortMultiDiff=4343;
	    this.IPMultiDiff = "225.0.0.1";
	}
	catch(Exception e) {
	    System.out.println(e);
	    e.printStackTrace();
	}
    }
    
    private void lancement_jeu() {
	this.serv.partie_lancer(this);
	finit = false;
	this.posEnt = new Entite[this.lab.hauteur][this.lab.largeur];
	this.liste_thread_fant = new ArrayList<Thread>();
	for (int j=0; j<this.listeJ.size(); j++){
	    Fantome f = new Fantome(j, this);
	    this.listeFant.add(f);
	    int[] pos = placer(f);
	    f.setPos(pos[0], pos[1]);
	    Thread th = new Thread(f);
	    this.liste_thread_fant.add(th);
	    th.start();
	}
	for (int i=0; i<this.listeJ.size(); i++){
	    Joueur j = this.listeJ.get(i);
	    int[] pos = placer(j);
	    j.setPosScore(pos[0], pos[1], 0);
	    this.serv.reveiller(j, null);
	}
    }
    
    //renvoi true si le joueur a manger un/plusieur fantome (s)
    
    public synchronized boolean deplacementJ (Joueur j, int direction, int distance) {
	boolean result=false;
	int x=j.posx;
	int y=j.posy;
	int [] t=new int[2];
	this.posEnt[x][y] = null;
	t=deplacementUn (x,y,direction);
	x=t[0];
	y=t[1];
	while (distance !=0 && x!=-1 && lab.lab[x][y]!=0){
	    distance-=1;
	    if(posEnt[x][y]!=null && posEnt[x][y].fant){
		Fantome f = (Fantome)this.posEnt[x][y];
		this.posEnt[x][y] = null;
		j.setScore(f.point);
		result=true;
		diffMess("SCOR "+j.id+" "+j.score+" "+x+" "+y+"+++");
		mangerFantome (f);
	    }
	    j.setPos(x,y);
	    t=deplacementUn (x,y,direction);
	    x=t[0];
	    y=t[1];	
	}
	this.posEnt[j.posx][j.posy] = j;
	return result;
    }
    
    public int[] deplacementUn (int x, int y, int direction) {
	int [] t= new int[2];
	t[0]=x;
	t[1]=y;
	switch (direction)
	    {
	    case 0:
		t[0]=t[0]-1;
		break;
	    case 1:
		t[1]=t[1]+1;
		break;
	    case 2:
		t[0]=t[0]+1;
		break;
	    case 3:
		t[1]=t[1]-1;
		break;
	    }
	if (t[0]<0 || t[1]<0 || t[0]>=lab.lab.length || t[1]>=lab.lab[0].length){
	    t[0]=-1;
	    t[1]=-1;
	}
	return t;
    }
    
    public synchronized void deplacementF (Fantome f) {
	int[] t = new int[2];
	int x=f.posx;
	int y=f.posy;
	int distance = (int) (Math.random() * lab.lab.length )+1;
	int direction =(int) (Math.random() * 4 );
	this.posEnt[x][y] = null;
	t=deplacementUn (x,y,direction);
	x=t[0];
	y=t[1];
	while (distance !=0 && x!=-1 && lab.lab[x][y]!=0){
	    distance-=1;
	    f.setPos(x,y);
	    t=deplacementUn (x,y,direction);
	    x=t[0];
	    y=t[1];	
	}
	diffMess("FANT "+f.posx+" "+f.posy+"+++");
	this.posEnt[f.posx][f.posy] = f;
    }
    
    private void mangerFantome(Fantome f){
	listeFant.remove(f);
	f.alive = false;
	if (getNbrFant()==0)
	    finPartie();
    }
    
    public int[] placer(Entite e){
	int[] tab = new int[2];
	int x=(int)(Math.random() * lab.hauteur );
	int y=(int)(Math.random() * lab.largeur );
	while (posEnt[x][y]!=null || lab.lab[x][y]!=1){
	    x=(int)(Math.random() * lab.lab.length );
	    y=(int)(Math.random() * lab.lab[0].length );
	}
	posEnt[x][y]=e;
	tab[0]=x;
	tab[1]=y;
	return tab;
    }
    
    public synchronized boolean connectionPartie (Joueur j) {
	if(listeJ.size()<nbrMaxJ) {
	    listeJ.add(j);
	    return true;
	}
	else
	    return false;
    }
    
    //Deconnecte j si il était présent dans la partie
    public synchronized boolean deconnection (Joueur j) {
	if(listeJ.contains(j)) {
	    listeJ.remove(j);
	    return true;
	}
	else
	    return false;
    }
    
    public void deconnectionGeneral () {
	while (!listeJ.isEmpty()){ 
	    Joueur j = listeJ.get(0);
	    deconnection(j);
	    j.fermer();
	}
    }
    
    
    //FONCTION D'ENVOIE DE MESSAGE UDP
    public synchronized boolean envoieMess (String id, String mess) {
	try{
	    Joueur j = getJoueur(id);
	    byte[]data = mess.getBytes();
	    DatagramPacket paquet = new DatagramPacket (data, data.length, j.getIP(), j.portUDP);
	    this.dataSock.send(paquet);
	    return true;
	}
	catch (Exception e){
	    System.out.print(e);
	    e.printStackTrace();
	    return false;
	}
    }
    
    public void diffMess(String mess){
	try{
	    byte[]data = mess.getBytes();
	    DatagramPacket paquet = new DatagramPacket (data, data.length, InetAddress.getByName(this.IPMultiDiff), this.PortMultiDiff);
	    this.dataSock.send(paquet);
	}
	catch (Exception e){
	    System.out.print(e);
	    e.printStackTrace();
	}
    }
    
    
    //FONCTIONS GET
    public int getNbrJoueur(){
	return listeJ.size();
    }
    
    public int getNbrFant(){
	return this.listeFant.size();
    }
    
    public String[] getJoueurs (){
	String[]res = new String[listeJ.size()];
	for (int i=0; i<res.length; i++)
	    res[i] = listeJ.get(i).id;
	return res;
    }
    
    public Joueur getJoueur (String s){
	for (int i=0; i<this.listeJ.size(); i++){
	    if(this.listeJ.get(i).id.equals(s))
		return this.listeJ.get(i);
	}
	return null;
    }
    
    public int[] getSizeLab () {
	int[]res = new int[2];
	res[0] = lab.hauteur;
	res[1] = lab.largeur;
	return res;
    }
    
    public Thread getThFant (Fantome f) {
	return this.liste_thread_fant.get(this.listeFant.indexOf(f));
    }
    
    
    public boolean isEmpty(){
	return listeJ.isEmpty();
    }
    
    public synchronized boolean allReady(){
	boolean all = true;
	for (int i=0; i<this.listeJ.size(); i++){
	    if (this.listeJ.get(i).ready == 0)
		all=false;
	}
	if(all)
	    lancement_jeu();
	return all;
    }
    
    private Joueur scoreMax(){
	Joueur max = this.listeJ.get(0);
	for (int i = 1; i<listeJ.size(); i++) {
	    if(this.listeJ.get(i).score > max.score)
		max=this.listeJ.get(i);
	}
	return max;
    }
    
    public void fermer() {
	deconnectionGeneral();
	dataSock.close();
	while(!listeFant.isEmpty())
	    listeFant.get(0).alive=false;
    }
    
    private void finPartie() {
	this.finit = true;
	Joueur j = scoreMax();
	diffMess("END "+j.id+" "+j.score+"+++");
	this.serv.fermerPartie(this);
    }
    
}
