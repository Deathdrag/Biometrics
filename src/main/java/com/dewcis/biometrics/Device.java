package com.dewcis.biometrics;


import com.github.sarxos.webcam.Webcam;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.apache.http.client.utils.URIBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

public class Device {
	Logger log = Logger.getLogger(Device.class.getName());

	
	String baseUrl = "http://192.168.3.195:8795/v2";

	public static String login(String apiName, String userId, String password) {
		
		String sessionId = null;
		
		JSONObject jLogin = new JSONObject();
		jLogin.put("mobile_app_version", "");
		jLogin.put("mobile_device_type", "");
		jLogin.put("mobile_os_version", "");
		jLogin.put("notification_token", "");
		jLogin.put("name", apiName);
		jLogin.put("password", password);
		jLogin.put("user_id", userId);
		
		httpClient client = new httpClient();
		String cookies = client.getCookies("http://192.168.0.128:8795/v2/login", jLogin.toString());
		
		System.out.println("BASE COOKIES : " + cookies);
		if(cookies != null) sessionId = cookies.split("=")[1].split(";")[0].trim();
		System.out.println("BASE SESSION : " + sessionId);
		
		return sessionId;
	}

	public String addUser(JSONObject jStudent,String sessionId) {
	    String userFile = "/users";
	    String uri = baseUrl + userFile;
	    httpClient post = new httpClient();
	    String results = post.post(uri,jStudent.toString(), sessionId);

	    return results;

    }

    public String scan(String deviceID,String sessionId){
    
	    String userFile = "/devices/"+deviceID+"/scan_fingerprint";

	    JSONObject jscan = new JSONObject();
	    jscan.put("enroll_quality","80");
	    jscan.put("retrieve_raw_image",true);
	    
	    String uri = baseUrl + userFile;

	    httpClient post = new httpClient();
	    String results = post.post(uri, jscan.toString(), sessionId);

	    return results;
	}

	public String[] userslist(String sessionId) throws URISyntaxException {

		String userFile = "/users";
		String url = baseUrl + userFile;

		URI uri = new URIBuilder(url)
				.addParameter("limit", "0")
				.addParameter("offset", "0")
				.build();

		httpClient get = new httpClient();
		String results = get.get(uri, sessionId);

		JSONObject jsonObject = new JSONObject(results);
		JSONArray jresponse = (JSONArray) jsonObject.get("records");

		ArrayList<Object> list = new ArrayList<Object>();

		for(int i=0; i<jresponse.length(); i++){
			list.add(""+jresponse.getJSONObject(i).getString("user_id")+"");
		}

		String[] userID = list.toArray(new String[0]);

		return userID;
	}

	public String enroll(String user_id,String sessionId,JSONObject jenroll){
		
		String userFile = "/users/"+user_id+"/fingerprint_templates";
		String contentType = "application/json";

		String uri = baseUrl + userFile;

		httpClient put = new httpClient();
		String results = put.put(uri, jenroll.toString(), sessionId,contentType);

		return results;
	}

	public String acinUser(String user_id,String sessionId,JSONObject jACINuser){

	    String userFile = "/users/"+user_id+"";
	    String contentType = "application/json";

	    String uri = baseUrl + userFile;

	    httpClient put = new httpClient();
	    String results = put.put(uri, jACINuser.toString(), sessionId,contentType);
	    
	    return results;
	}

	public String userDetails(String user_id,String sessionId) {
		String results =null;
        try {
            String userFile = "/users/"+user_id+"";
            String url = baseUrl + userFile;
            
            URI uri = new URIBuilder(url)
                    .build();
            
            httpClient get = new httpClient();
            results = get.get(uri, sessionId);
            
           
        } catch (URISyntaxException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
         return results;
	}

	public void userDelDevice(String user_id,String deviceID,String sessionId) {

		String userFile = "/devices/"+deviceID+"/users/"+user_id;
		String uri = baseUrl + userFile;

		httpClient del = new httpClient();
		del.delete(uri,sessionId);

	}

	public String deviceList(String sessionId) {
            String results =null;
            try {
                String userFile = "/devices";
                String url = baseUrl + userFile;
                
                URI uri = new URIBuilder(url)
                        .addParameter("group_id", "")
                        .addParameter("limit", "0")
                        .addParameter("offset", "0")
                        .build();
                
                httpClient get = new httpClient();
                results = get.get(uri, sessionId);
                
                
            } catch (URISyntaxException ex) {
                Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
            }
            return results;
	}

	public String eventsType(String sessionId) {
        String results=null;
        try {
            
            String userFile = "/references/event_types";
            
            String url = baseUrl + userFile;
            
            URI uri = new URIBuilder(url)
                    .build();
            
            httpClient get = new httpClient();
            results = get.get(uri, sessionId);
            
            
            
        } catch (URISyntaxException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return results;
	}

	public String mothlyLogEvent(JSONObject jEventlog,String sessionId){
	    
	    String userFile = "/monitoring/event_log/search_by_device";
            
        String uri = baseUrl + userFile;

	    httpClient post = new httpClient();
	    String results = post.post(uri, jEventlog.toString(), sessionId);

	    return results;
	}

	public String searchLogEvent(JSONObject jEventlog,String sessionId){

        String userFile = "/monitoring/event_log/search_by_device";
        String uri = baseUrl + userFile;
        
        httpClient post = new httpClient();
        String results = post.post(uri, jEventlog.toString(), sessionId);

        return results;
}

	public void addUserDevice(JSONObject jUsersID,String deviceID,String sessionId) {

		String userFile = "/devices/"+deviceID+"/users";
		String uri = baseUrl + userFile;

		httpClient post = new httpClient();
		post.post(uri, jUsersID.toString(), sessionId);

		System.out.println("BASE 2010 : " + jUsersID.toString());

	}

	public String takePhoto(String user_id){

		String encodedFile = null;

		Webcam webcam = Webcam.getDefault();

		if (webcam != null) 
		{
            try {
                BufferedImage image = webcam.getImage();
                
                base64Decoder myimage = new base64Decoder();
                ImageIO.write(image, "PNG", new File(""+myimage.results+"/images/"+user_id+".png"));
                
                File file = new File(""+myimage.results+"/images/"+user_id+".png");
                FileInputStream fileInputStreamReader = new FileInputStream(file);
                byte[] bytes = new byte[(int)file.length()];
                fileInputStreamReader.read(bytes);
                encodedFile = Base64.getEncoder().encodeToString(bytes);
            } catch (IOException ex) {
                Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
            }

		} else {
			JOptionPane.showMessageDialog(null,"No webcam detected");
		}

		return encodedFile;
	}

}
