package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.cucumber.java.Scenario;
import io.restassured.response.Response;

import java.io.File;

/**
 * Gerencia a criação e atualização do Extent Report HTML.
 * Registra cada cenário com cURL enviado e resposta recebida.
 */
public class ExtentReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> currentTest = new ThreadLocal<>();

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            File reportDir = new File("target/extent-reports");
            reportDir.mkdirs();

            ExtentSparkReporter spark = new ExtentSparkReporter("target/extent-reports/api-test-report.html");
            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle("API Test Automation Report");
            spark.config().setReportName("DummyJSON API Test Results");
            spark.config().setEncoding("UTF-8");
            spark.config().setTimeStampFormat("dd/MM/yyyy HH:mm:ss");
            spark.config().setCss(customCss());

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Ambiente", System.getProperty("env", "QA"));
            extent.setSystemInfo("Base URL", ConfigurationManager.getProperty("base.url"));
            extent.setSystemInfo("Java", System.getProperty("java.version"));
            extent.setSystemInfo("Projeto", "DummyJSON API Automation");
        }
        return extent;
    }

    public static void startTest(Scenario scenario) {
        ExtentTest test = getInstance()
                .createTest(scenario.getName())
                .assignCategory(scenario.getSourceTagNames().toArray(String[]::new));
        currentTest.set(test);
    }

    public static ExtentTest getTest() {
        return currentTest.get();
    }

    public static void logCurl(String curl) {
        if (getTest() != null && curl != null) {
            getTest().info("<details><summary><b>🔗 cURL Enviado</b></summary>"
                    + "<pre style='background:#1e1e1e;color:#d4d4d4;padding:12px;border-radius:6px;overflow:auto'>"
                    + escapeHtml(curl) + "</pre></details>");
        }
    }

    public static void logResponse(Response response) {
        if (getTest() == null || response == null)
            return;
        String body = response.getBody().prettyPrint();
        getTest().info("<details><summary><b>📥 Resposta — Status: "
                + response.getStatusCode() + "</b></summary>"
                + "<pre style='background:#1e1e1e;color:#d4d4d4;padding:12px;border-radius:6px;overflow:auto'>"
                + escapeHtml(body) + "</pre></details>");
    }

    public static void logInfo(String message) {
        if (getTest() != null)
            getTest().info(message);
    }

    public static void logPass(String message) {
        if (getTest() != null)
            getTest().pass("✅ " + message);
    }

    public static void logFail(String message) {
        if (getTest() != null)
            getTest().fail("❌ " + message);
    }

    public static void endTest(Scenario scenario) {
        if (getTest() == null)
            return;
        if (scenario.isFailed()) {
            getTest().fail("Cenário falhou: " + scenario.getName());
        } else {
            getTest().pass("Cenário passou com sucesso.");
        }
        currentTest.remove();
    }

    public static void flush() {
        if (extent == null)
            return;
        extent.flush();
        writeSummary();
    }

    private static void writeSummary() {
        try {
            int passed = (int) extent.getReport().getTestList().stream()
                    .filter(t -> t.getStatus() == com.aventstack.extentreports.Status.PASS)
                    .count();
            int failed = (int) extent.getReport().getTestList().stream()
                    .filter(t -> t.getStatus() == com.aventstack.extentreports.Status.FAIL)
                    .count();
            int total = passed + failed;

            String summary = String.format(
                    "TOTAL=%d%nPASSED=%d%nFAILED=%d%n", total, passed, failed);

            java.nio.file.Files.writeString(
                    java.nio.file.Path.of("target/extent-reports/summary.properties"),
                    summary);
        } catch (Exception e) {
            System.err.println("Erro ao gravar sumário: " + e.getMessage());
        }
    }

    private static String escapeHtml(String text) {
        if (text == null)
            return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static String customCss() {
        return ".r-img { display:none; } "
                + "pre { font-family: 'Fira Code', monospace; font-size:13px; }";
    }
}
