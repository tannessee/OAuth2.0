package files;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;


import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

public class DynamicJson {
	// Parameterize the API tests with multiple data sets
	@Test(dataProvider = "BooksData")
	public void addBook(String isnb, String aisle) 
	{
		RestAssured.baseURI = "http://216.10.245.166";
		String response = given().header("Content-Type", "application/json").body(Payload.addBook(isnb, aisle))
		.when().post("Library/Addbook.php")
		.then().log().all().assertThat().statusCode(200).extract().response().asString();
		
		JsonPath js = ReusableMethods.rawToJson(response);
		String bookId = js.get("ID");
		System.out.println("Book id is : "+ bookId);

	}
	
	@DataProvider(name = "BooksData")
	public Object[][] getData() {
		
		
		return new Object[][] {{"ttt", "12345"},{"oooo", "23456"},{"ppppp", "34567"}};
	}
}
