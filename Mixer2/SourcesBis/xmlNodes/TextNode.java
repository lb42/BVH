package xmlNodes;

//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.

//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.
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



public class TextNode implements GenericNode {
	String content;

	public TextNode(String cont) {
		this.content = cont;
	}

	public String getContent() {
		return this.content;
	}

	@Override
	public int getType() {
		return GenericNode.TEXT_NODE;
	}

	@Override
	public String toString() {
		return this.content;
	}

	@Override
	public String getStringValue() {
		return this.content;
	}

	@Override
	public boolean equals(GenericNode other) {
		return (other.getType() == GenericNode.TEXT_NODE)
				&& this.content.compareTo(((TextNode) other).content) == 0;
	}
}


