package com.novoda.staticanalysis.internal.lint

import com.novoda.test.Fixtures
import com.novoda.test.TestAndroidProject
import com.novoda.test.TestProject
import org.junit.Test

import static com.novoda.test.LogsSubject.assertThat

class LintIntegrationTest {

    private static GString LINT_CONFIGURATION =
            """
        lintOptions {              
            lintConfig = file("${Fixtures.Lint.RULES}") 
        }
        """

    @Test
    void shouldFailBuildWhenLintErrorsOverTheThresholds() {
        def result = createAndroidProjectWith(Fixtures.Lint.SOURCES_WITH_ERRORS, 0, 0)
                .buildAndFail('check')

        assertThat(result.logs).containsLintViolations(1, 0, 'reports/lint-results.html')
    }

    @Test
    void shouldNotFailBuildWhenLintErrorsWithinTheThresholds() {
        def result = createAndroidProjectWith(Fixtures.Lint.SOURCES_WITH_ERRORS, 0, 1)
                .build('check')

        assertThat(result.logs).doesNotContainLimitExceeded()
    }

    @Test
    void shouldFailBuildWhenLintWarningsOverTheThresholds() {
        def result = createAndroidProjectWith(Fixtures.Lint.SOURCES_WITH_WARNINGS, 0, 0)
                .buildAndFail('check')

        assertThat(result.logs).containsLintViolations(0, 1, 'reports/lint-results.html')
    }

    @Test
    void shouldNotFailBuildWhenLintWarningsWithinTheThresholds() {
        def result = createAndroidProjectWith(Fixtures.Lint.SOURCES_WITH_WARNINGS, 1, 0)
                .build('check')

        assertThat(result.logs).doesNotContainLimitExceeded()
    }

    private static TestProject createAndroidProjectWith(File sources, int maxWarnings = 0, int maxErrors = 0) {
        def testProject = new TestAndroidProject()
                .withSourceSet('main', sources)
                .withPenalty("""{
                    maxWarnings = ${maxWarnings}
                    maxErrors = ${maxErrors}
                }""")

        testProject.withToolsConfig(LINT_CONFIGURATION)
    }

}
