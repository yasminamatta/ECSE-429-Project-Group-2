Feature: Post Project Relationship to Category

  As a user, I want to create a relationship between a category and a project so that I can keep track of what projects are
  related to which categories

  Background:
    Given the Todo Manager system is running normally
    Given at least one category exists in the management system
    Given at least one project exists in the management system


  # NORMAL FLOW
  Scenario Outline: Create a relationship between a category and a project successfully
    Given there exists a project with id "<projectId>" in the system that is not assigned to a category with id "<categoryId>"
    When the user makes a POST request to assign a project with id "<projectId>" to a category with id "<categoryId>"
    Then a relationship shall exist between project with id "<projectId>" and the category with id "<categoryId>"
    And no category shall be created or deleted

    Examples:
      | categoryId | projectId |
      | 1          | 1         |
      | 2          | 1         |

  # ALTERNATE FLOW
  Scenario Outline: Create a relationship between a category and a project, when the relationship already exists
    Given there is already a relationship between category with id "<categoryId>" and project with id "<projectId>"
    When the user makes a POST request to assign a project with id "<projectId>" to a category with id "<categoryId>"
    Then a relationship shall exist between project with id "<projectId>" and the category with id "<categoryId>"
    And no category shall be created or deleted

    Examples:
      | categoryId | projectId |
      | 1          | 1         |
      | 2          | 1         |

  # ERROR FLOW
  Scenario Outline: Create a relationship between a non-existent project and an existing category
    When the user makes a POST request to assign a project with id "<projectId>" to a category with id "<categoryId>"
    Then an error message with content "<error>" shall be raised by the system
    And no category shall be created or deleted

    Examples:
      | categoryId | projectId | error                                                           |
      | 1          | 2000      | Could not find thing matching value for id |
      | 1          | 435       | Could not find thing matching value for id  |

  # ERROR FLOW
  Scenario Outline: Create a relationship between a existing project and an non-existent category
    When the user makes a POST request to assign a project with id "<projectId>" to a category with id "<categoryId>"
    Then an error message with content "<error>" shall be raised by the system
    And no category shall be created or deleted

    Examples:
      | categoryId | projectId | error                                                                  |
      | 1526       | 1         | Could not find parent thing for relationship categories/1526/projects  |
      | 78220      | 1         | Could not find parent thing for relationship categories/78220/projects |