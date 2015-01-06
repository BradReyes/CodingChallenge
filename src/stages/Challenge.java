package stages;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* Does the four stages for the coding challenge for CODE2040. 
 * Uses JSONObject from external jar file
 */
public class Challenge {

	private static String token; //for the unique token given to us

	
	/* 
	 * The following outlines four methods that helps solve the four stages
	 */
	/**
	 * Reverses a string
	 * @param input
	 * @return String
	 */
	private static String reverseString(String input) {
		return new StringBuilder(input).reverse().toString();
	}

	/**
	 * Finds the needle string in the haystack(array of strings)
	 * @param needle
	 * @param haystack
	 * @return int
	 */
	private static int findNeedle(String needle, String[] haystack) {
		int length = haystack.length;
		int index = -1; //returns -1 if not found
		for (int i = 0; i < length; i++) {
			if (needle.equals(haystack[i])) {
				index = i;
				break;
			}
		}
		return index;
	}

	/**
	 * Gives an array of the strings that do not include the given prefix
	 * @param prefix
	 * @param strings
	 * @return String[]
	 */
	private static String[] findPrefix(String prefix, String[] strings) {
		int length = strings.length;
		ArrayList<String> newStrings = new ArrayList<String>();
		for (int i = 0; i < length; i++) {
			if (!strings[i].startsWith(prefix))
				newStrings.add(strings[i]);
		}
		return newStrings.toArray(new String[newStrings.size()]);
	}

