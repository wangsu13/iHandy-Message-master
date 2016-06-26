package com.ihs.demo.message_2013011301;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.content.Intent;

import com.ihs.commons.notificationcenter.HSGlobalNotificationCenter;
import com.ihs.commons.notificationcenter.INotificationObserver;
import com.ihs.commons.utils.HSBundle;
import com.ihs.message_2013011301.R;

public class ContactsFragment extends Fragment implements INotificationObserver
{

    private ListView listView;
    private ContactAdapter adapter = null;
    protected String mid;
    protected String name;
    private final static String TAG = chatActivity.class.getName();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        listView = (ListView) view.findViewById(R.id.contact_list);
        final List<Contact> contacts = new ArrayList<Contact>();

        adapter = new ContactAdapter(this.getActivity(), R.layout.cell_item_contact, contacts);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mid = contacts.get(position).getMid();
                name = contacts.get(position).getName();
                String[] info=new String[]{name,mid};
                Intent intent=new Intent(ContactsFragment.this.getActivity(),chatActivity.class);
                intent.putExtra("info",info);
                startActivity(intent);
            }
        });
        HSGlobalNotificationCenter.addObserver(FriendManager.NOTIFICATION_NAME_FRIEND_CHANGED, this);
        refresh();
        return view;
    }

    void refresh() {
        adapter.getContacts().clear();
        adapter.getContacts().addAll(FriendManager.getInstance().getAllFriends());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onReceive(String arg0, HSBundle arg1) {
        refresh();
    }

}
