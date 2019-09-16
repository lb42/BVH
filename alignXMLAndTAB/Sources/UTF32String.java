import java.io.UnsupportedEncodingException;

class UTF32String{

    byte[] laChaine;

    UTF32String(String s){
	laChaine = null;
	try{
	    laChaine = s.getBytes("UTF-32");
	}
	catch (UnsupportedEncodingException e){
	};
    };

    UTF32Char charAt(int i){
	return new UTF32Char(laChaine[i*4], laChaine[i*4+1],
			     laChaine[i*4+2], laChaine[i*4+3]);
    };

    public int length(){
	return laChaine.length / 4;
    }

    public String toString(){
	String res="";

	try{
	    res = new String(laChaine, "UTF-32");
	}
	catch (Exception e){};
	
	return res.replace(' ', 'x').replace('\n','y');
    };
}
