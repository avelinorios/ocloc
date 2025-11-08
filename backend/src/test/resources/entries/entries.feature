Feature: Entries API

  Background:
    * url baseUrl
    * def userId = 1
    * call read('classpath:common/common.feature')

  Scenario: Get entries for a user
    Given path 'api', 'entries'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[0] == { id: '#number', user_id: '#(userId)', clock_in: '#string', clock_out: '#?', note: '#?' }

  Scenario: Get entries with date range
    * def today = java.time.LocalDate.now().toString()
    * def tomorrow = java.time.LocalDate.now().plusDays(1).toString()
    Given path 'api', 'entries'
    And param from = today + 'T00:00:00Z'
    And param to = tomorrow + 'T00:00:00Z'
    When method GET
    Then status 200
    And match response == '#array'

  Scenario: Get entries for user with clock in and clock out
    # Create a complete entry
    Given path 'api', 'clock-in'
    And request { note: 'Test entry' }
    When method POST
    Then status 200
    And def entryId = response.id
    
    # Wait a bit
    * karate.pause(1000)
    
    # Clock out
    Given path 'api', 'clock-out'
    When method POST
    Then status 200
    
    # Get entries
    Given path 'api', 'entries'
    When method GET
    Then status 200
    And match response == '#array'
    And def testEntry = response.find(x => x.id == entryId)
    And match testEntry.clock_out != null
