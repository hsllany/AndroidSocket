package com.ubirouting.mozzservicecommunicator.msgdispatcher;

import android.support.v4.app.Fragment;

/**
 * @author Yang Tao on 16/6/21.
 */
public class FindableFragment extends Fragment implements Findable {

    final long findableId = System.currentTimeMillis();

    @Override
    public long getFindableId() {
        return findableId;
    }

    @Override
    public void execute(Message msg) {

    }

    @Override
    public boolean hasBeenDestroyed() {
        return isDetached();
    }
}
