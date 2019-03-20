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

import java.io.*;
import java.util.ArrayList;
//import org.xml.sax.XMLReader;
//import org.xml.sax.helpers.XMLReaderFactory;
//import org.xml.sax.InputSource;


import javax.xml.stream.*;

import inputDag.InputDag;
import inputDag.InputDagState;
import xmlNodes.Attribute;
import xmlNodes.CloseElementNode;
import xmlNodes.CommentNode;
import xmlNodes.GenericNode;
import xmlNodes.OpenElementNode;
import xmlNodes.ProcessingInstructionNode;
import xmlNodes.TextNode;

/** 
 * a class dedicated to reading together the xml or text file and the companion file.
 * We build a dag that represents them both.
 * @author bertrand
 *
 */

// TODO : rewrite this using XmlNextChar.
public class XmlAndCompReader extends InputsReader {
	InputDag theText;
	InputDagState currentPosition;
	Compagnon theComp;
	int indexInComp; // the next line in the comp file.
	int currentTextPos;
	BufferedReader xmlBr;
	// char currentChar; // the next char in the XML file.
	int currentEventType;

	boolean endXMLFile;

	FileReader fichier;
	XMLInputFactory xmlif;
	XMLStreamReader xmlsr;

	boolean inTextNode;
	String remainingTextNodeContents;
	int firstIndexComp;

	public XmlAndCompReader(String xmlFileName, String companionFileName)
			throws Exception {

		// we need an XmlReader...

		this.fichier = new FileReader(xmlFileName);
		// we now need a handler to the sax + lexical events

		// we need all events that occur at the same position into the pcdata

		this.theComp = new Compagnon(companionFileName);
		this.indexInComp = 0;
		this.currentTextPos = 0;

		this.theText = new InputDag(0);

		this.xmlif = XMLInputFactory.newInstance();
		this.xmlsr = this.xmlif.createXMLStreamReader(this.fichier);

		this.inTextNode = false;
		this.remainingTextNodeContents = new String(""); //$NON-NLS-1$
		// we need a token in advance in the xml file (but we know that
		// currentTextPos is 0
		// and a token in advance in the comp file.(more preciselly, the first
		// index in the
		// comp file.
		if (this.indexInComp < this.theComp.getTable().size()) {
			this.firstIndexComp = this.theComp.getTable().get(this.indexInComp)
					.getIdTexte();
		} else {
			this.firstIndexComp = -1;
		}
	}

	@Override
	public InputDag getDag() {
		return this.theText;
	}

	// idem la fonction suivante, mais on ne dépasse pas (on splitte)
	// max.
	// currentTextpos is the position into the PCDATA. (counting from 0)

	// we now use a streaming parser.

