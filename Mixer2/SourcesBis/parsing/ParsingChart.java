package parsing;
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
import java.util.Iterator;
//import java.util.TreeSet;
import java.util.HashMap;
//import java.util.NoSuchElementException;


import grammar.NonTerminal;
import grammar.ProductionRule;
import inputDag.InputDagState;
import xmlNodes.CloseElementNode;

// Le problème est dans l'agenda !
// (je crois). Il faut qu'on finisse complètement textPos avant d'attaquer nextTextPos !
// (sinon, pB avec les corrections) !

public class ParsingChart {
	int firstToUse;

	
	
	
	// FirstArgsOfReduce is such that j -> NonTerminal -> ParsingItem List.
	ArrayList<HashMap<String, ArrayList<ParsingItem>>> firstArgsOfReduce;

	// we should sort by first index the items into itemsAtTextPos.
	// so that we test wether an item is already there a lot more
	// efficiently.
	//ArrayList<ParsingItem> itemsAtTextPos;
	//ArrayList<ParsingItem> itemsAtNextTextPos;
	SortedTableOfItems itemsAtTextPos;
	SortedTableOfItems itemsAtNextTextPos;
	
	
	
	// ArrayList<ParsingItem> table; // the items in the order they are produced
	// that is with max(i, j) in ascending order
	// the index from j to indexes in table.
	// ArrayList<ArrayList<Integer>> index;
	// TreeSet<ParsingItem> table2;

	int currentTextPosition;
	int indexAgendaTextPos;
	ArrayList<ParsingItem> agendaTextPos;
	int indexAgendaNextTextPos;
	ArrayList<ParsingItem> agendaNextTextPos;
	
	ArrayList<ProductionRule> results;

	// we need to find efficiently wether an item is allready in the chart.

	// we should keep the last items that failed at scanning a closing bracket
	// the idea is that whenever we go through a state that advances
	// the textPosition (that is into the PCDATA) this list is set to empty
	//
	int lastTextPositionSeenInScanning;
	InputDagState lastTextStateOfFailure;
	ArrayList<CloseElementNode> scanningFailures;

	@SuppressWarnings("unused")
	ParsingChart() {
		this.firstToUse = 0;
		// table = new ArrayList<ParsingItem>();
		// index = new ArrayList<ArrayList<Integer>>();
		// table2 = new TreeSet<ParsingItem>();
		this.firstArgsOfReduce = new ArrayList<HashMap<String, ArrayList<ParsingItem>>>();
		this.itemsAtTextPos = new SortedTableOfItems();
		this.itemsAtNextTextPos = new SortedTableOfItems();
		this.indexAgendaTextPos = 0;
		this.indexAgendaNextTextPos = 0;
		this.results = new ArrayList<ProductionRule>();
		this.agendaTextPos = new ArrayList<ParsingItem>();
		this.agendaNextTextPos = new ArrayList<ParsingItem>();
	}

	public ArrayList<ParsingItem> getFirstArgsOfReduce(InputDagState j, NonTerminal x) {

		return this.firstArgsOfReduce.get(j.getId()).get(x.getName());
	}

	/*
	 * public boolean isInChart(ParsingItem p){ // we use the index by j.
	 * 
	 * 
	 * 
	 * //System.out.println("Is in chart pour "+p); if (p.getJ().getId() >=
	 * index.size()){ //System.out.println("Pas trouvé ; index.size"); return
	 * false; } else{ ArrayList<Integer> jInd = index.get(p.getJ().getId());
	 * Iterator<Integer> jIter = jInd.iterator();
	 * 
	 * int iCour;
	 * 
	 * while (jIter.hasNext()){ iCour = jIter.next().intValue(); if
	 * (p.equals(table.get(iCour))){
	 * //System.out.println("On en a trouvé UN !!!"); return true; } }
	 * //System.out.println("Pas trouvé via liste d'index");
	 * //System.out.println("liste index = "+index); return false; } };
	 */

	public boolean isInChart(ParsingItem p) {
		// System.out.println("Is in chart("+p+")");

		/*if (this.itemsAtTextPos.size() == 0) {
			// System.out.println("Non 0");
			return false;
		}
		
		// return table2.contains(p);
		if (p.getJ().getTextPosition() == this.itemsAtTextPos.get(0).getJ()
				.getTextPosition()) {
			Iterator<ParsingItem> itIterator = this.itemsAtTextPos.iterator();
			while (itIterator.hasNext()) {
				if (itIterator.next().equals(p)) {
					return true;
				}
			}
			return false;
		}
		Iterator<ParsingItem> itIterator = this.itemsAtNextTextPos.iterator();
		while (itIterator.hasNext()) {
			if (itIterator.next().equals(p)) {
				return true;
			}
		}
		return false;*/
		return (this.itemsAtTextPos.isInTable(p) || this.itemsAtNextTextPos.isInTable(p));
		
	}

