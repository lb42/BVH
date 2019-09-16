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
import grammar.GramSymb;
import grammar.ProductionRule;
import grammar.Terminal;
import inputDag.InputDagState;
import inputDag.InputDagTransition;
import xmlNodes.CloseElementNode;
import xmlNodes.GenericNode;

public class ParsingItem implements Comparable<ParsingItem> {
	DottedRule theDr;
	InputDagState i;
	InputDagState j;

	ParsingItem(DottedRule dr, InputDagState ii, InputDagState ji) {
		this.theDr = dr;
		this.i = ii;
		this.j = ji;
	}

	public InputDagState getJ() {
		return this.j;
	}

	public InputDagState getI() {
		return this.i;
	}

	public boolean dotAtEnd() {
		return this.theDr.dotAtEnd();
	}

	public boolean isScannable() {
		if (this.theDr.dotAtEnd()) {
			return false;
		}
		return this.theDr.getNext().isTerminal();
	}

	public DottedRule getDottedRule() {
		return this.theDr;
	}

	// the item that results of scanning on this transition
	// of course, the result may be null.
	public ParsingItem scan(InputDagTransition trans) {
		GramSymb sAtDot = getSymbolAfterDot();
		GenericNode dagSymb = trans.getNode();
		GenericNode dotSymb = ((Terminal) sAtDot).getVal();

		// System.out.println("Dans scan");
		if (dagSymb.getType() != dotSymb.getType()) {
			// System.out.println("types différents");
			return null;
		}
		if (dotSymb.getType() == GenericNode.CLOSE_ELEM) {
			CloseElementNode dagSymbClose = (CloseElementNode) dagSymb;
			CloseElementNode dotSymbClose = (CloseElementNode) dotSymb;
			// System.out.print("scan + close element entre "+dagSymb+" et "+dotSymb);
			if (dagSymbClose.equals(dotSymbClose)) {
				// System.out.println(" égaux");
				return new ParsingItem(this.theDr.dotProgress(), this.i,
						trans.getSucc());
			}
			// System.out.println(" différents");
			return null;
		}
		// System.out.println("à instancier");
		// System.out.println("dagSymb = "+dagSymb);
		ProductionRule newRule = this.theDr.instanciate(dagSymb);
		DottedRule newDr = new DottedRule(newRule, this.theDr.getDot() + 1);
		return new ParsingItem(newDr, this.i, trans.getSucc());
	}

	public boolean isPredictable() {
		if (this.theDr.dotAtEnd()) {
			return false;
		}
		return !this.theDr.getNext().isTerminal();
	}

	public GramSymb getSymbolAfterDot() {
		return this.theDr.getNext();
	}

	public GramSymb getDottedSymbol() {
		return this.theDr.getSymbolAtDot();
	}

	@Override
	public String toString() {
		StringBuffer res = new StringBuffer("<"); //$NON-NLS-1$
		res.append("" + this.i.getId() + ", "); //$NON-NLS-1$ //$NON-NLS-2$
		res.append(this.theDr.toString());
		res.append(", " + this.j.getId() + ">"); //$NON-NLS-1$ //$NON-NLS-2$
		return res.toString();
	}

	public boolean equals(ParsingItem other) {
		return this.i.equals(other.getI()) && this.j.equals(other.getJ())
				&& this.theDr.equals(other.getDottedRule());
	}

	@Override
	public int compareTo(ParsingItem other) {
		if (this.i.getId() < other.i.getId()) {
			return -1;
		} else if (this.i.getId() > other.i.getId()) {
			return 1;
		} else if (this.j.getId() < other.j.getId()) {
			return -1;
		} else if (this.j.getId() > other.j.getId()) {
			return 1;
		} else
			return this.toString().compareTo(other.toString());
	}

	/*
	 * public boolean equals(ParsingItem other){ return (this.compareTo(other)
	 * == 0); };
	 */
	public String putNextOnOpeningBracket(int id) {
		return this.theDr.putNextOnOpeningBracket(id);
	}
}
