package com.aroundme.mostain.AroundMe.Messaging.Addon;

import android.content.Context;

import com.aroundme.mostain.Class.Chat;
import com.aroundme.mostain.Class.User;
import com.aroundme.mostain.Utils.fcm.FcmNotificationBuilder;
import com.aroundme.mostain.Utils.service.Constants;
import com.aroundme.mostain.Utils.service.SharedPrefUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class ChatInteractor implements ChatContract.Interactor {
    private static final String TAG = "ChatInteractor";

    private ChatContract.OnSendMessageListener mOnSendMessageListener;
    private ChatContract.OnGetMessagesListener mOnGetMessagesListener;
    private ChatContract.OnUpdateMessageListener mOnUpdateMessagesListener;

    public ChatInteractor(ChatContract.OnSendMessageListener onSendMessageListener) {
        this.mOnSendMessageListener = onSendMessageListener;
    }

    public ChatInteractor(ChatContract.OnGetMessagesListener onGetMessagesListener) {
        this.mOnGetMessagesListener = onGetMessagesListener;
    }


    public ChatInteractor(ChatContract.OnSendMessageListener onSendMessageListener,
                          ChatContract.OnGetMessagesListener onGetMessagesListener, ChatContract.OnUpdateMessageListener onUpdateMessageListener ) {
        this.mOnSendMessageListener = onSendMessageListener;
        this.mOnGetMessagesListener = onGetMessagesListener;
        this.mOnUpdateMessagesListener = onUpdateMessageListener;
    }

    @Override
    public void sendMessageToFirebaseUser(final Context context, final Chat chat, final String receiverFirebaseToken) {

        String senderId = chat.getSenderUid();
        String receiverId = chat.getReceiverUid();


        if (senderId.equals(User.getUser().getUid())){

            DatabaseReference senderChat = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS).child(senderId).child(Constants.ARG_CHAT_ROOMS);
            DatabaseReference receiverChat = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS).child(receiverId).child(Constants.ARG_CHAT_ROOMS);

            DatabaseReference senderChatList = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS).child(senderId).child(Constants.ARG_CHAT_LIST);
            DatabaseReference receiverChatList = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS).child(receiverId).child(Constants.ARG_CHAT_LIST);

            senderChat.child(receiverId).child(String.valueOf(chat.getTimestamp())).setValue(chat);
            receiverChat.child(senderId).child(String.valueOf(chat.getTimestamp())).setValue(chat);

            senderChatList.child(receiverId).setValue(chat);
            receiverChatList.child(senderId).setValue(chat);

        } else {

            DatabaseReference senderChat = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS).child(senderId).child(Constants.ARG_CHAT_ROOMS);
            DatabaseReference receiverChat = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS).child(receiverId).child(Constants.ARG_CHAT_ROOMS);

            DatabaseReference senderChatList = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS).child(senderId).child(Constants.ARG_CHAT_LIST);
            DatabaseReference receiverChatList = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS).child(receiverId).child(Constants.ARG_CHAT_LIST);

            senderChat.child(senderId).child(String.valueOf(chat.getTimestamp())).setValue(chat);
            receiverChat.child(receiverId).child(String.valueOf(chat.getTimestamp())).setValue(chat);

            senderChatList.child(senderId).setValue(chat);
            receiverChatList.child(receiverId).setValue(chat);

        }

        //getMessageFromFirebaseUser(chat.getReporterUid(), chat.getReportedUid());

                // send push notification to the receiver

                if (chat.getType() == 1){

                    sendPushNotificationToReceiver(
                            chat.getSender(),
                            chat.getMessage(),
                            chat.getSenderUid(),
                            new SharedPrefUtil(context).getString(Constants.ARG_FIREBASE_TOKEN),
                            receiverFirebaseToken);


                } else if (chat.getType() == 2){

                    sendPushNotificationToReceiver(
                            chat.getSender(),
                            "sent you an audio",
                            chat.getSenderUid(),
                            new SharedPrefUtil(context).getString(Constants.ARG_FIREBASE_TOKEN),
                            receiverFirebaseToken);

                } else if (chat.getType() == 3){

                    sendPushNotificationToReceiver(
                            chat.getSender(),
                            "sent you an image",
                            chat.getSenderUid(),
                            new SharedPrefUtil(context).getString(Constants.ARG_FIREBASE_TOKEN),
                            receiverFirebaseToken);

                }


                mOnSendMessageListener.onSendMessageSuccess();

    }

    private void sendPushNotificationToReceiver(String username, String message, String uid, String firebaseToken, String receiverFirebaseToken) {

        // Check and sent push only if tokens are different
        if (!firebaseToken.equals(receiverFirebaseToken)){

            FcmNotificationBuilder.initialize()
                    .title(username)
                    .message(message)
                    .username(username)
                    .uid(uid)
                    .firebaseToken(firebaseToken)
                    .receiverFirebaseToken(receiverFirebaseToken)
                    .send();
        }

    }

    @Override
    public void getMessageFromFirebaseUser(String senderUid, String receiverUid) {


        if (senderUid.equals(User.getUser().getUid())){

             DatabaseReference senderChat = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS).child(senderUid).child(Constants.ARG_CHAT_ROOMS);

            senderChat.child(receiverUid).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    mOnGetMessagesListener.onGetMessagesSuccess(chat);


                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                    mOnUpdateMessagesListener.onUpdateMessageSuccess();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {


                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
                }
            });

        } else {

            final DatabaseReference receiverChat = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS).child(receiverUid).child(Constants.ARG_CHAT_ROOMS);

            receiverChat.child(senderUid).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    mOnGetMessagesListener.onGetMessagesSuccess(chat);


                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                    mOnUpdateMessagesListener.onUpdateMessageSuccess();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {


                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
                }
            });

        }

    }
}
