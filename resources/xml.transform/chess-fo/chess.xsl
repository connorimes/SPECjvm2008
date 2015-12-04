<?xml version='1.0' encoding='UTF-8'?>
<!-- Modified for XSLT Benchmark by Kevin Jones -->

<!-- =============================================================== -->
<!--                                                                 -->
<!-- This stylesheet makes part of RenderX XSLFO Test Suite.         -->
<!--                                                                 -->
<!-- It produces a sequence of chess diagram in XSL FO, starting     -->
<!-- from a PGN-like XML notation. The result can be further         -->
<!-- converted to a page-oriented format (PDF or PostScript).        -->
<!--                                                                 -->
<!-- XSL FO version taken into account:                              -->
<!--     http://www.w3.org/TR/2000/CR-xsl-20001121                   -->
<!--                                                                 -->
<!--     Author: Anton Dovgyallo                                     -->
<!--                                                                 -->
<!-- (c) RenderX, 1999-2000. Permission to copy and modify is        -->
<!-- granted, provided that any derived work contain a reference     -->
<!-- to this original document.                                      -->
<!-- =============================================================== -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
	<xsl:output method="xml" version="1.0" indent="no" encoding="utf-8" omit-xml-declaration="no"/>
<!-- ****************************************************** -->
<!-- * Root template. Sets page layout.                   * -->
<!-- ****************************************************** -->

	<xsl:template match="chessgame">
		<fo:root>
			<fo:layout-master-set>
<!-- First (cover) page -->

				<fo:simple-page-master master-name="pagemaster-first">
					<fo:region-body margin="1in 0.7in" display-align="center" padding="0.1in"/>
					<fo:region-after extent="1in" display-align="before" padding="6pt 0.7in"/>
				</fo:simple-page-master>
<!-- Regular page -->

				<fo:simple-page-master master-name="pagemaster-others">
					<fo:region-body margin="0.7in" padding="0.1in" border-top="thin solid gray" border-bottom="thin solid gray"/>
					<fo:region-before extent="0.7in" display-align="after" padding="6pt 0.7in"/>
					<fo:region-after extent="0.7in" display-align="before" padding="6pt 0.7in"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
<!-- First page uses a single page-master -->

			<fo:page-sequence master-name="pagemaster-first" force-page-count="no-force">
				<fo:static-content flow-name="xsl-region-after">
					<fo:block text-align="center" font="14pt Times">
						<xsl:value-of select="@site"/>
						<xsl:text>, </xsl:text>
						<xsl:value-of select="@date"/>
					</fo:block>
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body">
					<fo:block text-align="center">
						<xsl:apply-templates select="@event"/>
						<xsl:apply-templates select="@round"/>
					</fo:block>
					<fo:list-block provisional-distance-between-starts="2in" provisional-label-separation="9pt" space-before="6pt" space-after="6pt" text-align="start" font="13pt Times">
						<xsl:apply-templates select="@white"/>
						<xsl:apply-templates select="@black"/>
						<xsl:apply-templates select="@opening"/>
						<xsl:apply-templates select="@result"/>
					</fo:list-block>
				</fo:flow>
			</fo:page-sequence>
			<fo:page-sequence master-name="pagemaster-others" initial-page-number="1">
				<fo:static-content flow-name="xsl-region-before">
					<fo:list-block font="10pt Helvetica" provisional-distance-between-starts="5in" provisional-label-separation="0in">
						<fo:list-item>
							<fo:list-item-label end-indent="label-end()">
								<fo:block text-align="start" font-weight="bold">
									<xsl:value-of select="@white"/>&#8211; <xsl:value-of select="@black"/>
								</fo:block>
							</fo:list-item-label>
							<fo:list-item-body start-indent="body-start()">
								<fo:block text-align="end">Page <fo:page-number/>
								</fo:block>
							</fo:list-item-body>
						</fo:list-item>
					</fo:list-block>
				</fo:static-content>
				<fo:static-content flow-name="xsl-region-after">
					<fo:list-block font="9pt Times" provisional-distance-between-starts="3in" provisional-label-separation="0in">
						<fo:list-item>
							<fo:list-item-label end-indent="label-end()" text-align="start">
								<fo:block font-weight="bold" font-style="italic">
									<xsl:text>Rendered to PDF with XEP</xsl:text>
								</fo:block>
							</fo:list-item-label>
							<fo:list-item-body start-indent="body-start()" text-align="end">
								<fo:block>
									<fo:basic-link external-destination="url('http://www.renderx.com/')" color="#0000C0">
										<xsl:text>www.RenderX.com</xsl:text>
									</fo:basic-link>
								</fo:block>
							</fo:list-item-body>
						</fo:list-item>
					</fo:list-block>
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body">
					<fo:table text-align="center" display-align="center">
						<fo:table-body>
							<xsl:apply-templates select="move[1]"/>
						</fo:table-body>
					</fo:table>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
