package inputDag;

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
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.HashMap;
import java.util.Iterator;



//import fr.atilf.PartialOrder.DagState;
import partialOrder.POSet;
import partialOrder.SubPOSet;
import xmlNodes.GenericNode;

/**
 * DagState
 * DagState contains all the logics of Dag. 
 * @author bertrand
 *
 */


public class InputDagState{
	int textPosition;
	
	int id;
	//ArrayList<ArrayList<GenericNode>> listesEtat; // les listes qui ont servi à
													// prolonger l'état
	POSet<GenericNode> listesEtat; 
	
	InputDagState etatFinal; // l'état auquel on arrive après avoir traité
						// listeEtats. Tous les états entre this et etatFinal
						// ont la même
						// textPosition.

	ArrayList<InputDagTransition> transitions;
	int level;
	
	
	//We need a pointer towards the "initial state"
	
	@SuppressWarnings("unused")
	InputDagState(int tp, int l) {

		this.textPosition = tp;
		this.transitions = new ArrayList<InputDagTransition>();
		this.id = InputDag.totalNbStates;
		InputDag.totalNbStates = InputDag.totalNbStates + 1;
		this.level = l;
		// listesEtat = new ArrayList<ArrayList<GenericNode>>();
	}

	public int getId() {
		return this.id;
	}

	public int getLevel() {
		return this.level;
	}

	public int getTextPosition() {
		return this.textPosition;
	}

	/*public ArrayList<ArrayList<GenericNode>> getListesEtat() {
		return this.listesEtat;
	}*/
	
	public POSet<GenericNode> getListesEtat(){
		return this.listesEtat;
	}

	public void addTransition(InputDagTransition t) {
		this.transitions.add(t);
	}

	public ArrayList<InputDagTransition> getTransitions() {
		return this.transitions;
	}

	public InputDagState getEtatFinal() {
		return this.etatFinal;
	}

	@Override
	public String toString() {
		StringBuffer res = new StringBuffer();
		Iterator<InputDagTransition> i = this.transitions.iterator();
		InputDagTransition t;

		
		while (i.hasNext()) {
			t = i.next();
			// res.append(""+this.getId()+"->"+t.getSucc().getId()+" [label=\""+t.getNode()+"\"]\n");
			res.append("n"
					+ this.getId()
					+ " [shape=box, fillcolor=lightcyan, style=filled, label=\""
					+ this.getId() + " (" + this.getTextPosition()+")\"];\n");
			res.append("i"
					+ this.getId()
					+ "_"
					+ t.getSucc().getId()
					+ " [shape=box, fillcolor=lemonchiffon, style=filled, label=\""
					+ t.getNode().toString().replaceAll("\n", "return") + "\"];\n");
			res.append("n"
					+ t.getSucc().getId()
					+ " [shape=box, fillcolor=lightcyan, style=filled, label=\""
					+ t.getSucc().getId() + " (" + t.getSucc().getTextPosition()
					+ ")\"];\n");
			res.append("n" + this.getId() + "->i" + this.getId() + "_"
					+ t.getSucc().getId() + "[dir=none];\n");
			res.append("i" + this.getId() + "_" + t.getSucc().getId() + "->"
					+ "n" + t.getSucc().getId() + ";\n");
		}

		return res.toString();
	}
	
	/*public String toString(){
		StringBuffer res = new StringBuffer();
		res.append("id = "+this.id+" construit à partir de :"+this.listesEtat);
		return res.toString();
	}*/

	public ArrayList<InputDagState> getSuccesseurs() {
		@SuppressWarnings("unused")
		ArrayList<InputDagState> res = new ArrayList<InputDagState>();
		InputDagTransition dt;

		Iterator<InputDagTransition> it = this.transitions.iterator();
		while (it.hasNext()) {
			dt = it.next();
			res.add(dt.getSucc());
		}

		return res;
	}

	private static boolean stateIsInList(InputDagState e, ArrayList<InputDagState> l) {
		Iterator<InputDagState> i = l.iterator();
		while (i.hasNext()) {
			if (e.getId() == i.next().getId()) {
				return true;
			}

		}

		return false;
	}

	private static void unionLD(ArrayList<InputDagState> l1, ArrayList<InputDagState> l2) {
		Iterator<InputDagState> i = l2.iterator();
		InputDagState c;
		while (i.hasNext()) {
			c = i.next();
			if (!stateIsInList(c, l1)) {
				l1.add(c);
			}

		}

	}

