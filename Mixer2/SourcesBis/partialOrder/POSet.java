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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.HashMap;

/**
 * Partial orders. We suppose we get all elements of the set at the construction.
 * 
 *
 */
public class POSet<T> {
	//T[] labels;
	ArrayList<T> labels;
	//ArrayList<Integer>[] trans;
	ArrayList<ArrayList<Integer>> trans;
	
	// we should verify that we do not have twice the same element in the list.
	// it is the Set that determines how elements are the same or not (== vs equals()).
	@SuppressWarnings({ "unchecked", "unused" })
	public POSet(Set<T> elts){
		if (elts.size()> 0){
			this.labels = new ArrayList<T>();
			//this.labels = (T[]) Array.newInstance(elts.iterator().next().getClass(), elts.size());
			//this.trans = (ArrayList<Integer>[]) Array.newInstance(ArrayList.class, elts.size());
			this.trans = new ArrayList<ArrayList<Integer>>();
			int j = 0;
			for (Iterator<T> i = elts.iterator(); i.hasNext();){
				//this.labels[j] = i.next();
				this.labels.add(j, i.next());
				//this.trans[j] = new ArrayList<Integer>();
				this.trans.add(new ArrayList<Integer>());
				j++;
			}
		}
	}
	
	public ArrayList<T> getLabels(){
		return labels;
	}
	protected int indexOf(T e) throws NoSuchElementException{
		boolean found = false;
		int i = 0;
		//while (i < this.labels.length && !found){
		while (i < this.labels.size() && !found){
			//found = (this.labels[i] == e);
			found = (this.labels.get(i) == e);
			if (!found){
				i++;
			}
		}
		if (found){
			return i;
		}
		throw new NoSuchElementException("element = "+e.toString());
	}
	
	// walk does a transtive closure.
	// the result will be into allreadySeen.
	private void walk(ArrayDeque<Integer> toSee, boolean[] allreadySeen){
		if (toSee.size() == 0){
			return;
		}
		int i = toSee.pop().intValue();
		int s;
		Iterator<Integer> succ_i = this.trans.get(i).iterator();
		while (succ_i.hasNext()){
			s = succ_i.next().intValue();
			if (!allreadySeen[s]){
				toSee.add(new Integer(s));
				allreadySeen[s] = true;
			}
		}
		walk(toSee, allreadySeen);
	}
	
	
	/**
	 * We add the fact that e1 is less than e2.
	 * We do not attempt at minimizing the dag to its transitive cover ; there is a dedicated
	 * method for simplifying.
	 * e1 and e2 have to be members of "labels".
	 * @param e1
	 * @param e2
	 */
	public void addEdge(T e1, T e2) throws NoSuchElementException{
		//first, we need the indexes of e1 and e2.
		int i_e1 = indexOf(e1);
		int i_e2 = indexOf(e2);
		this.trans.get(i_e1).add(new Integer(i_e2));
	}
	
	/**
	 * We specify the order by adding chains. 
	 * we do not attempt at minimizing the dag to its transitive cover ; there is a dedicated
	 * method for simplifying.
	 * @param ch
	 * It would be much easier when we could add a chain without bothering about the elements
	 * of the chain allready beeing into "labels" or not !
	 */
	public void addChain(List<T> ch) throws NoSuchElementException{
		Iterator<T> i = ch.iterator();
		T previous, current;
		if (!i.hasNext()){
			return;
		}
		previous = i.next();
		try{
			int id = indexOf(previous);
		}
		catch (NoSuchElementException e){
			labels.add(previous);
			this.trans.add(new ArrayList<Integer>());
		}
		if (!i.hasNext()){
			return;
		}
		while (i.hasNext()){
			current = i.next();
			try{
				int id = indexOf(current);
			}
			catch (NoSuchElementException e){
				labels.add(current);
				this.trans.add(new ArrayList<Integer>());
			}
			addEdge(previous, current);
			previous = current;
		}
	}
	
	/**
	 * we remove redundant edges so that the representation is the transitive reduction 
	 * of the initial dag.
	 */
	public void simplify(){
		// we compute the transitive closure
		// for each node, the relation r2 =  initial_relation composed with transitive closure
		// for each node, the result is initial_relation minus r2 
		// (that is we remove the edges that can be reached in transtive closure from a successor)
		ArrayDeque<Integer> ad;
		Iterator<Integer> succIterator;
		
		//boolean[][] transClosure = new boolean[this.labels.length][this.labels.length];
		boolean[][] transClosure = new boolean[this.labels.size()][this.labels.size()];
		for (int i = 0; i < this.labels.size(); i++){
			ad = new ArrayDeque<Integer>();
			ad.add(new Integer(i));
			for (int j = 0; j < this.labels.size(); j++){
				transClosure[i][j]= false;
			}
			this.walk(ad, transClosure[i]);
			// we remove the reflexive
			transClosure[i][i] = false;
		}
		// now, we compute r2 (composition of initial relation + transtive closure)
		boolean[][] r2 = new boolean[this.labels.size()][this.labels.size()];
		int s;
		for (int i = 0; i < this.labels.size(); i++){
			for (int j = 0; j < this.labels.size(); j++){
				r2[i][j] = false;
			}
			succIterator = this.trans.get(i).iterator();
			while (succIterator.hasNext()){
				s = succIterator.next().intValue();
				for (int j = 0; j < this.labels.size(); j++){
					r2[i][j] = r2[i][j] | transClosure[s][j];
				}
			}
		}
		// all we have to do now is remove edges in the initial representation when they are
		// also in r2.
		ArrayList<Integer> newTrans;
		for (int i = 0; i < this.labels.size(); i++){
			succIterator = this.trans.get(i).iterator();
			newTrans = new ArrayList<Integer>();
			while (succIterator.hasNext()){
				s = succIterator.next().intValue();
				if (!r2[i][s]){
					newTrans.add(new Integer(s));
				}
			}
			
			this.trans.set(i, newTrans);
		}
	}
	
