package com.ihs.demo.message_2013011301;

import com.ihs.message_2013011301.managers.HSMessageManager;
import com.ihs.message_2013011301.managers.MyMessage;
import com.ihs.message_2013011301.types.HSBaseMessage;

import java.util.ArrayList;
import java.util.List;

public class UnreadMessageProcess
{
    private List<dataStruct> data;
    private static UnreadMessageProcess instance=null;
    private UnreadMessageProcess()
    {
        data=new ArrayList<dataStruct>();
        List<Contact> temp=new ArrayList<Contact>();
        temp.addAll(FriendManager.getInstance().getAllFriends());
        for(Contact contact:temp)
        {
            HSMessageManager.QueryResult result = HSMessageManager.getInstance().queryMessages(contact.getMid(),0,-1);
            long cursor = result.getCursor();
            List<HSBaseMessage>tempList=new ArrayList<HSBaseMessage>();
            tempList = result.getMessages();
            if(tempList.size()!=0)
            {
                MyMessage msg=new MyMessage(tempList.get(0));
                dataStruct d=new dataStruct(contact.getMid(),msg.text);
                data.add(d);
            }
            else
            {
                dataStruct d=new dataStruct(contact.getMid(),"");
                data.add(d);
            }
        }
    }
    public static UnreadMessageProcess getInstance()
    {
        if(instance==null)
            instance=new UnreadMessageProcess();
        return instance;
    }

   public void setLatestText(String mid,String text)
   {
       for(dataStruct d:data)
       {
           if(d.mid.equals(mid))
           {
               d.text=text;
               break;
           }
       }
   }
    public String getText(String mid)
    {
        String ans="";
        for(dataStruct d:data)
        {
            if(d.mid.equals(mid))
            {
                ans=d.text;
                break;
            }
        }
        return ans;
    }
   class dataStruct
   {
        String mid;
        String text;
        dataStruct(String m,String t)
        {
            this.mid=m;
            this.text=t;
        }
   }
}
