Feature: Update a project's information
    As a user, I want to update the fields in a project so that I can keep the information up to date.

    Background:
        Given the server is running
        Given at least one project exists

    # Normal flow
    Scenario: Update all fields in a project
        When the user update the project with id "<id>" title to "<newTitle>", completed to "<completed>", active to "<active>", description to "<description>" and tasks to "<tasks>"
        Then the project with th new fields should be returned

        Example:
            | id | title     | completed | active | description | tasks |
            | 1  | New Title | false     | true   |  work       | [{2}] |


    # Alternative flow
    Scenario: Update a project title
        When the user update the project with id "<id>" title to "<newTitle>"
        Then the project with the new title should be returned
        And the other fields should not change

        Example:
            | id | title     | completed | active | description | tasks |
            | 1  | New Title | false     | true   |  work       | [{2}] |

    
