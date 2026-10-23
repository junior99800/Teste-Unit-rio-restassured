package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;



public class MovieControllerRA {

	private Long idMovie, nonExistingMovieId;
	private String clientUsername, clientPassword, adminUsername, adminPassword;
	private String adminToken, clientToken, invalidToken;

	private Map<String, Object> postMovieInstance;

    @BeforeEach
    public void setup() throws JSONException {
        baseURI = "http://localhost:8080";
        idMovie = 1L;
        nonExistingMovieId = 100L;

        clientUsername = "lucia@gmail.com";
        clientPassword = "123456";
        adminUsername = "alex@gmail.com";
        adminPassword = "123456";

        clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
        invalidToken = adminToken + "asdas";

        postMovieInstance = new HashMap<>();
        postMovieInstance.put("title", "");
        postMovieInstance.put("score", 0.0);
        postMovieInstance.put("count", 0);
        postMovieInstance.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
    }

	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
		given()
				.get("/movies")
				.then()
				.statusCode(200);

	}

	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {
		String searchTitle = "Livre";
		String expectedMovieTitle = "Django Livre";

		given()
				.queryParam("title", searchTitle)
				.when()
				.get("/movies")
				.then()
				.statusCode(200)
				.body("content.title", contains(expectedMovieTitle))
				.body("totalElements", is(1))
				.body("pageable.pageSize", is(20));
	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {

		given()
				.get("/movies/{id}", idMovie)
		.then()
				.statusCode(200)
				.body("id", is(1))
				.body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"))
				.body("score", is(4.5f))
				.body("title", equalTo("The Witcher"));

	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {

		given()
				.pathParam("id", nonExistingMovieId)
		.when()
						.get("/movies/{id}")
		.then()
				.statusCode(404);


	}

	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {
		JSONObject newMovies = new JSONObject(postMovieInstance);

		given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newMovies)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
		.when()
				.post("/movies")
		.then()
				.statusCode(422);


	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
		postMovieInstance.put("title", "Filme Válido");
		JSONObject newMovies = new JSONObject(postMovieInstance);

		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(newMovies)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.post("/movies")
				.then()
				.statusCode(403);
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {

		JSONObject newMovies = new JSONObject(postMovieInstance);

		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + invalidToken)
				.body(newMovies)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.post("/movies")
				.then()
				.statusCode(401);
	}
}
