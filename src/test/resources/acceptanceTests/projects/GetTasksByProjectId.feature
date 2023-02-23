Feature: Get tasks by project id
    As a user, I want to get all tasks related to a project so that I can see what tasks are assigned to a project

    Background: 
        Given the server is running
        Given at least one project exists
        Given at least one task exists

    # Normal flow
    Scenario: Get tasks by project id where there is a link between the project and the task
        When the user requests tasks by project "<id>"
        Then an array of tasks is returned
        
        Example:
            | id | title            | doneStatus | description | categories | tasksof |
            | 1  | scan paper work  |  false     |             |  [{1}]     | [{2}]   |

    # Error flow
    Scenario: Get tasks by project id where the project does not exist
        When the user requests tasks by project "<id>"
        Then an array with all existing todos is returned
        
        Example:
            | id | title            | doneStatus | description | categories | tasksof |
            | 1  | scan paper work  |  false     |             |  [{1}]     | [{2}]   |
        