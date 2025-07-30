@EnquiryPage
Feature: Enquiry Form Validation

  Scenario Outline: Validate enquiry form using data-driven row
    Given User is on Enquiry page
    When User submits enquiry with data from row <RowIndex>
    Then Validate all expected outcomes from row <RowIndex>

    Examples:
      | RowIndex |
	  | 1        |
      | 2        |
      | 3        | 
      | 4        |
      | 5        |
      
      
