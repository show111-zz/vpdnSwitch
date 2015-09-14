package com.example.vpdnswitch;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.TelephonyManager;

/**
 * 手机网络设置工具类
 * Created by lhf on 2015/9/14.
 */
public class ArcVPDN {

    private Context gContext;
    private int gDefaultApn = -1;

    private static final Uri APN_TABLE_URI = Uri.parse("content://com.example.vpdnswitch.telephony/carriers");  //所有的APN配置信息位置
    private static final Uri PREFERED_APN_URI = Uri.parse("content://com.example.vpdnswitch.telephony/carriers/preferapn");//当前的APN

    public ArcVPDN(Context gContext) {
        this.gContext = gContext;
        gDefaultApn = getDefault();
    }

    /**
     * 获取默认的网络编号
     * */
    private int getDefault() {
        Cursor cursor = gContext.getContentResolver().query(PREFERED_APN_URI,null,null,null,null);
        if(cursor == null){
            return -1;
        }

        int apnId = -1;
        if(cursor != null && cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex("_id"));
            apnId = Integer.valueOf(id);
        }
        return apnId;
    }

    /**
     * 根据网络名称返回该网络编号
     * */
    public int getApn(String _name){
        Cursor cursor = gContext.getContentResolver().query(APN_TABLE_URI,null,null,null,null);
        if(cursor != null && cursor.moveToNext()){
            int apnId = cursor.getInt(cursor.getColumnIndex("_id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            if(name.equals(_name)){
                return apnId;
            }
        }
        return -1;
    }


    /**
     * 根据手机网络名称判断是否存在
     * */
    public boolean isExist(String name){
        Cursor cursor = gContext.getContentResolver().query(APN_TABLE_URI,null,null,null,null);
        if(cursor != null && cursor.moveToNext()){
            String name2 = cursor.getString(cursor.getColumnIndex("name"));
            if(name2.equals(name)){
                return true;
            }
        }
        return false;
    }

    /**
     * 添加vpdn  _name,_user,_password,_apn
     * */
    public void addApn(String _name,String _user,String _password,String _apn){
        TelephonyManager tm = (TelephonyManager) gContext.getSystemService(gContext.TELEPHONY_SERVICE);
        String numeric = tm.getSimOperator();
        String mcc = numeric.substring(0, 3);
        String mnc = numeric.substring(3, numeric.length());
        String simOperator = tm.getSimOperator();

        ContentResolver resolver = gContext.getContentResolver();
        ContentValues values = new ContentValues();
        values.put("name",_name);
        values.put("user",_user);
        values.put("password",_password);
        values.put("apn",_apn);
        values.put("mcc",mcc);
        values.put("mnc",mnc);
        values.put("numeric",simOperator);
        resolver.insert(APN_TABLE_URI,values);
    }

    /**
     * 获取默认网络名称
     * */
    public String defaultApnName(){
        Cursor cursor = gContext.getContentResolver().query(PREFERED_APN_URI,null,null,null,null);
        if(cursor == null){
            return "";
        }
        if(cursor != null && cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex("name"));
            return name;
        }
        return "";
    }

    /**
     * 根据编号设置网络
     * */
    public void setDefault(int _apnId){
        if(_apnId == gDefaultApn){
            return;
        }
        ContentResolver resolver = gContext.getContentResolver();
        ContentValues values = new ContentValues();
        values.put("apn_id",_apnId);
        resolver.update(PREFERED_APN_URI,values,null,null);
    }

    public void release(){
        if(gDefaultApn != -1){
            ContentResolver resolver = gContext.getContentResolver();
            ContentValues values = new ContentValues();
            values.put("apn_id",gDefaultApn);
            resolver.update(PREFERED_APN_URI,values,null,null);
        }
    }


}
