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
import xmlNodes.GenericNode;

public class IntNode implements GenericNode {
	int value;

	public IntNode(String s) {
		this.value = Integer.parseInt(s);
	}

	@Override
	public int getType() {
		return GenericNode.INT_NODE;
	}

	public int getValue() {
		return this.value;
	}

	@Override
	public String getStringValue() {
		return "" + this.value; //$NON-NLS-1$
	}

	@Override
	public boolean equals(GenericNode other) {
		return other.getType() == GenericNode.INT_NODE
				&& this.value == ((IntNode) other).value;
	}
}