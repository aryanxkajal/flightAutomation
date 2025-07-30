@FlightSearch
Feature: Flight Search Validation

Scenario Outline: Validate flight search results
  Given User is on Flight Search page
  When User searches for flight using row <rowIndex>
  Then Validate search result for row <rowIndex>

Examples:
  | rowIndex |
  | 1        |
  | 2        |
  | 3        |
  | 4        |
  | 5        |
