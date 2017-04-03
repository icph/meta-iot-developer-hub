SUMMARY = "Low Level Skeleton Library for Communication on Intel platforms"
SECTION = "libs"
AUTHOR = "Brendan Le Foll, Tom Ingleby"

RDEPENDS_${PN} = "dfu-util"
DEPENDS = "json-c udev"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=66493d54e65bfc12c7983ff2e884f37f"

S = "${WORKDIR}/git"

inherit distutils-base pkgconfig python-dir cmake systemd

FILES_${PN}-doc += "${datadir}/mraa/examples/"

FILES_${PN}-dbg += "${libdir}/node_modules/mraajs/.debug/ \
                    ${PYTHON_SITEPACKAGES_DIR}/.debug/ \
                    ${prefix}/src/debug/ \
                    ${bindir}/.debug/"

PACKAGECONFIG ??= "python nodejs"
PACKAGECONFIG[python] = "-DBUILDSWIGPYTHON=ON, -DBUILDSWIGPYTHON=OFF, swig-native python"
PACKAGECONFIG[nodejs] = "-DBUILDSWIGNODE=ON, -DBUILDSWIGNODE=OFF, swig-native nodejs"

EXTRA_OECMAKE_append = "-DINSTALLGPIOTOOL=ON -DFIRMATA=ON -DIMRAA=ON -DENABLEEXAMPLES=OFF -DCMAKE_SKIP_RPATH:BOOL=ON"

do_compile_prepend () {
  # when yocto builds in ${D} it does not have access to ../git/.git so git
  # describe --tags fails. In order not to tag our version as dirty we use this
  # trick
  sed -i 's/-dirty//' src/version.c
}

do_install_append () {
  mkdir -p ${D}${systemd_unitdir}/system/
  install -m 0644 ${WORKDIR}/imraa.service ${D}${systemd_unitdir}/system/
  mkdir -p ${D}${datadir}/${PN}/
  install -m 0644 ${WORKDIR}/ConfigurableFirmataCurieImu.ino.bin ${D}${datadir}/${PN}/firmata101.ino.bin
  install -d ${D}${sysconfdir}
  install -m 0644 ${WORKDIR}/imraa.conf ${D}${sysconfdir}/
}

FILES_${PN}_append += " ${systemd_unitdir}/system/imraa.service \
                        ${datadir}/${PN}/firmata101.ino.bin \
                        ${sysconfdir}/imraa.conf \
                      "
SYSTEMD_SERVICE_${PN} = "imraa.service"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG = "python nodejs java usbplat"
PACKAGECONFIG_quark = "python nodejs java"

PACKAGECONFIG[java] = "-DBUILDSWIGJAVA=ON, -DBUILDSWIGJAVA=OFF, swig-native icedtea7-native,"
PACKAGECONFIG[usbplat] = "-DUSBPLAT=ON, -DUSBPLAT=OFF,,"
PACKAGECONFIG[ftdi4222] = "-DFTDI4222=ON, -DFTDI4222=OFF, libft4222, libft4222"

export JAVA_HOME="${STAGING_DIR}/${BUILD_SYS}/usr/lib/jvm/icedtea7-native"

cmake_do_generate_toolchain_file_append() {
  echo "
set (JAVA_AWT_INCLUDE_PATH ${JAVA_HOME}/include CACHE PATH \"AWT include path\" FORCE)
set (JAVA_AWT_LIBRARY ${JAVA_HOME}/jre/lib/amd64/libjawt.so CACHE FILEPATH \"AWT Library\" FORCE)
set (JAVA_INCLUDE_PATH ${JAVA_HOME}/include CACHE PATH \"java include path\" FORCE)
set (JAVA_INCLUDE_PATH2 ${JAVA_HOME}/include/linux CACHE PATH \"java include path\" FORCE)
set (JAVA_JVM_LIBRARY ${JAVA_HOME}/jre/lib/amd64/libjvm.so CACHE FILEPATH \"path to JVM\" FORCE)
" >> ${WORKDIR}/toolchain.cmake
}

# include .jar files in /usr/lib/java for 64 bit builds
FILES_${PN}_append = "${@' /usr/lib/*' if '${MACHINE}' == 'intel-baytrail-64' else ''}"

INSANE_SKIP_${PN} = "dev-so debug-files installed-vs-shipped"

# only for gateways
PACKAGES = "${PN}"
FILES_${PN}_append = " ${includedir}"