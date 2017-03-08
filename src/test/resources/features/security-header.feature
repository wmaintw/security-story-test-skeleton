@security-headers
Feature: Security Headers

  Scenario Outline: Security header included in the response when requesting dynamic resource
    Given web server is running on "https://www.buildsecurityin.cn"
    When the URL <targetURL> was visited
    Then the response contains "Content-Type" header with value "<expectedContentType>"
    Then the response contains "X-Content-Type-Options" header with value "NOSNIFF"
    Then the response contains "X-Frame-Options" header is either "SAMEORIGIN" or "DENY"
    Then the response contains "X-XSS-Protection" header with value "1; mode=block"
    Then the response contains "Strict-Transport-Security" header is set

  Examples:
      | targetURL                         | expectedContentType     |
      | /#customer/123                    | text/html               |
      | /assets/images/general/logo.png   | image/png               |
      | /assets/css/main.css              | text/css                |
      | /assets/js/main.js                | application/javascript  |