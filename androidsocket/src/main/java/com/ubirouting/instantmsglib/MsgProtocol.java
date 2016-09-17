package com.ubirouting.instantmsglib;

import com.ubirouting.instantmsglib.msgs.InstantMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yang Tao on 16/9/17.
 */
public abstract class MsgProtocol {

    private List<ProtocolItem> mRegisteredProtocols = new ArrayList<>();

    public final Class<?> messageTypeFromProtocol(String protocol) throws ClassNotFoundException {
        String clazzName = messageTypeFromProtocolInner(protocol);

        if (clazzName == null)
            throw new IllegalArgumentException("unknown protocol " + protocol + ", please register them");
        else {
            return Class.forName(clazzName);
        }
    }

    private String messageTypeFromProtocolInner(String protocol) {
        for (ProtocolItem item : mRegisteredProtocols) {
            if (item.protocolName.equals(protocol)) {
                return item.msgClassName;
            }
        }
        return null;
    }

    public final String protocolFromMessage(InstantMessage msg) {
        String protocol = protocolFromMessageInner(msg.getClass());
        if (protocol == null)
            throw new IllegalArgumentException("unknown msg type " + msg.getClass() + ", please register them");
        else
            return protocol;
    }

    private String protocolFromMessageInner(Class<? extends InstantMessage> msgClass) {
        for (ProtocolItem item : mRegisteredProtocols) {
            if (item.msgClassName.equals(msgClass.getName())) {
                return item.protocolName;
            }
        }

        return null;
    }

    protected final void registerProtocol(Class<? extends InstantMessage> msgClass, String protocol) {
        if (protocolFromMessageInner(msgClass) == null && messageTypeFromProtocolInner(protocol) == null) {
            ProtocolItem item = new ProtocolItem(msgClass.getName(), protocol);
            mRegisteredProtocols.add(item);
        } else
            throw new IllegalArgumentException("msg or protocol is already existing.");
    }

    private static class ProtocolItem {
        private String msgClassName;
        private String protocolName;

        public ProtocolItem(String msgClassName, String protocolName) {
            this.msgClassName = msgClassName;
            this.protocolName = protocolName;
        }
    }


}
