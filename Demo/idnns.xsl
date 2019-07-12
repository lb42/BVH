<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tei="http://www.tei-c.org/ns/1.0" version="1.0" exclude-result-prefixes="tei">


  <xsl:template match="/">
    <xsl:processing-instruction
	name="xml-stylesheet">
      type="text/css" href="http://asmode.atilf.fr/frantextAnnote.css"
    </xsl:processing-instruction> 
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="*">
    <xsl:element name="{local-name()}" namespace="{namespace-uri()}">
      <xsl:apply-templates select="@* | node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="@* | text() | comment() | processing-instruction()">
    <xsl:copy/>
  </xsl:template>
  
</xsl:stylesheet>
