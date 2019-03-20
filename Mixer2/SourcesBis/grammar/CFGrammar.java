package grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import xmlNodes.Attribute;
import xmlNodes.CommentNode;
import xmlNodes.GenericNode;
import xmlNodes.OpenElementNode;
import xmlNodes.ProcessingInstructionNode;

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


	public class CFGrammar {
		NonTerminal axiom;
		// ArrayList<ProductionRule> rules;
		HashMap<String, ArrayList<ProductionRule>> rulesByNt;

		@SuppressWarnings("unused")
		public CFGrammar(NonTerminal a, ArrayList<ProductionRule> r) {
			this.axiom = a;
			// rules = r;
			this.rulesByNt = new HashMap<String, ArrayList<ProductionRule>>();
			if (r != null) {
				Iterator<ProductionRule> rit = r.iterator();
				ProductionRule curRule;
				while (rit.hasNext()) {
					curRule = rit.next();
					if (!this.rulesByNt.containsKey(curRule.getLeft().getName())) {
						this.rulesByNt.put(curRule.getLeft().getName(),
								new ArrayList<ProductionRule>());

					}
					this.rulesByNt.get(curRule.getLeft().getName()).add(curRule);
				}
			}
		}
		
		public ArrayList<ProductionRule> rulesForAxiom() {
			/*
			 * Iterator<ProductionRule> ri = rules.iterator();
			 * ArrayList<ProductionRule> res = new ArrayList<ProductionRule>();
			 * ProductionRule currentRule;
			 * 
			 * while (ri.hasNext()){ currentRule = ri.next(); if
			 * (currentRule.getLeft().equals(axiom)){ res.add(currentRule); }; };
			 * return res;
			 */
			return rulesForNt(this.axiom);
		}

		public NonTerminal getAxiom() {
			return this.axiom;
		}

		/*
		 * public ArrayList<ProductionRule> getRules(){ return rules; };
		 */
		@Override
		public String toString() {
			StringBuffer res = new StringBuffer("Axiome : " + this.axiom + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
			/*
			 * Iterator<ProductionRule> ri = rules.iterator(); while (ri.hasNext()){
			 * res.append(ri.next().toString()+"\n"); };
			 */

			Iterator<String> kIt = this.rulesByNt.keySet().iterator();
			ArrayList<ProductionRule> curList;
			Iterator<ProductionRule> rIt;
			while (kIt.hasNext()) {
				curList = this.rulesByNt.get(kIt.next());
				rIt = curList.iterator();
				while (rIt.hasNext()) {
					res.append(rIt.next().toString() + "\n"); //$NON-NLS-1$
				}
			}

			/*
			 * res.append("la hasmap :"); res.append(rulesByNt.toString());
			 */

			return res.toString();
		}

		public ArrayList<ProductionRule> rulesForNt(NonTerminal nt) {
			/*
			 * ArrayList<ProductionRule> res = new ArrayList<ProductionRule>();
			 * Iterator<ProductionRule> prodIter = rules.iterator(); ProductionRule
			 * curProd;
			 * 
			 * while (prodIter.hasNext()){ curProd = prodIter.next(); if
			 * (curProd.getLeft().equals(nt)){ res.add(curProd); }; }; return res;
			 */
			return this.rulesByNt.get(nt.getName());
		}

		// we remove the unusefull production rules, that is the production rules
		// that are nore refered to...
		// very inefficient way to do it !

		// from the rules that define the axiom,
		// add the rules that define

		/*
		 * public void cleanProds(){ ArrayList<NonTerminal> NTSeen = new
		 * ArrayList<NonTerminal>(); NTSeen.add(axiom); ArrayList<ProductionRule>
		 * curProds; NonTerminal curNt; Iterator<ProductionRule> curIt;
		 * ProductionRule curPR; ArrayList<NonTerminal> ntRight;
		 * Iterator<NonTerminal> ntsToAddIt; NonTerminal curNtToAdd; boolean found;
		 * int j;
		 * 
		 * int i = 0; while (i < NTSeen.size()){ // we get the productions that
		 * define NTSeen.get(i); curNt = NTSeen.get(i); i = i + 1; if (i % 1000 ==
		 * 0){ System.out.print("#"); }; curProds = this.rulesForNt(curNt); curIt =
		 * curProds.iterator(); while (curIt.hasNext()){ curPR = curIt.next();
		 * ntRight = curPR.ntsInRight(); // for each of these NTs ; if there are not
		 * allready into // NTSeen, we add them. ntsToAddIt = ntRight.iterator();
		 * while (ntsToAddIt.hasNext()){ curNtToAdd = ntsToAddIt.next(); // do we
		 * add it ? found = false; j = 0; while (!found && j < NTSeen.size()){ found
		 * = curNtToAdd.equals(NTSeen.get(j)); j = j+1; }; if (!found){
		 * NTSeen.add(curNtToAdd); }; }; }; }; // we now have the usefull Non
		 * Terminals, ArrayList<ProductionRule> newRules = new
		 * ArrayList<ProductionRule>(); Iterator<NonTerminal> usefullNTIter =
		 * NTSeen.iterator(); ArrayList<ProductionRule> curRes;
		 * Iterator<ProductionRule> curResIt;
		 * 
		 * while (usefullNTIter.hasNext()){ curRes =
		 * this.rulesForNt(usefullNTIter.next()); curResIt = curRes.iterator();
		 * while (curResIt.hasNext()){ newRules.add(curResIt.next()); }; }; rules =
		 * newRules; };
		 */

		/*private OpenElementNode getFirstOpenElement(NonTerminal nt) {
			ProductionRule prod = rulesForNt(nt).get(0);
			Iterator<GramSymb> i = prod.getRight().iterator();
			GramSymb curSymb;
			boolean found = false;
			OpenElementNode res = null;
			while (!found && i.hasNext()) {
				curSymb = i.next();
				if (curSymb.isTerminal()) {
					found = ((Terminal) curSymb).getVal().getType() == GenericNode.OPEN_ELEMENT;
					if (found) {
						Terminal curSymbAsT = (Terminal) curSymb;
						res = (OpenElementNode) curSymbAsT.getVal();
					}
					;
				} else {
					NonTerminal curSymbAsNt = (NonTerminal) curSymb;
					res = getFirstOpenElement(curSymbAsNt);
					found = (res != null);
				}
			}
			return res;
		};*/

		
		public String extractAResultThroughDom() {
			String res = ""; //$NON-NLS-1$
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				factory.setNamespaceAware(true);
				DocumentBuilder builder = factory.newDocumentBuilder();
				// DOMImplementation impl = builder.getDOMImplementation();

				// we look for the first OpenElementNode we get from the axiom.
				// we then add son for each symbol that occurs between the
				// openElementNode
				// and the closing bracket.

				// we need the root element in order to go further !
				// Document doc = impl.createDocument();
				// OpenElementNode openRoot = getFirstOpenElement(axiom);
				// System.out.println("racine = "+openRoot);
				// No basename for the document we create
				Document doc = builder.newDocument();

				Element e = doc.createElement("toto"); //$NON-NLS-1$
				// doc.appendChild(e);

				// we push a junk node in the stack without linking it to the
				// document...
				@SuppressWarnings("unused")
				Stack<Node> st = new Stack<Node>();
				st.push(e);
				Node rn = makeSubTree(doc, this.axiom, st);

				doc.appendChild(rn);

				TransformerFactory transFactory = TransformerFactory.newInstance();
				Transformer idTransform = transFactory.newTransformer();
				idTransform.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
				Source input = new DOMSource(doc);
				Result output = new StreamResult(System.out);
				idTransform.transform(input, output);
				// res = doc.toString();
			} catch (Exception e) {
				System.err.println(e);
			}

			return res;

		}

		@SuppressWarnings("incomplete-switch")
		private Node makeSubTree(Document doc, GramSymb gsymb, Stack<Node> s) {

			//System.out.println("makeSubTree("+gsymb+") : "+doc);
			
			Node res = null;
			if (gsymb.isTerminal()) {
				// on teste le type.... et on fabrique un node en conséquence
				Terminal t = (Terminal) gsymb;
				GenericNode gn = t.getVal();
				Element p = (Element) s.peek();
				switch (gn.getType()) {
				case GenericNode.TEXT_NODE:
					res = doc.createTextNode(gn.toString());
					p.appendChild(res);
					break;
				case GenericNode.OPEN_ELEMENT:
					OpenElementNode oe = (OpenElementNode) gn;
					// System.out.println("doc.createElementNS("+oe.getNsUri()+", "+
					// oe.getQName()+")");
					res = doc.createElementNS(oe.getNsUri(), oe.getQName());
					// System.out.println("nsURi = "+oe.getNsUri()+" qname = "+oe.getQName());
					// res = doc.createElement(oe.getLocalName());
					p.appendChild(res);
					// we also should add the attribute nodes....
					Iterator<Attribute> i = oe.getAttributes().iterator();
					NamedNodeMap atts = res.getAttributes();
					while (i.hasNext()) {
						Attribute at = i.next();
						//C'est ici qu'on oublie le namespace !
						//Attr a = doc.createAttribute(at.getLocalName());
						System.err.println("ns uri="+at.getNameSpaceURI()+" qname="+at.getQName());
						Attr a = doc.createAttributeNS(at.getNameSpaceURI(), at.getQName());
						a.setValue(at.getValue());
						atts.setNamedItem(a);
						// res.appendChild(a);
					}
					s.push(res);
					break;
				case GenericNode.COMMENT_NODE:
					CommentNode cn = (CommentNode) gn;
					res = doc.createComment(cn.getStringValue());
					p.appendChild(res);
					break;
				case GenericNode.PI_NODE:
					ProcessingInstructionNode pi = (ProcessingInstructionNode) gn;
					res = doc.createProcessingInstruction(pi.getTarget(),
							pi.getData());
					p.appendChild(res);
					break;
				case GenericNode.CLOSE_ELEM:
					res = s.pop();
					break;
				case GenericNode.AUTO_CLOSE:
					OpenElementNode oe2 = (OpenElementNode) gn;
					// res = doc.createElementNS(oe2.getNsUri(), oe2.getQName());
					res = doc.createElement(oe2.getLocalName());
					p.appendChild(res);
					break;
				}
				return res;
			}
			
			// not a Terminal :
			NonTerminal gsymbNT = (NonTerminal) gsymb;
			ProductionRule prod = rulesForNt(gsymbNT).get(0);
			Iterator<GramSymb> i = prod.getRight().iterator();

			while (i.hasNext()) {
				res = makeSubTree(doc, i.next(), s);
			}
			return res;
		}

		// get a result...
		// first call should be on the axiom...
		public String extractAResult(NonTerminal nt) {
			// get a rewriting for nt.

			// System.out.println("Extract result pour "+nt);

			ProductionRule prod = rulesForNt(nt).get(0);
			Iterator<GramSymb> i = prod.getRight().iterator();
			StringBuffer res = new StringBuffer(""); //$NON-NLS-1$
			GramSymb curSymb;
			while (i.hasNext()) {
				curSymb = i.next();
				if (curSymb.isTerminal()) {
					res.append(curSymb.toString());
				} else {
					res.append(extractAResult((NonTerminal) curSymb));
				}
			}
			return res.toString();
		}
}
