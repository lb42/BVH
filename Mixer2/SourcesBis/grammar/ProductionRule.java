package grammar;

import grammar.*;
import partialOrder.*;
import xmlNodes.*;

import inputDag.InputDagState;

	import java.util.ArrayList;
import java.util.Iterator;

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
	

	public class ProductionRule {
		NonTerminal left;
		ArrayList<GramSymb> right;

		public ProductionRule(NonTerminal l, ArrayList<GramSymb> r) {
			this.left = l;
			this.right = r;
		}

		public int length() {
			return this.right.size();
		}

		public GramSymb getAt(int i) {
			if (i < this.right.size()) {
				return this.right.get(i);
			}
			// cannot go past the end of the rule !
			return null;
		}

		public NonTerminal getLeft() {
			return this.left;
		}

		public ArrayList<GramSymb> getRight() {
			return this.right;
		}

		@Override
		public String toString() {
			StringBuffer res = new StringBuffer(this.left.toString() + " -> "); //$NON-NLS-1$
			Iterator<GramSymb> rightIterator = this.right.iterator();
			while (rightIterator.hasNext()) {
				res.append(rightIterator.next().toString() + " "); //$NON-NLS-1$
			}
			return res.toString();
		}

		/*
		 * public ProductionRule instanciate(String newValue){ Iterator<GramSymb>
		 * rit = right.iterator(); ArrayList<GramSymb> newR = new
		 * ArrayList<GramSymb>(); GramSymb symbCour;
		 * 
		 * while (rit.hasNext()){ symbCour = rit.next(); if (symbCour.isTerminal()){
		 * newR.add(((Terminal)symbCour).instanciate(newValue)); } else{
		 * newR.add(symbCour); } }; return new ProductionRule(left, newR); };
		 */
		public ProductionRule modifyAt(int place, GenericNode newN) {
			@SuppressWarnings("unused")
			ArrayList<GramSymb> newR = new ArrayList<GramSymb>();
			Iterator<GramSymb> rit = this.right.iterator();

			for (int i = 0; i < place; i++) {
				newR.add(rit.next());
			}
			rit.next();
			newR.add(new Terminal(newN));
			while (rit.hasNext()) {
				newR.add(rit.next());
			}
			return new ProductionRule(this.left, newR);
		}

		public ProductionRule modifyCloseElement(String newName, String id) {
			Iterator<GramSymb> rit = this.right.iterator();
			@SuppressWarnings("unused")
			ArrayList<GramSymb> newR = new ArrayList<GramSymb>();
			GramSymb cur;
			Terminal curT;
			GenericNode curN;
			CloseElementNode curC, newcurC;
			while (rit.hasNext()) {
				cur = rit.next();
				if (cur.isTerminal()) {
					curT = (Terminal) cur;
					curN = curT.getVal();
					if (curN.getType() == GenericNode.CLOSE_ELEM) {
						curC = (CloseElementNode) curN;
						newcurC = curC.replaceName(newName);
						newcurC.setId(id);
						newR.add(new Terminal(newcurC));
					} else {
						newR.add(cur);
					}
				} else {
					newR.add(cur);
				}
			}
			return new ProductionRule(this.left, newR);
		}

		public boolean equals(ProductionRule other) {
			boolean res = this.left.equals(other.getLeft())
					&& (this.right.size() == other.getRight().size());
			Iterator<GramSymb> rightIter1 = this.right.iterator();
			Iterator<GramSymb> rightIter2 = other.getRight().iterator();
			while (res && rightIter1.hasNext()) {
				res = res && rightIter1.next().equals(rightIter2.next());
			}
			return res;
		}

		/* public ProductionRule replaceNTat(InputDagState i, InputDagState j, int place) {
		 
			Iterator<GramSymb> rightIt = this.right.iterator();
			@SuppressWarnings("unused")
			ArrayList<GramSymb> newRight = new ArrayList<GramSymb>();
			NonTerminal oldNT, newNT;

			for (int a = 0; a < place; a++) {
				newRight.add(rightIt.next());
			}
			oldNT = (NonTerminal) rightIt.next();
			if (oldNT.isTerminal()) {
				System.out.println("Pb : ProductionRule.replaceAt(" + this
						+ " index = " + place);
			}
			StringBuffer sb = new StringBuffer();
			sb.append(oldNT.getName())
					.append("_").append(i.getId()).append("_").append(j.getId()); //$NON-NLS-1$ //$NON-NLS-2$
			// newNT = new NonTerminal(oldNT.getName()+"_"+i.getId()+"_"+j.getId());
			newNT = new NonTerminal(sb.toString());
			newRight.add(newNT);
			while (rightIt.hasNext()) {
				newRight.add(rightIt.next());
			}
			return new ProductionRule(this.left, newRight);
		} */

		// the non terminals that occur in the right part of the production.
		public ArrayList<NonTerminal> ntsInRight() {
			Iterator<GramSymb> rightIter = this.right.iterator();
			@SuppressWarnings("unused")
			ArrayList<NonTerminal> res = new ArrayList<NonTerminal>();
			GramSymb cour;

			while (rightIter.hasNext()) {
				cour = rightIter.next();
				if (!cour.isTerminal()) {

					res.add((NonTerminal) cour);
				}
			}
			return res;
		}

		public String putNextOnOpeningBracket(int id) {
			Iterator<GramSymb> rIt = this.right.iterator();
			GramSymb curS;
			Terminal curT;
			GenericNode curN;
			OpenElementNode curO;
			while (rIt.hasNext()) {
				curS = rIt.next();
				if (curS.isTerminal()) {
					curT = (Terminal) curS;
					curN = curT.getVal();
					if (curN.getType() == GenericNode.OPEN_ELEMENT) {
						curO = (OpenElementNode) curN;
						curO.addAttribute("next", "", "#Repair_new" + id); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						return curO.getNsUri();
					}
				}
			}
			return new String(""); //$NON-NLS-1$
		}
		// What is this for ????
					public ProductionRule replaceNTat(InputDagState i, InputDagState j, int place) {
						Iterator<GramSymb> rightIt = this.right.iterator();
						@SuppressWarnings("unused")
						ArrayList<GramSymb> newRight = new ArrayList<GramSymb>();
						NonTerminal oldNT, newNT;

						for (int a = 0; a < place; a++) {
							newRight.add(rightIt.next());
						}
						oldNT = (NonTerminal) rightIt.next();
						if (oldNT.isTerminal()) {
							System.out.println("Pb : ProductionRule.replaceAt(" + this
									+ " index = " + place);
						}
						StringBuffer sb = new StringBuffer();
						sb.append(oldNT.getName())
								.append("_").append(i.getId()).append("_").append(j.getId()); //$NON-NLS-1$ //$NON-NLS-2$
						// newNT = new NonTerminal(oldNT.getName()+"_"+i.getId()+"_"+j.getId());
						newNT = new NonTerminal(sb.toString());
						newRight.add(newNT);
						while (rightIt.hasNext()) {
							newRight.add(rightIt.next());
						}
						return new ProductionRule(this.left, newRight);
					}
	}


