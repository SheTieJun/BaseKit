/*
 * MIT License
 *
 * Copyright (c) 2021 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package asm

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes


/**
 * 1. getLastKnownLocation 定位方法
 * 2. getDeviceId获取设备信息方法
 * 3. getSubscriberId imsi获取方法
 * 4. getMacAddress 低版本系统获取mac地方方法
 * 5. getHardwareAddress 获取mac地址方法acy method set
 * 6. getInstalledPackages 获取应用类别
 */

val privacyMethodSet = setOf(
    "getLastKnownLocation",//√
    "getDeviceId", //√
    "getSubscriberId",
    "getMacAddress",
    "getHardwareAddress",
    "getInstalledPackages" //无法通过代码检测
)


val privacyClassSet = setOf(
    "android/telephony/TelephonyManager",
    "android/net/wifi/WifiInfo",
    "java/net/NetworkInterface",
    "android/location/Location",
    "android/location/LocationManager",
    "android/content/pm"//无法检测
)

/**
 * Privacy class visitor
 *
 * 1. getLastKnownLocation 定位方法
 * 2. getDeviceId 获取设备信息方法
 * 3. getSubscriberId imsi获取方法
 * 4. getMacAddress 低版本系统获取mac地方方法
 * 5. getHardwareAddress 获取mac地址方法
 * @constructor
 *
 * @param classVisitor
 */
class PrivacyClassVisitor(classVisitor: ClassVisitor) :
    ClassVisitor(Opcodes.ASM9, classVisitor), Opcodes {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        if (privacyMethodSet.contains(name) && privacyClassSet.find { descriptor?.contains(it, true) == true } != null) {
            println("隐私检测插入:$name")
            return PrivacyMethodVisitor(super.visitMethod(access, name, descriptor, signature, exceptions))
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }
}