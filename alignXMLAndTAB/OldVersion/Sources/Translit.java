/* on s'inspire de :
   http://git.savannah.gnu.org/cgit/libiconv.git/tree/lib/translit.def
*/

import java.text.Normalizer;

class Translit{


    
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
	/*new UTF32Char('à'), new UTF32Char('A'),
	  new UTF32Char('A'), new UTF32Char('à')*/
    };


    
    public static boolean equals(UTF32Char c1, UTF32Char c2){
	if (c1.equals(c2)){
	    return true;
	}
	else{
	    boolean trouve = false;
	    int i = 0;
	    while (i*2 < equivs.length){
		if (
		    (c1.equals(equivs[i*2]) && c2.equals(equivs[i*2+1])) ||
		    (c2.equals(equivs[i*2]) && c1.equals(equivs[i*2+1]))
		    ){
		    return true;
		}
		else{
		    i = i+1;
		}
	    }
	    String s1=Normalizer.normalize(c1.toString(), Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").toUpperCase();
	    String s2=Normalizer.normalize(c2.toString(), Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").toUpperCase();
	    
	    return s1.equals(s2);
	}
    };

}
