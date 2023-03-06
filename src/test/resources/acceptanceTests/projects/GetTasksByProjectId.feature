Feature: Get Tasks by Project Id
  As a user, I want to get all the tasks related to a project so that I can decide what tasks need to be added and which
  ones to remove

  Background:
    Given the server is running

  # Normal flow
  Scenario Outline: Get task by project id successfully
    Given at least one project exists
    When the user makes a GET request for tasks of project with id "<id>"
    Then two tasks shall be returned

    Examples:
      | id |
      | 1  |

  # Alternative flow
  Scenario Outline: Get task by project with non-existent id
    When the user makes a GET request for tasks of project with non-existent id "<id>"
    Then an error message for project with content "<errorMessage>" shall be raised

    Examples:
      | id  | errorMessage                                 |
      | 577 | Could not find an instance with projects/577 |

    # Error flow
  Scenario Outline: Get task by project with invalid id
    When the user makes a GET request for tasks of project with invalid id "<id>"
    Then an error message for project with content "<errorMessage>" shall be raised

    Examples:
      | id         | errorMessage                                        |
      | invalid-id | Could not find an instance with projects/invalid-id |


