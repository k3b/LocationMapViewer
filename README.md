#LocationMapViewer

An app that can display geografic info in a map. It has support for **gpx** and **kml** files and **geo-uri-s**
and can work offline (without internet/wifi)
once geodata is downloaded and cached.

Other Android apps can use LocationMapViewer through an Intent interface.
(See GeoIntentDemo)<br/>

Minimal requirements: Android 2.1 (Eclair, API 7), internet/wifi-connection to download geodata and a SD-Card to cache geodata<br/>
Licence: [GPLv3](http://www.gnu.org/licenses/gpl-3.0)<br/>
Requred permissions:

* INTERNET: to download map data from Open Streetmap Server
* ACCESS_NETWORK_STATE and ACCESS_WIFI_STATE: to find out if wifi/internet is online to start downloaded geodata 
* WRITE_EXTERNAL_STORAGE
    * to cache downloaded map data in local file system
    * to load gpx/kml-Files to be displayed in the map
* ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION: to display my own location in the map, too

The code uses [osmdroid library](https://github.com/osmdroid/osmdroid) to
display maps from [Open Street Map](http://www.openstreetmap.org)

LocationMapViewer is based on "org.osmdroid.samples.SampleWithMinimapItemizedoverlay"
from DemoApp "OsmDroid-OpenStreetMapViewer"

##Intent Interface

Other Android apps can use LocationMapViewer as plug-in/device-driver through an Intent interface.
Examples:

* uri=geo:...  displays (and zomms to) a [geo uri](http://tools.ietf.org/html/draft-mayrhofer-geo-uri-00)
    * geo:54.0,8.0?q=(Hello)
        * displays "hello" at the location lat=54.0 north and lon= 8.0 east
    * geo:0,0?q=54.0,8.0(Hello)
        * displays "hello" at the location lat=54.0 north and lon= 8.0 east
    * geo:54.0,8.0?z=6
        * displays a pin at the location using zoomlevel "6"
* uri=file:/path/to/waypoints.gpx
    * displays a xml+gpx waypointfile
* uri=file:/path/to/waypoints.kml
    * displays a vnd.google-earth.kml+xml waypointfile

LocationMapViewer is designed to be used by other apps. This means in Terms of [GPLv3](http://www.gnu.org/licenses/gpl-3.0) that your app
that uses the Intent-Iterface [is not considered a Derived Work.](https://en.wikipedia.org/wiki/GPL_v3#Point_of_view:_linking_is_irrelevant)

In other words: you can used LocationMapViewer as a [driver for your non gpl/non opensource app.](http://www.rosenlaw.com/lj19.htm).
