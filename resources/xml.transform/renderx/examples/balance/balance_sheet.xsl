<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:rx="http://www.renderx.com/XSL/Extensions" version="1.0">
<xsl:output method="xml"
            version="1.0"
            indent="no"
            encoding="UTF-8"
			omit-xml-declaration="no"/>
    <!-- This document represents an English version of the balance sheet applied in the Republic of Armenia -->
    <!-- ***************** params *************** -->
    <xsl:param name="dark-bg-color" select="'rgb(200, 200, 200)'"/>
    <xsl:param name="part-separator-line-height" select="7"/>
    <xsl:param name="page-width" select="420"/>
    <xsl:param name="page-height" select="297"/>
    <!-- ***************** variables ************* -->
    <xsl:variable name="font-height" select="4"/>
    <xsl:variable name="available-height" select="277"/>
    <xsl:variable name="row-height-for-active" select="($available-height - 1 - $font-height * (4 + count(/balance/active/body/part)) - count(/balance/active/body/part) * $part-separator-line-height) div count(/balance/active/body/part/row)"/>
    <xsl:variable name="row-height-for-passive" select="($available-height - 1 - $font-height * (4 + count(/balance/passive/body/part)) - count(/balance/passive/body/part) * $part-separator-line-height) div count(/balance/passive/body/part/row)"/>
    <!-- ***************************************************** -->
    <!-- ***************** templates ************************* -->
    <!-- ***************************************************** -->
    <xsl:template match="balance">
        <fo:root>
            <fo:layout-master-set>
                <fo:simple-page-master master-name="order" page-width="{$page-width}mm" page-height="{$page-height}mm">
                    <fo:region-body margin="10mm"/>
                    <fo:region-before extent="10mm"/>
                    <fo:region-after extent="10mm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="order">
                <fo:static-content flow-name="xsl-region-before">
                    <fo:block text-align="center" font-size="10pt" padding-top="3mm">
                        This document represents an English version of the balance sheet applied in the Republic of Armenia
                    </fo:block>
                </fo:static-content>

                <fo:flow flow-name="xsl-region-body">
                    <fo:block font-size="10pt" font-family="NewtonAm">
                        <fo:table>
                            <fo:table-column column-width="170mm"/>
                            <fo:table-column column-width="5mm"/>
                            <fo:table-column column-width="170mm"/>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:apply-templates select="active"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell/>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:apply-templates select="passive"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
    <!-- ============================================= -->
    <xsl:template match="active | passive">
        <fo:table>
            <fo:table-column column-width="97mm"/>
            <fo:table-column column-width="10mm"/>
            <fo:table-column column-width="30mm"/>
            <fo:table-column column-width="33mm"/>
            <fo:table-body>
                <xsl:apply-templates select="head"/>
                <fo:table-row>
                    <xsl:call-template name="put-numbers">
                        <xsl:with-param name="qty" select="4"/>
                    </xsl:call-template>
                </fo:table-row>
                <xsl:apply-templates select="body"/>
            </fo:table-body>
        </fo:table>
    </xsl:template>
    <!-- ============================================= -->
    <xsl:template match="active/head | passive/head">
        <fo:table-row>
            <xsl:for-each select="column">
                <fo:table-cell border="1pt solid black" background-color="{$dark-bg-color}"
                    display-align="center" text-align="center" padding="1mm">
                    <fo:block font-size="11pt">
                        <xsl:value-of select="@name"/>
                    </fo:block>
                </fo:table-cell>
            </xsl:for-each>
        </fo:table-row>
    </xsl:template>
    <!-- ============================================= -->
    <xsl:template match="body">
        <xsl:apply-templates/>
    </xsl:template>
    <!-- ============================================= -->
    <xsl:template match="part">
        <xsl:choose>
            <xsl:when test="@legend = 'balance'">
                <xsl:apply-templates/>
            </xsl:when>
            <xsl:otherwise>
                <!-- part-header -->
                <fo:table-row>
                    <fo:table-cell border="1pt solid black">
                        <fo:block text-align="center">
                            <xsl:value-of select="concat(@legend, '.&#xA0;', @name)"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell border="1pt solid black">
                        <fo:block>&#xA0;</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border="1pt solid black">
                        <fo:block>&#xA0;</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border="1pt solid black">
                        <fo:block>&#xA0;</fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <!-- rows of the part -->
                <xsl:apply-templates/>
                <!-- part-separator row -->
                <xsl:if test="count(following-sibling::*) != 0">
                    <fo:table-row height="{$part-separator-line-height}mm">
                        <fo:table-cell border="1pt solid black" background-color="{$dark-bg-color}">
                            <fo:block>&#xA0;</fo:block>
                        </fo:table-cell>
                        <fo:table-cell border="1pt solid black" background-color="{$dark-bg-color}">
                            <fo:block>&#xA0;</fo:block>
                        </fo:table-cell>
                        <fo:table-cell border="1pt solid black" background-color="{$dark-bg-color}">
                            <fo:block>&#xA0;</fo:block>
                        </fo:table-cell>
                        <fo:table-cell border="1pt solid black" background-color="{$dark-bg-color}">
                            <fo:block>&#xA0;</fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- ============================================= -->
    <xsl:template match="row">
        <xsl:variable name="height">
            <xsl:choose>
                <xsl:when test="ancestor::active">
                    <xsl:value-of select="$row-height-for-active"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$row-height-for-passive"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <fo:table-row height="{$height}mm">
            <fo:table-cell border="1pt solid black" display-align="center">
                <fo:block margin-left="1mm">
                    <xsl:if test="../@legend = 'balance'">
                        <xsl:attribute name="text-align">center</xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="@name"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell border="1pt solid black" display-align="center">
                <fo:block text-align="center">
                    <xsl:value-of select="@id"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell border="1pt solid black" display-align="center">
                <fo:block text-align="right">
                    <xsl:value-of select="@start-value"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell border="1pt solid black" display-align="center">
                <fo:block text-align="right" margin-right="1mm">
                    <xsl:choose>
                        <xsl:when test="../@legend = 'balance'">
                            <xsl:attribute name="font-weight">bold</xsl:attribute>
                            <xsl:value-of select="sum(../preceding-sibling::part/row/@end-value)"/>
                        </xsl:when>
                        <xsl:when test="not(following-sibling::*)">
                            <xsl:attribute name="font-weight">bold</xsl:attribute>
                            <xsl:value-of select="sum(preceding-sibling::*/@end-value)"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="@end-value"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </fo:block>
            </fo:table-cell>
        </fo:table-row>
    </xsl:template>
    <!-- #################################################### -->
    <!-- ############### auxiliary templates ################ -->
    <!-- #################################################### -->
    <xsl:template name="put-numbers">
        <xsl:param name="qty"/>
        <xsl:param name="idx" select="1"/>
        <xsl:choose>
            <xsl:when test="$idx &gt; $qty"/>
            <xsl:otherwise>
                <fo:table-cell border="1pt solid black" background-color="{$dark-bg-color}"
                    display-align="center" text-align="center" padding="1mm">
                    <fo:block>
                        <xsl:value-of select="$idx"/>
                    </fo:block>
                </fo:table-cell>
                <xsl:call-template name="put-numbers">
                    <xsl:with-param name="qty" select="$qty"/>
                    <xsl:with-param name="idx" select="$idx + 1"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
