package steps;

import io.cucumber.java.pt.*;
import io.restassured.response.Response;
import org.junit.Assert;
import utils.*;
import services.AuthService;
import models.AuthPayload;

/**
 * Steps Cucumber para os cenários de autenticação (auth.feature).
 * Delega toda lógica de requisição para RequestBuilder.
 */
public class AuthSteps {

    private final ScenarioContext context;
    private final AuthService authService;
    private final RequestBuilder requestBuilder;
    private Response lastResponse;

    public AuthSteps(ScenarioContext context) {
        this.context = context;
        this.requestBuilder = new RequestBuilder(context);
        this.authService = new AuthService(context);
    }

    @Dado("que o sistema está configurado com a URL base {string}")
    public void configuraUrlBase(String url) {
        ExtentReportManager.logInfo("Base URL: <b>" + url + "</b>");
    }

    @Dado("que estou autenticado com credenciais válidas")
    public void autenticarComCredenciaisValidas() {
        AuthPayload payload = new AuthPayload(
                ConfigurationManager.getProperty("user.name"),
                ConfigurationManager.getProperty("user.password")
        );
        Response response = authService.login(payload);
        logAndStore(response);
        Assert.assertEquals("Login falhou durante Background", 200, response.getStatusCode());
        String token = response.jsonPath().getString("accessToken");
        Assert.assertNotNull("Token não encontrado na resposta de login", token);
        context.set(ScenarioContext.ACCESS_TOKEN, token);
        ExtentReportManager.logInfo("Token JWT obtido e armazenado com sucesso.");
    }

    @Quando("realizo login com usuário {string} e senha {string}")
    public void realizoLoginComUsuarioESenha(String username, String password) {
        AuthPayload payload = new AuthPayload(username, password);
        lastResponse = authService.login(payload);
        logAndStore(lastResponse);
    }

    @Quando("realizo login com a senha {string} e titulo nulo")
    public void realizoLoginComUsernameNulo(String password) {
        AuthPayload payload = new AuthPayload(null, password);
        lastResponse = authService.login(payload);
        logAndStore(lastResponse);
    }

    @Quando("realizo uma requisição POST para {string} com corpo malformado {string}")
    public void postarComCorpoMalformado(String path, String rawBody) {
        lastResponse = requestBuilder.postRaw(path, rawBody);
        logAndStore(lastResponse);
    }

    @Quando("busco meu perfil logado")
    public void buscoMeuPerfilLogado() {
        lastResponse = authService.getMeAutenticado();
        logAndStore(lastResponse);
    }

    @Quando("busco meu perfil logado sem token")
    public void buscoMeuPerfilLogadoSemToken() {
        lastResponse = authService.getMeSemToken();
        logAndStore(lastResponse);
    }

    @Quando("busco meu perfil logado com token inválido {string}")
    public void buscoMeuPerfilLogadoComTokenInvalido(String tokenInvalido) {
        lastResponse = authService.getMeTokenInvalido(tokenInvalido);
        logAndStore(lastResponse);
    }

    @Então("o status code da resposta deve ser {int}")
    public void validarStatusCode(int expectedStatus) {
        Response response = (Response) context.get("lastResponse");
        int actualStatus = response.getStatusCode();
        String msg = "Status code esperado: " + expectedStatus + " | Recebido: " + actualStatus;
        if (actualStatus == expectedStatus) {
            ExtentReportManager.logPass(msg);
        } else {
            ExtentReportManager.logFail(msg);
        }
        Assert.assertEquals(msg, expectedStatus, actualStatus);
    }

    @Então("o header {string} deve conter {string}")
    public void validarHeader(String headerName, String expectedValue) {
        Response response = (Response) context.get("lastResponse");
        String actual = response.getHeader(headerName);
        boolean ok = actual != null && actual.contains(expectedValue);
        String msg = "Header [" + headerName + "] esperado conter: " + expectedValue + " | Recebido: " + actual;
        if (ok)
            ExtentReportManager.logPass(msg);
        else
            ExtentReportManager.logFail(msg);
        Assert.assertTrue(msg, ok);
    }

    @Então("o corpo da resposta deve conter o campo {string}")
    public void validarCampoNoCorpo(String field) {
        Response response = (Response) context.get("lastResponse");
        Object value = response.jsonPath().get(field);
        String msg = "Campo [" + field + "] presente na resposta";
        if (value != null)
            ExtentReportManager.logPass(msg);
        else
            ExtentReportManager.logFail("Campo [" + field + "] ausente na resposta");
        Assert.assertNotNull("Campo esperado ausente: " + field, value);
    }

    @Então("o campo {string} da resposta deve ser {string}")
    public void validarValorCampo(String field, String expected) {
        Response response = (Response) context.get("lastResponse");
        String actual = response.jsonPath().getString(field);
        String msg = "Campo [" + field + "] esperado: " + expected + " | Recebido: " + actual;
        if (expected.equals(actual))
            ExtentReportManager.logPass(msg);
        else
            ExtentReportManager.logFail(msg);
        Assert.assertEquals(msg, expected, actual);
    }

    @Então("o campo {string} da resposta deve ser maior que {int}")
    public void validarCampoMaiorQue(String field, int min) {
        Response response = (Response) context.get("lastResponse");
        int actual = response.jsonPath().getInt(field);
        String msg = "Campo [" + field + "] esperado > " + min + " | Recebido: " + actual;
        if (actual > min)
            ExtentReportManager.logPass(msg);
        else
            ExtentReportManager.logFail(msg);
        Assert.assertTrue(msg, actual > min);
    }

    @Então("o token JWT é armazenado para uso posterior")
    public void armazenarToken() {
        Response response = (Response) context.get("lastResponse");
        String token = response.jsonPath().getString("accessToken");
        Assert.assertNotNull("accessToken não encontrado na resposta", token);
        context.set(ScenarioContext.ACCESS_TOKEN, token);
        ExtentReportManager.logPass("Token JWT armazenado no contexto.");
    }

    @Então("o contrato da resposta deve respeitar o schema {string}")
    public void validarSchema(String schemaPath) {
        Response response = (Response) context.get("lastResponse");
        try {
            response.then().assertThat().body(io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/" + schemaPath));
            ExtentReportManager.logPass("JSON Schema (" + schemaPath + ") validado com sucesso.");
        } catch (AssertionError e) {
            ExtentReportManager.logFail("Falha na validação do JSON Schema: " + e.getMessage());
            throw e;
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private void logAndStore(Response response) {
        String curl = context.getString(ScenarioContext.LAST_CURL);
        ExtentReportManager.logCurl(curl);
        ExtentReportManager.logResponse(response);
        this.lastResponse = response;
        context.set("lastResponse", response);
    }
}
