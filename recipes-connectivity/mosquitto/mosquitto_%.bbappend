
inherit systemd useradd 

FILESEXTRAPATHS_append := "${THISDIR}/files"
SRC_URI += "file://mosquitto.conf"

do_compile_prepend() {
   cd ${S}; sed -i "s/WITH_WEBSOCKETS:=no/WITH_WEBSOCKETS:=yes/g" config.mk
   cd ${S}; sed -i "s/WITH_EC:=yes/WITH_EC:=no/g" config.mk
}

do_install_append() {
    install -d -m 0755 ${D}${sysconfdir}/mosquitto/
    install -m 0755 ${WORKDIR}/mosquitto.conf ${D}${sysconfdir}/mosquitto/
}

# useradd and user group for mosquitto
USERADD_PACKAGES = "${PN}"
M_USER = "mosquitto"
M_GROUP = "mosquitto"
GROUPADD_PARAM_${PN} = "--system ${M_GROUP}"
USERADD_PARAM_${PN} = "--system -s /bin/bash -g ${M_GROUP} ${M_USER}"
M_OWNER_AND_GROUP = "-o ${M_USER} -g ${M_GROUP}"

DEPENDS += "c-ares libwebsockets"
RDEPENDS_${PN} += "c-ares libwebsockets"