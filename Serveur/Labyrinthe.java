public class Labyrinthe {
    
    public int hauteur;
    public int largeur;
    public int [][] lab; //matrice représentant le labyrinthe, 0 pour un murs, 1 pour une route vide
    public int tailleRoute; //Nombre de case de route

    public Labyrinthe (int h, int l) {
	hauteur = h;
	largeur = l;
	tailleRoute = 0;
	GenererLab (h, l);
    }

    public void GenererLab (int h, int l) {
	lab = new int [h][l];
	int x=hauteur/2, y=largeur/2;
	GenererChemin (x, y);
	CompleterLab (x, y);
    }

    public void GenererChemin (int x, int y) {
	if (0<=x && x<hauteur && 0<=y && y<largeur && lab[x][y]==0) {
	    lab[x][y] = 1;
	    tailleRoute++;
	    double mur = Math.random()*8.0;
	    double direction;
	    for (int i=0; i<(int)mur; i++)
		{
		    direction = Math.random()*4.0;
		    if (direction<1.0)
			GenererChemin (x-1, y);
		    else if (direction<2.0)
			GenererChemin (x+1, y);
		    else if (direction<3.0)
			GenererChemin (x, y-1);
		    else
			GenererChemin (x, y+1);
		}
	}
    }

    public void CompleterLab (int x, int y){
	for (int i=0; i<hauteur; i++) {
	    for (int j=0; j<largeur; j++) {
		if(lab[i][j]!=1)
		    lab[i][j] = -1;
	    }
	}
    }

    public void AffLab(){
	System.out.print(" ");
	for (int i=0; i<largeur; i++)
	    System.out.print("─");
	System.out.println("");
	for (int x=0; x<hauteur; x++) {
	    System.out.print("|");
	    for (int y=0; y<largeur; y++) {
		if(lab[x][y]==-1)
		    System.out.print("❌");
		else
		    System.out.print(" ");
	    }
	    System.out.println("|");
	}
	System.out.print(" ");
	for (int j=0; j<largeur; j++)
	    System.out.print("─");
	System.out.println("");
    }

}
