DESCRIPTION = "Node.js is a platform built on Chrome's JavaScript runtime for easily building fast, scalable network applications."
HOMEPAGE = "http://nodejs.org"
LICENSE = "MIT"
SECTION = "devel"
SUMMARY = "Node.js is a platform built on Chrome's JavaScript runtime for easily building fast, scalable network applications."
DEPENDS = "openssl"
DEPENDS_class-native += " openssl-native"
PR = "r1.0"


FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI = "https://nodejs.org/dist/v6.9.2/node-v6.9.2.tar.gz \
           file://fix_ar.patch"

LIC_FILES_CHKSUM = "file://LICENSE;md5=19eb7a3bddc44523bf96176c3da4b422"
SRC_URI[md5sum] = "b9e6bd6eddb78f34becfa891d60071d8"
SRC_URI[sha256sum] = "997121460f3b4757907c2d7ff68ebdbf87af92b85bf2d07db5a7cb7aa5dae7d9"


S = "${WORKDIR}/node-v${PV}"

# v8 errors out if you have set CCACHE
CCACHE = ""

def map_dest_cpu(target_arch, d):
    import re
    if   re.match('i.86$', target_arch): return 'ia32'
    elif re.match('x86_64$', target_arch): return 'x64'
    return target_arch

def v8_target_arch(target_arch, d):
    import re
    if   re.match('i.86$', target_arch): return 'x87'
    elif re.match('x86_64$', target_arch): return 'x64'
    return target_arch

GYP_DEFINES_append_mipsel = " mips_arch_variant='r1' "

do_configure () {
    export LD="${CXX}"
    alias g++="${CXX}"
    GYP_DEFINES="${GYP_DEFINES}" export GYP_DEFINES
    ./configure --prefix=${prefix} --without-snapshot --dest-cpu=${@map_dest_cpu(d.getVar('TARGET_ARCH', True), d)} --dest-os=linux ${ARCHFLAGS} ${EXTRA_OECONF} --with-intl=none
    unalias g++
    cd ${S}; sed -i '/want_separate_host_toolset/ i \                \ \x27v8_target_arch\x27:\x27${@v8_target_arch(d.getVar('TARGET_ARCH', True), d)}\x27,' config.gypi
}


do_compile () {
    export LD="${CXX}"
    alias g++="${CXX}"
    make BUILDTYPE=Release
    unalias g++
}

do_install () {
    oe_runmake DESTDIR=${D} install
}

PACKAGES =+ "${PN}-npm"
FILES_${PN}-npm = "/usr/lib/node_modules ${bindir}/npm"
RDEPENDS_${PN}-npm = "bash python-compiler python-shell python-datetime python-subprocess python-multiprocessing python-crypt python-textutils python-netclient python-misc"

PACKAGES =+ "${PN}-dtrace"
FILES_${PN}-dtrace = "${libdir}/dtrace"

PACKAGES =+ "${PN}-systemtap"
FILES_${PN}-systemtap = "${datadir}/systemtap"

BBCLASSEXTEND = "native"
RCONFLICTS_${PN} = "node"