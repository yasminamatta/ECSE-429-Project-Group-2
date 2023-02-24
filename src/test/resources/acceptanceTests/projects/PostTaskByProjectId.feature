Feature: Post Task by Project Id
    As a user, I want to link a task to a project so the project can be tracked

    Background:
        Given the server is running
        Given at least one project exists
        Given at least one task exists

    # Normal flow
    Scenario: Post task by project id succesfully using an existing task id
        When the user makes a POST request to "/projects/<id>/tasks" with a body containing the id "<id>" of the task desired
        Then the task shall appear in the task list of the project
        Then the return code shall be 201
    
    # Alternative flow
    Scenario: Post task by project id succesfully using an existing task title
        When the user makes a GET request to "/projects/<id>/tasks" with a body containing the title "<title>" of the task desired
        Then the task shall appear in the task list of the project
        Then the return code shall be 201

    # Error flow
    Scenario: Post task by project id succesfully using an non-existing task id
        When the user makes a POST request to "/projects/<id>/tasks" with a body containing a non-existing id "<id>"
        Then the task shall not appear in the task list of the project
        Then an error message with content "<errorMessage>" shall be returned
        Then the return code shall be 404

        Example:
            | id | title | errorMessage                               |
            | 0  | task1 | Could not find thing matching value for id |


