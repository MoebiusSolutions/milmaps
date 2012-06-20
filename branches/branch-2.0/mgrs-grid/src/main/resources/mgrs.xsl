<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	version="1.0"
>
    <xsl:param name="mgrs-url">http://localhost:8080/pli-service/rs/mgrs/kml</xsl:param>

    <xsl:template match="/">
        <kml xmlns="http://earth.google.com/kml/2.2">
            <Document>
                <name>MGRS</name>
                <NetworkLink>
                    <name>MGRS Gridlines</name>
                    <open>1</open>
                    <Link>
                        <href>
                            <xsl:value-of select="$mgrs-url"/>
                        </href>
                        <viewFormat>BBOX=[bboxWest],[bboxSouth],[bboxEast],[bboxNorth]&amp;LOOKAT=[lookatLon],[lookatLat],[lookatRange],[lookatTilt],[lookatHeading],[horizFov],[vertFov]</viewFormat>
                        <viewRefreshMode>onStop</viewRefreshMode>
                        <viewRefreshTime>0</viewRefreshTime>
                    </Link>
                </NetworkLink>
            </Document>
        </kml>
    </xsl:template>

</xsl:stylesheet>
