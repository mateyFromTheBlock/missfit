package com.example.arono.missfit;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by arono on 21/02/2016.
 */
public class PictureUtility {

    public static final int ORIENTATION_ROTATE_90 = 90;
    private Context context;

    public PictureUtility(Context context){
        this.context = context;
    }

    /** rotate the image by angle
     * @param bitmap - the image that u want to rotate.
     * @param angle - which angle you want to rotate.
     * @return bitmap
     */
    public Bitmap rotateImage(Bitmap bitmap,int angle){
        Matrix matrix = new Matrix();

        matrix.postRotate(angle);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
    }


    /** check orientation of the picture
     * @param path (path of the image).
     * @return angle of the image
     */
    public int checkOrientation(String path){
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int angle;
        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                angle = 270;
                break;
            default:
                angle = 0;
                break;
        }
        Log.e("Error", "picture angle = " + angle);
        return angle;
    }

    /** get picture form gallery.
     * @param data intent data (usually from activity result).
     * @return bitmap
     */
    public Bitmap getPictureFromGallery(Intent data){
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = context.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        Bitmap bitmap = shrinkBitmap(picturePath, 500, 500);

        int angle = checkOrientation(picturePath);

        if(angle == 0 && bitmap.getWidth() < bitmap.getHeight()) {
            bitmap = rotateImage(bitmap,ORIENTATION_ROTATE_90);
        }else
            bitmap = rotateImage(bitmap,angle);

        return bitmap;
    }


    /**  shrink the size of the Bitmap
     * @param path
     * @param width
     * @param height
     * @return bitmap
     */
    public Bitmap shrinkBitmap(String path, int width, int height){
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmpFactoryOptions);

        int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)height);
        int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)width);

        if (heightRatio > 1 || widthRatio > 1)
        {
            if (heightRatio > widthRatio)
            {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(path, bmpFactoryOptions);
        return bitmap;
    }

    /**
     * this function crop the center of the Image
     * @param src the Image that u want to crop
     * @return Bitmap
     */
    public Bitmap cropCenter(Bitmap src){
        if (src.getWidth() >= src.getHeight()){

            src = Bitmap.createBitmap(
                    src,
                    src.getWidth()/2 - src.getHeight()/2,
                    0,
                    src.getHeight(),
                    src.getHeight()
            );

        }else{

            src = Bitmap.createBitmap(
                    src,
                    0,
                    src.getHeight()/2 - src.getWidth()/2,
                    src.getWidth(),
                    src.getWidth()
            );
        }
        return src;
    }

}
