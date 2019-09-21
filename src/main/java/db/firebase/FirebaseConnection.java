package db.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import db.DBConnection;
import entity.Job;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class FirebaseConnection implements DBConnection {
    private final FirebaseDatabase database;

    public FirebaseConnection() {
        firebaseInitialize();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public Set<Job> getUserJobs(String userId, String type) {
        DatabaseReference ref = database.getReference("users/" + userId + "/" + type);
        final Set<Job> jobs = new HashSet<>();
        CountDownLatch done = new CountDownLatch(1);

        // Attach a listener to read the data
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dbJob: dataSnapshot.getChildren()) {
                    Job job = new Job();
                    job.setCompany((String) dbJob.child("company").getValue());
                    job.setTitle((String) dbJob.child("title").getValue());
                    job.setDescription((String) dbJob.child("description").getValue());
                    job.setLocation((String) dbJob.child("location").getValue());
                    jobs.add(job);
                }
                done.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        try {
            done.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //System.out.println(jobs.size());
        return jobs;
    }

    @Override
    public Set<String> getHistoryJobIds(String userId) {
        DatabaseReference ref = database.getReference("users/" + userId + "/history");
        final Set<String> jobIds = new HashSet<>();
        CountDownLatch done = new CountDownLatch(1);

        // Attach a listener to read the data
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dbJob: dataSnapshot.getChildren()) {
                    jobIds.add(dbJob.getKey());
                }
                done.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        try {
            done.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return jobIds;
    }

    @Override
    public String getPreference(String userId, final String type) {
        DatabaseReference ref = database.getReference("users/" + userId + "/profile");
        final String[] prefer = new String[1];
        CountDownLatch done = new CountDownLatch(1);

        // Attach a listener to read the data
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                prefer[0] = (String) dataSnapshot.child(type).getValue();
                done.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        try {
            done.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return prefer[0];
    }

    private void firebaseInitialize() {
        try {
            FileInputStream serviceAccount =
                    // path under your tomcat bin
                    new FileInputStream(System.getProperty("user.dir") + "/serviceAccount.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://mylinkedin-61579.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
