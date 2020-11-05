import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import pojo.Api;
import pojo.GetCourse;
import pojo.WebAutomation;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import files.Payload;
import files.ReusableMethods;

public class oAuthTest {

	public static void main(String[] args) throws InterruptedException, IOException, URISyntaxException {
		// TODO Auto-generated method stub

		// fetch url for extracting CODE 
		// hitting authorization code url

		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			Desktop.getDesktop().browse(new URI(
					"https://accounts.google.com/o/oauth2/v2/auth?scope=https://www.googleapis.com/auth/userinfo.email&auth_url=https://accounts.google.com/o/oauth2/v2/auth&client_id=692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com&response_type=code&redirect_uri=https://rahulshettyacademy.com/getCourse.php&state=verifyooo"));
		}

		/******************************* Google stops automated fill of the login ******************************
		 * //System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
		 * WebDriver driver = new ChromeDriver(); driver.get(
		 * "https://accounts.google.com/o/oauth2/v2/auth?scope=https://www.googleapis.com/auth/userinfo.email&auth_url=https://accounts.google.com/o/oauth2/v2/auth&client_id=692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com&response_type=code&redirect_uri=https://rahulshettyacademy.com/getCourse.php&state=verifyooo"
		 * ); driver.findElement(By.cssSelector("input[type='email']")).sendKeys(
		 * "testing.tannessee@gmail.com");
		 * driver.findElement(By.cssSelector("input[type='email']")).sendKeys(Keys.ENTER
		 * ); Thread.sleep(3000);
		 * driver.findElement(By.cssSelector("input[type='password']")).sendKeys(
		 * "Tanya2011533");
		 * driver.findElement(By.cssSelector("input[type='password']")).sendKeys(Keys.
		 * ENTER); Thread.sleep(3000); String url = driver.getCurrentUrl();
		 */

		
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter URL to get code: ");
		String url = sc.next();
		String partialCode = url.split("code=")[1];
		String code = partialCode.split("&scope")[0];
		System.out.println("Code is : " + code);

		// fetch access token, hitting access token url
		String accessTokenResponse = given().urlEncodingEnabled(false)//to keep special symbols unchanged !
				.queryParam("code", code)
				.queryParam("client_id", "692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com")
				.queryParam("client_secret", "erZOWM9g3UtwNRj340YYaK_W").queryParams("state", "verifyfjdss")
				.queryParam("redirect_uri", "https://rahulshettyacademy.com/getCourse.php")
				.queryParam("grant_type", "authorization_code")
				.when().log().all()
				.post("https://www.googleapis.com/oauth2/v4/token").asString();
		JsonPath js = ReusableMethods.rawToJson(accessTokenResponse);
		String accessToken = js.getString("access_token");

		// hitting actual url with existing access token and getting the json response
		/************************************************************
		 * String response = given().contentType("application/json")
		 * .queryParam("access_token", accessToken) .when().log().all()
		 * .get("https://rahulshettyacademy.com/getCourse.php").asString();
		 * System.out.println(response);
		 * 
		 * /*************************** Get with POJO classes ****************************/

		GetCourse getC = given().contentType("application/json").queryParam("access_token", accessToken).expect()
				.defaultParser(Parser.JSON)// wait for json file
				.when().get("https://rahulshettyacademy.com/getCourse.php").as(GetCourse.class);

		System.out.println("LinkedIn link is : " + getC.getLinkedIn()); // Deserialized data from json to Java objects
		System.out.println("Instructor is : " + getC.getInstructor());
		getC.getCourses().getApi().get(1).getCourseTitle();

		List<Api> apiCourses = getC.getCourses().getApi(); // API courses list, every has few fields

		for (int i = 0; i < apiCourses.size(); i++) {
			if (apiCourses.get(i).getCourseTitle().equalsIgnoreCase("SoapUI Webservices Testing")) {
				System.out.println("The price of the course with index " + i + " is : " + apiCourses.get(i).getPrice());

			}
		}
		
		/****************** Checking actual and existing lists of courses  ******************************/
		
		String[] courseTitles = {"Selenium Webdriver Java", "Cypress", "Protractor"};
		List<String> expectedCourses = Arrays.asList(courseTitles); 
		
		List <WebAutomation> webAutomationCourses = getC.getCourses().getWebAutomation();
		ArrayList <String> arrActual = new ArrayList <String>(); 
		System.out.println("-----Titles of the courses from WebAutomation course-----");
		for (int i = 0; i < webAutomationCourses.size(); i++) {
			
			System.out.println(webAutomationCourses.get(i).getCourseTitle());
			arrActual.add(webAutomationCourses.get(i).getCourseTitle());
		}
		
		Assert.assertTrue(arrActual.equals(expectedCourses));

	}

}
