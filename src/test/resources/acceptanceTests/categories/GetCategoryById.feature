Feature: Get Category by ID
  As a user, I want to get a category by ID so that I can access the contents of the specific category.

  Background:
    Given the Todo Manager system is running
    Given at least one category exists in the system

  Scenario Outline: Get category successfully
    When the user initiates the query of the category with ID "<id>"
    Then one category shall be returned
    Then the category shall have title "<title>" and description "<description>"

    Examples:
      | id | title  | description |
      | 1  | Office |             |
      | 2  | Home   |             |

  Scenario Outline: Get a category using a non existent ID
    When the user initiates the query of the category with ID "<id>"
    Then no category shall be returned
    Then the error message "<error>" shall be raised

    Examples:
      | id  | error                                          |
      | 100 | Could not find an instance with categories/100 |

  Scenario Outline: Get a category using an invalid parameter as ID
    When the user initiates the query of the category with ID "<id>"
    Then no category shall be returned
    Then the error message "<error>" shall be raised

    Examples:
      | id | error                                         |
      | -1 | Could not find an instance with categories/-1 |
