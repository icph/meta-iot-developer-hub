DESCRIPTION = "A collection of flows, scripts, and functions that makes it easy to get started on the Intel IoT Gateway using Node-RED."
HOMEPAGE = "http://www.intel.com"
LICENSE = "MIT"
SUMMARY ="Developer experience package for Node-RED"

PV = "0.13.4"

LIC_FILES_CHKSUM = "file://${THISDIR}/files/LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI = 	"file://LICENSE \
             file://node-red-experience.service \
             file://81-chart-tag.js \
             file://81-chart-tag.html \
             file://flows_IoTGateway.json \
             file://settings.js \
             file://omega.rules \
             file://DefaultTemperature.json \
             file://node-red_nginx_https.conf \
             file://node-red_nginx_http.conf \
             file://doorbell.json \
             "

S = "${WORKDIR}/node_modules/"

inherit systemd useradd


WR_USER = "gwuser"
WR_GROUP = "admin"
WR_PASSWORD = "\$6\$i379JFoua6/\$nxPIN.z9c.AU1ifz78f8nJYgFuT9vmUZUFxfdrbk3wr6phT0GkRRbVBumbRX/3y0f3d.oqb7zHzSOaq3Rz2TN1"
WR_HOME = "/home/${WR_USER}"
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system ${WR_GROUP}"
USERADD_PARAM_${PN} = "-m -d ${WR_HOME} -s /bin/bash -g ${WR_GROUP} -p '${WR_PASSWORD}' ${WR_USER}"
HOME_OWNER_AND_GROUP = "-o ${WR_USER} -g ${WR_GROUP}"

CONFFILES = "/etc/nginx/conf.d/node-red.conf ${D}${WR_HOME}/.node-red/settings.js"

do_install() {
    install -m 0755 ${HOME_OWNER_AND_GROUP} -d ${D}${WR_HOME}
    install -d ${D}/${systemd_unitdir}/system
    install -m 0755 ${HOME_OWNER_AND_GROUP} -d ${D}${WR_HOME}/.node-red/lib/flows
    install -d ${D}/etc/udev/rules.d
    install -m 600 ${WORKDIR}/node-red-experience.service ${D}/${systemd_unitdir}/system/node-red-experience.service
    install -d ${D}/usr/lib/node_modules/node-red/nodes/core/core
    install -m 644 ${WORKDIR}/81-chart-tag.js ${D}/usr/lib/node_modules/node-red/nodes/core/core
    install -m 644 ${WORKDIR}/81-chart-tag.html ${D}/usr/lib/node_modules/node-red/nodes/core/core
    install -m 644 ${WORKDIR}/flows_IoTGateway.json ${D}${WR_HOME}/.node-red/flows_IoTGateway.json
    install -m 644 ${WORKDIR}/node-red_nginx_http.conf ${D}${WR_HOME}/.node-red
    install -m 644 ${WORKDIR}/node-red_nginx_https.conf ${D}${WR_HOME}/.node-red
    install -d ${D}${sysconfdir}/nginx
    install -m 0755 -d ${D}/etc/nginx/conf.d
    install -m 644 ${WORKDIR}/node-red_nginx_http.conf ${D}/etc/nginx/conf.d/node-red.conf
    install -m 644 ${WORKDIR}/settings.js ${D}${WR_HOME}/.node-red
    install -m 644 ${WORKDIR}/omega.rules ${D}/etc/udev/rules.d
    install -m 0644 ${WORKDIR}/DefaultTemperature.json ${D}${WR_HOME}/.node-red/lib/flows
    install -m 0644 ${WORKDIR}/doorbell.json ${D}${WR_HOME}/.node-red/lib/flows
}

SYSTEMD_SERVICE_${PN} = "node-red-experience.service"

FILES_${PN} = "${WR_HOME} /etc/nginx/conf.d ${systemd_unitdir} /etc/udev/rules.d /usr/lib/* "

RCONFLICTS_${PN} = "node node-npm"
RDEPENDS_${PN} += "mosquitto node-red nodejs-npm upm mraa"