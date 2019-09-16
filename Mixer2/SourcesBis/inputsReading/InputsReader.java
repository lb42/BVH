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




//the class that is responsible for reading the inputs, namely :
//
//XmlAndCompReader : in case we have an Xml file and a copanion as inputs
//TextAndCompReader : in case we have a text file and a companion file
//DiffReader : in case we have two xml files that we merge
//HorseReader : in we have a file in Horse format that we want tu unHorse.

public abstract class InputsReader{

 abstract public boolean read();
 abstract public InputDag getDag();
}
