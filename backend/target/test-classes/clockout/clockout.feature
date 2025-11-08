Feature: Clock Out API

  Background:
    * url baseUrl
    * def userId = 1

  Scenario: Clock out for a user with active clock in
    # First, clock in
    Given path 'api', 'clock-in'
    And request {}
    When method POST
    Then status 200
    And def clockInId = response.id
    
    # Then clock out
    Given path 'api', 'clock-out'
    When method POST
    Then status 200
    And match response == { id: '#(clockInId)', user_id: '#(userId)', clock_in: '#string', clock_out: '#string', note: '#? _ == null' }

  Scenario: Clock out without active clock in should fail
    Given path 'api', 'clock-out'
    When method POST
    Then status 404
