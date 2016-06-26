package com.ihs.demo.message_2013011301;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.graphics.BitmapFactory;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.ihs.message_2013011301.R;
import com.ihs.message_2013011301.managers.HSMessageChangeListener;
import com.ihs.message_2013011301.managers.MyMessage;
import com.ihs.message_2013011301.types.HSImageMessage;
import com.ihs.message_2013011301.types.HSLocationMessage;
import com.ihs.message_2013011301.types.HSMessageType;
import com.ihs.message_2013011301.types.HSTextMessage;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import android.os.Environment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import com.ihs.commons.utils.HSLog;
import android.util.Log;
import java.io.IOException;
import android.view.ViewGroup.LayoutParams;
import com.ihs.app.framework.HSSessionMgr;
import com.ihs.commons.notificationcenter.HSGlobalNotificationCenter;
import com.ihs.commons.utils.HSBundle;
import com.ihs.commons.utils.HSError;
import com.ihs.message_2013011301.managers.HSMessageManager;
import com.ihs.message_2013011301.managers.HSMessageManager.SendMessageCallback;
import com.ihs.message_2013011301.types.HSBaseMessage;
import com.ihs.message_2013011301.types.HSAudioMessage;
import com.ihs.message_2013011301.types.HSOnlineMessage;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import android.widget.Toast;
import org.json.JSONObject;
import com.ihs.account.api.account.HSAccountManager;
import android.media.AudioManager;
import android.media.SoundPool;
import java.io.File;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.content.ContentResolver;

public class chatActivity extends Activity implements HSMessageChangeListener, OnGetGeoCoderResultListener
{
    private String result = "";
    private String chatterMid="";
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    private File outputImage;
    private Uri imageUri;
    private SoundPool soundPool;
    private int id1;
    private int id2;
    private String[] userInfo;
    private ListView messageListView;
    private EditText inputText;
    private Button send;
    private Button exit;
    private Button other;
    private Button audio;
    private Button audioSenddddd;
    private static final String LOG_TAG = "AudioRecordTest";
    private long cursor;
    private String AudioFileName = null;
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;
    private Context mContext=null;
    private int timeAudio=0;
    private TimerTask taskAudio;
    private Timer timerAudio;
    private Toast toastSpeaking=null;
    TextView info;
    private MessageAdapter adapter;
    private Date startDateAudio,stopDateAudio;
    GeoCoder mSearch = null;
    private List<MyMessage> messageList=new ArrayList<MyMessage>();
    private final static String TAG = chatActivity.class.getName();
    void Send(String toMid, String text) {
        HSTextMessage temp=new HSTextMessage(toMid, text);
        HSMessageManager.getInstance().send(temp, new SendMessageCallback() {

            @Override
            public void onMessageSentFinished(HSBaseMessage message, boolean success, HSError error) {
                HSLog.d(TAG, "success: " + success);
            }
        }, new Handler());
    }



    private boolean openGPSSettings() {
        LocationManager alm = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        if (alm
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS模块正常", Toast.LENGTH_SHORT)
                    .show();
            return true;
        }

        Toast.makeText(this, "请开启GPS！", Toast.LENGTH_SHORT).show();
        return false;

    }

    private void getLocation(String to)
    {
        LocationManager locationManager;
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) this.getSystemService(serviceName);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗

        String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
        Location location = locationManager.getLastKnownLocation(provider);
        if (location == null) {
            Send(to, "there is an error in your GPS");
            return;
        }
        LatLng ptCenter = new LatLng(location.getLatitude(), location.getLongitude());
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(ptCenter));
         HSLocationMessage locationMessage = new HSLocationMessage(to, location.getLatitude(), location.getLongitude(),result);
        HSMessageManager.getInstance().send(locationMessage, new SendMessageCallback() {
            @Override
            public void onMessageSentFinished(HSBaseMessage message, boolean success, HSError error) {
                HSLog.d(TAG, "success: " + success);
            }
        }, new Handler());
    }

    @Override
    public void onMessageChanged(HSMessageChangeType changeType, List<HSBaseMessage> messages) {
        if(changeType==HSMessageChangeType.UPDATED&&!messages.isEmpty())
        {
            for(Iterator<HSBaseMessage>it=messages.iterator();it.hasNext();)
            {
                HSBaseMessage value = it.next();
                if(value.getStatus()== HSBaseMessage.HSMessageStatus.FAILED)
                {
                    Toast t1 = Toast.makeText(chatActivity.this, "发送失败", Toast.LENGTH_SHORT);
                    t1.setGravity(Gravity.CENTER, 0, 0);
                    t1.show();
                }
                else if(value.getStatus()== HSBaseMessage.HSMessageStatus.SENDING)
                {
                    Toast t2 = Toast.makeText(chatActivity.this, "正在发送", Toast.LENGTH_SHORT);
                    t2.setGravity(Gravity.CENTER, 0, 0);
                    t2.show();
                }
                else if(value.getStatus()== HSBaseMessage.HSMessageStatus.SENT)
                {
                    Toast t3 = Toast.makeText(chatActivity.this, "已发送", Toast.LENGTH_SHORT);
                    t3.setGravity(Gravity.CENTER, 0, 0);
                    t3.show();
                }

            }
        }
        if (changeType == HSMessageChangeType.ADDED && !messages.isEmpty()) {
            for(Iterator<HSBaseMessage>it=messages.iterator();it.hasNext();) {

                HSBaseMessage value = it.next();
                MyMessage msg=new MyMessage(value);
                if(msg.from.equals(userInfo[1])||msg.from.equals(HSAccountManager.getInstance().getMainAccount().getMID()))
                    messageList.add(msg);


                adapter.notifyDataSetChanged();
                messageListView.setSelection(messageList.size());
            }
        }
    }

    public void onTypingMessageReceived(String fromMid) {

    }

    @Override
    public void onOnlineMessageReceived(HSOnlineMessage message) {
        HSLog.d(TAG, "onOnlineMessageReceived");
        HSBundle bundle = new HSBundle();
        bundle.putString(SampleFragment.SAMPLE_NOTIFICATION_BUNDLE_STRING, message.getContent().toString());
        HSGlobalNotificationCenter.sendNotificationOnMainThread(SampleFragment.SAMPLE_NOTIFICATION_NAME, bundle);
    }

    @Override
    public void onUnreadMessageCountChanged(String mid, int newCount) {
    }


    @Override
    public void onReceivingRemoteNotification(JSONObject userInfo) {
        HSLog.d(TAG, "receive remote notification: " + userInfo);
        if (HSSessionMgr.getTopActivity() == null) {
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        soundPool= new SoundPool(10,AudioManager.STREAM_SYSTEM,5);
        id1 = soundPool.load(this, R.raw.message_ringtone_received, 1);
        id2 = soundPool.load(this, R.raw.message_ringtone_sent, 1);
        AudioFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        AudioFileName += "/audiorecordtest.3gp";
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        mContext=this;
        adapter=new MessageAdapter(chatActivity.this,R.layout.message_item,messageList);
        info=(TextView)findViewById(R.id.infoText);
        inputText=(EditText)findViewById(R.id.newText);
        send=(Button)findViewById(R.id.send);
        messageListView=(ListView)findViewById(R.id.message_list_view);
        messageListView.setAdapter(adapter);
        Intent intent=getIntent();
        userInfo=intent.getStringArrayExtra("info");
        info.setText("  Name: "+userInfo[0]+" Mid: "+userInfo[1]);
        chatterMid=userInfo[1];
        other=(Button)findViewById(R.id.otherInfo);
        //audio=(Button)findViewById(R.id.audioButtonSend);
        audioSenddddd=(Button)findViewById(R.id.audioooSend);
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        int ID = Integer.parseInt(userInfo[1]);
        notificationManager.cancel(ID);
        HSMessageManager.QueryResult result = HSMessageManager.getInstance().queryMessages(userInfo[1],0,-1);
        cursor = result.getCursor();
        List<HSBaseMessage>tempList=new ArrayList<HSBaseMessage>();
        tempList = result.getMessages();
        for(HSBaseMessage msg:tempList)
        {
            MyMessage myMsg=new MyMessage(msg);
            messageList.add(myMsg);
        }
        Collections.reverse(messageList);
        messageListView.setSelection(messageList.size());
        HSMessageManager.getInstance().addListener(this, new Handler());
        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputText.getText().toString();
                System.out.println(text);
                if (!"".equals(text)) {
                    Send(userInfo[1], text);
                    UnreadMessageProcess.getInstance().setLatestText(userInfo[1], text);
                    messageListView.setSelection(messageList.size());
                    inputText.setText("");
                }
            }
        });
        audioSenddddd.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (toastSpeaking != null)
                        toastSpeaking.cancel();
                    toastSpeaking = Toast.makeText(getApplicationContext(),
                            "讲话结束", Toast.LENGTH_SHORT);
                    toastSpeaking.setGravity(Gravity.CENTER, 0, 0);
                    toastSpeaking.show();

                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;
                    stopDateAudio = new Date();
                    SendAudio(userInfo[1], AudioFileName, (stopDateAudio.getTime() - startDateAudio.getTime()) / 1000.0);
                    UnreadMessageProcess.getInstance().setLatestText(userInfo[1], "您发送了一条语音");
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    toastSpeaking = Toast.makeText(getApplicationContext(),
                            "正在讲话", Toast.LENGTH_LONG);
                    toastSpeaking.setGravity(Gravity.CENTER, 0, 0);
                    toastSpeaking.show();

                    mRecorder = new MediaRecorder();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mRecorder.setOutputFile(AudioFileName);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    try {
                        mRecorder.prepare();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "prepare() failed");
                    }
                    mRecorder.start();
                    startDateAudio = new Date();
                }
                return false;
            }
        });
        other.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm =  (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                if(imm != null)
                {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),0);
                }
                showPopupWindow(v);
            }
        });
        messageListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id)
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(chatActivity.this);
                builder.setTitle("删除信息？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast t = Toast.makeText(chatActivity.this, "正在删除。。。", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.CENTER, 0, 0);
                        t.show();
                        List<HSBaseMessage> temp=new ArrayList<HSBaseMessage>();
                        temp.add(messageList.get(position).originalMsg);
                        HSMessageManager.getInstance().deleteMessages(temp);
                        deleteRefresh(messageList.get(position).msgID);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        builder.create().dismiss();
                    }
                });
                builder.create().show();
                return true;
            }
        });
        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyMessage msg=messageList.get(position);
                if(msg.type== HSMessageType.AUDIO)
                {
                    mPlayer = new MediaPlayer();
                    try{
                        mPlayer.setDataSource(msg.audioPath);
                        mPlayer.prepare();
                        mPlayer.start();
                    }catch(IOException e){
                        Log.e(LOG_TAG,"播放失败");
                    }
                    System.out.println("正在播放！！！！");
                }
                if(msg.type==HSMessageType.IMAGE)
                {
                    String path=new String(msg.imageDatuPath);
                    showImageBig(view,path);
                }
                if (msg.type ==HSMessageType.LOCATION) {
                    String[] info=new String[]{msg.Latitude,msg.Longitude,msg.Description};
                    Intent intent=new Intent(chatActivity.this,MapActivity.class);
                    intent.putExtra("info", info);
                    startActivity(intent);
                }
            }
        });
    }

    void SendAudio(String toMid, String fileName, double duration) {
        HSAudioMessage temp=new HSAudioMessage(toMid, fileName, duration);
        HSMessageManager.getInstance().send(temp, new SendMessageCallback() {

            @Override
            public void onMessageSentFinished(HSBaseMessage message, boolean success, HSError error) {
                HSLog.d(TAG, "success: " + success);
            }
        }, new Handler());
    }
    private void showPopupWindow(View view) {

        View contentView = LayoutInflater.from(mContext).inflate(
                R.layout.other_input_item, null);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        popupWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
        popupWindow.showAtLocation(chatActivity.this.findViewById(R.id.otherInfo), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
//        final Button button = (Button) contentView.findViewById(R.id.audioButtonSend);
//        button.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    if (toastSpeaking != null)
//                        toastSpeaking.cancel();
//                    toastSpeaking = Toast.makeText(getApplicationContext(),
//                            "讲话结束", Toast.LENGTH_SHORT);
//                    toastSpeaking.setGravity(Gravity.CENTER, 0, 0);
//                    toastSpeaking.show();
//
//                    mRecorder.stop();
//                    mRecorder.release();
//                    mRecorder = null;
//                    stopDateAudio = new Date();
//                    SendAudio(userInfo[1], AudioFileName, (stopDateAudio.getTime() - startDateAudio.getTime()) / 1000.0);
//                    UnreadMessageProcess.getInstance().setLatestText(userInfo[1], "您发送了一条语音");
//                }
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    toastSpeaking = Toast.makeText(getApplicationContext(),
//                            "正在讲话", Toast.LENGTH_LONG);
//                    toastSpeaking.setGravity(Gravity.CENTER, 0, 0);
//                    toastSpeaking.show();
//
//                    mRecorder = new MediaRecorder();
//                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//                    mRecorder.setOutputFile(AudioFileName);
//                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//                    try {
//                        mRecorder.prepare();
//                    } catch (IOException e) {
//                        Log.e(LOG_TAG, "prepare() failed");
//                    }
//                    mRecorder.start();
//                    startDateAudio = new Date();
//                }
//                return false;
//            }
//        });
        final Button cmrbutton = (Button) contentView.findViewById(R.id.cameraButtonSend);
        cmrbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                outputImage = new File(Environment.getExternalStorageDirectory(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(outputImage);
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });
        final Button phtbutton = (Button) contentView.findViewById(R.id.photoButtonSend);
        phtbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                outputImage = new File(Environment.getExternalStorageDirectory(),"output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(outputImage);
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, CROP_PHOTO);
            }
        });

        final Button posbutton = (Button) contentView.findViewById(R.id.posButtonSend);
        posbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (openGPSSettings()) getLocation(userInfo[1]);
            }
        });
    }



    private void showImageBig(View view,String path)
    {
        InputMethodManager imm =  (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        if(imm != null)
        {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),0);
        }
        View contentView = LayoutInflater.from(mContext).inflate(
                R.layout.image_show_datu, null);


        final PopupWindow popupWindow = new PopupWindow(contentView,
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
        popupWindow.setWidth(LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(LayoutParams.MATCH_PARENT);

        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;

            }
        });


        popupWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
        popupWindow.showAtLocation(chatActivity.this.findViewById(R.id.otherInfo), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        final ImageView imageView = (ImageView) contentView.findViewById(R.id.image_view_datu);
        Bitmap bitmat = BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(bitmat);
    }



    void SendImg(String to, String path) {
        HSImageMessage temp=new HSImageMessage(to, path);
        HSMessageManager.getInstance().send(temp, new SendMessageCallback() {
            @Override
            public void onMessageSentFinished(HSBaseMessage message, boolean success, HSError error) {
                HSLog.d(TAG, "success: " + success);
            }
        }, new Handler());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    SendImg(userInfo[1], outputImage.getAbsolutePath());
                }
                break;
            case CROP_PHOTO:
                Bitmap bm = null;
                ContentResolver resolver = getContentResolver();
                //try {
                    Uri originalUri = data.getData();
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String path = cursor.getString(column_index);
                    SendImg(userInfo[1], path);
                break;
        }
    }
    private void deleteRefresh(String delId)
    {
        for(int i=0;i<messageList.size();++i)
        {
            if(messageList.get(i).msgID.equals(delId))
            {
                messageList.remove(i);
                break;
            }
        }
        adapter.notifyDataSetChanged();
        messageListView.setSelection(messageList.size());
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult arg0) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        // TODO Auto-generated method stub

        this.result = result.getAddress();
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            this.result = "Unknown Address";
        }
    }
    @Override
    protected void onPause()
    {
        HSMessageManager.getInstance().markRead(chatterMid);
        super.onPause();
    }
    @Override
    protected void onDestroy()
    {
        HSMessageManager.getInstance().markRead(chatterMid);
        super.onDestroy();
    }
}
