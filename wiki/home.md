#LocationMapViewer ![](https://github.com/k3b/LocationMapViewer/blob/master/LocationMapViewer/src/main/res/drawable/ic_launcher.png)

An android app that can display geografic info in a [map](map).

![](https://github.com/k3b/LocationMapViewer/blob/master/wiki/png/app-map-menu.png)

## <a name='features'>Features:</a>

* Support for displaying [gpx](data#gpx), [kml](data#kml) or [poi](data#poi) files
    * Example: open a gpx or kml file in an android filemanager
    * Example: open a link to a gpx or kml file in an android webbrowser
* Support for [geo-uri-s](data#geo)
    * Example: open a link to a geo uri in an android webbrowser.
        * &lt;a href='geo:0,0?q=53.0,8.0(Hello)'><a href='geo:0,0?q=53.0,8.0(Hello)'>geo:0,0?q=53.0,8.0(Hello)</a> &lt;/a>
* Support for [geo-bookmarks](bookmarks)
    * to remember and quickly navigate to named latitude/longitude/zoomlevel
* Can work offline (without internet/wifi) once geodata is downloaded and cached.
* Other android apps can use LocationMapViewer through an [intent api](api#intent) ...
    * ... to show a map at certain [latitude longitude zoomlevel](data#region) with [points of interest](data#marker)
    * ... to pick a location from a map (i.e. "Where was this photo taken?")

### <a name='toc'>Table of content</a>

* The [map's user ingerface](map)
* [geo-bookmarks](bookmarks) to remember and quickly navigate to named latitude/longitude/zoomlevel
* Customizing LocationMapViewer: [settings](settings)
* Supported [dataformats](data)
* How other apps can use LocationMapViewer through the [intent api](api#intent)

### <a name='requirements'>Minimal requirements:</a>

* Android 2.1 (Eclair, API 7),
* internet/wifi-connection to download geodata and a SD-Card to cache geodata<br/>
* Licence: [GPLv3](http://www.gnu.org/licenses/gpl-3.0)<br/>

### <a name='permissions'>Requred permissions:</a>

* INTERNET: to download map data from Open Streetmap Server
* ACCESS_NETWORK_STATE and ACCESS_WIFI_STATE: to find out if wifi/internet is online to start downloading mapdata
* WRITE_EXTERNAL_STORAGE
    * to cache downloaded map data in local file system
    * to load gpx/kml-Files to be displayed in the map
* ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION: to display my own location in the map

The code uses [osmdroid library](https://github.com/osmdroid/osmdroid)
with [osmbonuspack library](http://code.google.com/p/osmbonuspack/)
to display maps from [Open Street Map](http://www.openstreetmap.org).

LocationMapViewer is designed to be used by other apps. This means in Terms of [GPLv3](http://www.gnu.org/licenses/gpl-3.0) that your app
that uses the Intent-Iterface [is not considered a Derived Work.](https://en.wikipedia.org/wiki/GPL_v3#Point_of_view:_linking_is_irrelevant)

In other words: you can used LocationMapViewer as a [driver for your non gpl/non opensource app.](http://www.rosenlaw.com/lj19.htm).
