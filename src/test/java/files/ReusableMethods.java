package files;

import io.restassured.path.json.JsonPath;

public class ReusableMethods {

	
	public static JsonPath rawToJson(String response) {
		
		JsonPath js = new JsonPath(response); // for parsing Json
		return js;

	}
}
