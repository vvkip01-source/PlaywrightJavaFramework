package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class BookingsPage {

    Page page;
    private static final String VIEW_BOOKING_BUTTON = "button:has-text('View Details')";

    public BookingsPage(Page page) {
        this.page = page;
    }

    public void goTo() {
        page.navigate("https://eventhub.rahulshettyacademy.com/bookings");
    }

    //public Locator findBookingCard(String bookingReference) {
    //    return page.locator("div.booking-card:has-text(\"" + bookingReference + "\")");
    //}

    public BookingDetailsPage viewBookingDetails(Locator targetCard) {
        targetCard.getByRole(com.microsoft.playwright.options.AriaRole.LINK, new Locator.GetByRoleOptions().setName("View Details")).click();
        //page.locator("a[href=/bookings/" + bookingId + "]").click();
        return new BookingDetailsPage(page);
    }
    public Locator findBookingCard(String titleCard,Locator bookingCards)
    {
        Locator targetCard = bookingCards.filter(new Locator.FilterOptions().setHasText(titleCard));
        assertThat(targetCard).isVisible();
        return targetCard;
    }

    public boolean isBookingVisible(String bookingReference) {
        return page.getByText(bookingReference).isVisible();
    }

    public Locator waitForBookingsToLoad() {
        assertThat(page.getByRole(com.microsoft.playwright.options.AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("My Bookings"))).isVisible();
        Locator bookingCards = page.getByTestId("booking-card"); //{loc1,loc2,loc3,loc4}
        assertThat(bookingCards.first()).isVisible();
        return bookingCards;
    }

}
