<?xml version="1.0" encoding='UTF-8'?>
<!-- Modified for XSLT Benchmark by Kevin Jones -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:strip-space elements="*"/>
	<xsl:output method="html" indent='no' encoding="utf-8"/>
	
<!-- Created by Johan Lindgren (TT, Sweden) and Alan Karben (ScreamingMedia, US)
		to show various possible outputs from NITF.
		It's not intended to handle all possible combinations of data.
		  -->

<!--      MAIN TEMPLATE   -->

	<xsl:template match="/">
		<html>
			<head>
				<title>
					<xsl:value-of select="nitf/head/title"/>
				</title>
				<link rel="stylesheet" type="text/css" href="nitf.css"/>
			</head>
			<body>
				<table border="1" cellpadding="6" width="550">
					<tr>
						<td>
							<xsl:apply-templates/>
<!-- Call all subtemplates -->

						</td>
					</tr>
				</table>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="body.head|body.content">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="p">
		<p class="nitfp">
			<xsl:apply-templates/>
		</p>
	</xsl:template>
	<xsl:template match="title">
	</xsl:template>
<!-- table -->

	<xsl:template match="nitf-table-summary">
	</xsl:template>
	<xsl:template match="table">
		<xsl:element name="table">
			<xsl:attribute name="border">
				<xsl:value-of select="@border"/>
			</xsl:attribute>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="tr">
		<tr>
			<xsl:apply-templates/>
		</tr>
	</xsl:template>
	<xsl:template match="th">
		<xsl:element name="th">
			<xsl:attribute name="colspan">
				<xsl:value-of select="@colspan"/>
			</xsl:attribute>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="td">
		<td>
			<xsl:apply-templates/>
		</td>
	</xsl:template>
	<xsl:template match="byline">
		<p class="nitfby">
			<xsl:apply-templates/>
		</p>
	</xsl:template>
	<xsl:template match="person">
		<b>
			<xsl:value-of select="."/>
		</b>
	</xsl:template>
	<xsl:template match="byttl">
		<br/>
		<i>
			<xsl:value-of select="."/>
		</i>
	</xsl:template>
	<xsl:template match="hedline">
		<div class="hedline">
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	<xsl:template match="hl1">
		<h1 class="nitfhl1">
			<xsl:apply-templates/>
		</h1>
	</xsl:template>
	<xsl:template match="hl2">
		<h2 class="nitfhl2">
			<xsl:apply-templates/>
		</h2>
	</xsl:template>
	<xsl:template match="hl3">
		<h3 class="nitfhl3">
			<xsl:apply-templates/>
		</h3>
	</xsl:template>
	<xsl:template match="note">
		<div class="note">
			<blockquote>
				<i>Editor's Note:</i>
				<xsl:value-of select="."/>
			</blockquote>
		</div>
	</xsl:template>
	<xsl:template match="tagline">
		<p class="tagline">
			<i>
				<xsl:value-of select="."/>
			</i>
		</p>
	</xsl:template>
	<xsl:template match="ul">
		<ul>
			<xsl:apply-templates/>
		</ul>
	</xsl:template>
	<xsl:template match="li">
		<li>
			<xsl:apply-templates/>
		</li>
	</xsl:template>
	<xsl:template match="em">
		<b>
			<xsl:apply-templates/>
		</b>
	</xsl:template>
	<xsl:template match="org">
		<b>
			<xsl:element name="a">
				<xsl:attribute name="href">http://www.stockpoint.com/get-quote?ticker=<xsl:value-of select="@value"/>
				</xsl:attribute>
				<xsl:attribute name="class">org</xsl:attribute>
				<xsl:value-of select="."/>
			</xsl:element>
		</b>
	</xsl:template>
<!--

<xsl:template match="media">
	<table border cellpadding="4" align="right">
	<xsl:element name="a">
	<xsl:attribute name="href">http://www.stockpoint.com/get-quote?ticker=<xsl:value-of select="@value"/></xsl:attribute>
	<xsl:attribute name="class">org</xsl:attribute><xsl:value-of select="."/>
	</xsl:element>
	</b>
	</table>
</xsl:template>
-->

	<xsl:template match="media">
		<xsl:element name="table">
			<xsl:attribute name="align">right</xsl:attribute>
			<xsl:attribute name="border">1</xsl:attribute>
			<xsl:attribute name="width">
				<xsl:value-of select="media-reference/@width"/>
			</xsl:attribute>
			<xsl:attribute name="cellpadding">6</xsl:attribute>
			<tr>
				<td>
					<xsl:element name="img">
						<xsl:attribute name="src">images/<xsl:value-of select="media-reference/@source"/>
						</xsl:attribute>
						<xsl:attribute name="width">
							<xsl:value-of select="media-reference/@width"/>
						</xsl:attribute>
						<xsl:attribute name="height">
							<xsl:value-of select="media-reference/@height"/>
						</xsl:attribute>
						<xsl:attribute name="alt">
							<xsl:value-of select="media-reference/@alternate-text"/>
						</xsl:attribute>
					</xsl:element>
					<div align="right">
						<font size="-2">Photo: 

	<xsl:value-of select="media-producer"/>
						</font>
					</div>
					<b>
						<font size="-1">
							<xsl:value-of select="media-caption"/>
						</font>
					</b>
				</td>
			</tr>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>