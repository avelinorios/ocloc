Feature: Integration Test - Complete Workflow

  Background:
    * url baseUrl
    * def userId = 1
    * call read('classpath:common/common.feature')

  Scenario: Complete workflow: clock in, work, clock out, check summary
    # Step 1: Clock in
    Given path 'api', 'clock-in'
    And request { note: 'Integration test' }
    When method POST
    Then status 200
    And def clockInTime = response.clock_in
    And def entryId = response.id
    And match response.clock_out == null
    
    # Step 2: Verify active entry exists
    Given path 'api', 'entries'
    When method GET
    Then status 200
    And def activeEntry = response.find(x => x.clock_out == null)
    And match activeEntry != null
    And match activeEntry.id == entryId
    
    # Step 3: Wait a bit (simulating work)
    * karate.pause(1000)
    
    # Step 4: Clock out
    Given path 'api', 'clock-out'
    When method POST
    Then status 200
    And match response.clock_out != null
    And match response.id == entryId
    
    # Step 5: Verify entry is closed
    Given path 'api', 'entries'
    When method GET
    Then status 200
    And def closedEntry = response.find(x => x.id == entryId)
    And match closedEntry.clock_out != null
    
    # Step 6: Check summary
    * def today = java.time.LocalDate.now().toString()
    Given path 'api', 'summary'
    And param from = today + 'T00:00:00Z'
    When method GET
    Then status 200
    And def todaySummary = response.find(x => x.date == today)
    And match todaySummary != null
    And match todaySummary.hours == '#? _ >= 0'
