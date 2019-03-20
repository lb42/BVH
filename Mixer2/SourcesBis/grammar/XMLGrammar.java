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
import java.util.ArrayList;
import java.util.Iterator;

import xmlNodes.Attribute;
import xmlNodes.CloseElementNode;
import xmlNodes.CommentNode;
import xmlNodes.OpenElementNode;
import xmlNodes.ProcessingInstructionNode;
import xmlNodes.TextNode;

/**
 * A class to represent the grammar of the XML language.
 * @author bertrand
 *
 * By the way, we add spécific methods related to xml nodes...
 */
public class XMLGrammar extends CFGrammar {

	@SuppressWarnings("unused")
	public XMLGrammar() {
		super(null, null);

		// ArrayList<ProductionRule> rules;

		// the grammar :
		// 1) X -> Y
		// 2) X -> Y X
		// 3) Y -> <a> X </a>
		// 4) Y -> <a></a>
		// 5) Y -> <a/>
		// 6) Y -> TXT
		// 7) Y -> PI
		// 8) Y -> COMMENT

		// We now use the actual XML Grammar ! (almost ... 
		//    because no Character Data sections and
		//    lexical analyze is not the one needed for "real" XML)
		//    but this does not matter really because we presuppose we work on 
		//    well formed documents.
		// that is : (in Stax Terms)

		// 1) Document -> Prolog Element LMisc
		// 2) Document -> Prolog Element
		// 3) Document -> Element LMisc
		// 4) Document -> Element
		// 5) LMisc -> Misc
		// 6) LMisc -> Misc LMisc
		// 7) Misc -> Comment
		// 8) Misc -> PI
		// 9) Misc -> S
		// 10) Element -> "Stag" Content "Etag"
		// 11) Element -> "Stag" "Etag"
		// 12) Content -> SContent
		// 13) Content -> Content SContent
		// 14) SContent -> "TxtNode"
		// 15) SContent -> Element
		// 16) SContent -> CDSect
		// 17) SContent -> "Comment"
		// 18) SContent -> "PI"
		// 19) CDsect -> "CDStart" CDATA "CDEnd"
		// 20) Prolog -> LMisc
		// 21) Element -> Stag(autoclose)

		// We thus have actions (when serializing) on (instances of) :
		// Document
		// Element
		// SContent

		NonTerminal document = new NonTerminal("Document"); //$NON-NLS-1$
		NonTerminal prolog = new NonTerminal("Prolog"); //$NON-NLS-1$
		NonTerminal lmisc = new NonTerminal("LMisc"); //$NON-NLS-1$
		NonTerminal element = new NonTerminal("Element"); //$NON-NLS-1$
		ArrayList<GramSymb> rightR1 = new ArrayList<GramSymb>();
		rightR1.add(prolog);
		rightR1.add(element);
		rightR1.add(lmisc);
		ProductionRule r1 = new ProductionRule(document, rightR1);
		ArrayList<GramSymb> rightR2 = new ArrayList<GramSymb>();
		rightR2.add(prolog);
		rightR2.add(element);
		ProductionRule r2 = new ProductionRule(document, rightR2);
		ArrayList<GramSymb> rightR3 = new ArrayList<GramSymb>();
		rightR3.add(element);
		rightR3.add(lmisc);
		ProductionRule r3 = new ProductionRule(document, rightR3);
		ArrayList<GramSymb> rightR4 = new ArrayList<GramSymb>();
		rightR4.add(element);
		ProductionRule r4 = new ProductionRule(document, rightR4);
		ArrayList<GramSymb> rightR5 = new ArrayList<GramSymb>();
		NonTerminal misc = new NonTerminal("Misc"); //$NON-NLS-1$
		rightR5.add(misc);
		ProductionRule r5 = new ProductionRule(lmisc, rightR5);
		ArrayList<GramSymb> rightR6 = new ArrayList<GramSymb>();
		rightR6.add(misc);
		rightR6.add(lmisc);
		ProductionRule r6 = new ProductionRule(lmisc, rightR6);
		ArrayList<GramSymb> rightR7 = new ArrayList<GramSymb>();
		CommentNode comNode = new CommentNode("?x"); //$NON-NLS-1$
		Terminal tComment = new Terminal(comNode);
		rightR7.add(tComment);
		ProductionRule r7 = new ProductionRule(misc, rightR7);
		ProcessingInstructionNode piNode = new ProcessingInstructionNode("?x"); //$NON-NLS-1$
		Terminal tPI = new Terminal(piNode);
		ArrayList<GramSymb> rightR8 = new ArrayList<GramSymb>();
		rightR8.add(tPI);
		ProductionRule r8 = new ProductionRule(misc, rightR8);
		TextNode tSNode = new TextNode(" "); //$NON-NLS-1$
		Terminal tSpace = new Terminal(tSNode);
		ArrayList<GramSymb> rightR9 = new ArrayList<GramSymb>();
		rightR9.add(tSpace);
		ProductionRule r9 = new ProductionRule(misc, rightR9);
		OpenElementNode oeNode = new OpenElementNode(
				"?x", "", "", new ArrayList<Attribute>(), false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		Terminal tOpenElem = new Terminal(oeNode);
		CloseElementNode ceNode = new CloseElementNode("?x", ""); //$NON-NLS-1$ //$NON-NLS-2$
		Terminal tCloseElem = new Terminal(ceNode);
		NonTerminal content = new NonTerminal("Content"); //$NON-NLS-1$
		ArrayList<GramSymb> rightR10 = new ArrayList<GramSymb>();
		rightR10.add(tOpenElem);
		rightR10.add(content);
		rightR10.add(tCloseElem);
		ProductionRule r10 = new ProductionRule(element, rightR10);
		ArrayList<GramSymb> rightR11 = new ArrayList<GramSymb>();
		rightR11.add(tOpenElem);
		rightR11.add(tCloseElem);
		ProductionRule r11 = new ProductionRule(element, rightR11);
		ArrayList<GramSymb> rightR12 = new ArrayList<GramSymb>();
		NonTerminal sContent = new NonTerminal("SContent"); //$NON-NLS-1$
		rightR12.add(sContent);
		ProductionRule r12 = new ProductionRule(content, rightR12);
		ArrayList<GramSymb> rightR13 = new ArrayList<GramSymb>();
		rightR13.add(content);
		rightR13.add(sContent);

		ProductionRule r13 = new ProductionRule(content, rightR13);
		ArrayList<GramSymb> rightR14 = new ArrayList<GramSymb>();
		TextNode txtNode = new TextNode("?x"); //$NON-NLS-1$
		Terminal tNode = new Terminal(txtNode);
		rightR14.add(tNode);
		ProductionRule r14 = new ProductionRule(sContent, rightR14);
		ArrayList<GramSymb> rightR15 = new ArrayList<GramSymb>();
		rightR15.add(element);
		ProductionRule r15 = new ProductionRule(sContent, rightR15);
		/*
		 * ArrayList<GramSymb> rightR16 = new ArrayList<GramSymb>(); CdataNode
		 * cData = new CdataNode("?x"); Terminal tCdata = new Terminal(cData);
		 * rightR16.add(tCData); ProductionRule r16 = new
		 * ProductionRule(sContent, rightR16);
		 */
		ArrayList<GramSymb> rightR17 = new ArrayList<GramSymb>();
		rightR17.add(tComment);
		ProductionRule r17 = new ProductionRule(sContent, rightR17);
		ArrayList<GramSymb> rightR18 = new ArrayList<GramSymb>();
		rightR18.add(tPI);
		ProductionRule r18 = new ProductionRule(sContent, rightR18);
		/* reste r19 ... */
		ArrayList<GramSymb> rightR20 = new ArrayList<GramSymb>();
		rightR20.add(lmisc);
		ProductionRule r20 = new ProductionRule(prolog, rightR20);

		OpenElementNode aOuvEAuto = new OpenElementNode(
				"?x", "", "", new ArrayList<Attribute>(), true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		Terminal aAutoFerm = new Terminal(aOuvEAuto);
		ArrayList<GramSymb> rightR21 = new ArrayList<GramSymb>();
		rightR21.add(aAutoFerm);
		ProductionRule r21 = new ProductionRule(element, rightR21);

		ArrayList<ProductionRule> r = new ArrayList<ProductionRule>();

		r.add(r1);
		r.add(r2);
		r.add(r3);
		r.add(r4);
		r.add(r5);
		r.add(r6);
		r.add(r7);
		r.add(r8);
		r.add(r9);
		r.add(r10);
		r.add(r11);
		r.add(r12);
		r.add(r13);
		r.add(r14);
		r.add(r15);
		// r.add(r16);
		r.add(r17);
		r.add(r18);
		// r.add(r19);
		r.add(r20);
		r.add(r21);

		// super(x, r);
		this.axiom = document;
		// rules = r;
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



