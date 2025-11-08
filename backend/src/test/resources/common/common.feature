Feature: Common utilities for Karate tests

  Background:
    * def getToday = function() { return java.time.LocalDate.now().toString() }
    * def getTomorrow = function() { return java.time.LocalDate.now().plusDays(1).toString() }

