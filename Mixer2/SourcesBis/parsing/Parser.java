package parsing;

// XMLMixer: a tool for embedding annotations into XML files.

//Copyright (C) 2017  Bertrand Gaiffe
// bertrand.gaiffe@atilf.fr

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
import java.io.*;

import grammar.*;
import partialOrder.POSet;

import inputDag.InputDag;
import inputDag.InputDagState;
import inputDag.InputDagTransition;

import inputsReading.XmlAndCompReader;
import inputsReading.InputsReader;

import xmlNodes.CloseElementNode;
import xmlNodes.GenericNode;

import parsing.ScanningFailure;

// TODO : better parsing algorithm : so far we have pure Earley...
// TODO : Better error localisation ; we should not rely on textPosition in order to find
// the state from which the correction has to be made.
public class Parser {
	CFGrammar theGrammar;
	ParsingChart theChart;
	InputDag theDag;

	// for corrections...
	int textPosSeen;
	ArrayList<ScanningFailure> failuresForCorrection; /* a scanning failure is a couple : item + a state */
	//ArrayList<ParsingItem> itemsForCorrection;
	ArrayList<ProductionRule> resGrammar;
	int nbCorr;

	@SuppressWarnings("unused")
	Parser(CFGrammar g, InputDag s) {
		this.theGrammar = g;
		this.theChart = new ParsingChart();
		this.theDag = s;
		this.textPosSeen = -1;
		this.resGrammar = new ArrayList<ProductionRule>();
		this.nbCorr = 0;
	}

	public ParsingChart getChart() {
		return this.theChart;
	}

	public void nullifyChartAndDag() {
		this.theChart = null;
		this.theDag = null;
	}

	// The initRule that builds the initial Items
	public void initRule() {
		// for all rules that have the axiom as left
		ArrayList<ProductionRule> rulesAxiom = this.theGrammar.rulesForAxiom();
		Iterator<ProductionRule> ir = rulesAxiom.iterator();

		while (ir.hasNext()) {
			this.theChart.addItem(new ParsingItem(new DottedRule(ir.next(), 0),
					this.theDag.getInit(), this.theDag.getInit()));
		}
	}

	public ArrayList<ProductionRule> getResGrammar() {
		return this.resGrammar;
	}


	
	public ArrayList<ScanningFailure> getScanFailures(InputDagState init){
		int levelMax = 0;
		
		
		/* On change notre fusil d'épaule : le(les) item(s) à corriger sont ceux qui échouent dans le dag d'entrée 
		 * à un état de niveau maximal...
		 * */ 
		 
		Iterator<ScanningFailure> iicor = this.failuresForCorrection.iterator();
		ArrayList<ScanningFailure> res = new ArrayList<ScanningFailure>();
		ScanningFailure scanFailCour;
		InputDagTransition trans;
		
		while (iicor.hasNext()){
			scanFailCour = iicor.next();
			// what is the level of the failure ?
			
			trans = scanFailCour.getTrans();
			//System.err.println("getFailedItems : On regarde "+scanFailCour.getItem()+" de niveau "+scanFailCour.getItem().getJ().getLevel());
			//System.err.println("getFailedItems : On regarde "+scanFailCour.getItem()+" de niveau "+trans.getSucc().getLevel());
						
			if (trans.getSucc().getLevel() > levelMax) {
				res = new ArrayList<ScanningFailure>();
				levelMax = trans.getSucc().getLevel();
			}
			res.add(scanFailCour);
		}
		return res;
	}
	