<!-- ****************************************************** -->

<!-- * A series of templates to handle game attributes    * -->

<!-- ****************************************************** -->

	<xsl:template match="@event">
		<fo:block font="bold 24pt Times" space-after="12pt">
			<xsl:value-of select="."/>
		</fo:block>
	</xsl:template>
	<xsl:template match="@round">
		<fo:block font="18pt Times" space-after="24pt">

       Round <xsl:value-of select="."/>
		</fo:block>
	</xsl:template>
	<xsl:template match="@white">
		<xsl:call-template name="draw-list-item">
			<xsl:with-param name="left" select="'White:'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="@black">
		<xsl:call-template name="draw-list-item">
			<xsl:with-param name="left" select="'Black:'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="@opening">
		<xsl:call-template name="draw-list-item">
			<xsl:with-param name="left" select="'Opening:'"/>
		</xsl:call-template>
	</xsl:template>
<!-- Processing result attribute. The first template handles   -->
<!-- a case where result is specified in an unknown form;      -->
<!-- three subsequent templates handle standard result values. -->

	<xsl:template match="@result">
		<xsl:call-template name="draw-list-item">
			<xsl:with-param name="left" select="'Result:'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="@result[.='1-0']">
		<xsl:call-template name="draw-list-item">
			<xsl:with-param name="left" select="'Result:'"/>
			<xsl:with-param name="right" select="'White wins'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="@result[.='0-1']">
		<xsl:call-template name="draw-list-item">
			<xsl:with-param name="left" select="'Result:'"/>
			<xsl:with-param name="right" select="'Black wins'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="@result[.='1/2-1/2']">
		<xsl:call-template name="draw-list-item">
			<xsl:with-param name="left" select="'Result:'"/>
			<xsl:with-param name="right" select="'Draw'"/>
		</xsl:call-template>
	</xsl:template>
<!-- Draw a single list item on the cover page.       -->
<!-- Put into a separate template for maintainability -->

	<xsl:template name="draw-list-item">
		<xsl:param name="left"/>
		<xsl:param name="right" select="."/>
		<fo:list-item>
			<fo:list-item-label end-indent="label-end()" text-align="end">
				<fo:block font-weight="bold">
					<xsl:value-of select="$left"/>
				</fo:block>
			</fo:list-item-label>
			<fo:list-item-body start-indent="body-start()" text-align="start">
				<fo:block>
					<xsl:value-of select="$right"/>
				</fo:block>
			</fo:list-item-body>
		</fo:list-item>
	</xsl:template>
<!-- ****************************************************** -->
<!-- * Applies to a single move (i.e. two half-moves).    * -->
<!-- * Recursive: at the end, applies itself to the next  * -->
<!-- * move in the chessgame.                             * -->
<!-- ****************************************************** -->

	<xsl:template match="move">
		<xsl:param name="chessboard" select="'RNBQKBNRPPPPPPPPxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxpppppppprnbqkbnr'"/>
