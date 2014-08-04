package nebula.core.tasks

import nebula.test.IntegrationSpec
import nebula.test.functional.ExecutionResult
import spock.lang.Shared

class UnzipIntegrationSpec extends IntegrationSpec {
    @Shared
    URL zipUrl = getClass().getClassLoader().getResource("test.zip");

    @Shared
    def url = zipUrl.toURI().resolve(".").toURL()

    def 'confirm task runs'() {

        setup:
        buildFile << """
            import nebula.core.tasks.*
            task download(type: Download) {
                downloadBase = "${url.toExternalForm()}"
                downloadFileName = 'test.zip'
            }
            task unzip(type: Unzip) {
                from(tasks.download)
            }
            """.stripIndent()

        when:
        ExecutionResult result = runTasksSuccessfully('unzip')

        then:
        result.wasExecuted(':download')
    }

    def 'destination dir can be overridden'() {
        setup:
        buildFile << """
            import nebula.core.tasks.*
            task download(type: Download) {
                downloadBase = "${url.toExternalForm()}"
                downloadFileName = 'test.zip'
            }
            task unzip(type: Unzip) {
                into( new File(buildDir, 'unzipped') )
                from(tasks.download)
            }
            """.stripIndent()

        when:
        runTasksSuccessfully('unzip')

        then:
        File buildDir = new File(projectDir, 'build')
        File destDir = new File(buildDir, 'unzipped')
        destDir.exists()
        destDir.listFiles().toList()*.name == ['test-1.0.0']
    }

    def 'task runs'() {
        setup:
        buildFile << """
            import nebula.core.tasks.*
            task download(type: Download) {
                downloadUrl = "${zipUrl.toExternalForm()}"
            }
            task unzip(type: Unzip) {
                into( new File(buildDir, 'unzipped') )
                from(tasks.download)
            }
            """.stripIndent()

        when:
        runTasksSuccessfully('unzip')

        then:
        File buildDir = new File(projectDir, 'build')
        File destDir = new File(buildDir, 'unzipped')
        destDir.exists()

        destDir.listFiles().toList()*.name == ['test-1.0.0']
        def firstDir = new File(destDir, 'test-1.0.0')
        firstDir

        def readmeFile = new File(firstDir, 'README.md')
        readmeFile
        readmeFile.text.contains("Testing")
    }
}
