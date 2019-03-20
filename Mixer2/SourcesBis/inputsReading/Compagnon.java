package inputsReading;

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
//import java.io.*;
//import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Stack;
import java.util.Collections;

import xmlNodes.CloseElementNode;
import xmlNodes.GenericNode;
import xmlNodes.IntNode;
import xmlNodes.OpenElementNode;

//une classe qui représente le fichier compagnon. i.e 
//chaque ligne est formée de :
//indiceTexteDébut indiceTexteFin suite de noeuds.


public class Compagnon {
	private ArrayList<LigneComp> tableauCompagnon;

	@SuppressWarnings("unused")
	Compagnon() {
		this.tableauCompagnon = new ArrayList<LigneComp>();
	}

	public ArrayList<LigneComp> getTable() {
		return this.tableauCompagnon;
	}

	@SuppressWarnings("unused")
	/*private boolean isNumericToken(String t) {
		try {
			Integer.parseInt(t);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	};*/

	// lecture d'un fichier compagnon. On utilise évidemment la classe
	// CompTokenReader !
	Compagnon(String fileName) {
		CompTokenReader tr = new CompTokenReader(fileName);
		GenericNode token;
		int debut = 0;
		int fin = 0;
		ArrayList<GenericNode> toksOnLine = new ArrayList<GenericNode>();
		// GenericNode gn;
		LigneComp deb, fi;
		int lineNumber = 0;
		int j = 0;

		this.tableauCompagnon = new ArrayList<LigneComp>();

		// a line in the companion file :
		token = tr.next();
		while (tr.hasNext()) {
			j = j+1;
			if (j % 1000 == 0){
				System.err.print(".");
			};
			if (token.getType() == GenericNode.INT_NODE) { // IntNode
				debut = ((IntNode) token).getValue();
				lineNumber = lineNumber + 1;
				// System.out.println("Début = "+debut);
			}
			token = tr.next();
			if (token.getType() == GenericNode.INT_NODE) { // IntNode
				fin = ((IntNode) token).getValue();
				// System.out.println("Fin = "+fin);
			}
			token = tr.next();
			int numT = 0;
			while (tr.hasNext() && token.getType() != GenericNode.INT_NODE) {
				if (token.getType() == GenericNode.OPEN_ELEMENT) {
					OpenElementNode opT = (OpenElementNode) token;
					// we set the id so that opening brackets and closing ones
					// are co-indexed.
					opT.setId("" + lineNumber + "_" + numT); //$NON-NLS-1$ //$NON-NLS-2$
					numT = numT + 1;
				}
				toksOnLine.add(token);
				token = tr.next();
			}
			
			// we have a complete line.
			// Iterator<GenericNode> i = toksOnLine.iterator();
			// while (i.hasNext()){
			// System.out.println(i.next());
			// };
			// Now, we have to build two LigneComp ; one corresponds to what
			// happens at debut
			// and the other to what happens at fin.

			// the problem is that the opening brackets should be coindexed with
			// the corresponding closing
			// brackets.

			deb = new LigneComp(debut, toksOnLine);
			// at fin we have all the elements that are not closed into
			// toksOnLine
			this.tableauCompagnon.add(deb);
			ArrayList<GenericNode> unclosed = nonClosedNodes(toksOnLine);
			if (unclosed.isEmpty()) {
				if (debut != fin) {
					System.err
							.println("Pb fichier compagnon "
									+ debut
									+ " différent de "
									+ fin
									+ " alors que tous les balises ouvertes sont fermées");
				}
			} else {
				fi = new LigneComp(fin, nonClosedNodes(toksOnLine));
				this.tableauCompagnon.add(fi);
			}
			
			// remove toksOnline's content in order to be ready to gather the
			// contents of the next line.
			toksOnLine = new ArrayList<GenericNode>();
		}
		
		// we sort by ascending indexes int the Text.
		System.err.println("\n sorting");
		Collections.sort(this.tableauCompagnon);
		System.err.println("\n sorted");
	}

	@SuppressWarnings("unused")
	private static ArrayList<GenericNode> nonClosedNodes(
			ArrayList<GenericNode> initList) {
		// Il nous faut une pile...
		// Stack<String> st = new Stack<String>();
		Stack<OpenElementNode> st = new Stack<OpenElementNode>();
		Iterator<GenericNode> i = initList.iterator();
		GenericNode c;
		String ln;
		ArrayList<GenericNode> res = new ArrayList<GenericNode>();
		CloseElementNode ce;

		while (i.hasNext()) {
			c = i.next();
			if (c.getType() == GenericNode.OPEN_ELEMENT) {
				// st.push(((OpenElementNode)c).getLocalName());
				st.push((OpenElementNode) c);
			}
			if (c.getType() == GenericNode.CLOSE_ELEM) {
				// we should have the localName on top of stack !
				ce = (CloseElementNode) c;
				ln = ((CloseElementNode) c).getLocalName();
				if (!ln.equals(st.peek().getLocalName())) {
					System.err.println("Pb compagnon : |" + ln
							+ "| non ouverte et fermée");
					System.err.println("tête de pile = " + st.peek());
					System.exit(42);
				} else {
					// we co-index the closing bracket with the top of the
					// stack.
					ce.setId(st.peek().getId());
				}
				st.pop();
			}
		}
		// now, we are interessed in the contents of the stack !
		Iterator<OpenElementNode> is = st.iterator();
		OpenElementNode cop;
		while (is.hasNext()) {
			// The non closed elements should be coindexed with their opening
			// brackets...
			cop = is.next();
			res.add(new CloseElementNode(cop.getLocalName(), cop.getId()));
		}
		return res;
	}

	@Override
	public String toString() {
		Iterator<LigneComp> i = this.tableauCompagnon.iterator();
		StringBuffer res = new StringBuffer();

		while (i.hasNext()) {
			res.append(i.next().toString());
			res.append("\n"); //$NON-NLS-1$
		}
		return res.toString();
	}

	// for testing...
	/*public static void main(String argv[]) {
		Compagnon c = new Compagnon("comp4");

		System.out.println(c);
	}
	*/
	

}