<!-- $move-white contains the chessboard after the white's move -->

		<xsl:variable name="move-white">
			<xsl:call-template name="move-piece">
				<xsl:with-param name="chessboard" select="$chessboard"/>
				<xsl:with-param name="move" select="white"/>
				<xsl:with-param name="player" select="'white'"/>
			</xsl:call-template>
		</xsl:variable>
<!-- $move-black contains the chessboard after the black's move -->

		<xsl:variable name="move-black">
<!-- The test serves to handle incomplete moves at the end, -->

<!-- when the game stops at the white's half-move.          -->

			<xsl:choose>
				<xsl:when test="black">
					<xsl:call-template name="move-piece">
						<xsl:with-param name="chessboard" select="$move-white"/>
						<xsl:with-param name="move" select="black"/>
						<xsl:with-param name="player" select="'black'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="''"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
<!-- Draw a move diagram -->

		<xsl:call-template name="render-move">
			<xsl:with-param name="move-white" select="$move-white"/>
			<xsl:with-param name="move-black" select="$move-black"/>
		</xsl:call-template>
<!-- Recurse -->

		<xsl:apply-templates select="following-sibling::move[1]">
			<xsl:with-param name="chessboard" select="$move-black"/>
		</xsl:apply-templates>
	</xsl:template>
<!-- ****************************************************** -->
<!--  Moves the piece from one position to another.         -->
<!--  Special cases (O-O and O-O-O) are treated.            -->
<!--  Invoked by the 'move' template for each half-move.    -->
<!--  Parameters:                                           -->
<!--    $chessboard holds the initial chessboard state;     -->
<!--    $move is a PGN-like description of the move;        -->
<!--    $player = 'white'|'black' - needed for castlings.   -->
<!-- ****************************************************** -->

	<xsl:template name="move-piece">
		<xsl:param name="chessboard"/>
		<xsl:param name="move"/>
		<xsl:param name="player"/>
		<xsl:choose>
<!-- O-O castles short (King's side) -->

			<xsl:when test="$move = 'O-O'">
				<xsl:variable name="y">
					<xsl:choose>
						<xsl:when test="$player = 'white'">1</xsl:when>
						<xsl:otherwise>8</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="result">
					<xsl:call-template name="move-piece">
						<xsl:with-param name="chessboard" select="$chessboard"/>
						<xsl:with-param name="player" select="$player"/>
						<xsl:with-param name="move" select="concat('K ', 'e', $y, '-', 'g', $y)"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:call-template name="move-piece">
					<xsl:with-param name="chessboard" select="$result"/>
					<xsl:with-param name="player" select="$player"/>
					<xsl:with-param name="move" select="concat('R ', 'h', $y, '-', 'f', $y)"/>
				</xsl:call-template>
			</xsl:when>
<!-- O-O-O castles long (Queen's side) -->

			<xsl:when test="$move = 'O-O-O'">
				<xsl:variable name="y">
					<xsl:choose>
						<xsl:when test="$player = 'white'">1</xsl:when>
						<xsl:otherwise>8</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="result">
					<xsl:call-template name="move-piece">
						<xsl:with-param name="chessboard" select="$chessboard"/>
						<xsl:with-param name="player" select="$player"/>
						<xsl:with-param name="move" select="concat('K ', 'e', $y, '-', 'c', $y)"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:call-template name="move-piece">
					<xsl:with-param name="chessboard" select="$result"/>
					<xsl:with-param name="player" select="$player"/>
					<xsl:with-param name="move" select="concat('R ', 'a', $y, '-', 'd', $y)"/>
				</xsl:call-template>
			</xsl:when>
<!-- parse move -->

			<xsl:otherwise>
				<xsl:variable name="piece">
					<xsl:choose>
						<xsl:when test="$player = 'white'">
							<xsl:value-of select="substring-before($move, ' ')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="translate(substring-before($move, ' '), 'RNBQKP', 'rnbqkp')"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="coords" select="substring(substring-after($move, ' '), 1, 5)"/>
				<xsl:variable name="from" select="(substring(substring-before($coords, '-'), 2, 1) - 1) * 8 

          + string-length(substring-before('abcdefgh', substring(substring-before($coords, '-'), 1, 1))) + 1"/>
				<xsl:variable name="to" select="(substring(substring-after ($coords, '-'), 2, 1) - 1) * 8 

          + string-length(substring-before('abcdefgh', substring(substring-after ($coords, '-'), 1, 1))) + 1"/>
				<xsl:variable name="get-piece" select="concat(substring($chessboard, 1, $from - 1), 'x', 

                                                      substring($chessboard, $from + 1))"/>
				<xsl:value-of select="concat(substring($get-piece, 1, $to - 1), $piece, 

                                     substring($get-piece, $to + 1))"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
