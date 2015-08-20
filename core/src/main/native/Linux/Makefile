# Stuart Maclean.  Building the TSK4J C sources on Linux.
# Assumes use of GNU Make, and uses tricks/features from that tool.

SHELL = /bin/bash

ifndef ARCH
    $(error ARCH var missing.  Define in subdir Makefile, or pass here from basedir pom.xml)
endif

OS = Linux

PWD = $(shell pwd)

ifndef JDK_HOME
$(error JDK_HOME make variable missing.  Define in $(PWD)/Makefile.env, or pass here from basedir pom.xml)
endif

ifndef TSK_CPPFLAGS
    $(error TSK_CPPFLAGS make variable missing.  Copy $(PWD)/Makefile.env.sample to $(PWD)/Makefile.env and edit)
endif
ifndef TSK_LDLIBS
    $(error TSK_LDLIBS make variable missing.  Copy $(PWD)/Makefile.env.sample to $(PWD)/Makefile.env and edit)
endif

# The prefix and libName expected passed down by the Maven invocation
# of this build. We supply some bogus ones if not aleady set.  This
# will obviously lead to failure at runtime!
PREFIX  ?= P
LIBNAME ?= L

BASEDIR = $(abspath $(NATIVEHOME)/../../..)

CFLAGS += -Wall -Werror -fPIC -std=c99

CPPFLAGS += -I$(BASEDIR)/target/native/javah

CPPFLAGS += $(TSK_CPPFLAGS)

CPPFLAGS += -I$(JDK_HOME)/include -I$(JDK_HOME)/include/linux

LDLIBS += $(TSK_LDLIBS)

LDLIBS += -lstdc++

LDFLAGS += -shared

MAINSRCS = $(shell cd $(NATIVEHOME) && ls *.c)

MAINOBJS = $(MAINSRCS:.c=.o)

VPATH = $(NATIVEHOME)

LINK.so = $(CC) $(LDFLAGS) $(LDLIBS)

# the name of the library to build
LIB		= lib$(LIBNAME).so

# link the C objects into a shared library
$(LIB): $(MAINOBJS)
	$(CC) $(LDFLAGS) $^ $(LDLIBS) $(OUTPUT_OPTION)

PREFIXASPATH = $(subst .,/,$(PREFIX))

# This path matches what the NativeLoader class tries to locate...
TARGET_DIR = $(BASEDIR)/src/main/resources/$(PREFIXASPATH)/native/$(OS)/$(ARCH)

default: install

install: $(LIB)
	@mkdir -p $(TARGET_DIR)
	cp $(LIB) $(TARGET_DIR)

clean:
	-rm $(LIB) *.o

init: Makefile.env

Makefile.env : Makefile.env.sample
	cp Makefile.env.sample Makefile.env

############################## Test/debug targets #######################

show-env:
	@echo ARCH $(ARCH)
	@echo JDK_HOME $(JAVA_HOME)
	@echo TSK_CPPFLAGS $(TSK_CPPFLAGS)
	@echo TSK_LDLIBS  $(TSK_LDLIBS)
	@echo PREFIX $(PREFIX)
	@echo LIBNAME $(LIBNAME)

show-srcs:
	echo $(MAINSRCS)

show-objs:
	echo $(MAINOBJS)

show-lib:
	echo $(LIB)

show-target:
	echo $(TARGET_DIR)

show-install:
	echo $(TARGET_DIR)/$(LIB)

# eof
