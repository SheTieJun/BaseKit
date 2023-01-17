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
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MainThread
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * 适用于界面创建成功，后动态创建launcher使用
 */

/****************************************************************************************************************/

@MainThread
fun <I, O> ComponentActivity.register(
    @NonNull key: String,
    @NonNull contract: ActivityResultContract<I, O>,
    @NonNull callback: ActivityResultCallback<O>
): ActivityResultLauncher<I> {
    return activityResultRegistry.register(key, contract, callback).also {
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Event) {
                if (event == Event.ON_DESTROY) {
                    it.unregister()
                }
            }
        })
    }
}

@MainThread
fun ComponentActivity.startActivityResultLauncher(
    @NonNull key: String = "startActivityResultLauncher",
    @NonNull callback: ActivityResultCallback<ActivityResult>
): ActivityResultLauncher<Intent> {
    return register(key, ActivityResultContracts.StartActivityForResult(), callback)
}

@MainThread
fun ComponentActivity.startRequestPermissionsLauncher(
    @NonNull key: String = "startRequestPermissionsLauncher",
    @NonNull callback: ActivityResultCallback<Map<String, Boolean>>
): ActivityResultLauncher<Array<String>> {
    return register(key, ActivityResultContracts.RequestMultiplePermissions(), callback)
}

@MainThread
fun ComponentActivity.startRequestPermissionLauncher(
    @NonNull key: String = "startRequestPermissionLauncher",
    @NonNull callback: ActivityResultCallback<Boolean>
): ActivityResultLauncher<String> {
    return register(key, ActivityResultContracts.RequestPermission(), callback)
}


//region Activity 部分
/**
 * 获取多个权限
 */
@MainThread
fun ComponentActivity.startRequestPermissions(
    @NonNull key: String = "startRequestMultiplePermissions",
    @NonNull permissions: Array<String>,
    @NonNull callback: ActivityResultCallback<Map<String, Boolean>>
) {
    return startRequestPermissionsLauncher(key, callback).launch(permissions)
}


/**
 * 获取单个权限
 */
@MainThread
fun ComponentActivity.startRequestPermission(
    @NonNull key: String = "startRequestPermission",
    @NonNull permission: String,
    @NonNull callback: ActivityResultCallback<Boolean>
) {
    return startRequestPermissionLauncher(key, callback).launch(permission)
}


/**
 * startActivityResult
 */
@MainThread
fun ComponentActivity.startActivityResult(
    @NonNull key: String = "startActivityResult",
    @NonNull intent: Intent,
    @NonNull callback: ActivityResultCallback<ActivityResult>
) {
    return startActivityResultLauncher(key, callback).launch(intent)
}


/**
 * 选择一个文件
 */
@MainThread
fun AppCompatActivity.selectFile(type: String = "image/*", callback: ActivityResultCallback<Uri?>) {
    register("GetContent", ActivityResultContracts.GetContent(), callback).launch(type)
}

/**
 * 选择多个文件
 */
@MainThread
fun AppCompatActivity.selectMultipleFile(type: String = "image/*", callback: ActivityResultCallback<List<Uri>>) {
    register("selectMultipleFile", ActivityResultContracts.GetMultipleContents(), callback).launch(type)
}

/**
 * 搜索多类型的文件
 */
@MainThread
fun AppCompatActivity.searchFile(type: Array<String>, callback: ActivityResultCallback<Uri?>) {
    register("OpenDocument", ActivityResultContracts.OpenDocument(), callback).launch(type)
}


/**
 * 搜索多类型的文件
 */
@MainThread
fun AppCompatActivity.searchFiles(type: Array<String>, callback: ActivityResultCallback<List<@JvmSuppressWildcards Uri>>) {
    register("OpenDocument", ActivityResultContracts.OpenMultipleDocuments(), callback).launch(type)
}


/**
 * 创建文件
 * @param type image/png
*"
*/
@MainThread
fun AppCompatActivity.createFile(
    fileName: String,
    type: String = "*/*",
    callback: ActivityResultCallback<Uri?>
) {
    register("CreateDocument", ActivityResultContracts.CreateDocument(type), callback).launch(fileName)
}


/**
 * 拍照
 */
@MainThread
fun AppCompatActivity.takePicture(callback: ActivityResultCallback<Uri?>) {
    val pathUri = createImagePathUri(this)
    register("takePicture", ActivityResultContracts.TakePicture()) { result ->
        if (result) {
            callback.onActivityResult(pathUri)
        } else {
            callback.onActivityResult(null)
        }
    }.launch(pathUri)
}

/**
 * 视频
 */
@MainThread
fun AppCompatActivity.takeVideo(callback: ActivityResultCallback<Uri?>) {
    val pathUri = createVideoPathUri(this)
    register("takeVideo", ActivityResultContracts.CaptureVideo()) { result ->
        if (result) {
            callback.onActivityResult(pathUri)
        } else {
            callback.onActivityResult(null)
        }
    }.launch(pathUri)
}


