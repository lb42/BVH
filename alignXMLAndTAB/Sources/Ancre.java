
class Ancre{
    UTF32String token;
    int i1, j1, i2, j2;

    Ancre(UTF32String t, int a1, int b1, int a2, int b2){
	token = t;
	i1 = a1;
	j1 = b1;
	i2 = a2;
	j2 = b2;
    };
    public String toString(){
	String res=token.toString();
	res = res+" ("+i1+", "+j1+", "+i2+", "+j2+")";
	return res;
    }
    UTF32String getToken(){
	return token;
    };
    void set_i1(int a1){
	i1 = a1;
    };
    void set_j1(int b1){
	j1 = b1;
    };
    int get_i1(){
	return i1;
    };
    int get_j1(){
	return j1;
    };
    int get_i2(){
	return i2;
    };
    int get_j2(){
	return j2;
    };			    

}
