package com.ubirouting.instantmsg.msgservice;

import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;

import com.ubirouting.instantmsg.basic.Findable;
import com.ubirouting.instantmsg.msgs.InstantMessage;
import com.ubirouting.instantmsg.serialization.AbstractSerializer;

/**
 * @author Yang Tao on 16/8/17.
 */
public class Transaction implements Parcelable {

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    private byte[] transData;

    public Transaction(InstantMessage msg, AbstractSerializer serializer) {
        transData = serializer.buildWithObject(msg);
    }

    protected Transaction(Parcel in) {
        transData = in.createByteArray();
    }

    public static Message getMessage(InstantMessage msg, Messenger messenger, Findable findable, int status, AbstractSerializer serializer) {
        android.os.Message handlerMessage = android.os.Message.obtain();
        handlerMessage.getData().putParcelable("transaction", new Transaction(msg, serializer));
        handlerMessage.what = status;
        if (findable != null)
            handlerMessage.arg1 = findable.getFindableId();

        if (messenger != null)
            handlerMessage.replyTo = messenger;
        return handlerMessage;
    }

    public static InstantMessage getInstantMessage(Message message, AbstractSerializer serializer) {
        message.getData().setClassLoader(Transaction.class.getClassLoader());
        Transaction transaction = message.getData().getParcelable("transaction");
        return transaction != null ? (InstantMessage) serializer.buildViaBytes(transaction.transData) : null;
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
