<?xml version="1.0"?>


<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tei="http://www.tei-c.org/ns/1.0" version="2.0" xmlns:atilf="http://www.atilf.fr" xmlns:xs="http://www.w3.org/2001/XMLSchema">

  <xsl:output method="text"/>
  
  <xsl:function name="atilf:basename" as="xs:string">
    <xsl:param name="fileName" as="xs:string"/>

    <xsl:choose>
      <xsl:when test="contains($fileName, '/')">
	<xsl:value-of select="atilf:basename(substring-after($fileName, '/'))"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:value-of select="$fileName"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>
  
  <xsl:template match="/">
    <xsl:apply-templates/>
    <!-- <xsl:message><xsl:value-of select="concat(substring-before(atilf:basename(document-uri(.)), '.'), '.comp')"/></xsl:message>-->
    <xsl:result-document method="text" href="{concat(substring-before(atilf:basename(document-uri(.)), '.'), '.comp')}">
      <xsl:for-each select="descendant::*[not(self::tei:p) and not(self::tei:gloss) and not(self::tei:TEI) and not(self::tei:teiCorpus)]">
	<xsl:variable name="txtBefore">
	  <xsl:value-of select="preceding::text()"/>
	</xsl:variable>
	<xsl:variable name="myText">
	  <xsl:value-of select="descendant::text()"/>
	</xsl:variable>
	<xsl:value-of select="string-length($txtBefore)"/>
	<xsl:text>&#x9;</xsl:text>
	<xsl:value-of select="string-length($txtBefore)+string-length($myText)"/>
	<xsl:text>&#x9;&lt;</xsl:text>
	<xsl:value-of select="local-name(.)"/>
	<xsl:text> xmlns="</xsl:text>
	<xsl:value-of select="namespace-uri(.)"/>
	<xsl:text>"</xsl:text>
	<xsl:if test="@*">
	  <xsl:text> </xsl:text>
	</xsl:if>
	<xsl:for-each select="@*">
	  <xsl:value-of select="name(.)"/>
	  <xsl:text>="</xsl:text>
	  <xsl:value-of select="."/>
	  <xsl:text>"</xsl:text>
	  <xsl:if test="not(position() = last())">
	    <xsl:text> </xsl:text>
	  </xsl:if>
	</xsl:for-each>
	<xsl:text>&gt;&#x0a;</xsl:text>
      </xsl:for-each>
    </xsl:result-document>
  </xsl:template>

  <xsl:template match="*">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="tei:p | tei:gloss | tei:TEI | tei:teiCorpus">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="@* | text() | comment() | processing-instruction()">
    <xsl:copy/>
  </xsl:template>
  
</xsl:stylesheet>
