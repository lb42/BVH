<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tei="http://www.tei-c.org/ns/1.0" version="2.0" xmlns:atilf="http://www.atilf.fr" exclude-result-prefixes="atilf tei" xmlns="http://www.tei-c.org/ns/1.0">

  <xsl:param name="annotation">nomParDefautFichierAnnotation.xml</xsl:param>
  <xsl:param name="annotationAnnotees">nomParDefautNotesAnnotees.xml</xsl:param>

  <!-- Ce script est le pendant de extraireEdition, il reintègre les notes contenues dans le fichier d'annotations dans le fichier donné en paramètre -->



  <xsl:template match="/">
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

  <xsl:template match="*[@atilf:id | @id]">
    <xsl:variable name="id" select="@atilf:id | @id"/>

   
   
    <xsl:element name="{local-name()}" namespace="{namespace-uri()}">
      <xsl:for-each select="@*[not(namespace-uri()='http://www.atilf.fr')]">
	<xsl:copy/>
      </xsl:for-each>

      <xsl:choose>
	<!-- tjs le bug sur les namespace d'attributs dans XMLMixer.... -->
	<xsl:when test="document($annotationAnnotees)//tei:p[@atilf:id=$id]">
	  <xsl:apply-templates select="document($annotationAnnotees)//tei:p[@atilf:id=$id]/node()" mode="renameXmlIds"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:apply-templates select="document($annotation)//*[@atilf:id=$id]/node()"/>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:element>
  </xsl:template>

  <xsl:template match="tei:g[not(tei:w)]">
    <xsl:choose>
      <xsl:when test="@ref='#ET'">
	<xsl:text>&amp;</xsl:text>
      </xsl:when>
      <xsl:when test="@ref='#LESS_THAN'">
	<xsl:text>&lt;</xsl:text>
       </xsl:when>
       <xsl:when test="@ref='#MORE_THAN'">
	<xsl:text>&gt;</xsl:text>
      </xsl:when>
      <xsl:otherwise>
	<xsl:copy-of select="."/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Le g contient donc un w ! -->
  <xsl:template match="tei:g">
    <w>
      <xsl:for-each select="tei:w/@*">
	<xsl:copy/>
      </xsl:for-each>
      <xsl:choose>
	<xsl:when test="@ref='#ET'">
	  <xsl:text>&amp;</xsl:text>
	</xsl:when>
	<xsl:when test="@ref='#LESS_THAN'">
	  <xsl:text>&lt;</xsl:text>
	</xsl:when>
	<xsl:when test="@ref='#MORE_THAN'">
	  <xsl:text>&gt;</xsl:text>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:copy-of select="."/>
	</xsl:otherwise>
      </xsl:choose>
    </w>
  </xsl:template>
  
  <xsl:template match="tei:w[tei:g]">
    <xsl:copy>
      <xsl:for-each select="@*">
	<xsl:copy/>
      </xsl:for-each>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>


  <xsl:template match="*" mode="renameXmlIds">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="renameXmlIds"/>
    </xsl:copy>
  </xsl:template>


  <xsl:template match="@*" mode="renameXmlIds">
    <xsl:choose>
      <xsl:when test="local-name()='next'">
	<xsl:attribute name="next">
	  <xsl:value-of select="concat('#', concat('notes_', substring-after(., '#')))"/>
	</xsl:attribute>
      </xsl:when>
      <xsl:when test="local-name()='id'">
	<xsl:attribute name="xml:id">
	  <xsl:value-of select="concat('notes_', .)"/>
	</xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
	<xsl:copy/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="text() | comment() | processing-instruction()" mode="renameXmlIds">
    <xsl:copy/>
  </xsl:template>
  
  
</xsl:stylesheet>
