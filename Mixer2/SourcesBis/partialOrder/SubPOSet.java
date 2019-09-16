package partialOrder;

//XMLMixer: a tool for embedding annotations into XML files.

//Copyright (C) 2017  Bertrand Gaiffe
//bertrand.gaiffe@atilf.fr

//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.

//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.

//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import partialOrder.POSet;
import partialOrder.SubPOSet;

public class SubPOSet<T> {
	POSet<T> theWholeSet;
	boolean[] members;
	
	public String toString(){
		StringBuffer res = new StringBuffer();
		res.append("{");
		for (int i = 0; i < members.length; i++){
			if (members[i]){
				res.append(theWholeSet.labels.get(i).toString()+", ");
			}
		}
		res.append("}");
		return(res.toString());
	}
	public SubPOSet(POSet<T> tws){
		this.theWholeSet = tws;
		this.members = new boolean[tws.labels.size()];
		for (int i = 0; i < this.members.length; i++){
			this.members[i] = true;
		}
	}
	
	public SubPOSet(POSet<T> tws, boolean[] m){
		this.theWholeSet = tws;
		this.members = new boolean[tws.labels.size()];
		for (int i = 0; i < this.members.length; i++){
			this.members[i] = m[i];
		}
	}
	
	public SubPOSet(SubPOSet<T> source){
		this.theWholeSet = source.theWholeSet;
		this.members = new boolean[source.members.length];
		for (int i = 0; i < this.members.length; i++){
			this.members[i] = source.members[i];
		}
	}
	
	public void remove(int indexOfElement){
		this.members[indexOfElement] = false;
	}
	public void remove(T element) throws NoSuchElementException{
		this.remove(this.theWholeSet.indexOf(element));
	}
	
	
	@Override
	public boolean equals(Object other){
		boolean res;
		//System.out.println("Le monsieur te demande si "+this+" égale "+other);
		try{
			SubPOSet<T> otherAsSPs = (SubPOSet<T>)other;
			res = ((this.theWholeSet == otherAsSPs.theWholeSet) && (Arrays.equals(this.members, otherAsSPs.members)));
		}
		catch (ClassCastException e){
			res = false;
			
			}
		//System.out.println("et tu lui répond :"+res);
		return res;
	}
	
	
	/**
	 * the maximal elements in the subOrder
	 * @return
	 */
	public ArrayList<T> firsts(){
		// the firsts are those that are not members of the trans of a member of the set.
		// this costs n square 
		boolean[] resb = new boolean[this.members.length];
		Iterator<Integer> itrans;
		ArrayList<T> res = new ArrayList<T>();
		
		for (int j = 0; j < resb.length; j++){
			if (this.members[j]){
				resb[j] = true;
			}
			else{
				resb[j] = false;
			}
		}
		for (int j = 0; j < resb.length; j++){
			if (this.members[j]){
				itrans = this.theWholeSet.trans.get(j).iterator();
				while (itrans.hasNext()){
					resb[itrans.next().intValue()] = false;
				}
			}
		}
		for (int j = 0; j < resb.length; j++){
			if (resb[j]){
				res.add(this.theWholeSet.labels.get(j));
			}
		}
		return res;
	}
	//TODO : A better hashcode ! (we just want to force equals to be called).
	// as we do it, there is no real advantage in using HashMaps !
	public int hashCode(){
		return 42;
	}
}
