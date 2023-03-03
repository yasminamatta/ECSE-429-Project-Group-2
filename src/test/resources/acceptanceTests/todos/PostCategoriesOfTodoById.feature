Feature: Create a relationship between a todo and a category

  As a user, I want to create a relationship between a todo and a category so that I can categorize the todos in the
  system

  Background:
    Given the server is running
    Given atleast one todo exists in the system
    Given atleast one category exists in the system


  # Normal flow
  Scenario Outline: Create a relationship between a todo and a category successfully
    Given there exists todo with id "<todoId>" in the system that is not assigned to any category
    When the user makes a POST request to assign a todo with id "<todoId>" to a category with id "<categoryId>"
    Then a relationship named categories shall be created between category with id "<categoryId>" and the todo with id "<todoId>"
    Then no todo item shall be created or deleted

    Examples:
      | categoryId | todoId |
      | 1          | 2      |

  # Alternative flow
  Scenario Outline: Create a relationship between a todo and a project, where categories relationship already exists between todo and project
    Given there exists a categories relationship between todo with id "<todoId>" and a category
    When the user makes a POST request to assign a todo with id "<todoId>" to a category with id "<categoryId>"
    Then a relationship named categories shall exist between category with id "<categoryId>" and the todo with id "<todoId>"
    Then no todo item shall be created or deleted

    Examples:
      | categoryId | todoId |
      | 1          | 1      |
      | 1          | 2      |

  # Error flow
  Scenario Outline: Create a relationship between a non-existent todo and an existing category
    When the user makes a POST request to assign a todo with id "<todoId>" to a category with id "<categoryId>"
    Then an error message with content "<errorMessage>" shall be raised
    Then no todo item shall be created or deleted

    Examples:
      | categoryId | todoId | errorMessage                                                       |
      | 1          | 2080   | Could not find parent thing for relationship todos/2080/categories |
      | 1          | 435    | Could not find parent thing for relationship todos/435/categories  |

  # Error flow
  Scenario Outline: Create a relationship between a existing todo and an non-existent category
    When the user makes a POST request to assign a todo with id "<todoId>" to a category with id "<categoryId>"
    Then an error message with content "<errorMessage>" shall be raised
    Then no todo item shall be created or deleted

    Examples:
      | categoriesId | todoId | errorMessage                               |
      | 6526         | 2      | Could not find thing matching value for id |
      | 7860         | 1      | Could not find thing matching value for id |


