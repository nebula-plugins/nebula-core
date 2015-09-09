### Nebula Core

[![Join the chat at https://gitter.im/nebula-plugins/nebula-core](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/nebula-plugins/nebula-core?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
This specific project holds some "helper" classes for testing and interacting with Gradle. It's not meant to get too big, 
but should serve as a central place for all plugins. This project should have no dependency and not contain any specific
plugins.

## Tasks

# Download

A task to download a file, with incremental processing support. Current implementation is naive, but it'll be improved later. Either downloadBase and downloadFileName can be specified or a full URI as downloadUrl. The destinationDir can be specified but it's optional, though it can always be used by other tasks to reference its output.

    import nebula.core.tasks.Download

    task download(type: Download) {
        downloadBase = 'http://localhost'
        downloadFileName = 'file.zip'
    }

    task downloadFull(type: Download) {
        downloadUrl = 'http://localhost/file.zip'
    }

# Unzip

While unzipping comes built-in to Gradle, via ZipTree, encapsulating a few things in a task does make it easier. 

    import nebula.core.tasks.Unzip

    task unzip(type: Unzip) {
        from(file('tmp/resources.zip'))
    }

It is essentially still a Copy task, but with the added advantage of a _from_ method to allow pulling from the output of another task. And a convenience method, _firstDirectory_, to return the first directory in the zip file, which is common.

    import nebula.core.tasks.*

    task download(type: Download) {
        downloadBase = 'http://www.us.apache.org/dist/tomcat/tomcat-6/v6.0.39/bin'
        downloadFileName = 'apache-tomcat-6.0.39.tar.gz'
    }

    task unzip(type: Unzip) {
        from(tasks.download)
    }

    task package(type: Deb) {
        into('/opt/tomcat6')
        from(tasks.unzip.firstDirectory()) // to get the apache-tomcat-6.0.39 directory out of the way
    }

# Untar

Task that works identically yo Unzip, but works against tar files.
