Feature: Get categories by project id
    As a user, I want to get the categories that are linked to a project

    Background:
        Given the server is running
        Given at least one project exists
        Given at least one category exists

    # Normal flow
    Scenario: Get categories by project id succesfully and a category is linked to the project
        When the user makes a GET request to "/projects/<id>/categories"
        Then a non-empty array of categories shall be returned

        Example:
            | id | title  | description  |
            | 1  | office | office stuff |
    
    # Alternative flow
    Scenario: Get categories by project id succesfully and categories is not linked to the project
        When the user makes a GET request to "/projects/<id>/categories"
        Then an empty array shall be returned

    # Error flow
    Scenario: Get categories by project id where the project does not exist
        When the user makes a GET request to "/projects/<id>/categories"
        Then an array with all existing categories shall be returned
        Example:
            | id | title  | description  |
            | 1  | office | office stuff |


