Feature: Clock In API

  Background:
    * url baseUrl
    * def userId = 1
    * def clockInResponse = null

  Scenario: Clock in for a user
    Given path 'api', 'clock-in'
    And request { note: 'Test clock in' }
    When method POST
    Then status 200
    And match response == { id: '#number', user_id: '#(userId)', clock_in: '#string', clock_out: '#null', note: 'Test clock in' }
    And def clockInResponse = response
    And def clockInId = response.id
    
    # Clean up
    Given path 'api', 'clock-out'
    When method POST
    Then status 200

  Scenario: Clock in without note
    Given path 'api', 'clock-in'
    And request {}
    When method POST
    Then status 200
    And match response.user_id == userId
    And match response.clock_in == '#string'
    And match response.clock_out == '#null'
    
    # Clean up
    Given path 'api', 'clock-out'
    When method POST
    Then status 200
