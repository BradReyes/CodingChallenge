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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Challenge {

	private static String token;

	// Stage 1
	private static String reverseString(String input) {
		return new StringBuilder(input).reverse().toString();
	}

	// Stage 2
	private static int findNeedle(String needle, String[] haystack) {
		int length = haystack.length;
		int index = -1;
		for (int i = 0; i < length; i++) {
			if (needle.equals(haystack[i])) {
				index = i;
				break;
			}
		}
		// JSONObject o;
		// o
		return index;
	}

	// Stage 3
	private static String[] findPrefix(String prefix, String[] strings) {
		int length = strings.length;
		ArrayList<String> newStrings = new ArrayList<String>();
		for (int i = 0; i < length; i++) {
			if (!strings[i].startsWith(prefix))
				newStrings.add(strings[i]);
		}
		return newStrings.toArray(new String[newStrings.size()]);
	}

	// Stage 4
	private static String addInterval(String date, int seconds) {

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
		//2015-01-06T03:51:57+00:00
		Date curDate = null; // check this if error
		try {
			curDate = df.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(curDate);
		cal.add(Calendar.SECOND, seconds);

		String formatted = df.format(cal.getTime());

		return formatted;
	}


	private static JSONObject getJSONObject() {
		JSONObject o = new JSONObject();
		try {
			o.put("token", token);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return o;
	}

	private static void stage1() {
		JSONObject o = getJSONObject();
		// to getstring endpoint
		String jsonToReverse = sendAndGetPayload("http://challenge.code2040.org/api/getstring", o.toString());
		String toReverse = null;
		try {
			JSONObject feedback = new JSONObject(jsonToReverse);
			toReverse = feedback.getString("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String reversed = reverseString(toReverse);

		try {
			o.put("string", reversed);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		sendAndGetPayload("http://challenge.code2040.org/api/validatestring", o.toString());
	}

	private static void stage2() {
		JSONObject o = getJSONObject();

		// get from http
		String response = sendAndGetPayload("http://challenge.code2040.org/api/haystack", o.toString());
		String[] haystack = null;
		String needle = null;
		try {
			JSONObject resultFeedback = new JSONObject(response);
			JSONObject feedback = resultFeedback.getJSONObject("result");
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
		try {
			o.put("needle", index);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		sendAndGetPayload("http://challenge.code2040.org/api/validateneedle", o.toString());
	}

	private static void stage3() {
		JSONObject o = getJSONObject();
		String response = sendAndGetPayload("http://challenge.code2040.org/api/prefix", o.toString());
		String prefix = null;
		String[] array = null;
		try {
			JSONObject resultFeedback = new JSONObject(response);
			JSONObject feedback = resultFeedback.getJSONObject("result");;
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

	private static void stage4() {
		JSONObject o = getJSONObject();
		String response = sendAndGetPayload("http://challenge.code2040.org/api/time", o.toString());
		String date = null;
		int seconds = 0;
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

	
	
	private static String sendAndGetPayload(String targetURL, String jsonPost) {
		System.out.println("Post:" + jsonPost);
	    URL url;
	    HttpURLConnection connection = null;  
	    try {
	      //Create connection
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

	      //Get Response   
	      InputStream is = connection.getInputStream();
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	      String line;
	      StringBuffer response = new StringBuffer(); 
	      
	      boolean first = true;
	      while((line = rd.readLine()) != null) {
	    	if (!first) response.append('\n');
    		first = false;
	        response.append(line);
	      }
	      
	      rd.close();
	      System.out.println("Response: " + response.toString());
	      return response.toString();
	      
	    } catch (Exception e) {
	      e.printStackTrace();
	      return null;

	    } finally {

	      if(connection != null) {
	        connection.disconnect(); 
	      }
	    }
	}


	public static void main(String[] args) {
		String registrationURL = "http://challenge.code2040.org/api/register";
		JSONObject reg = new JSONObject();
		
		String email = "breyes28@stanford.edu";
		String github = "https://github.com/BradReyes/CodingChallenge/blob/master/src/stages/Challenge.java";
		try {
			reg.put("email", email);
			reg.put("github", github);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String jsonToken = sendAndGetPayload(registrationURL, reg.toString());
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
		JSONObject o = getJSONObject();
		String grade = sendAndGetPayload("http://challenge.code2040.org/api/status", o.toString());
		try {
			JSONObject resultFeedback = new JSONObject(grade);
			grade = resultFeedback.getString("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println(grade);
		
	}
}
