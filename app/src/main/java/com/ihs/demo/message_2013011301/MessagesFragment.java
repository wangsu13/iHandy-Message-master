package com.ihs.demo.message_2013011301;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ihs.account.api.account.HSAccountManager;
import com.ihs.app.framework.HSSessionMgr;
import com.ihs.commons.notificationcenter.HSGlobalNotificationCenter;
import com.ihs.commons.utils.HSBundle;
import com.ihs.commons.utils.HSLog;
import com.ihs.message_2013011301.R;
import com.ihs.message_2013011301.managers.HSMessageChangeListener;
import com.ihs.message_2013011301.managers.HSMessageManager;
import com.ihs.message_2013011301.managers.MyMessage;
import com.ihs.message_2013011301.types.HSBaseMessage;
import com.ihs.message_2013011301.types.HSMessageType;
import com.ihs.message_2013011301.types.HSOnlineMessage;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MessagesFragment extends Fragment implements HSMessageChangeListener
{
    private ListView listView;
    private MessageFragmentAdapter adapter = null;
    private final static String TAG = MessagesFragment.class.getName();
    protected String mid;
    protected String name;
    private List<Contact> contacts = new ArrayList<Contact>();





    /**
     * 有消息发生变化时的回调方法
     *
     * @param changeType 变化种类，消息增加 / 消息删除 / 消息状态变化
     * @param messages 变化涉及的消息对象
     */
    @Override
    public void onMessageChanged(HSMessageChangeType changeType, List<HSBaseMessage> messages)
    {

          refresh();
    }




///////////////////////分割线///////////////////////////////////////////////////////////////////////////////////
    /**
     * 收到 “正在输入” 消息时被调用
     *
     * @param fromMid “正在输入” 消息发送者的 mid
     */
    public void onTypingMessageReceived(String fromMid) {

    }

    /**
     * 收到在线消息时被调用
     *
     * @param message 收到的在线消息，其 content 值由用户定制，可实现自己的通讯协议和交互逻辑
     */
    @Override
    public void onOnlineMessageReceived(HSOnlineMessage message) {
        HSLog.d(TAG, "onOnlineMessageReceived");

        // 弹出 Toast 演示示例在线消息的 content 消息体内容
        HSBundle bundle = new HSBundle();
        bundle.putString(SampleFragment.SAMPLE_NOTIFICATION_BUNDLE_STRING, message.getContent().toString());
        HSGlobalNotificationCenter.sendNotificationOnMainThread(SampleFragment.SAMPLE_NOTIFICATION_NAME, bundle);
    }

    /**
     * 当来自某人的消息中，未读消息数量发生变化时被调用
     *
     * @param mid 对应人的 mid
     * @param newCount 变化后的未读消息数量
     */
    @Override
    public void onUnreadMessageCountChanged(String mid, int newCount) {
        // 消息未读数量的变化大家可以在这里进行处理，比如修改每条会话的未读数量等。
    }


    @Override
    public void onReceivingRemoteNotification(JSONObject userInfo) {
        HSLog.d(TAG, "receive remote notification: " + userInfo);
        if (HSSessionMgr.getTopActivity() == null) {
            // 大家在这里做通知中心的通知即可
        }
    }
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HSMessageManager.getInstance().addListener(this, new Handler());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        listView = (ListView) view.findViewById(R.id.contact_list);
        adapter = new MessageFragmentAdapter(this.getActivity(), R.layout.cell_item_contact, contacts);
        listView.setAdapter(adapter);
        HSMessageManager.getInstance().addListener(this, new Handler());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mid = contacts.get(position).getMid();
                name = contacts.get(position).getName();
                String[] info = new String[]{name, mid};
                Intent intent = new Intent(MessagesFragment.this.getActivity(), chatActivity.class);
                intent.putExtra("info", info);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id)
           {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MessagesFragment.this.getActivity());
                builder.setTitle("删除全部信息？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        Toast t = Toast.makeText(getActivity(),"正在删除。。。", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.CENTER, 0, 0);
                        t.show();
                        HSMessageManager.getInstance().deleteMessages(contacts.get(position).getMid());
                        refresh();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        builder.create().dismiss();
                    }
                });
                builder.create().show();
                return true;
            }
        });

        refresh();
        return view;
    }
    void refresh() {
        adapter.getContacts().clear();
        List<Contact> temp=FriendManager.getInstance().getAllFriends();
        for(int i=0;i<temp.size();++i)
        {
            HSMessageManager.QueryResult result = HSMessageManager.getInstance().queryMessages(temp.get(i).getMid(),0,-1);
            List<HSBaseMessage>tempList=result.getMessages();
            if(tempList.size()>0)
            {
                adapter.getContacts().add(temp.get(i));
                MyMessage msg =new MyMessage(tempList.get(0));
                adapter.getContacts().get(adapter.getContacts().size()-1).lastTime=tempList.get(0).getTimestamp().getTime();
                adapter.getContacts().get(adapter.getContacts().size()-1).last=tempList.get(0).getTimestamp();
                if(temp.get(i).getMid().equals(msg.from))
                {
                    if(msg.type== HSMessageType.TEXT)
                        UnreadMessageProcess.getInstance().setLatestText(temp.get(i).getMid(),msg.text);
                    else if(msg.type== HSMessageType.AUDIO)
                        UnreadMessageProcess.getInstance().setLatestText(temp.get(i).getMid(),"你收到了一条语音");
                    else if(msg.type== HSMessageType.IMAGE)
                        UnreadMessageProcess.getInstance().setLatestText(temp.get(i).getMid(),"你收到了一张图片");
                }
                else if(HSAccountManager.getInstance().getMainAccount().getMID().equals(msg.from))
                {
                    if(msg.type== HSMessageType.TEXT)
                        UnreadMessageProcess.getInstance().setLatestText(temp.get(i).getMid(),msg.text);
                    else if(msg.type== HSMessageType.AUDIO)
                        UnreadMessageProcess.getInstance().setLatestText(temp.get(i).getMid(),"你发送了一条语音");
                    else if(msg.type== HSMessageType.IMAGE)
                        UnreadMessageProcess.getInstance().setLatestText(temp.get(i).getMid(),"你发送了一张图片");
                }
            }
        }
       Collections.sort(adapter.getContacts(),new Comparator<Contact>() {
            @Override
            public int compare(Contact lhs, Contact rhs) {
                return lhs.getTime().compareTo(rhs.getTime());
            }
        });
        Collections.reverse(adapter.getContacts());
        adapter.notifyDataSetChanged();
    }

}
