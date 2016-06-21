package com.ubirouting.mozzservicecommunicator;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ubirouting.mozzservicecommunicator.msgdispatcher.FindableActivity;
import com.ubirouting.mozzservicecommunicator.msgdispatcher.Message;
import com.ubirouting.mozzservicecommunicator.msgdispatcher.MessageConsumeListener;

/**
 * @author Yang Tao on 16/6/20.
 */
public class SecondActivity extends FindableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ACTTEST", getTaskId() + "");

        sendMessage(new Message(), new MessageConsumeListener() {
            @Override
            public void consume(Message msg) {
                Toast.makeText(SecondActivity.this, "Seconde " + msg.getMessageId().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ACTTEST", "88");
    }
}
