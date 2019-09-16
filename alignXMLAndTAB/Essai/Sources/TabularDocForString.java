import java.util.Scanner;
import java.io.File;

class TabularDocForString{
    Scanner scn = null;

    TabularDocForString(String fileName){



	 try{
	     scn = new Scanner(new File(fileName), "UTF-8");
	 }
	 catch  (Exception e){
	   System.out.println("allo Houston : constr tab");
	   e.printStackTrace();
	 };
    };

    public String getText(){
	StringBuilder builder = new StringBuilder();
	String res="";
	String ligne;
	char eot='\003';
	try{
	    while (scn.hasNext()){
		ligne =  scn.nextLine();

		if (ligne.split("\\t").length > 1){
		    //res = res + ligne.split("\\t")[0];
		    builder.append(ligne.split("\\t")[0]);
		    
		    //res = res + ligne.split("\\t")[0]+eot;
		    //res = res + ligne.split("\\t")[0]+"#";
		}
	    }
	}
	catch (Exception e){
	    System.out.println("exception :"+e.getMessage()); 
	};
	return builder.toString();
    }

      public String getText(int numColonne){
	StringBuilder builder = new StringBuilder();
	String res="";
	String ligne;
	char eot='\003';
	try{
	    while (scn.hasNext()){
		ligne =  scn.nextLine();

		if (ligne.split("\\t").length > numColonne){
		    //res = res + ligne.split("\\t")[numColonne];
		    builder.append(ligne.split("\\t")[numColonne]);
		}
	    }
	}
	catch (Exception e){
	    System.out.println("exception :"+e.getMessage()); 
	};
	return builder.toString();
    }
}
