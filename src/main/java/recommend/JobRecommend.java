package recommend;

import db.DBConnection;
import db.firebase.FirebaseConnection;
import entity.Job;
import entity.User;
import external.JobAPI;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JobRecommend {
    private static class MutableInteger implements Comparable<MutableInteger> {
        private int count = 0;

        public MutableInteger(int count) {
            this.count = count;
        }

        public void increment() {
            this.count++;
        }

        public int getCount() {
            return count;
        }

        @Override
        public int compareTo(MutableInteger o) {
            if (this.count == o.count) {
                return 0;
            }
            return this.count < o.count ? -1 : 1;
        }
    }

    private User user;
    private final List<String> stopwords;
    private final Set<String> historyJobIds;

    public JobRecommend(String userId) throws IOException {
        DBConnection firebase = new FirebaseConnection();
        historyJobIds = firebase.getHistoryJobIds(userId);
        this.user = new User(userId);

        this.user.setAppliedJobs(firebase.getUserJobs(userId, "history"));
        this.user.setFavoriteJobs(firebase.getUserJobs(userId, "favorite"));
        this.user.setPreferJob(firebase.getPreference(userId, "preferJob"));
        this.user.setPreferLoc(firebase.getPreference(userId, "preferLoc"));
        stopwords = Files.readAllLines(Paths.get(System.getProperty("user.dir") + "/english_stopwords.txt"));
    }

    public JSONArray recommend() {
        StringBuilder kwStringBuilder = new StringBuilder();
        StringBuilder locStringBuilder = new StringBuilder();
        for (Job job: this.user.getAppliedJobs()) {
            kwStringBuilder.append(job.getTitle()).append(" ");
            kwStringBuilder.append(job.getCompany()).append(" ");
            kwStringBuilder.append(job.getDescription()).append(" ");
            locStringBuilder.append(job.getLocation()).append(" ");
        }
        for (Job job: this.user.getFavoriteJobs()) {
            kwStringBuilder.append(job.getTitle()).append(" ");
            kwStringBuilder.append(job.getCompany()).append(" ");
            kwStringBuilder.append(job.getDescription()).append(" ");
            locStringBuilder.append(job.getLocation()).append(" ");
        }
        String keyword = keywordFinding(preprocess(kwStringBuilder.toString()));
        System.out.println("keyword: " + keyword);

        String location = keywordFinding(preprocess(locStringBuilder.toString()));
        System.out.println("location: " + location);

        JobAPI jobAPI = new JobAPI();
        JSONArray recommendFromJobs = jobAPI.search(keyword, location);
        System.out.println("preferJob: " + this.user.getPreferJob());
        System.out.println("preferLoc: " + this.user.getPreferLoc());
        JSONArray recommendFromPreference = jobAPI.search(this.user.getPreferJob(), this.user.getPreferLoc());
        // merge two results
        for (int i = 0; i < recommendFromJobs.length(); i++) {
            recommendFromPreference.put(recommendFromJobs.getJSONObject(i));
        }
        System.out.println("original length: " + recommendFromPreference.length());
        JSONArray recommendation = new JSONArray();
        // filter history
        for (int i = 0; i < recommendFromPreference.length(); i++) {
            JSONObject rec = recommendFromPreference.getJSONObject(i);
            String jobId = rec.getString("id");
            if (!historyJobIds.contains(jobId)) {
                recommendation.put(rec);
            }
        }
        return recommendation;
    }

    private ArrayList<String> preprocess(String input) {

        ArrayList<String> wordList = Stream.of(input.toLowerCase().split("\\W+"))
                .collect(Collectors.toCollection(ArrayList<String>::new));
        wordList.removeAll(stopwords);

        return wordList;
    }

    private String keywordFinding(ArrayList<String> input) {
        Map<String, MutableInteger> counterMap = new HashMap<>();
        for (String str: input) {
            counterMap.compute(str, (k, v) -> v == null ? new MutableInteger(0): v).increment();
        }
        Map.Entry<String, MutableInteger> maxEntry = Collections.max(counterMap.entrySet(),
                (Map.Entry<String, MutableInteger> e1, Map.Entry<String, MutableInteger> e2) -> e1.getValue()
                    .compareTo(e2.getValue()));
        System.out.println(maxEntry.getValue().getCount());
        return maxEntry.getKey();
    }
}
