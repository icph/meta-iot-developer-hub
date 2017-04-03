SUMMARY = "Package Repository Web Interface for gateway"
DESCRIPTION = "Provides website and scripts needed for the Intel Package Repository Web Interface"
LICENSE = "GPLv2"
SECTION = "webconsole"

PR = "r1.0.9"

S = "${WORKDIR}/git"

SRC_URI = "git://github.com/icph/icph;protocol=git;branch=master;rev=ce8f1eba1e10d2820c5718e631c923a916b66a39"
LIC_FILES_CHKSUM = "file://README.md;md5=4dc01af0703b921b12b1e81c5409b0cb"

inherit systemd
EXTRA_OEMAKE = "DESTDIR=${D} PREFIX=/var/www"

do_install () {
    make install DESTDIR=${D} PREFIX=/var/www
}

SYSTEMD_SERVICE_${PN} = "iot-dev-hub.service"

FILES_${PN} += " \
    /lib/systemd/system  \
    /var/www/www-repo-gui \
"

CONFFILES = "/var/www/www-repo-gui/proxy_env /var/www/www-repo-gui/python/developer_hub_config /var/www/www-repo-gui/python/repo_tracking"

RDEPENDS_${PN} = "cherrypy pexpect simplejson routes python-lxml python-netifaces nginx mosquitto node-red-experience node-cloudcmd"
PACKAGES = "${PN}"