/**
 * 选择练习人
 */
@MainThread
fun AppCompatActivity.pickContact(callback: ActivityResultCallback<Uri?>) {
    register("pickContact", ActivityResultContracts.PickContact(), callback).launch(null)
}

//endregion

/********************************************Fragment 部分********************************************************/

//region Fragment部分
fun Fragment.getActivityResultRegistry(): ActivityResultRegistry? {
    return kotlin.runCatching { requireActivity().activityResultRegistry }.onFailure { it.printStackTrace() }
        .getOrNull()
}

@MainThread
fun <I, O> Fragment.register(
    @NonNull key: String,
    @NonNull contract: ActivityResultContract<I, O>,
    @NonNull callback: ActivityResultCallback<O>
): ActivityResultLauncher<I>? {
    return getActivityResultRegistry()?.register(key, contract, callback).also {
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Event) {
                if (event == Event.ON_DESTROY) {
                    it?.unregister()
                }
            }
        })
    }
}
@MainThread
fun Fragment.startActivityResultLauncher(
    @NonNull key: String = "startActivityResultLauncher",
    @NonNull callback: ActivityResultCallback<ActivityResult>
): ActivityResultLauncher<Intent>? {
    return register(key, ActivityResultContracts.StartActivityForResult(), callback)
}
@MainThread
fun Fragment.startRequestPermissionsLauncher(
    @NonNull key: String = "startRequestPermissionsLauncher",
    @NonNull callback: ActivityResultCallback<Map<String, Boolean>>
): ActivityResultLauncher<Array<String>>? {
    return register(key, ActivityResultContracts.RequestMultiplePermissions(), callback)
}
@MainThread
fun Fragment.startRequestPermissionLauncher(
    @NonNull key: String = "startRequestPermissionLauncher",
    @NonNull callback: ActivityResultCallback<Boolean>
): ActivityResultLauncher<String>? {
    return register(key, ActivityResultContracts.RequestPermission(), callback)
}

@MainThread
fun Fragment.startRequestPermissions(
    @NonNull key: String = "startRequestMultiplePermissions",
    @NonNull permissions: Array<String>,
    @NonNull callback: ActivityResultCallback<Map<String, Boolean>>
) {
    startRequestPermissionsLauncher(key, callback)?.launch(permissions)
}

@MainThread
fun Fragment.startRequestPermission(
    @NonNull key: String = "startRequestPermission",
    @NonNull permission: String,
    @NonNull callback: ActivityResultCallback<Boolean>
) {
    startRequestPermissionLauncher(key, callback)?.launch(permission)
}


/**
 * startActivityResult
 */
@MainThread
fun Fragment.startActivityResult(
    @NonNull key: String = "startActivityResult",
    @NonNull intent: Intent,
    @NonNull callback: ActivityResultCallback<ActivityResult>
) {
    startActivityResultLauncher(key, callback)?.launch(intent)
}

/**
 * 选择一个文件
 */
@MainThread
fun Fragment.selectFile(type: String = "image/*", callback: ActivityResultCallback<Uri?>) {
    register("GetContent", ActivityResultContracts.GetContent(), callback)?.launch(type)
}

/**
 * 选择多个文件
 */
@MainThread
fun Fragment.selectMultipleFile(type: String = "image/*", callback: ActivityResultCallback<List<Uri>>) {
    register("selectMultipleFile", ActivityResultContracts.GetMultipleContents(), callback)?.launch(type)
}

/**
 * 搜索多类型的文件
 */
@MainThread
fun Fragment.searchFile(type: Array<String>, callback: ActivityResultCallback<Uri?>) {
    register("OpenDocument", ActivityResultContracts.OpenDocument(), callback)?.launch(type)
}

@MainThread
fun Fragment.selectFileByDir(dir: Uri?, callback: ActivityResultCallback<Uri?>) {
    register("OpenDocumentTree", ActivityResultContracts.OpenDocumentTree(), callback)?.launch(dir)
}

/**
 * 创建文件
 */
@MainThread
fun Fragment.createFile(fileName: String, type: String = "*/*", callback: ActivityResultCallback<Uri?>) {
    register("CreateDocument", ActivityResultContracts.CreateDocument(type), callback)?.launch(fileName)
}


/**
 * 拍照
 */
@MainThread
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
@MainThread
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
 * 选择联系人
 */
@MainThread
fun Fragment.pickContact(callback: ActivityResultCallback<Uri?>) {
    register("pickContact", ActivityResultContracts.PickContact(), callback)?.launch(null)
}

//endregion

/***************************************************Context 部分*******************************************************/
@MainThread
fun <I, O> Any.register(
    @NonNull key: String,
    @NonNull contract: ActivityResultContract<I, O>,
    @NonNull callback: ActivityResultCallback<O>
): ActivityResultLauncher<I>? {
    return when (this) {
        is ComponentActivity -> this.register(key, contract, callback)
        is Fragment -> this.register(key, contract, callback)
        else -> null
    }
}


