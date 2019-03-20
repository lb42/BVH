package parsing;
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
import parsing.ParsingItem;
import inputDag.InputDagTransition;

// a scanning failure is a couple :
//     failed item
//     failed dag State
public class ScanningFailure {
	ParsingItem it;
	InputDagTransition st;
	
	ScanningFailure(ParsingItem pi, InputDagTransition dt){
		this.it = pi;
		this.st = dt;
	}
	public ParsingItem getItem(){
		return it;
	}
	public InputDagTransition getTrans(){
		return st;
	}
}
