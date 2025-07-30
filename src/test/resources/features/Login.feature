@UserLogin
Feature: Login Feature

  Scenario Outline: Login with various credentials
    Given User is on Login page
    When User enters "<username>" and "<password>"
    Then User should see "<expectedResult>"

    Examples:
      | username    | password    | expectedResult            |
      | flightadmin | flightadmin | Dashboard                 |
      | admin123    | wrongpass   | Password is wrong         |
      | wronguser   | flightadmin | Username is wrong         |
      |             | test123     | Username cannot be empty  |
      | flightadmin |             | Password cannot be empty  |

  Scenario: Validate forgot password link functionality
    Given User is on Login page
    When User clicks on "Click here to reset it" link
    Then User should be redirected to password reset page