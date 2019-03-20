

class UTF32Char{
    // chaque 2*n est équivalent (modulo transliteration) à 2*n+1
    // l'idéal serait que ça soit trié !
    
    
    byte b1, b2, b3, b4;

    UTF32Char(byte i1, byte i2, byte i3, byte i4){
	b1 = i1;
	b2 = i2;
	b3 = i3;
	b4 = i4;
    };

  
    public boolean equals(Object o){
	UTF32Char other;

	other = (UTF32Char)o;
        return ((b1 == other.b1) && (b2 == other.b2) &&  (b3 == other.b3) &&  (b4 == other.b4));

    };

    public int hashCode(){
	return this.toString().hashCode();
    };
    
    UTF32Char(int nbre){
	b1 = (byte)((nbre & 0xFF000000) >> 24);
	b2 = (byte)((nbre & 0x00FF0000) >> 16);
	b3 = (byte)((nbre & 0x0000FF00) >> 8);
	b4 = (byte)(nbre & 0x000000FF);
    };
    
    public boolean translitEquals(UTF32Char other){
        return ((b1 == other.b1) && (b2 == other.b2) &&  (b3 == other.b3) &&  (b4 == other.b4));

    };

    
    public boolean equals(char other){
	String s1="a";
	String s2;
	byte b[] = new byte[4];
	b[0] = b1;
	b[1] = b2;
	b[2] = b3;
	b[3] = b4;
	try{
	    s1 = new String(b, "UTF-32");
	}
	catch (Exception e){};
	s2=Character.toString(other);
	return s1.equals(s2);
    };
    public boolean isWhiteSpace(){
	String chars="a";
	byte b[] = new byte[4];
	b[0] = b1;
	b[1] = b2;
	b[2] = b3;
	b[3] = b4;
	try{
	    chars = new String(b, "UTF-32");
	}
	catch (Exception e){};
	return Character.isWhitespace(chars.charAt(0));
    };
    
    public String toString(){
	byte b[] = new byte[4];
	String res = null;
	
	b[0] = b1;
	b[1] = b2;
	b[2] = b3;
	b[3] = b4;
	try{
	    res = new String(b, "UTF-32");
	}
	catch (Exception e){};
	
	return res;
    };


    public static void main(String args[]){
	System.out.println(""+new UTF32Char(0xab));
	System.out.println(new UTF32Char(65));
    }
};
