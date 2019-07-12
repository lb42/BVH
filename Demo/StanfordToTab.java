import java.util.Scanner;
import java.io.File;
import java.util.regex.*;

class StanfordToTab{
    Scanner scn = null;

    public static void main(String[] args){
	Scanner scn = null;
	String line;
	Pattern p;
	Matcher m;
	
	p = Pattern.compile("(.*)_([^_]*)");
	try{
	    scn = new Scanner(new File(args[0]), "UTF-8");

	    while (scn.hasNext()){
		line = scn.next();
		m=p.matcher(line);
		if (m.matches()){
		    System.out.print(m.group(1));
		    System.out.print("\t");
		    System.out.println(m.group(2));
		    
		}
	
		//System.out.println(scn.next());
	    }
	}
	catch (Exception e){};
    }
}
