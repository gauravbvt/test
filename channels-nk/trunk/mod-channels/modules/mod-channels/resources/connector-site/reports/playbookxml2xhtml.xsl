<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet SYSTEM "http://www.w3.org/People/cmsmcq/lib/xslt10.dtd">
<xsl:stylesheet
    version="2.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output 
	   method="html" 
	   doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" 
	   doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
       omit-xml-declaration="no"
	   indent="yes"/>

	<xsl:strip-space elements="*"/>
	
    <xsl:template match="/playbook">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
				<link rel="stylesheet" type="text/css" href="../css/playbook.css"/>
				<link rel="stylesheet" type="text/css" href="../css/forms.css"/>
				<title>Communication Playbook</title>
			</head>
			<body>
			     <div id="header">
			         <xsl:call-template name="legend"/>
    			     <xsl:apply-templates select="createdFor" />
    			     <xsl:apply-templates select="approvedBy" />
                 </div>
                 <div>
                    <xsl:apply-templates select="scenario"/>
                    <xsl:apply-templates select="plan"/>
                 </div>
			</body>
		</html>
    </xsl:template>
    
    <xsl:template match="scenario">
	   <div class="tableHeader">
            <span style="width:33%;float:left;">&nbsp;</span>
            <div id="scenarioName"><xsl:value-of select="name"/></div>
            <div id="firstUse">
               first use date &amp; time:<input type="text" size="20" style="border:none;border-bottom:#999999 1px solid"/>
            </div>
        </div>
    </xsl:template>
    
    <xsl:template name="getFullName">
        <xsl:param name="node"/>
        <!--
        <xsl:variable name="firstName" select="$node/firstName"/>
        <xsl:variable name="middleName" select="$node/middleName"/>
        <xsl:variable name="lastName" select="$node/lastName"/> 
        -->
        <xsl:value-of select="normalize-space(string-join(($node/firstName, $node/middleName, $node/lastName),' '))"/>
    </xsl:template>
    
    <xsl:template match="plan">
       <xsl:variable name="tableSummary">
            tasks and communications for 
            <xsl:call-template name="getFullName">
                <xsl:with-param name="node" select="/playbook/createdFor/person"/>
            </xsl:call-template>
       </xsl:variable> 
	   <table>
           <xsl:attribute name="summary">
                <xsl:value-of select="normalize-space($tableSummary)"/>
           </xsl:attribute>
           <tr>
               <th>action/information</th>
               <th>guidelines/contact</th>
               <th>date &amp; time</th>
               <th>comments</th>
           </tr>
           <xsl:for-each select="*">
                <xsl:variable name="serialNum" select="position()"/>
                <xsl:call-template name="processPlanItem">
                    <xsl:with-param name="node" select="."/>
                    <xsl:with-param name="serialNum" select="$serialNum"/>
                </xsl:call-template>
           </xsl:for-each>
       </table>
    </xsl:template>
    
    <xsl:template name="processPlanItem">
        <xsl:param name="serialNum"/>
        <xsl:param name="node"/>
        <xsl:variable name="rowClass">
            <xsl:choose>
                <xsl:when test="$serialNum mod 2 = 0">evenRow</xsl:when>
                <xsl:otherwise>oddRow</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="itemClass" select="node-name($node)"/>
        <tr> 
            <xsl:attribute name="class">
                <xsl:value-of select="$rowClass"/>
            </xsl:attribute> 
            <td>
                <xsl:attribute name="class">
                    <xsl:value-of select="$itemClass"/>
                </xsl:attribute>
                <div class="serialNum">
                    <xsl:value-of select="$serialNum"/>
                </div>
                <xsl:apply-templates select="$node"/>
                <div class="formHint">
                    we can put additional information here
                </div>
            </td>
            <td>
                <xsl:choose>
                    <xsl:when test="string($itemClass) = 'task'">
                        <xsl:apply-templates select="guidelines"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates select="contacts"/>
                    </xsl:otherwise>
                </xsl:choose>
            </td>
            <td class="dateTime">
               <xsl:call-template name="dateTime"/>
            </td>
            <td>
                <xsl:call-template name="comments"/>
            </td>
        </tr>
    </xsl:template>
    
    <xsl:template name="comments">
	   <div class="success">
           success? <input type="checkbox"/>
       </div>
       <textarea rows="4" cols="10"></textarea>
    </xsl:template>
    
    <xsl:template name="dateTime">
	   <div class="formItem">
           <label for="date1">date</label>
           <input type="text" id="date1" size="10"/>
       </div>
       <div class="formItem">
           <label for="time1">time</label>
           <input type="text" id="time1" size="10"/>
       </div>
    </xsl:template>
    
    <xsl:template match="guidelines">
	   <xsl:value-of select="."/>
    </xsl:template>
    
    <xsl:template match="contacts">
	   <xsl:for-each select="*">
            <div>
                <xsl:attribute name="class">
                    <xsl:value-of select="concat(local-name(),'Contact')"/>
                </xsl:attribute>
                <xsl:apply-templates select="person"/>
            </div>
       </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="incomingSharing">
        <div class="action">
           <xsl:value-of select="information"/>
        </div>
    </xsl:template>
    
    <xsl:template match="outgoingSharing">
        <div class="action">
           <xsl:value-of select="information"/>
        </div>
    </xsl:template>
    
    <xsl:template match="task">
        <div class="action">
            <xsl:value-of select="description"/>
        </div>
    </xsl:template>
    
    <xsl:template match="createdFor">
         <div class="createdFor">
            <div class="topTitle">communication playbook</div>
            <xsl:apply-templates select="person"/>
	     </div>
    </xsl:template>
    
    <xsl:template name="legend">
        <div id="legend">
            <img src="../images/dhsseallarge.png" height="84" alt="DHS Logo Large TEMP"/>
            <span class="formHint">legend</span>
            <table>
                <tr>
                    <td class="incomingSharing">
                        incoming communication
                    </td>
                </tr>
                <tr>
                    <td class="outgoingSharing">
                        outgoing communication
                    </td>
                </tr>
            </table>
        </div>
    </xsl:template>
    
    <xsl:template match="approvedBy">
        <div id="approvedBy">
           <xsl:apply-templates select="../issueDate"/>
           <div style="text-align:center">approved by</div>
           <xsl:apply-templates select="person"/>
        </div>
    </xsl:template>
    
    <xsl:template match="/playbook/issueDate">
        <div id="issueDate">
            <div class="formItem">
                 <div class="formItemLabel">date issued</div>
                 <div class="formItemContent"><xsl:value-of select="."/></div>
             </div>
         </div>
    </xsl:template>
    
    <xsl:template match="person">
        <div class="personInfo">
            <div class="orgName">
                <xsl:value-of select="role/organizationName"/>
            </div>
            <div class="name">
	            <xsl:call-template name="getFullName">
                    <xsl:with-param name="node" select="."/>
                </xsl:call-template> 
	        </div>
	        <div class="role">
	            <xsl:value-of select="role/title"/>
	        </div>
	        <xsl:apply-templates select="contactInfo"/>
        </div>
    </xsl:template>
    
    <xsl:template match="contactInfo">
		<div class="contact">
	        <div class="phone">
	            <xsl:value-of select="phone"/>
	        </div>
	        <div class="email">
	            <xsl:value-of select="email"/>
	        </div>
	        <div class="fax">
	           <xsl:value-of select="fax"/>
	        </div>
	        <div class="cellPhone">
	            <xsl:value-of select="cellPhone"/>
	        </div>
		</div>
    </xsl:template>
    
</xsl:stylesheet>