
class CheminAlignementPartiel{
    int [] chemin;
    int premIndexLibre;

    CheminAlignementPartiel(int taille){
	//System.err.println("Tailel à la création : "+taille);
	chemin = new int[taille*2];
	premIndexLibre = 0;
    };

    /* Attention : on n'ajoute pas en tête ! */
    public void addPoint(int i, int j){
	chemin[premIndexLibre]= i;
	chemin[premIndexLibre+1] = j;
	premIndexLibre = premIndexLibre+2;
    };

    public int size(){
	return premIndexLibre;
    }

    public int get(int ind){
	return chemin[ind];
    };
}
