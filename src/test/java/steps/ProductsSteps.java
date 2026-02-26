package steps;

import io.cucumber.java.pt.*;
import io.restassured.response.Response;
import models.ProductPayload;
import services.ProductService;
import utils.ExtentReportManager;
import utils.ScenarioContext;

/**
 * Steps Cucumber para os cenários de produtos (products.feature).
 * Reutiliza os steps de asserção já definidos em AuthSteps via PicoContainer.
 */
public class ProductsSteps {

    private final ScenarioContext context;
    private final ProductService productService;
    private Response lastResponse;

    public ProductsSteps(ScenarioContext context) {
        this.context = context;
        this.productService = new ProductService(context);
    }

    // ── GET ───────────────────────────────────────────────────────────────────

    @Quando("listo todos os produtos")
    public void listAll() {
        lastResponse = productService.getAll();
        logAndStore(lastResponse);
    }

    @Quando("listo todos os produtos sem token")
    public void listAllNoToken() {
        lastResponse = productService.getAll(null);
        logAndStore(lastResponse);
    }

    @Quando("listo todos os produtos com token inválido {string}")
    public void listAllInvalidToken(String invalidToken) {
        lastResponse = productService.getAll(invalidToken);
        logAndStore(lastResponse);
    }

    @Quando("busco o produto de ID {int}")
    public void getById(int id) {
        lastResponse = productService.getById(id);
        logAndStore(lastResponse);
    }

    @Quando("busco o produto de ID {int} sem token")
    public void getByIdNoToken(int id) {
        lastResponse = productService.getById(id, null);
        logAndStore(lastResponse);
    }

    @Quando("busco o produto de ID {int} com token inválido {string}")
    public void getByIdInvalidToken(int id, String invalidToken) {
        lastResponse = productService.getById(id, invalidToken);
        logAndStore(lastResponse);
    }

    // ── POST ──────────────────────────────────────────────────────────────────

    @Quando("eu crio um novo produto com titulo {string} e preço {double}")
    public void createProduct(String title, Double price) {
        ProductPayload payload = new ProductPayload(title, price, "Produto criado via automação", "smartphones", 10);
        lastResponse = productService.create(payload);
        logAndStore(lastResponse);
    }

    @Quando("eu crio um produto sem token")
    public void createProductNoToken() {
        ProductPayload payload = new ProductPayload("Produto Sem Auth", 50.0, null, null, null);
        lastResponse = productService.create(payload, null);
        logAndStore(lastResponse);
    }

    @Quando("eu crio um produto com token inválido {string}")
    public void createProductInvalidToken(String invalidToken) {
        ProductPayload payload = new ProductPayload("Produto Token Invalido", 50.0, null, null, null);
        lastResponse = productService.create(payload, invalidToken);
        logAndStore(lastResponse);
    }

    @Quando("eu crio um produto enviando preco como string")
    public void createProductStringPrice() {
        String rawJson = "{\"title\": \"Produto Inválido\", \"price\": \"nao-e-numero\"}";
        lastResponse = productService.createRaw(rawJson);
        logAndStore(lastResponse);
    }

    // ── PATCH ─────────────────────────────────────────────────────────────────

    @Quando("atualizo o produto de ID {int} mudando o titulo para {string}")
    public void updateProduct(int id, String newTitle) {
        ProductPayload payload = new ProductPayload(newTitle, null, null, null, null);
        lastResponse = productService.update(id, payload);
        logAndStore(lastResponse);
    }

    @Quando("atualizo o produto de ID {int} sem token")
    public void updateProductNoToken(int id) {
        ProductPayload payload = new ProductPayload("Tentativa Sem Auth", null, null, null, null);
        lastResponse = productService.update(id, payload, null);
        logAndStore(lastResponse);
    }

    @Quando("atualizo o produto de ID {int} com token inválido {string}")
    public void updateProductInvalidToken(int id, String invalidToken) {
        ProductPayload payload = new ProductPayload("Tentativa Token Invalido", null, null, null, null);
        lastResponse = productService.update(id, payload, invalidToken);
        logAndStore(lastResponse);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Quando("deleto o produto de ID {int}")
    public void deleteProduct(int id) {
        lastResponse = productService.delete(id);
        logAndStore(lastResponse);
    }

    @Quando("deleto o produto de ID {int} sem token")
    public void deleteProductNoToken(int id) {
        lastResponse = productService.delete(id, null);
        logAndStore(lastResponse);
    }

    @Quando("deleto o produto de ID {int} com token inválido {string}")
    public void deleteProductInvalidToken(int id, String invalidToken) {
        lastResponse = productService.delete(id, invalidToken);
        logAndStore(lastResponse);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private void logAndStore(Response response) {
        String curl = context.getString(ScenarioContext.LAST_CURL);
        ExtentReportManager.logCurl(curl);
        ExtentReportManager.logResponse(response);
        
        // Armazena no context para que os asserts do AuthSteps possam pegar e validar 
        // ou usamos métodos globais se houver.
        // O Cucumber e o PicoContainer podem compartilhar states, AuthSteps valida lastResponse próprio, 
        // nós precisamos colocar a resposta lá, ou expor no scenario context!
        context.set("lastResponse", response);
    }
}
