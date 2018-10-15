package com.aroundme.mostain.AroundMe.Messaging.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.angopapo.aroundme2.Adapters.ChatAdapter;
import com.angopapo.aroundme2.AroundMe.Messaging.Addon.ChatContract;
import com.angopapo.aroundme2.AroundMe.Messaging.Addon.ChatPresenter;
import com.angopapo.aroundme2.AroundMe.Profile.EditProfileActivity;
import com.angopapo.aroundme2.AroundMe.Profile.UserProfile;
import com.angopapo.aroundme2.Class.Chat;
import com.angopapo.aroundme2.Class.User;
import com.angopapo.aroundme2.R;
import com.angopapo.aroundme2.Utils.Firebase.FirebaseUtil;
import com.angopapo.aroundme2.Utils.events.PushNotificationEvent;
import com.angopapo.aroundme2.Utils.service.Constants;
import com.angopapo.aroundme2.Utils.uiAudio.AudioWife;
import com.angopapo.aroundme2.Utils.uiAudio.ViewProxy;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.ImagePickerSheetView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.greysonparrelli.permiso.Permiso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.google.android.gms.internal.zzahn.runOnUiThread;


public class ChatFragment extends Fragment implements ChatContract.View {

    //private final int REQUEST_IMAGE_CAPTURE = 1;
    //private static final int REQUEST_SELECT_IMAGE = 2;

    public static final int REQUEST_CODE_CAMERA = 0012;
    public static final int REQUEST_CODE_GALLERY = 0013;


    private static final String LOG_TAG = "ChatFragment";
    private RecyclerView mRecyclerViewChat;
    private EmojiEditText chatEditText1;
    private ImageView enterChatView1 ;

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SELECT_IMAGE = 2;
    private static final int DIALOG_SEND_IMAGE = 100;
    private ProgressDialog mProgressUpload;


    protected BottomSheetLayout bottomSheetLayout;

    private DatabaseReference userDB;

    private Dialog progressDialog;

    LottieAnimationView animationView;

    private String mFileName;
    MediaRecorder mediaRecorder;

    EmojiPopup emojiPopup;

    ViewGroup rootView;

    ImageView emojiButton;
    //EmojiCompat emojiCompat;

    String mImageUrl;

    private ChatAdapter mChatRecyclerAdapter;

    private ChatPresenter mChatPresenter;

    RelativeLayout mLoading, mNomessages;
    CircleImageView otherProfile;

    ImageView PhotoButton;

    DatabaseReference database;

    // Audio
    private TextView recordTimeText;
    RelativeLayout recordLayout;
    //private ImageButton audioSendButton;
    private View recordPanel;
    private View slideText;
    private float startedDraggingX = -1;
    private float distCanMove = dp(80);
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    private Timer timer;
    AnimationDrawable animation;
    //ImageView audioRecorder;