	@SuppressWarnings("incomplete-switch")
	public GenericNode nextTokXMLWithMax(int max) {
		GenericNode res = null;
		// int eventType;

		// System.out.println("nextTokXMLWithMax("+max+")");
		if (this.remainingTextNodeContents.compareTo("") != 0) { //$NON-NLS-1$
			if (max == -1
					| (this.currentTextPos
							+ this.remainingTextNodeContents.length() <= max)) {
				this.currentTextPos = this.currentTextPos
						+ this.remainingTextNodeContents.length();
				res = new TextNode(this.remainingTextNodeContents);
				this.remainingTextNodeContents = new String(""); //$NON-NLS-1$
				// res = new TextNode(xmlsr.getText());

				this.inTextNode = false;
			} else {
				if (max - this.currentTextPos != 0) {
					res = new TextNode(
							this.remainingTextNodeContents.substring(0, max
									- this.currentTextPos));
				} else {
					res = null;
				}

				this.remainingTextNodeContents = this.remainingTextNodeContents
						.substring(max - this.currentTextPos,
								this.remainingTextNodeContents.length());
				this.currentTextPos = max;
				this.inTextNode = true;

			}
		} else {
			try {
				if (this.xmlsr.hasNext()) {
					this.currentEventType = this.xmlsr.next();
					switch (this.currentEventType) {
					case javax.xml.stream.XMLStreamConstants.START_ELEMENT:

						// we have to compute the attributes...
						@SuppressWarnings("unused")
						ArrayList<Attribute> Att = new ArrayList<Attribute>();
						for (int i = 0; i < this.xmlsr.getAttributeCount(); i++) {
							Att.add(new Attribute(this.xmlsr
									.getAttributeLocalName(i), this.xmlsr.getAttributePrefix(i),
									this.xmlsr.getAttributeNamespace(i),this.xmlsr
									.getAttributeValue(i)));
						}
						@SuppressWarnings("unused")
						ArrayList<Attribute> nsN = new ArrayList<Attribute>();
						Attribute nsAsAtt;
						for (int i = 0; i < this.xmlsr.getNamespaceCount(); i++) {
							if (this.xmlsr.getNamespacePrefix(i) == null) {
								nsAsAtt = new Attribute(
										"xmlns", "", this.xmlsr.getNamespaceURI(i)); //$NON-NLS-1$ //$NON-NLS-2$
							} else {
								nsAsAtt = new Attribute(
										this.xmlsr.getNamespacePrefix(i),
										"xmlns", "", this.xmlsr.getNamespaceURI(i)); //$NON-NLS-1$ //$NON-NLS-2$
							}

							nsN.add(nsAsAtt);
						}

						res = new OpenElementNode(this.xmlsr.getLocalName(),
								this.xmlsr.getNamespaceURI(), "", //$NON-NLS-1$
								Att, nsN, false);
						break;
					case javax.xml.stream.XMLStreamConstants.END_ELEMENT:

						res = new CloseElementNode(this.xmlsr.getLocalName(),
								""); //$NON-NLS-1$
						break;
					case javax.xml.stream.XMLStreamConstants.CHARACTERS:
						// System.out.println("currentTextPos = "+currentTextPos);
						// System.out.println("max = "+max);
						// we have to test with max...
						this.remainingTextNodeContents = this.xmlsr.getText();
						if (max == -1
								|| this.currentTextPos
										+ this.remainingTextNodeContents
												.length() <= max) {
							// System.out.println("then");
							this.currentTextPos = this.currentTextPos
									+ this.remainingTextNodeContents.length();
							this.remainingTextNodeContents = new String(""); //$NON-NLS-1$
							res = new TextNode(this.xmlsr.getText());
						} else {
							// System.out.println("else");
							// System.out.println("remainingTextNodeContents = "+remainingTextNodeContents);
							if (max == -1) {
								res = new TextNode(
										this.remainingTextNodeContents);
								this.remainingTextNodeContents = new String(""); //$NON-NLS-1$
								this.currentTextPos = this.currentTextPos
										+ this.remainingTextNodeContents
												.length();
								this.inTextNode = false;
							} else {
								if (max - this.currentTextPos == 0) {
									res = null;
								} else {
									res = new TextNode(
											this.remainingTextNodeContents
													.substring(
															0,
															max
																	- this.currentTextPos));

									this.remainingTextNodeContents = this.remainingTextNodeContents
											.substring(
													max - this.currentTextPos,
													this.remainingTextNodeContents
															.length());
									this.currentTextPos = max;
									this.inTextNode = true;
								}
							}
						}
						break;
					/*
					 * case javax.xml.stream.XMLStreamConstants.SPACE : res =
					 * new TextNode(xmlsr.getText()); break;
					 */
					case javax.xml.stream.XMLStreamConstants.COMMENT:
						res = new CommentNode(this.xmlsr.getText());
						break;
					case javax.xml.stream.XMLStreamConstants.PROCESSING_INSTRUCTION:
						res = new ProcessingInstructionNode(
								this.xmlsr.getPITarget() + " " + //$NON-NLS-1$
										this.xmlsr.getPIData());
						break;
					case javax.xml.stream.XMLStreamConstants.END_DOCUMENT:
						break;
					default:
						System.out
								.println("Evénement non traité : " + this.currentEventType); //$NON-NLS-1$
						switch (this.currentEventType) {
						case javax.xml.stream.XMLStreamConstants.ATTRIBUTE:
							System.out.println("ATTRIBUTE"); //$NON-NLS-1$
							break;
						case javax.xml.stream.XMLStreamConstants.CDATA:
							System.out.println("CDATA"); //$NON-NLS-1$
							break;
						case javax.xml.stream.XMLStreamConstants.CHARACTERS:
							System.out.println("CHARACTERS"); //$NON-NLS-1$
							break;
						case javax.xml.stream.XMLStreamConstants.COMMENT:
							System.out.println("COMMENT"); //$NON-NLS-1$
							break;
						case javax.xml.stream.XMLStreamConstants.DTD:
							System.out.println("DTD"); //$NON-NLS-1$
							break;
						case javax.xml.stream.XMLStreamConstants.END_DOCUMENT:
							System.out.println("END_DOCUMENT"); //$NON-NLS-1$
							break;
						case javax.xml.stream.XMLStreamConstants.END_ELEMENT:
							System.out.println("END_ELEMENT"); //$NON-NLS-1$
							break;
						case javax.xml.stream.XMLStreamConstants.ENTITY_DECLARATION:
							System.out.println("ENTITY_DECLARATION"); //$NON-NLS-1$
							break;
						case javax.xml.stream.XMLStreamConstants.ENTITY_REFERENCE:
							System.out.println("ENTITY_REFERENCE"); //$NON-NLS-1$
							break;
						case javax.xml.stream.XMLStreamConstants.NAMESPACE:
							System.out.println("NAMESPACE"); //$NON-NLS-1$
							break;
						case javax.xml.stream.XMLStreamConstants.NOTATION_DECLARATION:
							System.out.println("NOTATION_DECLARATION"); //$NON-NLS-1$
							break;
						case javax.xml.stream.XMLStreamConstants.PROCESSING_INSTRUCTION:
							System.out.println("PROCESSING_INSTRUCTION"); //$NON-NLS-1$
							break;
						case javax.xml.stream.XMLStreamConstants.SPACE:
							System.out.println("SPACE"); //$NON-NLS-1$
							break;
						case javax.xml.stream.XMLStreamConstants.START_DOCUMENT:
							System.out.println("START_DOCUMENT"); //$NON-NLS-1$
							break;
						case javax.xml.stream.XMLStreamConstants.START_ELEMENT:
							System.out.println("START_ELEMENT"); //$NON-NLS-1$
							break;

						}

					}

				} else {
					res = null;
				}
			} catch (XMLStreamException e) {
				return null;
			}
		}
		// System.out.println("On renvoie (nextTokXMLWithMax) "+res);
		return res;
	}