	@SuppressWarnings("unused")
	public void applyInfRulesToItem(ParsingItem item) {

		// System.out.println("applyInfRulesToItem("+item+")");

		// ***** SCAN **********
		if (item.isScannable()) {

			// System.out.println("Item = "+item);
			// System.out.println("scannable");

			InputDagState j = item.getJ();

			if (j.getTextPosition() != this.textPosSeen) {
				this.textPosSeen = j.getTextPosition();
				//this.itemsForCorrection = new ArrayList<ParsingItem>();
				this.failuresForCorrection = new ArrayList<ScanningFailure>();
				//System.out.println("On passe à textPos = "+j.getTextPosition());

			}
			// System.out.println("j = "+j);
			ArrayList<InputDagTransition> trans = j.getTransitions();
			Iterator<InputDagTransition> itrans = trans.iterator();
			InputDagTransition t;
			ParsingItem curRes;

			while (itrans.hasNext()) {
				t = itrans.next();

				// System.out.println("On regarde la transition : "+t);agenda

				curRes = item.scan(t);
				if (curRes != null) {
					this.theChart.addItem(curRes);

				} else {
					// we therefore failed.
					Terminal failedTerm = (Terminal) item.getSymbolAfterDot();
					GenericNode failedGn = failedTerm.getVal();

					// System.out.println(failedGn+" expected at textPos ="+j.getTextPosition()+" and state "+j.getId()+" there was "+t.getNode());

					if (failedGn.getType() == GenericNode.CLOSE_ELEM
							&& t.getNode().getType() == GenericNode.CLOSE_ELEM){
							//&& item.getI().getTextPosition() != j
							//		.getTextPosition()) {
						/*
						 * System.out.println(failedGn+" expected at textPos ="+j
						 * .getTextPosition()+ " and state "+j.getId());
						 * System.out.println("We read "+t.getNode());
						 */
						/* We should add the symbol we failed on so that the correction is doable ! */
						//this.itemsForCorrection.add(item);
						this.failuresForCorrection.add(new ScanningFailure(item, t));
					}
				
				}
			}
		} else if (item.isPredictable()) {
			// System.out.println("Predict");
			// We could there make use of the "first" set of terminals on  every non-terminal
			// this would reduce the number of predicted items.
			
			
			InputDagState j = item.getJ();
			// we need the NonTerminal that is at position dot.
			DottedRule dr = item.getDottedRule();
			NonTerminal nt = (NonTerminal) dr.getSymbolAtDot();
			// we now produce an item for each rule in the grammar
			// whose left part is nt.

			ArrayList<ProductionRule> rNt = this.theGrammar.rulesForNt(nt);
			Iterator<ProductionRule> rIt = rNt.iterator();
			ProductionRule curRule;
			ParsingItem newItem;
			DottedRule newDottedRule;
			while (rIt.hasNext()) {
				curRule = rIt.next();
				newDottedRule = new DottedRule(curRule, 0);
				newItem = new ParsingItem(newDottedRule, j, j);
				this.theChart.addItem(newItem);
			}
		} else if (item.dotAtEnd()) { // necesseraly the case !
			// System.out.println("reduce");
			// our item is such as : <W -> gama ., j, k>
			// we look for all items such as :
			// <Y -> alpha . W beta, i, j>
			// in order to produce :
			// <Y -> alpha W . beta, j, k>
			InputDagState j = item.getI();
			NonTerminal w = item.getDottedRule().getRule().getLeft();
			ArrayList<ParsingItem> theFirstArgs = this.theChart
					.getFirstArgsOfReduce(j, w);
			if (theFirstArgs != null) {
				Iterator<ParsingItem> itemIter = theFirstArgs.iterator();
				ParsingItem curItem, newItem;
				DottedRule newDr;
				while (itemIter.hasNext()) {
					curItem = itemIter.next();
					// we produce the new item.
					// we should modify the Non Terminal in the produced item...
					// that is, we modify the non terminal the dot is on.
					newDr = new DottedRule(curItem
							.getDottedRule()
							.getRule()
							.replaceNTat(item.getI(), item.getJ(),
									curItem.getDottedRule().getDot()), curItem
							.getDottedRule().getDot() + 1);

				

					newItem = new ParsingItem(newDr, curItem.getI(),
							item.getJ());
					//System.out.println("On a réduit !");
					//System.out.println("ca donne :"+newItem);
					this.theChart.addItem(newItem);
				}
			}
		}
	}

	
	// On devrait ne faire qu'une correction !
	
