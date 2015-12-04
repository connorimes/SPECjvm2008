<?xml version="1.0" encoding="ISO-8859-1" ?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:import href="xmlspec.xsl"/>
	<xsl:template match="proto">
		<p>
			<a name="function-{@name}">
				<b>Function: </b>
				<i>
					<xsl:value-of select="@return-type"/>
				</i>
				<xsl:text>
				</xsl:text>
				<b>
					<xsl:value-of select="@name"/>
				</b>
				<xsl:text>(</xsl:text>
				<xsl:for-each select="arg">
					<xsl:if test="not(position()=1)">
						<xsl:text>, </xsl:text>
					</xsl:if>
					<i>
						<xsl:value-of select="@type"/>
					</i>
					<xsl:choose>
						<xsl:when test="@occur='rep'">*</xsl:when>
						<xsl:when test="@occur='opt'">?</xsl:when>
					</xsl:choose>
				</xsl:for-each>
				<xsl:text>)</xsl:text>
			</a>
		</p>
	</xsl:template>
	<xsl:template match="function">
		<b>
			<a href="#function-{.}">
				<xsl:apply-templates/>
			</a>
		</b>
	</xsl:template>
	<xsl:template match="xfunction">
		<b>
			<a href="{@href}#function-{.}">
				<xsl:apply-templates/>
			</a>
		</b>
	</xsl:template>
<!-- Support for <loc role="available-format">...</loc> -->

	<xsl:template match="publoc/loc[@role='available-format']">
		<xsl:if test="not(preceding-sibling::loc[@role='available-format'])">
			<xsl:text>(available in </xsl:text>
		</xsl:if>
		<a href="{@href}">
			<xsl:apply-templates/>
		</a>
		<xsl:variable name="nf" select="count(following-sibling::loc[@role='available-format'])"/>
		<xsl:choose>
			<xsl:when test="not($nf)">
				<xsl:text>)</xsl:text>
			</xsl:when>
			<xsl:when test="$nf = 1">
				<xsl:choose>
					<xsl:when test="count(../loc[@role='available-format'])=2">
						<xsl:text> or </xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>, or </xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>, </xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>