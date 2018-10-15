package com.aroundme.mostain.Adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.angopapo.aroundme2.AroundMe.Messaging.Activity.ImageViewerActivity;
import com.angopapo.aroundme2.Class.Chat;
import com.angopapo.aroundme2.Class.User;
import com.angopapo.aroundme2.R;
import com.angopapo.aroundme2.Utils.service.Constants;
import com.angopapo.aroundme2.Utils.uiAudio.AudioWife;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vanniktech.emoji.EmojiTextView;

import org.joda.time.DateTime;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int DEFAULT_TYPE = 0;

    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    private static final int AUDIO_TYPE_ME = 3;
    private static final int AUDIO_TYPE_OTHER = 4;

    private static final int IMAGE_TYPE_ME = 5;
    private static final int IMAGE_TYPE_OTHER = 6;

    private List<Chat> mChats;

    private DatabaseReference database;

    public ChatAdapter(List<Chat> chats) {
        mChats = chats;
    }

    public void add(Chat chat) {

        mChats.add(chat);
        notifyItemInserted(mChats.size() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.item_chat_mine, parent, false);
                viewHolder = new MyChatViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.item_chat_other, parent, false);
                viewHolder = new OtherChatViewHolder(viewChatOther);
                break;
            case AUDIO_TYPE_ME:
                View audioChatMine = layoutInflater.inflate(R.layout.item_audio_mine, parent, false);
                viewHolder = new MyChatViewHolder(audioChatMine);
                break;
            case AUDIO_TYPE_OTHER:
                View audioChatOther = layoutInflater.inflate(R.layout.item_audio_other, parent, false);
                viewHolder = new OtherChatViewHolder(audioChatOther);
                break;
            case IMAGE_TYPE_ME:
                View imageChatMine = layoutInflater.inflate(R.layout.item_image_mine, parent, false);
                viewHolder = new MyChatViewHolder(imageChatMine);
                break;
            case IMAGE_TYPE_OTHER:
                View imageChatOther = layoutInflater.inflate(R.layout.item_image_other, parent, false);
                viewHolder = new OtherChatViewHolder(imageChatOther);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (TextUtils.equals(mChats.get(position).getSenderUid(), User.getUser().getUid())) {
            configureMyChatViewHolder((MyChatViewHolder) holder, position);
        } else {
            configureOtherChatViewHolder((OtherChatViewHolder) holder, position);
        }
    }

    private void configureMyChatViewHolder(MyChatViewHolder myChatViewHolder, int position) {
        Chat chat = mChats.get(position);

        if (chat.getType() == 1){

            // Text Message
            database = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(chat.getSenderUid());
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    if (user.getPhotoUrl() != null){

                        Glide.with(getApplicationContext())
                                .load(user.photoUrl)
                                .placeholder(R.drawable.profile_default_photo)
                                .dontAnimate()
                                .fitCenter()
                                .into(myChatViewHolder.mProfilePhoto);

                    } else {

                        Glide.with(getApplicationContext())
                                .load(R.drawable.profile_default_photo)
                                .placeholder(R.drawable.profile_default_photo)
                                .dontAnimate()
                                .fitCenter()
                                .into(myChatViewHolder.mProfilePhoto);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            DateTime senttime = new DateTime(chat.getTimestamp());

            String time = new PrettyTime().format( senttime.toDate());

            myChatViewHolder.time.setText(time);

            if (chat.isIsread()){

                myChatViewHolder.Status.setImageResource(R.drawable.ic_read_24dp);

            } else {

                myChatViewHolder.Status.setImageResource(R.drawable.ic_sent_24dp);
            }

            myChatViewHolder.txtChatMessage.setText(chat.getMessage());

        } else if (chat.getType() == 2){

            // Audio Message

            database = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(chat.getSenderUid());
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    if (user.getPhotoUrl() != null){

                        Glide.with(getApplicationContext())
                                .load(user.photoUrl)
                                .placeholder(R.drawable.profile_default_photo)
                                .dontAnimate()
                                .fitCenter()
                                .into(myChatViewHolder.AudiomProfilePhoto);

                    } else {

                        Glide.with(getApplicationContext())
                                .load(R.drawable.profile_default_photo)
                                .placeholder(R.drawable.profile_default_photo)
                                .dontAnimate()
                                .fitCenter()
                                .into(myChatViewHolder.AudiomProfilePhoto);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            DateTime senttime = new DateTime(chat.getTimestamp());

            String time = new PrettyTime().format( senttime.toDate());

            myChatViewHolder.audioTime.setText(time);

            if (chat.isIsread()){

                myChatViewHolder.AudioStatus.setImageResource(R.drawable.ic_read_24dp);

            } else {

                myChatViewHolder.AudioStatus.setImageResource(R.drawable.ic_sent_24dp);
            }

            //myChatViewHolder.prograssbar.setText(chat.message);

            // Audio Staffs here

            if (mChats.get(position) == chat){



                AudioWife.getInstance()
                        .init(getApplicationContext(), Uri.parse(chat.getFileurl()))
                        .setPlayView(myChatViewHolder.mPay)
                        .setPauseView(myChatViewHolder.mPause)
                        .setSeekBar(myChatViewHolder.prograssbar)
                        .setRuntimeView(myChatViewHolder.CurrentTime)
                        .setTotalTimeView(myChatViewHolder.TotalTime);

                AudioWife.getInstance().addOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        //Toast.makeText(getBaseContext(), "Completed", Toast.LENGTH_SHORT).show();
                        // do you stuff
                        //AudioWife.getInstance().play();
                        //AudioWife.getInstance().release();
                        //AudioWife.getInstance().kill();
                        //myChatViewHolder.mPay.setImageResource(R.drawable.ic_play_white);
                        //myChatViewHolder.mPause.setVisibility(View.GONE);
                    }
                });

                AudioWife.getInstance().addOnPlayClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getBaseContext(), "Play", Toast.LENGTH_SHORT).show();
                        // Lights-Camera-Action. Lets dance.

                        myChatViewHolder.mPay.setVisibility(View.GONE);
                        myChatViewHolder.mPause.setVisibility(View.VISIBLE);
                    }
                });

                AudioWife.getInstance().addOnPauseClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        myChatViewHolder.mPay.setVisibility(View.VISIBLE);
                        myChatViewHolder.mPause.setVisibility(View.GONE);
                        //Toast.makeText(getBaseContext(), "Pause", Toast.LENGTH_SHORT).show();
                        // Your on audio pause stuff.
                    }
                });
            }


            // Audio Staffs here
           /* myChatViewHolder.mPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (chat.fileurl == null)
                        return;

                    //myChatViewHolder.mPay.setImageResource(R.drawable.ic_play_gray);


                }
            });*/

        } else if (chat.getType() == 3){


            database = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(chat.getSenderUid());
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    if (user.getPhotoUrl() != null){

                        Glide.with(getApplicationContext())
                                .load(user.photoUrl)
                                .placeholder(R.drawable.profile_default_photo)
                                .dontAnimate()
                                .fitCenter()
                                .into(myChatViewHolder.mProfilePhotoImage);

                    } else {

                        Glide.with(getApplicationContext())
                                .load(R.drawable.profile_default_photo)
                                .placeholder(R.drawable.profile_default_photo)
                                .dontAnimate()
                                .fitCenter()
                                .into(myChatViewHolder.mProfilePhotoImage);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            DateTime senttime = new DateTime(chat.getTimestamp());

            String time = new PrettyTime().format( senttime.toDate());

            myChatViewHolder.timeImage.setText(time);

            if (chat.isIsread()){

                myChatViewHolder.StatusImage.setImageResource(R.drawable.ic_read_24dp);

            } else {

                myChatViewHolder.StatusImage.setImageResource(R.drawable.ic_sent_24dp);
            }

            myChatViewHolder.mImageUrl.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {


                    if (chat.getImageUrl() != null){

                        Intent imageViewerIntent = new Intent(getApplicationContext(), ImageViewerActivity.class);
                        imageViewerIntent.putExtra(ImageViewerActivity.EXTRA_IMAGE_URL,chat.getImageUrl());
                        imageViewerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(imageViewerIntent);


                    } else {

                        Toast.makeText(getApplicationContext(), "No Image found", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            if (chat.getImageUrl() != null){

                Glide.with(getApplicationContext())
                        .load(chat.getImageUrl())
                        .placeholder(R.color.gray)
                        .error(R.color.gray)
                        .dontAnimate()
                        .centerCrop()
                        .into(myChatViewHolder.mImageUrl);

            } else {

                Glide.with(getApplicationContext())
                        .load(R.color.gray)
                        .placeholder(R.color.gray)
                        .dontAnimate()
                        .centerCrop()
                        .into(myChatViewHolder.mImageUrl);
            }
        }


        /*Chat chatMessegae = mChats.get(position);
        myChatViewHolder.txtChatMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                myChatViewHolder.txtChatMessage.setBackgroundResource(R.drawable.chat_out_bg_dark);

                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(myChatViewHolder.mView.getContext());
                builder.setTitle("Delete this message ?");
                builder.setCancelable(false);
                // add a list
                String[] animals = {"Delete", "Cancel"};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:

                                myChatViewHolder.txtChatMessage.setBackgroundResource(R.drawable.chat_out_bg_dark);

                                DatabaseReference chat = FirebaseDatabase.getInstance().getReference()
                                        .child(Constants.ARG_USERS) // Users
                                        .child(User.getUser().getUid()) // Me
                                        .child(Constants.ARG_CHAT_ROOMS) // Chat
                                        .child(chatMessegae.getReceiverUid()) // Receiver
                                        .child(String.valueOf(chatMessegae.getTimestamp())); // Chat ID

                                chat.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(myChatViewHolder.mView.getContext(), "Message deleted", Toast.LENGTH_LONG).show();
                                    }
                                });


                            case 1:
                                dialog.dismiss();
                                myChatViewHolder.txtChatMessage.setBackgroundResource(R.drawable.chat_out_bg);

                        }
                    }
                });

                // create and show the alert dialog
                AlertDialog dialog2 = builder.create();
                dialog2.show();
                return false;
            }
        });

        myChatViewHolder.lView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                myChatViewHolder.lView.setBackgroundResource(R.drawable.chat_out_bg_dark);

                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(myChatViewHolder.mView.getContext());
                builder.setTitle("Delete this audio ?");
                builder.setCancelable(false);
                // add a list
                String[] animals = {"Delete", "Cancel"};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:

                                myChatViewHolder.lView.setBackgroundResource(R.drawable.chat_out_bg_dark);

                                DatabaseReference chat = FirebaseDatabase.getInstance().getReference()
                                        .child(Constants.ARG_USERS) // Users
                                        .child(User.getUser().getUid()) // Me
                                        .child(Constants.ARG_CHAT_ROOMS) // Chat
                                        .child(chatMessegae.getReceiverUid()) // Receiver
                                        .child(String.valueOf(chatMessegae.getTimestamp())); // Chat ID

                                chat.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(myChatViewHolder.mView.getContext(), "Audio deleted", Toast.LENGTH_LONG).show();
                                    }
                                });


                            case 1:
                                dialog.dismiss();
                                myChatViewHolder.lView.setBackgroundResource(R.drawable.chat_out_bg);

                        }
                    }
                });

                // create and show the alert dialog
                AlertDialog dialog2 = builder.create();
                dialog2.show();
                return false;
            }
        });

        myChatViewHolder.mImageUrl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                myChatViewHolder.mImageUrl.setBackgroundResource(R.drawable.chat_out_bg_image_dark);

                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(myChatViewHolder.mView.getContext());
                builder.setTitle("Delete this picture ?");
                builder.setCancelable(false);
                // add a list
                String[] animals = {"Delete", "Cancel"};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:

                                myChatViewHolder.mImageUrl.setBackgroundResource(R.drawable.chat_out_bg_image_dark);

                                DatabaseReference chat = FirebaseDatabase.getInstance().getReference()
                                        .child(Constants.ARG_USERS) // Users
                                        .child(User.getUser().getUid()) // Me
                                        .child(Constants.ARG_CHAT_ROOMS) // Chat
                                        .child(chatMessegae.getReceiverUid()) // Receiver
                                        .child(String.valueOf(chatMessegae.getTimestamp())); // Chat ID

                                chat.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(myChatViewHolder.mView.getContext(), "Picture deleted", Toast.LENGTH_LONG).show();
                                    }
                                });


                            case 1:
                                dialog.dismiss();
                                myChatViewHolder.mImageUrl.setBackgroundResource(R.drawable.chat_out_bg_image);

                        }
                    }
                });

                // create and show the alert dialog
                AlertDialog dialog2 = builder.create();
                dialog2.show();
                return false;
            }
        });
*/


    }

    private void configureOtherChatViewHolder(OtherChatViewHolder otherChatViewHolder, int position) {
        Chat chat = mChats.get(position);

        if (chat.getType() == 1) {

            database = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(chat.getSenderUid());

            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    if (user.getPhotoUrl() != null) {

                        Glide.with(getApplicationContext())
                                .load(user.photoUrl)
                                .placeholder(R.drawable.profile_default_photo)
                                .dontAnimate()
                                .fitCenter()
                                .into(otherChatViewHolder.mProfilePhoto);

                    } else {

                        Glide.with(getApplicationContext())
                                .load(R.drawable.profile_default_photo)
                                .placeholder(R.drawable.profile_default_photo)
                                .dontAnimate()
                                .fitCenter()
                                .into(otherChatViewHolder.mProfilePhoto);


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            otherChatViewHolder.txtChatMessage.setText(chat.getMessage());
            DateTime senttime = new DateTime(chat.getTimestamp());

            String time = new PrettyTime().format(senttime.toDate());

            otherChatViewHolder.time.setText(time);

            // Audio Streaming Part and Listners here

        } else if (chat.getType() == 2){


            database = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(chat.getSenderUid());
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    if (user.getPhotoUrl() != null){

                        Glide.with(getApplicationContext())
                                .load(user.photoUrl)
                                .placeholder(R.drawable.profile_default_photo)
                                .dontAnimate()
                                .fitCenter()
                                .into(otherChatViewHolder.AudiomProfilePhoto);

                    } else {

                        Glide.with(getApplicationContext())
                                .load(R.drawable.profile_default_photo)
                                .placeholder(R.drawable.profile_default_photo)
                                .dontAnimate()
                                .fitCenter()
                                .into(otherChatViewHolder.AudiomProfilePhoto);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            DateTime senttime = new DateTime(chat.getTimestamp());

            String time = new PrettyTime().format( senttime.toDate());

            otherChatViewHolder.audioTime.setText(time);

            // Audio Streaming Part and Listners here

            // Audio Staffs here
            AudioWife.getInstance()
                    .init(getApplicationContext(), Uri.parse(chat.getFileurl()))
                    .setPlayView(otherChatViewHolder.mPay)
                    .setPauseView(otherChatViewHolder.mPause)
                    .setSeekBar(otherChatViewHolder.prograssbar)
                    .setRuntimeView(otherChatViewHolder.CurrentTime)
                    .setTotalTimeView(otherChatViewHolder.TotalTime);

            AudioWife.getInstance().addOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    //Toast.makeText(getBaseContext(), "Completed", Toast.LENGTH_SHORT).show();
                    // do you stuff
                    //AudioWife.getInstance().play();

                    //myChatViewHolder.mPause.setVisibility(View.GONE);
                }
            });

            AudioWife.getInstance().addOnPlayClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //Toast.makeText(getBaseContext(), "Play", Toast.LENGTH_SHORT).show();
                    // Lights-Camera-Action. Lets dance.

                    otherChatViewHolder.mPay.setVisibility(View.GONE);
                    otherChatViewHolder.mPause.setVisibility(View.VISIBLE);


                }
            });

            AudioWife.getInstance().addOnPauseClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    otherChatViewHolder.mPay.setVisibility(View.VISIBLE);
                    otherChatViewHolder.mPause.setVisibility(View.GONE);
                    //Toast.makeText(getBaseContext(), "Pause", Toast.LENGTH_SHORT).show();
                    // Your on audio pause stuff.


                }
            });

            /*otherChatViewHolder.mPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (chat.fileurl == null)
                        return;

                    //otherChatViewHolder.mPay.setImageResource(R.drawable.ic_play_white);



                }
            });*/



        } else {

           // Image Message

            database = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(chat.getSenderUid());

            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    if (user.getPhotoUrl() != null) {

                        Glide.with(getApplicationContext())
                                .load(user.photoUrl)
                                .placeholder(R.drawable.profile_default_photo)
                                .dontAnimate()
                                .fitCenter()
                                .into(otherChatViewHolder.mProfilePhotoImage);

                    } else {

                        Glide.with(getApplicationContext())
                                .load(R.drawable.profile_default_photo)
                                .placeholder(R.drawable.profile_default_photo)
                                .dontAnimate()
                                .fitCenter()
                                .into(otherChatViewHolder.mProfilePhotoImage);


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if (chat.getImageUrl() != null){

                Glide.with(getApplicationContext())
                        .load(chat.getImageUrl())
                        .placeholder(R.color.gray)
                        .error(R.color.gray)
                        .dontAnimate()
                        .centerCrop()
                        .into(otherChatViewHolder.mImageUrl);





            } else {

                Glide.with(getApplicationContext())
                        .load(R.color.gray)
                        .placeholder(R.color.gray)
                        .dontAnimate()
                        .centerCrop()
                        .into(otherChatViewHolder.mImageUrl);
            }

            DateTime senttime = new DateTime(chat.getTimestamp());
            String time = new PrettyTime().format(senttime.toDate());
            otherChatViewHolder.timeImage.setText(time);

            otherChatViewHolder.mImageUrl.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {


                    if (chat.getImageUrl() != null){

                        Intent imageViewerIntent = new Intent(getApplicationContext(), ImageViewerActivity.class);
                        imageViewerIntent.putExtra(ImageViewerActivity.EXTRA_IMAGE_URL,chat.getImageUrl());
                        imageViewerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(imageViewerIntent);


                    } else {

                        Toast.makeText(getApplicationContext(), "No Image found", Toast.LENGTH_SHORT).show();
                    }


                }
            });

        }

        /*Chat chatMessegae = mChats.get(position);
        otherChatViewHolder.txtChatMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                otherChatViewHolder.txtChatMessage.setBackgroundResource(R.drawable.chat_in_bg_dark);

                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(otherChatViewHolder.mView.getContext());
                builder.setTitle("Delete this message ?");
                builder.setCancelable(false);
                // add a list
                String[] animals = {"Delete", "Cancel"};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:

                                otherChatViewHolder.txtChatMessage.setBackgroundResource(R.drawable.chat_in_bg_dark);

                                DatabaseReference chat = FirebaseDatabase.getInstance().getReference()
                                        .child(Constants.ARG_USERS) // Users
                                        .child(User.getUser().getUid()) // Me
                                        .child(Constants.ARG_CHAT_ROOMS) // Chat
                                        .child(chatMessegae.getSenderUid()) // Receiver
                                        .child(String.valueOf(chatMessegae.getTimestamp())); // Chat ID

                                chat.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(otherChatViewHolder.mView.getContext(), "Message deleted", Toast.LENGTH_LONG).show();
                                    }
                                });


                            case 1:
                                dialog.dismiss();
                                otherChatViewHolder.txtChatMessage.setBackgroundResource(R.drawable.chat_in_bg);

                        }
                    }
                });

                // create and show the alert dialog
                AlertDialog dialog2 = builder.create();
                dialog2.show();
                return false;
            }
        });

        otherChatViewHolder.lView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                otherChatViewHolder.lView.setBackgroundResource(R.drawable.chat_in_bg_dark);

                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(otherChatViewHolder.mView.getContext());
                builder.setTitle("Delete this audio ?");
                builder.setCancelable(false);
                // add a list
                String[] animals = {"Delete", "Cancel"};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:

                                otherChatViewHolder.lView.setBackgroundResource(R.drawable.chat_in_bg_dark);

                                DatabaseReference chat = FirebaseDatabase.getInstance().getReference()
                                        .child(Constants.ARG_USERS) // Users
                                        .child(User.getUser().getUid()) // Me
                                        .child(Constants.ARG_CHAT_ROOMS) // Chat
                                        .child(chatMessegae.getSenderUid()) // Receiver
                                        .child(String.valueOf(chatMessegae.getTimestamp())); // Chat ID

                                chat.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(otherChatViewHolder.mView.getContext(), "Audio deleted", Toast.LENGTH_LONG).show();
                                    }
                                });


                            case 1:
                                dialog.dismiss();
                                otherChatViewHolder.lView.setBackgroundResource(R.drawable.chat_in_bg);

                        }
                    }
                });

                // create and show the alert dialog
                AlertDialog dialog2 = builder.create();
                dialog2.show();
                return false;
            }
        });

        otherChatViewHolder.mProfilePhotoImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                otherChatViewHolder.mProfilePhotoImage.setBackgroundResource(R.drawable.chat_in_bg_image_dark);

                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(otherChatViewHolder.mView.getContext());
                builder.setTitle("Delete this picture ?");
                builder.setCancelable(false);
                // add a list
                String[] animals = {"Delete", "Cancel"};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:

                                otherChatViewHolder.mProfilePhotoImage.setBackgroundResource(R.drawable.chat_in_bg_image_dark);

                                DatabaseReference chat = FirebaseDatabase.getInstance().getReference()
                                        .child(Constants.ARG_USERS) // Users
                                        .child(User.getUser().getUid()) // Me
                                        .child(Constants.ARG_CHAT_ROOMS) // Chat
                                        .child(chatMessegae.getSenderUid()) // Receiver
                                        .child(String.valueOf(chatMessegae.getTimestamp())); // Chat ID

                                chat.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(otherChatViewHolder.mView.getContext(), "Picture deleted", Toast.LENGTH_LONG).show();
                                    }
                                });


                            case 1:
                                dialog.dismiss();
                                otherChatViewHolder.mProfilePhotoImage.setBackgroundResource(R.drawable.chat_in_bg_image);

                        }
                    }
                });

                // create and show the alert dialog
                AlertDialog dialog2 = builder.create();
                dialog2.show();
                return false;
            }
        });*/



       // / to explicitly pause
        //AudioWife.getInstance().pause();


        // when done playing, release the resources
        //AudioWife.getInstance().release();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*@Override
    public int getItemCount() {
        if (mChats != null) {
            return mChats.size();
        }
        return 0;
    }*/

    @Override
    public int getItemCount() {
        return this.mChats.size();
    }

    @Override
    public int getItemViewType(int position) {


        if (TextUtils.equals(mChats.get(position).getSenderUid(), User.getUser().getUid()) && (mChats.get(position).getType() == 1)) {
            return VIEW_TYPE_ME;
        } else if (TextUtils.equals(mChats.get(position).getReceiverUid(), User.getUser().getUid()) && (mChats.get(position).getType() == 1)) {
            return VIEW_TYPE_OTHER;
        } else if (TextUtils.equals(mChats.get(position).getSenderUid(), User.getUser().getUid()) && (mChats.get(position).getType() == 2)){
            return AUDIO_TYPE_ME;
        } else if (TextUtils.equals(mChats.get(position).getReceiverUid(), User.getUser().getUid()) && (mChats.get(position).getType() == 2)){
            return AUDIO_TYPE_OTHER;
        } else if (TextUtils.equals(mChats.get(position).getSenderUid(), User.getUser().getUid()) && (mChats.get(position).getType() == 3)){
            return IMAGE_TYPE_ME;
        } else if (TextUtils.equals(mChats.get(position).getReceiverUid(), User.getUser().getUid()) && (mChats.get(position).getType() == 3))
            return IMAGE_TYPE_OTHER;

        return DEFAULT_TYPE;
    }

    private class MyChatViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private LinearLayout lView;
        private TextView time;
        private EmojiTextView txtChatMessage;
        private CircleImageView mProfilePhoto;
        private ImageView Status;

        private TextView audioTime, CurrentTime, TotalTime;
        private CircleImageView AudiomProfilePhoto;
        private ImageView AudioStatus, mPay, mPause;
        private SeekBar prograssbar;

        private TextView timeImage;
        private CircleImageView mProfilePhotoImage;
        private ImageView mImageUrl;
        private ImageView StatusImage;

        private MyChatViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            Status = itemView.findViewById(R.id.send_progress);
            mProfilePhoto = itemView.findViewById(R.id.user_photo);
            time = itemView.findViewById(R.id.message_time);
            txtChatMessage = itemView.findViewById(R.id.text_view_chat_message);

            audioTime = itemView.findViewById(R.id.message_time_audio);
            CurrentTime = itemView.findViewById(R.id.run_time);
            TotalTime = itemView.findViewById(R.id.total_time);
            AudiomProfilePhoto = itemView.findViewById(R.id.user_photo_audio);
            lView = itemView.findViewById(R.id.linearLayout5);

            AudioStatus = itemView.findViewById(R.id.send_progress_audio);
            mPay = itemView.findViewById(R.id.play);
            mPause = itemView.findViewById(R.id.pause);

            prograssbar = itemView.findViewById(R.id.media_seekbar);

            timeImage = itemView.findViewById(R.id.message_time_image);
            mProfilePhotoImage = itemView.findViewById(R.id.user_photo_image);
            mImageUrl = itemView.findViewById(R.id.imageUrl);
            StatusImage = itemView.findViewById(R.id.send_progress_image);

        }
    }

    private static class OtherChatViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private LinearLayout lView;
        private TextView time;
        private EmojiTextView txtChatMessage;
        private CircleImageView mProfilePhoto;

        private TextView audioTime, CurrentTime, TotalTime;
        private CircleImageView AudiomProfilePhoto;
        private ImageView mPay, mPause;
        private SeekBar prograssbar;

        private TextView timeImage;
        private CircleImageView mProfilePhotoImage;
        private ImageView mImageUrl;

        private OtherChatViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mProfilePhoto =  itemView.findViewById(R.id.user_photo);
            time =  itemView.findViewById(R.id.message_time);
            txtChatMessage =  itemView.findViewById(R.id.text_view_chat_message);

            audioTime = itemView.findViewById(R.id.message_time_audio);
            CurrentTime = itemView.findViewById(R.id.run_time);
            TotalTime = itemView.findViewById(R.id.total_time);

            lView = itemView.findViewById(R.id.linearLayout5);

            AudiomProfilePhoto = itemView.findViewById(R.id.user_photo_audio);

            mPay = itemView.findViewById(R.id.play);
            mPause = itemView.findViewById(R.id.pause);

            prograssbar = itemView.findViewById(R.id.media_seekbar);

            timeImage = itemView.findViewById(R.id.message_time_image);
            mProfilePhotoImage = itemView.findViewById(R.id.user_photo_image);
            mImageUrl = itemView.findViewById(R.id.imageUrl);
        }
    }
}