<!-- ****************************************************** -->
<!--  Draws two diagrams for half-moves.                    -->
<!--  Parameters:                                           -->
<!--    $move-white - first half-move state;                -->
<!--    $move-black - second half-move state.               -->
<!-- ****************************************************** -->

	<xsl:template name="render-move">
		<xsl:param name="move-white"/>
		<xsl:param name="move-black"/>
		<xsl:variable name="move-number">
			<xsl:number count="move" format="1. "/>
		</xsl:variable>
		<fo:table-row keep-together.within-page="always">
			<fo:table-cell>
				<fo:block padding-before="10pt" padding-after="4pt">
					<xsl:value-of select="$move-number"/>
					<xsl:value-of select="white"/>
					<xsl:if test="not(following-sibling::move) and not(black)">
						<xsl:text>
						</xsl:text>
						<fo:wrapper font-weight="bold">
							<xsl:value-of select="ancestor::chessgame/@result"/>
						</fo:wrapper>
					</xsl:if>
				</fo:block>
				<xsl:call-template name="render-chessboard">
					<xsl:with-param name="chessboard" select="$move-white"/>
				</xsl:call-template>
			</fo:table-cell>
			<xsl:if test="$move-black != ''">
				<fo:table-cell>
					<fo:block padding-before="10pt" padding-after="4pt">
						<xsl:value-of select="$move-number"/>
						<xsl:text>&#x2026; </xsl:text>
						<xsl:value-of select="black"/>
						<xsl:if test="not(following-sibling::move)">
							<xsl:text>
							</xsl:text>
							<fo:wrapper font-weight="bold">
								<xsl:value-of select="ancestor::chessgame/@result"/>
							</fo:wrapper>
						</xsl:if>
					</fo:block>
					<xsl:call-template name="render-chessboard">
						<xsl:with-param name="chessboard" select="$move-black"/>
					</xsl:call-template>
				</fo:table-cell>
			</xsl:if>
		</fo:table-row>
	</xsl:template>
<!-- ****************************************************** -->
<!--  Draws a single diagram.                               -->
<!--  Parameters:                                           -->
<!--    $chessboard - board state.                          -->
<!-- ****************************************************** -->

	<xsl:template name="render-chessboard">
		<xsl:param name="chessboard"/>
		<fo:table width="192pt" height="192pt" border="thin solid black" start-indent="0.26in" end-indent="0.26in" font="20pt/0pt Chess">
			<fo:table-column column-width="24pt" number-columns-repeated="8"/>
			<fo:table-body>
				<xsl:call-template name="render-chessboard-row">
					<xsl:with-param name="chessboard" select="$chessboard"/>
				</xsl:call-template>
			</fo:table-body>
		</fo:table>
	</xsl:template>
<!-- ****************************************************** -->
<!--  Recursive template: draws a specified chessboard row. -->
<!--  Calls itself for the next row. Note that rows are     -->
<!--  listed in descending order: the first row will be     -->
<!--  drawn at the bottom.                                  -->
<!--  Parameters:                                           -->
<!--    $chessboard - board state;                          -->
<!--    $row-number - row being drawn.                      -->
<!-- ****************************************************** -->

	<xsl:template name="render-chessboard-row">
		<xsl:param name="chessboard"/>
		<xsl:param name="row-number" select="8"/>
		<fo:table-row height="24pt">
			<xsl:call-template name="render-chessboard-cell">
				<xsl:with-param name="row-number" select="$row-number"/>
				<xsl:with-param name="count" select="1"/>
				<xsl:with-param name="row-content">
					<xsl:value-of select="substring($chessboard, $row-number * 8 - 7, 8)"/>
				</xsl:with-param>
			</xsl:call-template>
		</fo:table-row>
		<xsl:if test="$row-number &gt; 1">
