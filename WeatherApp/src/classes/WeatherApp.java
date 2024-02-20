package classes;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


// retrive weather data   from API - this backend logic will fetch the latest weather
// data from the external API and return it. The GUI will display this data to the user
public class WeatherApp {
	// fetch weather data for given location
	public static JSONObject getWeatherData(String locationName) {
		//get location coordinates using the geolocation API
		JSONArray locationData = getLocationData(locationName);
		
		// extract latitude and longitude data
		JSONObject location = (JSONObject) locationData.get(0);
		double latitude = (double) location.get("latitude");
		double longitude = (double) location.get("longitude");
		
		// build API request URL with location coordinates
		String urlString = "https://api.open-meteo.com/v1/forecast?" + 
				"latitude=" + latitude + "&longitude=" + longitude + 
				"&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=Europe%2FBerlin";
		
		try {
			// call API and get response
			HttpURLConnection conn = fetchApiResponse(urlString);
			
			// check for response status
			// 200 - means that the connection was a success
			if(conn.getResponseCode() != 200) {
				System.out.println("Error: Could not connect to API");
				return null;
			}
			
			// store resulting JSON data
			StringBuilder resultJson = new StringBuilder();
			Scanner scanner = new Scanner(conn.getInputStream());
			while(scanner.hasNext()) {
				// read and store into the string builder
				resultJson.append(scanner.nextLine());
			}
			scanner.close();
			conn.disconnect();
			
			// parse through our data
			JSONParser parser = new JSONParser();
			JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));
			
			// retrive hourly data
			JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");
			JSONArray time = (JSONArray) hourly.get("time");
			int index = findIndexOfCurrentTime(time);
			
			// get temperature
			JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
			double temperature = (double) temperatureData.get(index);
			
			// get weather code
			JSONArray weathercode = (JSONArray) hourly.get("weather_code");
			String weatherCondition = convertWeatherCode((long) weathercode.get(index));
			
			// get humidity
			JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
			long humidity = (long) relativeHumidity.get(index);
			
			// get windspeed
			JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
			double windspeed = (double) windspeedData.get(index);
			
			// build the weather JSON data object that we are going to access in our frontend
			JSONObject weatherData = new JSONObject();
			weatherData.put("temperature", temperature);
			weatherData.put("weather_condition", weatherCondition);
			weatherData.put("humidity", humidity);
			weatherData.put("windspeed", windspeed);
			
			return weatherData;
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static JSONArray getLocationData(String locationName) {
		
		locationName = locationName.replaceAll(" ", "+");
		String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName + "&count=10&language=en&format=json";
		
		try {
			// call API and get a response
			HttpURLConnection conn = fetchApiResponse(urlString);
			
			// check response status
			// 200 means successful connection
			if(conn.getResponseCode() != 200) {
				System.out.println("Error: Could not connect to API");
				return null;
			}else {
				// store the API results
				StringBuilder resultJson = new StringBuilder();
				Scanner scanner = new Scanner(conn.getInputStream());
				
				//read and store the resulting JSON data into our string builder
				while(scanner.hasNext()){
					resultJson.append(scanner.nextLine());
				}
				
				scanner.close();
				conn.disconnect();
				
				//parse the JSON string into a JSON object
				JSONParser parser = new JSONParser();
				JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));
				
				//get the list of location data the API generated from the location name
				JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
				return locationData;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		//couldn't find location
		return null;
	}
	
	private static HttpURLConnection fetchApiResponse(String urlString) {
		try {
			//attempt to create connection
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			//set request method to get
			conn.setRequestMethod("GET");
			
			//connect to our API
			conn.connect();
			return conn;
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		// could not make connection
		return null;
	}
	
	private static int findIndexOfCurrentTime(JSONArray timeList) {
		String currentTime = getCurrentTime();
		
		// iterate through the time list and see which one matches our current time
		for(int i = 0; i < timeList.size(); i++) {
			String time = (String) timeList.get(i);
			if(time.equalsIgnoreCase(currentTime)) {
				return i;
			}
		}
		return 0;
	}
	
	private static String getCurrentTime() {
		// get current date and time
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
		String formattedDateTime = currentDateTime.format(formatter);
		return formattedDateTime;
	}
	
	private static String convertWeatherCode(long weathercode) {
		
		String weatherCondition = "";
		
		if(weathercode == 0L) {
			weatherCondition = "Clear";
		}else if(weathercode > 0L && weathercode <= 3L) {
			weatherCondition = "Cloudy";
		}else if((weathercode >= 51L && weathercode <= 67L) || (weathercode >= 80L && weathercode <= 99L)) {
			weatherCondition = "Rain";
		}else if(weathercode >= 71L && weathercode <= 77L) {
			weatherCondition = "Snow";
		}
		
		return weatherCondition;
	}
}
