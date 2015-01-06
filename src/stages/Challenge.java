package stages;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
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

	// for testing
	private static void printArray(String[] array) {
		int length = array.length;
		for (int i = 0; i < length; i++) {
			System.out.println(array[i]);
		}
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
		String toReverse = "reverse";
		String reversed = reverseString(toReverse);

		// JSONObject output = new JSONObject();
		try {
			o.put("string", reversed);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println(o.toString());
		String payload = o.toString(); // use this to return
		// to output to endpoint validatestring
	}

	private static void stage2() {
		JSONObject o = getJSONObject();

		// get from http
		String[] haystack = new String[] { "one", "two", "three", "four",
				"five" };
		int index = findNeedle("two", haystack);

		try {
			o.put("needle", index);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String payload = o.toString();
		System.out.println(payload);
	}

	private static void stage3() {
		JSONObject o = getJSONObject();

		// get from http
		String prefix = "bo";
		String[] strings = new String[] { "bowl", "yes1", "bold", "bo",
				"loco2", "b", "obo", "bbo" };

		String[] answers = findPrefix(prefix, strings);
		try {
			o.put("array", answers);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// printArray(answers);
		String payload = o.toString();
		System.out.println(payload);
	}

	private static void stage4() {
		JSONObject o = getJSONObject();
		// get from http
		String date = "date";
		int seconds = 1;
		String updatedDate = addInterval(date, seconds);
		try {
			o.put("datestamp", updatedDate);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String payload = o.toString();
	}

	
	
	private static String sendAndGetPayload(String targetURL, String jsonPost) {
	    URL url;
	    HttpURLConnection connection = null;  
	    try {
	      //Create connection
	      url = new URL(targetURL);
	      connection = (HttpURLConnection)url.openConnection();
	      connection.setRequestMethod("POST");
	      connection.setRequestProperty("Content-Type", 
	           "application/json");

	      /*connection.setRequestProperty("Content-Length", "" + 
	               Integer.toString(jsonPost.getBytes().length));
	      connection.setRequestProperty("Content-Language", "en-US"); */ 

	      connection.setUseCaches (false);
	      connection.setDoInput(true);
	      connection.setDoOutput(true);

	      //Send request
	      DataOutputStream wr = new DataOutputStream (
	                  connection.getOutputStream ());
	      wr.writeBytes (jsonPost);
	      wr.flush ();
	      wr.close ();

	      //Get Response    
	      InputStream is = connection.getInputStream();
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	      String line;
	      StringBuffer response = new StringBuffer(); 
	      while((line = rd.readLine()) != null) {
	        response.append(line);
	        response.append('\n');
	      }
	      rd.close();
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
		String registrationURL = "http://www.example.com";
		JSONObject reg = new JSONObject();
		reg.put("email", "breyes28@stanford.edu");
		reg.put("github", "")
		
		System.out.println(sendAndGetPayload(registrationURL, ));

		token = "example"; // take from http
		stage1();
		stage2();
		stage3();
		// stage4();
	}
}