	/**
	 * Adds an interval of time in seconds to a given date in ISO 8601 format
	 * @param date
	 * @param seconds
	 * @return String
	 */
	private static String addInterval(String date, int seconds) {
		TimeZone zone = TimeZone.getTimeZone("UTC"); //for daylight savings time error
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		df.setTimeZone(zone);
		Date curDate = null;
		try {
			curDate = df.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(curDate);
		cal.add(Calendar.SECOND, seconds); //adds interval
		String formatted = df.format(cal.getTime()); //formats back to string
		return formatted;
	}


	/**
	 * Gets a JSON object with the unique token given to me 
	 * @return JSONObject
	 */
	private static JSONObject getJSONObject() {
		JSONObject o = new JSONObject();
		try {
			o.put("token", token);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return o;
	}

	/*
	 * The following are the stages for the challenge called by main
	 */
	
	/**
	 * Completes stage 1 of the challenge
	 */
	private static void stage1() {
		JSONObject o = getJSONObject(); //returns JSON obejct with token
		String jsonToReverse = sendAndGetPayload("http://challenge.code2040.org/api/getstring", o.toString());
		String toReverse = null;
		try {
			JSONObject feedback = new JSONObject(jsonToReverse);
			toReverse = feedback.getString("result"); //gets string to manipulate
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String reversed = reverseString(toReverse);

		//stored reversed string in JSON object
		try {
			o.put("string", reversed);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		sendAndGetPayload("http://challenge.code2040.org/api/validatestring", o.toString());
	}

	/**
	 * Completes stage 2 of the challenge
	 */
	private static void stage2() {
		JSONObject o = getJSONObject();
		String response = sendAndGetPayload("http://challenge.code2040.org/api/haystack", o.toString());
		String[] haystack = null;
		String needle = null;
		try {
			JSONObject resultFeedback = new JSONObject(response);
			JSONObject feedback = resultFeedback.getJSONObject("result"); //retrieves nested object
			needle = feedback.getString("needle");
			JSONArray arr = feedback.getJSONArray("haystack");
			haystack = new String[arr.length()];
			for (int i = 0; i < arr.length(); i++) {
				haystack[i] = arr.getString(i);
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}	
		int index = findNeedle(needle, haystack);
		//stores rest of info
		try {
			o.put("needle", index);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		sendAndGetPayload("http://challenge.code2040.org/api/validateneedle", o.toString());
	}

	/**
	 * Completes stage 3 of the coding challenge
	 */
	private static void stage3() {
		JSONObject o = getJSONObject();
		String response = sendAndGetPayload("http://challenge.code2040.org/api/prefix", o.toString());
		String prefix = null;
		String[] array = null;
		try {
			JSONObject resultFeedback = new JSONObject(response);
			JSONObject feedback = resultFeedback.getJSONObject("result"); //nested object
			prefix = feedback.getString("prefix");
			JSONArray arr = feedback.getJSONArray("array");
			array = new String[arr.length()];
			for (int i = 0; i < arr.length(); i++) {
				array[i] = arr.getString(i);
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String[] answers = findPrefix(prefix, array);
		try {
			o.put("array", answers);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		sendAndGetPayload("http://challenge.code2040.org/api/validateprefix", o.toString());
	}

	/**
	 * Completes stage 4 of the coding challenge
	 */
	private static void stage4() {
		JSONObject o = getJSONObject();
		String response = sendAndGetPayload("http://challenge.code2040.org/api/time", o.toString());
		String date = null;
		int seconds = 0;
		//gets data to be manipulated
		try {
			JSONObject resultFeedback = new JSONObject(response);
			JSONObject feedback = resultFeedback.getJSONObject("result");;
			date = feedback.getString("datestamp");
			seconds = feedback.getInt("interval");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String updatedDate = addInterval(date, seconds);
		try {
			o.put("datestamp", updatedDate);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		sendAndGetPayload("http://challenge.code2040.org/api/validatetime", o.toString());
	}

	/**
	 * Sends a given payload to the target URL. After that, the method retrieves
	 * the payload and returns it as a string
	 * I had a lot of help from the following source:
	 * http://stackoverflow.com/questions/1359689/how-to-send-http-request-in-java
	 * @param targetURL
	 * @param jsonPost
	 * @return String
	 */
	private static String sendAndGetPayload(String targetURL, String jsonPost) {
		System.out.println("Post:" + jsonPost); //to see progress
	    URL url;
	    HttpURLConnection connection = null;  
	    try {
	      //Creates the url connection
	      url = new URL(targetURL);
	      connection = (HttpURLConnection)url.openConnection();
	      connection.setRequestMethod("POST");
	      connection.setRequestProperty("Content-Type", 
	           "application/json");
	      connection.setRequestProperty("Content-Length", "" + jsonPost.length());
	      connection.setDoInput(true);
	      connection.setDoOutput(true);

	      //Send request
	      OutputStream wr = connection.getOutputStream();
	      wr.write(jsonPost.getBytes());
	      wr.close ();

	      //Get Response after sending
	      InputStream is = connection.getInputStream();
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	      String line;
	      StringBuffer response = new StringBuffer(); 
	      
	      boolean first = true; //so newline is not put before 
	      while((line = rd.readLine()) != null) {
	    	if (!first) response.append('\n'); //
    		first = false;
	        response.append(line);
	      }
	      rd.close();
	      System.out.println("Response: " + response.toString()); //to see results
	      return response.toString();
	    } catch (Exception e) {
	      e.printStackTrace();
	      return null;
	    } finally {
	      if(connection != null) connection.disconnect(); //so it always closes connection
	    }
	}

	/**
	 * Runs the program
	 * @param args
	 */
	public static void main(String[] args) {
		String registrationURL = "http://challenge.code2040.org/api/register";
		JSONObject reg = new JSONObject(); //JSON object for initial register
		String email = "breyes28@stanford.edu";
		String github = "https://github.com/BradReyes/CodingChallenge/blob/master/src/stages/Challenge.java";
		try {
			reg.put("email", email);
			reg.put("github", github);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String jsonToken = sendAndGetPayload(registrationURL, reg.toString());
		//the following parses the JSON and stores it in the token
		try {
			JSONObject feedback = new JSONObject(jsonToken);
			token = feedback.getString("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		stage1();
		stage2();
		stage3();
		stage4();
		//the following is to view how I did
		JSONObject o = getJSONObject();
		sendAndGetPayload("http://challenge.code2040.org/api/status", o.toString());
	}
}
