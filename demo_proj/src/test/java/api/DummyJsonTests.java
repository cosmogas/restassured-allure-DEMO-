package api;

import io.qameta.allure.*;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("DummyJSON API")
@Feature("Product API Tests")
@DisplayName("DummyJSON Product API Test Suite")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class DummyJsonTests {

    private static String BASE_URL;

    @BeforeAll
    static void setup() {
        BASE_URL = "https://dummyjson.com";
    }

    @Step("GET product by ID = 1 and validate fields")
    @Test
    @DisplayName("01 - GET product by ID")
    @Description("Should return product details for ID 1")
    public void getProductById() {
        given()
                .baseUri(BASE_URL)
                .when()
                .get("/products/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("title", containsString("Mascara"));
    }

    @Step("POST new product and verify response")
    @Test
    @DisplayName("02 - POST create new product (dummy)")
    @Description("Should create a new dummy product and return 201 Created")
    public void createProduct() {
        String newProduct = """
            {
              "title": "Test Product",
              "price": 99.99
            }
            """;

        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(newProduct)
                .when()
                .post("/products/add")
                .then()
                .statusCode(201)
                .body("title", equalTo("Test Product"))
                .body("price", equalTo(99.99f));
    }

    @Step("GET all products and verify list is not empty")
    @Test
    @DisplayName("03 - GET all products list")
    @Description("Should return list of products with non-empty items")
    public void getAllProducts() {
        given()
                .baseUri(BASE_URL)
                .when()
                .get("/products")
                .then()
                .statusCode(200)
                .body("products.size()", greaterThan(0));
    }

    @Step("Search products with query 'phone'")
    @Test
    @DisplayName("04 - Search products by keyword")
    @Description("Should return results when searching by keyword")
    public void searchProduct() {
        given()
                .baseUri(BASE_URL)
                .queryParam("q", "phone")
                .when()
                .get("/products/search")
                .then()
                .statusCode(200)
                .body("products.title.flatten()", hasItem(containsStringIgnoringCase("phone")));
    }

    // 🔽 Новые тесты

    @Step("Validate product categories endpoint")
    @Test
    @DisplayName("05 - GET all product categories")
    @Description("Should return list of product categories")
    public void getCategories() {
        given()
                .baseUri(BASE_URL)
                .when()
                .get("/products/categories")
                .then()
                .statusCode(200)
                .body("$", hasItem("beauty"));
    }

    @Step("GET all products from 'beauty' category")
    @Test
    @DisplayName("06 - GET products by category")
    @Description("Should return products from 'beauty' category")
    public void getProductsByCategory() {
        given()
                .baseUri(BASE_URL)
                .when()
                .get("/products/category/beauty")
                .then()
                .statusCode(200)
                .body("products.size()", greaterThan(0))
                .body("products.category.flatten()", everyItem(equalTo("beauty")));
    }

    @Step("Validate GET with limit param")
    @Test
    @DisplayName("07 - GET products with limit=5")
    @Description("Should return only 5 products")
    public void getProductsWithLimit() {
        given()
                .baseUri(BASE_URL)
                .queryParam("limit", 5)
                .when()
                .get("/products")
                .then()
                .statusCode(200)
                .body("products.size()", equalTo(5));
    }

    @Step("GET specific product and verify fields")
    @Test
    @DisplayName("08 - GET /products/2 details")
    @Description("Should return correct product info for ID 2")
    public void getSecondProduct() {
        given()
                .baseUri(BASE_URL)
                .when()
                .get("/products/2")
                .then()
                .statusCode(200)
                .body("id", equalTo(2))
                .body("title", not(emptyOrNullString()));
    }

    @Step("Intentional fail - validate nonexistent endpoint")
    @Test
    @DisplayName("09 - FAIL DEMO: GET invalid endpoint")
    @Description("This test is expected to fail for Allure report demonstration")
    public void failDemoTest() {
        given()
                .baseUri(BASE_URL)
                .when()
                .get("/products/broken-endpoint")
                .then()
                .statusCode(200); // ❌ Фейл: будет 404, а ожидаем 200
    }
}


