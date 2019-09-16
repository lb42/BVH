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
import java.util.Iterator;

import partialOrder.POSet;
import inputDag.InputDagState;
import xmlNodes.GenericNode;
import xmlNodes.TextNode;


//les dags représentent le texte en entrée (auquel on ajoute ce qui vient du fichier compagnon)
//Ils sont formés d'états (dont on connait la position dans le texte)
//reliés par des :
//    TextNode ou
//    OpenElementNode ou
//    CloseElementNode  ou
//    ProcessingInstructionNode ou
//    CommentNode.

//exemple, étant donné le fichier <r>01<a><b>23</b>4</a>5</r>

//le DAG est :

//etats                 s_0 --<r>--> s_1 --"01"--> s_1   -- <a> --> s_2 --<b>--> s_3 --"23"--> s_4 --</b>--> s_5 --"4"--> etc...
//position texte        {0}          {0}           {2}              {2}          {2}           {4}           {4}
//listes               [[r]]        [["01"]]     [[<a><b>]]          ?          [["23"]]       [[</b>]]      [["4"]]

//On peut décider de compléter un état (uniquement un état le plus à gauche pour une position textuelle)
//avec des listes supplémentaires.

//Il faudrait écrire ça avec graphviz !

//les dags qu'on considère sont tels que les états correspondant à une nouvelle position dans le texte 
//sont des états de passage obligés
//on ne peut pas avoir parmi les successeurs d'un état à la fois du texte et 
//autre chose.



/**
* Dag
* @see InputDagState (most of the logic is there)
* @author bertrand
*
*/
public class InputDag {
	InputDagState initState;
	InputDagState lastState;

	static int totalNbStates = 0;
	
	String curText;
	int curTextPos;

	public InputDag(InputDagState i) {
		this.initState = i;
		this.lastState = this.initState;
		this.curText = ""; //$NON-NLS-1$
	}

	public InputDag(int i) {
		this.initState = new InputDagState(i, 0);
		this.lastState = this.initState;
		this.curText = ""; //$NON-NLS-1$
	}

	public InputDagState getInit() {
		return this.initState;
	}

	public InputDagState getLast() {
		return this.lastState;
	}
	public void extendAutomaton(POSet<GenericNode> tokens, int textPos){
		// we take into account the particular case that :
		// 1) the state just before last state as only one transition made of
		//    a textNode
		// 2) tokens contains only one chain made of a sole textNode.
		//
		//   if that is so, we just change the last transition making the textNode longer.
		this.lastState.textPosition = textPos;
		this.lastState = this.lastState.extendAutomaton(tokens, textPos);
		
	}
	public void prolongerAutomate(ArrayList<ArrayList<GenericNode>> tokens,
			int textPos) {

		// System.out.println("prolongerAutomate("+tokens+")");

		if (tokens == null) {
			System.out.println("WARNING : Dag.prolongerAutomate(null)");
		} else {
			this.lastState = this.lastState.prolongerAutomate(tokens, textPos);
		}
	}

	private static boolean isTextOnly(ArrayList<ArrayList<GenericNode>> tokens) {
		GenericNode gn;
		if (tokens.size() == 1) {
			if (tokens.get(0).size() == 1) {
				gn = tokens.get(0).get(0);
				if (gn.getType() == GenericNode.TEXT_NODE) {
					return true;
				}
				// not a Text node
				return false;
			}
			// tokens.get(0).size() != 1
			return false;
		}
		// tokens.size() != 1
		return false;
	}

	private static String textOf(ArrayList<ArrayList<GenericNode>> tokens) {
		return tokens.get(0).get(0).getStringValue();
	}

	// we suppose we will never add a mix of tags and textNodes ; however, textNodes may be (and will be)
	// only one character long, we therefore group them.
	public void prolongerAutomate2(ArrayList<ArrayList<GenericNode>> tokens,
			int textPos) {
		/*if (isTextOnly(tokens)) {
			this.curText = this.curText + textOf(tokens);
			this.curTextPos = textPos;
		} else {*/
			if (this.curText.length() != 0) {
				TextNode tn = new TextNode(this.curText);
				@SuppressWarnings("unused")
				ArrayList<GenericNode> l1 = new ArrayList<GenericNode>();
				l1.add(tn);
				@SuppressWarnings("unused")
				ArrayList<ArrayList<GenericNode>> ll = new ArrayList<ArrayList<GenericNode>>();
				ll.add(l1);
				this.prolongerAutomate(ll, this.curTextPos);
				this.curText = ""; //$NON-NLS-1$
			}
			this.prolongerAutomate(tokens, textPos);
		//}
	}

	@Override
	public String toString() {
		// we want all successors of initState.
		// Then we print them in order.
		StringBuffer res = new StringBuffer();

		ArrayList<InputDagState> s = this.initState.successeursEtoile();
		Iterator<InputDagState> i = s.iterator();

		res.append("digraph resultat{\n");
		// res.append("graph [rankdir=LR];\n");

		res.append(this.initState);
		while (i.hasNext()) {
			res.append(i.next().toString());
		}

		res.append("}"); //$NON-NLS-1$
		return res.toString();
	}

	public InputDagState getFirstStateOfTextPosition(int tp) {
		// all we have to do is go on (by any state) from the initial state
		InputDagState curState = this.initState;
		boolean end = false;
		ArrayList<InputDagState> suc;

		while (!end) {
			
			 /*System.err.println("getFirstStateOfTextPosition("+tp+") curState = "
			  +curState+ " textposition = "+curState.getTextPosition()); */
			 
			/*if (curState.getTextPosition() == tp && curState.getListesEtat() != null){*/
			if (curState.getTextPosition() == tp){
				
				return curState;
			}
			// curState.getTextPosition() != tp
			suc = curState.getSuccesseurs();
			if (suc.size() > 0) {
				curState = suc.get(0);
			} else {
				end = true;
			}
		}
		return null;
	}

}