<!-- recurse if there are more rows -->

			<xsl:call-template name="render-chessboard-row">
				<xsl:with-param name="chessboard" select="$chessboard"/>
				<xsl:with-param name="row-number" select="$row-number - 1"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
<!-- ****************************************************** -->
<!--  Recursive template: draws a specified board cell and  -->
<!--  calls itself for the next cell.                       -->
<!--  Parameters:                                           -->
<!--    $row-number - curent row;                          -->
<!--    $col-number - curent column;                       -->
<!--    $row-content - curent state of figures in the row. -->
<!-- ****************************************************** -->

	<xsl:template name="render-chessboard-cell">
		<xsl:param name="col-number" select="1"/>
		<xsl:param name="row-number"/>
		<xsl:param name="row-content"/>
		<xsl:variable name="cell-background">
			<xsl:choose>
				<xsl:when test="($col-number mod 2 = 0 and $row-number mod 2 = 0) or 

                        ($col-number mod 2 != 0 and $row-number mod 2 != 0)">
					<xsl:value-of select="'b'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'w'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="cell-content" select="substring($row-content, $col-number, 1)"/>
		<fo:table-cell>
			<xsl:if test="$cell-background = 'b'">
				<xsl:attribute name="background-color">#C0C0C0</xsl:attribute>
			</xsl:if>
			<fo:block>
				<xsl:if test="$cell-content != 'x'">
					<xsl:call-template name="piece-image">
						<xsl:with-param name="piece-name" select="$cell-content"/>
						<xsl:with-param name="cell-background" select="$cell-background"/>
					</xsl:call-template>
				</xsl:if>
			</fo:block>
		</fo:table-cell>
		<xsl:if test="$col-number &lt; 8">
<!-- recurse to the next cell -->

			<xsl:call-template name="render-chessboard-cell">
				<xsl:with-param name="row-number" select="$row-number"/>
				<xsl:with-param name="col-number" select="$col-number + 1"/>
				<xsl:with-param name="row-content" select="$row-content"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
<!-- ****************************************************** -->
<!--  Produce a reference to the image of the figure.       -->
<!--  Parameters:                                           -->
<!--    $piece-name - piece letter code;                    -->
<!--    $cell-background = b | w - cell color.              -->
<!-- The image is taken from a chess font, referenced by    -->
<!-- its Unicode value (yes, there are chess figures in the -->
<!-- Unicode!)                                              -->
<!-- ****************************************************** -->

	<xsl:template name="piece-image">
		<xsl:param name="piece-name"/>
		<xsl:param name="cell-background"/>
		<xsl:choose>
			<xsl:when test="$piece-name='K'">&#x2654;</xsl:when>
			<xsl:when test="$piece-name='Q'">&#x2655;</xsl:when>
			<xsl:when test="$piece-name='R'">&#x2656;</xsl:when>
			<xsl:when test="$piece-name='B'">&#x2657;</xsl:when>
			<xsl:when test="$piece-name='N'">&#x2658;</xsl:when>
			<xsl:when test="$piece-name='P'">&#x2659;</xsl:when>
			<xsl:when test="$piece-name='k'">&#x265A;</xsl:when>
			<xsl:when test="$piece-name='q'">&#x265B;</xsl:when>
			<xsl:when test="$piece-name='r'">&#x265C;</xsl:when>
			<xsl:when test="$piece-name='b'">&#x265D;</xsl:when>
			<xsl:when test="$piece-name='n'">&#x265E;</xsl:when>
			<xsl:when test="$piece-name='p'">&#x265F;</xsl:when>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>