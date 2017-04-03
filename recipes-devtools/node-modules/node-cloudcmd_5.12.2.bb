DESCRIPTION = "Cloud Commander orthodox web file manager with console and editor"
LICENSE = "MIT"
SUMMARY = "Cloud Commander orthodox web file manager with console and editor"
SECTION = "devel/node"
HOMEPAGE = "https://github.com/coderaiser/cloudcmd"
PR = "r1.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=75aa809c9a8d056405393271d484dc67"

FILESEXTRAPATHS_prepend := "${THISDIR}/node-cloudcmd:"
SRC_URI = 	"git://github.com/coderaiser/cloudcmd.git;protocol=git;branch=master;rev=de2144819e7418a1921e2e8c58d811ba6c1acf09 \
             file://node-cloudcmd.service \
             file://index.html \
             file://node-cloudcmd_nginx_https.conf \
             file://node-cloudcmd_nginx_http.conf"

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

CONFFILES = "/etc/nginx/conf.d/node-cloudcmd.conf"

S = "${WORKDIR}/git"

NODE_MODULES_DIR = "${prefix}/lib/node_modules/"
NPM_CACHE_DIR ?= "${WORKDIR}/npm_cache"
NPM_REGISTRY ?= "https://registry.npmjs.org/"
NPM_INSTALL_FLAGS ?= "--production"

do_compile() {
	export NPM_CONFIG_CACHE="${NPM_CACHE_DIR}"

	npm config set dev false
	npm set strict-ssl false
	npm config set registry http://registry.npmjs.org/

	# configure http proxy if neccessary
	if [ -n "${http_proxy}" ]; then
	    npm config set proxy ${http_proxy}
	fi

	if [ -n "${HTTP_PROXY}" ]; then
	    npm config set proxy ${HTTP_PROXY}
	fi
	
	# Clear cache
	npm cache clear

	# Install
	npm --registry=${NPM_REGISTRY} --arch=${TARGET_ARCH} --target_arch=${TARGET_ARCH} ${NPM_INSTALL_FLAGS} install
	npm prune --production
	grep -lrIZ "#!/usr/bin/spidermonkey" ${S}/node_modules | xargs -0 rm -f --
}

do_install() {
	install -d ${D}${NODE_MODULES_DIR}/cloudcmd
	cp -r ${S}/* ${D}${NODE_MODULES_DIR}/cloudcmd
	install -m 0755 ${HOME_OWNER_AND_GROUP} -d ${D}${WR_HOME}
	install -m 0755 ${HOME_OWNER_AND_GROUP} -d ${D}${WR_HOME}/.node-cloudcmd
	install -m 644 ${WORKDIR}/node-cloudcmd_nginx_http.conf ${D}${WR_HOME}/.node-cloudcmd
	install -m 644 ${WORKDIR}/node-cloudcmd_nginx_https.conf ${D}${WR_HOME}/.node-cloudcmd
	install -d ${D}${sysconfdir}/nginx
	install -m 0755 -d ${D}/etc/nginx/conf.d
	install -m 644 ${WORKDIR}/node-cloudcmd_nginx_http.conf  ${D}/etc/nginx/conf.d/node-cloudcmd.conf
	install -d ${D}/${systemd_unitdir}/system
	install -m 600 ${WORKDIR}/node-cloudcmd.service ${D}${base_libdir}/systemd/system/node-cloudcmd.service
	rm ${D}/usr/lib/node_modules/cloudcmd/html/index.html
	install -m 644 ${WORKDIR}/index.html ${D}/usr/lib/node_modules/cloudcmd/html
}

SYSTEMD_SERVICE_${PN} = "node-cloudcmd.service"
DEPENDS = "nodejs nodejs-native"
INSANE_SKIP_${PN} = "staticdev debug-files file-rdeps"
FILES_${PN} = "${WR_HOME} /usr/lib /etc/nginx/conf.d /lib/systemd"
RDEPENDS_${PN} = "nodejs"