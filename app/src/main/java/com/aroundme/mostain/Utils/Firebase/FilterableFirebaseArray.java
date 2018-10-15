

package com.aroundme.mostain.Utils.Firebase;

import com.firebase.ui.database.FirebaseArray;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;

import java.util.HashSet;
import java.util.Set;

public class FilterableFirebaseArray extends FirebaseArray {

    private Set<String> excludes = new HashSet<>();

    public FilterableFirebaseArray(Query query, Class aClass) {
        super(query, aClass);
    }

    public FilterableFirebaseArray(Query query, SnapshotParser parser) {
        super(query, parser);
    }

    public void addExclude(String key) {
        excludes.add(key);
    }

    public void removeExclude(String key) {
        excludes.remove(key);
    }

    @Override
    public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
        if (!excludes.contains(snapshot.getKey())) {
            super.onChildAdded(snapshot, excludes.contains(previousChildKey)? null : previousChildKey);
        }
    }

    @Override
    public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
        if (!excludes.contains(snapshot.getKey())) {
            super.onChildChanged(snapshot, excludes.contains(previousChildKey)? null : previousChildKey);
        }
    }

    @Override
    public void onChildMoved(DataSnapshot snapshot, String previousChildKey) {
        if (!excludes.contains(snapshot.getKey())) {
            super.onChildMoved(snapshot, excludes.contains(previousChildKey)? null : previousChildKey);
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot snapshot) {
        if (!excludes.contains(snapshot.getKey())) {
            super.onChildRemoved(snapshot);
        }
    }

}