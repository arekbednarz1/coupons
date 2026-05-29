package pl.arekbednarz.coupons.adapter;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import pl.arekbednarz.coupons.CouponsApplication;
import pl.arekbednarz.coupons.adapter.in.web.dto.CouponResponse;
import pl.arekbednarz.coupons.adapter.in.web.dto.CreateCouponRequest;
import pl.arekbednarz.coupons.utils.PostgresqlTestContainer;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@ContextConfiguration(initializers = PostgresqlTestContainer.class)
@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
	properties = { "server.port=4324" },
	classes = CouponsApplication.class)
class CouponControllerTest {

	@BeforeEach
	void setUp() {
		RestAssured.port = 4324;
	}

	@Test
	void shouldCreateCoupon() {

		var dto = new CreateCouponRequest();
		dto.setCode("TESTCODE_1");
		dto.setMaxUsages(3);
		dto.setCountryCode("PL");

		var response =
			given()
				.contentType(ContentType.JSON)
				.when()
				.body(dto)
				.post("/api/v1/coupons")
				.then()
				.statusCode(201)
				.extract()
				.as(CouponResponse.class);

		assertNotNull(response);
		assertNotNull(response.getId());
		assertNotNull(response.getCode());
	}

	@Test
	void shouldThrowExcpetionWhenCodeExists() {

		var dto = new CreateCouponRequest();
		dto.setCode("TESTCODE_44");
		dto.setMaxUsages(3);
		dto.setCountryCode("PL");

		given()
			.contentType(ContentType.JSON)
			.when()
			.body(dto)
			.post("/api/v1/coupons")
			.then()
			.statusCode(201);

		dto.setCode("testcode_44");

		given()
			.contentType(ContentType.JSON)
			.when()
			.body(dto)
			.post("/api/v1/coupons")
			.then()
			.statusCode(409);

	}

	@Test
	void shouldUseCode() {
		var dto = new CreateCouponRequest();
		dto.setCode("testercoder");
		dto.setMaxUsages(3);
		dto.setCountryCode("PL");

		given()
			.contentType(ContentType.JSON)
			.when()
			.body(dto)
			.post("/api/v1/coupons")
			.then()
			.statusCode(201);

		given()
			.contentType(ContentType.JSON)
			.when()
			.header("X-Real-IP", "185.157.14.235")
			.queryParam("userId", "testUser")
			.post("/api/v1/coupons/testercoder/use")
			.then()
			.statusCode(200);
	}

	@Test
	void shouldReturn404WhenNotFOund() {

		given()
			.contentType(ContentType.JSON)
			.when()
			.header("X-Real-IP", "185.157.14.235")
			.queryParam("userId", "testUser")
			.post("/api/v1/coupons/testerder/use")
			.then()
			.statusCode(404);
	}

}
