Feature: Users API

  Background:
    * url baseUrl
    * def userId = null

  Scenario: Create a new user
    Given path 'api', 'users'
    And request { name: 'Test User', email: 'test@example.com' }
    When method POST
    Then status 200
    And match response == { id: '#number', name: 'Test User', email: 'test@example.com' }
    And def userId = response.id

  Scenario: Get user by id
    Given path 'api', 'users', userId != null ? userId : 1
    When method GET
    Then status 200
    And match response.id == '#number'
    And match response.name == '#string'
    And match response.email == '#string'

  Scenario: Create user with duplicate email should fail
    Given path 'api', 'users'
    And request { name: 'Another User', email: 'test@example.com' }
    When method POST
    Then status 400

