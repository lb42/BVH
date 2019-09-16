import java.util.ArrayList;
import java.util.Iterator;

class CheminAlignement{

    // On va être obligé de changer de structure de donnée !

    ArrayList<CouplesIndices> chemin;
    
    private class CouplesIndices{
	int indiceF1;
	int indiceF2;

	CouplesIndices(int i1, int i2){
	    indiceF1 = i1;
	    indiceF2 = i2;
	};
	int i1(){
	    return indiceF1;
	};
	int i2(){
	    return indiceF2;
	};
	public String toString(){
	    String res;
	    res = "("+indiceF1+", "+indiceF2+")";
	    return res;
	};
    }

    CheminAlignement(){
	chemin = new  ArrayList<CouplesIndices>();
    };

    /*public void concat(CheminAlignement c2){
	int ai, aj, i, j;
	
	// On vérifie que le chemin global reste bien croissant !
	ai=chemin.get(chemin.size()-1).i1();
	aj=chemin.get(chemin.size()-1).i2();
	i=c2.chemin.get(0).i1();
	j=c2.chemin.get(0).i2();
	if (i < ai || j < aj){
	    System.err.println("PB AJOUT CONCAT");
	}
	chemin.addAll(c2.chemin);
	};*/
    
    public void concat(CheminAlignementPartiel c2){
	
	// On vérifie que le chemin global reste bien croissant !
	for (int i=c2.size()-2; i > 0; i = i-2){
	    chemin.add(new CouplesIndices(c2.get(i), c2.get(i+1)));
	};
	//chemin.addAll(c2.chemin);
    };
    
    public void addPoint(int i, int j){
	// On devrait vérifier que i >= i dans le précédent couple
	// et j >= j dans le précédent couple.
	//On ajoute en tête...
	int ai, aj;

	if (chemin.size() > 0){
	    ai = chemin.get(0).i1();
	    aj = chemin.get(0).i2();
	    if (i > ai || j > aj){
		System.err.println("PB AJOUT");
	    }
	}
	chemin.add(0, new CouplesIndices(i, j));
    };

    public String toString(){
	CouplesIndices c;
	String res="";

	int j = 0;
	for (Iterator<CouplesIndices> i = chemin.iterator(); i.hasNext();){
	    c = i.next();
	    res = res+c.i1()+":"+c.i2()+" ";
	    j = j + 1;
	};
	return res;
    };

    public String toString(UTF32String s1, UTF32String s2){
	CouplesIndices c;
	String res="";
	int j = 0;
	
	for (Iterator<CouplesIndices> i = chemin.iterator(); i.hasNext();){
	    c = i.next();
	    if (c.i1() < s1.length() && c.i2() < s2.length()){
		res = res+s1.charAt(c.i1()).toString()+":"+s2.charAt(c.i2()).toString();
	    };
	    j = j+1;
	};
	return res;
    };

    public String toStringFin(){
	CouplesIndices c;
	String res="";

	int j = 0;
	for (int k=20; k > 0 ; k--){
	    c = chemin.get(chemin.size()-k);
	    res = res+c.i1()+":"+c.i2()+" ";
	};
	return res;
    };

    
    // les indices dans la première chaîne qui correspond à une certaine valeur
    // dans la seconde chaîne. (le premier puis le deuxième).
    // Le plus souvent, ça devrait être le même !
    public int premierIndiceS1PourIndiceS2(int indiceS2){
	// normalement, les indices sont croissants... Ben, en fait, non !!!!!
	int a, b, m;
	boolean trouve;
	int res = -1;
	int vm;

	//System.err.println("premierIndiceS1PourIndiceS2("+indiceS2+")");
	a = 0;
	b = chemin.size()-1;
	trouve = false;
	while ((!trouve) && (b != a) &&  (b != a +1)){
	    m = (a+b)/2;
	    //System.err.println("m="+m+" a="+a+" b="+b);
	    vm = chemin.get(m).i2();
	    if (vm == indiceS2){
		trouve = true;
		a = m;
	    }
	    else{
		if (vm > indiceS2){
		    b = m;
		}
		else{
		    a = m;
		}
	    }
	};
	if (trouve){
	    // on remonte en arrière....
	    //int i = a-1; //Pourquoi a - 1 ??
	    int i = a;
	    while (i > 0 && chemin.get(i).i2() == indiceS2){
		//System.err.print("§i="+i+" "+chemin.get(i).i2()+" ");
		i = i-1;
	    };
	    res = chemin.get(i+1).i1();
	}
	else{
	    //System.err.println("premierIndiceS1PourIndiceS2 ; Non trouvé !");
	    int i = b;
	    while (i > 0 && chemin.get(i).i2() == indiceS2){
		i = i-1;
	    };
	    res = chemin.get(i+1).i1();
	}
	//System.err.println("on renvoie :"+res);
	return res;
    };

    public int dernierIndiceS1PourIndiceS2(int indiceS2){
	// normalement, les indices sont croissants...
	int a, b, m;
	boolean trouve;
	int res = -1;
	int vm;
	
	a = 0;
	b = chemin.size();
	trouve = false;
	while ((!trouve) && b != a &&  (b != a +1)){
	    m = (a+b)/2;
	    vm = chemin.get(m).i2();
	    if (vm == indiceS2){
		trouve = true;
		a = m;
	    }
	    else{
		if (vm > indiceS2){
		    b = m;
		}
		else{
		    a = m;
		}
	    }
	};
	if (trouve){
	    // on avance.
	    int i = a+1;
	    while (i < chemin.size() && chemin.get(i).i2() == indiceS2){
		i = i+1;
	    };
	    res = chemin.get(i-1).i1();
	}
	else{
	    res = chemin.get(b-1).i1();
	}
	return res;
    };

    ArrayList<CouplesIndices> tousIndicesPour(int indiceS2){
	ArrayList<CouplesIndices> res = new ArrayList<CouplesIndices>();
	CouplesIndices cCour;
	
	for (Iterator<CouplesIndices> i = chemin.iterator(); i.hasNext();){
	    cCour = i.next();
	    if (cCour.i2() == indiceS2){
		res.add(cCour);
	    };
	};
	return res;
    }

    String str_tousIndicesPour(int indiceS2){
	String res="";
	CouplesIndices cCour;
	int k=0;
	int mink=Integer.MAX_VALUE;
	int maxk=0;
	
	for (Iterator<CouplesIndices> i = chemin.iterator(); i.hasNext();){
	    cCour = i.next();
	    if (cCour.i2() == indiceS2){
		res=k+"->"+cCour+res;
		if (k < mink){
		    mink = k;
		}
		if (k > maxk){
		    maxk = k;
		};
	    };
	    k=k+1;
	};
	int v;
	boolean trou= false;
	v = chemin.get(mink).i2();
	for (k=mink; k <= maxk && !trou; k++){
	    trou = (chemin.get(k).i2() != v);
	};
	if (trou){
	    System.err.println("TROU");
	    for (k=mink; k <= maxk; k++){
		System.err.print(""+k+"->"+chemin.get(k));
	    };
	};
	return res;
    }
}
