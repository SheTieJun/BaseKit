package shetj.me.base.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Message
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.tools.PictureFileUtils
import java.io.File
import me.shetj.base.base.BaseCallback
import shetj.me.base.R.style
import timber.log.Timber

/**
 * **@packageName：** com.mobile.pipiti2.utils.mediapicke<br></br>
 * **@author：** shetj<br></br>
 * **@createTime：** 2018/4/1<br></br>
 * **@company：**<br></br>
 * **@email：** 375105540@qq.com<br></br>
 * **@describe**<br></br>
 */
object MediaPickerUtil {
    const val CHOOSE_REQUEST_VIDEO = 189
    const val CHOOSE_REQUEST_PHOTO_SIZE = 190
    fun startPicke(activity: Activity?) {
        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(activity)
            .openGallery(PictureMimeType.ofImage()) //全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
            .theme(style.picture_default_style) //主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
            .maxSelectNum(1) // 最大图片选择数量 int
            .minSelectNum(1) // 最小选择数量 int
            .imageSpanCount(4) // 每行显示个数 int
            .selectionMode(PictureConfig.SINGLE) // 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
            .isPreviewImage(true) // 是否可预览图片 true or false
            .isPreviewVideo(true) // 是否可预览视频 true or false
            .isCamera(true) // 是否显示拍照按钮 true or false
            .isZoomAnim(true) // 图片列表点击 缩放效果 默认true
            .setOutputCameraPath("/CustomPath") // 自定义拍照保存路径,可不填
            .isEnableCrop(true) // 是否裁剪 true or false
            .isCompress(true) // 是否压缩 true or false
            .withAspectRatio(9, 16) // int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
            .hideBottomControls(false) // 是否显示uCrop工具栏，默认不显示 true or false
            .isGif(true) // 是否显示gif图片 true or false
            .freeStyleCropEnabled(true) // 裁剪框是否可拖拽 true or false
            .circleDimmedLayer(true) // 是否圆形裁剪 true or false
            .showCropFrame(true) // 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
            .showCropGrid(true) // 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
            .isOpenClickSound(false) // 是否开启点击声音 true or false
            //						.selectionMedia()// 是否传入已选图片 List<LocalMedia> list
            .isPreviewEggs(true) // 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
            .cutOutQuality(70) // 裁剪压缩质量 默认90 int
            .minimumCompressSize(2048) // 小于100kb的图片不压缩
            .synOrAsy(true) //同步true或异步false 压缩 默认同步
            .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
            .scaleEnabled(true) // 裁剪是否可放大缩小图片 true or false
            .videoQuality(1) // 视频录制质量 0 or 1 int
            .videoMaxSecond(60) // 显示多少秒以内的视频or音频也可适用 int
            .videoMinSecond(30) // 显示多少秒以内的视频or音频也可适用 int
            .recordVideoSecond(30) //视频秒数录制 默认60s int
            .isDragFrame(false) // 是否可拖动裁剪框(固定)
            .forResult(PictureConfig.CHOOSE_REQUEST) //结果回调onActivityResult code
    }

    fun startPickPhoto(activity: Activity?, selectMedias: List<LocalMedia?>?) {
        PictureSelector.create(activity)
            .openGallery(PictureMimeType.ofImage())
            .theme(style.picture_default_style)
            .selectionData(selectMedias)
            .isPreviewImage(true)
            .isCompress(true)
            .isCamera(true)
            .maxSelectNum(9)
            .isGif(true) // 是否显示gif图片 true or false
            .cutOutQuality(90) //						.minimumCompressSize(3048)
            .selectionMode(PictureConfig.MULTIPLE)
            .forResult(PictureConfig.CHOOSE_REQUEST)
    }

    fun startPickPhoto(activity: Activity?) {
        PictureSelector.create(activity)
            .openGallery(PictureMimeType.ofImage())
            .theme(style.picture_default_style)
            .selectionMode(PictureConfig.SINGLE)
            .cutOutQuality(70)
            .minimumCompressSize(1024)
            .forResult(PictureConfig.CHOOSE_REQUEST)
    }

    fun startPickOnePhoto(activity: Activity?) {
        PictureSelector.create(activity)
            .openGallery(PictureMimeType.ofImage())
            .theme(style.picture_default_style)
            .isPreviewImage(true)
            .isCompress(true)
            .isCamera(true)
            .maxSelectNum(1)
            .isEnableCrop(true)
            .withAspectRatio(1, 1)
            .isGif(false)
            .cutOutQuality(90)
            .selectionMode(PictureConfig.MULTIPLE)
            .forResult(PictureConfig.CHOOSE_REQUEST)
    }

    fun startPickSizePhoto(activity: Activity?, size: Int) {
        PictureSelector.create(activity)
            .openGallery(PictureMimeType.ofImage())
            .theme(style.picture_default_style)
            .isPreviewImage(true)
            .isCompress(true)
            .isCamera(true)
            .maxSelectNum(size)
            .withAspectRatio(1, 1)
            .isGif(false)
            .cutOutQuality(90)
            .selectionMode(PictureConfig.MULTIPLE)
            .forResult(CHOOSE_REQUEST_PHOTO_SIZE)
    }

    fun startPreviewImage(activity: Activity?, position: Int, selectList: List<LocalMedia?>?) {

        /*PictureSelector.create(activity).themeStyle(R.style.picture_QQ_style)
					.openExternalPreview(position, "/custom_file", selectList);*/
        PictureSelector.create(activity).themeStyle(style.picture_default_style)
            .openExternalPreview(position, selectList)
    }

    fun startPreviewVideo(activity: Activity?, videoPath: String?) {
        PictureSelector.create(activity).externalPictureVideo(videoPath)
    }

    fun clear(context: Context?) {
        //包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统sd卡权限
        PictureFileUtils.deleteCacheDirFile(context, PictureMimeType.ofImage())
    }

    fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent?,
        commonCallback: BaseCallback<Message?>
    ) {
        if (resultCode == Activity.RESULT_OK) {
            val message = Message.obtain()
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST, CHOOSE_REQUEST_PHOTO_SIZE, CHOOSE_REQUEST_VIDEO -> {
                    message.obj = PictureSelector.obtainMultipleResult(data)
                    commonCallback.onSuccess(message)
                }
                else -> {}
            }
        }
    }

    fun getImageName(media: LocalMedia): String {
        val name: String = if (media.isCompressed) {
            File(media.compressPath).name
        } else if (media.isCut) {
            File(media.cutPath).name
        } else {
            File(media.path).name
        }
        return name
    }

    fun getImagePath(media: LocalMedia): String {
        Timber.i("getCutPath =" + media.compressPath)
        Timber.i("getCutPath =" + media.cutPath)
        Timber.i("getPath =" + media.path)
        val name: String = if (media.isCompressed) {
            media.compressPath
        } else if (media.isCut) {
            media.cutPath
        } else {
            media.path
        }
        return name
    }

    fun startPickVideo(activity: Activity?, selectVideo: List<LocalMedia?>?) {
        PictureSelector.create(activity)
            .openGallery(PictureMimeType.ofVideo())
            .theme(style.picture_default_style)
            .selectionData(selectVideo)
            .isPreviewVideo(true)
            .videoQuality(1)
            .videoMaxSecond(15)
            .videoMinSecond(1)
            .recordVideoSecond(15)
            .selectionMode(PictureConfig.SINGLE)
            .forResult(CHOOSE_REQUEST_VIDEO)
    }
}