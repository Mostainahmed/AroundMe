package com.aroundme.mostain.Class;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;


@IgnoreExtraProperties
public class User {
    public static final String Class = "users";
    public static final String LastTime = "timestamp";
    public static final String Fcm = "fcmUserDeviceId";
    public static final String geofire1 = "geofire";
    public static final String Online = "isOnline";
    public static final String isMaleGender = "isMale";
    public static final String getuid = "uid";
    public static final String setBirthday = "birthday";
    public static final String setAge = "age";
    public static final String GENDER_MALE = "true";
    public static final String GENDER_FEMALE = "false";

    public static String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }

    public static FirebaseUser getUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public String photoUrl;
    public long birthdate;
    public String name;
    public String  firstname;
    public String lastname;
    private long timestamp;
    private String photoThumb;
    private String uid;
    private String geofire;
    private int countViews;
    private long lastseenView;
    public boolean isVisitor;
    public boolean isTravel;
    private String email;
    private String username;
    private int credits;
    private String membership;
    private Double lat;
    private Double log;
    private Date lastActive;
    private String phone;
    public String fcmUserDeviceId;
    private String desc;
    private String birthday;
    private String country;
    private String actualcity;
    public String isNew;
    public String isNewPhone;
    private boolean FreeAds;
    private HashMap<String, Object> visitores;
    private HashMap<String, Object> chat;
    private HashMap<String, Object> chatlist;
    private HashMap<String, Object> connections;
    private HashMap<String, Object> timestampJoined;
    private int age;
    private int sexuality;
    private int  orientation;
    private int status;
    public boolean IsPrivate;
    public boolean privateActive;
    public boolean online;
    public boolean isMale;
    private long privateEnd;
    private long adsEnd;
    private long visitorEnd;
    private long passportEnd;
    private long vipEnd;
    private long proEnd;

    public User() {


    }

    public User(String fcmUserDeviceId,
                String username,
                long timestamp,
                String name,
                String firstname,
                String lastname,
                String email,
                String photoThum,
                String geofire,
                int credits,
                int countViews,
                long lastseenView,
                String membership,
                String photoUrl,
                String coverPhoto,
                Double lat,
                Double log,
                String desc,
                String birthday,
                String  country,
                String  actualcity,
                Date lastActive,
                String phone,
                long birthdate,
                long privateEnd,
                long adsEnd,
                long visitorEnd,
                long passportEnd,
                long vipEnd,
                long proEnd,
                String isNew,
                String isNewPhone,
                boolean FreeAds,
                int age,
                int sexuality,
                int orientation,
                int status,
                boolean online,
                boolean isMale,
                boolean IsPrivate,
                boolean privateActive,
                boolean isVip,
                boolean isVisitor,
                boolean isTravel,
                HashMap<String, Object> connections,
                HashMap<String, Object> visitores,
                HashMap<String, Object> chat,

                HashMap<String, Object> timestampJoined) {

        this.fcmUserDeviceId = fcmUserDeviceId;
        this.name = name;
        this.firstname = firstname;
        this.lastname = lastname;
        this.geofire = geofire;
        this.username = username;
        this.email = email;
        this.credits = credits;
        this.membership = membership;
        this.photoUrl = photoUrl;
        this.photoThumb = photoThum;
        this.isNewPhone = isNewPhone;
        //this.CoverPhoto = coverPhoto;
        this.lat = lat;
        this.log = log;
        this.desc = desc;
        this.birthday = birthday;
        this.country = country;
        this.actualcity = actualcity;
        this.lastActive = lastActive;
        this.age = age;
        this.phone = phone;
        this.connections = connections;
        this.chat = chat;
        this.visitores = visitores;
        this.timestampJoined = timestampJoined;
        this.sexuality = sexuality;
        this.orientation = orientation;
        this.status = status;
        this.online = online;
        this.isMale = isMale;
        this.IsPrivate = IsPrivate;
        this.privateActive = privateActive;
        this.FreeAds = FreeAds;
        this.isVisitor = isVisitor ;
        this.isTravel = isTravel;
        this.timestamp = timestamp;
        this.adsEnd = adsEnd;
        this.passportEnd = passportEnd ;
        this.privateEnd = privateEnd;
        this.visitorEnd = visitorEnd;
        this.vipEnd = vipEnd;
        this.proEnd = proEnd;
        this.isNew = isNew;
        this.countViews = countViews;
        this.lastseenView = lastseenView;
        this.birthdate = birthdate;
    }

    // Save Location
    public User(Double lat,
                Double log) {

        this.lat = lat;
        this.log = log;
    }

    public User(String name, String photoUrl, String uid) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.uid = uid;
    }

    // cards
    public User(String uid, String name, String firstname, String lastname, String photoUrl, String birthdate, String isMale) {
        this.uid = uid;
        this.name = name;
        this.firstname = firstname;
        this.lastname = lastname;
        this.photoUrl = photoUrl;
        this.birthdate = Long.parseLong(birthdate);
        this.isMale = Boolean.parseBoolean(isMale);

    }
    // cards with birthday
    public User(String uid, String name, String photoUrl, String isMale, String birthdate) {
        this.uid = uid;
        this.name = name;
        this.photoUrl = photoUrl;
        this.isMale = Boolean.parseBoolean(isMale);
        this.birthdate = Long.parseLong(birthdate);

    }

    // Signup user
    public User(String username,
                String uid,
                String name,
                String firstname,
                String lastname,
                String email,
                long birthdate,
                boolean isMale,
                boolean IsPrivate,
                int credits,
                String membership,
                String desc,
                int age,
                int sexuality,
                int orientation,
                int status) {

        this.username = username;
        this.uid = uid;
        this.credits = credits;
        this.membership = membership;
        this.desc = desc;
        this.age = age;
        this.sexuality = sexuality;
        this.orientation = orientation;
        this.status = status;
        this.name = name;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.isMale = isMale;
        this.birthdate = birthdate;
        this.IsPrivate = IsPrivate;
    }

    // Facebook user
    public User(String photoUrl,
                String photoThumb,
                String uid,
                String username,
                String name,
                String email,
                int credits,
                String membership,
                String desc,
                int age,
                int sexuality,
                int orientation,
                int status,
                boolean IsPrivate,
                String isNew) {

        this.photoUrl = photoUrl;
        this.uid = uid;
        this.username = username;
        this.credits = credits;
        this.membership = membership;
        this.desc = desc;
        this.age = age;
        this.sexuality = sexuality;
        this.orientation = orientation;
        this.status = status;
        this.name = name;
        this.email = email;
        this.isNew = isNew;
        this.photoThumb = photoThumb;
        this.IsPrivate = IsPrivate;
    }

    // Update user
    public User(String username,
                String name,
                String firstname,
                String lastname,
                String email,
                String desc)
    {

        this.username = username;
        this.name = name;
        this.email = email;
        this.desc = desc;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    // Phone Login
    public User(boolean IsPrivate,
                String uid,
                int credits,
                String membership,
                String desc,
                int age,
                int sexuality,
                int orientation,
                int status,
                String isNewPhone) {


        this.credits = credits;
        this.uid = uid;
        this.membership = membership;
        this.desc = desc;
        this.age = age;
        this.sexuality = sexuality;
        this.orientation = orientation;
        this.status = status;
        this.isNewPhone = isNewPhone;
        this.IsPrivate = IsPrivate;
    }


    // geter objects
    public String getFcmUserDeviceId() {
        return fcmUserDeviceId;
    }

    public String getName() {
        return name;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public int getCredits() {
        return credits;
    }

    public String getGeofire() {
        return geofire;
    }

    public String getMembership() {
        return membership;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String  getPhotoUrl() {
        return photoUrl;
    }

    public String  getPhotoThumb() {
        return photoThumb;
    }

    public Double getlat() {
        return lat;
    }


    public Double getlog() {
        return log;
    }

    public String getcountry() {
        return country;
    }

    public String getactualcity() {
        return actualcity;
    }

    public long getbirthdate() {

        return birthdate;
    }

    public String getbirthday() {
        return birthday;
    }

    public String getdesc() {
        return desc;
    }

    public int getAge() {
        return age;
    }

    public long getsexuality() {
        return sexuality;
    }

    public long getorientation() {
        return orientation;
    }

    public long getstatus() {
        return status;
    }

    public String getPhone() {
        return phone;
    }

    public Date getLastActive() {
        return lastActive;
    }

    public String getUid() {
        return uid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public HashMap<String, Object> getTimestampJoined() {
        return timestampJoined;
    }



    public boolean getisOnline() {
        return online;
    }

    public boolean isPrivate() {
        return IsPrivate;
    }

    public boolean getisMale() {
        return isMale;
    }

    public String getIsVip() {
        return membership;
    }

    public boolean getIsVisitor() {
        return isVisitor;
    }

    public boolean getIsTravel() {
        return isTravel;
    }


    public boolean getFreeAds() {
        return FreeAds;
    }

    public long getadsEnd() {
        return adsEnd;
    }

    public long getpassportEnd() {
        return passportEnd;
    }

    public long getprivateEnd() {
        return privateEnd;
    }

    public long getvisitorEnd() {
        return visitorEnd;
    }

    public long getvipEnd() {
        return vipEnd;
    }

    public long getproEnd() {
        return proEnd;
    }

    public HashMap<String, Object> getConnections() {
        return connections;
    }

    public void setConnections(HashMap<String, Object> connections) {
        this.connections = connections;
    }

    public HashMap<String, Object> getVisitores() {
        return visitores;
    }

    public void setVisitores(HashMap<String, Object> visitores) {
        this.visitores = visitores;
    }

    public int getCountViews() {
        return countViews;
    }

    public void setCountViews(int countViews) {
        this.countViews = countViews;
    }

    public long getLastseenView() {
        return lastseenView;
    }

    public void setLastseenView(long lastseenView) {
        this.lastseenView = lastseenView;
    }

    public String getIsNew() {
        return isNew;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }

    public String getIsNewPhone() {
        return isNewPhone;
    }

    public void setIsNewPhone(String isNewPhone) {
        this.isNewPhone = isNewPhone;
    }

    //__________________// ______________________// ____________________//

    // Setter objects
    public void setFcmUserDeviceId(String fcmUserDeviceId) {
        this.fcmUserDeviceId = fcmUserDeviceId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setMembership(String membership) {
        this.membership = membership ;
    }

    public void setGeofire(String geofire) {
        this.geofire = geofire ;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username ;
    }

    /*public void setCoverPhoto(String CoverPhoto) {
        this.CoverPhoto = CoverPhoto ;
    }*/

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl ;
    }

    public void setuid(String uid) {
        this.uid = uid ;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp ;
    }

    public void setlat(Double lat) {
        this.lat = lat ;
    }

    public void setlog(Double log) {
        this.log = log;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setActualcity(String actualcity) {
        this.actualcity = actualcity;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday ;
    }

    public void setDesc(String desc) {
        this.desc =desc ;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setSexuality(int sexuality) {
        this.sexuality = sexuality;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void setStatus(int status) {
        this.status =status ;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLastActive(Date lastActive) {
        this.lastActive =  lastActive;
    }

    public void  setIsOnline(boolean online) {
        this.online = online;
    }

    public void setIsMale(boolean isMale) {
        this.isMale = isMale;
    }

    public boolean setIsVip(boolean isVip) {
        return isVip;
    }

    public boolean setIsVisitor(boolean isVisitor) {
        return isVisitor;
    }

    public boolean setIsTravel(boolean isTravel) {
        return isTravel;
    }

    public HashMap<String, Object> getChat() {
        return chat;
    }

    public void setChat(HashMap<String, Object> chat) {
        this.chat = chat;
    }

    public boolean isPrivateActived() {
        return privateActive;
    }
}
