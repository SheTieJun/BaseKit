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
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.atomic.AtomicInteger

/**
 * 适用于界面创建成功，后动态创建launcher使用
 */

/****************************************************************************************************************/


private val mNextLocalRequestCode: AtomicInteger = AtomicInteger()

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


fun ComponentActivity.startActivityResultLauncher(
    @NonNull key: String = "startActivityResult" + mNextLocalRequestCode.getAndIncrement(),
    @NonNull callback: ActivityResultCallback<ActivityResult>
): ActivityResultLauncher<Intent> {
    return register(key, ActivityResultContracts.StartActivityForResult(), callback)
}

fun ComponentActivity.startRequestPermissionsLauncher(
    @NonNull key: String = "startRequestMultiplePermissions" + mNextLocalRequestCode.getAndIncrement(),
    @NonNull callback: ActivityResultCallback<Map<String, Boolean>>
): ActivityResultLauncher<Array<String>> {
    return register(key, ActivityResultContracts.RequestMultiplePermissions(), callback)
}

fun ComponentActivity.startRequestPermissionLauncher(
    @NonNull key: String = "startRequestPermission" + mNextLocalRequestCode.getAndIncrement(),
    @NonNull callback: ActivityResultCallback<Boolean>
): ActivityResultLauncher<String> {
    return register(key, ActivityResultContracts.RequestPermission(), callback)
}


fun ComponentActivity.startRequestPermissions(
    @NonNull key: String = "startRequestMultiplePermissions" + mNextLocalRequestCode.getAndIncrement(),
    @NonNull permissions: Array<String>,
    @NonNull callback: ActivityResultCallback<Map<String, Boolean>>
) {
    return startRequestPermissionsLauncher(key, callback).launch(permissions)
}


fun ComponentActivity.startRequestPermission(
    @NonNull key: String = "startRequestPermission" + mNextLocalRequestCode.getAndIncrement(),
    @NonNull permission: String,
    @NonNull callback: ActivityResultCallback<Boolean>
) {
    return startRequestPermissionLauncher(key, callback).launch(permission)
}


/**
 * startActivityResult
 */
fun ComponentActivity.startActivityResult(
    @NonNull key: String = "startActivityResult" + mNextLocalRequestCode.getAndIncrement(),
    @NonNull intent: Intent,
    @NonNull callback: ActivityResultCallback<ActivityResult>
) {
    return startActivityResultLauncher(key, callback).launch(intent)
}


/**
 * 搜索文档
 */
fun AppCompatActivity.searchTypeFile(type: String = "image/*", callback: ActivityResultCallback<Uri?>) {
    register("GetContent", ActivityResultContracts.GetContent(), callback).launch(type)
}

/**
 * 搜索多类型的文件
 */
fun AppCompatActivity.searchFile(type: Array<String>, callback: ActivityResultCallback<Uri?>) {
    register("OpenDocument", ActivityResultContracts.OpenDocument(), callback).launch(type)
}


/**
 * 创建文件
 */
fun AppCompatActivity.createFile(fileName: String, callback: ActivityResultCallback<Uri?>) {
    register("CreateDocument", ActivityResultContracts.CreateDocument(), callback).launch(fileName)
}


/********************************************Fragment 部分********************************************************/


fun Fragment.getActivityResultRegistry(): ActivityResultRegistry? {
    return kotlin.runCatching { requireActivity().activityResultRegistry }.onFailure { it.printStackTrace() }
        .getOrNull()
}

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

fun Fragment.startActivityResultLauncher(
    @NonNull key: String = "startActivityResult" + mNextLocalRequestCode.getAndIncrement(),
    @NonNull callback: ActivityResultCallback<ActivityResult>
): ActivityResultLauncher<Intent>? {
    return register(key, ActivityResultContracts.StartActivityForResult(), callback)
}

fun Fragment.startRequestPermissionsLauncher(
    @NonNull key: String = "startRequestMultiplePermissions" + mNextLocalRequestCode.getAndIncrement(),
    @NonNull callback: ActivityResultCallback<Map<String, Boolean>>
): ActivityResultLauncher<Array<String>>? {
    return register(key, ActivityResultContracts.RequestMultiplePermissions(), callback)
}

fun Fragment.startRequestPermissionLauncher(
    @NonNull key: String = "startRequestPermission" + mNextLocalRequestCode.getAndIncrement(),
    @NonNull callback: ActivityResultCallback<Boolean>
): ActivityResultLauncher<String>? {
    return register(key, ActivityResultContracts.RequestPermission(), callback)
}


fun Fragment.startRequestPermissions(
    @NonNull key: String = "startRequestMultiplePermissions" + mNextLocalRequestCode.getAndIncrement(),
    @NonNull permissions: Array<String>,
    @NonNull callback: ActivityResultCallback<Map<String, Boolean>>
) {
    startRequestPermissionsLauncher(key, callback)?.launch(permissions)
}


fun Fragment.startRequestPermission(
    @NonNull key: String = "startRequestPermission" + mNextLocalRequestCode.getAndIncrement(),
    @NonNull permission: String,
    @NonNull callback: ActivityResultCallback<Boolean>
) {
    startRequestPermissionLauncher(key, callback)?.launch(permission)
}


/**
 * startActivityResult
 */
fun Fragment.startActivityResult(
    @NonNull key: String = "startActivityResult" + mNextLocalRequestCode.getAndIncrement(),
    @NonNull intent: Intent,
    @NonNull callback: ActivityResultCallback<ActivityResult>
) {
    startActivityResultLauncher(key, callback)?.launch(intent)
}

/***************************************************Context 部分*******************************************************/

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


