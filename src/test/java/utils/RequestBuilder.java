package utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * Centraliza a construção e envio de todas as requisições HTTP.
 * Aplica o CurlLogger como filtro em cada request.
 */
public class RequestBuilder {

    private final ScenarioContext context;

    public RequestBuilder(ScenarioContext context) {
        this.context = context;
        RestAssured.baseURI = ConfigurationManager.getProperty("base.url");
    }

    // ── GET sem autenticação ──────────────────────────────────────────────────

    public Response get(String path) {
        return baseSpec().get(path);
    }

    // ── GET com token Bearer ──────────────────────────────────────────────────

    public Response getAuthenticated(String path) {
        String token = context.getString(ScenarioContext.ACCESS_TOKEN);
        return baseSpec()
                .header("Authorization", "Bearer " + token)
                .get(path);
    }

    public Response getWithToken(String path, String tokenHeader) {
        return baseSpec()
                .header("Authorization", tokenHeader)
                .get(path);
    }

    // ── POST ──────────────────────────────────────────────────────────────────

    public Response post(String path, Object body) {
        return baseSpec()
                .contentType("application/json")
                .body(body)
                .post(path);
    }

    public Response postAuthenticated(String path, Object body) {
        String token = context.getString(ScenarioContext.ACCESS_TOKEN);
        return baseSpec()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body(body)
                .post(path);
    }

    public Response postWithToken(String path, String tokenHeader, Object body) {
        return baseSpec()
                .header("Authorization", tokenHeader)
                .contentType("application/json")
                .body(body)
                .post(path);
    }

    public Response postRaw(String path, Object rawBody) {
        return baseSpec()
                .contentType("text/plain")
                .body(rawBody)
                .post(path);
    }

    public Response postRawAuthenticated(String path, Object rawBody) {
        String token = context.getString(ScenarioContext.ACCESS_TOKEN);
        return baseSpec()
                .header("Authorization", "Bearer " + token)
                .contentType("text/plain")
                .body(rawBody)
                .post(path);
    }

    // ── PATCH ─────────────────────────────────────────────────────────────────

    public Response patchAuthenticated(String path, Object body) {
        String token = context.getString(ScenarioContext.ACCESS_TOKEN);
        return baseSpec()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body(body)
                .patch(path);
    }

    public Response patchWithToken(String path, String tokenHeader, Object body) {
        return baseSpec()
                .header("Authorization", tokenHeader)
                .contentType("application/json")
                .body(body)
                .patch(path);
    }

    public Response patchNoAuth(String path, Object body) {
        return baseSpec()
                .contentType("application/json")
                .body(body)
                .patch(path);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public Response deleteAuthenticated(String path) {
        String token = context.getString(ScenarioContext.ACCESS_TOKEN);
        return baseSpec()
                .header("Authorization", "Bearer " + token)
                .delete(path);
    }

    public Response deleteWithToken(String path, String tokenHeader) {
        return baseSpec()
                .header("Authorization", tokenHeader)
                .delete(path);
    }

    public Response deleteNoAuth(String path) {
        return baseSpec().delete(path);
    }

    // ── Base spec com filtro de logging ──────────────────────────────────────

    private RequestSpecification baseSpec() {
        return given()
                .filter(new CurlLogger(context))
                .log().all();
    }
}
