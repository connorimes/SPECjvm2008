<?xml version="1.0" encoding='UTF-8'?>
<!-- Modified for XSLT Benchmark by Kevin Jones -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output encoding='UTF-8' indent='no'/>
 
  <xsl:template match="collection">
    <html>
      <head>
        <title><xsl:apply-templates select="description"/></title>
        <link href="../style.css" rel="stylesheet" type="text/css"/>
      </head>
      <body>
        <table border="1">
          <xsl:apply-templates select="recipe"/>
        </table>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="description">
     <xsl:value-of select="text()"/>
  </xsl:template>

  <xsl:template match="recipe">
    <tr>
      <td>
        <h1>
          <xsl:apply-templates select="title"/>
        </h1>
        <ul>
          <xsl:apply-templates select="ingredient"/>
        </ul>
        <xsl:apply-templates select="preparation"/>
        <xsl:apply-templates select="comment"/>
        <xsl:apply-templates select="nutrition"/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="ingredient">
    <xsl:choose>
      <xsl:when test="@amount">
        <li>
          <xsl:if test="@amount!='*'">
            <xsl:value-of select="@amount"/>
            <xsl:text> </xsl:text>
            <xsl:if test="@unit">
              <xsl:value-of select="@unit"/>
              <xsl:if test="number(@amount)>number(1)">
                 <xsl:text>s</xsl:text>
              </xsl:if>
              <xsl:text> of </xsl:text>
            </xsl:if>
            <xsl:text> </xsl:text>
          </xsl:if>
          <xsl:value-of select="@name"/>
        </li>
      </xsl:when>
      <xsl:otherwise>
        <li><xsl:value-of select="@name"/></li>
        <ul>
          <xsl:apply-templates select="ingredient"/>
        </ul>
        <xsl:apply-templates select="preparation"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="preparation">
    <ol><xsl:apply-templates select="step"/></ol>
  </xsl:template>

  <xsl:template match="step">
    <li><xsl:value-of select="text()|node()"/></li>
  </xsl:template>

  <xsl:template match="comment">
    <ul>
      <li type="square"><xsl:value-of select="text()|node()"/></li>
    </ul>
  </xsl:template>

  <xsl:template match="nutrition">
    <table border="2">
      <tr>
        <th>Calories</th><th>Fat</th><th>Carbohydrates</th><th>Protein</th>
        <xsl:if test="@alcohol">
          <th>Alcohol</th>
        </xsl:if>
      </tr>
      <tr>
        <td align="right"><xsl:value-of select="@calories"/></td>
        <td align="right"><xsl:value-of select="@fat"/>%</td>
        <td align="right"><xsl:value-of select="@carbohydrates"/>%</td>
        <td align="right"><xsl:value-of select="@protein"/>%</td>
        <xsl:if test="@alcohol">
          <td align="right"><xsl:value-of select="@alcohol"/>%</td>
        </xsl:if>
      </tr>
    </table>
  </xsl:template>

</xsl:stylesheet>

