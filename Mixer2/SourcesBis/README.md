# XMLMixer

The entry point is into "parsing/Parser.java

To test, go to directory example

c1.xml and c2.xml are two versions of a text with different annotation.
debaliser.xsl produces a companion file from an xml file.
For instance, c2.comp is the produced when running debaliser.xsl on c2.xml

java -jar mix2.jar c1.xml c2.comp

produces a version that contains annotations from both files.
