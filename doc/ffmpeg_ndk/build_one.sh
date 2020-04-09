#!/bin/bash
export TMPDIR=/mnt/c/other/ffmpeglib #设置编译中临时文件目录，不然会报错 unable to create temporary file

# + TMPDIR为编译生成的临时文件存放的目录
# + SYSROOT为so文件支持的最低Android版本的平台目录
# + CPU为指定so文件支持的平台
# + PREFIX为生成的so文件存放目录
# + TOOLCHAIN为编译所使用的工具链目录
# + cross-prefix为编译所使用的工具链文件
# + enable和disable指定了需要编译的项
# + target-os为目标操作系统；

ARCH=arm
# 配置NDK路径 C:\other\ndk\android-ndk-r20
NDK=/mnt/c/other/ndk/android-ndk-r20
# C:\other\sdk\ndk\20.0.5594570\toolchains\llvm\prebuilt\windows-x86_64
TOOLCHAIN=$NDK/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64
API=29

#armv8-a
ARCH=arm64
CPU=armv8-a
CC=$TOOLCHAIN/bin/aarch64-linux-android$API-clang
CXX=$TOOLCHAIN/bin/aarch64-linux-android$API-clang++
SYSROOT=$NDK/toolchains/llvm/prebuilt/linux-x86_64/sysroot
CROSS_PREFIX=$TOOLCHAIN/bin/aarch64-linux-android-
PREFIX=/mnt/c/other/ffmpeglib/android3/$CPU
OPTIMIZE_CFLAGS="-march=$CPU"
ADDI_LDFLAGS=""

function build_android
{
echo "Compiling FFmpeg for $CPU"
./configure \
    --prefix=$PREFIX \
    --disable-neon \
    --disable-hwaccels \
    --disable-gpl \
    --disable-postproc \
    --enable-static \
    --disable-shared \
    --enable-small \
    --enable-jni \
    --disable-mediacodec \
    --disable-decoder=h264_mediacodec \
    --disable-doc \
    --disable-ffmpeg \
    --disable-ffplay \
    --disable-ffprobe \
    --disable-avdevice \
    --disable-doc \
    --disable-symver \
    --cross-prefix=$CROSS_PREFIX \
    --target-os=android \
    --arch=$ARCH \
    --cpu=$CPU \
    --cc=$CC
    --cxx=$CXX
    --sysroot=$SYSROOT \
    --cross-prefix=$TOOLCHAIN/bin/arm-linux-androideabi- \
    --extra-cflags="-Os -fpic $OPTIMIZE_CFLAGS" \
    --extra-ldflags="$ADDI_LDFLAGS" \

$ADDITIONAL_CONFIGURE_FLAG
sed -i '' 's/HAVE_LRINT 0/HAVE_LRINT 1/g' config.h
sed -i '' 's/HAVE_LRINTF 0/HAVE_LRINTF 1/g' config.h
sed -i '' 's/HAVE_ROUND 0/HAVE_ROUND 1/g' config.h
sed -i '' 's/HAVE_ROUNDF 0/HAVE_ROUNDF 1/g' config.h
sed -i '' 's/HAVE_TRUNC 0/HAVE_TRUNC 1/g' config.h
sed -i '' 's/HAVE_TRUNCF 0/HAVE_TRUNCF 1/g' config.h
sed -i '' 's/HAVE_CBRT 0/HAVE_CBRT 1/g' config.h
sed -i '' 's/HAVE_RINT 0/HAVE_RINT 1/g' config.h

make clean
# 这里是定义用几个CPU编译，我用4个，一般在5分钟之内编译完成
make -j8
make install
# $TOOLCHAIN/bin/arm-linux-androideabi-ar d libavcodec/libavcodec.a inverse.o
$TOOLCHAIN/bin/arm-linux-androideabi-ld \
-rpath-link=$PLATFORM/usr/lib \
-L$PLATFORM/usr/lib \
-L$PREFIX/lib \
-soname libffmpeg.so -shared -nostdlib -Bsymbolic --whole-archive --no-undefined -o \
$PREFIX/libffmpeg.so \
libavformat/libavformat.a \
libavcodec/libavcodec.a \
libavutil/libavutil.a \
libavfilter/libavfilter.a \
libswscale/libswscale.a \
libswresample/libswresample.a \
-lc -lm -lz -ldl -llog --dynamic-linker=/system/bin/linker \
$TOOLCHAIN/lib/gcc/arm-linux-androideabi/4.9.x/libgcc.a

# 优化so
$TOOLCHAIN/bin/arm-linux-androideabi-strip  $PREFIX/libffmpeg.so
echo "The Compilation of FFmpeg for $CPU is completed"
}





# $PREBUILT/bin/arm-linux-androideabi-ld 
# -rpath-link=$PLATFORM/usr/lib 
# -L$PLATFORM/usr/lib  
# -soname libffmpeg.so -shared -nostdlib  -z noexecstack -Bsymbolic --whole-archive --no-undefined -o 
# $PREFIX/libffmpeg.so 
# libavcodec/libavcodec.a 
# libavformat/libavformat.a 
# libavutil/libavutil.a 
# libavfilter/libavfilter.a 
# libswscale/libswscale.a 
# libavdevice/libavdevice.a 
# libswresample/libswresample.a
#  -lc -lm -lz -ldl -llog --dynamic-linker=/system/bin/linker 
#  $PREBUILT/lib/gcc/arm-linux-androideabi/4.9.x/libgcc.a

 