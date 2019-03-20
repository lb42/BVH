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

public interface GenericNode {
	   public static int TEXT_NODE = 1;
	    public static int OPEN_ELEMENT = 2;
	    public static int INT_NODE = 3;
	    public static int COMMENT_NODE = 4;
	    public static int PI_NODE = 5;
	    public static int CLOSE_ELEM = 6;
	    public static int AUTO_CLOSE = 7;
	    public static int CDATA = 8;

	    public int getType();
	    public String getStringValue();
	    public boolean equals(GenericNode other);
}
