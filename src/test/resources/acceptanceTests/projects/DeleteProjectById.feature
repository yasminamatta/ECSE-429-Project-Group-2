Feature: Delete project by ID
  As a user, I want to delete a project so that I can get rid of the projects that are completed

  Background:
    Given the server is running

  # Normal flow
  Scenario Outline: Delete project id successfully
    Given at least one project exists
    When the user makes a DELETE request for project with id "<id>"
    Then the project with id "<id>" shall not exist

    Examples:
      | id |
      | 1  |

  # Alternative flow
  Scenario Outline: Delete project with non-existent id
    When the user makes a DELETE request for project with non-existent id "<id>"
    Then an error message for project with content "<errorMessage>" shall be raised

    Examples:
      | id  | errorMessage                                 |
      | 577 | Could not find any instances with projects/577 |

    # Error flow
  Scenario Outline: Get task by project with invalid id
    When the user makes a DELETE request for project with invalid id "<id>"
    Then an error message for project with content "<errorMessage>" shall be raised

    Examples:
      | id         | errorMessage                                        |
      | invalid-id | Could not find any instances with projects/invalid-id |


