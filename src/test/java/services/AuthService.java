package services;

import io.restassured.response.Response;
import models.AuthPayload;
import utils.RequestBuilder;
import utils.ScenarioContext;

public class AuthService {

    private static final String AUTH_LOGIN_PATH = "/auth/login";
    private static final String AUTH_ME_PATH = "/auth/me";

    private final RequestBuilder requestBuilder;

    public AuthService(ScenarioContext context) {
        this.requestBuilder = new RequestBuilder(context);
    }

    public Response login(AuthPayload payload) {
        return requestBuilder.post(AUTH_LOGIN_PATH, payload);
    }

    public Response getMeAutenticado() {
        return requestBuilder.getAuthenticated(AUTH_ME_PATH);
    }

    public Response getMeSemToken() {
        return requestBuilder.get(AUTH_ME_PATH);
    }

    public Response getMeTokenInvalido(String invalidTokenHeader) {
        return requestBuilder.getWithToken(AUTH_ME_PATH, invalidTokenHeader);
    }
}
