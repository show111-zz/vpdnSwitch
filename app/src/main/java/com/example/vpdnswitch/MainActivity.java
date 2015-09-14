package com.example.vpdnswitch;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    private Button switchBtn;
    private int apnId = -1;
    private ArcVPDN arcVPDN;
    private String TAG = "vpnd";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        switchBtn = (Button) findViewById(R.id.switch_btn);
        arcVPDN = new ArcVPDN(this);

        Log.i(TAG,arcVPDN.defaultApnName());
        Log.i(TAG,arcVPDN.getApn("中国电信互联网设置CTNET")+"");

        if(!arcVPDN.defaultApnName().equals("IGRPVPDN")){
            switchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!arcVPDN.isExist("网络名称")){
                        startActivityForResult(new Intent(Settings.ACTION_APN_SETTINGS),0);
                    }
                    apnId = arcVPDN.getApn("网络名称");
                    arcVPDN.setDefault(apnId);
                }
            });
        }

    }


}
