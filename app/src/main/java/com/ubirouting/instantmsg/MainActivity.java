package com.ubirouting.instantmsg;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ubirouting.instantmsg.msgdispatcher.FindableActivity;
import com.ubirouting.instantmsg.msgdispatcher.MessageConsumeListener;
import com.ubirouting.instantmsg.msgs.DispatchableMessage;
import com.ubirouting.instantmsg.msgservice.MsgService;

public class MainActivity extends FindableActivity {

    private TextView mTxt;

    StringBuilder sb = new StringBuilder();

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
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SecondActivity.class);
                MainActivity.this.startActivity(i);
            }
        });

        Intent i = new Intent(this, MsgService.class);
        startService(i);

        registerListener(DispatchableMessage.class, new MessageConsumeListener() {
            @Override
            public void consume(DispatchableMessage msg) {
                sb.append(msg + "\n");
                mTxt.setText(sb.toString());
            }
        });
    }
}
