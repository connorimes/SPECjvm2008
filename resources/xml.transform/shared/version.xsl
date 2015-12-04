<?xml version="1.0"?>

<!--
	Generate an xml document containing basic processor information.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml" omit-xml-declaration="yes" encoding="utf-8"/>

<xsl:template match="/">
	<version>
		<vendor><xsl:value-of select="system-property('xsl:vendor')"/></vendor>
		<vendor-url><xsl:value-of select="system-property('xsl:vendor-url')"/></vendor-url>	
		<xsl-version><xsl:value-of select="system-property('xsl:version')"/></xsl-version>	
	</version>
</xsl:template>

</xsl:stylesheet>

