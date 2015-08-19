TSK4j - A Java Binding for Sleuthkit
====================================

A Java binding to Carrier's Sleuthkit C library for host-based forensics.
See http://www.sleuthkit.org/sleuthkit/.

These Java bindings are not related to an existing effort described 
[here](http://wiki.sleuthkit.org/index.php?title=TSK_Bindings).  Those
bindings are used more to populate Java objects from an SQL database of
pre-acquired data.  The bindings described here are geared more to image
and filesystems traversal as performed by the core sleuthkit C/C++
code.  These bindings are essentially just a Java veneer over libtsk, with
some additions.

Motivation
----------

Why use these bindings?

* You prefer coding in Java over C (and thus likely prefer Maven over make).

* You want to compose a larger application that needs Sleuthkit's
  filesystem traversal power but want to use existing Java
  libraries/codebases.

* You do not need/want to store your results in SQL.

* You want a fast result to a simple task such as 'locate all PE
  executables'.

* The included 'digests' module is useful to you.

* The included 'sample' programs (own module) are useful to you.

* You want to use or build upon the Armour tool, a method for
  comparing BodyFiles and thus filesystem contents.

PRE-REQUISITES
--------------

This codebase is Java, and so needs a Java compiler system, aka a
'Java Developmment Kit (JDK)'.  A 1.7 or later JDK is required.
Sun/Oracle's JDK works well, as does OpenJDK's JDK.

The build is via Maven, a build and project management tool for Java
artifacts. So Maven is required too.  All code dependencies are
resolved by Maven. At time of writing (Mar 2015), the author uses
Maven 3.2.5 on both Ubuntu 12.04 LTS and Mint 17. See
http://maven.apache.org/download.cgi for more details.

To verify you are running suitable versions of Java and Maven, run
'mvn -v' and inspect the output, like this:

```
[stuart-vaio]$ mvn -v
Apache Maven 3.2.5 (12a6b3acb947671f09b81f49094c53f426d8cea1; 2014-12-14T09:29:23-08:00)
Maven home: /usr/local/apache-maven/apache-maven-3.2.5
Java version: 1.7.0_17, vendor: Oracle Corporation
Java home: /usr/lib/jvm/jdk1.7.0_17/jre
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "2.6.32-73-generic", arch: "i386", family: "unix"
```

If you wish to take on the task of compiling the native C parts for
either MacOS or Windows platforms, you will need a suitable C compiler
and make/build system.  These native parts are already built for
Linux.

BUILD
-----

```
$ cd /path/to/tsk4j

$ mvn install

$ mvn javadoc:aggregate
```

The Javadoc APIs should then be available at ./target/site/apidocs.

There are unit tests for some modules.  These are only run when the
'tester' profile is activated.  If you want to run unit tests, try:

```
$ mvn test -Ptester
```

MODULES
-------

The tsk4j codebase is organised as four Maven 'modules', with a
parent pom at the root level.  The modules are as follows

# Core

The primary tsk4j module, the one that wraps existing
Sleuthkit C routines with their Java equivalents.  If you just want to
write new filesystem traversal routines in Java, this module is the
only artifact you need.  To build:

```
$ cd /path/to/tsk4j/core

$ mvn install

// Unit tests optional, need profile activation
$ mvn test -Ptester
```

The core module is mostly a thin Java wrapper for libtsk.  Perhaps the
one value-added feature is the ability to extract data from various
objects via the familiar java.io.InputStream api.  An example:

```
InputStream is = new Image( "/dev/sda" ).getInputStream();
```

The following core tsk4j classes support the InputStream API:

* [image.Image] (./core/src/main/java/edu/uw/apl/commons/tsk4j/image/Image.java)

* [filesys.Attribute] (./core/src/main/java/edu/uw/apl/commons/tsk4j/filesys/Attribute.java)

* [filesys.File] (./core/src/main/java/edu/uw/apl/commons/tsk4j/filesys/File.java)

* volsys.Partition (./core/src/main/java/edu/uw/apl/commons/tsk4j/volsys/Partition.java)

So far we have built the native parts of the core tsk4j module for
Linux 32bit and Linux 64bit.  Still to do are MacOS and Windows
builds.  We use the [Java Native Loader]
(https://github.com/uw-dims/java-native-loader) framework to handle
the split Java/C language build.

# Digests

TODO

# Samples

This module contains some sample programs built around the core tsk4j
artifact.

```
$ cd /path/to/tsk4j/samples

$ mvn package
```

Included are some Unix-style shell scripts to drive the sample program
invocation, on Linux/MacOS at least, e.g:

```
$ ./adsfind /path/to/fileSystem

$ ./hivefind /path/to/some/ntfsFilesystem

$ ./pefind /path/to/some/ntfsFilesystem

$ ./unallochash /dev/sda
```

# Armour

TODO


LOCAL REPOSITORY
----------------

The Maven artifacts built here themselves depend on the following
existing Maven artifacts which are not (yet) available on a public
Maven repository (like Maven Central):

* edu.uw.apl.commons:native-lib-loader:jar:2.1.0

* edu.uw.apl.commons:shell-base:jar:1.0

The source for the first Maven artifact is available on
(github)[https://github.com/uw-dims/java-native-loader].  But to save
the TSK4J user the chore of building and installing the dependencies,
we are bundling these artifacts in a 'project-local Maven repo' at
./repository.  The relevant poms refer to this repo to resolve the
artifact dependencies.  The project-local repo looks like this:

```
$ cd /path/to/tsk4j

$ tree .repository/
.repository/
`-- edu
    `-- uw
        `-- apl
            `-- commons
                `-- native-lib-loader
                    `-- 2.1.0
                        |-- native-lib-loader-2.1.0.jar
                        `-- native-lib-loader-2.1.0.pom
                `-- shell-base
                    `-- 1.0
                        |-- shell-base-1.0.jar
                        `-- shell-base-1.0.pom
```

# Video/Slides

Ideas related to this work were presented at the [OSDFCon]
(http://www.osdfcon.org/2013-event/) workshop in 2013.  A local copy
of the slides is also included [here](./doc/Maclean-OSDF2013-tsk4j.pdf).

