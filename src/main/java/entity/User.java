package entity;

import java.util.Set;

public class User {
    private String userId;
    private String preferJob;
    private String preferLoc;
    private Set<Job> favoriteJobs;
    private Set<Job> appliedJobs;

    public User(String userId) {
        this.userId = userId;
    }

    public String getPreferJob() {
        return preferJob;
    }

    public void setPreferJob(String preferJob) {
        this.preferJob = preferJob;
    }

    public String getPreferLoc() {
        return preferLoc;
    }

    public void setPreferLoc(String preferLoc) {
        this.preferLoc = preferLoc;
    }

    public Set<Job> getFavoriteJobs() {
        return favoriteJobs;
    }

    public void setFavoriteJobs(Set<Job> favoriteJobs) {
        this.favoriteJobs = favoriteJobs;
    }

    public Set<Job> getAppliedJobs() {
        return appliedJobs;
    }

    public void setAppliedJobs(Set<Job> appliedJobs) {
        this.appliedJobs = appliedJobs;
    }
}
