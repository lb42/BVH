<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:t="http://www.tei-c.org/ns/1.0"
    xmlns:math="http://exslt.org/math" extension-element-prefixes="math"
    exclude-result-prefixes="xs t" version="2.0">
    <!-- Take 5 random passages of 400 “tokens” from each text: 100 samples per language. 
        Whitespace tokenizer: 2,000 whitespace-delimited tokens per text. 
Tokens that don’t fit a entire sentence get trimmed manually.
 Exclude headings, include poetry -->

    <xsl:variable name="textID" select="//t:TEI/@xml:id"/>

    <!-- get a random number between 1 and number of paragraphs in the body of the text -->
    <xsl:variable name="pCount" select="count(//t:p)"/>
    <xsl:variable name="random">
        <xsl:value-of select="floor(math:random() * $pCount)"/>
    </xsl:variable>
    <!-- how many words in that random para? -->
    <xsl:variable name="wCount">
        <xsl:value-of
            select="
                string-length(translate(normalize-space
                (//t:p[count(preceding::t:p[ancestor::t:body]) = $random]), ' ', '')) + 1"
        />
    </xsl:variable>
    <xsl:variable name="prefix">
        <xsl:value-of select="concat($textID, format-number($random, '0000'))"/>
    </xsl:variable>
    <xsl:template match="/">
        <xsl:message>Text is <xsl:value-of select="$textID"/> and there are <xsl:value-of
                select="$pCount"/> paragraphs here.</xsl:message>
        <xsl:message>Paragraph <xsl:value-of select="$random"/> has <xsl:value-of select="$wCount"/>
            words</xsl:message>

        <!-- if para has at least 400 words proceed to process it; otherwise do nothing -->
        <xsl:if test="$wCount &gt; 399">
            <sample>
                <xsl:apply-templates
                    select="//t:p[count(preceding::t:p[ancestor::t:body]) = $random]"/>
            </sample>
        </xsl:if>

    </xsl:template>

    <xsl:template match="t:p">
        <xsl:variable name="pString">
            <xsl:value-of select="."/>
        </xsl:variable>
        <xsl:message>Which has <xsl:value-of select="string-length($pString)"/> chars</xsl:message>

        <xsl:message>Which has <xsl:value-of select="count(tokenize($pString, '\.\s'))"/>
            sUnits</xsl:message>
        <xsl:for-each select="tokenize($pString, '\.\s')">
            <xsl:variable name="seq">
                <xsl:value-of select="concat($prefix, string(position()))"/>
            </xsl:variable>
            <s n="{$seq}">
                <xsl:value-of select="."/>
            </s>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
