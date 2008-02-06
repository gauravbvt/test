<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" exclude-result-prefixes="#default"  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output indent="yes" method="xml" />    
<xsl:template match="/results">
      <html>
          <head><title>Semantic Match Profiling</title>
          <style type="text/css">
              .hi { 
                  color: green;
                  background-color: inherit;
                  font-weight: bold;
              }
              .med { 
                  color: green;
                  background-color: inherit;
                  font-weight: normal;
              }
              .lo { 
                  color: green;
                  background-color: inherit;
                  font-weight: normal;
                  font-style: italic;
              }
              .none { 
                  color: black;
                  background-color: inherit;
                  font-weight: normal;
              }
              .normal { 
                  color: gray;
                  background-color: inherit;
                  font-weight: normal;
              }
              .neg { 
                  color: red;
                  background-color: inherit;
                  font-weight: normal;
              }
              .failed {
                  background-color:  yellow;
              }
              #results th {
                  text-align: right;
              }
              #results td {
                  text-align: center;
              }
              #results h2, #parms h2, #legend h2 {
                  display: none;
              }   
              div#parms {
                  border: 1px solid black;
                  margin-left: auto;
                  margin-right: auto;
                  width: 33%;
              }
              div#legend {
                  width: 2in;
                  border: 1px solid gray;
                  margin-right: 5px;
                  padding: 5px;
                  text-align: center;
                  font-size: smaller;
                  position: relative;
                  top: -12em;
                  float: right;
              }
          </style>
          </head>
          <body>
              <h1>Semantic match profile for <xsl:value-of select="@source"/></h1>
              <div id="results">
                  <h2>Sample values</h2>
                  <table><xsl:apply-templates/>
                  </table>
                  <div id="legend">
                      <h2>Legend</h2>
                      <table>
                          <tr><td class="hi">Expecting high score</td></tr>
                          <tr><td class="med">Expecting medium score</td></tr>
                          <tr><td class="lo">Expecting low score</td></tr>
                          <tr><td class="none">Expecting "no match" score</td></tr>
                          <tr><td class="neg">Expecting negative score</td></tr>
                          <tr><td class="normal">No expectation</td></tr>
                      </table>
                  </div>
              </div>
              <div id="parms">
                  <h2>Match parameters:</h2>
                  <table>
                      <tr><th>Score:</th><td id="score"><xsl:value-of select="@score"/></td></tr>
                      <tr><th>Half-Life:</th><td><xsl:value-of select="@halfLife"/></td></tr>
                      <tr><th>Child Count Weight:</th><td><xsl:value-of select="@childCount"/></td></tr>
                      <tr><th>Child Scores Weight:</th><td><xsl:value-of select="@childScores"/></td></tr>
                  </table>
              </div>
          </body>
      </html>
</xsl:template>

<xsl:template match="row">
    <tr><th><a><xsl:attribute name="href">http://localhost:8080/matcher/sign+text@data:,<xsl:value-of select="translate(@text,' ','_')"/></xsl:attribute>
            <xsl:value-of select="@text"/></a>:</th>
        <xsl:apply-templates select="cell"/>
        <td><span class="arrow">▲</span></td>
    </tr>
</xsl:template>
    
    <xsl:template match="cell[ @pass='true' ]">
        <td><xsl:attribute name="class"><xsl:value-of select="@expected"/></xsl:attribute>
            <xsl:value-of select="@value"/></td>
    </xsl:template>
    
    <xsl:template match="cell[ @pass='false' ]">
        <td><xsl:attribute name="class"><xsl:value-of select="@expected"/></xsl:attribute>
            <span class="failed"><xsl:value-of select="@value"/></span></td>
    </xsl:template>
    
</xsl:stylesheet>
