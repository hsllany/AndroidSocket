package com.ubirouting.instantmsg;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

import com.ubirouting.instantmsg.msgdispatcher.FindableActivity;
import com.ubirouting.instantmsg.msgdispatcher.MessageConsumeListener;
import com.ubirouting.instantmsg.msgs.Heartbeat;
import com.ubirouting.instantmsg.msgs.Message;
import com.ubirouting.instantmsg.msgservice.MsgService;

public class MainActivity extends FindableActivity {

    StringBuilder sb = new StringBuilder();
    private TextView mTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        mTxt = (TextView) findViewById(R.id.hellotext);


        Button button = (Button) findViewById(R.id.another);
        assert button != null;

        Intent i = new Intent(this, MsgService.class);
        startService(i);

        registerListener(Heartbeat.class, new MessageConsumeListener() {
            @Override
            public void consume(Message msg) {
                sb.append(msg + "\n");
                mTxt.setText(sb.toString());
            }
        });
    }
}
