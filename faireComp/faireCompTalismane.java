import java.util.Scanner;
import java.io.File;

/* On veut ajouter l erepère des premiers et dermiers mots d'une phrase.... */
class faireCompTalismane{

    public static void main(String args[]){
	Scanner scn;
	String ligne;
	String[] champ;
	int i1, i2, oi1, oi2;
	int debutPhrase = 0;
	int finPhrase;

	i2 = -1;
	try{
	    scn = new Scanner(new File(args[0]), "UTF-8");
	    oi1 = -1;
	    oi2 = -1;
	    while (true){
		ligne =  scn.nextLine();
		champ = ligne.split("\\t");
		if (champ.length > 1){

		    i1 = Integer.parseInt(champ[0]);
		    i2 = Integer.parseInt(champ[1]);
		    //if (i2 - i1 == champ[2].length() && (oi1 != i1)){
		    //if (oi1 != i1){
			oi1 = i1;
			
			if (champ[2].equals("1")){
			  
			    debutPhrase= Integer.parseInt(champ[0]);
			}

			System.out.println(champ[0]+"\t"+champ[1]+
					   "\t<w xmlns=\"http://www.tei-c.org/ns/1.0\" pos=\""
					   +champ[5].replace('"', '_')+
					   "\" lemma=\""+
					   champ[4].replace('"', 'q')+"\">");

			/*System.out.print(champ[0]+"\t"+champ[1]+"\t<w xmlns=\"http://www.tei-c.org/ns/1.0\" word=\""+champ[3].replace('"', 'q')+"\"");
			  System.out.print(" cid=\""+champ[2]+"\"");	
			  for (int c = 4; c < champ.length; c++){
			  System.out.print(" c"+c+"=\""+champ[c].replace('"', 'q')+"\"");	
			  }
			  System.out.println(">");*/
			//};
			/*}
		    else{
			System.err.println("Pb : "+ligne);
			//System.out.println(champ[0]+"\t"+champ[0]+"\t<probleme>");
			//System.out.println(champ[0]+"\t"+champ[1]+"\t<w xmlns=\"http://www.tei-c.org/ns/1.0\" pos=\""+champ[3].replace('"', 'q')+"\" pb=\"t"+"\">");
			}*/
		}
		else{
		    if (i2 != -1){
			finPhrase = i2;
			System.out.println(debutPhrase+"\t"+finPhrase+"\t<s xmlns=\"http://www.tei-c.org/ns/1.0\">");
		    }
		}
	    }
	}
	catch (Exception e){};
    };
}
