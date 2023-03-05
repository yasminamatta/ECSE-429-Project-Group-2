Feature: Create a relationship between a todo and a project

  As a user, I want to create a relationship between a todo and a project so that I can keep track of what todos are
  related to which projects

  Background:
    Given the server is running
    Given atleast one todo exists in the system
    Given atleast one project exists in the system


  # Normal flow
  Scenario Outline: Create a relationship between a todo and a project successfully
    Given there exists todo with id "<todoId>" in the system that is not assigned project with id "<projectId>"
    When the user makes a POST request to assign a todo with id "<todoId>" to a project with id "<projectId>"
    Then a relationship named tasksof shall be created between project with id "<projectId>" and the todo with id "<todoId>"
    Then no todo item shall be created or deleted

    Examples:
      | projectId | todoId |
      | 1         | 1      |
      | 1         | 2      |

  # Alternative flow
  Scenario Outline: Create a relationship between a todo and a project, where tasksof relationship already exists between todo and project
    Given there exists a tasksof relationship between todo with id "<todoId>" and project with id "<projectId>"
    When the user makes a POST request to assign a todo with id "<todoId>" to a project with id "<projectId>"
    Then a relationship named tasksof shall exist between project with id "<projectId>" and the todo with id "<todoId>"
    Then no todo item shall be created or deleted

    Examples:
      | projectId | todoId |
      | 1         | 1      |
      | 1         | 2      |

  # Error flow
  Scenario Outline: Create a relationship between a non-existent todo and an existing project
    When the user makes a POST request to assign a todo with id "<todoId>" to a project with id "<projectId>"
    Then an error message with content "<errorMessage>" shall be raised
    Then no todo item shall be created or deleted

    Examples:
      | projectId | todoId | errorMessage                                                    |
      | 1         | 2000   | Could not find parent thing for relationship todos/2000/tasksof |
      | 1         | 435    | Could not find parent thing for relationship todos/435/tasksof  |

  # Error flow
  Scenario Outline: Create a relationship between a existing todo and an non-existent project
    When the user makes a POST request to assign a todo with id "<todoId>" to a project with id "<projectId>"
    Then an error message with content "<errorMessage>" shall be raised
    Then no todo item shall be created or deleted

    Examples:
      | projectId | todoId | errorMessage                               |
      | 1526      | 2      | Could not find thing matching value for id |
      | 78220     | 1      | Could not find thing matching value for id |


