

class UTF32Char{
    // chaque 2*n est équivalent (modulo transliteration) à 2*n+1
    // l'idéal serait que ça soit trié !
    static UTF32Char[] equivs = new  UTF32Char[]{ new UTF32Char(0xab), new UTF32Char('"'),
						  new UTF32Char(0xbb), new UTF32Char('"'),
						  new UTF32Char(0x02B9), new UTF32Char('\''),
						  new UTF32Char(0x02BA), new UTF32Char('\''),
						  new UTF32Char(0x02BB), new UTF32Char('\''),
						  new UTF32Char(0x02BC), new UTF32Char('\''),
						  new UTF32Char(0x02BD), new UTF32Char('\'')
						  
    };
    
    byte b1, b2, b3, b4;

    UTF32Char(byte i1, byte i2, byte i3, byte i4){
	b1 = i1;
	b2 = i2;
	b3 = i3;
	b4 = i4;
    };

  
    public boolean equals(UTF32Char other){
        return ((b1 == other.b1) && (b2 == other.b2) &&  (b3 == other.b3) &&  (b4 == other.b4));

    };

    UTF32Char(int nbre){
	int reste;
	
	b1 = (byte)(nbre / 0xFFFFFF);
	reste=nbre - b1*0xFFFFFF;
	b2 = (byte)(reste / 0xFFFF);
	reste = reste - b2*0xFFFF;
	b3 = (byte)(reste / 0xFF);
	b4 = (byte)(reste - b3*0xFF);
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
