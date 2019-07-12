<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tei="http://www.tei-c.org/ns/1.0" version="2.0" xmlns:atilf="http://www.atilf.fr" xmlns="http://www.tei-c.org/ns/1.0" xmlns:xi="http://www.w3.org/2001/XInclude" xmlns:ex="http://www.tei-c.org/ns/Examples" exclude-result-prefixes="atilf tei xi ex">

  <xsl:param name="annotation">nomParDefautFichierAnnotation.xml</xsl:param>
  <xsl:param name="fichierNotes">nomParDefautNotes.xml</xsl:param>

  <!-- en même temps qu'on extrait l'édition, on met les <pb.../> sur une ligne tout seuls -->
  
  <!-- on travaille en deux passes, une pour mettre les id et une pour engendrer les notes -->
  
  <xsl:template match="/">
    <xsl:apply-templates mode="passIds"/>
    <xsl:result-document href="{$annotation}">
      <annotations>
	<xsl:apply-templates mode="passNotes"/>
      </annotations>
    </xsl:result-document>
    <xsl:result-document href="{$fichierNotes}">
      <div>
	<xsl:for-each select="//tei:note">
	  <p atilf:id="{generate-id()}"><xsl:apply-templates select="node()" mode="traitnotes"/></p><xsl:text>&#x0a;</xsl:text>
	</xsl:for-each>
      </div>
    </xsl:result-document>
  </xsl:template>


  <xsl:template match="*" mode="passIds">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="passIds"/>
    </xsl:copy>
  </xsl:template>
  
  <!--<xsl:template match="tei:pb" mode="passIds">
    <xsl:text>&#x0a;</xsl:text>
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="passIds"/>
    </xsl:copy>
    <xsl:text>&#x0a;</xsl:text>
  </xsl:template>-->
    
  <xsl:template match="tei:p | tei:lg | tei:head" mode="passIds">
    <xsl:text>&#x0a;</xsl:text>
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="passIds"/>
    </xsl:copy>
    <xsl:text>&#x0a;</xsl:text>
    </xsl:template>

    
  
  
  <xsl:template match="@* | comment() | processing-instruction()" mode="passIds">
    <xsl:copy/>
  </xsl:template>

  <xsl:template match="text()" mode="passIds">
    <xsl:analyze-string select="." regex="&amp;">
      <xsl:matching-substring>
	<g ref="#ET"><xsl:text>et</xsl:text></g>
      </xsl:matching-substring>
      <xsl:non-matching-substring>
	<xsl:analyze-string select="." regex="&lt;">
	  <xsl:matching-substring>
	    <g ref="#LESS_THAN"><xsl:text>moins</xsl:text></g>
	  </xsl:matching-substring>
	  <xsl:non-matching-substring>
	    <xsl:analyze-string select="." regex="&gt;">
	       <xsl:matching-substring>
		 <g ref="#MORE_THAN"><xsl:text>plus</xsl:text></g>
	       </xsl:matching-substring>
	       <xsl:non-matching-substring>
		 <xsl:copy/>
	       </xsl:non-matching-substring>
	    </xsl:analyze-string>
	  </xsl:non-matching-substring>
	</xsl:analyze-string>
      </xsl:non-matching-substring>
    </xsl:analyze-string>
  </xsl:template>

  <xsl:template match="*" mode="passNotes">
      <xsl:apply-templates mode="passNotes"/>
  </xsl:template>
  
  <xsl:template match="text()" mode="passNotes"/>

  

  

  <!-- le traitement des élements qu'on retire de l'édition : -->
  <!-- et qu'on insère dans le fichier des annotations        -->
  
  <!-- Il faudra évidemment complèter ce fichier ! -->
  
  <xsl:template match="tei:teiHeader | tei:orig[parent::tei:choice] | tei:sic[parent::tei:choice] | tei:note | tei:speaker | tei:facsimile | tei:graphic | tei:specGrp | tei:schemaSpec | tei:moduleSpec | xi:include | ex:egXML" mode="passIds">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:attribute name="id" namespace="http://www.atilf.fr">
	<xsl:value-of select="generate-id(.)"/>
      </xsl:attribute>
    </xsl:copy>
  </xsl:template>

  
  <xsl:template match="tei:teiHeader | tei:orig[parent::tei:choice] | tei:sic[parent::tei:choice] | tei:note | tei:speaker | tei:facsimile | tei:graphic |  tei:specGrp | tei:schemaSpec | tei:moduleSpec | xi:include | ex:egXML" mode="passNotes">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:attribute name="id" namespace="http://www.atilf.fr">
	<xsl:value-of select="generate-id(.)"/>
      </xsl:attribute>
      <xsl:copy-of select="node()"/>
    </xsl:copy>
  </xsl:template>


  <xsl:template match="*" mode="traitnotes">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="traitnotes"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="@* | comment() | processing-instruction()" mode="traitnotes">
    <xsl:copy/>
  </xsl:template>

  <xsl:template match="text()" mode="traitnotes">
    <xsl:analyze-string select="." regex="&amp;">
      <xsl:matching-substring>
	<g ref="#ET"><xsl:text>et</xsl:text></g>
      </xsl:matching-substring>
      <xsl:non-matching-substring>
	<xsl:analyze-string select="." regex="&lt;">
	  <xsl:matching-substring>
	    <g ref="#LESS_THAN"><xsl:text>moins</xsl:text></g>
	  </xsl:matching-substring>
	  <xsl:non-matching-substring>
	    <xsl:analyze-string select="." regex="&gt;">
	       <xsl:matching-substring>
		 <g ref="#MORE_THAN"><xsl:text>plus</xsl:text></g>
	       </xsl:matching-substring>
	       <xsl:non-matching-substring>
		 <xsl:copy/>
	       </xsl:non-matching-substring>
	    </xsl:analyze-string>
	  </xsl:non-matching-substring>
	</xsl:analyze-string>
      </xsl:non-matching-substring>
    </xsl:analyze-string>
  </xsl:template>
  
</xsl:stylesheet>
