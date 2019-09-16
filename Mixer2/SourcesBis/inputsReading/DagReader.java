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

import inputDag.InputDag;

import java.io.FileReader;

import xmlNodes.*;

// We read a dag stored in a file.
// each line in the file reads :
// nodeName \t arc \t nodeName
// with arc = <El atts> or </El> or <?PI ... ?> or <!-- Comment --> or "some text" Pb : si il y a des retours charriots dans le texte ? on les encode &#x0a; ?
// sinon, on n'est pas loin d'un compReader isn't it ?

public class DagReader extends InputsReader{
	
	FileReader fichier;
	
	public DagReader(String dagFileName) throws Exception{
		// we will have to build xmlNodes
		this.fichier = new FileReader(dagFileName);
	}

	@Override
	public boolean read() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public InputDag getDag() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
