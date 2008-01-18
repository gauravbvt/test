<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xhtml="http://www.w3.org/1999/xhtml" exclude-result-prefixes="xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
    <xsl:output indent="yes" />

    <xsl:template match="/xhtml:html">
        <topics>
            <xsl:apply-templates select=".//xhtml:div[ @class='g']"/>
        </topics>
    </xsl:template>
    <xsl:template match="//xhtml:div">
            <xsl:apply-templates select=".//xhtml:td[1]//xhtml:a[1]"/>
    </xsl:template>
    <xsl:template match="xhtml:a">
        <topic>
            <xsl:attribute name="name">
                <xsl:value-of select="substring-after(substring-before(@href,'/?'),'/Top')"/></xsl:attribute>
        </topic>
    </xsl:template>
    
</xsl:stylesheet>
