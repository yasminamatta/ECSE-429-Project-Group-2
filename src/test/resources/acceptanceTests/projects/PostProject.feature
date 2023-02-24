Feature: Create a project
    As a user, I want to create a project so that I can link it to tasks and categories

    Background:
        Given the server is running

    # Normal flow
    Scenario: Create a project
        When the user makes a query to create a project item with title "<title>", completed "<completed>", active "<active>", description "<description>", tasks "<tasks>"
        Then one project item shall be created and returned

        Examples:
            | title | completed | active | description | tasks     |
            | foo   | true      | false  | bar         | [{1}]     |

    # Alternative flow
    Scenario: Create a project with only title and other attributes are set to their defauly values 
        When the user makes a query to create a project item with title "<title>"
        Then one project item shall be created and returned
        And completed should be set to "<completed>", active "<active>", description "<description>", tasks "<tasks>"

        Examples:
            | title      | completed | active | description | tasks  |
            |   study    | false     | false  |             |        |

    # Error flow
    Scenario: Create a project by providing an id
        When the user create a project using a body with id "<id>"
        Then the project should not be created
        Then an error message with content "<errorMessage>" should be returned

    Example:
            | id | errorMessage                                                       |
            | 1  | Invalid Creation: Failed Validation: Not allowed to create with id |

