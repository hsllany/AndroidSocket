package com.ubirouting.instantmsg.basic;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.ubirouting.instantmsg.msgs.InstantMessage;

/**
 * @author Yang Tao on 16/6/21.
 */
public abstract class FindableFragment extends Fragment implements Findable {

    private boolean isParentFindableActivity;
    private FindableActivity parentActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity parentActivity = getActivity();

        if (parentActivity != null) {
            isParentFindableActivity = parentActivity instanceof FindableActivity;
            if (isParentFindableActivity)
                this.parentActivity = (FindableActivity) parentActivity;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onGetInstantMessageReply(InstantMessage msg) {

    }

    @Override
    public final void sendMessage(InstantMessage msg, MessageConsumeListener l) {
        //delegate to parent
        parentActivity.sendMessage(msg, l);
    }
}
