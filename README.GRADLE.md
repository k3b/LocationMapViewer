#osmdroid-gradle

This is statusfile of the osmdroid-sub-project "gradle build"

mission: port gradle-build to become compatible with androidStudio1+ and/or gradle-2.2+

##status summary

* AndroidStudio1+/gradle2.2+ project
    * works with sub-moduls `OpenStreetMapViewer`, `osmdroid-android` and `osmdroid-third-party`
    * does not work with sub-moduls `osmdroid-android-it` and `OSMMapTilePackager`
    * **Risks**
        * when using AndroidStuio with
            * making changes to supported modules may break sourcecode/functionality of non supportet modules
            * making changes to submodules/filenames may break maven build scripts

##detail status of osmdroid modules

* module name: **osmdroid-android**
    * purpose: main android `Open Streetmap` supportlibrary, no properiterian dependenciesc
    * gradle status: **done** can be used as a sourcecode android-library-module in AndroidStudio1+ and gradle 2.2+
        * todo add code to support build result maven upload
    * **problem:** no seperate library that contains non-andriod code
        * if this lib is build by gradle creating an *aar-file* non android gradle-apps cannout link to this.
            * i found a way to also generate a *jar file* with gradle
            * i donot know how to consume it in a non android gradle-app
        * the original maven build only produces *jar files* and can link this to non android gradle-apps.
* module name: **osmdroid-third-party**
    * purpose: addon to android `Open Streetmap` supportlibrary that adds microsoft-bing-map and google-maps, properiterian dependencies to non-free-google/microsoft code and/or services
    * gradle status: **done** can be used as a sourcecode android-library-module in AndroidStudio1+ and gradle 2.2+
        * where the depending library `osmdroid-android` can be
            * a referenced local sourcecode module dependency or
            * a referenced remote repository dependency
        * todo add code to support build result maven upload
    * **problem:** to build apps you need
        * a developper-licence from google and/or microsoft (non free services)
        * to manually add a properiterian google library, we are not allowed to supply
* module name: **OpenStreetMapViewer**
    * purpose: android demo app to show maps
        * what you can do with `osmdroid-android`
        * how to build android apps that use `osmdroid-android`
    * gradle status: **done** can be used as a sourcecode module in AndroidStudio1+ and gradle 2.2+
        * where the depending libraries `osmdroid-android` (and optionally `osmdroid-third-party`) can be
            * a referenced local sourcecode module dependency and/or
            * a referenced remote repository dependency
    * **problem:** One demo app fits all
    * **longtime goal**
        * create several minimal apps. each app demonstates one issue
        * current AndroidStudio-1+/gradle2.2+ does not support project-subfolder yet
             * see [this stackoverflow question](http://stackoverflow.com/questions/27860674/androidstudio-1-0-2-with-local-modules-in-relative-paths)
* module name: **osmdroid-android-it**
    * purpose: OpenStreetMapViewer-integration-tests used to automatic verify that `osmdroid-android` works as expected.
    * gradle status: **not done yet**
    * **problem:**
        * direct dependency to demo-app OpenStreetMapViewer
        * Folder Structure cannot be ported to gradle build without breaking current maven support
            * in gradle android-build-tools1+ an automated integration test must be in the same project as the app that is tested: `OpenStreetMapViewer`
            * current build tools cannot reference apk-project `OpenStreetMapViewer`
            * the original maven build only produces *jar files* and can link this to non android gradle-apps.
        * if you develop in AndroidStudio `OpenStreetMapViewer` with `osmdroid-android` and `osmdroid-third-party`
            * you cannot check if the integration test compiles
            * the automated tests break because the developper introduced new bugs.
    * **goal**
        * refactor project folderstructure and maven-build together into one project for `OpenStreetMapViewer` and it integrationtests.
        * fix possible build-erros and test failures
* module name: **OSMMapTilePackager**
    * purpose: desktop j2se java application to package OpenStreetMap tiles
    * gradle status: **not done yet**
    * **problem:**
        * gradle2.2.1 build cannot link non android gradle-apps like `OSMMapTilePackager` to an android gradle-lib like `osmdroid-android` (or i donot know how to do this.)

## how to go on

The author of this status (k3b) file

* knows
    * how to implement android apps using AndroidStudio
    * how to create gradle build scripts
* has no knowledge about (and don-t want to learn)
    * maven-script related topics
* is willing to cooperate
    * (from gradle-s point of view) necessary change in architecture
        * changes in project folder structure for the integration-tests
        * ?? seperating `osmdroid-android` into a android dependent lib and a android-independet-lib to allow `OSMMapTilePackager` gradle build.

* To acomplish the sub-project "gradle build" we need
    * a gradle expert (k3b or sombody else)
    * a maven/pom expert ???
    * somone who knows how to upload new version of osmdroid to remote repository
    * a integration-test expert ???
    * optionally a `osmdroid-android` architect ???
    * a forum-topic where to discuss architectual changes and their consequences before making them

* open questions
    * is handling of the "add non free libs" problem description of gradle build (copy to ".../osmdroid/osmdroid-third-party/libs/maps.jar")  compatibel with maven build?

