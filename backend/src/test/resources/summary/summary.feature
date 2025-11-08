Feature: Summary API

  Background:
    * url baseUrl
    * def userId = 1
    * call read('classpath:common/common.feature')

  Scenario: Get summary for a user
    Given path 'api', 'summary'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[0] == { date: '#string', hours: '#number' }

  Scenario: Get summary with date range
    * def today = java.time.LocalDate.now().toString()
    * def tomorrow = java.time.LocalDate.now().plusDays(1).toString()
    Given path 'api', 'summary'
    And param from = today + 'T00:00:00Z'
    And param to = tomorrow + 'T00:00:00Z'
    When method GET
    Then status 200
    And match response == '#array'

  Scenario: Get summary after clock in and clock out
    # Clock in
    Given path 'api', 'clock-in'
    And request {}
    When method POST
    Then status 200
    
    # Wait a bit to have some hours worked
    * karate.pause(2000)
    
    # Clock out
    Given path 'api', 'clock-out'
    When method POST
    Then status 200
    
    # Get summary
    * def today = java.time.LocalDate.now().toString()
    Given path 'api', 'summary'
    And param from = today + 'T00:00:00Z'
    When method GET
    Then status 200
    And match response == '#array'
    And def todaySummary = response.find(x => x.date == today)
    And match todaySummary != null
    And match todaySummary.hours == '#? _ >= 0'
