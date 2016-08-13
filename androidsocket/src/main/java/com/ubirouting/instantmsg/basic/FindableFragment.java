package com.ubirouting.instantmsg.basic;

import android.support.v4.app.Fragment;

import com.ubirouting.instantmsg.msgs.InstantMessage;

/**
 * @author Yang Tao on 16/6/21.
 */
public class FindableFragment extends Fragment implements Findable {

    final int findableId = (int) System.currentTimeMillis();

    @Override
    public int getFindableId() {
        return findableId;
    }

    @Override
    public void execute(InstantMessage msg) {

    }

    @Override
    public boolean hasBeenDestroyed() {
        return isDetached();
    }
}
