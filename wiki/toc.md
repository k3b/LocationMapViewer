# toc

v [home](home)
* The [map's user ingerface](map)
* [geo-bookmarks](bookmarks) to remember and quickly navigate to named latitude/longitude/zoomlevel
* Customizing LocationMapViewer: [settings](settings)
v Supported [dataformats](data)
v How other apps can use LocationMapViewer through the [intent api](api#intent)

--------------------------
[home](home)
    [Features](home#Features)
    [toc](home#toc)
    [requirements](home#requirements)
    [permissions](home#permissions)

[map](map)
    [zoomlevel](map#z)
	[bookmark](map#bookmark) a region.
	[bubble](map#bubble)
	[poi-marker](map#marker).
	[">" button](map#link) if it has a url. Tabbing on this button opens the url.

[LocationMapViewer API](api)
    [Intent Interface](api#intent)
        [What to do: **action**](api#action)
        [What and where to show: **data-uri** and **data-type**](api#uri)
            [Data-uri="geo:..."](api#geo)
            [Data-uri as local file-uri "file://..." or internet-uri "http:..." or "https:..."](api#file)
        [What title to show: **EXTRA_TITLE**](api#title)
        [Additional points-of-interest **EXTRA_POIS**](api#extra-pois)
        [Example LocationMapViewer as a geo-picker](api#example)
    [Legal stuff](gpl)

[data](data)
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

images

![](https://github.com/k3b/LocationMapViewer/blob/master/wiki/png/api-demo.png)
![](https://github.com/k3b/LocationMapViewer/blob/master/wiki/png/teneriffa-noncluster-bubble.png)
![](https://github.com/k3b/LocationMapViewer/blob/master/wiki/png/picker.png)
![](https://github.com/k3b/LocationMapViewer/blob/master/wiki/png/settings.png)
![](https://github.com/k3b/LocationMapViewer/blob/master/wiki/png/teneriffa-cluster.png)
![](https://github.com/k3b/LocationMapViewer/blob/master/wiki/png/app-map-menu.png)
![](https://github.com/k3b/LocationMapViewer/blob/master/wiki/png/app-map-nomenue.png)
![](https://github.com/k3b/LocationMapViewer/blob/master/wiki/png/bookmarks.png)
![](https://github.com/k3b/LocationMapViewer/blob/master/wiki/png/ic_action_map.png)
![](https://github.com/k3b/LocationMapViewer/blob/master/wiki/png/ic_action_previous_item.png)
![](https://github.com/k3b/LocationMapViewer/blob/master/wiki/png/ic_action_settings.png)

![](https://github.com/k3b/LocationMapViewer/blob/master/wiki/png/ic_action_cancel.png)
![](https://github.com/k3b/LocationMapViewer/blob/master/wiki/png/ic_action_delete.png)
![](https://github.com/k3b/LocationMapViewer/blob/master/wiki/png/ic_action_edit.png)
![](https://github.com/k3b/LocationMapViewer/blob/master/wiki/png/ic_action_help.png)
![](https://github.com/k3b/LocationMapViewer/blob/master/wiki/png/ic_action_important.png)


![](https://github.com/k3b/LocationMapViewer/blob/master/LocationMapViewer/src/main/res/drawable/ic_launcher.png)
