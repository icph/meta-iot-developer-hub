SUMMARY = "HTTP and reverse proxy server"

DESCRIPTION = "Nginx is a web server and a reverse proxy server for \
HTTP, SMTP, POP3 and IMAP protocols, with a strong focus on high  \
concurrency, performance and low memory usage."

HOMEPAGE = "http://nginx.org/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9d3b27bad611f5204a84ba6a572698e1"
SECTION = "net"

DEPENDS = "libpcre gzip openssl"

PR = "r1"

SRC_URI = " \
	http://nginx.org/download/nginx-${PV}.tar.gz \
	file://0001-Modified-for-cross-compile.patch \
	file://0001-http-header-hide-server.patch \
	file://nginx.conf \
	file://nginx_passwd \
	file://nginx-ssl-cert \
	file://nginx-pass \
	file://nginx.init \
	file://nginx.service \
	file://nginx-volatile.conf \
	"

SRC_URI[md5sum] = "8efa354f1c3c2ccf434a50d3fbe82340"
SRC_URI[sha256sum] = "943ad757a1c3e8b3df2d5c4ddacc508861922e36fa10ea6f8e3a348fc9abfc1a"

inherit update-rc.d useradd systemd

do_configure () {
	if [ "${SITEINFO_BITS}" = "64" ]; then
		PTRSIZE=8
	else
		PTRSIZE=4
	fi

	./configure \
	--crossbuild=Linux:${TUNE_ARCH} \
	--with-endian=${@base_conditional('SITEINFO_ENDIANNESS', 'le', 'little', 'big', d)} \
	--with-int=4 \
	--with-long=${PTRSIZE} \
	--with-long-long=8 \
	--with-ptr-size=${PTRSIZE} \
	--with-sig-atomic-t=${PTRSIZE} \
	--with-size-t=${PTRSIZE} \
	--with-off-t=${PTRSIZE} \
	--with-time-t=${PTRSIZE} \
	--with-sys-nerr=132 \
	--conf-path=${sysconfdir}/nginx/nginx.conf \
	--http-log-path=${localstatedir}/log/nginx/access.log \
	--error-log-path=${localstatedir}/log/nginx/error.log \
	--pid-path=${localstatedir}/run/nginx/nginx.pid \
	--prefix=${prefix} \
	--with-http_ssl_module \
	--with-http_gzip_static_module \
}

CONFIG_FILE = "nginx.conf"

do_install () {
	oe_runmake 'DESTDIR=${D}' install
	rm -fr ${D}${localstatedir}/run ${D}/run

	install -d -m 0755 ${D}/${sbindir}
	install -m 0755 ${WORKDIR}/nginx-pass  ${D}/${sbindir}
	install -m 0755 ${WORKDIR}/nginx-ssl-cert  ${D}/${sbindir}

	install -d ${D}${localstatedir}/www/localhost
	mv ${D}/usr/html ${D}${localstatedir}/www/localhost/

	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/nginx.init ${D}${sysconfdir}/init.d/nginx
	sed -i 's,/usr/sbin/,${sbindir}/,g' ${D}${sysconfdir}/init.d/nginx
	sed -i 's,/etc/,${sysconfdir}/,g'  ${D}${sysconfdir}/init.d/nginx

	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/nginx.service ${D}${systemd_unitdir}/system

	install -d ${D}${sysconfdir}/nginx
	install -m 0644 ${WORKDIR}/${CONFIG_FILE} ${D}${sysconfdir}/nginx/nginx.conf
	install -d ${D}${sysconfdir}/nginx/sites-enabled
	install -m 0755 -d ${D}/etc/nginx/conf.d
	install -m 0644 ${WORKDIR}/nginx_passwd ${D}${sysconfdir}/nginx/nginx_passwd

	install -d ${D}${sysconfdir}/default/volatiles
	install -m 0644 ${WORKDIR}/nginx-volatile.conf ${D}${sysconfdir}/default/volatiles/99_nginx
}

pkg_postinst_${PN} () {
	if [ -z "$D" ]; then
		if type systemd-tmpfiles >/dev/null; then
			systemd-tmpfiles --create
		elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
			${sysconfdir}/init.d/populate-volatile.sh update
		fi
	fi
}

FILES_${PN} += "${localstatedir}/"

CONFFILES_${PN} = "${sysconfdir}/nginx/nginx.conf \
		${sysconfdir}/nginx/nginx_passwd \
		${sysconfdir}/nginx/fastcgi.conf \
		${sysconfdir}/nginx/fastcgi_params \
		${sysconfdir}/nginx/koi-utf \
		${sysconfdir}/nginx/koi-win \
		${sysconfdir}/nginx/mime.types \
		${sysconfdir}/nginx/scgi_params \
		${sysconfdir}/nginx/uwsgi_params \
		${sysconfdir}/nginx/win-utf \
"

INITSCRIPT_NAME = "nginx"
INITSCRIPT_PARAMS = "defaults 92 20"

SYSTEMD_SERVICE_${PN} = "nginx.service"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = " \
    --system --no-create-home \
    --home ${localstatedir}/www/localhost \
    --groups www-data \
    --user-group www"