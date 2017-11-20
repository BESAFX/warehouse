package com.besafx.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.Future;

@Service
public class BulkSMSManager {

    private final Logger log = LoggerFactory.getLogger(BulkSMSManager.class);

    static public String stringToHex(String s) {
        char[] chars = s.toCharArray();
        String next;
        StringBuffer output = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            next = Integer.toHexString((int)chars[i]);
            // Unfortunately, toHexString doesn't pad with zeroes, so we have to.
            for (int j = 0; j < (4-next.length()); j++)  {
                output.append("0");
            }
            output.append(next);
        }
        return output.toString();
    }

    @Async("threadPoolBulkSMS")
    public Future<Boolean> send(String to, String body) throws Exception {
        try {
            // Construct data
            String data = "";
            /*
             * Note the suggested encoding for certain parameters, notably
             * the username and password.
             *
             * Please remember that 16bit support is a route-specific feature.
             * Please contact us if you need to confirm the status of a
             * particular route.
             *
             * Also, mobile handsets only implement partial support for non-
             * Latin characters in various languages and will generally only
             * support languages of the area of their distribution.
             * Please do not expect e.g. a handset sold in South America to
             * display Arabic text.
             */
            data += "username=" + URLEncoder.encode("besafx", "ISO-8859-1");
            data += "&password=" + URLEncoder.encode("Besa2000@", "ISO-8859-1");
            data += "&message=" + stringToHex(body);
            data += "&dca=16bit";
            data += "&want_report=1";
            data += "&msisdn=".concat(to);

            // Send data
            // Please see the FAQ regarding HTTPS (port 443) and HTTP (port 80/5567)
            URL url = new URL("https://bulksms.vsms.net/eapi/submission/send_sms/2/2.0");

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                // Print the response output...
                System.out.println(line);
            }
            wr.close();
            rd.close();
            return new AsyncResult<>(true);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new AsyncResult<>(false);
        }
    }
}
