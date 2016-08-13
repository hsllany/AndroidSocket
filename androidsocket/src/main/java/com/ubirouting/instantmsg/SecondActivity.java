package com.ubirouting.instantmsg;

import android.os.Bundle;
import android.util.Log;

import com.ubirouting.instantmsg.basic.FindableActivity;

/**
 * @author Yang Tao on 16/6/20.
 */
public class SecondActivity extends FindableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ACTTEST", getTaskId() + "");

//        sendMessageToSocketStream(new DispatchMessage(), new MessageConsumeListener() {
//            @Override
//            public void consume(DispatchMessage msg) {
//                Toast.makeText(SecondActivity.this, "Seconde " + msg.getMessageId().toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ACTTEST", "88");
    }
}
