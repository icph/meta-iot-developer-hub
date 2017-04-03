# This will remove java support for mraa
PACKAGES_remove = "${PN}-java"
PACKAGECONFIG_remove = "java"

CFLAGS=" -O2 -pipe -g -feliminate-unused-debug-types -fstack-protector-strong -D_FORTIFY_SOURCE=2 -O2 -Wformat -Wformat-security"
