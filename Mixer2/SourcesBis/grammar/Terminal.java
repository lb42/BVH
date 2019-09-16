package grammar;

import grammar.GramSymb;
import grammar.Terminal;
import xmlNodes.GenericNode;
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

	public class Terminal implements GramSymb {
		GenericNode val;

		Terminal(GenericNode v) {
			this.val = v;
		}

		@Override
		public boolean isTerminal() {
			return true;
		}

		@Override
		public String toString() {
			return this.val.toString();
		}

		public GenericNode getVal() {
			return this.val;
		}

		@Override
		public boolean equals(GramSymb other) {
			return other.isTerminal()
					&& (this.val.toString().compareTo(
							((Terminal) other).getVal().toString()) == 0);
		}

		/*
		 * public static void main(String argv[]){ GenericNode ca1 = new
		 * CloseElementNode("a", "0"); GenericNode ca2 = new CloseElementNode("a",
		 * "0");
		 * 
		 * if (ca1.equals(ca2)){ System.out.println("égaux"); } else{
		 * System.out.println("Différents"); } }
		 */
	}


