Feature: Post Categories by Project Id
    As a user, I want to link a category to a project so the project can be tracked

    Background:
        Given the server is running
        Given at least one project exists
        Given at least one category exists

    # Normal flow
    Scenario: Post category by project id succesfully using an existing category id
        When the user makes a POST request to "/projects/<id>/categories" with a body containing the id "<id>" of the task desired
        Then the category shall appear in the category list of the project
        Then the return code shall be 201

    # Alternative flow
    Scenario: Post category by project id succesfully using an existing category title
        When the user makes a GET request to "/projects/<id>/categories" with a body containing the title "<title>" of the task desired
        Then the categories shall appear in the categories list of the project
        Then the return code shall be 201

    # Error flow
    Scenario: Post category by project id succesfully using an non-existing category id
        When the user makes a POST request to "/projects/<id>/categories" with a body containing a non-existing id "<id>"
        Then the category shall not appear in the category list of the project
        Then an error message with content "<errorMessage>" shall be returned
        Then the return code shall be 404

    Example:
            | id | title     | errorMessage                               |
            | 0  | category1 | Could not find thing matching value for id |


