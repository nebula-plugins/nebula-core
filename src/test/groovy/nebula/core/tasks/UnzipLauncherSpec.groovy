package nebula.core.tasks

import nebula.test.IntegrationSpec
import nebula.test.functional.ExecutionResult
import nebula.test.functional.internal.launcherapi.LauncherExecutionResult
import spock.lang.Shared

class UnzipLauncherSpec extends IntegrationSpec {
    @Shared
    URL zipUrl = getClass().getClassLoader().getResource("test.zip");

    @Shared
    def url = zipUrl.toURI().resolve(".").toURL()

    def setup() {
        useToolingApi = false
    }

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
        def project = ((LauncherExecutionResult) result).gradle.getRootProject()
        def unzip = project.tasks.getByName('unzip')
        unzip.source
        unzip.destinationDir.exists()
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
        ExecutionResult result = runTasksSuccessfully('unzip')

        then:
        def project = result.gradle.getRootProject()
        def unzipTask = project.tasks.getByName('unzip')
        def output = unzipTask.destinationDir
        output.name == 'unzipped'

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
        def result = runTasksSuccessfully('unzip')

        then:
        def project = result.gradle.getRootProject()
        def unzipDir = new File(project.buildDir, 'unzipped')
        unzipDir

        unzipDir.listFiles().length == 1
        def firstDir = new File(unzipDir, 'test-1.0.0')
        firstDir

        def readmeFile = new File(firstDir, 'README.md')
        readmeFile
        readmeFile.text.contains("Testing")

        def unzipTask = project.tasks.getByName('unzip')
        unzipTask.firstDirectory().name == 'test-1.0.0'

    }
}
