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

package me.shetj.base.ktx

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.VisualMediaType
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.LifecycleEventObserver
import java.util.concurrent.atomic.AtomicInteger

/**
 * 适用于界面创建成功，后动态创建launcher使用
 */

/****************************************************************************************************************/

private val mNextLocalRequestCode: AtomicInteger = AtomicInteger()

fun <I, O> ComponentActivity.register(
    key: String,
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>
) = activityResultRegistry.register(key, contract, callback).also {
    lifecycle.addObserver(
        LifecycleEventObserver { _, event ->
            if (event == Event.ON_DESTROY) {
                it.unregister()
            }
        }
    )
}

fun ComponentActivity.startActivityResultLauncher(
    key: String = "startActivityResult",
    callback: ActivityResultCallback<ActivityResult>
) = register(key, ActivityResultContracts.StartActivityForResult(), callback)

fun ComponentActivity.startRequestPermissionsLauncher(
    key: String = "startRequestMultiplePermissions",
    callback: ActivityResultCallback<Map<String, Boolean>>
) = register(key, ActivityResultContracts.RequestMultiplePermissions(), callback)

fun ComponentActivity.startRequestPermissionLauncher(
    key: String = "startRequestPermission",
    callback: ActivityResultCallback<Boolean>
) = register(key, ActivityResultContracts.RequestPermission(), callback)

//region Activity 部分
/**
 * 获取多个权限
 */
fun ComponentActivity.startRequestPermissions(
    permissions: Array<String>,
    callback: ActivityResultCallback<Map<String, Boolean>>
) {
    if (hasPermission(*permissions)) {
        callback.onActivityResult(permissions.associateWith { true })
        return
    }
    return startRequestPermissionsLauncher("startRequestMultiplePermissions", callback).launch(permissions)
}

/**
 * 获取单个权限
 */
fun ComponentActivity.startRequestPermission(
    permission: String,
    callback: ActivityResultCallback<Boolean>
) {
    if(hasPermission(permission)){
        callback.onActivityResult(true)
        return
    }
    return startRequestPermissionLauncher("startRequestPermission", callback).launch(permission)
}

/**
 * startActivityResult
 */
fun ComponentActivity.startActivityResult(
    intent: Intent,
    callback: ActivityResultCallback<ActivityResult>
) {
    return startActivityResultLauncher("startActivityResult", callback).launch(intent)
}

/**
 * 选择一个文件
 */
fun ComponentActivity.selectFile(type: String = "image/*", callback: ActivityResultCallback<Uri?>) {
    register("GetContent", ActivityResultContracts.GetContent(), callback).launch(type)
}

/**
 * 选择多个文件
 */
fun ComponentActivity.selectMultipleFile(type: String = "image/*", callback: ActivityResultCallback<List<Uri>>) {
    register("selectMultipleFile", ActivityResultContracts.GetMultipleContents(), callback).launch(type)
}

/**
 * 搜索多类型的文件
 */
fun ComponentActivity.searchFile(
    type: Array<String> = arrayOf("image/*"),
    callback: ActivityResultCallback<Uri?>
) {
    register("OpenDocument", ActivityResultContracts.OpenDocument(), callback).launch(type)
}

fun ComponentActivity.searchFiles(type: Array<String>, callback: ActivityResultCallback<List<@JvmSuppressWildcards Uri>>) {
    register("OpenDocuments", ActivityResultContracts.OpenMultipleDocuments(), callback).launch(type)
}

/**
 * 创建文件
 */
fun ComponentActivity.createFile(
    fileName: String,
    mimeType: String = "image/*",
    callback: ActivityResultCallback<Uri?>
) {
    register("CreateDocument", ActivityResultContracts.CreateDocument(mimeType), callback).launch(fileName)
}

/**
 * 拍照
 */
fun ComponentActivity.takePicture(callback: ActivityResultCallback<Uri?>) {
    val pathUri = createImagePathUri(this)
    register("takePicture", ActivityResultContracts.TakePicture()) { result ->
        if (result) {
            callback.onActivityResult(pathUri)
        }
    }.launch(pathUri)
}

