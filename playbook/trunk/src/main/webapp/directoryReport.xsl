<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/report">
        <html>
            <head>
                <title><xsl:value-of select="header/title"/></title>
            </head>
            <body>
                <div class="report">
                    <xsl:apply-templates/>
                </div>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="header">
        <div class="header"> <xsl:apply-templates/> </div>
    </xsl:template>

    <xsl:template match="header/title">
        <div class="title"><xsl:value-of select="."/></div>
    </xsl:template>

    <xsl:template match="header/context">
        <div class="context"><xsl:value-of select="."/></div>
    </xsl:template>

    <xsl:template match="header/user">
        <div class="user"><xsl:value-of select="."/></div>
    </xsl:template>

    <xsl:template match="header/date">
        <div class="date"><xsl:value-of select="."/></div>
    </xsl:template>

    <xsl:template match="body">
        <div class="body"><xsl:apply-templates/></div>
    </xsl:template>

    <xsl:template match="group">
        <div >
            <xsl:attribute name="class">group <xsl:value-of select="@type"/></xsl:attribute>
            <xsl:value-of select="@name"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="resource">
        <div >
            <xsl:attribute name="class">resource <xsl:value-of select="@type"/></xsl:attribute>
            <div class="name"><xsl:value-of select="name"/></div>
            <div class="description"><xsl:value-of select="description"/></div>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="resource/organization">
        <div class="organization">
             <xsl:value-of select="@name"/>
        </div>
    </xsl:template>

    <xsl:template match="role">
        <div class="role">
            <xsl:value-of select="@name"/>
        </div>
    </xsl:template>

    <xsl:template match="contactInfos">
        <div class="contactInfos">
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="noContactInfo">
        <div class="noContactInfo">No contact info</div>
    </xsl:template>

    <xsl:template match="contactInfo">
        <div class="contactInfo">
            <div class="mediumType"> <xsl:value-of select="@medium"/></div>
            <div class="entryPoint"> <xsl:value-of select="."/> </div>
        </div>
    </xsl:template>

    <xsl:template match="instructions">
        <div class="instructions"><xsl:value-of select="."/></div>
    </xsl:template>

</xsl:stylesheet>
