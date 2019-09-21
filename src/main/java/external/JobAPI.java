package external;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JobAPI {
    private static final String URL = "https://jobs.github.com/positions.json";

    public JSONArray search(String keyword, String location) {
        location = location.replaceAll(" ", "+");

        String query = String.format("description=%s&location=%s", keyword, location);
        String url = URL + "?" + query;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            System.out.println("response code: " + responseCode);
            if (responseCode != 200) {
                return new JSONArray();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            return new JSONArray(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }
}
