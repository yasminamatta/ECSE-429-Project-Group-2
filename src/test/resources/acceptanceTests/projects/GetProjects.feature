Feature: Get project by id
  As a user, I want to get all the projects so that I make a decision on what projects need to be added and which ones to remove


  # Normal flow
  Scenario: Get all projects successfully
    Given the server is running
    When the user makes GET request to get all project items
    Then one project item shall be returned



  # Alternative flow
  Scenario: Get empty project list
    Given the server is running
    Given there exists zero projects in the system
    When the user makes GET request to get all project items
    Then zero project item shall be returned

  # Error flow
  Scenario: Get project list when server not running
    Given the server is not running
    When the user makes GET request to get all project items
    Then the API call should not be successful