/**
 * 视频
 */
fun ComponentActivity.takeVideo(callback: ActivityResultCallback<Uri?>) {
    val pathUri = createVideoPathUri(this)
    register("takeVideo", ActivityResultContracts.CaptureVideo()) { result ->
        if (result) {
            callback.onActivityResult(pathUri)
        }
    }.launch(pathUri)
}

/**
 * 选择联系人
 */
fun ComponentActivity.pickContact(callback: ActivityResultCallback<Uri?>) {
    register("pickContact", ActivityResultContracts.PickContact(), callback).launch(null)
}

/**
 * 剪切图片
 */
fun ComponentActivity.cropImage(imageResult: CropImage, callback: ActivityResultCallback<Uri?>) {
    register("cropImage", CropImageContract(), callback).launch(imageResult)
}

/**
 * Pick visual media
 *
 * @param inputType [PickVisualMediaRequest]
 */
fun ComponentActivity.pickVisualMedia(inputType: PickVisualMediaRequest, callback: ActivityResultCallback<Uri?>) {
    register("PickVisualMedia", ActivityResultContracts.PickVisualMedia(), callback).launch(inputType)
}

fun ComponentActivity.pickVisualMedia(inputType: VisualMediaType, callback: ActivityResultCallback<Uri?>) {
    PickVisualMediaRequest.Builder().setMediaType(inputType).build().let {
        register("PickVisualMedia", ActivityResultContracts.PickVisualMedia(), callback).launch(it)
    }
}

/**
 * Pick visual media
 *
 * @param inputType [PickVisualMediaRequest]
 */
fun ComponentActivity.pickMultipleVisualMedia(
    inputType: PickVisualMediaRequest,
    callback: ActivityResultCallback<List<@JvmSuppressWildcards Uri>>
) {
    register("PickMultipleVisualMedia", ActivityResultContracts.PickMultipleVisualMedia(), callback).launch(inputType)
}

fun ComponentActivity.pickMultipleVisualMedia(inputType: VisualMediaType, callback: ActivityResultCallback<List<@JvmSuppressWildcards Uri>>) {
    PickVisualMediaRequest.Builder().setMediaType(inputType).build().let {
        register("PickMultipleVisualMedia", ActivityResultContracts.PickMultipleVisualMedia(), callback).launch(it)
    }
}

//endregion

/********************************************Fragment 部分********************************************************/

//region Fragment部分
fun Fragment.getActivityResultRegistry(): ActivityResultRegistry? {
    return kotlin.runCatching { requireActivity().activityResultRegistry }.onFailure { it.printStackTrace() }
        .getOrNull()
}

fun <I, O> Fragment.register(
    key: String,
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>
): ActivityResultLauncher<I>? {
    return getActivityResultRegistry()?.register(key, contract, callback)?.also {
        lifecycle.addObserver(
            LifecycleEventObserver { _, event ->
                if (event == Event.ON_DESTROY) {
                    it.unregister()
                }
            }
        )
    }
}

fun Fragment.startActivityResultLauncher(
    key: String = "startActivityResult" + mNextLocalRequestCode.getAndIncrement(),
    callback: ActivityResultCallback<ActivityResult>
): ActivityResultLauncher<Intent>? {
    return register(key, ActivityResultContracts.StartActivityForResult(), callback)
}

fun Fragment.startRequestPermissionsLauncher(
    callback: ActivityResultCallback<Map<String, Boolean>>
): ActivityResultLauncher<Array<String>>? {
    return register(
        "startRequestMultiplePermissions" + mNextLocalRequestCode.getAndIncrement(),
        ActivityResultContracts.RequestMultiplePermissions(),
        callback
    )
}

fun Fragment.startRequestPermissionLauncher(
    callback: ActivityResultCallback<Boolean>
): ActivityResultLauncher<String>? {
    return register(
        "startRequestPermission" + mNextLocalRequestCode.getAndIncrement(),
        ActivityResultContracts.RequestPermission(),
        callback
    )
}

