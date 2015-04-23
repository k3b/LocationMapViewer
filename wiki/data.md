# Data Formats

the geo information can have one of these formats

#### !!! todo link to gpx and kml spec
#### !!! todo link to regresseion test data

* [geo uri](http://tools.ietf.org/html/draft-mayrhofer-geo-uri-00) with k3b-s extensions
    * geo:lat,lon?q=(name)&z=zoomLevel&z2=ZoomMax&link=link&s=symbol&d=description&id=id&t=time
    * geo:0,0?q=lat,lon(name)&z=zoomLevel&z2=zoomMax&link=link&s=symbol&d=description&id=id&t=time
* standard gpx-1.0 or gpx-1.1 format
    * &lt;gpx version="1.0" ...>&lt;wpt lat="lat" lon="lon">&lt;name>name&lt;/name>....&lt;/wpt>&lt;/gpx>
    * &lt;gpx version="1.1" ...>&lt;trk>&lt;trkseg>&lt;trkpt lat="lat" lon="lon">&lt;name>name&lt;/name>....
    * every gpx **wpt** or **trkpt** element will create a new point in the map
* standard kml-2.2 format
    * &lt;kml>...&lt;Placemark>...&lt;coordinates>lon,lat&lt;/coordinates>...&lt;name>name&lt;/name>....&lt;/kml>
    * every kml **Placemark** element will create a new point in the map
* k3b-s internal xml format
    * &lt;poi ll='lat,lon' n='name' z='zoomLevel' z2='zoomMax' link='link' s='symbol' d='description' id='id' t='time' geoUri='geoUri' />
    * every **poi** element will create a new point in the map

**Note:** uri field content

* it **can** be url-encoded, but it is not neccessary.
* it **must** be url-encoded if the field contain url-special-chars **?** or **&**

**Note:** xml attribute content

* xml-special-chars **<**, **&** and **>** must be xml-escaped

## Data Fields

### <a name='ll'></>ll=lat,lon : Latitude(north), Longitude(east)
* geo:179.345,-86.7890123?...
* geo:0,0?q=179.345,-86.7890123&...
* &lt;gpx version="1.0" >...&lt;wpt lat="179.345" lon="-86.7890123">...
* &lt;gpx version="1.1" >...&lt;trkpt lat="179.345" lon="-86.7890123">...
* &lt;kml>...&lt;coordinates>-86.7890123,179.345&lt;/coordinates>...
* &lt;poi ll='179.345,-86.7890123' ... />

note: GoogleMaps on android only understands "geo:0,0?q=..."

### <a name='n'></>n=name : name, title or caption of the point
* geo:0,0?q=(Name of the point)&...
* geo:0,0?q=(Name+of+the+point)&...
* geo:0,0?q=179.345,-86.7890123(Name of the point)&...
* geo:?...&n=Name of the point&...
* geo:?...&n=Name+of+the+point&...
* geo:?...&n=Name%20of%20the%20point&...
* &lt;gpx>...&lt;name>name&lt;/name>...
* &lt;kml>...&lt;name>name&lt;/name>...
* &lt;poi n='Name of the point' ... />

note: GoogleMaps on android only understands "geo:0,0?q=179.345,-86.7890123(Name of the point)"

### <a name='d'></>d=description : Information about the point.
* geo:...&d=Some Text that describes the point&...
* geo:...&d=Some+Text+that*describes+the+point&...
* geo:...&d=Some%20Text%20that%20describes%20the%20point&...
* &lt;gpx>...&lt;description>Some Text that describes the point&lt;/description>...
* &lt;kml>...&lt;description>Some Text that describes the point&lt;/description>...
* &lt;poi d='Some Text that describes the point' ... />

### <a name='z'></>z=zoomLevel : how much detail should be shown
* geo:...&z=8&...
* &lt;poi z='8' ... />

Value 0..18 or -1 if there is no value

* 1=World
* 3=Continent (ie Africa)
* 6=Country (ie Germany)
* 18=MostDetailed.
* -1=no zoomlevel

### <a name='z2'></>z2=ZoomMax : (not used yet. reserved for zoom intervalls)
* geo:...&z=8&z2=12...
* &lt;poi z='8' z2='12' ... />

### <a name='link'></>link=link : url pointing to additional informations
* geo:...&link=http://link/to/some/page.htm&...
* &lt;gpx version="1.0" >...&lt;url>http://link/to/some/page.htm&lt;/url>...
* &lt;gpx version="1.1" >...&lt;link href='http://link/to/some/page.htm' />...
* &lt;kml>...&lt;atom:link href="http://link/to/some/page.htm" />...
* &lt;poi link='file://sdcard/copy/test.htm' ... />

any kind of url is possible. ie:

* http: and https:  for internet links
* file: for local files
* content: android specific content uri (ie for media items)
* intent: url to android specific activity
* geo: url to a point in an other map/zoomlevel

#### !!! todo intent example to open lmv-settings

### <a name='s'></>s=symbol : symbol-id or url pointing to a symbol, preview-image, icon
* geo:...&link=http://link/to/some/image.png&...
* &lt;poi link='file://sdcard/copy/test.jpg' ... />

any kind of url is possible.

If the symbol contains a colon ":" it is interpreted as a url. else as an id.

### <a name='id'></>id=id : unique number or value identifying the point.
* geo:...&id=42&...
* &lt;poi id='x97' ... />

### <a name='t'></>t=time : that relates to the point
* geo:...&t=2015-02-10T08:04:45Z&...
* &lt;gpx>...&lt;time>2015-02-10T08:04:45.000Z&lt;/time>...
* &lt;kml>...&lt;when>2015-02-10T08:04:45.000Z&lt;/when>...
* &lt;poi t='2015-02-10T08:04:45Z' ... />

time is in iso date format ISO8601.

example: if the poi represents a foto then the time corresponds to "when the photo was taken".

#### !!! todo reference to examples in unittests

### <a name='geoUri'></>geoUri=geoUri : xml contains info in geo-uri format
* &lt;poi geoUri='geo:0,0?q=179.345,-86.7890123(Name of the point)' />

### <a name='infer'></>infer=1 : xml mit geoUri= try to infer lat,lon and name from description
* &lt;poi geoUri='geo:?d=Text containing (Name of the point) with a loc 179.345,-86.7890123' />

#### !!! hier gehts weiter. alter content
