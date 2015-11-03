<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:tei="http://www.tei-c.org/ns/1.0"
    exclude-result-prefixes="xs" version="2.0">
    <xsl:key name="elements" match="tei:*" use="1"/>
    <xsl:key name="atts" match="@*" use="name(parent::*)"/>
    <xsl:key name="attVals" match="@*" use="concat(name(parent::*),name())"/>
    <xsl:template match="/">
        
        <TEI xmlns="http://www.tei-c.org/ns/1.0">
            <teiHeader>
                <fileDesc>
                    <titleStmt>
                        <title>ValList Report</title>
                    </titleStmt>
                    <publicationStmt><publisher>Lou Burnard Consulting</publisher></publicationStmt>
                    <sourceDesc><p>Report generated by valLister stylesheet</p></sourceDesc>
                </fileDesc>
            </teiHeader>
            <text>
                <body>
        
        
        <list type="giList">
            <xsl:for-each-group select="key('elements',1)" group-by="local-name()">
                <xsl:sort select="current-grouping-key()"/>
                <xsl:variable name="ident" select="current-grouping-key()"/>
                <label>
                    <xsl:value-of select="$ident"/> (<xsl:value-of select="count(current-group())"
                    />) </label>
              <item>
                  <list>  
                      <xsl:for-each-group select="key('atts',$ident)" group-by="name()">
                      <xsl:sort select="current-grouping-key()"/>
                      <xsl:variable name="attName" select="current-grouping-key()"/>
                      <label> <xsl:value-of select="concat('@',$attName)"/> (<xsl:value-of
                        select="count(current-group())"/>)</label> 
                      <item>
                          <list>                  
                              <xsl:for-each-group  select="key('attVals',concat($ident,name()))" group-by=".">
                                  <xsl:sort select="current-grouping-key()"/>
                                  <xsl:variable name="attVals" select="current-grouping-key()"/>
                                   <item>                        
                                       <xsl:value-of select="concat('=', $attVals)"/> (<xsl:value-of
                            select="count(current-group())"/>)     
                                   </item>                  
                              </xsl:for-each-group>
                          </list>
                     </item>
                </xsl:for-each-group></list></item>
            </xsl:for-each-group></list>
                </body></text></TEI>
    </xsl:template>
</xsl:stylesheet>
