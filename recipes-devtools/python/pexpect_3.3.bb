DESCRIPTION = "Pexpect allows easy control of interactive console applications."
HOMEPAGE = "https://pexpect.readthedocs.org/en/latest/"
SECTION = "devel/python"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c25d9a0770ba69a9965acc894e9f3644"

SRCNAME = "pexpect"
PR = "r1.0"
DEPENDS += "python"
DEPENDS_class-native += "python-native"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI = "https://pypi.python.org/packages/52/97/13924c85a4b7544a4174781360e0530a7fff23e62d76da0e211369dd61f5/pexpect-3.3.tar.gz"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit distutils 

do_install_prepend() {
    install -d ${D}/${libdir}/${PYTHON_DIR}/site-packages
}

RDEPENDS_${PN} = "python-distutils "

RDEPENDS_${PN}_class-native = "\
    python-distutils \
"

SRC_URI[md5sum] = "0de72541d3f1374b795472fed841dce8"
SRC_URI[sha256sum] = "dfea618d43e83cfff21504f18f98019ba520f330e4142e5185ef7c73527de5ba"

BBCLASSEXTEND = "native"
