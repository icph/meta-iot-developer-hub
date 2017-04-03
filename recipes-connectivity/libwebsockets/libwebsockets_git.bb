SUMMARY = "libwebsockets"
DESCRIPTION = "Libwebsockets is a lightweight pure C library built to use minimal CPU and memory \
               resources, and provide fast throughput in both directions."
HOMEPAGE = "https://libwebsockets.org"
SECTION = "libs"
LICENSE = "MIT"
DEPENDS = "zlib openssl"
PV = "1.5"

SRC_URI = "git://github.com/warmcat/libwebsockets;protocol=git;branch=master;rev=ab620ffde36b3186a6392e75d96d8f7016a2b3f6"
LIC_FILES_CHKSUM = "file://LICENSE;md5=041a1dec49ec8a22e7f101350fd19550"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

FILES_${PN} += "/usr/share/*"

RDEPENDS_${PN} = "zlib libcrypto openssl"

