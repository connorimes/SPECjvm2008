<?xml version="1.0" encoding="UTF-8" ?>
<!-- Modified for XSLT Benchmark by Kevin Jones -->

<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:e="http://www.w3.org/1999/XSL/Spec/ElementSyntax"
        exclude-result-prefixes="e"
>

<xsl:import href="xpath.xsl"/>

<xsl:strip-space elements="e:*"/>

<xsl:template match="spec" mode="css">
<xsl:text>p.element-syntax { border: solid thin }</xsl:text>
<xsl:apply-imports/>
</xsl:template>

<xsl:template match="e:element-syntax-summary">
  <xsl:for-each select="//e:element-syntax">
    <xsl:sort select="@name"/>
    <p class="element-syntax-summary"><code>
      <xsl:apply-templates select="e:in-category"/>
      <xsl:text>&lt;</xsl:text>
      <a href="#element-{@name}">
        <xsl:text>xsl:</xsl:text><xsl:value-of select="@name"/>
      </a>
      <xsl:apply-templates mode="top"/>
   </code></p>
  </xsl:for-each>
</xsl:template>

<xsl:template match="e:element-syntax">
<p class="element-syntax"><a name="element-{@name}"/><code>
<xsl:apply-templates select="e:in-category"/>
<xsl:text>&lt;xsl:</xsl:text><xsl:value-of select="@name"/>
<xsl:apply-templates mode="top"/>
</code></p>
</xsl:template>

<xsl:template match="e:in-category">
<xsl:text>&lt;!-- Category: </xsl:text>
<xsl:value-of select="@name"/>
<xsl:text> --&gt;</xsl:text>
<br/>
</xsl:template>

<xsl:template match="e:sequence|e:choice|e:model|e:element|e:text" mode="top">
<xsl:text>&gt;</xsl:text>
<br/>
<xsl:text>&#160;&#160;&lt;!-- Content: </xsl:text>
<xsl:apply-templates select="."/>
<xsl:text> --&gt;</xsl:text>
<br/>
<xsl:text>&lt;/xsl:</xsl:text>
<xsl:value-of select="../@name"/>
<xsl:text>&gt;</xsl:text>
</xsl:template>

<xsl:template match="e:sequence|e:choice">
<xsl:text>(</xsl:text>
<xsl:apply-templates/>
<xsl:text>)</xsl:text>
<xsl:call-template name="repeat"/>
</xsl:template>

<xsl:template match="e:model">
<xsl:call-template name="group"/>
<var><xsl:value-of select="@name"/></var>
<xsl:call-template name="repeat"/>
</xsl:template>

<xsl:template match="e:text">#PCDATA</xsl:template>

<xsl:template match="e:element">
<xsl:call-template name="group"/>
<a href="#element-{@name}">
<xsl:text>xsl:</xsl:text>
<xsl:value-of select="@name"/>
</a>
<xsl:call-template name="repeat"/>
</xsl:template>

<xsl:template name="group">
<xsl:if test="position()>1">
<xsl:choose>
<xsl:when test="parent :: e:sequence">, </xsl:when>
<xsl:when test="parent :: e:choice"> | </xsl:when>
</xsl:choose>
</xsl:if>
</xsl:template>

<xsl:template name="repeat">
  <xsl:choose>
   <xsl:when test="@repeat='one-or-more'">
    <xsl:text>+</xsl:text>
   </xsl:when>
   <xsl:when test="@repeat='zero-or-more'">
    <xsl:text>*</xsl:text>
   </xsl:when>
   <xsl:when test="@repeat='zero-or-one'">
    <xsl:text>?</xsl:text>
   </xsl:when>
  </xsl:choose>
</xsl:template>


<xsl:template match="e:empty" mode="top">
<xsl:text>&#160;/&gt;</xsl:text>
</xsl:template>

<xsl:template match="e:attribute" mode="top">
<br/>

<xsl:text>&#160;&#160;</xsl:text>
<xsl:choose>
<xsl:when test="@required='yes'">
<b><xsl:value-of select="@name"/></b>
</xsl:when>
<xsl:otherwise>
<xsl:value-of select="@name"/>
</xsl:otherwise>
</xsl:choose>
<xsl:text> = </xsl:text>

<xsl:apply-templates/>
</xsl:template>

<xsl:template match="e:data-type">
<xsl:if test="position()>1"> | </xsl:if>
<var><xsl:value-of select="@name"/></var>
</xsl:template>

<xsl:template match="e:constant">
<xsl:if test="position()>1"> | </xsl:if>
<xsl:text>"</xsl:text>
<xsl:value-of select="@value"/>
<xsl:text>"</xsl:text>
</xsl:template>

<xsl:template match="e:attribute-value-template">
<xsl:text>{ </xsl:text>
<xsl:apply-templates/>
<xsl:text> }</xsl:text>
</xsl:template>

<xsl:template match="var">
<var><xsl:apply-templates/></var>
</xsl:template>

</xsl:stylesheet>
