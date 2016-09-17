package com.ubirouting.instantmsglib.msgservice;

import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;

import com.ubirouting.instantmsglib.$Checkr;
import com.ubirouting.instantmsglib.Findable;
import com.ubirouting.instantmsglib.msgs.InstantMessage;
import com.ubirouting.instantmsglib.serialization.AbstractSerializer;

import org.jetbrains.annotations.NotNull;

/**
 * @author Yang Tao on 16/8/17.
 */
public class InstantMessageConverter {

    static final int MSG_WHAT = 0xfa;

    public static Message getMessage(InstantMessage msg, Messenger messenger, Findable findable, AbstractSerializer serializer) {
        android.os.Message handlerMessage = android.os.Message.obtain();
        handlerMessage.getData().putParcelable("transaction", new InstantMessageParcelableWrapper(msg, serializer));
        handlerMessage.what = MSG_WHAT;
        if (findable != null)
            handlerMessage.arg1 = findable.getFindableId();

        if (messenger != null)
            handlerMessage.replyTo = messenger;
        return handlerMessage;
    }

    public static
    @NotNull
    InstantMessage getInstantMessage(Message message, AbstractSerializer serializer) {
        message.getData().setClassLoader(InstantMessageConverter.class.getClassLoader());
        InstantMessageParcelableWrapper transaction = message.getData().getParcelable("transaction");
        return transaction != null ? (InstantMessage) serializer.buildObject(transaction.transData) : null;
    }

    /**
     * Wraps the instantMessage so that it can be transferred in IPC.
     */
    private static class InstantMessageParcelableWrapper implements Parcelable {

        public static final Creator<InstantMessageParcelableWrapper> CREATOR = new Creator<InstantMessageParcelableWrapper>() {
            @Override
            public InstantMessageParcelableWrapper createFromParcel(Parcel in) {
                return new InstantMessageParcelableWrapper(in);
            }

            @Override
            public InstantMessageParcelableWrapper[] newArray(int size) {
                return new InstantMessageParcelableWrapper[size];
            }
        };
        private byte[] transData;

        private InstantMessageParcelableWrapper(InstantMessage msg, AbstractSerializer serializer) {
            $Checkr.notNull(msg, "can't be null");
            transData = serializer.buildBytes(msg);
        }

        protected InstantMessageParcelableWrapper(Parcel in) {
            transData = in.createByteArray();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByteArray(transData);
        }
    }


}
