package com.ihs.demo.message_2013011301;

import test.contacts.demo.friends.api.HSContactFriendsMgr;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

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

import java.util.Iterator;
import java.util.List;

public class MainActivity extends HSActionBarActivity implements HSMessageChangeListener
{

    private SoundPool soundPool;
    private int id1;
    private int id2;

    private final static String TAG = MainActivity.class.getName();
    private Tab tabs[];
    private String latestSender="";
    private String latestText;

    /**
     * 有消息发生变化时的回调方法
     *
     * @param changeType 变化种类，消息增加 / 消息删除 / 消息状态变化
     * @param messages 变化涉及的消息对象
     */
    @Override
    public void onMessageChanged(HSMessageChangeType changeType, List<HSBaseMessage> messages) {
        // 同学们可以根据 changeType 的消息增加、删除、更新信息进行会话数据的构建
        if (changeType == HSMessageChangeType.ADDED && !messages.isEmpty()) {

            for(Iterator<HSBaseMessage> it=messages.iterator();it.hasNext();)
            {

                HSBaseMessage value = it.next();
                MyMessage msg=new MyMessage(value);
                latestSender=value.getFrom();
                if(msg.type== HSMessageType.TEXT)
                    latestText=msg.text;
                else if(msg.type==HSMessageType.TEXT.AUDIO)
                    latestText="你收到了一条语音";
                if(!msg.from.equals(HSAccountManager.getInstance().getMainAccount().getMID()))
                {
                    soundPool.play(id1, 1, 1, 0, 0, 1);
                    if (HSSessionMgr.getTopActivity()==null) {
                        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        Notification notification = new Notification();
                        notification.icon = R.drawable.notification_icon;
                        String nfStr = "";
                        String name = FriendManager.getInstance().getFriend(msg.from).getName();
                        String mid = msg.from;
                        int unRead = HSMessageManager.getInstance().queryUnreadCount(mid);
                        nfStr = name + ": ";
                        switch (msg.type) {
                            case TEXT:
                                nfStr += msg.text;
                                break;
                            case AUDIO:
                                nfStr += "[Audio Message]";
                                break;
                            case IMAGE:
                                nfStr += "[Image Message]";
                                break;
                            case LOCATION:
                                nfStr += "[Location Message]";
                                break;
                            default:
                                nfStr += "[Unknown Type Message]";
                        }
//                        notification.tickerText = nfStr;
                        notification.flags |= Notification.FLAG_AUTO_CANCEL;
                        Intent intent = new Intent(MainActivity.this, chatActivity.class);
                        String data[] = new String[]{name, mid};
                        intent.putExtra("info", data);
                        PendingIntent pd = PendingIntent.getActivity(MainActivity.this, Integer.parseInt(data[1]), intent, 0);
                        notification.setLatestEventInfo(MainActivity.this, unRead + " new messages", nfStr, pd);
                        int ID = Integer.parseInt(mid);
                        nm.notify(ID, notification);
                    }

                }
                else
                {
                    soundPool.play(id2, 1, 1, 0, 0, 1);
                }
            }

        }
    }




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
        if (HSSessionMgr.getTopActivity() == null)
        {
       }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        soundPool= new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
        id1 = soundPool.load(this, R.raw.message_ringtone_received, 1);
        id2 = soundPool.load(this, R.raw.message_ringtone_sent, 1);

        ActionBar bar = this.getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        int[] tabNames = { R.string.contacts, R.string.messages, R.string.settings, R.string.sample };
        HSMessageManager.getInstance().addListener(this, new Handler());
        tabs = new Tab[4];
        for (int i = 0; i < 3; i++) {
            Tab tab = bar.newTab();
            tabs[i] = tab;
            tab.setText(tabNames[i]);
            tab.setTabListener(new TabListener() {

                @Override
                public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
                    HSLog.d(TAG, "unselected " + arg0);
                }

                @Override
                public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
                    HSLog.d(TAG, "selected " + arg0);
                    if (tabs[0] == arg0) {
                        Fragment f = new ContactsFragment();
                        arg1.replace(android.R.id.content, f);
                    } else if (tabs[1] == arg0) {
                        Fragment f = new MessagesFragment();
                        arg1.replace(android.R.id.content, f);
                    } else if (tabs[2] == arg0) {
                        Fragment f = new SettingsFragment();
                        arg1.replace(android.R.id.content, f);
                    } else if (tabs[3] == arg0) {
                        Fragment f = new SampleFragment();
                        arg1.replace(android.R.id.content, f);
                    }
                }

                @Override
                public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
                    HSLog.d(TAG, "reselected " + arg0);
                }
            });
            bar.addTab(tab);
        }

        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        HSMessageManager.getInstance().pullMessages();
        HSContactFriendsMgr.startSync(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