	/*
	 * public GenericNode nextTokXMLWithMax(int max){ int state = 0;
	 * StringBuffer res = new StringBuffer(); int r; GenericNode resNode = new
	 * TextNode(""); boolean fin = false;
	 * 
	 * if (endXMLFile){ return null; };
	 * //System.out.println("nextTokXMLWithMax ; max = "+max); while (!fin){
	 * res.append(currentChar);
	 * //System.out.println("state = "+state+"  currentChar : |"
	 * +currentChar+"| texPos = "+currentTextPos); switch (state){ case 0 : if
	 * (currentChar == '<'){state = 1;} else{ if (currentTextPos == max){ return
	 * null; } else{ if (currentTextPos == max -1){ fin = true; currentTextPos =
	 * currentTextPos + 1; resNode = new TextNode(res.toString()); } else{ state
	 * = 15; }; } } break; case 1 : switch (currentChar){ case '?' : state = 2;
	 * break; case '/' : state = 5; break; case '!' : state = 7; break; default
	 * : state = 13; break;}; break; case 2 : if (currentChar == '?'){state =
	 * 3;}; break; case 3 : if (currentChar == '>'){ //state = 4; fin = true;
	 * resNode = new ProcessingInstructionNode(res.toString().substring(2,
	 * res.toString().length()-2)) ; // PI } else{ state = 2; };break; case 4 :
	 * // supprimé ! (non atteint) //fin = true; //resNode = new
	 * ProcessingInstructionNode(res.toString()) ; // PI case 5 : if
	 * (currentChar == '>'){ //state = 6; fin = true; resNode = new
	 * CloseElementNode(res.substring(2, res.length()-1), ""); // Closing }
	 * else{ state = 5; }; break; case 6 : // supprimé ! (non atteint) //fin =
	 * true; //resNode = new CloseElementNode(res.substring(2, res.length()-2),
	 * ""); // Closing case 7 : if (currentChar == '-'){ state = 8; } else{
	 * state = 13; }; break; case 8 : if (currentChar == '-'){ state = 9; }
	 * else{ state = 13; }; break; case 9 : if (currentChar == '-'){ state = 10;
	 * }; break; case 10 : if (currentChar == '-'){ state = 11; } else{ state =
	 * 9; };break; case 11 : if (currentChar == '>'){ fin = true; resNode = new
	 * CommentNode(res.toString().substring(4, res.toString().length()-3)); //
	 * comment } else{ state = 9; }; break; case 12: // supprimé ! (non atteint)
	 * //fin = true; //resNode = new CommentNode(res.toString()); // comment
	 * case 13 : if (currentChar == '>'){ fin = true; resNode = new
	 * OpenElementNode(res.toString()); // Open elem state = 14; } break; case
	 * 14 : // supprimé ! (non atteint) //fin = true; //resNode = new
	 * OpenElementNode(res.toString()); // Open elem case 15 : // #############
	 * // we should at least take into account the characters &lt; &gt; &amp; +
	 * numbers. // and we do not !!! beware !!!!!! // ############### if
	 * (currentChar == '<'){ fin = true;
	 * 
	 * return new TextNode(res.substring(0, res.length()-1)); // textNode }
	 * else{
	 * //System.out.println("Entrée dans 15, currentTextPos = "+currentTextPos);
	 * 
	 * if (currentTextPos==max-1){ fin = true; resNode = new
	 * TextNode(res.toString()); } };
	 * 
	 * ; break; } try{ if (state != 15 | currentChar != '<'){ r = xmlBr.read();
	 * if (r == -1){ endXMLFile = true; return new TextNode(res.toString()); }
	 * else{ if (state == 15 & currentChar != '<'){ currentTextPos =
	 * currentTextPos + 1; }; currentChar = (char)r; } } } catch (IOException
	 * e){endXMLFile = true;}; }
	 * //System.out.println("On sort avec currentChar = "+currentChar); return
	 * resNode; }
	 */

