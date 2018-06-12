package me.shetj.base.tools.image;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.shetj.base.base.CommonCaseCallBack;
import me.shetj.base.tools.app.AppUtils;
import me.shetj.base.tools.file.SDCardUtils;

public class ImageUtils {

	public static final int GET_IMAGE_BY_CAMERA = 5001;
	public static final int GET_IMAGE_FROM_PHONE = 5002;
	public static final int CROP_IMAGE = 5003;
	public static Uri imageUriFromCamera;
	public static Uri cropImageUri;
	private static String imagePath = "pipiti/image";

	/**
	 * 把View对象转换成bitmap
	 * */
	public static Bitmap convertViewToBitmap(View view) {
		view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
						View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		if (bitmap != null) {
		} else {
		}
		return bitmap;
	}


	/** 图片压缩方法一
	 *
	 * 计算 bitmap大小，如果超过100kb，则进行压缩
	 *
	 * @param bitmap
	 * @return
	 */
	public static Bitmap ImageCompressL(Bitmap bitmap) {
		double targetWidth = Math.sqrt(80.00 * 1024);
		if (bitmap.getWidth() > targetWidth || bitmap.getHeight() > targetWidth) {
			// 创建操作图片用的matrix对象
			Matrix matrix = new Matrix();
			// 计算宽高缩放率
			double x = Math.max(targetWidth / bitmap.getWidth(), targetWidth
							/ bitmap.getHeight());
			// 缩放图片动作
			matrix.postScale((float) x, (float) x);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
							bitmap.getHeight(), matrix, true);
		}
		return bitmap;
	}

	public  static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while ( baos.toByteArray().length / 1024>100) {
			//循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();
			//重置baos即清空baos
			image.compress(Bitmap.CompressFormat.PNG, options, baos);
			//这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;
			//每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		//把压缩后的数据baos存放到ByteArrayInputStream中
		return BitmapFactory.decodeStream(isBm, null, null);
	}

	/**
	 * 创建一条图片地址uri,用于保存拍照后的照片
	 *
	 * @param context
	 * @return 图片的uri
	 */
	private static Uri createImagePathUri(Context context) {
		File file =new File( createImagePath( ));
		if (context == null) {
			throw new NullPointerException();
		}
		Uri uri;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			uri = FileProvider.getUriForFile(context.getApplicationContext(), AppUtils.getAppPackageName()+".FileProvider", file);
		} else {
			uri = Uri.fromFile(file);
		}
		return uri;
	}
	public static String createImagePath( ) {
		SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
		long time = System.currentTimeMillis();
		String imageName = timeFormatter.format(new Date(time));
		return SDCardUtils.getPath(imagePath)+"/"+imageName+".jpg";
	}

	public static void openCameraImage(final Activity activity) {
		ImageUtils.imageUriFromCamera = ImageUtils.createImagePathUri(activity);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUtils.imageUriFromCamera);
		activity.startActivityForResult(intent, ImageUtils.GET_IMAGE_BY_CAMERA);
	}

	public static void openLocalImage(final Activity activity) {
		Intent intent  ;
		intent = new Intent(Intent.ACTION_PICK);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		activity.startActivityForResult(intent, ImageUtils.GET_IMAGE_FROM_PHONE);
	}

	public static void cropImage(Activity activity, Uri srcUri) {
		ImageUtils.cropImageUri = Uri.fromFile(new File(createImagePath()));
		Intent intent = new Intent("com.android.camera.action.CROP");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		}
		intent.setDataAndType(srcUri, "image/*");
		//裁剪图片的宽高比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("crop", "true");
		//可裁剪
		intent.putExtra("scale", true);
		//支持缩放
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUtils.cropImageUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		//输出图片格式
		intent.putExtra("noFaceDetection", true);
		//取消人脸识别
		activity.startActivityForResult(intent, CROP_IMAGE);
	}


	public void onActivityResult(Activity context, int requestCode, Intent data, CommonCaseCallBack callBack) {
		switch (requestCode) {
			default:
				if (null != callBack){
					callBack.onClose();
				}
				break;
			case ImageUtils.GET_IMAGE_BY_CAMERA:
				if (ImageUtils.imageUriFromCamera != null) {
					// 对图片进行裁剪
					ImageUtils.cropImage(context, ImageUtils.imageUriFromCamera);
					break;
				}
				break;
			case ImageUtils.GET_IMAGE_FROM_PHONE:
				if (data != null && data.getData() != null) {
					ImageUtils.cropImage(context, data.getData());
				}
				break;
			case ImageUtils.CROP_IMAGE:
				String path = ImageUtils.cropImageUri.getPath();
				if (ImageUtils.cropImageUri != null&&new File(path).exists()) {
					if (callBack != null){
						callBack.onSuccess(path);
					}
				}else {
					if (null != callBack){
						callBack.onClose();
					}
				}
				break;
		}
	}


	/**
	 * 把uri转成file
	 *
	 * @param activity
	 * @param uri
	 * @return
	 */
	public static File getFileByUri(Activity activity, Uri uri) {
		String path = null;
		if ("file".equals(uri.getScheme())) {
			path = uri.getEncodedPath();
			if (path != null) {
				path = Uri.decode(path);
				ContentResolver cr = activity.getContentResolver();
				StringBuffer buff = new StringBuffer();
				buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
				Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.ImageColumns._ID,
								MediaStore.Images.ImageColumns.DATA }, buff.toString(), null, null);
				int index = 0;
				int dataIdx = 0;
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
					index = cur.getInt(index);
					dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
					path = cur.getString(dataIdx);
				}
				cur.close();
				if (index == 0) {
				} else {
					Uri u = Uri.parse("content://media/external/images/media/" + index);
					System.out.println("temp uri is :" + u);
				}
			}
			if (path != null) {
				return new File(path);
			}
		} else if ("content".equals(uri.getScheme())) {
			// 4.2.2以后
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = activity.getContentResolver().query(uri, proj, null, null, null);
			if (cursor.moveToFirst()) {
				int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				path = cursor.getString(columnIndex);
			}
			cursor.close();
			return new File(path);
		} else {
		}
		return null;
	}

	/**
	 * 获取圆形图片
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getRoundImage(Bitmap bitmap)
	{
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int left = 0, top = 0, right = width, bottom = height;
		float roundPx = height/2;
		if (width > height) {
			left = (width - height)/2;
			top = 0;
			right = left + height;
			bottom = height;
		} else if (height > width) {
			left = 0;
			top = (height - width)/2;
			right = width;
			bottom = top + width;
			roundPx = width/2;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		int color = 0xff424242;
		Paint paint = new Paint();
		Rect rect = new Rect(left, top, right, bottom);
		RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * 缩放图片
	 *
	 * @param src       源图片
	 * @param newWidth  新宽度
	 * @param newHeight 新高度
	 * @return 缩放后的图片
	 */
	public static Bitmap scale(Bitmap src, int newWidth, int newHeight) {
		return scale(src, newWidth, newHeight, false);
	}

	/**
	 * 缩放图片
	 *
	 * @param src       源图片
	 * @param newWidth  新宽度
	 * @param newHeight 新高度
	 * @param recycle   是否回收
	 * @return 缩放后的图片
	 */
	public static Bitmap scale(Bitmap src, int newWidth, int newHeight, boolean recycle) {
		if (isEmptyBitmap(src)) {
			return null;
		}
		Bitmap ret = Bitmap.createScaledBitmap(src, newWidth, newHeight, true);
		if (recycle && !src.isRecycled()) {
			src.recycle();
		}
		return ret;
	}

	/**
	 * 缩放图片
	 *
	 * @param src         源图片
	 * @param scaleWidth  缩放宽度倍数
	 * @param scaleHeight 缩放高度倍数
	 * @return 缩放后的图片
	 */
	public static Bitmap scale(Bitmap src, float scaleWidth, float scaleHeight) {
		return scale(src, scaleWidth, scaleHeight, false);
	}

	/**
	 * 缩放图片
	 *
	 * @param src         源图片
	 * @param scaleWidth  缩放宽度倍数
	 * @param scaleHeight 缩放高度倍数
	 * @param recycle     是否回收
	 * @return 缩放后的图片
	 */
	public static Bitmap scale(Bitmap src, float scaleWidth, float scaleHeight, boolean recycle) {
		if (isEmptyBitmap(src)) {
			return null;
		}
		Matrix matrix = new Matrix();
		matrix.setScale(scaleWidth, scaleHeight);
		Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
		if (recycle && !src.isRecycled()) {
			src.recycle();
		}
		return ret;
	}

	/**
	 * 判断bitmap对象是否为空
	 *
	 * @param src 源图片
	 * @return {@code true}: 是<br>{@code false}: 否
	 */
	private static boolean isEmptyBitmap(Bitmap src) {
		return src == null || src.getWidth() == 0 || src.getHeight() == 0;
	}

	/******************************~~~~~~~~~ 下方和压缩有关 ~~~~~~~~~******************************/

	/**
	 * 按缩放压缩
	 *
	 * @param src       源图片
	 * @param newWidth  新宽度
	 * @param newHeight 新高度
	 * @return 缩放压缩后的图片
	 */
	public static Bitmap compressByScale(Bitmap src, int newWidth, int newHeight) {
		return scale(src, newWidth, newHeight, false);
	}

	/**
	 * 按缩放压缩
	 *
	 * @param src       源图片
	 * @param newWidth  新宽度
	 * @param newHeight 新高度
	 * @param recycle   是否回收
	 * @return 缩放压缩后的图片
	 */
	public static Bitmap compressByScale(Bitmap src, int newWidth, int newHeight, boolean recycle) {
		return scale(src, newWidth, newHeight, recycle);
	}

	/**
	 * 按缩放压缩
	 *
	 * @param src         源图片
	 * @param scaleWidth  缩放宽度倍数
	 * @param scaleHeight 缩放高度倍数
	 * @return 缩放压缩后的图片
	 */
	public static Bitmap compressByScale(Bitmap src, float scaleWidth, float scaleHeight) {
		return scale(src, scaleWidth, scaleHeight, false);
	}

	/**
	 * 按缩放压缩
	 *
	 * @param src         源图片
	 * @param scaleWidth  缩放宽度倍数
	 * @param scaleHeight 缩放高度倍数
	 * @param recycle     是否回收
	 * @return 缩放压缩后的图片
	 */
	public static Bitmap compressByScale(Bitmap src, float scaleWidth, float scaleHeight, boolean recycle) {
		return scale(src, scaleWidth, scaleHeight, recycle);
	}

	/**
	 * 按质量压缩
	 *
	 * @param src     源图片
	 * @param quality 质量
	 * @return 质量压缩后的图片
	 */
	public static Bitmap compressByQuality(Bitmap src, int quality) {
		return compressByQuality(src, quality, false);
	}

	/**
	 * 按质量压缩
	 *
	 * @param src     源图片
	 * @param quality 质量
	 * @param recycle 是否回收
	 * @return 质量压缩后的图片
	 */
	public static Bitmap compressByQuality(Bitmap src, int quality, boolean recycle) {
		if (isEmptyBitmap(src) || quality < 0 || quality > 100) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		src.compress(Bitmap.CompressFormat.JPEG, quality, baos);
		byte[] bytes = baos.toByteArray();

		if (recycle && !src.isRecycled()) {
			src.recycle();
		}
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}

	/**
	 * 按质量压缩
	 *
	 * @param src         源图片
	 * @param maxByteSize 允许最大值字节数
	 * @return 质量压缩压缩过的图片
	 */
	public static Bitmap compressByQuality(Bitmap src, long maxByteSize) {
		return compressByQuality(src, maxByteSize, false);
	}

	/**
	 * 按质量压缩
	 *
	 * @param src         源图片
	 * @param maxByteSize 允许最大值字节数
	 * @param recycle     是否回收
	 * @return 质量压缩压缩过的图片
	 */
	public static Bitmap compressByQuality(Bitmap src, long maxByteSize, boolean recycle) {
		if (isEmptyBitmap(src) || maxByteSize <= 0) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int quality = 100;
		src.compress(Bitmap.CompressFormat.JPEG, quality, baos);
		while (baos.toByteArray().length > maxByteSize && quality > 0) {
			baos.reset();
			src.compress(Bitmap.CompressFormat.JPEG, quality -= 5, baos);
		}
		if (quality < 0) {
			return null;
		}
		byte[] bytes = baos.toByteArray();
		if (recycle && !src.isRecycled()) {
			src.recycle();
		}
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}

	/**
	 * 按采样大小压缩
	 *
	 * @param src        源图片
	 * @param sampleSize 采样率大小
	 * @return 按采样率压缩后的图片
	 */
	public static Bitmap compressBySampleSize(Bitmap src, int sampleSize) {
		return compressBySampleSize(src, sampleSize, false);
	}

	/**
	 * 按采样大小压缩
	 *
	 * @param src        源图片
	 * @param sampleSize 采样率大小
	 * @param recycle    是否回收
	 * @return 按采样率压缩后的图片
	 */
	public static Bitmap compressBySampleSize(Bitmap src, int sampleSize, boolean recycle) {
		if (isEmptyBitmap(src)) {
			return null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = sampleSize;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] bytes = baos.toByteArray();
		if (recycle && !src.isRecycled()) {
			src.recycle();
		}
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
	}
}