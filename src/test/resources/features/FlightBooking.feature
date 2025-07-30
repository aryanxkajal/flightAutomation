@FlightBooking
Feature: Flight Booking Input Validation

Scenario Outline: Validate booking inputs and error messages
Given User is on Ticket Booking page
When User enters booking details from row <rowIndex>
And clicks on Book Now
Then Validate email and date for row <rowIndex>
And Validate expected message for row <rowIndex>

Examples:
| rowIndex |
| 1        | # Valid 
| 2        | # Invalid date
| 3        | # Invalid email
| 4        | # Empty email
| 5        | # Empty dropdown
| 6        | # Empty travel from
| 7        | # Empty travel to
| 8        | # Empty name
| 9        | # Empty date
| 10       | # Empty phone
| 11       | # Empty passengers count
| 12       | # Wrong name
| 13       | # All empty fields

Scenario: Validate reset button functionality
Given User is on Ticket Booking page
When User enters booking details from row 1
And User clicks on Reset button
Then All form fields should be empty