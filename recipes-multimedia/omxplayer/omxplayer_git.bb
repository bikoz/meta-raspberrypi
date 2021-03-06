SUMMARY = "A commandline OMX player for the Raspberry Pi"
DESCRIPTION = "This player was developed as a testbed for the XBMC \
Raspberry PI implementation and is quite handy to use standalone"
HOMEPAGE = "https://github.com/popcornmix/omxplayer"
SECTION = "console/utils"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "libpcre libav virtual/egl boost freetype dbus openssl"
PR = "r3"

SRCREV = "f3221825a92f27c5f49a52200ff933e454977ed2"
SRC_URI = "git://github.com/popcornmix/omxplayer.git;protocol=git;branch=master \
           file://0001-Remove-Makefile.include-which-includes-hardcoded.patch \
           file://0002-Libraries-and-headers-from-ffmpeg-are-installed-in-u.patch \
           file://0003-Remove-strip-step-in-Makefile.patch \
           file://fix-tar-command-with-DIST.patch \
           "
S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "raspberrypi"

inherit autotools-brokensep

# Needed in ffmpeg configure
export TEMPDIR = "${S}/tmp"

# Needed in Makefile.ffmpeg
export HOST = "${HOST_SYS}"
export WORK = "${S}"
export FLOAT = "${@bb.utils.contains("TUNE_FEATURES", "callconvention-hard", "hard", "softfp", d)}"

export LDFLAGS = "-L${S}/ffmpeg_compiled/usr/lib \
                  -L${STAGING_DIR_HOST}/lib \
                  -L${STAGING_DIR_HOST}/usr/lib \
                 "

export INCLUDES = "-isystem${STAGING_DIR_HOST}/usr/include \
                   -isystem${STAGING_DIR_HOST}/usr/include/interface/vcos/pthreads \
                   -isystem${STAGING_DIR_HOST}/usr/include/freetype2 \
                   -isystem${STAGING_DIR_HOST}/usr/include/interface/vmcs_host/linux \
                   -isystem${STAGING_DIR_HOST}/usr/include/dbus-1.0 \
                   -isystem${STAGING_DIR_HOST}/usr/lib/dbus-1.0/include \
                  "

# Install in ${D}
export DIST = "${D}"

do_compile() {
    # Needed for compiler test in ffmpeg's configure
    mkdir -p tmp

    oe_runmake ffmpeg
    oe_runmake
}

do_install() {
    oe_runmake STRIP='echo skipping strip' dist
    mkdir -p ${D}${datadir}/fonts/truetype/freefont/
    install ${S}/fonts/* ${D}${datadir}/fonts/truetype/freefont/
}

FILES_${PN} = "${bindir}/omxplayer* \
               ${libdir}/omxplayer/lib*${SOLIBS} \
               ${datadir}/fonts"

FILES_${PN}-dev += "${libdir}/omxplayer/*.so"

RDEPENDS_${PN} += "bash procps"
