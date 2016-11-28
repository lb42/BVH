<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:t="http://www.tei-c.org/ns/1.0"
   
    version="2.0"
    >
    <xsl:output method="text" encoding="utf-8"/>
   
    
    <xsl:template match="/">
        <xsl:text>    </xsl:text>
        <xsl:apply-templates />
   </xsl:template>         
        
    <xsl:template match="t:teiHeader"/>
    <xsl:template match="t:note"/>
    <xsl:template match="t:p[@rend]"/>
    <xsl:template match="t:hi[@rend]"/>
    <xsl:template match="text()"/>
    
<xsl:template match="t:p/text()">
<xsl:value-of select="ancestor::t:div/@n"/><xsl:text>_</xsl:text><xsl:number level="any"/><xsl:text>  </xsl:text>
    <xsl:value-of select="normalize-space(.)"/><xsl:text>  
    </xsl:text>
 </xsl:template>
    
</xsl:stylesheet>

