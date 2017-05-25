package command;

import com.cloudera.alfredo.client.AuthenticatedURL;
import com.cloudera.alfredo.client.AuthenticationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import utils.Configs;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vlad on 04.04.2017.
 */
public class CommandActions {
    public final static String COMMANDS_PATH = "/queue_commands";

    public static CloseableHttpResponse sendCommand(Command command) throws Exception {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("http://" + Configs.COMMAND_QUEUE_URL + COMMANDS_PATH);

        ObjectMapper objectMapper = new ObjectMapper();
        StringEntity payload;
        try {
            payload = new StringEntity(objectMapper.writeValueAsString(command));
        } catch (UnsupportedEncodingException | JsonProcessingException e) {
//            e.printStackTrace();
            throw new Exception("Couldn't parse command as json!");
        }
        httpPost.setEntity(payload);

        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpPost);
        } catch (IOException e) {
//            e.printStackTrace();
            throw new Exception("Couldn't send HTTP POST with command payload!");
        }

        client.close();
        return response;
    }

    public static List<Command> getCommands(String requesterDn) {
        CloseableHttpClient client = HttpClientBuilder.create().build();

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(Configs.COMMAND_QUEUE_URL).setPath(COMMANDS_PATH)
                .setParameter("requesterDn", requesterDn);
        URI uri = null;
        try {
            uri = builder.build();
        } catch (URISyntaxException e) {
            System.out.println("Wrong URI syntax at getting commands!");
            e.printStackTrace();
        }

        AuthenticatedURL.Token token = new AuthenticatedURL.Token();
        URL url = null;
        try {
            url = new URL("http://localhost:18080/queue_commands?requesterDn=noDn");
            HttpURLConnection conn = new AuthenticatedURL().openConnection(url, token);
            System.out.println(conn.getResponseMessage());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }


//        System.out.println(uri.toString());
//        HttpGet httpGet = new HttpGet(uri);
//        try {
//            CloseableHttpResponse response =  client.execute(httpGet);
//            HttpEntity entity = response.getEntity();
//
//            String responseString = EntityUtils.toString(entity, "UTF-8");
//            ObjectMapper objectMapper = new ObjectMapper();
//
//            System.out.println(responseString);
//            return Arrays.asList(objectMapper.readValue(responseString, Command[].class));
//        } catch (IOException e) {
//            System.out.println("Failed to execute HttpGet at getting commands!");
//            e.printStackTrace();
//        }

        return new ArrayList<>();
    }
}
