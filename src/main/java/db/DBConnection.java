package db;

import entity.Job;

import java.util.Set;

public interface DBConnection {

    public Set<Job> getUserJobs(String userId, String type);

    public Set<String> getHistoryJobIds(String userId);

    public String getPreference(String userId, String type);

}