    public static ChatFragment newInstance(String receiver,
                                           String receiverUid,
                                           String firebaseToken) {
        Bundle args = new Bundle();
        args.putString(Constants.ARG_RECEIVER, receiver);
        args.putString(Constants.ARG_RECEIVER_UID, receiverUid);
        args.putString(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across config changes.
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (emojiPopup != null) {
            emojiPopup.dismiss();
        }
        EventBus.getDefault().unregister(this);
    }

    private EditText.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press

                EditText editText = (EditText) v;

                if(v==chatEditText1)
                {
                    sendMessage(editText.getText().toString());
                }

                chatEditText1.setText("");

                return true;
            }
            return false;

        }
    };


    private ImageView.OnTouchListener clickListener = new View.OnTouchListener() {
        //@SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if (chatEditText1.getText().toString().trim().length() == 0){

                    if (Build.VERSION.SDK_INT >= 23) {

                        Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
                            @Override public void onPermissionResult(Permiso.ResultSet resultSet) {
                                if (resultSet.areAllPermissionsGranted()) {

                                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) slideText
                                                .getLayoutParams();
                                        params.leftMargin = dp(30);
                                        slideText.setLayoutParams(params);
                                        ViewProxy.setAlpha(slideText, 1);
                                        startedDraggingX = -1;
                                        // startRecording();
                                        startrecord();
                                        enterChatView1.getParent()
                                                .requestDisallowInterceptTouchEvent(true);
                                        recordPanel.setVisibility(View.VISIBLE);
                                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP
                                            || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                                        startedDraggingX = -1;
                                        stoprecord(true);
                                        // stopRecording(true);
                                    } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                                        float x = motionEvent.getX();
                                        if (x < -distCanMove) {
                                            stoprecord(false);
                                            // stopRecording(false);
                                        }
                                        x = x + ViewProxy.getX(enterChatView1);
                                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) slideText
                                                .getLayoutParams();
                                        if (startedDraggingX != -1) {
                                            float dist = (x - startedDraggingX);
                                            params.leftMargin = dp(30) + (int) dist;
                                            slideText.setLayoutParams(params);
                                            float alpha = 1.0f + dist / distCanMove;
                                            if (alpha > 1) {
                                                alpha = 1;
                                            } else if (alpha < 0) {
                                                alpha = 0;
                                            }
                                            ViewProxy.setAlpha(slideText, alpha);
                                        }
                                        if (x <= ViewProxy.getX(slideText) + slideText.getWidth()
                                                + dp(30)) {
                                            if (startedDraggingX == -1) {
                                                startedDraggingX = x;
                                                distCanMove = (recordPanel.getMeasuredWidth()
                                                        - slideText.getMeasuredWidth() - dp(48)) / 2.0f;
                                                if (distCanMove <= 0) {
                                                    distCanMove = dp(80);
                                                } else if (distCanMove > dp(80)) {
                                                    distCanMove = dp(80);
                                                }
                                            }
                                        }
                                        if (params.leftMargin > dp(30)) {
                                            params.leftMargin = dp(30);
                                            slideText.setLayoutParams(params);
                                            ViewProxy.setAlpha(slideText, 1);
                                            startedDraggingX = -1;
                                        }
                                    }

                                } else {
                                    Toast.makeText(getApplicationContext(), getString(R.string.msg_permission_required), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                                Permiso.getInstance().showRationaleInDialog(null,
                                        getString(R.string.msg_permission_required),
                                        null, callback);
                            }
                        }, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO);


                    } else {

                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) slideText
                                    .getLayoutParams();
                            params.leftMargin = dp(30);
                            slideText.setLayoutParams(params);
                            ViewProxy.setAlpha(slideText, 1);
                            startedDraggingX = -1;
                            // startRecording();
                            startrecord();
                            enterChatView1.getParent()
                                    .requestDisallowInterceptTouchEvent(true);
                            recordPanel.setVisibility(View.VISIBLE);
                        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP
                                || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                            startedDraggingX = -1;
                            stoprecord(true);
                            // stopRecording(true);
                        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                            float x = motionEvent.getX();
                            if (x < -distCanMove) {
                                stoprecord(false);
                                // stopRecording(false);
                            }
                            x = x + ViewProxy.getX(enterChatView1);
                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) slideText
                                    .getLayoutParams();
                            if (startedDraggingX != -1) {
                                float dist = (x - startedDraggingX);
                                params.leftMargin = dp(30) + (int) dist;
                                slideText.setLayoutParams(params);
                                float alpha = 1.0f + dist / distCanMove;
                                if (alpha > 1) {
                                    alpha = 1;
                                } else if (alpha < 0) {
                                    alpha = 0;
                                }
                                ViewProxy.setAlpha(slideText, alpha);
                            }
                            if (x <= ViewProxy.getX(slideText) + slideText.getWidth()
                                    + dp(30)) {
                                if (startedDraggingX == -1) {
                                    startedDraggingX = x;
                                    distCanMove = (recordPanel.getMeasuredWidth()
                                            - slideText.getMeasuredWidth() - dp(48)) / 2.0f;
                                    if (distCanMove <= 0) {
                                        distCanMove = dp(80);
                                    } else if (distCanMove > dp(80)) {
                                        distCanMove = dp(80);
                                    }
                                }
                            }
                            if (params.leftMargin > dp(30)) {
                                params.leftMargin = dp(30);
                                slideText.setLayoutParams(params);
                                ViewProxy.setAlpha(slideText, 1);
                                startedDraggingX = -1;
                            }
                        }

                    }



                view.onTouchEvent(motionEvent);

                //enterChatView1.performClick();


            } else {

                if(view==enterChatView1)
                {
                    sendMessage(chatEditText1.getText().toString());
                }

                chatEditText1.setText("");
            }

            return true;
        }

    };

    private final TextWatcher watcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (chatEditText1.getText().toString().equals("")) {

                enterChatView1.setBackgroundResource(R.drawable.rounded_textview);
                enterChatView1.setImageResource(R.drawable.ic_mic_24dp);

            } else {
                recordLayout.setVisibility(View.GONE);
                enterChatView1.setBackgroundResource(R.drawable.rounded_textview);
                enterChatView1.setImageResource(R.drawable.input_send);

            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.length()==0){

                enterChatView1.setBackgroundResource(R.drawable.rounded_textview);
                enterChatView1.setImageResource(R.drawable.ic_mic_24dp);
            }else{
                recordLayout.setVisibility(View.GONE);
                enterChatView1.setBackgroundResource(R.drawable.rounded_textview);
                enterChatView1.setImageResource(R.drawable.input_send);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_chat, container, false);
        bindViews(fragmentView);
        return fragmentView;
    }

    private void bindViews(View view) {
        LinearLayout linearLayout = view.findViewById(R.id.bottomlayout);
        bottomSheetLayout =  view.findViewById(R.id.bottomsheet);
        bottomSheetLayout.setPeekOnDismiss(true);

        PhotoButton =  view.findViewById(R.id.PhotoButton);

        mRecyclerViewChat =  view.findViewById(R.id.recycler_view_chat);

        otherProfile =  view.findViewById(R.id.imageView5);
        mLoading =  view.findViewById(R.id.prograss_layout);
        mNomessages =  view.findViewById(R.id.no_message_layout);

        chatEditText1 =  view.findViewById(R.id.edit_text_message);
        enterChatView1 =  view.findViewById(R.id.enter_chat1);

        emojiButton =  view.findViewById(R.id.emojiButton);

        animationView =  view.findViewById(R.id.animation_progress);

        animationView.setVisibility(View.GONE);

        rootView = view.findViewById(R.id.chat_layout);

        setUpEmojiPopup();

        Permiso.getInstance().setActivity(getActivity());

        //mediaRecorder = new MediaRecorder();

        emojiButton.setColorFilter(ContextCompat.getColor(getActivity(), R.color.emoji_icons), PorterDuff.Mode.SRC_IN);

        // Audio

        recordPanel = view.findViewById(R.id.record_panel);
        recordTimeText =  view.findViewById(R.id.recording_time_text);
        slideText = view.findViewById(R.id.slideText);
        TextView textView =  view.findViewById(R.id.slideToCancelTextView);
        recordLayout = view.findViewById(R.id.recordLayout);
        textView.setText("Slide to cancel");

        recordLayout.setVisibility(View.GONE);


        PhotoButton.setOnClickListener(View -> {



            if (Build.VERSION.SDK_INT >= 23) {

                showImagePicker();


            } else {

                processUpload();
            }
        });

        DatabaseReference blocked = FirebaseDatabase.getInstance().getReference()
                .child(Constants.ARG_USERS_BLOCK)
                .child(User.getUser().getUid());

        blocked.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String reportedUid = getArguments().getString(Constants.ARG_RECEIVER_UID);

                assert reportedUid != null;
                if (dataSnapshot.hasChild(reportedUid)){

                    emojiButton.setEnabled(false);
                    enterChatView1.setEnabled(false);
                    enterChatView1.setBackgroundResource(R.drawable.rounded_textview_gray);
                    PhotoButton.setEnabled(false);
                    chatEditText1.setEnabled(false);
                    chatEditText1.setText("Unblock to chat");
                    chatEditText1.setTextColor(Color.RED);

                } else {

                    emojiButton.setEnabled(true);
                    enterChatView1.setEnabled(true);
                    enterChatView1.setBackgroundResource(R.drawable.rounded_textview);
                    PhotoButton.setEnabled(true);
                    chatEditText1.setEnabled(true);
                    chatEditText1.setText("");
                    chatEditText1.setHint("Write here...");
                    chatEditText1.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        String reportedUid = getArguments().getString(Constants.ARG_RECEIVER_UID);

        assert reportedUid != null;
        DatabaseReference blockedUSer = FirebaseDatabase.getInstance().getReference()
                .child(Constants.ARG_USERS_BLOCK)
                .child(reportedUid);

        blockedUSer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.hasChild(User.getUser().getUid())){

                    enterChatView1.setEnabled(false);
                    enterChatView1.setBackgroundResource(R.drawable.rounded_mic_gray);
                    PhotoButton.setEnabled(false);
                    chatEditText1.setEnabled(false);
                    chatEditText1.setText("You can't chat");
                    chatEditText1.setTextColor(Color.RED);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        checkMessages();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewChat.setLayoutManager(layoutManager);
        mRecyclerViewChat.setHasFixedSize(true);
        mRecyclerViewChat.setBackgroundResource(R.color.white);
        mRecyclerViewChat.setBackgroundColor(Color.WHITE);

        //layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mRecyclerViewChat.setLayoutManager(layoutManager);


    }

    private void init() {

        mLoading.setVisibility(View.GONE);
        mNomessages.setVisibility(View.GONE);

        mChatPresenter = new ChatPresenter(this);
        mChatPresenter.getMessage(User.getUser().getUid(), getArguments().getString(Constants.ARG_RECEIVER_UID));

        // Hide the emoji on click of edit text
        chatEditText1.setOnClickListener(view -> {

            if (emojiPopup.isShowing()){
                emojiPopup.dismiss();

            }
        });

        emojiButton.setOnClickListener(v -> emojiPopup.toggle());

        chatEditText1.setOnKeyListener(keyListener);

        enterChatView1.setOnTouchListener(clickListener);


        chatEditText1.addTextChangedListener(watcher1);
    }

    public void checkMessages(){

        String receiverUid = getArguments().getString(Constants.ARG_RECEIVER_UID);

        assert receiverUid != null;
        database = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(receiverUid);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if (user != null && user.getPhotoUrl() != null){

                    String photo = user.getPhotoUrl();

                    if (!photo.isEmpty()){

                        Glide.with(getApplicationContext())
                                .load(user.photoUrl)
                                .placeholder(R.drawable.profile_default_photo)
                                .dontAnimate()
                                .fitCenter()
                                .into(otherProfile);

                    } else {


                        Glide.with(getApplicationContext())
                                .load(R.drawable.profile_default_photo)
                                .placeholder(R.drawable.profile_default_photo)
                                .dontAnimate()
                                .fitCenter()
                                .into(otherProfile);

                    }

                } else {

                    Glide.with(getApplicationContext())
                            .load(R.drawable.profile_default_photo)
                            .placeholder(R.drawable.profile_default_photo)
                            .dontAnimate()
                            .fitCenter()
                            .into(otherProfile);

                }
                // Error here
                /*
                E/UncaughtException: java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String com.angopapo.aroundme2.Class.User.getPhotoUrl()' on a null object reference
                                                                               at com.angopapo.aroundme2.AroundMe.Messaging.Fragment.ChatFragment$4.onDataChange(ChatFragment.java:621)
                 */

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String message) {

        if (message.trim().length() ==0){

            recordLayout.setVisibility(View.VISIBLE);

        } else {

            recordLayout.setVisibility(View.GONE);
            String receiver = getArguments().getString(Constants.ARG_RECEIVER);
            String receiverUid = getArguments().getString(Constants.ARG_RECEIVER_UID);
            String sender = User.getUser().getDisplayName();
            String senderUid = User.getUser().getUid();
            String receiverFirebaseToken = getArguments().getString(Constants.ARG_FIREBASE_TOKEN);
            Chat chat = new Chat(
                    1, sender, // Type for text messages
                    receiver,
                    senderUid,
                    receiverUid,
                    message,
                    System.currentTimeMillis(),
                    false);

            mChatPresenter.sendMessage(getActivity().getApplicationContext(), chat, receiverFirebaseToken);
        }


    }

    private void sendAudio(String audioFileUrl) {

        /*if(message.trim().length()==0)
            return;*/

        if (audioFileUrl.trim().length() ==0){

            recordLayout.setVisibility(View.VISIBLE);

        } else {

            recordLayout.setVisibility(View.GONE);

            //String message = mETxtMessage.getText().toString();
            String receiver = getArguments().getString(Constants.ARG_RECEIVER);
            String receiverUid = getArguments().getString(Constants.ARG_RECEIVER_UID);
            String sender = User.getUser().getDisplayName();
            String senderUid = User.getUser().getUid();
            String receiverFirebaseToken = getArguments().getString(Constants.ARG_FIREBASE_TOKEN);
            Chat chat = new Chat(
                    2, // Type for audio messages
                    sender,
                    receiver,
                    senderUid,
                    receiverUid,
                    System.currentTimeMillis(),
                    false,
                    audioFileUrl);

            mChatPresenter.sendMessage(getActivity().getApplicationContext(), chat, receiverFirebaseToken);
        }


    }

    private void sendImage(String imageUrl) {

        /*if(message.trim().length()==0)
            return;*/

        mImageUrl = imageUrl;

        if (mImageUrl != null){

            recordLayout.setVisibility(View.GONE);

            //String message = mETxtMessage.getText().toString();
            String receiver = getArguments().getString(Constants.ARG_RECEIVER);
            String receiverUid = getArguments().getString(Constants.ARG_RECEIVER_UID);
            String sender = User.getUser().getDisplayName();
            String senderUid = User.getUser().getUid();
            String receiverFirebaseToken = getArguments().getString(Constants.ARG_FIREBASE_TOKEN);
            Chat chat = new Chat(
                    3, // Type for image messages
                    sender,
                    receiver,
                    senderUid,
                    receiverUid,
                    System.currentTimeMillis(),
                    imageUrl,
                    false
                    );

            mChatPresenter.sendMessage(getActivity().getApplicationContext(), chat, receiverFirebaseToken);
        }

    }

    public void UploadAudio(){

        // File or Blob
        Uri file = Uri.fromFile(new File(mFileName));

        FirebaseStorage storageRef = FirebaseStorage.getInstance();
        StorageReference photoRef = storageRef.getReferenceFromUrl("gs://" + getString(R.string.google_storage_bucket));


        Long timestamp = System.currentTimeMillis();
        final StorageReference audio = photoRef.child(FirebaseUtil.getCurrentUserId()).child("voice").child(timestamp.toString()).child(file.getLastPathSegment());


        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/mpeg")
                .build();

        UploadTask uploadTask;

        // Upload file and metadata to the path 'audio/audio.mp3'
        uploadTask = audio.putFile(file, metadata);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //System.out.println("Upload is " + progress + "% done");

                animationView.setVisibility(View.VISIBLE);
                animationView.setAnimation("preloader.json");
                animationView.loop(true);
                //animationView.setProgress(0.5f);
                animationView.playAnimation();
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                //System.out.println("Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                //animationView.cancelAnimation();
                //animationView.setVisibility(View.GONE);
                sendAudio(String.valueOf(downloadUrl));
            }
        });
    }



    @Override
    public void onSendMessageSuccess() {

        chatEditText1.setText("");
        recordLayout.setVisibility(View.GONE);
        animationView.setVisibility(View.GONE);
    }

    @Override
    public void onSendMessageFailure(String message) {
        recordLayout.setVisibility(View.GONE);
        //Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateMessageSuccess() {

        mChatRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetMessagesSuccess(Chat chat) {

        if (mChatRecyclerAdapter == null) {
            mChatRecyclerAdapter = new ChatAdapter(new ArrayList<>());
            mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);
        }

        mChatRecyclerAdapter.add(chat);
        mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);

        if (mChatRecyclerAdapter.getItemCount() == 0){

            mLoading.setVisibility(View.GONE);
            mNomessages.setVisibility(View.VISIBLE);
        }

        if (mChatRecyclerAdapter.getItemCount() > 0){

            mLoading.setVisibility(View.GONE);
            mNomessages.setVisibility(View.GONE);

        }
    }

    @Override
    public void onGetMessagesFailure(String message) {
        mLoading.setVisibility(View.GONE);
        mNomessages.setVisibility(View.VISIBLE);
        recordLayout.setVisibility(View.GONE);
    }

    @Subscribe
    public void onPushNotificationEvent(PushNotificationEvent pushNotificationEvent) {
        if (mChatRecyclerAdapter == null || mChatRecyclerAdapter.getItemCount() == 0) {
            mChatPresenter.getMessage(User.getUser().getUid(), pushNotificationEvent.getUid());
        }
    }

    private void setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView)
                .setOnEmojiBackspaceClickListener(v -> Log.d(TAG, "Clicked on Backspace"))
                .setOnEmojiClickListener((imageView, emoji) -> Log.d(TAG, "Clicked on emoji"))
                .setOnEmojiPopupShownListener(() -> emojiButton.setImageResource(R.drawable.ic_keyboard))
                .setOnSoftKeyboardOpenListener(keyBoardHeight -> Log.d(TAG, "Opened soft keyboard"))
                .setOnEmojiPopupDismissListener(() -> {
                    emojiButton.setImageResource(R.drawable.ic_emoticon);
                })
                .setOnSoftKeyboardCloseListener(() -> Log.d(TAG, "Closed soft keyboard"))
                .build(chatEditText1);
    }



    private void startrecord() {

        // File path of recorded audio

        mediaRecorder = new MediaRecorder();

        // Verify that the device has a mic first
        PackageManager pmanager = getActivity().getPackageManager();
        if (pmanager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {

            //mediaRecorder = new MediaRecorder();

            // Set the file location for the audio
            mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFileName += "/audiorecord.3gp";
            // Create the recorder

            // Set the audio format and encoder
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            // Setup the output location
            mediaRecorder.setOutputFile(mFileName);


            // Start the recording
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }
        } else { // no mic on device
            Toast.makeText(getActivity(), "This device doesn't have a mic!", Toast.LENGTH_LONG).show();
        }

        recordLayout.setVisibility(View.VISIBLE);
        enterChatView1.setBackgroundResource(R.drawable.rounded_mic_gray);
        // TODO Auto-generated method stub
        startTime = SystemClock.uptimeMillis();
        timer = new Timer();
        MyTimerTask myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 1000, 1000);
        vibrate();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
        layoutParams.setMargins(10,0,0,0);
        enterChatView1.setLayoutParams(layoutParams);


        // Relative layout
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int right = (int) (85 * scale + 0.5f);
        int top = (int) (15 * scale + 0.5f);
        int widht = (int) (30 * scale + 0.5f);

        RelativeLayout.LayoutParams rel_btn = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        rel_btn.setMargins(25,top,right,25);
        //rel_btn.addRule(RelativeLayout.BELOW);
        rel_btn.addRule(RelativeLayout.BELOW, R.id.recycler_view_chat);
        rel_btn.addRule(RelativeLayout.ALIGN_BOTTOM);
        recordLayout.setLayoutParams(rel_btn);
        //recordLayout.setScrollingEnabled(false);

        /*RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(0, 0);
        layoutParams2.setMargins(0,0,0,0);
        recordLayout.setLayoutParams(layoutParams2);*/
    }

    private void stoprecord(boolean upload) {
        // TODO Auto-generated method stub

        if (upload){

            if (timer != null) {
                timer.cancel();
            }

            if (recordTimeText.getText().toString().equals("00:00")) {

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
                layoutParams.setMargins(0,0,0,0);
                enterChatView1.setLayoutParams(layoutParams);
                enterChatView1.setBackgroundResource(R.drawable.rounded_textview);
                recordLayout.setVisibility(View.GONE);

                if (mediaRecorder != null){

                    mediaRecorder.reset();    // set state to idle
                    mediaRecorder.release();  // release resources back to the system
                    mediaRecorder = null;

                }

                return;

            } else {

                mediaRecorder.reset();    // set state to idle
                mediaRecorder.release();  // release resources back to the system
                mediaRecorder = null;



                //animationView.cancelAnimation();
                //animationView.setVisibility(View.GONE);

                //animation.stop();
                UploadAudio();
            }

        } else {

            animationView.setVisibility(View.GONE);
        }


        recordTimeText.setText("00:00");
        vibrate();
        enterChatView1.setBackgroundResource(R.drawable.rounded_textview);
        recordLayout.setVisibility(View.GONE);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
        layoutParams.setMargins(0,0,0,0);
        enterChatView1.setLayoutParams(layoutParams);
    }


    private void vibrate() {
        // TODO Auto-generated method stub
        try {
            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int dp(float value) {
        return (int) Math.ceil(1 * value);
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            @SuppressLint("DefaultLocale") final String hms = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(updatedTime)
                            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                            .toHours(updatedTime)),
                    TimeUnit.MILLISECONDS.toSeconds(updatedTime)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                            .toMinutes(updatedTime)));
            long lastsec = TimeUnit.MILLISECONDS.toSeconds(updatedTime)
                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                    .toMinutes(updatedTime));
            System.out.println(lastsec + " hms " + hms);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (recordTimeText != null)
                            recordTimeText.setText(hms);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Permiso.getInstance().setActivity(getActivity());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permiso.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDestroy() {
        // store the data in the fragment

        AudioWife.getInstance().release();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void showProgressBar(String message) {
        progressDialog = ProgressDialog.show(getActivity(),"",message,true);
    }

    public void dismissProgressBar() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    ///////////////////////////// New Image Upload progress ////////////////////////////////


    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = "image";
        UCrop.Options options = new UCrop.Options();

        /*
        If you want to configure how gestures work for all UCropActivity tabs
        * */

        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);

        /*
        This sets max size for bitmap that will be decoded from source Uri.
        More size - more memory allocation, default implementation uses screen diagonal.
        * */

        options.setMaxBitmapSize(640);

        // Color palette
        options.setToolbarColor(ContextCompat.getColor(getActivity(), R.color.alizarin));
        options.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.alizarin_dark));
        options.setActiveWidgetColor(ContextCompat.getColor(getActivity(), R.color.alizarin));
        //options.setToolbarWidgetColor(ContextCompat.getColor(getActivity(), R.color.alizarin));


        UCrop.of(uri, Uri.fromFile(new File(getActivity().getCacheDir(), destinationFileName  + ".png")))
                .withOptions(options)
                .withMaxResultSize(640, 640)
                .start(getActivity(), ChatFragment.this);
    }

    private void initializeGalleryPickerIntent() {

        EasyImage.openGallery(ChatFragment.this, REQUEST_CODE_GALLERY);

    }

    private void initializeCameraIntent() {

        EasyImage.openCamera(ChatFragment.this, REQUEST_CODE_CAMERA);

    }

    ///////////////////////////// Update profile begin here ////////////////////////////////


    public void showImagePicker() {
        Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
            @Override public void onPermissionResult(Permiso.ResultSet resultSet) {
                if (resultSet.areAllPermissionsGranted()) {

                    processUpload();

                } else {
                    Toast.makeText(getActivity(), getString(R.string.msg_permission_required), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                Permiso.getInstance().showRationaleInDialog(null,
                        getString(R.string.msg_permission_required),
                        null, callback);
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
    }

    public void processUpload(){

        ImagePickerSheetView imagePickerSheetView = new ImagePickerSheetView.Builder(getApplicationContext())
                .setMaxItems(30)
                .setShowCameraOption(true)
                .setShowPickerOption(true)
                .setTitle(getString(R.string.dialog_title_select_image))
                .setOnTileSelectedListener(selectedTile -> {
                    if (selectedTile.isCameraTile()) {
                        initializeCameraIntent();
                    } else if (selectedTile.isPickerTile()) {
                        initializeGalleryPickerIntent();
                    } else if (selectedTile.isImageTile()) {
                        if (selectedTile.getImageUri() != null) {

                            startCropActivity(selectedTile.getImageUri());
                        }
                    }

                    bottomSheetLayout.dismissSheet();
                })
                .setImageProvider((imageView, imageUri, size) -> Glide.with(getApplicationContext())
                        .load(imageUri)
                        .asBitmap()
                        .fitCenter()
                        .into(new SimpleTarget<Bitmap>(480, 480) {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                imageView.setImageBitmap(resource);
                            }
                        }))
                .create();

        bottomSheetLayout.showWithSheetView(imagePickerSheetView);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            uploadToFirebase(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {

            final Throwable cropError = UCrop.getError(data);

        }

        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                Uri imageUri = Uri.fromFile(new File(imageFile.getAbsolutePath()));
                switch (type){
                    case REQUEST_CODE_CAMERA:

                        startCropActivity(imageUri);

                        break;
                    case REQUEST_CODE_GALLERY:

                        startCropActivity(imageUri);

                        break;

                }
            }
        });
    }

    public void uploadToFirebase (Uri cropImageUri){

        //Toast.makeText(getActivity(), "Uri received", Toast.LENGTH_LONG).show();

        userDB = FirebaseDatabase.getInstance().getReference("users").child(User.getUser().getUid());
        FirebaseStorage storageRef = FirebaseStorage.getInstance();
        StorageReference photoRef = storageRef.getReferenceFromUrl("gs://" + getString(R.string.google_storage_bucket));


        Long timestamp = System.currentTimeMillis();
        final StorageReference fullSizeRef = photoRef.child(User.getUser().getUid()).child("image_full").child(timestamp.toString() + ".jpg");

        fullSizeRef.putFile(cropImageUri).addOnSuccessListener(taskSnapshot -> {
            final Uri fullSizeUrl = taskSnapshot.getDownloadUrl();

            sendImage(String.valueOf(fullSizeUrl));


        }).addOnProgressListener(taskSnapshot -> {
            //calculating progress percentage
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

            animationView.setVisibility(View.VISIBLE);
            animationView.setAnimation("preloader.json");
            animationView.loop(true);
            //animationView.setProgress(0.5f);
            animationView.playAnimation();
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "Failed to upload chat image to database.", Toast.LENGTH_LONG).show();
            FirebaseCrash.logcat(Log.ERROR, TAG, "Failed to upload chat image to database.");
            FirebaseCrash.report(e);

            animationView.setVisibility(View.GONE);
        });

    }


}
