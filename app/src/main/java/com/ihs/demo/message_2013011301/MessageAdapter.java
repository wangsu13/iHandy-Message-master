package com.ihs.demo.message_2013011301;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.content.Context;

import com.ihs.account.api.account.HSAccountManager;
import com.ihs.message_2013011301.R;
import com.ihs.message_2013011301.types.HSMessageType;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ihs.message_2013011301.managers.MyMessage;

import java.util.List;


public class MessageAdapter extends ArrayAdapter<MyMessage>
{
    private int resourceID;
    public MessageAdapter(Context context, int textViewResourceID, List<MyMessage> objects)
    {
        super(context,textViewResourceID,objects);
        resourceID=textViewResourceID;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        MyMessage message=getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView==null)
        {
            view=LayoutInflater.from(getContext()).inflate(resourceID,null);
            viewHolder=new ViewHolder();
            viewHolder.leftLayoutMessage=(LinearLayout)view.findViewById(R.id.left_layout_message);
            viewHolder.rightLayoutMessage=(LinearLayout)view.findViewById(R.id.right_layout_message);
            viewHolder.leftLayoutImage=(LinearLayout)view.findViewById(R.id.left_layout_image);
            viewHolder.rightLayoutImage=(LinearLayout)view.findViewById(R.id.right_layout_image);
            viewHolder.leftMessage=(TextView)view.findViewById(R.id.left_message);
            viewHolder.rightMessage=(TextView)view.findViewById(R.id.right_message);
            viewHolder.leftImage=(ImageView)view.findViewById(R.id.left_image);
            viewHolder.rightImage=(ImageView)view.findViewById(R.id.right_image);
            view.setTag(viewHolder);
        }
        else
        {
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        if((!message.from.equals(HSAccountManager.getInstance().getMainAccount().getMID()))&&(message.type==HSMessageType.TEXT||message.type==HSMessageType.AUDIO||message.type==HSMessageType.LOCATION))
        {
            viewHolder.leftLayoutMessage.setVisibility(View.VISIBLE);
            viewHolder.rightLayoutMessage.setVisibility(View.GONE);
            viewHolder.leftLayoutImage.setVisibility(View.GONE);
            viewHolder.rightLayoutImage.setVisibility(View.GONE);
            viewHolder.leftMessage.setText(message.text);
        }
        else if(message.from.equals(HSAccountManager.getInstance().getMainAccount().getMID())&&(message.type==HSMessageType.TEXT||message.type==HSMessageType.AUDIO||message.type==HSMessageType.LOCATION))
        {
            viewHolder.rightLayoutMessage.setVisibility(View.VISIBLE);
            viewHolder.leftLayoutMessage.setVisibility(View.GONE);
            viewHolder.leftLayoutImage.setVisibility(View.GONE);
            viewHolder.rightLayoutImage.setVisibility(View.GONE);
            viewHolder.rightMessage.setText(message.text);
        }
        else {
            if ((!message.from.equals(HSAccountManager.getInstance().getMainAccount().getMID())) && message.type == HSMessageType.IMAGE) {
                viewHolder.leftLayoutMessage.setVisibility(View.GONE);
                viewHolder.rightLayoutMessage.setVisibility(View.GONE);
                viewHolder.leftLayoutImage.setVisibility(View.VISIBLE);
                viewHolder.rightLayoutImage.setVisibility(View.GONE);
                BitmapFactory.Options myoptions = new BitmapFactory.Options();
                myoptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(message.imageSuolvetuPath, myoptions);
                int height = myoptions.outHeight * 222 / myoptions.outWidth;
                myoptions.outWidth = 222;
                myoptions.outHeight = height;
                myoptions.inJustDecodeBounds = false;
                myoptions.inSampleSize = myoptions.outWidth / 222;
                myoptions.inPurgeable = true;
                myoptions.inInputShareable = true;
                myoptions.inPreferredConfig = Bitmap.Config.ARGB_4444;
                Bitmap bitmat = BitmapFactory.decodeFile(message.imageSuolvetuPath, myoptions);
                viewHolder.leftImage.setImageBitmap(bitmat);

            }
            else {
                viewHolder.leftLayoutMessage.setVisibility(View.GONE);
                viewHolder.rightLayoutMessage.setVisibility(View.GONE);
                viewHolder.leftLayoutImage.setVisibility(View.GONE);
                viewHolder.rightLayoutImage.setVisibility(View.VISIBLE);
                BitmapFactory.Options myoptions = new BitmapFactory.Options();
                myoptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(message.imageSuolvetuPath, myoptions);
                int height = myoptions.outHeight * 222 / myoptions.outWidth;
                myoptions.outWidth = 222;
                myoptions.outHeight = height;
                myoptions.inJustDecodeBounds = false;
                myoptions.inSampleSize = myoptions.outWidth / 222;
                myoptions.inPurgeable = true;
                myoptions.inInputShareable = true;
                myoptions.inPreferredConfig = Bitmap.Config.ARGB_4444;
                Bitmap bitmat = BitmapFactory.decodeFile(message.imageSuolvetuPath, myoptions);
                viewHolder.rightImage.setImageBitmap(bitmat);
            }
        }
        return view;
    }
    private class ViewHolder
    {
        LinearLayout leftLayoutMessage;
        LinearLayout rightLayoutMessage;
        LinearLayout leftLayoutImage;
        LinearLayout rightLayoutImage;
        TextView leftMessage;
        TextView rightMessage;
        ImageView leftImage;
        ImageView rightImage;
    }
}
