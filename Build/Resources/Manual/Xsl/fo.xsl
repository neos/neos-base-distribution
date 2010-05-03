<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:fo="http://www.w3.org/1999/XSL/Format"
  xmlns:s6hl="http://net.sf.xslthl/ConnectorSaxon6"
  xmlns:saxon6="http://icl.com/saxon"
  xmlns:xslthl="http://xslthl.sf.net"
  extension-element-prefixes="s6hl xslthl"
  exclude-result-prefixes="xslthl">

  <!-- import Docbook XSL stylesheet -->
  <xsl:import href="{DocBookXSLNSPath}Resources/Private/XSL/fo/docbook.xsl"/>
  <xsl:import href="{DocBookXSLNSPath}Resources/Private/XSL/highlighting/common.xsl"/>
  <xsl:import href="{DocBookXSLNSPath}Resources/Private/XSL/fo/highlight.xsl"/>

  <!-- adjust properties - see http://www.docbook.org/tdg/en/html/docbook.html for a reference-->
  <xsl:param name="paper.type" select="'A4'"/>
  <xsl:param name="line-height">150%</xsl:param>
  <xsl:param name="fop1.extensions" select="1"/>
  <xsl:param name="section.autolabel" select="1"/>
  <xsl:param name="generate.chapter.toc" select="0"/>
  <xsl:param name="body.font.family">sans-serif</xsl:param>
  <xsl:param name="dingbat.font.family">sans-serif</xsl:param>
  <xsl:param name="title.font.family">Share-Regular</xsl:param>

  <xsl:param name="admon.graphics" select="1"/>
  <xsl:param name="admon.graphics.path">{DocBookXSLNSPath}Resources/Private/XSL/images/</xsl:param>
  <xsl:param name="admon.graphics.extension">.svg</xsl:param>
  <xsl:param name="callout.graphics.path">{DocBookXSLNSPath}Resources/Private/XSL/images/callouts/</xsl:param>

  <!-- The header file which is used -->
  <xsl:param name="header.image.filename">{DocToolsPath}Resources/Private/Images/f3_logo.svg</xsl:param>

  <!-- Make the variablelists render like definition lists in HTML -->
  <xsl:param name="variablelist.term.break.after" select="1"/>
  <xsl:param name="variablelist.as.blocks" select="1"></xsl:param>

  <!-- Define the layout of the caption of tables/examples and figures-->
  <xsl:attribute-set name="formal.title.properties">
    <xsl:attribute name="font-size">80%</xsl:attribute>
    <xsl:attribute name="font-family">Share-Regular</xsl:attribute>
    <xsl:attribute name="hyphenate">false</xsl:attribute>
    <xsl:attribute name="space-after.minimum">0.4em</xsl:attribute>
    <xsl:attribute name="space-after.optimum">0.6em</xsl:attribute>
    <xsl:attribute name="space-after.maximum">0.8em</xsl:attribute>
    <xsl:attribute name="keep-together.within-column">always</xsl:attribute>
  </xsl:attribute-set>

  <!-- Define vertical placement of titles -->
  <xsl:param name="formal.title.placement">
    figure after
    example after
    table after
    procedure after
  </xsl:param>

  <xsl:attribute-set name="admonition.title.properties">
    <xsl:attribute name="font-size">14pt</xsl:attribute>
    <xsl:attribute name="font-family">Share-Regular</xsl:attribute>
    <xsl:attribute name="hyphenate">false</xsl:attribute>
    <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="formal.object.properties">
    <xsl:attribute name="keep-together.within-column">always</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="list.item.spacing">
    <xsl:attribute name="space-before.optimum">0.5em</xsl:attribute>
    <xsl:attribute name="space-before.minimum">0.3em</xsl:attribute>
    <xsl:attribute name="space-before.maximum">0.7em</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="normal.para.spacing">
    <xsl:attribute name="space-before.optimum">0.5em</xsl:attribute>
    <xsl:attribute name="space-before.minimum">0.3em</xsl:attribute>
    <xsl:attribute name="space-before.maximum">0.7em</xsl:attribute>
  </xsl:attribute-set>

  <!-- Programlisting configuration -->
  <xsl:attribute-set name="monospace.verbatim.properties">
    <!-- The line below enables the Share-TechMono font for program listings
         but breaks syntax highlighting (font has no bold/italic) -->
    <!-- xsl:attribute name="font-family">Share-TechMono</xsl:attribute -->
    <xsl:attribute name="font-family">FreeMono</xsl:attribute>
    <xsl:attribute name="font-size">85%</xsl:attribute>
    <xsl:attribute name="line-height">normal</xsl:attribute>
  </xsl:attribute-set>

  <!-- Image configuration -->
  <xsl:attribute-set name="figure.properties">
    <xsl:attribute name="text-align">center</xsl:attribute>
  </xsl:attribute-set>


  <!-- Header configuration -->
  <!-- No ruler below the header -->
  <xsl:param name="header.rule">0</xsl:param>
  <!-- template to overrule the default header content. -->
  <xsl:template name="header.content">
    <xsl:param name="pageclass" select="''"/>
    <xsl:param name="sequence" select="''"/>
    <xsl:param name="position" select="''"/>
    <xsl:param name="gentext-key" select="''"/>

    <!-- sequence can be odd, even, first, blank -->
    <!-- position can be left, center, right -->
    <fo:block>
    <xsl:choose>
      <xsl:when test="$sequence = 'blank'">
        <!-- nothing -->
      </xsl:when>

      <xsl:when test="$position='left'">
        <!-- Same for odd, even, empty, and blank sequences -->
          <xsl:if test="$pageclass != 'titlepage'">
            <fo:block space-before="-0.5em">
	    <fo:external-graphic content-height="0.6cm">
              <xsl:attribute name="src">
              <xsl:call-template name="fo-external-image">
                <xsl:with-param name="filename" select="$header.image.filename"/>
              </xsl:call-template>
            </xsl:attribute>
          </fo:external-graphic>
          </fo:block>
        </xsl:if>
      </xsl:when>

      <xsl:when test="$position='center'">
        <!-- nothing for empty and blank sequences -->
      </xsl:when>

      <xsl:when test="$position='right'">
        <!-- Same for odd, even, empty, and blank sequences -->
      </xsl:when>

      <xsl:when test="$sequence = 'first'">
        <!-- nothing for first pages -->
      </xsl:when>

      <xsl:when test="$sequence = 'blank'">
        <!-- nothing for blank pages -->
      </xsl:when>
    </xsl:choose>
  </fo:block>

  </xsl:template>

  <!-- enable syntax highlighting -->
  <xsl:param name="highlight.source" select="1" />
  <xsl:param name="highlight.default.language" select="php" />
  <saxon6:script implements-prefix="s6hl" language="java"
    src="java:net.sf.xslthl.ConnectorSaxon6" />

  <!-- add colors to highlighting -->
  <xsl:template match='xslthl:keyword' mode="xslthl">
    <fo:inline color="#007700"><xsl:apply-templates mode="xslthl"/></fo:inline>
  </xsl:template>
  <xsl:template match='xslthl:string' mode="xslthl">
    <fo:inline color="#dd0000"><xsl:apply-templates mode="xslthl"/></fo:inline>
  </xsl:template>
  <xsl:template match='xslthl:number' mode="xslthl">
    <fo:inline color="#0000bb"><xsl:apply-templates mode="xslthl"/></fo:inline>
  </xsl:template>

  <xsl:template match='xslthl:comment' mode="xslthl">
    <fo:inline color="#ff8000"><xsl:apply-templates mode="xslthl"/></fo:inline>
  </xsl:template>
  <xsl:template match='xslthl:doccomment' mode="xslthl">
    <fo:inline color="#ff8000"><xsl:apply-templates mode="xslthl"/></fo:inline>
  </xsl:template>

  <xsl:template match='xslthl:tag' mode="xslthl">
    <fo:inline font-weight="bold"><xsl:apply-templates mode="xslthl"/></fo:inline>
  </xsl:template>
  <xsl:template match='xslthl:attribute' mode="xslthl">
    <fo:inline color="#007700"><xsl:apply-templates mode="xslthl"/></fo:inline>
  </xsl:template>
  <xsl:template match='xslthl:value' mode="xslthl">
    <fo:inline color="#dd0000"><xsl:apply-templates mode="xslthl"/></fo:inline>
  </xsl:template>

</xsl:stylesheet>
