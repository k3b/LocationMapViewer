# LocationMapViewer ![](https://github.com/k3b/LocationMapViewer/blob/master/LocationMapViewer/src/main/res/drawable/ic_launcher.png)

An Android app to display geographic information from url or local gpx/kml/kmz/zip file in a [map](map). 

It has support for **gpx**, **kml** and **kmz** files and **geo-uri-s**
and can work offline (without internet/wifi)
once geodata is downloaded and cached.

![](https://github.com/k3b/LocationMapViewer/blob/master/fastlane/metadata/android/en-US/images/phoneScreenshots/1-LocationMapViewer.png)

Other Android apps can use LocationMapViewer through an Intent interface
(see [GeoIntentDemo.java](https://github.com/k3b/LocationMapViewer/blob/master/geoIntentDemo/src/main/java/de/k3b/android/locationMapViewer/demo/GeoIntentDemoActivity.java) )
or through html links like &lt;a href=&quot;geo:0,0?q=53.0,8.0(Hello)&quot;&gt;geo:0,0?q=53.0,8.0(Hello)&lt;/a&gt;<br/>

## Minimal requirements: 

* version v0.3.5.170911(6) 
  * Android-2.3.7 - Android-7 (API 10ff)   
* version v0.3.7.210429 
  * Android-4.1 and later (API 16ff) 
  * compatible with android-10
* internet/wifi-connection to download geodata and a SD-Card to cache geodata<br/>

Licence: [GPLv3](http://www.gnu.org/licenses/gpl-3.0)<br/>

## Required permissions:

* INTERNET: to download map data from Open Streetmap Server
* ACCESS_NETWORK_STATE and ACCESS_WIFI_STATE: to find out if wifi/internet is online to start downloaded geodata 
* WRITE_EXTERNAL_STORAGE
    * to cache downloaded map data in local file system
    * to load gpx/kml-Files to be displayed in the map
* ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION: to display my own location in the map, too

The code uses [osmdroid library](https://github.com/osmdroid/osmdroid)
with [osmbonuspack library](http://code.google.com/p/osmbonuspack/)
to display maps from [Open Street Map](http://www.openstreetmap.org).

## Intent Interface

Other Android apps can use LocationMapViewer as plug-in/device-driver through an Intent interface
or through html-a-href-links.

Examples:

* uri=geo:...  displays (and zomms to) a [geo uri](http://tools.ietf.org/html/draft-mayrhofer-geo-uri-00)
    * geo:0,0?q=53.0,8.0(Hello)
        * displays "hello" at the location lat=53.0 north and lon= 8.0 east.
        * in html you can use &lt;a href=&quot;geo:0,0?q=53.0,8.0(Hello)&quot;&gt;geo:0,0?q=53.0,8.0(Hello)&lt;/a&gt;
        * this format is compatible with google-maps for android
    * geo:53.0,8.0?q=(Hello)
        * displays "hello" at the location lat=53.0 north and lon= 8.0 east
        * this format is **not** compatible with google-maps for android
    * geo:53.0,8.0?z=6
        * displays a pin at the location using zoomlevel "6"
* uri=file:/path/to/waypoints.gpx
    * displays a xml+gpx waypointfile
* uri=content:/path/to/waypoints.kml
    * displays a vnd.google-earth.kml+xml waypointfile

LocationMapViewer can handle local files that end with 

* .kml, .kml.zip, .kmz (kml in zip) 
* .gpx, .gpx.zip, .gpz (gpx in zip)
* .poi, .poi.zip, .poz (poi in zip)

LocationMapViewer can handle http(s): and content: urls with mime 

* application/vnd.google-earth.kml+xml
* application/vnd.google-earth.kmz
* application/xml+kml
* application/xml+gpx
* application/xml+poi
* application/zip+xml+kml
* application/zip+xml+gpx
* application/zip+xml+poi

LocationMapViewer is designed to be used by other apps. This means in Terms of [GPLv3](http://www.gnu.org/licenses/gpl-3.0) that your app
that uses the Intent-Iterface [is not considered a Derived Work.](https://en.wikipedia.org/wiki/GPL_v3#Point_of_view:_linking_is_irrelevant)

In other words: you can used LocationMapViewer as a [driver for your non gpl/non opensource app.](http://www.rosenlaw.com/lj19.htm).

For more informations see https://github.com/k3b/LocationMapViewer/wiki

-----

## Donations: 

If you like this app please consider to donating to https://wiki.openstreetmap.org/wiki/Donations .

Since android-developping is a hobby (and an education tool) i do not want any 
money for my apps so donation should go to projects I benefit from.