fun Fragment.startRequestPermissions(
    permissions: Array<String>,
    callback: ActivityResultCallback<Map<String, Boolean>>
) {
    if (requireActivity().hasPermission(*permissions)) {
        callback.onActivityResult(permissions.associateWith { true })
        return
    }
    startRequestPermissionsLauncher(callback)?.launch(permissions)
}

fun Fragment.startRequestPermission(
    permission: String,
    callback: ActivityResultCallback<Boolean>
) {
    if (requireActivity().hasPermission(permission)){
        callback.onActivityResult(true)
        return
    }
    startRequestPermissionLauncher(callback)?.launch(permission)
}

/**
 * startActivityResult
 */
fun Fragment.startActivityResult(
    key: String = "startActivityResult" + mNextLocalRequestCode.getAndIncrement(),
    intent: Intent,
    callback: ActivityResultCallback<ActivityResult>
) {
    startActivityResultLauncher(key, callback)?.launch(intent)
}

/**
 * 选择一个文件
 */
fun Fragment.selectFile(type: String = "image/*", callback: ActivityResultCallback<Uri?>) {
    register("GetContent", ActivityResultContracts.GetContent(), callback)?.launch(type)
}

/**
 * 选择多个文件
 */
fun Fragment.selectMultipleFile(type: String = "image/*", callback: ActivityResultCallback<List<Uri>>) {
    register("selectMultipleFile", ActivityResultContracts.GetMultipleContents(), callback)?.launch(type)
}

/**
 * 搜索多类型的文件
 */
fun Fragment.searchFile(type: Array<String>, callback: ActivityResultCallback<Uri?>) {
    register("OpenDocument", ActivityResultContracts.OpenDocument(), callback)?.launch(type)
}

fun Fragment.selectFileByDir(dir: Uri?, callback: ActivityResultCallback<Uri?>) {
    register("OpenDocumentTree", ActivityResultContracts.OpenDocumentTree(), callback)?.launch(dir)
}

/**
 * 创建文件
 */
fun Fragment.createFile(fileName: String, fileType: String = "*/*", callback: ActivityResultCallback<Uri?>) {
    register("CreateDocument", ActivityResultContracts.CreateDocument(fileType), callback)?.launch(fileName)
}

/**
 * 拍照
 */
fun Fragment.takePicture(callback: ActivityResultCallback<Uri?>) {
    val pathUri = createImagePathUri(requireContext())
    register("takePicture", ActivityResultContracts.TakePicture()) { result ->
        if (result) {
            callback.onActivityResult(pathUri)
        } else {
            callback.onActivityResult(null)
        }
    }?.launch(pathUri)
}

/**
 * 视频
 */
fun Fragment.takeVideo(callback: ActivityResultCallback<Uri?>) {
    val pathUri = createVideoPathUri(requireContext())
    register("takeVideo", ActivityResultContracts.CaptureVideo()) { result ->
        if (result) {
            callback.onActivityResult(pathUri)
        } else {
            callback.onActivityResult(null)
        }
    }?.launch(pathUri)
}

/**
 * 选择练习人
 */
fun Fragment.pickContact(callback: ActivityResultCallback<Uri?>) {
    register("pickContact", ActivityResultContracts.PickContact(), callback)?.launch(null)
}

/**
 * Pick visual media
 *
 * @param inputType [PickVisualMediaRequest]
 */
fun Fragment.pickVisualMedia(inputType: PickVisualMediaRequest, callback: ActivityResultCallback<Uri?>) {
    register("PickVisualMedia", ActivityResultContracts.PickVisualMedia(), callback)?.launch(inputType)
}

/**
 * Pick visual media
 *
 * @param inputType [PickVisualMediaRequest]
 */
fun Fragment.pickMultipleVisualMedia(
    inputType: PickVisualMediaRequest,
    callback: ActivityResultCallback<List<@JvmSuppressWildcards Uri>>
) {
    register("PickMultipleVisualMedia", ActivityResultContracts.PickMultipleVisualMedia(), callback)?.launch(inputType)
}

//endregion
