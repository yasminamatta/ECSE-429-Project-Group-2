Feature: Create a project
  As a user, I want to create a project so that I can assign different tasks to it

  Background:
    Given the server is running


  # Normal flow
  Scenario Outline: Create a project successfully
    When the user makes POST request to create a project item with title "<title>" and description "<description>"
    Then one project item shall be created and returned

    Examples:
      | title        | description                  |
      | shred papers | shred papers into 100 pieces |
      | clean desk   | clean with lysol             |

  # Alternate flow
  Scenario Outline: Create a project with only title specified and others fields are set to their default values implicitly
    When the user makes POST request to create a project item with only title "<title>"
    Then one project item shall be created and returned
    Then description shall be set to "<description>", completed to "<completed>", active to "<active>"

    Examples:
      | title         | description | completed | active |
      | staple papers |             | false     | false  |
      | make labels   |             | false     | false  |

  # Error flow
  Scenario Outline: Create a project with id
    When the user makes POST request to create a project item with id "<id>"
    Then an error message for project with content "<errorMessage>" shall be raised

    Examples:
      | id | errorMessage                                                       |
      | 3  | Invalid Creation: Failed Validation: Not allowed to create with id |