	public void addItem(ParsingItem it) {
		
		//System.out.println("On essaie d'ajouter : "+it);
		
		int j = it.getJ().getId();
		// int textPosOfItem = it.getJ().getTextPosition();
		int currentTextPos;

		try {
			/*currentTextPos = this.itemsAtTextPos.get(0).getJ()
					.getTextPosition();*/
			currentTextPos = this.itemsAtTextPos.getTextPos();
		} catch (Exception e) {
			currentTextPos = 0;
		}
		if (!isInChart(it)) {
			
			//System.out.println("On ajoute vraiment (pas déjà dans le chart)");
			
			if (it.getJ().getTextPosition() == currentTextPos) {
				this.itemsAtTextPos.add(it);
				this.agendaTextPos.add(it);
				
			} else {
				this.itemsAtNextTextPos.add(it);
				this.agendaNextTextPos.add(it);
			}

			if (it.isPredictable()) {

				while (this.firstArgsOfReduce.size() <= j) {
					this.firstArgsOfReduce
							.add(new HashMap<String, ArrayList<ParsingItem>>());
				}
				String ntName = ((NonTerminal) it.getDottedSymbol()).getName();

				if (this.firstArgsOfReduce.get(j).get(ntName) == null) {
					this.firstArgsOfReduce.get(j).put(ntName,
							new ArrayList<ParsingItem>());
				}
				this.firstArgsOfReduce.get(j).get(ntName).add(it);
			} else {
				if (it.dotAtEnd()) {
					StringBuffer ntName = new StringBuffer();

					ntName.append(
							it.getDottedRule().getRule().getLeft().getName())
							.append("_").append(it.getI().getId()).append("_").append(it.getJ().getId()); //$NON-NLS-1$ //$NON-NLS-2$
					// ntName =
					// it.getDottedRule().getRule().getLeft().getName()+"_"+it.getI().getId()+"_"+it.getJ().getId();
					NonTerminal newLeft = new NonTerminal(ntName.toString());
					this.results.add(new ProductionRule(newLeft, it
							.getDottedRule().getRule().getRight()));
				}
			}
		}
	}

	@Override
	public String toString() {
		StringBuffer res = new StringBuffer();

		/*for (int i = 0; i < this.itemsAtTextPos.size(); i++) {
			res.append(this.itemsAtTextPos.get(i) + "\n"); //$NON-NLS-1$
		}
		res.append("-------------\n"); //$NON-NLS-1$
		for (int i = 0; i < this.itemsAtNextTextPos.size(); i++) {
			res.append(this.itemsAtNextTextPos.get(i) + "\n"); //$NON-NLS-1$
		}*/
		Iterator<ParsingItem> it = this.itemsAtTextPos.iterator();
		while (it.hasNext()){
			res.append(it.next()+"\n");
		};
		res.append("fin Pos text Courante\n");
		it = this.itemsAtNextTextPos.iterator();
		while (it.hasNext()){
			res.append(it.next()+"\n");
		};
		return res.toString();
	}

	@SuppressWarnings("unused")
	public ParsingItem giveNextToProcess() {
		// ParsingItem res = table.get(firstToUse);
		// firstToUse = firstToUse + 1;
		// return res;
		if (this.indexAgendaTextPos < this.agendaTextPos.size()) {
			this.indexAgendaTextPos = this.indexAgendaTextPos + 1;
			
			//System.out.println("Next to process = "+this.agenda.get(this.indexAgenda -1));
			
			return this.agendaTextPos.get(this.indexAgendaTextPos -1);
		}
		//System.out.println("On passe à la position suivante dans le texte...");
		this.itemsAtTextPos = this.itemsAtNextTextPos;
		this.itemsAtNextTextPos = new SortedTableOfItems();
		
		this.agendaTextPos = this.agendaNextTextPos;
		this.indexAgendaTextPos = 0;
		this.indexAgendaNextTextPos = 0;
		this.agendaNextTextPos = new ArrayList<ParsingItem>();
		return giveNextToProcess();
	}

	@SuppressWarnings("unused")
	public void resetAgenda() {
		
		this.indexAgendaTextPos = 0;
		this.itemsAtNextTextPos = new SortedTableOfItems();
		this.agendaNextTextPos = new ArrayList<ParsingItem>();
		this.indexAgendaNextTextPos = 0;
		
	}

	public boolean hasNextToProcess() {
		// System.out.println("indexAgenda = "+indexAgenda);
		// System.out.println("itemsAtTextPos.size() = "+itemsAtTextPos.size());
		return this.indexAgendaTextPos < this.agendaTextPos.size() || 
				this.indexAgendaNextTextPos < this.agendaNextTextPos.size();
				
	}

	public boolean testSuccess(InputDagState initState, InputDagState finalState,
			NonTerminal axiom) {
		// succes is an item looking like :
		// Axiom -> alpha ., initState, finalState.
		// we first get the index for "finalState".
		// actually, any item with initState ansd lastState would (almost)
		// be ok !

		// System.out.println("testSuccess");

		/* we look for success into testPos */
		Iterator<ParsingItem> itIterator = this.itemsAtTextPos.iterator();
		boolean found = false;
		ParsingItem cur;
		while (!found && itIterator.hasNext()) {
			cur = itIterator.next();

			//System.out.println("test success : On regarde : "+cur);

			found = cur.getI().equals(initState)
					&& cur.getDottedRule().getRule().getLeft().equals(axiom);
			/*
			 * if (found){ System.out.println("OK, TROUVE"); }
			 */
		}
		return found;
	}

	public ArrayList<ProductionRule> extractResult() {
		return this.results;
	}
	
	public int getSize(){
		return this.itemsAtTextPos.size();
	}
	
}