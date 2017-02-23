package cn.net.cc.weibo.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class BitmapUtil {

	private static final String TAG = "BitmapUtil";

	private static final int DEFAULT_SIZE = 256;
	
	/*private void applyBlur(View view) {  
//	    View view = getWindow().getDecorView();  
	    view.setDrawingCacheEnabled(true);  
	    view.buildDrawingCache(true);  
	    *//** 
	     * 获取当前窗口快照，相当于截屏 
	     *//*  
	    Bitmap bmp1 = view.getDrawingCache();  
	    int height = getOtherHeight();  
	    *//** 
	     * 除去状态栏和标题栏 
	     *//*  
	    Bitmap bmp2 = Bitmap.createBitmap(bmp1, 0, height,bmp1.getWidth(), bmp1.getHeight() - height);  
	    blur(bmp2, text);  
	} */

	/** 保存方法 */
	public static String saveBitmap(Bitmap bitmap,String url) {
		Log.d(TAG, "保存图片");
		String imageDirectory = MemoryUtil.getImageCache();
		if (imageDirectory == null) {
			return null;
		}
		String fileName = getBitmapFile(url);
		File f = new File(fileName);
		if (f.exists()) {
//			f.delete();
			if (f.length() < 50) {
				f.delete();
			}else{
				return fileName;
			}
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
			out.flush();
			out.close();
			Log.i(TAG, "已经保存");
			return fileName;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap getBitmap(String url) {
		return BitmapFactory.decodeFile(getBitmapFile(url));
	}

	public static boolean isBitmapExist(String url) {
		File file = new File(getBitmapFile(url));
		return file.exists();
	}

	public static String getBitmapFile(String url) {
		String imageDirectory = MemoryUtil.getImageCache();
		String name = MD5Sum.md5Sum(url);
		return imageDirectory+File.separator+name;
	}

	/**
	 * Converts a immutable bitmap to a mutable bitmap. This operation doesn't allocates
	 * more memory that there is already allocated.
	 * 
	 * @param imgIn - Source image. It will be released, and should not be used more
	 * @return a copy of imgIn, but muttable.
	 */
	public static Bitmap convertToMutable(Bitmap imgIn) {
	    try {
	        //this is the file going to use temporally to save the bytes. 
	        // This file will not be a image, it will store the raw image data.
	        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

	        //Open an RandomAccessFile
	        //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
	        //into AndroidManifest.xml file
	        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

	        // get the width and height of the source bitmap.
	        int width = imgIn.getWidth();
	        int height = imgIn.getHeight();
	        Config type = imgIn.getConfig();

	        //Copy the byte to the file
	        //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
	        FileChannel channel = randomAccessFile.getChannel();
	        MappedByteBuffer map = channel.map(MapMode.READ_WRITE, 0, imgIn.getRowBytes()*height);
	        imgIn.copyPixelsToBuffer(map);
	        //recycle the source bitmap, this will be no longer used.
	        imgIn.recycle();
	        System.gc();// try to force the bytes from the imgIn to be released

	        //Create a new bitmap to load the bitmap again. Probably the memory will be available. 
	        imgIn = Bitmap.createBitmap(width, height, type);
	        map.position(0);
	        //load it back from temporary 
	        imgIn.copyPixelsFromBuffer(map);
	        //close the temporary file and channel , then delete that also
	        channel.close();
	        randomAccessFile.close();

	        // delete the temp file
	        file.delete();

	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } 

	    return imgIn;
	}
	  
	@SuppressLint("NewApi")  
	public static void blur(Bitmap bkg, View view) {  
	    long startMs = System.currentTimeMillis();  
	    float scaleFactor = 8;//图片缩放比例；  
	    float radius = 20;//模糊程度  
	  
	    Bitmap overlay = Bitmap.createBitmap(  
	            (int) (view.getMeasuredWidth() / scaleFactor),  
	            (int) (view.getMeasuredHeight() / scaleFactor),  
	            Config.ARGB_8888);
	    Canvas canvas = new Canvas(overlay);  
	    canvas.translate(-view.getLeft() / scaleFactor, -view.getTop()/ scaleFactor);  
	    canvas.scale(1 / scaleFactor, 1 / scaleFactor);  
	    Paint paint = new Paint();  
	    paint.setFlags(Paint.FILTER_BITMAP_FLAG);  
	    canvas.drawBitmap(bkg, 0, 0, paint);  
	  
	      
	    overlay = FastBlur.doBlur(overlay, (int) radius, true);  
	    view.setBackgroundDrawable(new BitmapDrawable(view.getContext().getResources(), overlay));  
	    /** 
	     * 打印高斯模糊处理时间，如果时间大于16ms，用户就能感到到卡顿，时间越长卡顿越明显，如果对模糊完图片要求不高，可是将scaleFactor设置大一些。 
	     */  
	    Log.i("jerome", "blur time:" + (System.currentTimeMillis() - startMs));  
	}  
	
	public static byte[] inputStream2byte(InputStream is) {
		byte[] buffer = new byte[1024];
    	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(DEFAULT_SIZE);
        int count;
        try {
			while ((count = is.read(buffer)) != -1) {
				byteArrayOutputStream.write(buffer, 0, count);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        return byteArrayOutputStream.toByteArray();
	}
	
	/**
     * The real guts of parseNetworkResponse. Broken out for readability.
     */
    public static Bitmap fixBitmap(InputStream is,int desiredWidth, int desiredHeight) {
    	byte[] data = inputStream2byte(is);
    	
    	return fixBitmap(data, desiredWidth, desiredHeight);
    }
	
    public static Bitmap fixBitmap(byte[] data,int desiredWidth, int desiredHeight) {
    	BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        // If we have to resize this image, first get the natural bounds.
        decodeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
        int actualWidth = decodeOptions.outWidth;
        int actualHeight = decodeOptions.outHeight;

        // Decode to the nearest power of two scaling factor.
        decodeOptions.inJustDecodeBounds = false;
        // decodeOptions.inPreferQualityOverSpeed = PREFER_QUALITY_OVER_SPEED;
        decodeOptions.inSampleSize =
            findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
        Bitmap tempBitmap =
        		BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
        return fixBitmap(tempBitmap, desiredWidth, desiredHeight);
        
    }
    
    public static Bitmap fixBitmap(Bitmap tempBitmap, int desiredWidth, int desiredHeight) {
    	Bitmap bitmap = null;
    	// If necessary, scale down to the maximal acceptable size.
        if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth ||
                tempBitmap.getHeight() > desiredHeight)) {
            bitmap = Bitmap.createScaledBitmap(tempBitmap,
                    desiredWidth, desiredHeight, true);
            tempBitmap.recycle();
        } else {
            bitmap = tempBitmap;
        }
        return bitmap;
    }
    
    public static Bitmap fixBitmapToViewSize(Bitmap bitmap, int viewWidth, int viewHeight,int maxSize) {
		if (viewHeight == 0 || viewWidth == 0) {
			viewHeight = 2;
			viewWidth = 1;
		}
		int ViewHeight = maxSize*viewHeight/(viewWidth);
		if(ViewHeight == 0) {
			ViewHeight = 100;
		}
		if(!bitmap.isMutable()){
			bitmap = BitmapUtil.convertToMutable(bitmap);
		}

		int centerX = bitmap.getWidth()/2;
		int centerY = bitmap.getHeight()/2;
		float heightRatio = viewHeight/(float)(bitmap.getHeight());
		float widthRatio = viewWidth/(float)(bitmap.getWidth());
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float scale = 1;
		if(heightRatio > widthRatio) {
			width = (int) (viewWidth/heightRatio);
			scale = 1.0f*maxSize/bitmap.getHeight();
		}else {
			height = (int) (viewHeight/widthRatio);
			scale = 1.0f*maxSize/bitmap.getWidth();
		}

		if(bitmap.getWidth() > maxSize && bitmap.getHeight() > maxSize) {
			Matrix matrix = new Matrix();
			matrix.setScale(scale,scale);
			bitmap = Bitmap.createBitmap(bitmap, centerX- width/2, centerY-height/2, width, height,matrix,false);
//			bitmap = Bitmap.createBitmap(bitmap, maxSize, ViewHeight, false);
			Log.d("setBg", "create width:"+bitmap.getWidth()+",height:"+bitmap.getHeight());
			return bitmap;
		}

		bitmap = Bitmap.createBitmap(bitmap, centerX- width/2, centerY-height/2, width, height);
		return bitmap;
	}
    
	/**
     * Returns the largest power-of-two divisor for use in downscaling a bitmap
     * that will not result in the scaling past the desired dimensions.
     *
     * @param actualWidth Actual width of the bitmap
     * @param actualHeight Actual height of the bitmap
     * @param desiredWidth Desired width of the bitmap
     * @param desiredHeight Desired height of the bitmap
     */
	// Visible for testing.
    static int findBestSampleSize(
            int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }

        return (int) n;
    }
	
	/** 
	 * 获取系统状态栏和软件标题栏，部分软件没有标题栏，看自己软件的配置； 
	 * @return 
	 */  
	/*private int getOtherHeight() {  
	    Rect frame = new Rect();  
	    getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
	    int statusBarHeight = frame.top;  
	    int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();  
	    int titleBarHeight = contentTop - statusBarHeight;  
	    return statusBarHeight + titleBarHeight;  
	} */
}
