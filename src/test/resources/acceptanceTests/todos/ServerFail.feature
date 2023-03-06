Feature: Tests fail if server is not running
	The API calls must fail if the server is not running

  Background:
    Given the server is not running

  Scenario Outline: GET fails successfully
    When the user makes GET request to get a todo item by ID "<todoId>"
    Then response should be "<response>"

    Examples:
      | todoId | response |
      | 1      | null     |
      | 2      | null     |

  Scenario Outline: POST fails successfully
    When the user makes POST request to create a todo item with only title "<title>"
    Then response should be "<response>"

    Examples:
      | title         | response |
      | staple papers | null     |
      | make labels   | null     |


  Scenario Outline: DELETE fails successfully
    When the user makes a DELETE request to delete a category with id "<categoryId>" of a todo with id "<todoId>"
    Then response should be "<response>"

    Examples:
      | categoryId | todoId | response |
      | 1          | 1      | null     |
      | 1          | 2      | null     |

