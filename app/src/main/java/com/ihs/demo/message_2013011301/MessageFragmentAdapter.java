package com.ihs.demo.message_2013011301;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihs.message_2013011301.R;
import com.ihs.message_2013011301.managers.HSMessageManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.regex.*;

public class MessageFragmentAdapter extends ArrayAdapter<Contact>
{

    private List<Contact> contacts;
    private Context context;
    DisplayImageOptions options;

    private class ViewHolder {
        ImageView avatarImageView;
        TextView titleTextView;
        TextView detailTextView;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public MessageFragmentAdapter(Context context, int resource, List<Contact> objects) {
        super(context, resource, objects);
        this.contacts = objects;
        this.context = context;

        options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.chat_avatar_default_icon).showImageForEmptyUri(R.drawable.chat_avatar_default_icon)
                .showImageOnFail(R.drawable.chat_avatar_default_icon).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.cell_item_contact, parent, false);
            TextView titleView = (TextView) convertView.findViewById(R.id.title_text_view);
            TextView detailView = (TextView) convertView.findViewById(R.id.detail_text_view);
            holder.titleTextView = titleView;
            holder.detailTextView = detailView;
            holder.avatarImageView = (ImageView) convertView.findViewById(R.id.contact_avatar);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        Contact contact = contacts.get(position);
        Pattern p=Pattern.compile("[0-9][0-9]:[0-9][0-9]");
        String tttt=new String(""+contact.last);
        Matcher m=p.matcher(tttt);
        String time=null;
        Pattern p2=Pattern.compile("[a-zA-Z]{3,} [0-9]{1,2}");
        Matcher m2=p2.matcher(tttt);
        if(m.find())
        {
            time=new String(tttt.substring(m.start(),m.end()));
        }
        if(m2.find())
        {
            time+=" ";
            time+=tttt.substring(m2.start(),m2.end());
        }
        holder.titleTextView.setText("" + contact.getName() +"("+ HSMessageManager.getInstance().queryUnreadCount(contact.getMid())+"条未读)   "+time);
        holder.detailTextView.setText(UnreadMessageProcess.getInstance().getText(contact.getMid()));
        ImageLoader.getInstance().displayImage("content://com.android.contacts/contacts/" + contact.getContactId(), holder.avatarImageView, options);
        return convertView;
    }
}
