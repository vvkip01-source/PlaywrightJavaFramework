package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class BasicTest {

    Playwright playwright;
    Browser browser;
    Page page;
    @BeforeMethod(alwaysRun = true)
    public void setUp()
    {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        //page = browser.newPage();
        BrowserContext context = browser.newContext(
                new Browser.NewContextOptions().setViewportSize(null));
        page = context.newPage();
        page.setDefaultTimeout(8000);
        // Maximize the window using CDP (Chrome DevTools Protocol)
        page.navigate("https://eventhub.rahulshettyacademy.com/login");
        assertThat(page).hasTitle("EventHub — Discover & Book Events");
        //setDefaultAssertionTimeout(2000);
    }

    @Test(description = "Create Event -Book that event and verify if its booked")
    public void DemoTest() throws InterruptedException {
        String price = "100";
        int ticketCountToBeBooked = 3;
        page.getByPlaceholder("you@email.com").fill("vvkip01@gmail.com");
        page.getByLabel("Password").fill("Vvkip01@71984");
        page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Sign In")).click();
        assertThat(page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Browse Events →"))).isVisible();

        page.locator("#nav-events").click(new Locator.ClickOptions().setTimeout(10000));
        //page.setDefaultTimeout(12000);
        Thread.sleep(1000);
        Locator eventcardsAtStart =page.getByTestId("event-card");
        //Thread.sleep(3000);
        System.out.println("Total events at Start: " + eventcardsAtStart.count());
        page.setDefaultTimeout(8000);
        page.navigate("https://eventhub.rahulshettyacademy.com/admin/events");
        page.getByLabel("Title").fill("AI and ML Summit for QA");
        page.locator("#admin-event-form textarea").fill("A demostration of how AI and ML together has entered the human race and how should humans adapt to it..");
        page.getByLabel("Category").selectOption("Workshop");
        page.getByPlaceholder("e.g. Bangalore").fill("Mumbai");
        page.getByLabel("Venue").fill("Mumbai 400071");
        page.getByLabel("Event Date & Time").fill("2026-09-28T07:25");
        page.getByLabel("Price ($)").fill("100");
        page.getByLabel("Total Seats").fill(price);
        //page.getByLabel("Image URL (optional)").fill("C:\\workspace\\Playwright_Automation\\playwright_automation\\picture\\asus.jpg");
        //page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("+ Add Event")).click();
        page.locator("#add-event-btn").click(new Locator.ClickOptions().setTimeout(10000));
        assertThat(page.getByText("Event Created!")).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(10000));

        page.locator("#nav-events").click(new Locator.ClickOptions().setTimeout(10000));
        //page.setDefaultTimeout(12000);
        Thread.sleep(1000);
        Locator eventcardsBeforeBooking =page.getByTestId("event-card");
        System.out.println("Total events before booking: " + eventcardsBeforeBooking.count());
        page.setDefaultTimeout(8000);
        Locator targetcardBeforeBooking = eventcardsBeforeBooking.filter(new Locator.FilterOptions().setHasText("AI and ML Summit for QA"));
        assertThat(targetcardBeforeBooking).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(10000));
        String seatsTextBeforeBooking = targetcardBeforeBooking.getByText("seats").innerText();
        String[] strNumberCountBeforeBooking = seatsTextBeforeBooking.split(" ");
        String availableSeatsBeforeBooking = strNumberCountBeforeBooking[0];
        System.out.println("Total available seats before booking: " + availableSeatsBeforeBooking);
        targetcardBeforeBooking.getByText("Book Now").click();

        if(ticketCountToBeBooked!=0)
        {
           int icounter = 1;
           for (icounter = 1;icounter<ticketCountToBeBooked;icounter++)
           {
               page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("+")).click();
           }
        }
        String seatbooknumber = page.locator("#ticket-count").innerText();
        System.out.println("No. of seats to be booked: " +seatbooknumber);
        int priceperticket = Integer.parseInt(price) * Integer.parseInt(seatbooknumber);
        page.getByLabel("Full Name").fill("Ram");
        page.getByLabel("Email").fill("ram@gmail1.com");
        page.getByLabel("Phone Number").fill("9815823586");
        String totalamount = page.locator("//span[contains(text(),'Total')]/following-sibling::span[1]").innerText();
        String calculatedamt = Integer.toString(priceperticket);
        Assert.assertEquals(totalamount,"$"+calculatedamt);

        page.locator("#confirm-booking").click(new Locator.ClickOptions().setTimeout(3000));
        assertThat(page.getByText("Booking Confirmed!")).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(10000));

        //String bookingRefID = page.locator("//span[contains(text(),'Booking Ref')]/following-sibling::span[1]").innerText();
        String bookingRefID = page.locator(".booking-ref").innerText();
        System.out.println("Booking Ref Id : "+bookingRefID );

        String strTicketsBookedCount = page.locator("//span[contains(text(),'Tickets')]/following-sibling::span[1]").innerText();

        page.locator("#nav-events").click(new Locator.ClickOptions().setTimeout(10000));
        Thread.sleep(1000);
        Locator eventcardsAfterBooking =page.getByTestId("event-card");
        Locator targetcardAfterBooking = eventcardsAfterBooking.filter(new Locator.FilterOptions().setHasText("AI and ML Summit for QA"));
        String seatsTextAfterBoolking = targetcardAfterBooking.getByText("seats").innerText();
        String[] strNumberAfterBookningCount = seatsTextAfterBoolking.split(" ");
        String availableSeatsAfterBooking = strNumberAfterBookningCount[0];

        System.out.println("Available seats after booking : "+availableSeatsAfterBooking );
        int validateUpdatedSeatCount = Integer.parseInt(availableSeatsBeforeBooking) - Integer.parseInt(strTicketsBookedCount);
        Assert.assertEquals(Integer.parseInt(availableSeatsAfterBooking),validateUpdatedSeatCount);







    }

}
