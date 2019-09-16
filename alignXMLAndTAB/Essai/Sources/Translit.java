/* on s'inspire de :
   http://git.savannah.gnu.org/cgit/libiconv.git/tree/lib/translit.def
*/

import java.text.Normalizer;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.io.File;
import java.net.URL;

class Translit{

    /* les équivalences caractère <-> caractère */
    static Hashtable<UTF32Char, ArrayList<UTF32Char>> translitDefShort = null;
    /* les éuivalences caractère <-> chaine */
    static Hashtable<UTF32Char, ArrayList<UTF32String>> translitDefLong = new Hashtable<UTF32Char, ArrayList<UTF32String>>();
    
    static void addAssociation(UTF32Char c, UTF32String s){

	//System.err.println("ajout de "+c+" <-> "+s);
	if (s.length() > 1){
	    ArrayList<UTF32String> curVal =  translitDefLong.get(c);
	
	    if (curVal == null){
		curVal = new ArrayList<UTF32String>();
	    }
	    curVal.add(s);
	    translitDefLong.put(c, curVal);
	}
	else{
	    if  (s.length() > 0){
		addShortAssociation(c, s.charAt(0));
	    }
	}
    };
    
    static void addShortAssociation(UTF32Char c, UTF32Char e){
	ArrayList<UTF32Char> curVal =  translitDefShort.get(c);


	if (curVal == null){
	    curVal = new ArrayList<UTF32Char>();
	    translitDefShort.put(c, curVal);
	}
	curVal.add(e);
	
    };
    
    static{
	translitDefShort=new  Hashtable<UTF32Char, ArrayList<UTF32Char>>();

	UTF32Char g=new UTF32Char('«');
	
	addShortAssociation(g, new UTF32Char('"'));
	addShortAssociation(new UTF32Char(0xbb), new UTF32Char('"'));
	addShortAssociation(new UTF32Char(0x2010), new UTF32Char('-'));
	addShortAssociation(new UTF32Char(0x2011), new UTF32Char('-'));
	addShortAssociation(new UTF32Char(0x2012), new UTF32Char('-'));
	addShortAssociation(new UTF32Char(0x2013), new UTF32Char('-'));
	addShortAssociation(new UTF32Char(0x2014), new UTF32Char('-'));	
	addShortAssociation(new UTF32Char(0x2015), new UTF32Char('-'));
	addShortAssociation(new UTF32Char(0x2019), new UTF32Char(0x27));
	addShortAssociation(new UTF32Char(0x2212), new UTF32Char('-'));
	addShortAssociation(new UTF32Char(0x2E3A), new UTF32Char('-'));
	addShortAssociation(new UTF32Char(0x2E3B), new UTF32Char('-'));

	//System.err.println("assocs. ajoutées");
	//System.err.println(translitDefShort);
	//System.err.println("essai : get(«)="+translitDefShort.get(new UTF32Char('«')));
	//System.err.println("equal ?"+new UTF32Char('«').equals(new UTF32Char('«')));

	String[] elts;
	String l;
	/* On ajoute à ça le contenu de translit.def */
	try{
	    URL url= Translit.class.getClassLoader().getResource("translit.def");


	    Scanner sc = new Scanner(url.openStream());
	    while (sc.hasNext()){
		l = sc.nextLine();
		elts = l.split("\\t");
		if (elts.length > 1){
		    //System.out.println(new UTF32Char(Integer.parseInt(elts[0], 16))+"    "+elts[1]);
		    addAssociation(new UTF32Char(Integer.parseInt(elts[0], 16)), new UTF32String(elts[1]));
		};
	    };
	}
	catch (Exception e){
	    e.printStackTrace();
	    System.err.println("translit.def not found !");
	    System.exit(42);
	};
	
    }
    
    /* On veut une hasttable UTF32Char -> ArrayList<UTF32String> */
    
    /* si la table devient plus longue, on fera du dichotomique.
       pour l'instant, en moyenne 6 accès, par dichotomie,
       en moyenne, environ 5 */
    static UTF32Char[] equivs = new  UTF32Char[]{
	new UTF32Char(0xab), new UTF32Char('"'),
	new UTF32Char(0xbb), new UTF32Char('"'),
	new UTF32Char(0x2010), new UTF32Char('-'),
	new UTF32Char(0x2011), new UTF32Char('-'),
	new UTF32Char(0x2012), new UTF32Char('-'),
	new UTF32Char(0x2013), new UTF32Char('-'),
	new UTF32Char(0x2014), new UTF32Char('-'),	
	new UTF32Char(0x2015), new UTF32Char('-'),
	new UTF32Char(0x2019), new UTF32Char(0x27),
	new UTF32Char(0x2212), new UTF32Char('-'),
	new UTF32Char(0x2E3A), new UTF32Char('-'),
	new UTF32Char(0x2E3B), new UTF32Char('-')

    };

    

    public static ArrayList<UTF32Char> shortEquiv(UTF32Char c1){
	return translitDefShort.get(c1);
    };

    public static ArrayList<UTF32String> longEquiv(UTF32Char c1){
	if (translitDefLong.get(c1) != null){
	    return translitDefLong.get(c1);
	}
	else{
	    return new ArrayList<UTF32String>();
	}
    };
    
    public static boolean equals(UTF32Char c1, UTF32Char c2){
	if (c1.equals(c2)){
	    return true;
	}
	else{
	    if (Normalizer.normalize(c1.toString(), Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").toUpperCase().equals(Normalizer.normalize(c2.toString(), Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").toUpperCase())){
		return true;
	    }
	    else{
		//System.err.print("comparaison |"+c1+"| et " +c2);
		boolean trouve = false;
		ArrayList<UTF32Char> eq=translitDefShort.get(c1);
		
		if (eq != null){
		    
		    Iterator<UTF32Char> i = eq.iterator();
		    UTF32Char cCour;
		    //System.err.println(" eq pas null ");
		    while (!trouve && i.hasNext()){
			cCour = i.next();
			//System.err.println("On compare : |"+cCour+"| et "+c2);
			trouve = cCour.equals(c2);
		    }
		}
		else{
		    //System.err.print(" eq null ");
		};
		//System.err.println(" "+trouve);
		return trouve;
	    }
	}
    };

}
