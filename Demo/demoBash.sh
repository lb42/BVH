#!/bin/bash

#à refaire sous forme de build ant puis ajouter un scénario Oxygen....
#mettre aussi autre chose que les noms par défaut pour les fichiers de notes...

bn=$( basename $1 .xml )

saxonb-xslt -ext:on $bn.xml extraireEdition.xsl > ${bn}Edition.xml

saxonb-xslt -ext:on ${bn}Edition.xml extraireTexte.xsl > ${bn}.txt

java -mx300m -classpath stanford-postagger-3.9.2.jar edu.stanford.nlp.tagger.maxent.MaxentTagger -model english-left3words-distsim.tagger -textFile ${bn}.txt > ${bn}.pos

java StanfordToTab ${bn}.pos > ${bn}.tab

java -jar alignXMLAndTab.jar ${bn}Edition.xml ${bn}.tab > ${bn}.tab.align

java -jar faireComp.jar ${bn}.tab.align > ${bn}.comp

java -jar mix2.jar ${bn}Edition.xml ${bn}.comp > ${bn}Edition.ann.xml

saxonb-xslt -ext:on ${bn}Edition.ann.xml reIntegrerNotes.xsl >  ${bn}.ann.xml
