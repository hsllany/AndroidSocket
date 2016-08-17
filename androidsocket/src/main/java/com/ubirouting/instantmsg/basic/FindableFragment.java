package com.ubirouting.instantmsg.basic;

import android.support.v4.app.Fragment;

import com.ubirouting.instantmsg.msgs.InstantMessage;

/**
 * @author Yang Tao on 16/6/21.
 */
public abstract class FindableFragment extends Fragment implements Findable {
    
    @Override
    public void onGetInstantMessageReply(InstantMessage msg) {

    }
}
