package com.ubirouting.instantmsg.msgservice;

import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;

import com.ubirouting.instantmsg.basic.Findable;
import com.ubirouting.instantmsg.msgs.InstantMessage;
import com.ubirouting.instantmsg.msgs.MessageFactory;
import com.ubirouting.instantmsg.serialization.Serializer;
import com.ubirouting.instantmsg.serialization.bytelib.PrimaryDatas;

import java.util.Arrays;

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

    public Transaction(InstantMessage msg) {
        byte[] msgData = Serializer.serializer().buildWithObject(msg);
        int msgCode = MessageFactory.codeFromMessage(msg);

        transData = new byte[msgData.length + 4];
        PrimaryDatas.i2b(msgCode, transData, 0);
        System.arraycopy(msgData, 0, transData, 4, msgData.length);
    }

    protected Transaction(Parcel in) {
        transData = in.createByteArray();
    }

    public static Message getMessage(InstantMessage msg, Messenger messenger, Findable findable, int status) {
        android.os.Message handlerMessage = android.os.Message.obtain();
        handlerMessage.obj = new Transaction(msg);
        handlerMessage.what = status;
        if (findable != null)
            handlerMessage.arg1 = findable.getFindableId();

        if (messenger != null)
            handlerMessage.replyTo = messenger;
        return handlerMessage;
    }

    public static InstantMessage getInstantMessage(Message message) {
        Transaction transaction = (Transaction) message.obj;
        return transaction.instantMessage();
    }

    public InstantMessage instantMessage() {
        int msgCode = PrimaryDatas.b2i(transData, 0);
        byte[] msgData = Arrays.copyOfRange(transData, 4, transData.length);

        return MessageFactory.buildWithCode(msgCode, msgData, Serializer.serializer());
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
