<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xhtml="http://www.w3.org/1999/xhtml" exclude-result-prefixes="xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
    <xsl:output indent="yes" />
    <xsl:template match="/xhtml:html">
        <topics>
            <xsl:apply-templates select=".//xhtml:li//xhtml:a[ starts-with( @href, 'http://dmoz.org' ) ]"/>
        </topics>
    </xsl:template>
    <xsl:template match="xhtml:a">
        <topic>
            <xsl:attribute name="name">
                <xsl:value-of select="substring-after(@href,'http://dmoz.org')"/></xsl:attribute>
        </topic>
    </xsl:template>
</xsl:stylesheet>
