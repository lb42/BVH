import java.util.Scanner;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Enumeration;

/* ================================================================================ */

/*
RESTE A FAIRE : enlever les ancres qui sont contenues dans d'autres ancres.
Iterer sur les ancres jusqu'à couvrir tout le fichier...
*/

/* ================================================================================ */


// Prend en paramètres un fichier XML et un fichier tabulaire dont la première colonne est de type mot
// ou suite de mots (ex : résultat d'annotation treeTagger) et donne
// comme résultat le fichier tabulaire de départ avce deux colonnes supplémentaires : indice de début et
// indice de fin du mot (ou suite de mot) dans le fichier xml de départ.
//
// Le but est ensuite de tranformer le fichier tabulaire obtenu en fichier compagnon pour Xmixer...

public class alignXMLAndTab {

    
    static int I, J;
    static int largeurDiagonale = 500;
    static int LG_MIN_ANCRES=15;
    
	public static void main(String[] args) {
 
	    //Scanner scn = new Scanner(System.in);
	    //Scanner scn = null;
	    XMLdocForString xdoc;
	    TabularDocForString tdoc;
	    int numChampTexte;
	    
	    // On fabrique les deux chaînes à partir du fichier XML et du fichier tabulaire. 
	    // pour le XML : XMLdocForString
	    // pour le tabulaire : TABdocForString !

	    if (args.length < 2){
		System.err.println("Usage : java -jar alignXMLAndTab fichierXML fichierTabulaire");
		System.err.println("Ou : java -jar alignXMLAndTab fichierXML fichierTabulaire indexColonneMots ; dans le cas où ce n'est pas la première");
		System.exit(1);
	    };

	    
	    try{
		/*scn = new Scanner(new File(args[1]), "UTF-8");*/
		String s1;
		String s2;


		
		xdoc = new XMLdocForString(args[0]);
		s1 = xdoc.getText();
		//System.err.println("s1 lue = "+s1);
		tdoc = new TabularDocForString(args[1]);

		if (args.length > 2){
		    s2 = tdoc.getText(Integer.parseInt(args[2]));
		    numChampTexte = Integer.parseInt(args[2]);
		}
		else{
		   
		    s2 = tdoc.getText();
		    numChampTexte = 0;
		};
		//System.err.println("s1 et s2 lues : s2 = "+s2);

		
		UTF32String us1 = new UTF32String(s1);
		UTF32String us2 = new UTF32String(s2);

		//System.err.println("us1 = "+us1);
		//System.err.println("us2 = "+us2);
		

		
		CheminAlignement calign = null;
		CheminAlignement calignBis = null;
		
		char eot='\003';
		//char eot='#';
		// Ce qu'on voudrait maintenant, c'est reporter les indices sur le tabulaire de départ !
		// dans un premier temps, on reporte les indices sur les lignes de s2 :

		//System.err.println("Chemin :");
		//System.err.println(""+calign);
		Scanner scTabFile = new Scanner(new File(args[1]), "UTF-8");
		Scanner scTabFile2 = new Scanner(new File(args[1]), "UTF-8");
		int indiceS1Un = 0;
		int precIndiceS2Un = 0;
		String tokenUn;
		String ltabUn;
		Boolean trouve;
		UTF32String tokenUnBis;
		int i2=0;
		int j2 = 0;
		Ancre a;
		int iCour;
		ArrayList<Ancre> l=null;
		int indiceListe;
		Hashtable<String, ArrayList<Ancre>> ancres = new Hashtable<String, ArrayList<Ancre>>();
		try{
		    while (scTabFile2.hasNext()){
			ltabUn = scTabFile2.nextLine();
			if (ltabUn.split("\\t").length > 1){
			    tokenUn = ltabUn.split("\\t")[numChampTexte];
			    tokenUnBis = new UTF32String(tokenUn);
			    j2=i2+tokenUnBis.length();
			    System.err.println(tokenUnBis);
			    /* On l'ajoute aux ancres si sa longueur est supérieure à LG_MIN_ANCRE */
			    /* les ancres sont indexées sur i2 et j2 */
			    if (tokenUnBis.length() >= LG_MIN_ANCRES){
				l=ancres.get(tokenUn);
				System.err.println("token "+tokenUnBis+" l = "+l);
				if (l == null){
				    ancres.put(tokenUn, new ArrayList<Ancre>());
				    l = ancres.get(tokenUn);
				    System.err.println("Après ajout : l="+l);
				}
				l.add(new Ancre(tokenUnBis, -1, -1, i2, j2));
				System.err.println("Après ajout elt : l="+l);
				System.err.println("On vérifie : ancres.get ="+ancres.get(tokenUn));
			    }
			    i2=j2;
			}
		    }

		    
		    /* on essaie d'ancrer les ancres ! */
		    /* Pour chaque token : */
		    for (Enumeration<String> e=ancres.keys(); e.hasMoreElements();){
			tokenUn=e.nextElement();
			System.err.println("|"+tokenUn+"|"+ancres.get(tokenUn));
			/* on essaie d'aligner tokenUn (enfin, sa version utf32) sur S1 */
			tokenUnBis=ancres.get(tokenUn).get(0).getToken();
			indiceS1Un=0;
			iCour=0;
			indiceListe=0;

			/* Il faut aller jusqu'au bout : si on a trop de matches, il faut éliminer cette ancre...*/
			/* to do... */

			
			/*while (indiceListe < ancres.get(tokenUn).size() && indiceS1Un < us1.length()){ */
			while (indiceS1Un < us1.length()){
			    while ( iCour < tokenUnBis.length() &&
				    (indiceS1Un+iCour) < us1.length() && 
				    tokenUnBis.charAt(iCour).equals(us1.charAt(indiceS1Un+iCour)) ){
				iCour++;
				//System.out.print("+");
			    }
			    if (iCour == tokenUnBis.length()){ /* O a un match ! */
				System.err.println("matche : "+indiceS1Un);
				if (indiceListe < ancres.get(tokenUn).size()){
				    ancres.get(tokenUn).get(indiceListe).set_i1(indiceS1Un);
				    ancres.get(tokenUn).get(indiceListe).set_j1(indiceS1Un+iCour);
				};
				indiceS1Un = indiceS1Un+iCour;
				iCour=0;
				indiceListe++;
			    }
			    else{
				indiceS1Un++;
				iCour=0;
				//System.out.print("-");
			    }
			}
			if ((indiceListe - ancres.get(tokenUn).size()) != 0){
			    System.err.println("Pb : trouve "+indiceListe+" matches pour "+tokenUn+" au lieu de "+ancres.get(tokenUn).size());
			    /* On enlève cette ancre (elle est probablement contenue dans une autre plus longue... */
			    ancres.remove(tokenUn);
			}
		
		    }
		    /* on regarde ce que ça donne... */
		    /*System.out.println("VERIF....");
		    for (Enumeration<String> e=ancres.keys(); e.hasMoreElements();){
			tokenUn=e.nextElement();
			System.out.println("|"+tokenUn+"|"+ancres.get(tokenUn));
		    };
		    System.out.println("ESSAI COMP");
		    for (Enumeration<String> e=ancres.keys(); e.hasMoreElements();){
			tokenUn=e.nextElement();
			for (Iterator<Ancre> i=ancres.get(tokenUn).iterator(); i.hasNext();){
			    a=i.next();
			    System.out.println(a.get_i1()+"\t"+a.get_j1()+"\t<w xmlns=\"http://www.tei-c.org/ns/1.0\" word=\""+a.getToken()+"\">");
			};
			};*/
		}
		catch (Exception e){
		    System.out.println("Pb affichage "+e.getMessage()+e.getCause());
		    e.printStackTrace();
		};


		boolean fini = false;
		int i0 = 0; /* dans S1 */
		int i1 = 0; /* dans S2 */
		calign = null;
		calignBis = null;
		while (!fini){
		    
		    /* Il faut ordonner et filtrer les ancres... */
		    /* on cherche la plus petite */
		    int minA = Integer.MAX_VALUE;
		    Ancre aMin = null;

		    for (Enumeration<String> e=ancres.keys(); e.hasMoreElements();){
			tokenUn=e.nextElement();
			for (Iterator<Ancre> i=ancres.get(tokenUn).iterator(); i.hasNext();){
			    a=i.next();
			    //System.out.println(a.get_i1()+"\t"+a.get_j1()+"\t<w xmlns=\"http://www.tei-c.org/ns/1.0\" word=\""+a.getToken()+"\">");
			    if (a.get_i1() < minA){
				minA = a.get_i1();
				aMin = a;
			    }
			    else{
				if (a.get_i1() == minA){
				    // Pb ! on a deux mins !
				    if (minA == Integer.MAX_VALUE){
					//fini = true;
				    }
				    else{
					System.err.println("pb deux mins : "+a+" et "+aMin);
					System.exit(42);
				    }
				}
			    }
			};
		    };
		    if (minA == Integer.MAX_VALUE){
			fini = true;
		    };
		    if (!fini){
			System.err.println("Plus petite ancre : "+aMin);

			/* On tente l'alignement entre i0, j0 et aMin.get */
			calignBis = editDist3(us1, us2, i0, aMin.get_i1(), i1, aMin.get_i2());
			
			if (calign==null){
			    calign = calignBis;
			}
			else{
			    calign.concat(calignBis);
			}

			/* on essaie la suivante ... */
			i0 = aMin.get_i1()+1;
			i1 = aMin.get_i2()+1;
		
			aMin.set_i1(Integer.MAX_VALUE);

		
		    }
		}

		/* Il nous reste à faire la fin du fichier.... */
		calignBis = editDist3(us1, us2, i0, us1.length(), i1, us2.length());
		if (calign != null){
		    calign.concat(calignBis);
		}
		else{
		    calign = calignBis;
		};
		
		System.err.println("calign = ");
		System.err.println(calign);
		
		int indiceS2 = 0;
		int precIndiceS2 = 0;
		String token;
		String ltab;

		int numColonne;
		if (args.length > 2){
		    numColonne = Integer.parseInt(args[2]);
		}
		else{
		    numColonne=0;
		}
		
		try{

		    // On écrit le résultat ....   RESULTAT
		    
		    int p1, p2;
		    
		    while (scTabFile.hasNext()){
			ltab = scTabFile.nextLine();
			if (ltab.split("\\t").length > 1){
			    //System.err.println("on traite le mot : "+ltab);
			    p1 = calign.dernierIndiceS1PourIndiceS2(indiceS2);
			    System.out.print(calign.dernierIndiceS1PourIndiceS2(indiceS2));
			    precIndiceS2 =  indiceS2;

			    indiceS2 = indiceS2 + ltab.split("\\t")[numColonne].length();
			    //System.err.println("indice S2 (après) = "+indiceS2);
			    p2 = calign.premierIndiceS1PourIndiceS2(indiceS2);
			    System.out.print("\t"+calign.premierIndiceS1PourIndiceS2(indiceS2));
			    System.out.println("\t"+ltab);
			    //indiceS2 = indiceS2+1;
			    if (precIndiceS2 == indiceS2){
				System.err.println("Pb sur : "+precIndiceS2+":"+indiceS2+ltab);
				// On veut la fin du chemin pour vérifier !
				//System.err.println("Couples Chemin :"+calign.tousIndicesPour(indiceS2));
				System.exit(42);
			    };
			    if (p2 - p1 !=  ltab.split("\\t")[numColonne].length()){

				System.err.println("Pb lg sur : "+precIndiceS2+":"+indiceS2+" "+ltab);
				//System.err.println("fin du chemin complet :"+calign.toStringFin());

				System.err.println(p1+"\t"+p2+"\t"+ltab);
				/*for (int c=0; c <=  ltab.split("\\t")[numColonne].length(); c++){

				    System.err.println("Couples Chemin de :"+ (precIndiceS2+c)+" = "+calign.str_tousIndicesPour(precIndiceS2+c));
				    };*/
				    /*System.err.println("Couples Chemin de :"+indiceS2+" = "+calign.tousIndicesPour(indiceS2));
				      System.err.println("Couples Chemin de :"+precIndiceS2+" = "+calign.tousIndicesPour(precIndiceS2));*/
			    }
			}
			else{
			    System.out.println(ltab);		
			}
		    };
		}
		catch (Exception e){
		    System.out.println("Pb affichage "+e.getMessage()+e.getCause());
		    e.printStackTrace();
		};
		    
	    
	
	    
		
 
	    }
	    catch (Exception e){
		System.err.println("fichier non trouvé ou problème de conversion vers UTF32...");
		e.printStackTrace();
	    };
	
	}

