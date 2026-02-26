package utils;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.util.Map;

/**
 * Filtro RestAssured que intercepta cada request e monta o cURL equivalente.
 * O cURL gerado é armazenado no ScenarioContext e exibido no Extent Report.
 */
public class CurlLogger implements Filter {

    private final ScenarioContext context;

    public CurlLogger(ScenarioContext context) {
        this.context = context;
    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {

        String curl = buildCurl(requestSpec);
        context.set(ScenarioContext.LAST_CURL, curl);

        Response response = ctx.next(requestSpec, responseSpec);
        context.set(ScenarioContext.LAST_RESPONSE, response);

        return response;
    }

    private String buildCurl(FilterableRequestSpecification req) {
        StringBuilder sb = new StringBuilder();
        sb.append("curl -X ").append(req.getMethod()).append(" \\\n");
        sb.append("  '").append(req.getURI()).append("'");

        // Headers
        Map<String, ?> headers = req.getHeaders().asList()
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        h -> h.getName(),
                        h -> h.getValue(),
                        (a, b) -> b,
                        java.util.LinkedHashMap::new));

        for (Map.Entry<String, ?> entry : headers.entrySet()) {
            sb.append(" \\\n  -H '")
              .append(entry.getKey()).append(": ").append(entry.getValue())
              .append("'");
        }

        // Body
        String body = req.getBody() != null ? req.getBody().toString() : null;
        if (body != null && !body.isBlank()) {
            sb.append(" \\\n  -d '").append(body.replace("'", "\\'")).append("'");
        }

        return sb.toString();
    }
}