	// each call to read extends theText (if it return true)
	// when it returns false, end of file was reached.

	/*
	 * public boolean read(){ // general principle :
	 * 
	 * // if currentTextPos < firstIndexComp, // we read a token in the xml file
	 * and add it to the Dag. // if this token happens to be a textNode, we have
	 * to stop when // currentTextpos == firstIndexComp.
	 * 
	 * // if currentTextPos == firstIndexComp then // if we are in a textNode
	 * into the XMLFile, // we do not read there (it would increase the text
	 * position) // we only read the companion until // firstIndexComp >
	 * currentTextPos // if not, we read in the xmlFile until we are at some
	 * text Node (and stop there) // we also read the companion, until
	 * firstIndexComp > currentTextPos
	 * 
	 * 
	 * 
	 * 
	 * 
	 * int firstIndexComp;
	 * 
	 * 
	 * //System.err.println("entrée dans read");
	 * 
	 * if (indexInComp < theComp.getTable().size()){ firstIndexComp =
	 * theComp.getTable().get(indexInComp).getIdTexte(); } else{ firstIndexComp
	 * = -1; };
	 * 
	 * ArrayList<GenericNode> nodesFromXml = new ArrayList<GenericNode>();
	 * ArrayList<ArrayList<GenericNode>> listsForDag = new
	 * ArrayList<ArrayList<GenericNode>>(); ArrayList<GenericNode> curCompLine;
	 * GenericNode currTxtN;
	 * 
	 * if (firstIndexComp == -1 | currentTextPos < firstIndexComp){
	 * //System.err.println("on lit dans le xml"); if (currentChar == '<'){
	 * //System.err.println("du balisage"); while (currentChar == '<'){ currTxtN
	 * = nextTokXMLWithMax(firstIndexComp);
	 * //System.err.println("balisage : "+currTxtN); if (currTxtN != null){
	 * nodesFromXml.add(currTxtN); } }; listsForDag.add(nodesFromXml); } else{
	 * //System.err.println("du pcdata"); currTxtN =
	 * nextTokXMLWithMax(firstIndexComp); if (currTxtN != null){
	 * //System.err.println("|"+currTxtN+"|"); nodesFromXml.add(currTxtN);
	 * listsForDag.add(nodesFromXml); } else{ return false; } } } else{ // we
	 * want everything that happens at the currentTextPos, both in the xml file
	 * and in the companion file. //xml file :
	 * 
	 * //System.err.println("on lit dans les deux ; currentTextPos = "+
	 * currentTextPos); while (currentChar == '<'){ currTxtN =
	 * nextTokXMLWithMax(firstIndexComp);
	 * //System.err.println("les 2 : dans le xml :"+currTxtN); if (currTxtN !=
	 * null){ nodesFromXml.add(currTxtN); } }; if (nodesFromXml.size() > 0){
	 * listsForDag.add(nodesFromXml); };
	 * //System.err.println("Dans les 2, après xml, currentTextPos = "
	 * +currentTextPos+" firstIndexComp = "+firstIndexComp); //now, we want
	 * everything from the companion file at this index. while (firstIndexComp
	 * == currentTextPos & indexInComp < theComp.getTable().size()){ // on
	 * ajoute la ligne correspondante du compagnon curCompLine =
	 * theComp.getTable().get(indexInComp).getTheNodes();
	 * 
	 * //System.err.println("dans les 2, fichier compagnon : "+curCompLine);
	 * 
	 * listsForDag.add(curCompLine); indexInComp = indexInComp+1; if
	 * (indexInComp < theComp.getTable().size()){ firstIndexComp =
	 * theComp.getTable().get(indexInComp).getIdTexte(); }; } }; if
	 * (listsForDag.size() > 0){
	 * //System.err.println("on ajoute sur l'état :"+theText
	 * .getLast().getId()+" la liste :"+listsForDag+
	 * " à la position "+currentTextPos);
	 * 
	 * theText.prolongerAutomate(listsForDag, currentTextPos);
	 * 
	 * return true; } else{ return false; } };
	 * 
	 * // Pb : on ne lit pas au delà de la dernière textPosition du fichier
	 * compagnon !
	 */