	@SuppressWarnings("unused")
	public void repairDag(InputDagState init) {

		ArrayList<ScanningFailure> failedScans = this.getScanFailures(init);
		Iterator<ScanningFailure> iFailedScan = failedScans.iterator();
		
		int localNbCorr = this.nbCorr;

		InputDagState sdeb = null;
		InputDagState sfin = null;
		
		ArrayList<Terminal> failedTok = new ArrayList<Terminal>();

		ScanningFailure curFailure;
		ParsingItem curFailItem;
		Terminal cTok;
		int tIt;
		boolean found;
		String nameSpace = new String(""); //$NON-NLS-1$
		InputDagTransition failedTrans = null;
		
		//while (iFailedScan.hasNext()) {       //BG 26/02/2019
			
			curFailure = iFailedScan.next();
			curFailItem = curFailure.getItem();
			
			System.err.println("On s'intéresse à l'item : "+curFailItem);
			tIt = 0;
			found = false;
			while (tIt < failedTok.size() && !found) {
				cTok = failedTok.get(tIt);
				//System.out.println("On compare "+cTok+" et "+curFailItem.getDottedSymbol());
				if (cTok.equals(curFailItem.getDottedSymbol())) {
					// System.out.println("égaux");
					found = true;
				} else {
					// System.out.println("Différents");

				}

				tIt = tIt + 1;
			}
			if (!found) {
				failedTok.add(((Terminal) curFailItem.getDottedSymbol()));
				// we also modify the opening bracket !
				System.err.println("repairDag item to modify "+curFailItem+" avec pour next : "+nbCorr);
				nameSpace = curFailItem.putNextOnOpeningBracket(localNbCorr);
				localNbCorr = localNbCorr + 1;
				failedTrans = curFailure.getTrans();
				sdeb = curFailItem.getI();
				sfin = curFailItem.getJ();
				Iterator<InputDagTransition> idt;
				idt=sfin.getTransitions().iterator();
				System.err.println("++++++++++++++++++++++");
				while (idt.hasNext()){
					System.err.println(idt.next().getNode());
				};
				System.err.println("++++++++++++++++++++++");
				/*on les met dans une liste des trucs à ajouter... */
				/* du coup, on a des </x> et il faudra ajouter des </x> <x> sur l'état de début de l'item qui faile */
		//}  BG 26/02/2019

		// Pb : on peut maintenant échouer sur autre chose que des closing
		// brackets !

		// failedTok contains closing brackets, we build the corresponding
		// opening brackets.
		// we need the ArrayList<ArrayList<GenericNode>> that was used to build
		// the dag at
		// this textPosition.
		InputDagState stateToModify;
		//System.err.println("Failed Items = "+this.getFailedItems(init)+
		//" at position = "+this.getFailedItems(init).get(0).getJ().getTextPosition());

		stateToModify = this.theDag.getFirstStateOfTextPosition(this
				.getScanFailures(init).get(0).getItem().getJ().getTextPosition());
		//ArrayList<ArrayList<GenericNode>> initialListsForState = stateToModify
		//		.getListesEtat();
		
		
		//System.err.println("state to modify = "+stateToModify+" c.a.d. premier état à la position "+this
		//.getFailedItems(init).get(0).getJ().getTextPosition());
		
		POSet<GenericNode> initialListsForState = stateToModify.getListesEtat();
		
		
		
		
		//System.out.println("Liste initiales pour l'état");
		//System.out.println(initialListsForState);
		// we have to add a new list made of initialListsForState + closing
		// bracket + opening one.
		//
		
		Iterator<Terminal> failedTokIt = failedTok.iterator();
		ArrayList<GenericNode> newToks, newToks2;
		Terminal ftk;
		GenericNode openBrak;
		while (failedTokIt.hasNext()) {
			newToks = new ArrayList<GenericNode>();
			ftk = failedTokIt.next();

			System.err.println("failed on (failedToks) "+ftk);

			// we add a closing bracket
			newToks.add(ftk.getVal());
			// the namespace we need is into this.getFailedItems(init).get(0)

			
			openBrak = ((CloseElementNode) ftk.getVal()).openFromClosing(
					this.nbCorr, nameSpace);
			// we add the corresponding opening bracket.
			//newToks.add(((CloseElementNode) ftk.getVal()).openFromClosing(
			//		this.nbCorr, nameSpace));
			newToks.add(openBrak);
			
			// we have to modify the opening brackets (@next)
			// into the items that failed on this symbol
			
			this.nbCorr = this.nbCorr + 1;
			if (initialListsForState == null) {
			
			} else {
				// the problem now, is that this inserts anywhere !
				// it should follow the point of failure !
				initialListsForState.addChain(newToks);
				
				System.err.println("On a ajouté la chaine : ");
				for (Iterator<GenericNode> ita = newToks.iterator(); ita.hasNext();){
					System.err.println(ita.next());
				};
				// Il faut aussi ajouter une chaîne entre 
				// </c> et </y> avec </c> : le close ajouté et </y> le token en erreur dans le dag !
				
				newToks2 = new ArrayList<GenericNode>();
				newToks2.add(ftk.getVal());
				newToks2.add(failedTrans.getNode());
				newToks2.add(openBrak);
				// Il nous faut l'élément sur lequel on a échoué (dans le DAG) (ce sur quoi le scan a échoué....)
				initialListsForState.addChain(newToks2);
				
				
				System.err.println("Liste initiales pour l'état (après modif)");
				System.err.println(initialListsForState);
			}
			// the problem is to insert also the element we read instead of ftk.
			// we should also add an attribute (@newt) into the
			// item on the opening bracket.
		}
	
		InputDagState oldFinal = stateToModify.getEtatFinal();
		//System.err.println("oldFinal = "+oldFinal);
		InputDagState newFinal = stateToModify.extendAutomaton(
				initialListsForState, stateToModify.getTextPosition());
	
		/* Il faudrait faire un ou avec l'automte initial ! */
		Iterator<InputDagTransition> itrans = oldFinal.getTransitions().iterator();
		while (itrans.hasNext()) {
			newFinal.addTransition(itrans.next());
		}}
		
		/* On s'intéresse maintenant à l'autre bout de l'item en échec ! */
		System.err.println("On veut ajouter à :");
		System.err.println(sdeb);
		
		/* On veut lui ajouter </a> et <a> avec a le symbole sur lequel on a échoué... */
		/* normalement, ce symbole est celui qui est présent en sFin */
		//System.out.println("=======================");
		//System.out.println(sfin);
		/*Iterator<InputDagTransition> idt;
		idt=sfin.getTransitions().iterator();
		while (idt.hasNext()){
			System.out.println(idt.next().getNode());
		}*/
	} 
	
	
	
	

