<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tei="http://www.tei-c.org/ns/1.0" version="2.0">

  <xsl:output method="text"/>

  <xsl:variable name="returnApres">
    <xsl:text>p|head|incident</xsl:text>
  </xsl:variable>

  <xsl:variable name="lreturnApres">
    <xsl:value-of select="tokenize($returnApres, '\|')"/>
  </xsl:variable>

  
  <xsl:template match="/">
    <xsl:variable name="pass1">
      <xsl:apply-templates mode="pass1"/>
    </xsl:variable>
    <xsl:apply-templates select="$pass1" mode="pass2"/>
  </xsl:template>

  <xsl:template match="*" mode="pass1">
    <xsl:choose>
      <xsl:when test="contains($lreturnApres, local-name(.))">
	<xsl:copy>
	  <xsl:apply-templates mode="pass1"/>
	</xsl:copy>
      </xsl:when>
      <xsl:otherwise>
	<xsl:apply-templates mode="pass1"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="text()" mode="pass1">
    <xsl:copy/>
  </xsl:template>

  <!--             pass2               -->

  <xsl:template match="*" mode="pass2">
    <xsl:apply-templates mode="pass2"/>
    <xsl:text>&#x0a;</xsl:text>
  </xsl:template>
  
  <xsl:template match="text()" mode="pass2">
    <xsl:analyze-string select="." regex="-\s*\n\s*">
      <xsl:matching-substring>
	<xsl:text>-</xsl:text>
      </xsl:matching-substring>
      <xsl:non-matching-substring>
	<xsl:analyze-string select="." regex="(\s|\n)+">
	  <xsl:matching-substring>
	    <xsl:text> </xsl:text>
	  </xsl:matching-substring>
	  <xsl:non-matching-substring>
	    <xsl:value-of select="."/>
	  </xsl:non-matching-substring>
	</xsl:analyze-string>
      </xsl:non-matching-substring>
    </xsl:analyze-string>
  </xsl:template>
  
</xsl:stylesheet>
