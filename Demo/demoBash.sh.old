#!/bin/bash

#à refaire sous forme de build ant puis ajouter un scénario Oxygen....
#mettre aussi autre chose que les noms par défaut pour les fichiers de notes...

saxonb-xslt -ext:on ST-Infrastructure.xml extraireEdition.xsl > ST-InfrastructureEdition.xml

saxonb-xslt -ext:on ST-InfrastructureEdition.xml extraireTexte.xsl > ST-Infrastructure.txt

java -mx300m -classpath stanford-postagger-3.9.2.jar edu.stanford.nlp.tagger.maxent.MaxentTagger -model english-left3words-distsim.tagger -textFile ST-Infrastructure.txt > ST-Infrastructure.pos

java StanfordToTab ST-Infrastructure.pos > ST-Infrastructure.tab

java -jar alignXMLAndTab.jar ST-InfrastructureEdition.xml ST-Infrastructure.tab > ST-Infrastructure.tab.align

java -jar faireComp.jar ST-Infrastructure.tab.align > ST-Infrastructure.comp

java -jar mix2.jar ST-InfrastructureEdition.xml ST-Infrastructure.comp > ST-InfrastructureEdition.ann.xml

saxonb-xslt -ext:on ST-InfrastructureEdition.ann.xml reIntegrerNotes.xsl >  ST-Infrastructure.ann.xml
