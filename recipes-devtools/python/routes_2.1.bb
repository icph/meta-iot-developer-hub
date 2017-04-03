DESCRIPTION = "Routes is a Python re-implementation of the Rails routes system for mapping URLs to Controllers/Actions and generating URL's. Routes makes it easy to create pretty and concise URLs that are RESTful with little effort"
HOMEPAGE = "https://pypi.python.org/pypi/Routes/2.1"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://setup.py;md5=1ca66888ba7e74f2a4a12d377d764aef"

SRCNAME = "Routes"
PR = "r1.0"
DEPENDS += "python"
DEPENDS_class-native += "python-native"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI = "https://pypi.python.org/packages/f6/e8/ea8fe1eefe02f12e80cbd5878b8fdb1c451d0381f6c5ee98099e6ed0e138/Routes-2.1.tar.gz"
SRC_URI[md5sum] = "e6e463318a9dc6ad2f1b3040e998f0b4"
SRC_URI[sha256sum] = "ebf4126e244cf11414653b5ba5f27ed4abfad38b906a01e5d4c93d3ce5568ea3"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools 

do_install_prepend() {
    install -d ${D}/${libdir}/${PYTHON_DIR}/site-packages
}

pkg_postinst_${PN} () {
#!/bin/sh 
if [ x"$D" != "x" ]; then
	exit 1
fi
#actions to cary out on device
cd /usr/lib/python2.7/site-packages
echo "./Routes-2.1-py2.7.egg-info" > routes.pth
echo "./routes" > routes.pth
}

RDEPENDS_${PN} = "python-setuptools"
BBCLASSEXTEND = "native"