	public ArrayList<InputDagState> successeursEtoile() {
		ArrayList<InputDagState> succ = getSuccesseurs();
		Iterator<InputDagState> is = succ.iterator();
		@SuppressWarnings("unused")
		ArrayList<InputDagState> res = new ArrayList<InputDagState>();
		InputDagState s;

		while (is.hasNext()) {
			s = is.next();
			res.add(s);
			unionLD(res, s.successeursEtoile());
		}
		return res;
	}

	// renvoie true si d est successeur* de this
	// false sinon.
	public boolean hasSucc(InputDagState d) {
		if (d == this) {
			return true;
		}
		// not teh same pointer, we compare in depth
		ArrayList<InputDagState> succs = this.getSuccesseurs();
		Iterator<InputDagState> i = succs.iterator();
		InputDagState n;
		boolean found = false;
		while (!found & i.hasNext()) {
			n = i.next();
			found = (n == d);
			if (found) {
				return true;
			}
			// not found yet.
			found = n.hasSucc(d);
		}
		return found;
	}

	private class CpleVecteurEtat {
		int[] leVecteur;
		InputDagState lEtat;

		CpleVecteurEtat(int[] v, InputDagState d) {
			this.leVecteur = v;
			this.lEtat = d;
		}

		// comparaison dans l'ordre lexicographique sur le vecteur.
		// on se compare à un simple vecteur (et pas à un CpleVecteurEtat)
		public int compareTo(int[] autre) {
			// int courSelf, courAutre;

			for (int i = 0; i < this.leVecteur.length; i++) {
				if (this.leVecteur[i] < autre[i]) {
					return -1;
				}
				if (this.leVecteur[i] > autre[i]) {
					return 1;
				}
			}
			return 0;
		}

		public InputDagState getDag() {
			return this.lEtat;
		}

		public int[] getVect() {
			return this.leVecteur;
		}
	}

	// this class represents a level in the dag ; see prolongerAutomate.
	private class NiveauDag {
		ArrayList<CpleVecteurEtat> leNiveau;

		@SuppressWarnings("unused")
		NiveauDag() {
			this.leNiveau = new ArrayList<CpleVecteurEtat>();
		}

		public void ajoutCouple(CpleVecteurEtat e) {
			this.leNiveau.add(e);
		}

		public ArrayList<CpleVecteurEtat> getNiveau() {
			return this.leNiveau;
		}

		public InputDagState find(int[] clef) {
			// we do a dichotomic search...
			int iMin = 0;
			int iMax = this.leNiveau.size() - 1;
			int iMid;

			while (iMax >= iMin) {
				iMid = iMin + ((iMax - iMin) / 2); // rather pedantic, do we
													// really risk overflow ?
				if (this.leNiveau.get(iMid).compareTo(clef) == 1) {
					iMin = iMid + 1;
				} else if (this.leNiveau.get(iMid).compareTo(clef) == -1) {
					iMax = iMid - 1;
				} else {
					return this.leNiveau.get(iMid).getDag();
				}
			}
			return null;
		}

