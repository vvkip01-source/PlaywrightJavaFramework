package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class BookingFormPage {

    Page page;

    private static final String FULL_NAME_LABEL = "Full Name";
    private static final String CUSTOMER_EMAIL_ID = "#customer-email";
    private static final String PHONE_PLACEHOLDER = "+91 98765 43210";

    public BookingFormPage(Page page) {
        this.page = page;
    }


    public void fillAndConfirm(String fullName, String email, String phone, String numberOfTickets, String price) {

        int ticketCountToBeBooked = Integer.parseInt(numberOfTickets);
        if(ticketCountToBeBooked!=0)
        {
            int icounter = 1;
            for (icounter = 1;icounter<ticketCountToBeBooked;icounter++)
            {
                page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("+")).click();
            }
        }
        page.getByLabel(FULL_NAME_LABEL).fill(fullName);
        page.locator(CUSTOMER_EMAIL_ID).fill(email);
        page.getByPlaceholder(PHONE_PLACEHOLDER).fill(phone);
        String seatbooknumber = page.locator("#ticket-count").innerText();
        System.out.println("No. of seats to be booked: " +seatbooknumber);
        int totalTicketPrice = Integer.parseInt(price) * Integer.parseInt(seatbooknumber);
        System.out.println("Total price for the booked tickets: " + totalTicketPrice);
        String temptotalamount = page.locator("//span[contains(text(),'Total')]/following-sibling::span[1]").innerText();
        String totalamount = temptotalamount.replace(",","");
        String calculatedamt = Integer.toString(totalTicketPrice);
        Assert.assertEquals(totalamount,"$"+calculatedamt);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm Booking")).click();
        assertThat(page.getByText("Your tickets are reserved.")).isVisible();
    }
    public String getBookingReference() {
        return page.locator(".booking-ref").innerText();
    }
}
