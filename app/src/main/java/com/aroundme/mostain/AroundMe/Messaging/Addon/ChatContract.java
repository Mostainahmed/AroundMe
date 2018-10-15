package com.aroundme.mostain.AroundMe.Messaging.Addon;

import android.content.Context;

import com.angopapo.aroundme2.Class.Chat;


public interface ChatContract {
    interface View {
        void onSendMessageSuccess();

        void onUpdateMessageSuccess();

        void onSendMessageFailure(String message);

        void onGetMessagesSuccess(Chat chat);

        void onGetMessagesFailure(String message);
    }

    interface Presenter {
        void sendMessage(Context context, Chat chat, String receiverFirebaseToken);

        void getMessage(String senderUid, String receiverUid);
    }

    interface Interactor {
        void sendMessageToFirebaseUser(Context context, Chat chat, String receiverFirebaseToken);

        void getMessageFromFirebaseUser(String senderUid, String receiverUid);
    }

    interface OnSendMessageListener {
        void onSendMessageSuccess();

        void onSendMessageFailure(String message);
    }

    interface OnUpdateMessageListener {
        void onUpdateMessageSuccess();

    }

    interface OnGetMessagesListener {
        void onGetMessagesSuccess(Chat chat);

        void onGetMessagesFailure(String message);
    }

}
