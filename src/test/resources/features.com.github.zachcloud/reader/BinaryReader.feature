Feature: Test the Binary Reader

  Scenario: Test reading simple String
    Given Bytes "4D 50 51"
    When A String with 3 length is read
    Then String result will be "MPQ"

  Scenario: Test reading null terminated String
    Given Bytes "4D 50 51 00"
    When A String is read
    Then String result will be "MPQ"

  Scenario: Test finding simple String
    Given Bytes "00 4D 1A 00 4D 50 51 05"
    When String "MPQ" is found
    When A String with 3 length is read
    Then String result will be "MPQ"

  Scenario: Test reading integer
    Given Bytes "03 52 55 32"
    When A Short is read
    Then Short result will be 20995

  Scenario: Test reading short
    Given Bytes "03 52"
    When A Short is read
    Then Short result will be 20995

  Scenario: Test reading long (small)
    Given Bytes "15 00 00 00 00 00 00 00"
    When A Long is read
    Then Long result will be "21"

  Scenario: Test reading long (big)
    Given Bytes "00 00 00 00 00 00 00 15"
    When A Long is read
    Then Long result will be "1513209474796486656"

  Scenario: Test reading byte
    Given Bytes "01 00 00 00 00 00 00 15"
    When A Byte is read
    Then Byte should be 1
    When A Byte is read
    Then Byte should be 0

  Scenario: Test reading byte array
    Given Bytes "01 00 00 00 00 00 00 15"
    When A Byte Array of size 3 is read
    Then Byte Array should be "01 00 00"

  Scenario: Test reading byte array with position
    Given Bytes "01 00 00 A0 00 40 00 15"
    When We go to position 2
    When A Byte Array of size 4 is read
    Then Byte Array should be "00 A0 00 40"