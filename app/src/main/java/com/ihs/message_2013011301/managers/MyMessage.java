package com.ihs.message_2013011301.managers;

import com.ihs.message_2013011301.types.HSAudioMessage;
import com.ihs.message_2013011301.types.HSBaseMessage;
import com.ihs.message_2013011301.types.HSImageMessage;
import com.ihs.message_2013011301.types.HSLocationMessage;
import com.ihs.message_2013011301.types.HSMessageType;
import com.ihs.message_2013011301.types.HSTextMessage;

public class MyMessage
{
    public HSMessageType type;
    public String text;
    public double duration;
    public String from;
    public String audioPath;
    public String imageSuolvetuPath;
    public String imageDatuPath;
    public String msgID;
    public HSBaseMessage originalMsg;
    public String Latitude;
    public String Longitude;
    public String Description;
    public String status;
    //图画内容
    public MyMessage(HSBaseMessage msg)
    {
        this.type=msg.getType();
        from=msg.getFrom();
        msgID=msg.getMsgID();
        originalMsg=msg;
        switch(msg.getType().getValue())
        {
            case 1:
                HSTextMessage temp1=(HSTextMessage)msg;
                text=temp1.getText();
                break;
            case 2:
                HSAudioMessage temp2=(HSAudioMessage)msg;
                this.duration=temp2.getDuration();
                text="";
                for(int i=0;i<this.duration&&i<6;++i)
                    text+="   ";
                text+=new java.text.DecimalFormat("#.0").format(this.duration)+" s";
                audioPath=new String(temp2.getAudioFilePath());
                break;
            case 5:
                HSLocationMessage temp4=(HSLocationMessage)msg;
                text="收到位置";
                Latitude = ((Double)temp4.getLatitude()).toString();
                Longitude = ((Double)temp4.getLongitude()).toString();
                Description = temp4.getDescription();
                break;
            default:
                HSImageMessage temp3=(HSImageMessage)msg;
                this.imageSuolvetuPath=temp3.getThumbnailFilePath();
                this.imageDatuPath=temp3.getNormalImageFilePath();
                text="收到一张图片";
                break;
        }
    }
}
