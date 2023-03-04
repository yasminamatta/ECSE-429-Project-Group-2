Feature: Delete a category of a todo by id
  As a user, I want to delete a category of todo so that I can assign it to another category that fits it more

  Background:
    Given the server is running
    Given atleast one todo exists in the system
    Given atleast one category exists in the system


  # Normal flow
  Scenario Outline: Delete a category of a todo by id successfully
    Given todo with id "<todoId>" is assigned to a category in the system
    When the user makes a DELETE request to delete a category with id "<categoryId>" of a todo with id "<todoId>"
    Then the relationship between the category with id "<categoryId>" and the todo with id "<todoId>" shall not exist in the system
    Then no todo item shall be created or deleted

    Examples:
      | categoryId | todoId |
      | 1          | 1      |

  # Alternative flow
  Scenario Outline: Delete a category of a todo that is not assigned to any category
    When the user makes a DELETE request to delete a category with id "<categoryId>" of a todo with id "<todoId>"
    Then an error message with content "<errorMessage>" shall be raised
    Then no todo item shall be created or deleted

    Examples:
      | categoryId | todoId | errorMessage                                           |
      | 1          | 2      | Could not find any instances with todos/2/categories/1 |

  # Error flow
  Scenario Outline: Delete a category of a todo, where todo does not exist in the system
    When the user makes a DELETE request to delete a category with id "<categoryId>" of a todo with id "<todoId>"
    Then an error message with contents "<errorMessage>" shall be raised
    Then no todo item shall be created or deleted

    Examples:
      | categoryId | todoId | errorMessage                                                                                                                       |
      | 1          | 2000   | Cannot invoke \"uk.co.compendiumdev.thingifier.core.domain.instances.ThingInstance.getRelationships()\" because \"parent\" is null |

  # Error flow
  Scenario Outline: Delete a category of a todo, where category does not exist in the system
    When the user makes a DELETE request to delete a category with id "<categoryId>" of a todo with id "<todoId>"
    Then an error message with content "<errorMessage>" shall be raised
    Then no todo item shall be created or deleted

    Examples:
      | categoryId | todoId | errorMessage                                              |
      | 1783       | 2      | Could not find any instances with todos/2/categories/1783 |


