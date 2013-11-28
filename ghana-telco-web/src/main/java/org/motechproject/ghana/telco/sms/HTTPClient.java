package org.motechproject.ghana.telco.sms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.util.DateUtil;


public class HTTPClient {

    private final static Logger log = Logger.getLogger(HTTPClient.class);

    public void SendForFree(String mobileNumber, String message)
    {
        Send("freeservice", mobileNumber, message);
    }

    public void SendAtFee(String mobileNumber, String message)
    {
        Send("paidservice", mobileNumber, message);
    }

    private void Send(String type, String mobileNumber, String message)
    {
            try {
                message = message.replace(" ","+");
                message = message.replace("&","%26");
                String url = "http://localhost:13004/cgi-bin/sendsms?username=motech&password=motech&smsc="+type+
                             "&to="+mobileNumber + "&text=" + message;

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet getRequest = new HttpGet(url);
                HttpResponse response = httpClient.execute(getRequest);

                //if (response.getStatusLine().getStatusCode() != 200) {
                //    throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
               //}

                BufferedReader br = new BufferedReader(
                        new InputStreamReader((response.getEntity().getContent())));

                String output;
                String outln = "";
                while ((output = br.readLine()) != null) {
                    outln += output;
                }

                DateTime now = DateUtil.now();
                log.info("Subscriber: " + mobileNumber + ":" + message + " : @" + now + " > "+ outln);

                httpClient.getConnectionManager().shutdown();

            } catch (ClientProtocolException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();
            }
    }
}
