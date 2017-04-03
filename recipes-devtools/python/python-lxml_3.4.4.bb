DESCRIPTION = "Powerful and Pythonic XML processing library combining \
libxml2/libxslt with the ElementTree API."
HOMEPAGE = "http://codespeak.net/lxml"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSES.txt;md5=f9f1dc24f720c143c2240df41fe5073b"
SRCNAME = "lxml"
PV="3.4.4"
PR = "r2"

DEPENDS = "libxml2 libxslt"
RDEPENDS_${PN} += "libxml2 libxslt python-compression"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI = "https://pypi.python.org/packages/63/c7/4f2a2a4ad6c6fa99b14be6b3c1cece9142e2d915aa7c43c908677afc8fa4/lxml-3.4.4.tar.gz"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools


DEPENDS += "libxml2 libxslt"

SRC_URI[md5sum] = "a9a65972afc173ec7a39c585f4eea69c"
SRC_URI[sha256sum] = "b3d362bac471172747cda3513238f115cbd6c5f8b8e6319bf6a97a7892724099"

DISTUTILS_BUILD_ARGS += " \
    --with-xslt-config='pkg-config libxslt' \
    --with-xml2-config='pkg-config libxml-2.0' \
"

DISTUTILS_INSTALL_ARGS += " \
    --with-xslt-config='pkg-config libxslt' \
    --with-xml2-config='pkg-config libxml-2.0' \
"

inherit pypi

do_configure_prepend() {
    sed -i -e 's/--version/--modversion/' ${B}/setupinfo.py
}

CFLAGS=" -O2 -pipe -g -feliminate-unused-debug-types -fstack-protector-strong -D_FORTIFY_SOURCE=2 -O2 -Wformat -Wformat-security"

BBCLASSEXTEND = "native nativesdk"
RDEPENDS_${PN}_virtclass-native = "libxml2-native libxslt-native"

RPROVIDES_${PN} = "python-lxml"

