Feature: Get project by id
    As a user, I want to get a project by id so that I can see the details of the project

    Background:
        Given the server is running
        Given at least one project exists

    # Normal Flow
    Scenario Outline: Get project by id successfully
        When the user makes a GET request to /projects/"<id>"
        Then one project shall be returned
        Then the project shall have the following properties:
            | id   | name   | description   | completed   | active   | tasks   |
            | <id> | <name> | <description> | <completed> | <active> | <tasks> |

        Examples:
            | id | name | description | completed | active | tasks     |
            | 1  | A    | A           | false     | true   | [{1}]     |
            | 2  | B    | B           | true      | false  | [{2}]     |

        # Error Flow
        Scenario: Get project by id that does not exist
            When the user makes a GET request to /projects/"<id>"
            Then no project shall be returned
            Then an error message with content "<errorMessage>" shall be returned

            Examples:
                | id  | errorMessage                               |
                | 12  | Could not find an instance with projects/12 |

