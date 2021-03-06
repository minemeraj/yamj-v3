YAMJ-v3
=======

Main Project for YAMJ v3

[![Build Status](http://jenkins.omertron.com/job/YAMJv3/badge/icon)](http://jenkins.omertron.com/job/YAMJv3)

Modules
-------
### yamj3-common:
> Holds the commonly used classes.

### yamj3-core:
> Main webapp for YAMJ3 core; entry point for everything.
This is a real webapp; can be started with a jetty server or deployed to an existing tomcat server.

### yamj3-jetty:
> Starts the yamj3-core if you do not have, or want to use a tomcat server.

### yamj3-filescanner:
> The file scanner for processing the media directories and sending the file information to the core processor.

### yamj3-plugin-api:
> The plugin api which holds tools and extension points for building own YAMJ v3 plugins.

### yamj3-distribution:
> The distribution package to build and pack YAMJ v3 into a single zip archive.

Running YAMJv3
--------------
Please see [this wiki page](https://github.com/YAMJ/yamj-v3/wiki/Getting-Started-with-the-Alpha-Test) for instructions on how to run YAMJv3

Using the API
-------------
Documentation on teh API can be found at [Apiary.io](http://docs.yamj.apiary.io/#)
