Feature: Create a todo
  As a user, I want to create a todo so that I can assign it to projects and categories

  Background:
    Given the server is running


  # Normal flow
  Scenario Outline: Create a todo successfully
    When the user makes a query to create a todo item with title "<title>" and description "<description>"
    Then one todo item shall be created and returned

    Examples:
      | title        | description                  |
      | shred papers | shred papers into 100 pieces |
      | clean desk   | clean with lysol             |

  # Alternate flow
  Scenario Outline: Create a todo with only title specified and others fields are set to their default values implicitly
    When the user makes a query to create a todo item with only title "<title>"
    Then one todo item shall be created and returned
    Then description shall be set to "<description>" and doneStatus to "<doneStatus>"

    Examples:
      | title         | description | doneStatus |
      | staple papers |             | false      |
      | make labels   |             | false      |

  # Error flow
  Scenario Outline: Create a todo with empty title field
    When the user makes a query to create a todo item with title "<title>" and description "<description>"
    Then no todo item shall be created
    Then an error message with content "<errorMessage>" shall be raised

    Examples:
      | title | description                  | errorMessage                                |
      |       | shred papers into 100 pieces | Failed Validation: title : can not be empty |


