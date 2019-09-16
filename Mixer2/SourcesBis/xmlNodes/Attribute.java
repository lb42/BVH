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


	public class Attribute {
		String localName;
		String nameSpace;
		String value;
		String prefix;

		public Attribute(String ln, String ns, String v) {
			if (ln.contains("xml:")){
				this.localName=ln.split(":")[1];
				this.nameSpace="http://www.w3.org/XML/1998/namespace";
				this.prefix="xml";
				this.value=v;
			}
			else{
				this.localName = ln;
				this.nameSpace = ns;
				this.value = v;
				this.prefix = ""; //$NON-NLS-1$
			}
		}

		public Attribute(String ln, String p, String ns, String v) {
			this.localName = ln;
			this.nameSpace = ns;
			this.value = v;
			this.prefix = p;
		}

		public Attribute(javax.xml.stream.events.Attribute at) {
			this.localName = at.getName().getLocalPart();
			this.nameSpace = at.getName().getNamespaceURI();
			this.prefix = at.getName().getPrefix();
			this.value = at.getValue();
		}

		@Override
		public String toString() {
			if (this.prefix.compareTo("") != 0) { //$NON-NLS-1$
				return this.prefix + ":" +  //$NON-NLS-1$
					   this.localName + "='" +  //$NON-NLS-1$
					   this.value + "'"; //$NON-NLS-1$
			} 
			return this.localName + "='" +  //$NON-NLS-1$
				   this.value + "'";  //$NON-NLS-1$
			
		}

		public boolean equals(Attribute other) {
			return this.localName.compareTo(other.localName) == 0
					&& this.nameSpace.compareTo(other.nameSpace) == 0
					&& this.value.compareTo(other.value) == 0;
		}

		public String getLocalName() {
			return this.localName;
		}

		public String getValue() {
			return this.value;
		}
		public String getQName(){
			if (this.prefix.compareTo("") != 0){
				return this.prefix+":"+this.localName;
			}
			else{
				return this.localName;
			}
		}
		public String getNameSpaceURI(){
			return this.nameSpace;
		}
	}

