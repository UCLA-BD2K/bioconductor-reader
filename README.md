Bioconductor Reader
===================
	BD2K Center of Excellence for Big Data Computing at UCLA

-------------------------------------------------------------------------------
Overview
---------------
Retrieves information about tools in Bioconductor and generates a json file.

Usage
---------------
  usage: Bioconductor Reader [-h] [-o <arg>] -p <arg> [-r <arg>]
  Retrieve all metadata about packages in Bioconductor

    -h,--help
    -o,--output <arg>       Output path
    -p,--properties <arg>   (REQUIRED) Input properties file
    -r,--retries <arg>      Number of tries when requesting server when an
                            error occurs. 0<r<50  Default: 5

Dependencies
---------------
 - [Apache Commons CLI](http://commons.apache.org/proper/commons-cli/)
 - [JSoup](http://jsoup.org/)
 - [Unirest](http://unirest.io/)
 - [Gson] (https://github.com/google/gson)
