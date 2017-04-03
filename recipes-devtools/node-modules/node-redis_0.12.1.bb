DESCRIPTION = "This is a complete Redis client for node.js. It supports all Redis commands, including many recently added commands like EVAL from experimental Redis server branches."
LICENSE = "MIT"
SUMMARY = "A complete Redis client for node.js."
SECTION = "devel/node"
HOMEPAGE = "https://github.com/NodeRedis/node_redis"
PR = "r1.0"
LIC_FILES_CHKSUM = "file://README.md;md5=52218f5372aa3a3f2d0dbd5d261f337c"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI = "git://github.com/NodeRedis/node_redis.git;protocol=git;branch=master;rev=38ae7a7f507a71224e24a02d703a959f1b0fc657"

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
	install -d ${D}${NODE_MODULES_DIR}/redis
	cp -r ${S}/* ${D}${NODE_MODULES_DIR}/redis
}

FILES_${PN} = "/usr/lib/node_modules/redis"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

DEPENDS = "nodejs nodejs-native"
RDEPENDS_${PN} = "bash python nodejs"