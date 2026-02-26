package services;

import io.restassured.response.Response;
import models.ProductPayload;
import utils.RequestBuilder;
import utils.ScenarioContext;

public class ProductService {

    private static final String PRODUCTS_PATH = "/auth/products";
    private static final String PRODUCTS_ADD_PATH = "/auth/products/add";
    
    private final RequestBuilder requestBuilder;

    public ProductService(ScenarioContext context) {
        this.requestBuilder = new RequestBuilder(context);
    }

    public Response getAll() {
        return requestBuilder.getAuthenticated(PRODUCTS_PATH);
    }

    public Response getAll(String invalidToken) {
        if (invalidToken == null) return requestBuilder.get(PRODUCTS_PATH);
        return requestBuilder.getWithToken(PRODUCTS_PATH, invalidToken);
    }

    public Response getById(int id) {
        return requestBuilder.getAuthenticated(PRODUCTS_PATH + "/" + id);
    }

    public Response getById(int id, String invalidToken) {
        if (invalidToken == null) return requestBuilder.get(PRODUCTS_PATH + "/" + id);
        return requestBuilder.getWithToken(PRODUCTS_PATH + "/" + id, invalidToken);
    }

    public Response create(ProductPayload payload) {
        return requestBuilder.postAuthenticated(PRODUCTS_ADD_PATH, payload);
    }

    public Response create(ProductPayload payload, String invalidToken) {
        if (invalidToken == null) return requestBuilder.post(PRODUCTS_ADD_PATH, payload);
        return requestBuilder.postWithToken(PRODUCTS_ADD_PATH, invalidToken, payload);
    }

    public Response createRaw(String rawPayload) {
        return requestBuilder.postRawAuthenticated(PRODUCTS_ADD_PATH, rawPayload);
    }

    public Response update(int id, ProductPayload payload) {
        return requestBuilder.patchAuthenticated(PRODUCTS_PATH + "/" + id, payload);
    }

    public Response update(int id, ProductPayload payload, String invalidToken) {
        if (invalidToken == null) return requestBuilder.patchNoAuth(PRODUCTS_PATH + "/" + id, payload);
        return requestBuilder.patchWithToken(PRODUCTS_PATH + "/" + id, invalidToken, payload);
    }

    public Response delete(int id) {
        return requestBuilder.deleteAuthenticated(PRODUCTS_PATH + "/" + id);
    }

    public Response delete(int id, String invalidToken) {
        if (invalidToken == null) return requestBuilder.deleteNoAuth(PRODUCTS_PATH + "/" + id);
        return requestBuilder.deleteWithToken(PRODUCTS_PATH + "/" + id, invalidToken);
    }
}
