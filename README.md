#LocationMapViewer

An app that can display geografic info in a map. <br/>

Other Android apps can use LocationMapViewer through an Intent interface.
(See GeoIntentDemo)<br/>

Minimal requirements: Android 2.1 (Eclair, API 7)<br/>
Licence: [GPLv3](http://www.gnu.org/licenses/gpl-3.0)<br/>
Requred permissions:

* INTERNET to download map data from Open Streetmap Server
* WRITE_EXTERNAL_STORAGE
    * to cache downloaded map data in local file system
    * to load gpx/kml-Files to be displayed in the map

The code uses [osmdroid library](https://github.com/osmdroid/osmdroid) to
display maps from [Open Street Map](http://www.openstreetmap.org)

LocationMapViewer is based on "org.osmdroid.samples.SampleWithMinimapItemizedoverlay"
from DemoApp "OsmDroid-OpenStreetMapViewer"

##Intent Interface

* uri=geo:...  displays (and zomms to) a [geo uri](http://tools.ietf.org/html/draft-mayrhofer-geo-uri-00)
    * geo:54.0,8.0?q=(Hello)
        * displays "hello" at the location lat=54.0 north and lon= 8.0 west
    * geo:0,0?q=54.0,8.0(Hello)
        * displays "hello" at the location lat=54.0 north and lon= 8.0 west
    * geo:54.0,8.0?z=6
        * displays a pin at the location using zoomlevel "6"
* uri=file:/path/to/waypoints.gpx
    * displays a xml+gpx waypointfile

