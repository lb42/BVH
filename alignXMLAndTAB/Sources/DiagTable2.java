
class DiagTable2{
    int I;
    int J;
    //int [][] tt;
    Colonne[] tt;
    int dh;
    
    class Colonne{
	int b;
	int h;
	int[] tc;

	Colonne(int col){
	    b = b(col);
	    h = h(col);
	    tc = new int[h-b+1];

	    //piste de réflexion : si on a des minimums dans
	    // la colonne précédente, on se centre dessus pour la suivante...
	    //On cherche le minimum si il y en a un...
	};

	/*Colonne(int col){
	    boolean minConnexes=false;
	    int idMin=0;
	    int leMin;
	    if (col < 2){
		b=0;
		h = dh+col;
		if (h > J)
		    h=J;
		tc = new int[h+1];
	    }
	    else{
		leMin = Integer.MAX_VALUE;
		System.out.println("cherche min");
		for (int i=tt[col-1].b; i < tt[col-1].h; i++){
		    System.out.println("i = "+i+" tc[col-1]="+tt[col-1].tc[i]);
		    if (tt[col-1].tc[i] < leMin){
			leMin = tt[col-1].tc[i];
			idMin = i;
			minConnexes=true;
		    }
		    else{
			if (tt[col-1].tc[i] == leMin){
			    //System.err.println("nouvel idmin : "+i+" min = "+leMin);
			    if (i == idMin+1){
				idMin = i;
			    }
			    else{
				minConnexes=false;	
			    }
			}
		    }
		}
		if (minConnexes){
		    if (idMin+tt[col-1].b < dh){
			b=0;
			h=dh;
		    }
		    else{
			if (idMin+dh+tt[col-1].b  >= J){
			    h = J;
			    b = h-dh;
			}
			else{
			    b = tt[col-1].b+idMin - dh;
			    h = +tt[col-1].b+idMin + dh;
			}
		    }
		    tc= new int[h-b+1];

		}
		else{
		    System.err.println("Pb min, col="+col+" idMin = "+idMin);
		    System.exit(42);
		}
	    }
	    }; */
	
	void set(int i, int v){
	    if (i >=b && i <= h){
		tc[i-b]=v;
	    };
	};
	int get(int i){
	    int res = Integer.MAX_VALUE; ;
	    if (i >=b && i <= h){
		res=tc[i-b];
	    };
	    return res;
	};
    };
    

    static int outOfDiagValue = Integer.MAX_VALUE; 
    
    int b(int i){
	//return (J*i)/I - dh;
	float res;
	res = ((float)J*i)/I -dh;
	if (res < 0){
	    res = 0;
	};
	return (int)res;
    }

    int h(int i){
	//return (J*i)/I + dh;
	float res;
	res = ((float)J*i)/I +dh;
	if (res > J){
	    res = J;
	};
	return (int)res;
    }

    
    DiagTable2(int taille_l1, int taille_l2, int demi_hauteur){
	I = taille_l1;
	J = taille_l2;
	dh = demi_hauteur;
	//tt = new int[taille_l1+1][dh*2+2];
	tt = new Colonne[taille_l1+1];
	/*for (int col = 0; col < I; col++){
	    tt[col] = new Colonne(col);
	    };*/
	for (int col = 0; col < I; col++){
	    tt[col] = null;
	};
    };

    public void set(int i, int j, int v){
	/* i compris entre 0 et I */
	/* j compris entre 0 et J */
	/* et en plus entre b(i) et h(i) */
	
	if (tt[i] == null){
	    //System.out.println("nouvelle colonne pour "+i);
	    //System.exit(42);
	    tt[i] = new Colonne(i);
	};
	if (i >= 0 && i <= I){
	    tt[i].set(j,v);
	}
    };

    public int get(int i, int j){
	int v;

	v =  Integer.MAX_VALUE; 
	if (i >= 0 && i <= I){
	    v = tt[i].get(j);
	}
	return v;
    };
}
