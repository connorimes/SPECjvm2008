<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg" version="1.0">
<xsl:output method="xml"
            version="1.0"
            indent="no"
            encoding="UTF-8"
			omit-xml-declaration="no"/>
    <xsl:template match="minutes">
        <fo:root>
            <fo:layout-master-set>
                <fo:simple-page-master master-name="order" page-width="210mm" page-height="297mm">
                    <fo:region-body margin="25mm"/>
                    <fo:region-before extent="25mm"/>
                    <fo:region-after extent="0mm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="order">
                <fo:flow flow-name="xsl-region-body">
                    <fo:block font-size="11pt" font-family="Times">
                        <fo:block space-before="10mm" font-size="16pt">
                            <xsl:apply-templates select="head/title"/>
                        </fo:block>
                        <fo:block font-weight="bold" font-size="12pt" space-before="5mm">
                            <xsl:apply-templates select="head/date"/>
                        </fo:block>
                        <fo:block>
                            <xsl:apply-templates select="head/attending"/>
                        </fo:block>
                        <fo:block>
                            <xsl:apply-templates select="head/agenda"/>
                        </fo:block>
                        <fo:block>
                            <xsl:apply-templates select="body"/>
                        </fo:block>
                        <fo:block border-top="1pt solid black" font-size="8pt" font-style="italic" space-before="110mm" padding-top="1mm">
                            <xsl:apply-templates select="head/disclaimer"/>
                        </fo:block>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
    <!-- ====================================== -->
    <xsl:template match="attending">
        <fo:block>
            <fo:block space-before="5mm" space-after="5mm" font-weight="bold">
                Attending:
            </fo:block>
            <xsl:for-each select="name">
                <fo:block margin-left="7mm">                                            
                    * <xsl:apply-templates/>
                </fo:block>
            </xsl:for-each>
        </fo:block>
    </xsl:template>
    <!-- ====================================== -->
    <xsl:template match="agenda">
        <fo:block>
            <fo:block space-before="5mm" space-after="5mm" font-weight="bold">
                Agenda:
            </fo:block>
            <xsl:for-each select="subject">
                <fo:block margin-left="7mm">                                            
                    <xsl:number count="subject"/>. <xsl:apply-templates/>
                </fo:block>
            </xsl:for-each>
        </fo:block>
    </xsl:template>
    <!-- ====================================== -->
    <xsl:template match="body">
        <fo:block>
            <xsl:for-each select="discussion">
                <fo:block font-weight="bold" font-size="12pt" space-before="5mm" space-after="5mm">
                    <xsl:number count="discussion"/>. <xsl:value-of select="header"/>
                </fo:block>
                <fo:block>
                    <xsl:apply-templates select="text"/>
                </fo:block>
            </xsl:for-each>
        </fo:block>
    </xsl:template>
    <!-- ====================================== -->
    <xsl:template match="text">
        <fo:block>
            <xsl:apply-templates/>
        </fo:block>
    </xsl:template>
    <!-- ====================================== -->
    <xsl:template match="p">
        <fo:block space-after="5mm">
            <xsl:if test="@indent">
                <xsl:attribute name="margin-left">
                    <xsl:value-of select="@indent"/>mm
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates/>
        </fo:block>
    </xsl:template>
    <!-- ====================================== -->
    <!-- ====================================== -->
    <!-- ====================================== -->
    <!-- ====================================== -->
    <!-- ====================================== -->
</xsl:stylesheet>
