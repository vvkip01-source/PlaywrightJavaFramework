package tests;

import com.microsoft.playwright.Locator;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.*;
import utils.DataProviderUtil;

import java.io.IOException;
import java.util.HashMap;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertTrue;

public class FrameworkBuildDataDrivenTest extends TestBase {

    @DataProvider(name="eventBookingData")
    public Object[][] eventBookingData() throws IOException {
        return  DataProviderUtil.getJsonDataToMap("/src/test/resources/eventBookingData.json");
    }

    @Test(groups = {"framework"},dataProvider = "eventBookingData", priority=1, description = "Create an Event -verify if its created successfully")
    public void CreateEventTest(HashMap<String,String> data) throws InterruptedException {

        LoginPage loginPage = new LoginPage(page, base_url);
        DashboardPage dashboardPage = loginPage.loginToApplication();
        dashboardPage.waitForEventsToLoad();
        EventsPage eventsPage = new EventsPage(page);
        eventsPage.goTo();
        Locator eventCardsBeforeCreatingEvent = eventsPage.waitForEventsToLoad();
        System.out.println("No.of Events in the page at Start: " + eventCardsBeforeCreatingEvent.count());
        AdminEventsPage adminEventsPage = new AdminEventsPage(page);
        adminEventsPage.goTo();
        //Step 1 - Create Event from Admin pageeventTitle
        adminEventsPage.createEvent(
                data.get("titlePrefix"),
                data.get("description"),
                data.get("city"),
                data.get("venue"),
                data.get("dateTime"),
                data.get("price"),
                data.get("totalSeats")
        );

        //Step 2 - Find newly created event in the events page
        eventsPage.goTo();
        Locator eventCardsAfterCreatingEvent = eventsPage.waitForEventsToLoad();
        System.out.println("No.of Events in the page after Creating Event: " + eventCardsAfterCreatingEvent.count());
        Locator targetCard = eventsPage.findEventCard(data.get("titlePrefix"));
    }
    @Test(groups = {"framework"},dataProvider = "eventBookingData", priority=2, description = "Book an Event -verify if its booked successfully")
    public void BookEventTest(HashMap<String,String> data) throws InterruptedException
    {
        LoginPage loginPage = new LoginPage(page, base_url);
        DashboardPage dashboardPage = loginPage.loginToApplication();
        dashboardPage.waitForEventsToLoad();
        EventsPage eventsPage = new EventsPage(page);
        eventsPage.goTo();
        eventsPage.waitForEventsToLoad();
        Locator targetCard = eventsPage.findEventCard(data.get("titlePrefix"));
        int seatsNumBeforeBooking = eventsPage.getSeatsCount(targetCard);
        System.out.println("Seats available before booking: " + seatsNumBeforeBooking);

        // Step 3 - Book the event and verify the seats count after booking
        BookingFormPage bookingFormPage = eventsPage.proceedToBookingEvent(targetCard);
        bookingFormPage.fillAndConfirm(data.get("fullName"),
                data.get("email"),
                data.get("phone"),
                data.get("numberoftickets"),
                data.get("price"));

        booking_reference=bookingFormPage.getBookingReference();
        System.out.println("Booking Reference: " + booking_reference);

        // Step 4 - Verify the seats count after booking
        eventsPage.goTo();
        Thread.sleep(1000); // Wait for the page to refresh and show updated seats count
        Locator targetCardAfterBooking = eventsPage.findEventCard(data.get("titlePrefix"));
        int seatsNumAfterBooking = eventsPage.getSeatsCount(targetCardAfterBooking);
        System.out.println("Seats available after booking: " + seatsNumAfterBooking);
        int expectedSeatsAfterBooking = seatsNumBeforeBooking - Integer.parseInt(data.get("numberoftickets"));
        assert seatsNumAfterBooking == expectedSeatsAfterBooking : "Seats count after booking does not match expected value. Expected: " + expectedSeatsAfterBooking + ", Actual: " + seatsNumAfterBooking;

        //Step 5 - Verify the booking in Booking details
        BookingsPage bookingsPage = new BookingsPage(page);
        bookingsPage.goTo();
        Locator bookingsCards = bookingsPage.waitForBookingsToLoad();
        assertTrue(bookingsPage.isBookingVisible(booking_reference));

        //Step 6 - View Booking Details by Identifying the booking card associated to the booking and verify the details
        Locator targetBookingCard = bookingsPage.findBookingCard(booking_reference, bookingsCards);
        BookingDetailsPage bookingDetailsPage = bookingsPage.viewBookingDetails(targetBookingCard);
        bookingDetailsPage.verifyBookingDetailsPageIsDisplayed();

    }
}
