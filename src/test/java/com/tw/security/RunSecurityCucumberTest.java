package com.tw.security;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {
                "src/test/resources/features/"
        },
        format = {"pretty", "html:build/reports/cucumber/html",
                "json:build/reports/cucumber/all_tests.json",
                "junit:build/reports/junit/all_tests.xml"},
        glue = {"com.tw.security.steps"},
        tags = {"~@skip"}
)
public class RunSecurityCucumberTest {
}
