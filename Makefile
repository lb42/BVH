ECHO=
CURRENT=`pwd`
ROOT=/Users/Travail/Documents/workspace/Git/BVH
SAXON=/Applications/saxon/saxon9he.jar
CORPUS=$(ROOT)/testBed
NEWCORPUS=$(ROOT)/Corpus
CLEANCORPUS=$(ROOT)/finalCorpus
BVHRNG=$(ROOT)/out/bvh.rng
CLEANUP=$(ROOT)/cleanup.xsl
IDENTITY=$(ROOT)/identity.xsl
SCHEMA=http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng
OBE=(ROOT)/oddbyexample.xsl
CORPHDR=/Users/Travail/Documents/workspace/Git/BVH/corphdr.txt

include theFiles

check:
	cd $(CORPUS); for f in $(FILES) ; do \
		echo $$f; \
		java -jar /Users/Travail/Downloads/jing-20091111/bin/jing.jar  $(SCHEMA) \
		$$f ; done; cd $(CURRENT);
generate:
		java -Xmx2G -jar $(SAXON) -it:main -o:generated.odd \
	$(OBE) \
	corpus=$(CORPUS) ; \
	teitorelaxng generated.odd 
# generated.odd needs handediting to produce bvh.odd 

copy:
	echo Running identity transform ...
	 if [! -d $(NEWCORPUS)] ; then  mkdir $(NEWCORPUS) ; fi
	cd $(CORPUS); 	for f in $(FILES) ; do \
		echo $$f; \
		java -Xmx2G -jar $(SAXON) \
		$$f $(IDENTITY) >$(NEWCORPUS)/$$f; done; cd $(CURRENT);

checkSpec:
	echo Validate  against BVH schema
	cd $(NEWCORPUS); 	for f in $(FILES) ; do \
		echo $$f; \
		java -jar /Users/Travail/Downloads/jing-20091111/bin/jing.jar $(BVHRNG) \
		$$f ; done; cd $(CURRENT);

cleanup:
	echo Do cleanup ; 

	cd $(NEWCORPUS); 	for f in $(FILES) ; do \
		echo $$f; \
		java -Xmx2G -jar $(SAXON)  \
		$$f $(CLEANUP) > $(CLEANCORPUS)/$$f; done; cd $(CURRENT);


checkClean:
	echo Validate cleaned corpus  against BVH schema
	cd $(CLEANCORPUS); 	for f in $(FILES) ; do \
		echo $$f; \
		java -jar /Users/Travail/Downloads/jing-20091111/bin/jing.jar $(BVHRNG) \
		$$f ; done; cd $(CURRENT);
checkLinks:
	java -Xmx2G -jar $(SAXON) -xi $(CLEANCORPUS)/driver.xml checkSharps.xsl | grep ERROR | sort | uniq > failedLinks.txt

driver:
	cd $(CLEANCORPUS); cp $(CORPHDR) driver.xml;\
 		for f in $(FILES) ; do \
		echo "<include xmlns='http://www.w3.org/2001/XInclude' href='$$f'/>" >> driver.xml; \
	done; echo "</teiCorpus>" >> driver.xml; cd $(CURRENT);