Feature: Get project by id
  As a user, I want to get a project by id so that I can assign tasks to it

  Background:
    Given the server is running
    Given atleast one project exists in the system

  # Normal flow
  Scenario Outline: Get a project by id successfully
    When the user makes GET request to get a project item by ID "<id>"
    Then one project item shall be returned
    Then the project shall have id "<id>", title "<title>", completed "<completed>", active "<active>"

    Examples:
      | id | title       | completed | active |
      | 1  | Office Work | false     | false  |

  # Alternative flow
  Scenario Outline: Get a project by non-existing id
    When the user makes GET request to get a project item by ID "<id>"
    Then an error message for project with content "<errorMessage>" shall be raised

    Examples:
      | id  | errorMessage                                 |
      | 161 | Could not find an instance with projects/161 |
      | 224 | Could not find an instance with projects/224 |

  # Error flow
  Scenario Outline: Get a project by invalid id
    When the user makes GET request to get a project item by ID "<id>"
    Then an error message for project with content "<errorMessage>" shall be raised

    Examples:
      | id         | errorMessage                                        |
      | invalid-id | Could not find an instance with projects/invalid-id |
