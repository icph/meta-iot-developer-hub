DESCRIPTION = "The MongoDB driver is the high level part of the 2.0 or higher MongoDB driver and is meant for end users.."
LICENSE = "Apache-2.0"
SUMMARY = "Mongodb plugin for NodeJS"
SECTION = "devel/node"
HOMEPAGE = "https://github.com/mongodb/node-mongodb-native"
PR = "r1.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=6c4db32a2fa8717faffa1d4f10136f47"

SRC_URI = "git://github.com/mongodb/node-mongodb-native.git;protocol=git;branch=2.0;rev=6b5ca53cffa9969839080be5baed7b52fe3d92ff"

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
}

do_install() {
	install -d ${D}${NODE_MODULES_DIR}/mongodb
	cp -r ${S}/* ${D}${NODE_MODULES_DIR}/mongodb
}

FILES_${PN} = "/usr/lib/node_modules/mongodb"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

DEPENDS = "nodejs nodejs-native"
RDEPENDS_${PN} = "bash python nodejs"