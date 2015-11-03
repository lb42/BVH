<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:tei="http://www.tei-c.org/ns/1.0"
    exclude-result-prefixes="xs  tei" version="2.0">
    <xsl:template match="tei:teiHeader"/>
    <xsl:template match="tei:text">
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
                    <table>
                        <xsl:apply-templates select="//tei:elementSpec">
                            <xsl:sort select="@ident"/>
                        </xsl:apply-templates>
                    </table>
                </body>
            </text>
        </TEI>
    </xsl:template>
    <xsl:template match="tei:elementSpec">
        <xsl:for-each select="tei:attList/tei:attDef/tei:valList">
            <row>
                <cell role="label">
                    <xsl:value-of select="../../../@ident"/>
                    <xsl:text>@</xsl:text>
                    <xsl:value-of select="../@ident"/>
                </cell>
                <cell>
                    <xsl:for-each select="tei:valItem">
                        <xsl:value-of select="@ident"/>
                        <xsl:text>  </xsl:text>
                    </xsl:for-each>
                </cell>
            </row>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
