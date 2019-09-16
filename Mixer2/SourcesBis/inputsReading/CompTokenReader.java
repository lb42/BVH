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

import java.util.Iterator;
import java.io.*;

import xmlNodes.CloseElementNode;
import xmlNodes.CommentNode;
import xmlNodes.GenericNode;
import xmlNodes.IntNode;
import xmlNodes.OpenElementNode;
import xmlNodes.ProcessingInstructionNode;
import xmlNodes.TextNode;

// Une classe pour lire le fichier compagnon. On renvoie les tokens un par un...
// on ne renvoit que des strings...

public class CompTokenReader implements Iterator<GenericNode> {
	private BufferedReader br;
	boolean endFile = false;

	CompTokenReader(String fileName) {
		try {
			this.br = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			this.endFile = true;
		}
	}

	@Override
	public boolean hasNext() {
		return !this.endFile;
	}

	@Override
	public void remove() {// we do not need the remove method from Iterator.
	}

	private GenericNode readClosure() {
		StringBuffer res = new StringBuffer(""); //$NON-NLS-1$
		int r;

		try {
			while ((r = this.br.read()) != -1 && (char) r != '>') {
				res.append((char) r);
			}
			if (r == -1) {
				this.endFile = true;
			}
		} catch (IOException e) {
			this.endFile = true;
		}
		return new CloseElementNode(res.toString());
	}

	private GenericNode readPI() {
		StringBuffer res = new StringBuffer("<?"); //$NON-NLS-1$
		int r;
		boolean seenQMark = false;

		try {
			while (((r = this.br.read()) != -1)
					&& ((char) r != '>' | !seenQMark)) {
				if ((char) r == '?') {
					seenQMark = true;
				} else {
					seenQMark = false;
				}
				res.append((char) r);
			}
			return new ProcessingInstructionNode(res.toString());
		} catch (IOException e) {
			this.endFile = true;
		}
		return null;
	}

	@SuppressWarnings("incomplete-switch")
	private GenericNode readComment() {
		StringBuffer res = new StringBuffer("<!--"); //$NON-NLS-1$
		int r;
		char c;
		int state = 0; // 0 init, 1 first - seen, 2 second - seen, 3 > seen
		try {
			r = this.br.read();
			if ((char) r != '-') {
				throw new IOException();
			}
			c = (char) r;
			while (state != 3) {
				switch (state) {
				case 0:
					if (c == '-')
						state = 1;
					break;
				case 1:
					if (c == '-')
						state = 2;
					else
						state = 0;
					break;
				case 2:
					if (c == '>')
						state = 3;
					else
						state = 0;
					break;
				}
				res.append(c);
				if (state != 3) {
					c = (char) this.br.read();
				}
			}
			return new CommentNode(res.toString());
		} catch (IOException e) {
			this.endFile = true;
		}
		return null;
	}

	@SuppressWarnings("incomplete-switch")
	private GenericNode readCData() {
		StringBuffer res = new StringBuffer("<!["); //$NON-NLS-1$
		// int r;
		char c;
		int state = 0; // 0 : init, 1 first ] seen, 2 second ] seen, 3 > seen
		try {
			c = (char) this.br.read();
			while (state != 3) {
				switch (state) {
				case 0:
					if (c == ']')
						state = 1;
					break;
				case 1:
					if (c == ']')
						state = 2;
					else
						state = 0;
					break;
				case 2:
					if (c == '>')
						state = 3;
					else
						state = 0;
					break;
				}

				res.append(c);

				if (state != 3) {
					c = (char) this.br.read();
				}
			}
			// we remove <![CDATA[ at the beginning and ]]> at the end.
			return new TextNode(res.toString().substring(9,
					res.toString().length() - 3));
			// return new CDataNode(res.toString().substring(9,
			// res.toString().length()-3));
		} catch (IOException e) {
			this.endFile = true;
		}
		return null;
	}

	/*
	 * Pas bon ! On se retrouve avec des > dans des valeurs d'attributs et ça
	 * sème le bazzar !
	 */

	private GenericNode readOpening(char d) {
		StringBuffer res = new StringBuffer("<"); //$NON-NLS-1$
		char c;
		res.append(d);
		boolean inAttApos = false;
		try {
			c = (char) this.br.read();
			while (c != '>' || inAttApos) {
				res.append(c);
				if (c == '"') {
					if (inAttApos) {
						inAttApos = false;
					} else {
						inAttApos = true;
					}
				}
				c = (char) this.br.read();
			}
			res.append('>');
			// System.out.println("readOpening : On a lu l'élement :"+res);
			return new OpenElementNode(res.toString());
		} catch (IOException e) {
			this.endFile = true;
		}
		return null;
	}

	private GenericNode readCommentOrCdata() {
		// StringBuffer res = new StringBuffer("<!");
		int r;
		char c;
		try {
			r = this.br.read();
			c = (char) r;
			switch (c) {
			case '-':
				return readComment();
			case '[':
				return readCData();
			default:
				throw new IOException();
			}
		} catch (IOException e) {
			this.endFile = true;
		}
		return null;
	}

	@Override
	public GenericNode next() {
		char c;
		int r;
		StringBuffer res = new StringBuffer();

		// we read the next character
		try {
			while ((r = this.br.read()) != -1
					&& Character.isWhitespace((char) r)) {
				// we go past the whitespaces.
			}
			c = (char) r;
			if (Character.isDigit(c)) {
				res.append(c);
				while ((r = this.br.read()) != -1
						&& Character.isDigit((char) r)) {
					c = (char) r;
					res.append(c);
				}
				return new IntNode(res.toString());
			}
			// not a digit
			if (c == '<') {
				r = this.br.read();
				if (r == -1) {
					throw (new IOException());
				}
				// not a digit and not <
				c = (char) r;
				// System.out.println("dans next : char = |"+c+"|");
				switch (c) {
				case '/':
					return readClosure(); // element closure
				case '?':
					return readPI();// processing instruction
				case '!':
					return readCommentOrCdata(); // comment or cdata
				default:
					return readOpening(c); // element opening
				}
			}
			// should never arrive here !
			// System.out.println("else c < ; en fait, c = |"+c+"|");
			throw (new IOException()); // erreur de syntaxe.
		} catch (IOException e) {
			this.endFile = true;
		}
		return null;
	}

	public static void main(String argv[]) {
		CompTokenReader essai = new CompTokenReader(argv[0]);
		while (essai.hasNext()) {
			System.out.println("token = " + essai.next()); //$NON-NLS-1$
		}
	}
}

