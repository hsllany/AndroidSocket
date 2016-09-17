package com.ubirouting.testinstant;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ubirouting.instantmsg.R;
import com.ubirouting.instantmsglib.FindableActivity;
import com.ubirouting.instantmsglib.MessageConsumeListener;
import com.ubirouting.instantmsglib.MsgProtocol;
import com.ubirouting.instantmsglib.MsgServiceConfig;
import com.ubirouting.instantmsglib.MsgServiceLoader;
import com.ubirouting.instantmsglib.msgs.Heartbeat;
import com.ubirouting.instantmsglib.msgs.InstantMessage;
import com.ubirouting.instantmsglib.msgservice.MsgService;

public class MainActivity extends FindableActivity {

    StringBuilder sb = new StringBuilder();
    private TextView mTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init code
        MsgServiceConfig config = (new MsgServiceConfig.Builder()).withHostAndPort("192.168.1.105", 8001).build();
        MsgServiceLoader.init(this, config, new MsgProtocol() {

        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        mTxt = (TextView) findViewById(R.id.hellotext);


        Button button = (Button) findViewById(R.id.another);
        assert button != null;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.sendMessage(new ExampleMessage(MainActivity.this, "Hello world"), new MessageConsumeListener() {
                    @Override
                    public void consume(InstantMessage msg) {
                        Toast.makeText(MainActivity.this, msg.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Intent i = new Intent(this, MsgService.class);
        startService(i);

        registerMessageBroadcastListener(Heartbeat.class, new MessageConsumeListener() {
            @Override
            public void consume(InstantMessage msg) {
                sb.append(msg + "\n");
                mTxt.setText(sb.toString());
            }
        });
    }

    @Override
    public int getFindableId() {
        return 1;
    }
}
