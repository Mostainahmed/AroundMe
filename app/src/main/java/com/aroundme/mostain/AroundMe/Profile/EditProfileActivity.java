package com.aroundme.mostain.AroundMe.Profile;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.angopapo.aroundme2.App.BaseActivity;
import com.angopapo.aroundme2.Class.User;
import com.angopapo.aroundme2.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.ImagePickerSheetView;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.greysonparrelli.permiso.Permiso;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class EditProfileActivity extends BaseActivity {


    //private final int REQUEST_IMAGE_CAPTURE = 1;
    //private static final int REQUEST_SELECT_IMAGE = 2;

    public static final int REQUEST_CODE_CAMERA = 0012;
    public static final int REQUEST_CODE_GALLERY = 0013;


    Uri mCropImageUri;

    @BindView(R.id.input_name_first)
    EditText _nameFisrtText;
    @BindView(R.id.input_name_last)
    EditText _nameLastText;
    @BindView(R.id.input_username)
    EditText _usernameText;
    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_bio)
    EditText _bioText;
    @BindView(R.id.radio_male)
    RadioButton mGenderMale;
    @BindView(R.id.radio_femele)
    RadioButton mGenderFemale;
    @BindView(R.id.genderGroup)
    RadioGroup mGenderLayout;
    @BindView(R.id.button_change)
    Button _changeButton;
    @BindView(R.id.profilePhoto)
    CircleImageView ProfilePhoto;
    @BindView(R.id.back_image)
    ImageView _backButton;
    @BindView(R.id.done_button)
    ImageButton _doneButton;
    @BindView(R.id.birth2)
    TextView mBirth;
    @BindView(R.id.layout_signup) LinearLayout mSignUpActivity;
    protected BottomSheetLayout bottomSheetLayout;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference mFirebaseRef;

    private DatabaseReference userDB;

    String gender = null;
    String dateCheck = null;
    Intent mLoginIntent;
    Date date;
    private User mCurrentUser;
    private Dialog progressDialog;
    private int year;
    private int month;
    private int day;

    boolean hasPhoto;
    String menuTitle;
    CharSequence finalmenuTitle;


    private ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //Lấy thông tin của user về và cập nhật lên giao diện

            mCurrentUser = dataSnapshot.getValue(User.class);

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //Có lỗi xảy ra, không lấy đc dữ liệu
            Log.e(EditProfileActivity.class.getName(), "loadPost:onCancelled", databaseError.toException());
        }
    };


    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view,int mYear,int monthOfYear,int dayOfMonth) {

            year = mYear;
            month = monthOfYear;
            day = dayOfMonth;

            Calendar calendar = Calendar.getInstance();
            calendar.set(year,month,day);

            Date BirDate = calendar.getTime();

            Calendar dob = Calendar.getInstance();
            Calendar today = Calendar.getInstance();


            dob.set(year,month,day);

            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            Integer ageInt = age;
            String ageS = ageInt.toString();

            //return ageS;


            //////////////////////////////////

            date = BirDate;

            dateCheck = ("true");

            userDB.child("birthdate").setValue(date.getTime());

            Date date = new Date(BirDate.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy "); // the format of your date

            String birthday = sdf.format(date);


            String Birthdate = String.valueOf((new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year).append("")));
            userDB.child(User.setBirthday).setValue(Birthdate);

            //mBirth.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year).append(" ") + " " + "(" + ageS + " " + "year old" + ")");

            mBirth.setText(birthday + " " + "(" + ageS + " " + "year old" + ")");

            //mCurrentUser.setAge(ageInt);
            userDB.child(String.valueOf(User.setAge)).setValue(ageInt);





        }

    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_profile);

        ButterKnife.bind(this);

        Permiso.getInstance().setActivity(this);

        mLoginIntent = getIntent();

        mCurrentUser = new User();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        bottomSheetLayout = findViewById(R.id.bottomsheet);
        bottomSheetLayout.setPeekOnDismiss(true);

        menuTitle = getString(R.string.profile_photo);

        SpannableString span2 = new SpannableString(menuTitle);
        span2.setSpan(new StyleSpan(Typeface.BOLD),0,menuTitle.length(),SPAN_INCLUSIVE_INCLUSIVE); // set Style

        // let's put both spans together with a separator and all
        finalmenuTitle = TextUtils.concat(span2);

        userDB = FirebaseDatabase.getInstance().getReference("users").child(User.getUser().getUid());
        userDB.addListenerForSingleValueEvent(userListener);

        final DatabaseReference userFacebook = FirebaseDatabase.getInstance().getReference("users").child(User.getUser().getUid());
        userFacebook.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mCurrentUser = dataSnapshot.getValue(User.class);

                if (mCurrentUser != null){

                    loadProfile();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mBirth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DatePickerDialog dpd = new DatePickerDialog(EditProfileActivity.this,mDateSetListener,year,month,day);

                final Calendar calendar = Calendar.getInstance();
                final Calendar calendar2 = Calendar.getInstance();

                calendar2.add(Calendar.YEAR,-18);

                dpd.getDatePicker().setMaxDate(calendar2.getTimeInMillis());

                // Subtract 6 days from Calendar updated date
                //calendar.add(Calendar.DATE, -6);

                calendar.add(Calendar.YEAR,-65);

                // Set the Calendar new date as minimum date of date picker
                dpd.getDatePicker().setMinDate(calendar.getTimeInMillis());


                //dpd.getDatePicker().setMaxDate(new Date().getTime());

                dpd.show();
            }
        });


        mGenderMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gender = "true";
                userDB.child(User.isMaleGender).setValue(true);

            }
        });

        //mGenderFemale.setOnClickListener(view -> gender = User.GENDER_FEMALE);

        mGenderFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gender = "false";
                userDB.child(User.isMaleGender).setValue(false);

            }
        });

        _doneButton.setOnClickListener(view -> {

            String username1 = _usernameText.getText().toString().trim();
            String name = _nameFisrtText.getText().toString() + " " + _nameLastText.getText().toString();
            String firstname = _nameFisrtText.getText().toString();
            String lastname = _nameLastText.getText().toString();
            String email = _emailText.getText().toString().trim();
            String desc = _bioText.getText().toString();

            String emailFinal = email.replace(" ","").toLowerCase().trim();
            String usernameFinal = username1.replace(" ","").toLowerCase().trim();


            if (!validate()) {
                onSignupFailed();
                return;
            }

            if (isInternetAvailable()) {

                showProgressBar("Updating Profile...");

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Map<String, Object> newPost = new HashMap<>();

                                        newPost.put("username", usernameFinal);
                                        newPost.put("name", user.getDisplayName());
                                        newPost.put("firstname", firstname);
                                        newPost.put("lastname", lastname);
                                        newPost.put("email", emailFinal  );
                                        newPost.put("desc", desc);
                                        newPost.put("uid", user.getUid());

                                        userDB.updateChildren(newPost, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                                if (databaseError == null) {

                                                    dismissProgressBar();

                                                    Intent loginIntent = new Intent(EditProfileActivity.this, MyProfile.class);
                                                    EditProfileActivity.this.startActivity(loginIntent);
                                                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    EditProfileActivity.this.finish();

                                                } else {

                                                    dismissProgressBar();

                                                    Snackbar.make(mSignUpActivity, "Updating error", Snackbar.LENGTH_INDEFINITE).setAction(R.string.login_ok, new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                        }
                                                    }).setActionTextColor(Color.WHITE).show();
                                                }
                                            }
                                        });
                                    }
                                 }
                            });
                }


            } else {

                showInternetConnectionLostMessage();
            }

        });

        _changeButton.setOnClickListener(view -> {

            if (Build.VERSION.SDK_INT >= 23) {

                //showImagePicker();

                if (!hasPhoto){

                    showMenuSheet(MenuSheetView.MenuType.GRID);

                } else {

                    showMenuSheetPhoto(MenuSheetView.MenuType.GRID);
                }


            } else {

                //processUpload();

                if (!hasPhoto){

                    showMenuSheet(MenuSheetView.MenuType.GRID);

                } else {

                    showMenuSheetPhoto(MenuSheetView.MenuType.GRID);
                }


            }


        });

        ProfilePhoto.setOnClickListener(view -> {

            if (Build.VERSION.SDK_INT >= 23) {

               // showImagePicker();

                if (!hasPhoto){

                    showMenuSheet(MenuSheetView.MenuType.GRID);

                } else {

                    showMenuSheetPhoto(MenuSheetView.MenuType.GRID);
                }


            } else {

                if (!hasPhoto){

                    showMenuSheet(MenuSheetView.MenuType.GRID);

                } else {

                    showMenuSheetPhoto(MenuSheetView.MenuType.GRID);
                }

                //processUpload();
            }
        });




    }

    private void showMenuSheetPhoto(final MenuSheetView.MenuType menuType) {
        MenuSheetView menuSheetView = new MenuSheetView(EditProfileActivity.this, menuType, finalmenuTitle, new MenuSheetView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Toast.makeText(RegisterNameActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                if (bottomSheetLayout.isSheetShowing()) {
                    bottomSheetLayout.dismissSheet();
                }
                if (item.getItemId() == R.id.camera) {

                    //Toast.makeText(RegisterNameActivity.this, " Camera clicked", Toast.LENGTH_LONG).show();

                    initializeCameraIntent();
                }
                if (item.getItemId() == R.id.gallery) {

                    //Toast.makeText(RegisterNameActivity.this, " Gallery clicked", Toast.LENGTH_LONG).show();
                    processUpload();
                }
                if (item.getItemId() == R.id.remove) {

                    //Toast.makeText(RegisterNameActivity.this, " Remove clicked", Toast.LENGTH_LONG).show();
                    deletePhoto();
                }

                return true;
            }
        });
        menuSheetView.inflateMenu(R.menu.manu_photo);
        bottomSheetLayout.showWithSheetView(menuSheetView);
    }

    private void showMenuSheet(final MenuSheetView.MenuType menuType) {
        MenuSheetView menuSheetView2 =
                new MenuSheetView(EditProfileActivity.this, menuType, finalmenuTitle, new MenuSheetView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //Toast.makeText(RegisterNameActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                        if (bottomSheetLayout.isSheetShowing()) {
                            bottomSheetLayout.dismissSheet();
                        }
                        if (item.getItemId() == R.id.camera) {

                            initializeCameraIntent();

                            //Toast.makeText(RegisterNameActivity.this, " Camera clicked", Toast.LENGTH_LONG).show();
                        }
                        if (item.getItemId() == R.id.gallery) {

                            //Toast.makeText(RegisterNameActivity.this, " Gallery clicked", Toast.LENGTH_LONG).show();
                            processUpload();
                        }

                        return true;
                    }
                });
        menuSheetView2.inflateMenu(R.menu.manu_photo_2);
        bottomSheetLayout.showWithSheetView(menuSheetView2);
    }

    public void deletePhoto(){

        showProgressBar("Deleting Photo...");

        if (mCurrentUser.getPhotoUrl() != null){

            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();

            StorageReference photo = storageRef.getStorage().getReferenceFromUrl(mCurrentUser.getPhotoUrl());

            // Delete the photo
            photo.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully

                    userDB.child("photoUrl").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            if (mCurrentUser.getPhotoThumb() != null){

                                StorageReference photoThumb = storageRef.getStorage().getReferenceFromUrl(mCurrentUser.getPhotoThumb());

                                // Delete the thumb
                                photoThumb.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // File deleted successfully

                                        hasPhoto = false;

                                        userDB.child("photoThumb").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                hasPhoto = false;
                                                dismissProgressBar();
                                                //progressBar.setVisibility(View.GONE);
                                                Picasso.with(EditProfileActivity.this)
                                                        .load(R.drawable.profile_default_photo)
                                                        .placeholder(R.drawable.profile_default_photo)
                                                        .error(R.drawable.profile_default_photo)
                                                        .into(ProfilePhoto);

                                                Toast.makeText(EditProfileActivity.this, "Photo Deleted", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Uh-oh, an error occurred!
                                        hasPhoto = false;
                                        dismissProgressBar();
                                        Toast.makeText(EditProfileActivity.this, "Falied to delete a photo", Toast.LENGTH_LONG).show();
                                        //progressBar.setVisibility(View.GONE);
                                    }
                                });

                            } else {

                                hasPhoto = false;
                                dismissProgressBar();
                                Picasso.with(EditProfileActivity.this)
                                        .load(R.drawable.profile_default_photo)
                                        .placeholder(R.drawable.profile_default_photo)
                                        .error(R.drawable.profile_default_photo)
                                        .into(ProfilePhoto);
                                Toast.makeText(EditProfileActivity.this, "Photo Deleted", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                    hasPhoto = false;
                    dismissProgressBar();
                    Toast.makeText(EditProfileActivity.this, "Falied to delete a photo", Toast.LENGTH_LONG).show();
                    //progressBar.setVisibility(View.GONE);
                }
            });



        } else {

            hasPhoto = false;
            dismissProgressBar();
            Toast.makeText(EditProfileActivity.this, "No photo found", Toast.LENGTH_LONG).show();
        }

        //progressBar.setVisibility(View.VISIBLE);


    }

    public void loadProfile(){


        if (mCurrentUser.getbirthdate() != 0) {

            LocalDate birthdate = new LocalDate(mCurrentUser.getbirthdate());          //Birth date
            LocalDate now = new LocalDate();                                         //Today's date
            Period period = new Period(birthdate,now,PeriodType.yearMonthDay());

            //int ages = period.getYears();
            final Integer ageInt = period.getYears();
            final String ageS = ageInt.toString();

            dateCheck = "true";


            Date date = new Date(mCurrentUser.getbirthdate()); // *1000 is to convert seconds to milliseconds
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy "); // the format of your date

            String birthday = sdf.format(date);

            mBirth.setText(birthday + " " + "(" + String.valueOf(ageS) + " " + "year old" + ")");

            //mCurrentUser.setAge(Integer.parseInt((ageS)));

        } else {

            mBirth.setHint("01/01/1990 ? Your birthday");
        }


        // User profile info

        _nameFisrtText.setText(mCurrentUser.getFirstname() );
        _nameLastText.setText(mCurrentUser.getLastname());
        _usernameText.setText(mCurrentUser.getUsername());
        _emailText.setText(mCurrentUser.getEmail());
        _bioText.setText((mCurrentUser.getdesc()));


        if (mCurrentUser.isMale) {

            mGenderLayout.check(R.id.radio_male);

            gender = "true";

        } else if (!mCurrentUser.isMale) {

            mGenderLayout.check(R.id.radio_femele);
            gender = "false";

        } else {

            mGenderLayout.clearCheck();
        }


        _backButton.setOnClickListener(view -> {

            finish();
        });

        if (mCurrentUser.getPhotoUrl() == null) {

            hasPhoto = false;

            ProfilePhoto.setImageResource(R.drawable.profile_default_photo);

        } else {

            hasPhoto = true;

            Glide.with(getApplicationContext())
                    .load(mCurrentUser.getPhotoUrl())
                    .asBitmap()
                    .fitCenter()
                    .into(new SimpleTarget<Bitmap>(1360, 1360) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            ProfilePhoto.setImageBitmap(resource);
                        }
                    });


        }
    }

    public boolean validate() {
        boolean valid = true;

        String namefisrt = _nameFisrtText.getText().toString();
        String nameLast = _nameLastText.getText().toString();
        String username = _usernameText.getText().toString();
        String email = _emailText.getText().toString();


        if (namefisrt.isEmpty() || namefisrt.length() < 3) {
            _nameFisrtText.setError(getString(R.string.signup_name_at));
            valid = false;
        } else {
            _nameFisrtText.setError(null);
        }

        if (nameLast.isEmpty() || nameLast.length() < 3) {
            _nameLastText.setError(getString(R.string.signup_name_at));
            valid = false;
        } else {
            _nameLastText.setError(null);
        }

        if (username.isEmpty() || username.length() < 3) {
            _usernameText.setError(getString(R.string.signup_user_at));
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(getString(R.string.signup_valid_email));
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (dateCheck == null) {
            mBirth.setError(getString(R.string.signup_user_date));
            valid = false;
        } else {
            mBirth.setError(null);
        }

        if (gender == null) {

            valid = false;


            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.signup_gender))
                    .setIcon(android.R.drawable.stat_notify_error)
                    .setMessage(getString(R.string.signup_gender_explzin))
                    .setCancelable(false)
                    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,int which) {
                            // Whatever...
                        }
                    }).create().show();

        }
        //else
        //{
        //    mGenderLayout.setError(null);
        // }

        return valid;
    }

    public void onSignupFailed() {
        Toast.makeText(getApplicationContext(),R.string.login_missi,Toast.LENGTH_LONG).show();
    }

    public void showInternetConnectionLostMessage() {

        Snackbar.make(mSignUpActivity,R.string.login_no_int, Snackbar.LENGTH_SHORT).show();
    }

    public boolean isInternetAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void showProgressBar(String message) {
        progressDialog = ProgressDialog.show(this,"",message,true);
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
        options.setToolbarColor(ContextCompat.getColor(this, R.color.alizarin));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.alizarin_dark));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.alizarin));
        //options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.alizarin));


        UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName  + ".png")))
                .withAspectRatio(1, 1)
                .withOptions(options)
                .withMaxResultSize(640, 640)
                .start(EditProfileActivity.this);
    }


    private void initializeGalleryPickerIntent() {

        EasyImage.openGallery(EditProfileActivity.this, REQUEST_CODE_GALLERY);
    }

    private void initializeCameraIntent() {

        EasyImage.openCamera(EditProfileActivity.this,REQUEST_CODE_CAMERA);

    }

    public void uploadToFirebase (Uri cropImageUri){

        showProgressBar("Updating...");

        StorageReference ref = storageReference.child(User.getUser().getUid()).child("photo_full").child(UUID.randomUUID().toString());
        ref.putFile(cropImageUri)
                .addOnSuccessListener(taskSnapshot -> {

                    Uri fullUri = taskSnapshot.getDownloadUrl();

                    StorageReference ref2 = storageReference.child(User.getUser().getUid()).child("photo_thumb").child(UUID.randomUUID().toString());
                    ref2.putFile(cropImageUri)
                            .addOnSuccessListener(taskSnapshot2 -> {

                                Uri smallUri = taskSnapshot2.getDownloadUrl();

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {


                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(fullUri)
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    userDB.child("photoThumb").setValue(fullUri.toString());
                                                    userDB.child("photoUrl").setValue(smallUri.toString())
                                                            .addOnCompleteListener(task1 -> {

                                                        if(task1.isSuccessful()){

                                                            Picasso.with(EditProfileActivity.this)
                                                                    .load(fullUri.toString())
                                                                    .placeholder(R.drawable.profile_default_photo)
                                                                    .centerCrop()
                                                                    .resize(640, 640)
                                                                    .into(ProfilePhoto, new Callback() {
                                                                        @Override
                                                                        public void onSuccess() {
                                                                            dismissProgressBar();
                                                                            Toast.makeText(EditProfileActivity.this, "Photo updated", Toast.LENGTH_LONG).show();
                                                                        }

                                                                        @Override
                                                                        public void onError() {
                                                                            dismissProgressBar();
                                                                            Toast.makeText(EditProfileActivity.this, "Failed to update photo", Toast.LENGTH_LONG).show();
                                                                        }
                                                                    });

                                                            dismissProgressBar();
                                                        }
                                                    }).addOnFailureListener(e -> {

                                                        Toast.makeText(EditProfileActivity.this, "Failed to update photo", Toast.LENGTH_LONG).show();
                                                        dismissProgressBar();
                                                    });


                                                }
                                            });
                                }


                            })
                            .addOnFailureListener(e -> {
                                dismissProgressBar();
                                Toast.makeText(EditProfileActivity.this, "Failed to update photo", Toast.LENGTH_LONG).show();
                                //ProfilePhoto.setImageResource(R.drawable.profile_default_photo);
                            });

                })
                .addOnFailureListener(e -> {
                    dismissProgressBar();
                    Toast.makeText(EditProfileActivity.this, "Failed to update photo", Toast.LENGTH_LONG).show();
                    //ProfilePhoto.setImageResource(R.drawable.profile_default_photo);
                });

    }

    ///////////////////////////// Update profile begin here ////////////////////////////////

    public void showImagePicker() {
        Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
            @Override public void onPermissionResult(Permiso.ResultSet resultSet) {
                if (resultSet.areAllPermissionsGranted()) {

                    processUpload();

                } else {
                    Toast.makeText(EditProfileActivity.this, getString(R.string.msg_permission_required), Toast.LENGTH_LONG).show();
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


    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            uploadToFirebase(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {

            final Throwable cropError = UCrop.getError(data);

        }

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
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


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Permiso.getInstance().setActivity(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permiso.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults);

    }
}
