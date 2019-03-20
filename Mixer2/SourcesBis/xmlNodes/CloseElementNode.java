package xmlNodes;
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

import xmlNodes.Attribute;
import xmlNodes.GenericNode;
import xmlNodes.OpenElementNode;

public class CloseElementNode implements GenericNode {
	String localName;
	String id;
	String qName;

	public CloseElementNode(String ln, String i) {
		this.localName = ln;
		this.id = i;
	}

	public CloseElementNode(String ln) {
		super();
		// localName = ln.substring(2);
		this.localName = ln;
		this.id = ""; //$NON-NLS-1$
	}

	@Override
	public String toString() {
		// return "</"+localName+"_"+id+">";
		return "</" + this.localName + ">"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getLocalName() {
		return this.localName;
	}

	@Override
	public int getType() {
		return GenericNode.CLOSE_ELEM;
	}

	public void setId(String i) {
		this.id = i;
	}

	@Override
	public String getStringValue() {
		return this.localName;
	}

	public CloseElementNode replaceName(String newName) {
		return new CloseElementNode(newName, this.id);
	}

	@Override
	public boolean equals(GenericNode other) {
		// System.out.println("CloseElementNode.equals entre "+this+" et "+other);
		boolean res = other.getType() == GenericNode.CLOSE_ELEM;
		CloseElementNode otCE = (CloseElementNode) other;
		// System.out.println("id = "+id+" other.id = "+otCE.id);

		return res && this.localName.compareTo(otCE.localName) == 0
				&& this.id.compareTo(otCE.id) == 0;
	}

	// Pb we need the associated namespace from the opening bracket
	public OpenElementNode openFromClosing(int nbCorr, String namespace) {
		@SuppressWarnings("unused")
		ArrayList<Attribute> lAtt = new ArrayList<Attribute>();
		Attribute a = new Attribute("xml:id", "", "Repair_new" + nbCorr); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		lAtt.add(a);
		return new OpenElementNode(this.localName, namespace, this.id, lAtt,
				false);
	}
}




