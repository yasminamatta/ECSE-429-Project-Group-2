Feature: Get todo by id
  As a user, I want to get a todo by id so that I can assign it to projects and categories

  Background:
    Given the server is running
    Given atleast one todo exists in the system

  # Normal flow
  Scenario Outline: Get a todo by id successfully
    When the user makes a query to get a todo item by ID "<id>"
    Then one todo item shall be returned
    Then the todo shall have id "<id>", title "<title>", and doneStatus "<doneStatus>"

    Examples:
      | id | title          | doneStatus |
      | 1  | scan paperwork | false      |
      | 2  | file paperwork | false      |

  # Error flow
  Scenario Outline: Get a todo by non-existing id
    When the user makes a query to get a todo item by ID "<id>"
    Then no todo item shall be returned
    Then an error message with content "<errorMessage>" shall be raised

    Examples:
      | id  | errorMessage                              |
      | 161 | Could not find an instance with todos/161 |
      | 224 | Could not find an instance with todos/224 |
