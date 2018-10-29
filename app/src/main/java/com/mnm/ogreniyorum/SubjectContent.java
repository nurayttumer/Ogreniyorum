package com.mnm.ogreniyorum;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.os.StrictMode;
import android.provider.Settings.Secure;
import android.util.Log;

import com.mnm.ogreniyorum.ItemListActivity;
import com.mnm.ogreniyorum.ObjectSubject;
import com.mnm.ogreniyorum.TableControllerSubject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Helper class for providing sample Title for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class SubjectContent {

    /**
     * An array of sample (dummy) items.
     */
    public static Context _context;
    public static final List<Subject> ITEMS = new ArrayList<Subject>();
    private static String deviceId = Secure.ANDROID_ID;
    private static String apiUrl = "http://localhost:40705";
    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Subject> ITEM_MAP = new HashMap<String, Subject>();

    private static final int COUNT = 25;

    static {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }

    public static List<Subject> getItems(Context context) {
        _context = context;
        getSubjectsFromApi();
        return ITEMS;
    }

    private static void getSubjectsFromApi() {
        List<ObjectSubject> subjects = new TableControllerSubject(_context).read();
        if(subjects.size() > 0){
            for (ObjectSubject obj : subjects) {
                Integer id = obj.id;
                Subject subject = new Subject(
                        "LOCAL" + id.toString(),
                        obj.title,
                        obj.body);
                addItem(subject);
            }
            return;
        }
        //String json = getJSON(apiUrl + "/api/subject/all", 10000, deviceId);
        String json = getJSON("https://jsonplaceholder.typicode.com/posts", 10000, deviceId);
        try {
            JSONArray jObj = new JSONArray(json);
            int objectLength = jObj.length();

            for (int i = 0; i < objectLength; i++) {
                Integer id = jObj.getJSONObject(i).getInt("id");
                Subject subject = new Subject(
                        id.toString(),
                        jObj.getJSONObject(i).getString("title"),
                        jObj.getJSONObject(i).getString("body"));
                addItem(subject);

                ObjectSubject objectSubject = new ObjectSubject();
                objectSubject.id = id;
                objectSubject.title = subject.Title;
                objectSubject.body = subject.Body;
                boolean createSuccessful = new TableControllerSubject(_context).create(objectSubject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void insert(ObjectSubject subject){
        int subjectCount = new TableControllerSubject(_context).count();
        subject.id = subjectCount + 1;
        boolean createSuccessful = new TableControllerSubject(_context).create(subject);
    }

    public static String getJSON(String url, int timeout, String deviceId) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestProperty("DEVICE_ID", deviceId);
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger("SubjectContent").log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger("SubjectContent").log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger("SubjectContent").log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

    private static void addItem(Subject item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.Id, item);
    }

    private static Subject createDummyItem(int position) {
        return new Subject(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore Body information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of Title.
     */
    public static class Subject {
        public final String Id;
        public final String Title;
        public final String Body;

        public Subject(String id, String title, String body) {
            this.Id = id;
            this.Title = title;
            this.Body = body;
        }

        @Override
        public String toString() {
            return Title;
        }
    }
}
