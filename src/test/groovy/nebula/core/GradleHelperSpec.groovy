package nebula.core

import com.google.common.io.Files
import org.gradle.api.internal.project.DefaultProject
import spock.lang.Specification

class GradleHelperSpec extends Specification {
    GradleHelper gradleHelper

    def setup() {
        def project = Stub(DefaultProject) {
            getBuildDir() >> Files.createTempDir()
        }
        project.getBuildDir() >> Files.createTempDir()
        gradleHelper = new GradleHelper(project)
    }

    def 'can create temp directory'() {
        when:
        def tmpDir = gradleHelper.getTempDir('for-unit-test')

        then:
        //1 * project.getBuildDir()
        tmpDir != null
        tmpDir.isDirectory()
        tmpDir.canWrite()
    }

}
