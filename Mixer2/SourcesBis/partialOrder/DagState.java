package partialOrder;

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
import java.util.concurrent.CopyOnWriteArraySet;


import partialOrder.DagTransition;


	public class DagState<T> {
		ArrayList<DagTransition<T>> outEdges;
		
		
		public DagState(){
			this.outEdges = new ArrayList<DagTransition<T>>();
		}
		public void addTransition(DagTransition<T> dt){
			this.outEdges.add(dt);
		}
		public void addTransition(T label, DagState<T> next){
			this.outEdges.add(new DagTransition(label, next));
		}
		public String toGraphViz(CopyOnWriteArraySet<DagState<T>> allReadySeen){
			
			StringBuffer res = new StringBuffer();
			if (!allReadySeen.contains(this)){
				
			    
				
				DagTransition<T> curTrans;
				Iterator<DagTransition<T>> outEdgeIter = this.outEdges.iterator();
				int i = 0;
				String nameIntNode;
				allReadySeen.add(this);
				while (outEdgeIter.hasNext()){
					
					curTrans = outEdgeIter.next();
					res.append("n"+Integer.toHexString(this.hashCode()));
					res.append(" -> ");
					nameIntNode = "n"+Integer.toHexString(this.hashCode())+"_"+i;
					res.append(nameIntNode);
					res.append(";\n");
					res.append(nameIntNode);
					res.append("[label = \"");
					res.append(curTrans.label.toString());
					res.append("\"];\n");
					res.append(nameIntNode);
					res.append(" -> n");
					res.append(Integer.toHexString(curTrans.next.hashCode()));
					res.append(";\n");
					i = i + 1;
					res.append(curTrans.next.toGraphViz(allReadySeen));
				}
			}
			return res.toString();
		}
}


