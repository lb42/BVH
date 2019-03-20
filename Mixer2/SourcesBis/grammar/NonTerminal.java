package grammar;



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

	public class NonTerminal implements GramSymb {

		String val;

		public NonTerminal(String v) {
			// System.out.println("On crée le NT : "+v);
			this.val = v.intern();
		}

		@Override
		public boolean isTerminal() {
			return false;
		}

		public String getName() {
			return this.val;
		}

		@Override
		public String toString() {
			return "NT[" + this.val + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		@Override
		public boolean equals(GramSymb other) {
			return !other.isTerminal()
					&& getName().equals(((NonTerminal) other).getName());
		}
	}


