<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="/report">
        <head>
            <title>
                <xsl:value-of select="header/title"/>
            </title>
        </head>
        <body>
            <div class="report">
                <div class="title"><xsl:value-of select="header/title"/></div>
                <div class="info">
                    <xsl:value-of select="concat('Produced by ',header/user, ' on ', header/date)"/>
                </div>
                <table>
                    <xsl:attribute name="summary">
                        <xsl:value-of select="concat(header/title, ' for ' , header/context)"/>
                    </xsl:attribute>
                    <xsl:apply-templates/>
                </table>
            </div>
        </body>
    </xsl:template>

    <xsl:template match="header"/>

    <xsl:template match="body">
        <div class="body">
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="group">
        <div class="group">
            <xsl:value-of select="@name"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="resource">
        <div class="resource">

            <div class="description">
                <xsl:value-of select="description"/>
            </div>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="name">
        <div class="name">
            <xsl:value-of select="."/>
        </div>
    </xsl:template>

    <xsl:template match="description">
        <div class="description">
            <xsl:value-of select="."/>
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
            <div class="mediumType">
                <xsl:value-of select="@medium"/>
            </div>
            <div class="entryPoint">
                <xsl:value-of select="."/>
            </div>
        </div>
    </xsl:template>

    <xsl:template match="instructions">
        <div class="instructions">
            <xsl:value-of select="."/>
        </div>
    </xsl:template>

    <xsl:template match="confidential">
        <div class="confidential">
            <xsl:value-of select="."/>
        </div>
    </xsl:template>

    <xsl:template match="proprietary">
        <div class="proprietary">
            <xsl:value-of select="."/>
        </div>
    </xsl:template>

</xsl:stylesheet>
