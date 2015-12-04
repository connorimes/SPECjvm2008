<?xml version="1.0" encoding="UTF-8" ?>
<!-- Modified for XSLT Benchmark by Kevin Jones -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

<xsl:import href="markup.xsl"/>
<xsl:output encoding='UTF-8' indent='no'/>

<xsl:template match="p">
  <p>
    <xsl:call-template name="markup">
      <xsl:with-param name="text" select="." />
      <xsl:with-param name="phrases" select="/text/keywords/keyword" />
      <xsl:with-param name="first-only" select="false()" />
    </xsl:call-template>
  </p>
</xsl:template>

<xsl:template match="keyword" mode="markup">
  <xsl:param name="word" />
  <em>
    <xsl:value-of select="$word" />
  </em>
</xsl:template>

</xsl:stylesheet>