	@Override
	// principe :
	// on prend un token d'avance dans chaque fichier.
	//
	@SuppressWarnings("unused")
	public boolean read() {
		int txtPos;
		ArrayList<GenericNode> nodesFromXml = new ArrayList<GenericNode>();
		ArrayList<ArrayList<GenericNode>> listsForDag = new ArrayList<ArrayList<GenericNode>>();
		ArrayList<GenericNode> curCompLine;
		GenericNode xmlFileCurrentNode = new TextNode("bidon"); //$NON-NLS-1$
		boolean res;

		//System.err.println("read() ; firstIndexComp = "+firstIndexComp);

		res = false;
		txtPos = this.currentTextPos;
		/* 1) We read in the xml file at this txt position */
		while (txtPos == this.currentTextPos && xmlFileCurrentNode != null) {
			// readXmlwithmax
			// build the tagging of xml file. (that is build the list)
			// System.out.println("reading xml at "+txtPos);
			xmlFileCurrentNode = nextTokXMLWithMax(this.firstIndexComp);
			/*
			 * if (xmlFileCurrentNode == null){ System.out.println("NULL !"); };
			 */
			if (xmlFileCurrentNode != null
					&& xmlFileCurrentNode.getType() != GenericNode.TEXT_NODE) {
				nodesFromXml.add(xmlFileCurrentNode);
			}
			
		}
		if (nodesFromXml.size() > 0) {
			listsForDag.add(nodesFromXml);
		}
		// 2) we may now have some txt read into xmlFileCurrentNode.

		// 3) Is there some comp at txtPos ?
		// if so, build the lists from the comp file.
		while (this.firstIndexComp == txtPos
				& this.indexInComp < this.theComp.getTable().size()) {
			// on ajoute la ligne correspondante du compagnon
			curCompLine = this.theComp.getTable().get(this.indexInComp)
					.getTheNodes();

			// System.err.println("dans les 2, fichier compagnon : "+curCompLine);

			listsForDag.add(curCompLine);
			this.indexInComp = this.indexInComp + 1;
			if (this.indexInComp < this.theComp.getTable().size()) {
				this.firstIndexComp = this.theComp.getTable()
						.get(this.indexInComp).getIdTexte();
			} else {
				this.firstIndexComp = -1;
			}
		}

		// 4) extend the dag.
		if (listsForDag.size() > 0) {
			// System.err.println("on ajoute sur l'état :"+theText.getLast().getId()+" la liste :"+listsForDag+
			// " à la position "+currentTextPos);

			// theText.prolongerAutomate(listsForDag, currentTextPos);
			this.theText.prolongerAutomate(listsForDag, txtPos);
			res = true;
		}

		// 5) if we had some txt at step 2, extend the dag with it.
		if (xmlFileCurrentNode != null
				&& xmlFileCurrentNode.getType() == GenericNode.TEXT_NODE) {
			nodesFromXml = new ArrayList<GenericNode>();
			nodesFromXml.add(xmlFileCurrentNode);
			listsForDag = new ArrayList<ArrayList<GenericNode>>();
			listsForDag.add(nodesFromXml);
			this.theText.prolongerAutomate(listsForDag, this.currentTextPos);
			res = true;
		}

		return res;

	}

	/*public static void main(String argv[]) throws Exception {
		XmlAndCompReader essai = new XmlAndCompReader("test.xml", "comp4"); //$NON-NLS-1$ //$NON-NLS-2$
		// GenericNode res;

		while (essai.read()) {
			// we read the whole file.
		}
		System.out.println(essai.theText.toString());
		// we now should verify what happens at each call to read()
	}*/
}

