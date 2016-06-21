Message Dispatcher
=======

A test expirement of communication from service to activities.

###Usage

Send message to Instante Message Service(not included in this rep.), which is responsibile for communication with Server. When client receive the reply message, which has the same MessageId with the sending one, MessageConsumeListener will be invoked to update UI.

```
	Message sendMessage = new Message();
	sendMessage(sendMessage, new MessageConsumeListener() {
            @Override
            public void consume(Message msg) {
            	//msg usually returned by server, which has the same MessageId with sendMessage.
                Toast.makeText(SecondActivity.this, "Seconde " + msg.getMessageId().toString(), Toast.LENGTH_SHORT).show();
            }
        });
```

Register a broadcast handler:

```
	registerListener(Message.class, new MessageConsumeListener() {
            @Override
            public void consume(Message msg) {
                sb.append(msg + "\n");
                mTxt.setText(sb.toString());
            }
        });
```

No matter who send the Message via sendMessage, all Activity or Fragment which register this kind of Message, will be also notified the Message comes through MessageConsumeListener.consume().


