<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:java="http://xml.apache.org/xalan/java"
	exclude-result-prefixes="java"
	version="1.0"
>
    <xsl:param name="span"/>
    
    <xsl:template match="MGRS">
        <kml xmlns="http://earth.google.com/kml/2.0">
            <Document>
                <Placemark>
                    <name>Span</name>
                    <description>
                        <xsl:value-of select="$span"/>
                    </description>
                </Placemark>

                <xsl:choose>
       
                    <xsl:when test="$span &gt; 125">
                        <Folder>
                            <name>Please zoom in to plot gridlines</name>
                            <visibility>1</visibility>
                        </Folder>
                    </xsl:when>

                    <xsl:otherwise>
                        <xsl:apply-templates select="ZoneGrid"/>
                        <xsl:apply-templates select="HundredKmGrid"/>
                        <xsl:apply-templates select="TenKmGrid"/>
                        <Style id="zoneLabel">
                            <IconStyle>
                                <scale>0</scale>
                            </IconStyle>
                            <LabelStyle>
                                <scale>1.5</scale>
                                <color>ff00ffff</color>
                            </LabelStyle>
                        </Style>
                        <Style id="hundredKmLabel">
                            <IconStyle>
                                <scale>0</scale>
                            </IconStyle>
                            <LabelStyle>
                                <scale>0.8</scale>
<!--                                <color>ff0000ff</color>-->
                            </LabelStyle>
                        </Style>

                        <Folder>
                            <name>Zone Labels</name>
                            <visibility>1</visibility>
                            <xsl:for-each select="ZoneGrid/LatLines/Line">
                                <Placemark>
                                    <name>
                                        <xsl:value-of select="@lblName"/>
                                    </name>
                                    <Point>
                                        <coordinates>
                                            <xsl:value-of select="@lblPnt"/>,25
                                        </coordinates>
                                    </Point>
                                    <styleUrl>#zoneLabel</styleUrl>
                                </Placemark>
                            </xsl:for-each>
                        </Folder>

                        <xsl:if test="$span &lt;= 15">
                            <Folder>
                                <name>100Km Labels</name>
                                <visibility>1</visibility>
                                <xsl:for-each select="HundredKmGrid/LatLines/Line">
                                    <Placemark>
                                        <name>
                                            <xsl:value-of select="@lblName"/>
                                        </name>
                                        <Point>
                                            <coordinates>
                                                <xsl:value-of select="@lblPnt"/>,25
                                            </coordinates>
                                        </Point>
                                        <styleUrl>#hundredKmLabel</styleUrl>
                                    </Placemark>
                                </xsl:for-each>
                            </Folder>
                        </xsl:if>
                    </xsl:otherwise>
                </xsl:choose>
            </Document>
        </kml>
    </xsl:template>

    <xsl:template match="ZoneGrid">
        <Style id="zoneGrid">
            <LineStyle>
                <color>ff00ffff</color>
                <width>2</width>
            </LineStyle>
        </Style>
        <Placemark>
            <name>Zones</name>
            <visibility>1</visibility>
            <styleUrl>#zoneGrid</styleUrl>
            <MultiGeometry>
                <xsl:for-each select="LatLines/Line">
                    <LineString>
                        <altitudeMode>clampedToGround</altitudeMode>
                        <tessellate>1</tessellate>
                        <coordinates>
                            <xsl:value-of select="@start"/>,25
                            <xsl:value-of select="@end"/>,25
                        </coordinates>
                    </LineString>
                </xsl:for-each>
                <xsl:for-each select="LngLines/Line">
                    <LineString>
                        <altitudeMode>clampedToGround</altitudeMode>
                        <tessellate>1</tessellate>
                        <coordinates>
                            <xsl:value-of select="@start"/>,25
                            <xsl:value-of select="@end"/>,25
                        </coordinates>
                    </LineString>
                </xsl:for-each>
            </MultiGeometry>
        </Placemark>
    </xsl:template>

    <xsl:template match="HundredKmGrid">
        <Style id="hundredKmGrid">
            <LineStyle>
                <color>ff0000ff</color>
                <width>2</width>
            </LineStyle>
        </Style>
        <Placemark>
            <name>100km Grid</name>
            <visibility>1</visibility>
            <styleUrl>#hundredKmGrid</styleUrl>
            <MultiGeometry>
                <xsl:for-each select="LatLines/Line">
                    <LineString>
                        <altitudeMode>clampedToGround</altitudeMode>
                        <tessellate>1</tessellate>
                        <coordinates>
                            <xsl:value-of select="@start"/>,25
                            <xsl:value-of select="@end"/>,25
                        </coordinates>
                    </LineString>
                </xsl:for-each>
                <xsl:for-each select="LngLines/Line">
                    <LineString>
                        <altitudeMode>clampedToGround</altitudeMode>
                        <tessellate>1</tessellate>
                        <coordinates>
                            <xsl:value-of select="@start"/>,25
                            <xsl:value-of select="@end"/>,25
                        </coordinates>
                    </LineString>
                </xsl:for-each>
            </MultiGeometry>
        </Placemark>
    </xsl:template>

    <xsl:template match="TenKmGrid">
        <Style id="tenKmGrid">
            <LineStyle>
                <color>7f00ff00</color>
                <width>2</width>
            </LineStyle>
        </Style>
        <Placemark>
            <name>10km Grid Lines</name>
            <visibility>1</visibility>
            <styleUrl>#tenKmGrid</styleUrl>
            <MultiGeometry>
                <xsl:for-each select="LatLines/Line">
                    <LineString>
                        <altitudeMode>clampedToGround</altitudeMode>
                        <tessellate>1</tessellate>
                        <coordinates>
                            <xsl:value-of select="@start"/>,25
                            <xsl:value-of select="@end"/>,25
                        </coordinates>
                    </LineString>
                </xsl:for-each>
                <xsl:for-each select="LngLines/Line">
                    <LineString>
                        <altitudeMode>clampedToGround</altitudeMode>
                        <tessellate>1</tessellate>
                        <coordinates>
                            <xsl:value-of select="@start"/>,25
                            <xsl:value-of select="@end"/>,25
                        </coordinates>
                    </LineString>
                </xsl:for-each>
            </MultiGeometry>
        </Placemark>
    </xsl:template>

</xsl:stylesheet>
