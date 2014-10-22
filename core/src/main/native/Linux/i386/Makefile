# Our directory is 'i386', so Maven can find us (via os.arch)
# But our arch as known to the NativeLoader will be x86
# (which is what the 'arch' command shows)

ARCH = x86

CFLAGS += -Wno-int-to-pointer-cast -Wno-pointer-to-int-cast

-include Makefile.env

NATIVEHOME = $(abspath ../../)

include ../Makefile

# eof
