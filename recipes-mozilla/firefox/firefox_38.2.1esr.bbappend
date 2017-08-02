FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "\
            file://0001-firefox-set-homepage-to-show-devhub-homepage.patch \
            "

do_install_append() {
        install -d ${D}${sysconfdir}/xdg/autostart
        install -m 0644 ${WORKDIR}/mozilla-firefox.desktop ${D}${sysconfdir}/xdg/autostart/
}

FILES_${PN} += "${sysconfdir}/xdg/"
