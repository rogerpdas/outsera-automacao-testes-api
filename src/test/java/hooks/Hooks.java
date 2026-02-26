package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import utils.ExtentReportManager;
import utils.ScenarioContext;

/**
 * Hooks Cucumber que gerenciam o ciclo de vida de cada cenário:
 * - @Before: inicializa os testes no Extent Report
 * - @After:  finaliza o teste, registra status e libera contexto
 */
public class Hooks {

    private final ScenarioContext context;

    public Hooks(ScenarioContext context) {
        this.context = context;
    }

    @Before(order = 1)
    public void beforeScenario(Scenario scenario) {
        ExtentReportManager.startTest(scenario);
        ExtentReportManager.logInfo(
            "▶ Iniciando cenário: <b>" + scenario.getName() + "</b><br/>"
            + "Tags: " + scenario.getSourceTagNames()
        );
    }

    @After(order = 1)
    public void afterScenario(Scenario scenario) {
        if (scenario.isFailed()) {
            ExtentReportManager.logFail("Cenário finalizado com falha.");
        } else {
            ExtentReportManager.logPass("Cenário finalizado com sucesso.");
        }
        ExtentReportManager.endTest(scenario);
        ExtentReportManager.flush();
    }
}
