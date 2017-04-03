DESCRIPTION = "Node-RED is a tool for wiring together hardware devices, APIs and online services in new and interesting ways."
HOMEPAGE = "http://nodered.org"
LICENSE = "Apache-2.0"
SUMMARY ="Node-RED is a tool for wiring together hardware devices, APIs and online services in new and interesting ways."

LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

PR = "r0"

SRC_URI = "https://github.com/${PN}/${PN}/releases/download/${PV}/${PN}-${PV}.zip"
SRC_URI[md5sum] = "b89a1309b687af9845d0ae593988e3de"
SRC_URI[sha256sum] = "d4ce01c646550efa96b7c8f9fd117c713d994c9cecd6fc9a53944dd18b384b66"

S = "${WORKDIR}/${PN}-${PV}"

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
}

do_install() {
	install -d ${D}${NODE_MODULES_DIR}${PN}
	cp -r ${S}/* ${D}${NODE_MODULES_DIR}${PN}
}

PACKAGES = "${PN}"
FILES_${PN} = "${NODE_MODULES_DIR}${PN}"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

DEPENDS = "nodejs nodejs-native node-redis node-mongodb"
RDEPENDS_${PN} = "bash python nodejs"