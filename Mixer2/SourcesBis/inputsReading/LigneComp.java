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
import java.util.Iterator;

import xmlNodes.GenericNode;

// une ligne du fichier compagnon

public class LigneComp implements Comparable<LigneComp> {
	private int idTexte;
	private ArrayList<GenericNode> theNodes;

	LigneComp(int i, ArrayList<GenericNode> l) {
		this.idTexte = i;
		this.theNodes = l;
	}

	@Override
	public String toString() {
		StringBuffer res = new StringBuffer();

		res.append("" + this.idTexte); //$NON-NLS-1$
		Iterator<GenericNode> i = this.theNodes.iterator();
		while (i.hasNext()) {
			res.append(" "); //$NON-NLS-1$
			res.append(i.next().toString());
		}

		return res.toString();
	}

	public int getIdTexte() {
		return this.idTexte;
	}

	public ArrayList<GenericNode> getTheNodes() {
		return this.theNodes;
	}

	@Override
	public int compareTo(LigneComp o) {

		if (this.idTexte == o.getIdTexte()) {
			return 0;
		}
		if (this.idTexte < o.getIdTexte()) {
			return -1;
		}
		return 1;
	}
}
