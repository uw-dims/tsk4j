SHELL = /bin/bash

ifndef ARCH
    $(error ARCH var missing.  Define in subdir Makefile and include this file)
endif

# We install into a directory whose name matches the NativeLoader name
OS = Mac

PWD = $(shell pwd)

ifndef JAVA_HOME
$(error JAVA_HOME make variable missing.  Copy $(PWD)/Makefile.env.sample to $(PWD)/Makefile.env and edit)
endif
ifndef TSK_CPPFLAGS
    $(error TSK_CPPFLAGS make variable missing.  Copy $(PWD)/Makefile.env.sample to $(PWD)/Makefile.env and edit)
endif
ifndef TSK_LDLIBS
    $(error TSK_LDLIBS make variable missing.  Copy $(PWD)/Makefile.env.sample to $(PWD)/Makefile.env and edit)
endif

# The package and artifact name expected passed down by the Maven invocation of this
# build. We supply some bogus ones if not aleady set.  This will
# obviously lead to failure at runtime!
PACKAGE  ?= P
ARTIFACT ?= A

BASEDIR = $(abspath $(NATIVEHOME)/../../..)

CFLAGS += -Wall -Werror -fPIC -std=c99

CPPFLAGS += -I$(BASEDIR)/target/native/javah

CPPFLAGS += $(TSK_CPPFLAGS)

CPPFLAGS += -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/darwin

LDLIBS += $(TSK_LDLIBS)

LDLIBS += -lstdc++

LDFLAGS += -shared

MAINSRCS = $(shell cd $(NATIVEHOME) && ls *.c)

MAINOBJS = $(MAINSRCS:.c=.o)

VPATH = $(NATIVEHOME)

LINK.so = $(CC) $(LDFLAGS) $(LDLIBS)

# the name of the library to build
LIB		= lib$(ARTIFACT).dylib

# link the C objects into a shared library
$(LIB): $(MAINOBJS)
	$(CC) $(LDFLAGS) $^ $(LDLIBS) $(OUTPUT_OPTION)

PACKAGEASPATH = $(subst .,/,$(PACKAGE))

TARGET_DIR = $(BASEDIR)/src/main/resources/$(PACKAGEASPATH)/native/$(OS)/$(ARCH)

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
	@echo JAVA_HOME $(JAVA_HOME)
	@echo TSK_CPPFLAGS $(TSK_CPPFLAGS)
	@echo TSK_LDLIBS  $(TSK_LDLIBS)

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
