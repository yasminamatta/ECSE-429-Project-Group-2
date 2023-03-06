Feature: Post Category
  As a user, I want to create a new category with a body containing a specified set of parameters

  Background:
    Given the server is running
    Given more than one category exists in the system

    # NORMAL FLOW
  Scenario Outline: Create a category successfully
    When the user creates a category with title "<title>" and description "<description>"
    Then one category is created and returned
    And the category shall have the title "<title>" and description "<description>"

    Examples:
      | title      | description        |
      | School     | McGill University  |
      | Restaurant | Cheffing things up |

    # ALTERNATE FLOW
  Scenario Outline: Create a category with only a title specified and the rest of the parameters default
    When the user creates a category with title "<title>" and description "<description>"
    Then one category is created and returned
    And the category shall have the title "<title>" and description "<description>"

    Examples:
      | title   | description |
      | Office2 |             |

    # ERROR FLOW
  Scenario Outline: Create a category without specifying a title
    When the user creates a category with title "<title>" and description "<description>"
    Then a category is not created
    Then the error message "<error>" is raised by the system

    Examples:
      | title | description | error                                       |
      |       | desc1       | Failed Validation: title : can not be empty |