    public static int max(int a, int b){
	if (a > b){
	    return a;
	}
	else{
	    return b;
	}
    }
    public static int min(int a, int b){
	double ad, bd;

	ad = (double)a;
	bd = (double)b;
	if (ad >= Integer.MAX_VALUE -1){
	    return b;
	}
	else{
	    if (bd  >= Integer.MAX_VALUE -1){
		return a;
	    }
	}
	if (a < b){
	    return a;
	}
	else{
	    return b;
	}
    }



    /* on calcule l'alignement de i1..j1 dans s1 sur i2..j2 dans s2 */
    public static CheminAlignement editDist3(UTF32String s1, UTF32String s2, int i1, int j1, int i2, int j2) {
	int v1, v2;
	int minOnRow;
	
	/*I = j1 - i1 + 1;
	  J = j2 - i2 + 1;*/
	I = j1 - i1;
	J = j2 - i2;
	    

	DiagTable2 dp2 = new DiagTable2(I, J, largeurDiagonale);

	ArrayList<UTF32String> alternativesLonguesCol;
	ArrayList<UTF32String> alternativesLonguesRow;
	UTF32String eqColCour;
	 
	dp2.set(0,0,0);
 
	for (int row = 0; row <= J; row++){
	    dp2.set(0, row, row);
	};

	int vDiag, vHoriz, vVert, addDiag;
	UTF32String sAlt;
	int vDiag2;
	boolean eql;
	
	for (int col = 1; col <= I; col++){
	    dp2.set(col, 0, col);

	    /* les alternatives de longueur supérieure à 1 de s1.charAt-col-1+i1) */
	    alternativesLonguesCol = Translit.longEquiv(s1.charAt(col-1+i1));
	  
	    for (int row = max(1, dp2.b(col)); row <= min(J, dp2.h(col)); row++){
		//System.out.print(""+col+"  "+row+" ");
		
		alternativesLonguesRow = Translit.longEquiv(s2.charAt(row-1+i2));
		
		vDiag = dp2.get(col-1, row-1);
		vHoriz = dp2.get(col-1, row);
		vVert = dp2.get(col, row-1);

		if (Translit.equals(s1.charAt(col-1+i1), s2.charAt(row-1+i2))){
		    addDiag = 0;
		}
		else{
		    addDiag = 1;
		};
		
		if (vDiag != Integer.MAX_VALUE){
		    vDiag = vDiag + addDiag;
		};


		/* On regarde maintenant les alternatives longues par rapport à S1 */
		for (Iterator<UTF32String> i=alternativesLonguesCol.iterator(); i.hasNext();){
		    sAlt = i.next();
		    vDiag2=dp2.get(col-1, row-sAlt.length());
		    /* on compare sAlt et s2.charsAt(row+i2-sAlt.length()....row+i2-1) */
		    //System.out.println("altern longue  de "+s1.charAt(col-1+i1)+" = "+sAlt+" de longueur :"+sAlt.length());
		    eql = true;
		    for (int j=0; j < sAlt.length(); j++){
			//System.out.println("on va en : "+(row-sAlt.length()+j+i2));
			if (row-sAlt.length()+j+i2 >=0){
			    eql= eql && s2.charAt(row-sAlt.length()+j+i2).equals(sAlt.charAt(j));
			}
			else{
			    eql = false;
			}
		    };
		    if (eql){
			addDiag = 0;
		    }
		    else{
			addDiag = sAlt.length();
		    }
		    if (vDiag2 != Integer.MAX_VALUE){
			vDiag2 = vDiag2+addDiag;
		    }
		    if (vDiag2 < vDiag){
			//System.out.println("changement diagonale...");
			vDiag = vDiag2;
		    }
		};
		/* Puis les alternatives longues par rapport à S2 */
		for (Iterator<UTF32String> i=alternativesLonguesRow.iterator(); i.hasNext();){
		    sAlt = i.next();
		    vDiag2=dp2.get(col-sAlt.length(), row-1);
		    eql = true;
		    for (int j=0; j < sAlt.length(); j++){
			if (col-sAlt.length()+j+i1 >= 0){
			    eql = eql && s1.charAt(col-sAlt.length()+j+i1).equals(sAlt.charAt(j));
			}
			else{
			    eql=false;
			}
		    }
		    if (eql){
			addDiag = 0;
		    }
		    else{
			addDiag = sAlt.length();
		    }
		    if (vDiag2 != Integer.MAX_VALUE){
			vDiag2 = vDiag2+addDiag;
		    }
		    if (vDiag2 < vDiag){
			vDiag = vDiag2;
		    }
		}
		
		if (vHoriz != Integer.MAX_VALUE){
		    vHoriz = vHoriz + 1;
		};
		
		if (vVert != Integer.MAX_VALUE){
		    vVert = vVert + 1;
		};
		
		dp2.set(col, row, min(vDiag, min(vHoriz, vVert)));
	    }
	
	    
	}

	  
	    
	// On voudrait maintenant le chemin optimal.
	// On reprend donc à l'endroit :

	   
	System.err.println("Distance : "+dp2.get(I,J));

	CheminAlignement res=new CheminAlignement();
	int col=I;
	int row=J;
	int val;
	val = dp2.get(col,row);
	int stepMinCol, stepMinRow;
	//System.err.println(""+col+":"+row+"->"+val);

	int vaVert, vaDiag, vaDiag2, vaHoriz, mi;
	
	res.addPoint(col+i1, row+i2);
	
	while (col > 0 && row > 0){
	    //System.err.print("ds while"+col+":"+row+"->"+val+" ");
	    vaVert = dp2.get(col, row-1);
	    vaDiag = dp2.get(col-1, row-1);
	    vaHoriz = dp2.get(col-1, row);
	    //System.err.println("vaDiag="+vaDiag+" vaVert="+vaVert+" vaHoriz="+vaHoriz);
	    mi = min(vaDiag, min(vaVert, vaHoriz));
	    stepMinCol = 1;
	    stepMinRow = 1;
	    
	    /* On reprend les alternatives de s1.charAt(col-1) */
	    for (Iterator<UTF32String> i = Translit.longEquiv(s1.charAt(col-1)).iterator(); i.hasNext();){
		sAlt = i.next();
		vaDiag2 = dp2.get(col-1, row-sAlt.length());
		if (vaDiag2 == dp2.get(col,row) && vaDiag2 < vaDiag){
		    vaDiag = vaDiag2;
		    stepMinCol = 1;
		    stepMinRow = sAlt.length();
		};
	    }
	    /* puis celles de s2.charAt(row-1) */
	    for (Iterator<UTF32String> i = Translit.longEquiv(s2.charAt(row-1)).iterator(); i.hasNext();){
		sAlt = i.next();
		vaDiag2 = dp2.get(col-sAlt.length(), row-1);
		if (vaDiag2==dp2.get(col,row) && vaDiag2 < vaDiag){
		    //System.out.println("diag sur alternative de "+s2.charAt(row-1)+"= "+sAlt);
		    vaDiag = vaDiag2;
		    stepMinCol = sAlt.length();
		    stepMinRow = 1;
		};
	    }
	    mi = min(vaDiag, min(vaVert, vaHoriz));
	    if (mi == vaDiag){ // pas forcément ! col-1 et row -1 , on peut maintenant faire des diagonales plus longues !
		col = col - stepMinCol;
		row = row - stepMinRow;
	    }
	    else{
		if (mi == vaVert){
		    row = row -1;
		}
		else{
		    col = col -1;
		}
	    }
	    val = dp2.get(col,row);
	    
	    res.addPoint(col+i1, row+i2);
	    
	    
	}
	//System.out.println("");
	//System.out.println("dp2[0][0]="+dp2.get(0,0));

	    
	/*try{
	  System.out.println(res.toString(s1,s2));
	  System.out.println("########");
	  }
	  catch (Exception e){
	  e.printStackTrace();
	  };*/

	
	return res;
    }


    
}
 
