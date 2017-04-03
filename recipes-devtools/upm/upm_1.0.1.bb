SUMMARY = "Sensor/Actuator repository for Mraa"
SECTION = "libs"
AUTHOR = "Brendan Le Foll, Tom Ingleby, Yevgeniy Kiveisha"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d1cc191275d6a8c5ce039c75b2b3dc29"

DEPENDS = "nodejs swig-native mraa jpeg"

SRC_URI = "git://github.com/intel-iot-devkit/upm.git;protocol=git;branch=master;rev=a2698fd560c9fa7917de33a65601bea50d218481"

S = "${WORKDIR}/git"

inherit distutils-base pkgconfig python-dir cmake

FILES_${PN}-doc += "${datadir}/upm/examples/"

PACKAGECONFIG ??= "python nodejs"
PACKAGECONFIG[python] = "-DBUILDSWIGPYTHON=ON, -DBUILDSWIGPYTHON=OFF, swig-native python"
PACKAGECONFIG[nodejs] = "-DBUILDSWIGNODE=ON, -DBUILDSWIGNODE=OFF, swig-native nodejs"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

DEPENDS_append = " libmodbus"
RDEPENDS_${PN}_append = " libmodbus"

export JAVA_HOME="${STAGING_DIR}/${BUILD_SYS}/usr/lib/jvm/icedtea7-native"

PACKAGECONFIG ??= "python nodejs java"

PACKAGECONFIG[java] = "-DBUILDSWIGJAVA=ON, -DBUILDSWIGJAVA=OFF, swig-native icedtea7-native,"

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
FILES_${PN}_append = "${@' /usr/lib/java/*.jar' if '${MACHINE}' == 'intel-baytrail-64' else ''}"

# include nodejs files in /usr/lib/node_modules for 64 bit builds
FILES_${PN}_append = "${@' /usr/lib/node_modules/*' if '${MACHINE}' == 'intel-baytrail-64' else ''}"

INSANE_SKIP_${PN} = "dev-so"

# only for gateways
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
PACKAGES = "${PN}"
FILES_${PN}_append = " ${includedir}"