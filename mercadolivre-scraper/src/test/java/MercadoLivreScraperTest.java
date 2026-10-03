import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class MercadoLivreScraperTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--lang=pt-BR");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Test
    public void buscarPrimeiroProdutoESalvarCsv() throws IOException {
        driver.get("https://www.mercadolivre.com.br/");

        try {
            WebElement cookies = new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                    ExpectedConditions.elementToBeClickable(
                            By.cssSelector("button[data-testid='action:understood-button']")));
            cookies.click();
        } catch (Exception e) {
        }

        WebElement campoBusca = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[name='as_word'], #cb1-edit")));
        campoBusca.clear();
        campoBusca.sendKeys("Xiaomi POCO");
        campoBusca.sendKeys(Keys.ENTER);

        By seletorCard = By.cssSelector(
                "li.ui-search-layout__item, div.poly-card, ol.ui-search-layout > li");
        wait.until(ExpectedConditions.presenceOfElementLocated(seletorCard));
        WebElement primeiroCard = driver.findElements(seletorCard).get(0);

        WebElement link = primeiroCard.findElement(By.cssSelector(
                "a.poly-component__title, h2.ui-search-item__title a, a.ui-search-link"));
        String descricao = link.getText().trim();
        String url = link.getAttribute("href");

        if (descricao.isEmpty()) {
            descricao = primeiroCard.findElement(By.cssSelector("h2, h3")).getText().trim();
        }

        String precoTexto = primeiroCard.findElement(By.cssSelector(
                "span.andes-money-amount:not(.andes-money-amount--previous) "
                        + "span.andes-money-amount__fraction")).getText();
        String preco = precoTexto.replace(".", "").trim();

        System.out.println("Descricao: " + descricao);
        System.out.println("Preco: " + preco);
        System.out.println("URL: " + url);

        String descricaoCsv = "\"" + descricao.replace("\"", "\"\"") + "\"";

        try (FileWriter writer = new FileWriter("produto.csv", StandardCharsets.UTF_8)) {
            writer.write("Descrição,Preço,URL\n");
            writer.write(descricaoCsv + "," + preco + "," + url + "\n");
        }

        System.out.println("Arquivo produto.csv gerado com sucesso!");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