		/*
		 * public DagState findSimple(int[] clef) { Iterator<CpleVecteurEtat> i
		 * = leNiveau.iterator(); CpleVecteurEtat cve; int[] vCour; while
		 * (i.hasNext()) { cve = i.next(); vCour = cve.getVect(); boolean idem =
		 * true; for (int j = 0; j < vCour.length; j++) { idem = idem & clef[j]
		 * == vCour[j]; } ; if (idem) { return cve.getDag(); } ; } ;
		 * 
		 * return null; };
		 */
	}

	
	public InputDagState extendAutomaton(POSet<GenericNode> tokens, int textPos){
		// we extend this.
		// the algorithm is exactly the one in POSet (allTotalOrders)
		// we suppose that tokens has been .simplify()'ed
		// before this method is called.
		HashMap<SubPOSet<GenericNode>, InputDagState> currentLevel;
		HashMap<SubPOSet<GenericNode>, InputDagState> previousLevel = 
				new HashMap<SubPOSet<GenericNode>, InputDagState>();
		
		
		Iterator<SubPOSet<GenericNode>> prevLevelIter;
		SubPOSet<GenericNode> cursPOSet;
		InputDagState curState;
		ArrayList<GenericNode> firsts;
		Iterator<GenericNode> firstsIter;
		InputDagState newState = null;
		GenericNode curmaxElement;
		SubPOSet<GenericNode> newSubPoset;
		
		// Pb mettre les listes d'états dans le dag !
		this.listesEtat = tokens;
		
		previousLevel.put(new SubPOSet<GenericNode>(tokens), this);
		for (int level = 0; level < tokens.getLabels().size(); level++){
			//System.out.println("\nall to, level = "+level);
			prevLevelIter = previousLevel.keySet().iterator();
			currentLevel = new HashMap<SubPOSet<GenericNode>, InputDagState>();
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
					newSubPoset = new SubPOSet<GenericNode>(cursPOSet);
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
						newState = new InputDagState(textPos, level);
						currentLevel.put(newSubPoset, newState);
						//System.out.println("on a ajouté :"+newSubPoset.toString());
					}
					// if so, we just build an edge labelled curMaxElement towards the
					// corresponding state
					// if not, we have to build the state first.
					
					curState.addTransition(new InputDagTransition(curmaxElement, newState));
					//System.out.println(curState.toString()+"-"+curmaxElement.toString()+"->"+newState.toString());
				}
			}
			previousLevel = currentLevel;
		}
		
		this.etatFinal = newState;
		return newState;
	}
	
	public InputDagState prolongerAutomate(ArrayList<ArrayList<GenericNode>> tokens,
			int textPos){
		
		//this.listesEtat = tokens;
		// we build a POSEt
		CopyOnWriteArraySet<GenericNode> elements = new CopyOnWriteArraySet<GenericNode>();
		Iterator<ArrayList<GenericNode>> i1 = tokens.iterator();
		Iterator<GenericNode> i2;
		while (i1.hasNext()){
			i2 = i1.next().iterator();
			while (i2.hasNext()){
				elements.add(i2.next());
			}
		}
		POSet<GenericNode> tokenPoset = new POSet<GenericNode>(elements);
		// we add the chains now.
		i1 = tokens.iterator();
		while (i1.hasNext()){
			tokenPoset.addChain(i1.next());
		}
		tokenPoset.simplify();
		this.listesEtat = tokenPoset;
		return this.extendAutomaton(tokenPoset, textPos);
	}
	
	// prolongation de l'automate. avec une liste de listes (chaines) disjointes
	/*public InputDagState prolongerAutomate_old(ArrayList<ArrayList<GenericNode>> tokens,
			int textPos) {

		// System.out.println("prolongerAutomate("+tokens+", "+textPos+")");

		// algorithme :
		// on a n listes de la forme :
		// a b c
		// d e
		// f g h
		//
		// le dag est créé par niveaux ;
		// niveau 1, on a choisi 1 elt (parmi a, d, f)
		// niveau 2, on en a choisi 2, etc...

		// les états sont représentés par des vecteurs colonnes :
		// | nb elts pris dans la première liste
		// | nb elts pris dasn la seconde
		// | nb elts pris dans la troisième.

		// l'état : |x
		// |y
		// |z

		// est connecté aux états : | x +1 | x | x
		//                             | y | y +1 | y
		//                             | z | z | z +1

		// par les symboles respectifs ligne1[x], ligne2[y] et ligne3[z]
		// (évidement, il faut x+1 <= taille première ligne,
		// y+1 <= taille deuxième ligne
		// et z+1 <= taille 3 eme ligne

		// le nombre de niveaux dans le dag est la somme des longueurs des
		// listes contenues dans tokens.

		if (tokens == null) {
			System.out.println("WARNING : DagState.prolongerAutomate(null)");
			System.out.println("Sur l'état " + this);
			// return new DagState(textPos,0);
			return null;
		}
		this.listesEtat = tokens;

		this.level = 0;
		int niveau = 1;
		Iterator<ArrayList<GenericNode>> i = tokens.iterator();
		int nbNiveaux = 0;
		// pour représenter un niveau on a besoin d'une liste de couples
		// (vecteur + etat)

		while (i.hasNext()) {
			nbNiveaux = nbNiveaux + i.next().size();
		}
		NiveauDag previousLevel = new NiveauDag();
		int[] vInit = new int[tokens.size()];
		for (int in = 0; in < tokens.size(); in++) {
			vInit[in] = 0;
		}
		CpleVecteurEtat init = new CpleVecteurEtat(vInit, this);
		previousLevel.ajoutCouple(init);
		NiveauDag newLevel = new NiveauDag();
		CpleVecteurEtat cpleCour;
		int[] vectCour;
		int[] newVect;
		boolean isNewState;

		// we should first create the last state (in case it is different from
		// this)
		// so that we may set this last state as etatFinal for all internal
		// states.

		while (niveau <= nbNiveaux) {
			// on part de previousLevel, on construit newLevel.

			// System.out.println("Niveau "+niveau);

			newLevel = new NiveauDag();
			Iterator<CpleVecteurEtat> itPrevLevel = previousLevel.getNiveau()
					.iterator();
			while (itPrevLevel.hasNext()) {
				cpleCour = itPrevLevel.next();
				vectCour = cpleCour.getVect();

				

				// we build all possible new vectors from vectCour;
				for (int li = 0; li < tokens.size(); li++) {
					newVect = new int[tokens.size()];
					for (int j = 0; j < tokens.size(); j++) {
						newVect[j] = vectCour[j];
					}
					newVect[li] = newVect[li] + 1;
					// is it legal ? It is only if the line size in tokens
					// contains newVect[li] elements at least.
					if (tokens.get(li).size() >= newVect[li]) {
						// System.out.println("   li = "+li+" est légal");
						// ok newVect is legal, do we already know about an
						// associated DagState ?
						isNewState = false;

						// as the vectors are produced in lexicographic order,
						// if newVect > last(newLevel) then it is new
						// if not then we have to look for it.
						InputDagState succ = newLevel.find(newVect);
						if (succ == null) {
							succ = new InputDagState(textPos, niveau);
							// System.out.println("     -> pas trouvé ; on crée "+succ.getId());
							isNewState = true;
						}
						
						// we add a transition from cpleCour's State to succ
						// the transition is labeled by the symbol that occurs
						// in tokens at line li
						// and in position newVect[li]

						InputDagTransition trans = new InputDagTransition(tokens.get(li)
								.get(newVect[li] - 1), succ);
						cpleCour.getDag().addTransition(trans);
						// we now have to add the new couple Vector + state to
						// newLevel.

						
						if (isNewState) {
							// System.out.println("created "+succ.getId()+" as a new DagState");
							newLevel.ajoutCouple(new CpleVecteurEtat(newVect,
									succ));
							// should not we also put the lists ont the state ?
						}
					}
				}
			}
			previousLevel = newLevel;
			niveau = niveau + 1;
		}
		// le dernier état créé est le seul élément contenu dans newLevel.
		if (newLevel.getNiveau().size() != 1) {
			System.err.println("Pb sur le dernier état");
			System.exit(42);
			return null;
		}
		this.etatFinal = newLevel.getNiveau().get(0).getDag();
		// we should set "etatFinal" for all states we just created.
		return this.etatFinal;
	} */

	
	
	public boolean equals(InputDagState other) {
		return this.id == other.getId();
	}

	// pour tester...
	/*
	 * public static void main(String argv[]) { ArrayList<GenericNode> l1 = new
	 * ArrayList<GenericNode>(); ArrayList<GenericNode> l2 = new
	 * ArrayList<GenericNode>(); ArrayList<GenericNode> l3 = new
	 * ArrayList<GenericNode>(); // ArrayList<GenericNode> l4 = new
	 * ArrayList<GenericNode>(); ArrayList<ArrayList<GenericNode>> tokens = new
	 * ArrayList<ArrayList<GenericNode>>();
	 * 
	 * DagState init = new DagState(0, 0); Dag d = new Dag(init);
	 * 
	 * l1.add(new TextNode("a"));
	 * 
	 * l2.add(new TextNode("b")); l2.add(new TextNode("c"));
	 * 
	 * l3.add(new TextNode("d")); l3.add(new TextNode("e")); l3.add(new
	 * TextNode("f"));
	 * 
	 * tokens.add(l1); tokens.add(l2); tokens.add(l3); // tokens.add(l4);
	 * init.prolongerAutomate(tokens, 0);
	 * 
	 * System.out.println(d); };
	 */
}


