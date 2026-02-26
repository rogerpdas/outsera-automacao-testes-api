package runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Ponto de entrada da suíte de testes Cucumber + JUnit4.
 *
 * Execução por tag:
 * mvn test -Dcucumber.filter.tags="@smoke"
 * mvn test -Dcucumber.filter.tags="@negativo"
 * mvn test -Dcucumber.filter.tags="@auth"
 * mvn test -Dcucumber.filter.tags="@products"
 */
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features", glue = { "steps",
                "hooks" }, tags = "not @ignore", plugin = {
                                "pretty",
                                "html:target/cucumber-reports/cucumber.html",
                                "json:target/cucumber-reports/cucumber.json",
                                "junit:target/cucumber-reports/cucumber.xml",
                                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
                }, monochrome = false, publish = false)
public class TestRunner {
        // Classe vazia — JUnit 4 usa reflexão via @RunWith
}
