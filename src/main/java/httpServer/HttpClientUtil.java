package httpServer;

import com.google.gson.Gson;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

public class HttpClientUtil {

    // init gson
    private static final Gson gson = new Gson();

    /**
     * Perform a GET request towards a server
     *
     * @param URL the @link{URL} to call
     * @return the response, parsed from JSON
     * @throws HueException 
     */
    public static String get(String URL)  {			//http GET
        // init
        Map<String, ?> response = new HashMap<>();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet request = new HttpGet(URL);
        String json = "ERROR";
        CloseableHttpResponse result = null;									//risposta che riceve dalla get

        try
        {									
            //this can cause HttpHostConnectException if no bridge is found
        	//(different from no lights found)
        	result = httpclient.execute(request);								//esegue la get su una URL passata per parametro 
            json = EntityUtils.toString(result.getEntity());				//la get ritorna un JSON 
            // do something useful with the response body
           
            // should be inside a finally...
            result.close();														//chiude connessioni
            httpclient.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return json;
    }

    /**
     * Perform a POST request towards a server
     *
     * @param URL         the @link{URL} to call
     * @param contentBody the content body of the request
     * @param contentType the content type of the request
     */
 // HTTP POST request
 	public static String post(String url, String json) throws Exception
 	{
 		URL obj = new URL(url);
 		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

 		//add reuqest header
 		con.setRequestMethod("POST");
 		con.setRequestProperty("Content-Type", "application/json");

 		// Send post request
 		con.setDoOutput(true);
 		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
 		wr.writeBytes(json);
 		wr.flush();
 		wr.close();

 		int responseCode = con.getResponseCode();
 		System.out.println("\nSending 'POST' request to URL : " + url);
 		System.out.println("Post parameters : " + json);
 		System.out.println("Response Code : " + responseCode);
 		System.out.println("Response msg : " + con.getResponseMessage());

 		//print result
 		return writeResp(con);

 	}
 	
 	private static String writeResp(HttpsURLConnection con) throws IOException
 	{
 		BufferedReader in = new BufferedReader(
 		        new InputStreamReader(con.getInputStream()));
 		String inputLine;
 		StringBuffer response = new StringBuffer();

 		while ((inputLine = in.readLine()) != null) {
 			response.append(inputLine);
 		}
 		in.close();
 		
 		return response.toString();
 	}

}
