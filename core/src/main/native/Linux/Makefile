SHELL = /bin/bash

ifndef ARCH
    $(error ARCH var missing.  Define in subdir Makefile and include this file)
endif

OS = Linux

PWD = $(shell pwd)

ifndef JAVA_HOME
    $(error JAVA_HOME make variable missing.  Copy $(PWD)/Makefile.env.sample to $(PWD)/Makefile.env and edit)
endif
ifndef TSK_INSTALL
    $(error TSK_INSTALL make variable missing.  Copy $(PWD)/Makefile.env.sample to $(PWD)/Makefile.env and edit)
endif

BASEDIR = $(abspath $(NATIVEHOME)/../../..)

CFLAGS += -Wall -Werror -fPIC -std=c99

CPPFLAGS += -I$(BASEDIR)/target/native/javah

CPPFLAGS += $(TSK_CPPFLAGS)

CPPFLAGS += -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux

LDLIBS += $(TSK_LDLIBS)

LDFLAGS += -shared

MAINSRCS = $(shell cd $(NATIVEHOME) && ls *.c)

MAINOBJS = $(MAINSRCS:.c=.o)

VPATH = $(NATIVEHOME)

LINK.so = $(CC) $(LDFLAGS) $(LDLIBS)

# the name of the library to build
LIB		= libtsk4j.so

# link the C objects into a shared library
$(LIB): $(MAINOBJS)
	$(CC) $(LDFLAGS) $^ $(LDLIBS) $(OUTPUT_OPTION)

TARGET_DIR	= $(BASEDIR)/target/classes/META-INF/lib

default: install

install: $(LIB)
	@mkdir -p $(TARGET_DIR)
	cp $(LIB) $(TARGET_DIR)

clean:
	-rm $(LIB) *.o

############################## Test/debug targets #######################

srcs:
	echo $(MAINSRCS)

objs:
	echo $(MAINOBJS)

# eof
