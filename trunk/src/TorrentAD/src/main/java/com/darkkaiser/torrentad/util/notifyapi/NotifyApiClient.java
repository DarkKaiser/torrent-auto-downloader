package com.darkkaiser.torrentad.util.notifyapi;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Slf4j
public final class NotifyApiClient {

    private static String URL;
    private static String API_KEY;
    private static String APPLICATION_ID;

    public static void init(final String url, final String apiKey, final String applicationId) {
        NotifyApiClient.URL = url;
        NotifyApiClient.API_KEY = apiKey;
        NotifyApiClient.APPLICATION_ID = applicationId;
    }

    public static void sendNotifyMessage(final String message, final boolean errorOccurred) {
        try {
            final URL url = new URL(NotifyApiClient.URL);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + NotifyApiClient.API_KEY);
            conn.setRequestProperty("Cache-Control", "no-cache");

            conn.setDoOutput(true);
            @Cleanup final OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            @Cleanup final BufferedWriter bw = new BufferedWriter(osw);
            final JSONObject jsonObj = new JSONObject(new HashMap<String, Object>(){{
                put("application_id", NotifyApiClient.APPLICATION_ID);
                put("message", message);
                put("error_occurred", errorOccurred);
            }});

            bw.write(jsonObj.toString());
            bw.flush();

            final int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                log.error("NotifyAPI 서비스 호출이 실패하였습니다(HTTP 상태코드:{})", responseCode);
            }
        } catch (final IOException e) {
            log.error("NotifyAPI 서비스를 호출하는 중에 예외가 발생하였습니다.", e);
        }
    }

}
