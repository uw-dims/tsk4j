TSK4j - A Java Binding for Sleuthkit
====================================

A Java binding to Carrier's Sleuthkit C library for host-based forensics.
See http://www.sleuthkit.org/sleuthkit/.

These Java bindings are not related to an existing effort described 
[here](http://wiki.sleuthkit.org/index.php?title=TSK_Bindings).  Those
bindings are used more to populate Java objects from an SQL database of
pre-acquired data.  The bindings described here are geared more to image
and filesystems traversal as performed by the core sleuthkit C/C++
code.  These bindings are thus just a Java veneer over libtsk, with
some additions.

Motivation
----------

Why use these bindings?

* You prefer coding in Java over C (and thus likely prefer Maven over make).

* You want to compose a larger application that needs Sleuthkit's
  power but want to use existing Java libraries/codebases.

* You do not need/want to store your results in SQL.

* You want a fast result to a simple task such as 'locate all PE
  executables'.

* The included 'digests' module is useful to you.

* The included 'sample' programs (module) are useful to you.


PRE-REQUISITES
--------------

This codebase is Java, and so needs a Java compiler system, aka a
'Java Developmment Kit (JDK)'.  A 1.7 or later JDK is required.
Sun/Oracle's JDK works well, as does OpenJDK's JDK.

The build is via Maven, a build and project management tool for Java
artifacts. So Maven is required too.  All code dependencies are
resolved by Maven. At time of writing (Mar 2015), the author uses
Maven 3.2.5 on Ubuntu 12.04 LTS. See
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

INSTALL
-------

```
$ mvn install

$ mvn javadoc:aggregate
```

The Javadoc APIs should then be available at ./target/site/apidocs


LOCAL REPOSITORY
----------------

The Maven artifacts built here themselves depend on the following
existing Maven artifacts:

* edu.uw.apl.commons:native-lib-loader:jar:2.0.0

These artifacts are not currently available in binary form (e.g. they
are not on Maven Central nor any other popular repo site).  Neither
are they yet available in source form (on e.g. github).  

Until either of these two conditions change, we are bundling these
artifacts in a 'local Maven repo' at XXX.  The poms refer to this repo
to resolve the build dependencies.  The local repo looks like this:

```
$ cd /path/to/tsk4j

$ tree .repository/
.repository/
`-- edu
    `-- uw
        `-- apl
            `-- commons
                `-- native-lib-loader
                    `-- 2.0.0
                        |-- native-lib-loader-2.0.0.jar
                        `-- native-lib-loader-2.0.0.pom
```


INSTALLATION
============

Currently this codebase is organised as four Maven 'modules', with a
parent pom at the root level.

* Module 1: the .  Java classes auto-generated from .xsd
file set via xjc. xjc is bundled with recent JDK releases (1.6+). This
module includes some sample STIX  documents (from Mitre and elsewhere).
See under jaxb/src/test/resources.



Getting the xsd file set to build took some work. See
[./jaxb/README.md](./jaxb/README.md) for more details.

* Module 2: utils. Example routines for document authoring (writing)
  and document ingesting (reading).  Authoring utilities are at
  [HashComposers](./utils/src/main/java/edu/uw/apl/stix/utils/HashComposers.java).  Ingest utilities are at
  [HashExtractors](./utils/src/main/java/edu/uw/apl/stix/utils/HashExtractors.java).

* Module 3: cli.  Command line driver tools for invoking the utilities
  above.  For authoring, see
  [MD5Composer](./cli/src/main/java/edu/uw/apl/stix/cli/MD5Composer.java).
  For ingesting, see
  [MD5Extractor](./cli/src/main/java/edu/uw/apl/stix/cli/MD5Extractor.java).

* Module 4: json.  Experimenting whether STIX Java objects can be
  represented as JSON.  The basic answer is no, at least for the
  complex STIX instance documents cited above. See the [json
  sources](./json/src/main) and [json test
  case](./json/src/test/java/edu/uw/apl/stix/json/SamplesTest.java) for
  more details.


To build:

```
$ cd stix-java

$ mvn install
```
To then try out a bundled command line interface (cli) tool:
```
$ cd stix-java/cli

$ ./stix.md5 ../jaxb/src/test/resources/APT1/Appendix_G_IOCs_Full.xml
``` 

The tool simply loads the supplied file and extracts any md5 hashes
found in Indicators and/or Observables.  It should print a list of
1797 md5 hashes to stdout. The stix.md5 file is a simple bash script
driving the JVM invocation of the appropriate class.


Observation: This whole jaxb lark is way too complicated. Just use grep!

eof
