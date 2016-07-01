package com.ubirouting.instantmsg;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.ubirouting.instantmsg.msgdispatcher.FindableActivity;

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

        assert fab != null;
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("LALA", "click");
//                MainActivity.this.sendMessage(new MessageImp(), new MessageConsumeListener() {
//                    @Override
//                    public void consume(MessageImp msg) {
//                        Toast.makeText(MainActivity.this, msg.getMessageId().toString(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//
//        Button button = (Button) findViewById(R.id.another);
//        assert button != null;
//        button.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(MainActivity.this, SecondActivity.class);
//                MainActivity.this.startActivity(i);
//            }
//        });
//
//        Intent i = new Intent(this, MessageService.class);
//        startService(i);
//
//        registerListener(MessageImp.class, new MessageConsumeListener() {
//            @Override
//            public void consume(MessageImp msg) {
//                sb.append(msg + "\n");
//                mTxt.setText(sb.toString());
//            }
//        });
    }
}
