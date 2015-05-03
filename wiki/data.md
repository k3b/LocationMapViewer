# Data Formats

<a name='toc'>Data Formats Table of content</a>

* Introduction
	* [region of the world](data#region)
	* [point-of-interest](data#marker)
* [dataformats](data#dataformats)
	* [Geo uri](data#geo)
	* [gpx-1.0 and gpx-1.1](data#gpx)
	* [kml-2.2](data#kml)
	* [poi](data#poi)
* [Data Fields](data#data)
	* [ll=lat,lon](data#ll) : Latitude(north), Longitude(east)
	* [n=name](data#n) : name, title or caption of the point
	* [d=description](data#d) : Information about the point.
	* [z=zoomLevel](data#z) : how much detail should be shown
	* [z2=ZoomMax](data#z2) : (not used yet. reserved for zoom intervalls)
	* [link](data#link) : url pointing to additional informations
	* [s=symbol](data#s) : symbol-id or url pointing to a symbol, preview-image, icon
	* [id=id](data#id) : unique number or value identifying the point.
	* [t=time](data#t) : that relates to the point
	* [geoUri](data#geoUri) : (xml only) contains info in geo-uri format
	* [infer=1](data#infer) : (xml with geoUri only) try to infer lat,lon and name from description

## Introduction

LocationMapViewer's main purpose is to show a region of the world in a [map](map)
with points-of-interest.

A **<a name='region'>region of the world</a>** usually has a

* geographic coordinate that defines the center of the region as [latitude-north and longitude-east](data#ll).
* [zoomlevel](data#z) that defines how big the visible part of the world is. With zoomlevel 1 you can see all continents. With zoomlevel 6 you see one country. With zoomlevel 13 you will see streets.
* optionally a [name](data#n) that can become a title if you [bookmark](map#bookmark) a region.

A **<a name='marker'>point-of-interest</a>** or poi-marker usually has a

* geographic coordinate in the [map](map) as [latitude-north and longitude-east](data#ll)
* optionally a [description](data#d) that will be displayed in the [bubble](map#bubble) shown when the user taps on a [poi-marker](map#marker).
* optionally a [name](data#n) that can become a title or caption in the [poi-marker](map#marker) or [bubble](map#bubble).
* optionally a [link](data#link) with a url pointing to additional informations. The [bubble](map#bubble) gets a [">" button](map#link) if it has a url. Tabbing on this button opens the url.
* optionally a [symbol](data#s) with a png or jpg icon url to be displayed in the [poi-marker](map#marker) or in the [bubble](map#bubble).

There are several possible <a name='formats'>dataformats</a> to tell LocationMapViewer [what visible region](data#region) to display
and to describe [additional poi details](data#marker)

* [geo uri](data#geo)-s
* [gpx-1.0](data#gpx10) and [gpx-1.1](data#gpx) files
* [kml-2.2](data#kml) files used by google
* [poi](data#poi) files, k3b-s internal xml format

Here is a short introduction of the supported formats. You can find a more detailed description in the [fields section](data#data).

These formats are used in the [intent api](api#intent) to control LocationMapViewer.

### <a name='geo'>Geo uri</a> with k3b-s extensions

A [geo uri](http://tools.ietf.org/html/draft-mayrhofer-geo-uri-00) describes a [visible region](data#region) to display or a [geografic point of interest](data#marker)

LocationMapViewer displays the geo uri if it receives [android.intent.action.VIEW](api#intent) or [android.intent.action.PICK](api#intent)

In general a geo uri looks like this

* geo:lat,lon?q=(name)&z=zoomLevel&z2=ZoomMax&link=link&s=symbol&d=description&id=id&t=time
* geo:0,0?q=lat,lon(name)&z=zoomLevel&z2=zoomMax&link=link&s=symbol&d=description&id=id&t=time

Example:

* geo:0,0?q=52.366667,9.716667(Hannover)

The same way as a webbrowser can interprete a url like *http://github.com/* LocationMapViewer and other apps
can interprete [geo:0,0?q=52.366667,9.716667(Hannover)](geo:0,0?q=52.366667,9.716667(Hannover)) .

If a user clicks on a geo uri in an android web browser the browser forwards this to a geo handler like LocationMapViewer.

Currently a pc web-browser can handle "http:", "https:", "file:" urls but not "geo:" .

Since LocationMapViewer needs more than the current [geo uri standard defines](http://tools.ietf.org/html/draft-mayrhofer-geo-uri-00)
there are some LocationMapViewer-specific additions to the format. For a complete list see [fields section](data#data).

**Note:** uri field content

* it **can** be url-encoded, but it is not neccessary.
* it **must** be url-encoded if the field contain url-special-chars **?** or **&**

In other words

* geo:...&d=hello+world&...and
* geo:...&d=hello%20world&.. are valid geo uris while
* geo:...&d=hello world&...is not a valid uri but LocationMapViewer understands it.

Note: Currently GoogleMaps for android supports "geo:0,0?q=52.366667,9.716667(Hannover)" but not "geo:52.366667,9.716667?q=(Hannover)"

See also: gero-uri junit-tests

* /k3b-geoHelper/src/test/java/de.k3b.geo.io.[GeoUriTests.java](https://github.com/k3b/LocationMapViewer/blob/master/k3b-geoHelper/src/test/java/de/k3b/geo/io/GeoUriTests.java)

### <a name='gpx'>gpx-1.0 and gpx-1.1</a> xml file format

[gpx-1.0](http://www.topografix.com/GPX/1/0/) and [gpx-1.1](http://www.topografix.com/GPX/1/1/) is a
standard xml fileformat to describe one ore more [geografic points of interest](data#marker).

LocationMapViewer will open and display gpx content if

* a local file has the file extension **".gpx"**
* an internet link has the mime-type **"application/xml+gpx"**

In general gpx xml looks like this:

* &lt;gpx version="1.0" ...>&lt;wpt lat="lat" lon="lon">&lt;name>name&lt;/name>....&lt;/wpt>&lt;/gpx>
* &lt;gpx version="1.1" ...>&lt;trk>&lt;trkseg>&lt;trkpt lat="lat" lon="lon">&lt;name>name&lt;/name>....

LocationMapViewer will create a poi-marker for every gpx **wpt** or **trkpt** element.
For a complete list see [fields section](data#data).

See also gpx junit-regression-testdata

* /k3b-geoHelper/src/test/resources/de/k3b/geo/io/regressionTests/[gpx10.gpx](https://github.com/k3b/LocationMapViewer/blob/master/k3b-geoHelper/src/test/resources/de/k3b/geo/io/regressionTests/gpx10.gpx) and
* /k3b-geoHelper/src/test/resources/de/k3b/geo/io/regressionTests/[gpx11.gpx](https://github.com/k3b/LocationMapViewer/blob/master/k3b-geoHelper/src/test/resources/de/k3b/geo/io/regressionTests/gpx11.gpx)

### <a name='kml'>kml-2.2</a> xml file format

[kml](https://developers.google.com/kml/documentation/kmlreference?csw=1) is the xml fileformat that
is used by google.

LocationMapViewer will open and display kml content if

* a local file has the file extension **".kml"**
* an internet link has the mime-type **"application/xml+kml"** or **""application/vnd.google-earth.kml+xml""**

In general kml looks like this

* &lt;kml>...&lt;Placemark>...&lt;coordinates>lon,lat&lt;/coordinates>...&lt;name>name&lt;/name>....&lt;/kml>

LocationMapViewer will create a poi-marker for every kml **Placemark** element.

For a complete list see [fields section](data#data).

See also kml junit-regression-testdata

* /k3b-geoHelper/src/test/resources/de/k3b/geo/io/regressionTests/[kml22.kml](https://github.com/k3b/LocationMapViewer/blob/master/k3b-geoHelper/src/test/resources/de/k3b/geo/io/regressionTests/kml22.kml).

### k3b-s internal <a name='poi'>poi</a> xml format

The LocationMapViewer internally uses its own **poi xml format** to define a [visible region](data#region) and to describe [poi-marker details](data#marker).

Most [geo uri](data#geo) parameters correspond to a xml-attribute with the same name.

LocationMapViewer will open and display poi content if

* a local file has the file extension **".poi"**
* an internet link has the mime-type **"application/xml+poi"**

In general poi looks like this

* &lt;poi ll='lat,lon' n='name' z='zoomLevel' z2='zoomMax' link='link' s='symbol' d='description' id='id' t='time' geoUri='geoUri' />

LocationMapViewer will create a poi-marker for every **poi** element.
For a complete list see [fields section](data#data).

See also poi junit-regression-testdata

* /k3b-geoHelper/src/test/resources/de/k3b/geo/io/regressionTests/[poi.xml](https://github.com/k3b/LocationMapViewer/blob/master/k3b-geoHelper/src/test/resources/de/k3b/geo/io/regressionTests/poi.xml).

## <a name='data'>Data Fields</a>

This paragraph lists the fields currently supported by LocationMapViewer.

It contains an example for each [dataformat](data#dataformats) that supports the current field.

### <a name='ll'>ll=lat,lon : Latitude(north), Longitude(east)</a>
* [geo:](data#geo)179.345,-86.7890123?...
* [geo:](data#geo)0,0?q=179.345,-86.7890123&...
* [&lt;gpx](data#gpx) version="1.0" >...&lt;wpt lat="179.345" lon="-86.7890123">...
* [&lt;gpx](data#gpx) version="1.1" >...&lt;trkpt lat="179.345" lon="-86.7890123">...
* [&lt;kml](data#kml)>...&lt;coordinates>-86.7890123,179.345&lt;/coordinates>...
* [&lt;poi](data#poi) ll='179.345,-86.7890123' ... />

This field is used to define

* map´s [visible region](data#region)
* a [map´s bookmark](map#bookmark)
* a [point-of-interest](data#marker) in the [map](map#marker).

note: GoogleMaps on android only understands "[geo:](data#geo)0,0?q=..."

### <a name='n'>n=name : name, title or caption of the point</a>
* [geo:](data#geo)0,0?q=(Name of the point)&...
* [geo:](data#geo)0,0?q=(Name+of+the+point)&...
* [geo:](data#geo)0,0?q=179.345,-86.7890123(Name of the point)&...
* [geo:](data#geo)?...&n=Name of the point&...
* [geo:](data#geo)?...&n=Name+of+the+point&...
* [geo:](data#geo)?...&n=Name%20of%20the%20point&...
* [&lt;gpx](data#gpx)>...&lt;name>name&lt;/name>...
* [&lt;kml](data#kml)>...&lt;name>name&lt;/name>...
* [&lt;poi](data#poi) n='Name of the point' ... />

This field is used to define

* a [bookmark](data#region) in the [map](map#bookmark)
* a [point-of-interest](data#marker) to be displayed as the title of the [map's bubble](map#bubble).

note: GoogleMaps on android only understands "[geo:](data#geo)0,0?q=179.345,-86.7890123(Name of the point)"

### <a name='d'>d=description : Information about the point</a>.
* [geo:](data#geo)...&d=Some Text that describes the point&...
* [geo:](data#geo)...&d=Some+Text+that*describes+the+point&...
* [geo:](data#geo)...&d=Some%20Text%20that%20describes%20the%20point&...
* [&lt;gpx](data#gpx)>...&lt;description>Some Text that describes the point&lt;/description>...
* [&lt;kml](data#kml)>...&lt;description>Some Text that describes the point&lt;/description>...
* [&lt;poi](data#poi) d='Some Text that describes the point' ... />

This field is used to define

* a [point-of-interest](data#marker) to be displayed as the main text of the [map's bubble](map#bubble).

### <a name='z'>z=zoomLevel : how much detail should be shown</a>
* [geo:](data#geo)...&z=8&...
* [&lt;poi](data#poi) z='8' ... />

Value 0..18 or -1 if there is no value

* 1=World
* 3=Continent (ie Africa)
* 6=Country (ie Germany)
* 18=MostDetailed.
* -1=no zoomlevel

This field is used to define

* the [visible region](data#region) as the [map's zoomlevel](map#z) and [bookmark's zoomlevel](map#bookmark)

### <a name='z2'>z2=ZoomMax</a> : (not used yet. reserved for zoom intervalls)
* [geo:](data#geo)...&z=8&z2=12...
* [&lt;poi](data#poi) z='8' z2='12' ... />

### <a name='link'>link=link : url pointing to additional informations</a>
* [geo:](data#geo)...&link=http://link/to/some/page.htm&...
* [&lt;gpx](data#gpx) version="1.0" >...&lt;url>http://link/to/some/page.htm&lt;/url>...
* [&lt;gpx](data#gpx) version="1.1" >...&lt;link href='http://link/to/some/page.htm' />...
* [&lt;kml](data#kml)>...&lt;atom:link href="http://link/to/some/page.htm" />...
* [&lt;poi](data#poi) link='file://sdcard/copy/test.htm' ... />

any kind of url is possible. ie:

* http: and https:  for internet links
* file: for local files
* content: android specific content uri (ie for media items)
* intent: url to android specific activity
* [geo:](data#geo) url to a point in an other map/zoomlevel

#### !!! todo intent example to open lmv-settings

This field is used to define

* a [point-of-interest](data#marker) as the link button in the [map's bubble](map#bubble).

### <a name='s'>s=symbol : symbol-id or url pointing to a symbol, preview-image, icon</a>
* [geo:](data#geo)...&link=http://link/to/some/image.png&...
* [&lt;poi](data#poi) link='file://sdcard/copy/test.jpg' ... />

any kind of url is possible.

If the symbol contains a colon ":" it is interpreted as a url. else as an id.

### <a name='id'>id=id : unique number or value identifying the point</a>.
* [geo:](data#geo)...&id=42&...
* [&lt;poi](data#poi) id='x97' ... />
* [&lt;poi](data#poi) ... >&lt;id>x97&lt;/id></poi>

### <a name='t'>t=time : that relates to the point</a>
* [geo:](data#geo)...&t=2015-02-10T08:04:45Z&...
* [&lt;gpx](data#gpx)>...&lt;time>2015-02-10T08:04:45.000Z&lt;/time>...
* [&lt;kml](data#kml)>...&lt;when>2015-02-10T08:04:45.000Z&lt;/when>...
* [&lt;poi](data#poi) t='2015-02-10T08:04:45Z' ... />

time is in iso date format ISO8601.

example: if the poi represents a foto then the time corresponds to "when the photo was taken".

For a list of different supported time formats see : time-parser junit-tests

* /k3b-geoHelper/src/test/java/de.k3b.util[IsoDateTimeParserTests.java](https://github.com/k3b/LocationMapViewer/blob/master/k3b-geoHelper/src/test/java/de/k3b/util/IsoDateTimeParserTests.java)

### <a name='geoUri'>geoUri=geoUri : (xml only) contains info in geo-uri format</a>
* [&lt;poi](data#poi) geoUri='geo:0,0?q=179.345,-86.7890123(Name of the point)' />

This way you can use [geo uris](data#geo) in xml.

Note: Since **"&"** has a special meaning in xml it must be escaped using **"& amp;"**

### <a name='infer'>infer=1 : (xml with geoUri only) try to infer lat,lon and name from description</a>
* [&lt;poi](data#poi) geoUri='geo:?d=Text containing (Name of the point) with a loc 179.345,-86.7890123' />

the uri parser can try to find [name](data#n) and [LatLon](data#ll) from other fields this way.

