package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginPage {
    Page page;
    String base_url;
    private static final String email_placeHolder = "you@email.com";
    private static final String password_label = "Password";

    public LoginPage(Page page, String baseUrl)
    {
        this.page =page;
        this.base_url = baseUrl;
    }
    public DashboardPage loginToApplication()
    {
        page.navigate(base_url);
        System.out.println(page.title());
        assertThat(page).hasTitle("EventHub — Discover & Book Events");
        //page.getByLabel("Email").fill("rahulshetty1@yahoo.com");
        page.getByPlaceholder(email_placeHolder).fill("vvkip01@gmail.com");
        page.getByLabel(password_label).fill("Vvkip01@71984");
        //locator
        page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Sign In")).click();
        return new DashboardPage(page);
    }

}