	public void parse(boolean first) {
		ParsingItem curItem;
		int textPosition;
		int oldTextPosition;
		int i;
		
		
		i = 0;
		oldTextPosition = 0;
		textPosition = 0;
		// itemsForCorrection = new ArrayList<ParsingItem>();
		if (first) {
			initRule();
		} else {
			//this.itemsForCorrection = new ArrayList<ParsingItem>();
			
			this.failuresForCorrection = new ArrayList<ScanningFailure>();
			
			this.theChart.resetAgenda();
			// itemsForCorrection = new ArrayList<ParsingItem>();
			//System.out.println("on a reset Agenda ; le chart est maintenant :");
			//System.out.println(this.theChart);
			//System.out.println("fin du chart après reset.");
			//System.out.println("Agenda :");
			//for (Iterator<ParsingItem> it = this.theChart.agendaTextPos.iterator(); it.hasNext();){
			//	System.out.println(it.next());
			//};
			//System.out.println("fin  Agenda");
		}
		if (!this.theChart.hasNextToProcess()) {
			System.out.println("RIEN A FAIRE APRES INIT");
		}
		while (this.theChart.hasNextToProcess()) {
			
			
			curItem = this.theChart.giveNextToProcess();
			
			
			textPosition = curItem.j.getTextPosition();
			
			if (textPosition - oldTextPosition > 10){
				System.err.print(".");
				oldTextPosition = textPosition;
			}
			/*i = i + 1;
			if (i % 10000 == 0){
				System.err.println("taille chart ="+this.theChart.getSize());
			};*/
			// On voudrait la position dans le texte...
			
			//System.out.println("On traite : "+curItem);
			applyInfRulesToItem(curItem);
			//System.out.println("The chart at this step ");
			//System.out.println(theChart);
			//System.out.println("End the chart at this step ");
		}
		// Deux cas maintenant, soit on a un succès, soit on est en erreur.

	}


