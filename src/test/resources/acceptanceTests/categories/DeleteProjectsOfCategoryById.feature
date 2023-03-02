Feature: Delete Project related to Category

  As a user, I want to delete a relationship between a category and a project so that I can manually change
  what projects are related to which categories

  Background:
    Given the server is running normally
    Given at least one category exists on the server
    Given at least one project exists on the server

   # Normal flow
  Scenario Outline: Delete a project of a category by id successfully
    Given project with id "<projectId>" is assigned to a category with id "<categoryId>" in the system
    When the user requests to delete a project with id "<projectId>" of a category with id "<categoryId>"
    Then the relationship between the project with id "<projectId>" and the category with id "<categoryId>" shall not exist in the system
    And no category shall be created nor deleted

    Examples:
      | categoryId | projectId |
      | 1          | 1         |

  # Alternative flow
  Scenario Outline: Delete a project from a category when not assigned to any category
    When the user requests to delete a project with id "<projectId>" of a category with id "<categoryId>"
    Then an error with content "<errorMessage>" shall be raised
    And no category shall be created nor deleted

    Examples:
      | categoryId | projectId | errorMessage                                              |
      | 2          | 1         | Could not find any instances with categories/2/projects/1 |

  # Error flow
  Scenario Outline: Delete a project of a category, where project does not exist in the system
    When the user requests to delete a project with id "<projectId>" of a category with id "<categoryId>"
    Then an error with content "<errorMessage>" shall be raised
    And no category shall be created nor deleted

    Examples:
      | categoryId | projectId | errorMessage                                                 |
      | 1          | 2000      | Could not find any instances with categories/1/projects/2000 |
