#LocationMapViewer API

Other Android apps can use LocationMapViewer

* to display Informations in a [geografic map](map) or
* to pick a location from a map

This chapter describes how this can be done through android's **Intent Interface**

# <a name='intent'>Intent Interface</a>

In Android an [Intent](http://developer.android.com/guide/components/intents-filters.html) is a messaging object
you can use to request an action from another app's component.

The intent tells LocationMapViewer

* What to do: **action** VIEW or PICK
* What and where to show: **data-uri** and **data-type**
* What title to show: putExtra( **Intent.EXTRA_TITLE** , title);
* Additional points-of-interest putExtra( **"de.k3b.POIS"** , ...);

## <a name='action'>What to do: **action**</a>

Currently LocationMapViewer supports these action:

* show a map: **Intent.ACTION_VIEW**
* select a location from  a map: **Intent.ACTION_PICK**

PICK-s result location is returned to the callers onActivityResult as a [geo uri](data#geo).

Example:

* Intent mapIntent = new Intent();
* mapIntent.setAction(Intent.ACTION_VIEW);

## <a name='uri'>What and where to show: **data-uri** and **data-type**</a>

Data uri and data type determine the primary content of the map:

### <a name='geo'>data-uri="geo:..."</a>

If you set data-uri to a [geo uri](data#geo) without a mimetype this location will be shown as a
[point-of-interest](data#marker) and as [visible region of the world](data#region) in the [map](map#marker)

if you do not supply a [z=zoomLevel](data#z) the last zoomlevel will be used

Example:

* mapIntent.setDataAndType(Uri.parse("geo:53.2,8.8?&z=12"), null);

### <a name='file'>data-uri as local file-uri "file://..." or internet-uri "http:..." or "https:..."</a>

If you set the data-uri to a local file or as internet http(s) the content of the file will be loaded as a
[point-of-interest](data#marker) in the [map](map#marker).

The [visible region of the world](data#region) will be the center of the data and the
[z=zoomLevel](data#z) will be be adjusted so that all points will be visible.

LocationMapViewer supports these formats:

* [gpx-1.0 and gpx-1.1](data#gpx)
    * file-extension ".gpx" or
    * mimeType "application/xml+gpx"
* [kml-2.2](data#kml)
    * file-extension ".kml" or
    * mimeType "application/vnd.google-earth.kml+xml" or
    * mimeType "application/xml+kml"
* [poi](data#poi)
    * file-extension ".poi" or
    * mimeType "application/xml+poi"

Example:

* mapIntent.setData(Uri.parse("file://mnt/sdcard/copy/some.gpx"));
* mapIntent.setData(Uri.parse("file://mnt/sdcard/copy/some.kml"));
* mapIntent.setData(Uri.parse("file://mnt/sdcard/copy/some.poi"));
* mapIntent.setDataAndType(Uri.parse("https://myserver.org/path/to/some.gpx", "application/xml+gpx"));
* mapIntent.setDataAndType(Uri.parse("http://myserver.org/path/to/some.kml", "application/vnd.google-earth.kml+xml"));
* mapIntent.setDataAndType(Uri.parse("http://myserver.org/path/to/some.kml", "application/xml+kml"));
* mapIntent.setDataAndType(Uri.parse("https://myserver.org/path/to/some.poi", "application/xml+poi"));

Note: The file mimetype is autmatically determined by android for files if it is not supplied.

## <a name='title'>What title to show: **EXTRA_TITLE**</a>

You can define LocationMapViewer's title text like this:

* mapIntent.putExtra( **Intent.EXTRA_TITLE** , "Where did you take the foto?");

![](https://github.com/k3b/LocationMapViewer/blob/master/wiki/png/picker.png)

Note: If your app starts LocationMapViewer through the [Intent Interface](api#intent) without
EXTRA_TITLE the app will not have a title.

![](https://github.com/k3b/LocationMapViewer/blob/master/wiki/png/app-map-nomenue.png)

Note: If you start LocationMapViewer directly from Android's app launcher it will show "LocationMapViewer"
as the app's title.

## <a name='extra-pois'>Additional points-of-interest **EXTRA_POIS**</a>

You can start LocationMapViewer with [intent geo data-uri](api#geo) and use EXTRA_POIS
to put additional [point-of-interest](data#marker) into the [map](map#marker).

If the [intent geo data-uri](api#geo) does not have a [z=zoomLevel](data#z) the zoomlevel will be
adjusted so that all points will be visible.

LocationMapViewer supports these EXTRA_POI formats:

* [geo uri's](data#geo) separated by cr/lf
* [poi](data#poi) xml fragments
* [gpx-1.0 and gpx-1.1](data#gpx) xml fragments
* [kml-2.2](data#kml) xml fragments

Xml fragment means xml without the root xml element.

Examples:

* mapIntent.putExtra( **"de.k3b.POIS"** , "geo:53.2,8.8\ngeo:53.4,8.3"); // [geo uri's](data#geo)
* mapIntent.putExtra( "de.k3b.POIS" , "&lt;poi ll='53.2,8.8'/>\n&lt;poi ll='53.4,8.3'/>"); // [poi](data#poi)
* mapIntent.putExtra( "de.k3b.POIS" , "&lt;wpt lat='53.2' lon='8.8'/>\n&lt;wpt lat='53.4' lon='8.3'/>"); // [gpx-1.0](data#gpx)
* mapIntent.putExtra( "de.k3b.POIS" , "&lt;trkpt lat='53.2' lon='8.8'/>\n&lt;trkpt lat='53.4' lon='8.3'/>"); // [gpx-1.1](data#gpx)
* mapIntent.putExtra( "de.k3b.POIS" , "&lt;coordinates>8.8,53.2&lt;/coordinates>\n&lt;coordinates>8.3,53.4&lt;/coordinates>"); // [kml-2.2](data#kml)


## Example LocationMapViewer as a <a name='example'>geo-picker</a>

Here is a more verbose example that uses LocationMapViewer as a picker :

		private static int ACTION_ID = 42;

        Intent mapIntent = new Intent();
        mapIntent.setAction(Intent.ACTION_PICK);
        mapIntent.putExtra(Intent.EXTRA_TITLE, "Where did you take the photo?");
        mapIntent.putExtra("de.k3b.POIS",
                  "<poi ll='53.1,8.9'/>\n" +
                  "<poi ll='53.3,8.7' n='Name' \n" +
                        " link='https://github.com/k3b/LocationMapViewer/' \n" +
                        " d='Here is some comment' />");

        Uri uri = Uri.parse("geo:53.2,8.8?q=(name)&z=1");
        mapIntent.setDataAndType(uri, null);
        try {
            startActivityForResult(Intent.createChooser(mapIntent,"Choose app to show location"), ACTION_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }


The [yellow marker](map#yellow) is the current geo-pick-selection.
You can move it by long-pressing and then moving it.

![](https://github.com/k3b/LocationMapViewer/blob/master/wiki/png/picker.png settings.png)

After pressing the [OK] button in the lower left corner this location is returned
to the callers onActivityResult as a [geo uri](data#geo)

<pre>
    /** Process the result location of the geo-picker  */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String resultIntent = getUri(data);
        super.onActivityResult(requestCode, resultCode, data);
        final String result = "got result " + resultIntent;
        Toast.makeText(this, appName + result, Toast.LENGTH_LONG).show();
    }
</pre>

There is a demo app geoIntentDemo/src/main/java/[de.k3b.android.locationMapViewer.demo.GeoIntentDemoActivity.java](https://github.com/k3b/LocationMapViewer/blob/master/geoIntentDemo/src/main/java/de/k3b/android/locationMapViewer/demo/GeoIntentDemoActivity.java)
that you can use to experiment with the different intent parameters.

![](https://github.com/k3b/LocationMapViewer/blob/master/wiki/png/api-demo.png)

* If you press the "Demo" button you will execute android.intent.action.VIEW.
* If you press the "Pick" button you will execute android.intent.action.PICK.

# <a name='gpl'>Legal stuff</a>

LocationMapViewer is designed to be used by other apps. This means in Terms of [GPLv3](http://www.gnu.org/licenses/gpl-3.0) that your app
that uses the [Intent Interface](api#intent) is [**not** considered a Derived Work.](https://en.wikipedia.org/wiki/GPL_v3#Point_of_view:_linking_is_irrelevant)

In other words: you can used LocationMapViewer as a [driver for your non gpl/non opensource app.](http://www.rosenlaw.com/lj19.htm).