	@Override
	public String toString(){
		Iterator<Integer> succIter;
		String res = "";
		for (int i = 0; i < this.labels.size(); i++){
			succIter = this.trans.get(i).iterator();
			while (succIter.hasNext()){
				res = res + this.labels.get(i).toString()+ " < "+this.labels.get(succIter.next().intValue()).toString()+
						";\n";
			}
		}
		return res;
	}
	

	// What we really need is a method that takes a state 's' in a dag
	// and that builds from this state as though 's' was an initial state.
	


	// we create a hook so that this method may be overloaded.
	public DagState<T> createNode(){
		return new DagState<T>();
	}
	

	// we'd better have an initial state as a parameter.


	// The result is a Dag of all total orders compatible with this. More preciselly,
	// each path in the dag is a total ordering of the elements in this.
	public DagState<T> allTotalOrders(){
		DagState<T> res = new DagState<T>();
		
		
		HashMap<SubPOSet<T>, DagState<T>> currentLevel;
		HashMap<SubPOSet<T>, DagState<T>> previousLevel = new HashMap<SubPOSet<T>, DagState<T>>();
		
		
		Iterator<SubPOSet<T>> prevLevelIter;
		SubPOSet<T> cursPOSet;
		DagState<T> curState;
		ArrayList<T> firsts;
		Iterator<T> firstsIter;
		DagState<T> newState;
		T curmaxElement;
		SubPOSet<T> newSubPoset;
		
		
		previousLevel.put(new SubPOSet<T>(this), res);
		for (int level = 0; level < this.labels.size(); level++){
			//System.out.println("\nall to, level = "+level);
			prevLevelIter = previousLevel.keySet().iterator();
			currentLevel = new HashMap<SubPOSet<T>, DagState<T>>();
			while (prevLevelIter.hasNext()){
				cursPOSet = prevLevelIter.next();
				curState = previousLevel.get(cursPOSet);
				// we take the firsts in the subOrder
				firsts = cursPOSet.firsts();
				//System.out.println("curPOSet = "+cursPOSet);
				//System.out.println("firsts = "+firsts);
				firstsIter = firsts.iterator();
				while (firstsIter.hasNext()){
					curmaxElement = firstsIter.next();
					newSubPoset = new SubPOSet<T>(cursPOSet);
					newSubPoset.remove(curmaxElement);
					
					// is newSubPoset allready in currentLevel ?
					//System.out.println("avant get("+newSubPoset+")");
					newState = currentLevel.get(newSubPoset);
					//System.out.println("newState récupéré = "+newState);
					//System.out.println("currentLevel = "+currentLevel);
					/*Iterator<SubPOSet<T>> testIterator = currentLevel.keySet().iterator();
					SubPOSet<T> testV;
					boolean testR;
					while (testIterator.hasNext()){
						testV = testIterator.next();
						System.out.println("clef = "+testV);
						testR = testV.equals(newSubPoset);
						System.out.println(testV.toString()+".equals("+newSubPoset.toString()+") ="+testR);
					}*/
					if (newState == null){
						//newState = new DagState<T>();
						newState = this.createNode();
						currentLevel.put(newSubPoset, newState);
						//System.out.println("on a ajouté :"+newSubPoset.toString());
					}
					// if so, we just build an edge labelled curMaxElement towards the
					// corresponding state
					// if not, we have to build the state first.
					
					curState.addTransition(curmaxElement, newState);
					//System.out.println(curState.toString()+"-"+curmaxElement.toString()+"->"+newState.toString());
				}
			}
			previousLevel = currentLevel;
		}
		
		
		return res;
	}
	
	public static void main(String argv[]){
		//ArrayList<String> set = new ArrayList<String>();
		CopyOnWriteArraySet<String> set = new CopyOnWriteArraySet<String>();
		String odel = "<del>";
		String c = "c";
		String d = "d";
		String fdel = "</del>";
		String ode = "<DE>";
		String fa = "</A>";
		String ob = "<B>";
		set.add(odel);
		set.add(c);
		set.add(d);
		set.add(fdel);
		set.add(ode);
		set.add(fa);
		set.add(ob);
		POSet<String> ex = new POSet<String>(set);
		ArrayList<String> ch1 = new ArrayList<String>();
		ch1.add(odel); ch1.add(c); ch1.add(d); ch1.add(fdel);
		ex.addChain(ch1);
		ArrayList<String> ch2 = new ArrayList<String>();
		ch2.add(c); ch2.add(ode); ch2.add(d);
		ex.addChain(ch2);
		ArrayList<String> ch3 = new ArrayList<String>();
		ch3.add(fa); ch3.add(ob);
		ex.addChain(ch3);
		// now we add the relation that makes a->g redundant :
		
		//System.out.println("avant simplification");
		//System.out.println(ex);
		ex.simplify();
		//System.out.println("après simplification");
		//System.out.println(ex);
		//System.out.println("tous les ordres totaux");
		DagState<String> ds;
		ds = ex.allTotalOrders();
		//System.out.println("Résultat");
		System.out.println("digraph resultat{");
		System.out.println(ds.toGraphViz(new CopyOnWriteArraySet()));
	        System.out.println("}");
	}
}
