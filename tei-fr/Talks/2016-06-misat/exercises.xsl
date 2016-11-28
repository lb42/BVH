<xsl:stylesheet
    version="2.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:ev="http://purl.org/rss/1.0/modules/event/"
  xmlns:atom="http://www.w3.org/2005/Atom" 
  xmlns:teix="http://www.tei-c.org/ns/Examples"
  xmlns:tei="http://www.tei-c.org/ns/1.0"
  exclude-result-prefixes="atom ev teix xsl"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="/usr/share/xml/tei/stylesheet/latex2/tei.xsl"/>
<xsl:param name="reencode">false</xsl:param>


<xsl:template match="teix:egXML[@rend='invisible']"/>

</xsl:stylesheet>


