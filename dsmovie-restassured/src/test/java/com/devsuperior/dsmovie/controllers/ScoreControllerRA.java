package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

public class ScoreControllerRA {

	private String  adminUsername, adminPassword;
	private String adminToken;

	private Map<String, Object> putScoreInstance;

	@BeforeEach
	public void setup() throws JSONException {
		baseURI = "http://localhost:8080";


		adminUsername = "maria@gmail.com";
		adminPassword = "123456";


		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);

		putScoreInstance = new HashMap<>();
		putScoreInstance.put("movieId", 100);
		putScoreInstance.put("score", 4.0);
	}
	
	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {
		JSONObject newScore = new JSONObject(putScoreInstance);

		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.body(newScore)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
		.when()
				.put("/scores")
		.then()
				.statusCode(404);
	}


	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
		Map<String, Object> invalidScore = new HashMap<>();
		invalidScore.put("score", 4.0);
		JSONObject newScore = new JSONObject(invalidScore);

		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.body(newScore)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.put("/scores")
				.then()
				.statusCode(422);
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {
		Map<String, Object> invalidScore = new HashMap<>();
		invalidScore.put("movieId", 2);
		invalidScore.put("score", -4);
		JSONObject newScore = new JSONObject(invalidScore);

		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.body(newScore)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.put("/scores")
				.then()
				.statusCode(422);
	}

}