	@SuppressWarnings({ "boxing", "null" })
	public static void main(String[] argv) throws Exception {

		

		/* Il faudrait qu'on se prémunisse du BOM utf8 ! */
		/* Une façon de le faire pourrait être de modifier la grammaire.... */ 

		XMLGrammar g = new XMLGrammar();

		

		InputsReader xmlC = null;
		
		//System.out.print("arguments :"+argv[0]+" et "+argv[1]);
		try{
			xmlC = new XmlAndCompReader(argv[0], argv[1]);
		
		
		
			System.err.println("bw");
			int r = 0;
			while (xmlC.read()) {
			// we'll see later how to read the dag only when needed.
				r = r+1;
				if (r % 1000 == 0){
					System.err.print("#");
				}
			}
			System.err.println("après bw");
		}
		catch (Exception e){
			System.out.println("Erreur(pour voir.... !");
			e.printStackTrace();
		}
		InputDag d = xmlC.getDag();

		// System.out.println("Got the dag");

		// we free the rest of xmlC that we do not need anymore...
		xmlC = null;

		// we write the dag in a graphviz file
		
		 //try{ PrintWriter out = new PrintWriter("dag.dot"); out.println(d);
		 //out.close(); } catch(Exception e){
		 //System.out.println("Pb a l'écriture du graphe !"); };
		 
		/*try{ PrintWriter out = new PrintWriter("dag.dot"); out.println(d);
		 out.close(); } catch(Exception e){
		 System.out.println("Pb a l'écriture du graphe !"); };
		// System.err.println("dag construit");*/

		System.err.println("Entrées lues");
		Parser p = null;
		try{
			p = new Parser(g, d);
		}
		catch (Exception e){
			System.out.println("Création parser");
			e.printStackTrace();
		};
		try{
			p.parse(true);
		}
		catch (Exception e){
			System.out.println("Sur le parsing...");
			e.printStackTrace();
		}
		//System.out.println("le chart après parsing :");
		//System.out.println(p.getChart());

		//System.out.println("après le chart après parsing.");
		
		try{
			if (p.getChart().testSuccess(d.getInit(), d.getLast(),
					new NonTerminal(g.getAxiom().getName()))) {
				//System.out.println("Succes !");
				//System.out.println("Résultat (grammaire)"); 
			
				ArrayList<ProductionRule> gramResProds = p.getChart()
						.extractResult();
			
				//System.out.println("après extract résults");
			
				p.nullifyChartAndDag();

				/*
				 * Iterator<ProductionRule> itRes = gramResProds.iterator(); while
				 * (itRes.hasNext()){ System.out.println(itRes.next()); };
				 */
				// we build a CFGrammar with the result.
				CFGrammar gResult = new CFGrammar(new NonTerminal(g.getAxiom()
						.getName()
						+ "_" //$NON-NLS-1$
						+ d.getInit().getId()
						+ "_" //$NON-NLS-1$
						+ d.getLast().getId()), gramResProds);

				//System.out.println("gResult :");
				//System.out.println(gResult);
					
				// On ne nettoie pas !
				// gResult.cleanProds();
				//System.out.println("après nettoyage");
				//System.out.println(gResult);

				// System.out.println("Résultat en XML");
				// System.out.println(gResult.extractAResult(gResult.getAxiom()));

				// System.out.println("A travers DOM :");
				System.out.println(gResult.extractAResultThroughDom());
			} else {
				/*System.err.println("Echec !");
				System.err.println("dernier état : "+d.getLast().getId());
				System.err.println("dernière position : "+d.getLast().getTextPosition());
				//System.out.println("failed items ");*/

				while (!p.getChart().testSuccess(d.getInit(), d.getLast(),
						new NonTerminal(g.getAxiom().getName()))) {
					System.err.println("Boucle correction");
					
					p.repairDag(d.getInit());
					//System.err.println("re Parsing");
					p.parse(false);
				}
			
				//System.err.println("Sorti de la boucle correction");
			
				if (p.getChart().testSuccess(d.getInit(), d.getLast(),
						new NonTerminal(g.getAxiom().getName()))) {
				
				
					ArrayList<ProductionRule> gramResProds = p.getChart()
							.extractResult();
				
					/*Iterator<ProductionRule> itRes = gramResProds.iterator();
				 		while (itRes.hasNext()){ System.out.println(itRes.next()); };*/
				 
					// we build a CFGrammar with the result.

					p.nullifyChartAndDag();

					//System.out.println("Après nullify chart and dag");
					//System.out.println("état initial = "+d.getInit().getId());
					//System.out.println("état final = "+d.getLast().getId());	
				
					CFGrammar gResult = new CFGrammar(new NonTerminal(g.getAxiom()
							.getName()
							+ "_" //$NON-NLS-1$
							+ d.getInit().getId()
							+ "_" //$NON-NLS-1$
							+ d.getLast().getId()), gramResProds);

					// On ne nettoie pas !
					//gResult.cleanProds();
					//System.out.println("après nettoyage");
					//System.out.println(gResult);

					// System.out.println("Résultat en XML");
					// System.out.println(gResult.extractAResult(gResult.getAxiom()));

					// System.out.println("A travers DOM :");
					System.out.println(gResult.extractAResultThroughDom());

					// };
				}
			}
		}
		catch (Exception e){
				System.out.println("Dans la réparation");
				e.printStackTrace();
		}	
			
	}
}
