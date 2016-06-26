package com.ihs.demo.message_2013011301;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.ihs.message_2013011301.R;

/**
 * Created by ty on 2015/9/7.
 */
public class ImageShowInChat extends Activity
{
    private ImageView imageView;
    private String path;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Intent intent=getIntent();
        //path=intent.getStringExtra("path");\
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.image_show_datu);
        imageView=(ImageView)findViewById(R.id.image_view_datu);
        /*BitmapFactory.Options myoptions = new BitmapFactory.Options();
        myoptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, myoptions);
        int height = myoptions.outHeight * 222 / myoptions.outWidth;
        myoptions.outWidth = 222;
        myoptions.outHeight = height;
        myoptions.inJustDecodeBounds = false;
        myoptions.inSampleSize = myoptions.outWidth / 222;
        myoptions.inPurgeable = true;
        myoptions.inInputShareable = true;
        myoptions.inPreferredConfig = Bitmap.Config.ARGB_4444;
        Bitmap bitmat = BitmapFactory.decodeFile(path, myoptions);*/
        //imageView.setImageBitmap(bitmat);
        imageView.setImageResource(R.drawable.arrow);
    }
}
