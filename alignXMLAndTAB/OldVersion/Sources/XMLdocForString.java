import javax.xml.stream.*;
import java.io.FileReader;
import javax.xml.stream.events.*;

// Une classe uniquement pour extraire le contenu textuel d'un document.
class XMLdocForString{
    XMLStreamReader xmlsr = null;
   
    
    XMLdocForString(String fileName){
	XMLInputFactory xmlif = XMLInputFactory.newInstance();
	xmlif.setProperty("javax.xml.stream.isCoalescing",Boolean.TRUE);
	xmlif.setProperty("javax.xml.stream.isReplacingEntityReferences", Boolean.TRUE);
	try{
	    xmlsr = xmlif.createXMLStreamReader(new FileReader(fileName));
	}
	catch (Exception e){};

    };

    public String getText(){
	StringBuilder builder = new StringBuilder();
	String res = "";
	int eventType;
	try{
	    while (xmlsr.hasNext()){
		eventType = xmlsr.next();
		if (eventType == XMLEvent.CHARACTERS){
		    //res = res + xmlsr.getText();
		    builder.append(xmlsr.getText());
		};
	    };
	}
	catch (Exception e){};
	return builder.toString();
    };
};
