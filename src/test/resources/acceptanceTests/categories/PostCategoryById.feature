Feature: Post Category by ID
  As a user, I want to POST a category by ID so that I can edit the body of a specific category.

  Background:
    Given the Todo Manager system runs
    Given a minimum of one category exists in the system

    # NORMAL FLOW
  Scenario Outline: Post a category with ID successfully
    When the user posts a category with ID "<id>", title "<title>" and description "<description>"
    Then the category shall be returned with ID "<id>", title "<title>" and description "<description>"

    Examples:
      | id | title  | description |
      | 1  | Office | desc1       |
      | 2  | Home   | desc2       |

    # ERROR FLOW
  Scenario Outline: Post a category using a non existent ID
    When the user posts a category with invalid ID "<id>", title "<title>" and description "<description>"
    Then no category is returned
    Then the error message "<error>" is raised

    Examples:
      | id  | title  | description | error                                                      |
      | 100 | Office | desc1       | No such category entity instance with GUID or ID 100 found |

    # ALTERNATE ERROR FLOW
  Scenario Outline: Post a category using an invalid parameter as ID
    When the user posts a category with invalid ID "<id>", title "<title>" and description "<description>"
    Then no category is returned
    Then the error message "<error>" is raised

    Examples:
      | id | title  | description | error                                                     |
      | -1 | Office | desc1       | No such category entity instance with GUID or ID -1 found |
