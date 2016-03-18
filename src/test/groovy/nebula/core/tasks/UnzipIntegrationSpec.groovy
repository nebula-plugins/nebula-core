package nebula.core.tasks

import nebula.test.IntegrationSpec
import nebula.test.functional.ExecutionResult
import spock.lang.Shared

class UnzipIntegrationSpec extends IntegrationSpec {
    @Shared
    String base = 'https://bintray.com/artifact/download/nebula/gradle-plugins/com/netflix/nebula/nebula-core/3.0.1'
    String filename = 'nebula-core-3.0.1.jar'
    String url = "$base/$filename"

    def 'confirm task runs'() {
        setup:
        buildFile << """
            import nebula.core.tasks.*
            task download(type: Download) {
                downloadBase = '$base'
                downloadFileName = '$filename'
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

    def 'confirm task is up to date for a second run, when the destination is set'() {
        setup:
        buildFile << """
            import nebula.core.tasks.*
            task download(type: Download) {
                downloadBase = '$base'
                downloadFileName = '$filename'
                destinationDir = buildDir
            }
            task unzip(type: Unzip) {
                from(tasks.download)
            }
            """.stripIndent()

        when:
        runTasksSuccessfully('unzip')
        ExecutionResult result = runTasksSuccessfully('unzip')

        then:
        result.wasUpToDate(':download')
    }

    def 'destination dir can be overridden'() {
        setup:
        buildFile << """
            import nebula.core.tasks.*
            task download(type: Download) {
                downloadBase = '$base'
                downloadFileName = '$filename'
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
        destDir.listFiles().toList()*.name == ['META-INF', 'nebula']
    }

    def 'task runs'() {
        setup:
        buildFile << """
            import nebula.core.tasks.*
            task download(type: Download) {
                downloadUrl = '$url'
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

        destDir.listFiles().toList()*.name == ['META-INF', 'nebula']
        def firstDir = new File(destDir, 'META-INF')
        firstDir

        def manifestFile = new File(firstDir, 'MANIFEST.MF')
        manifestFile
        manifestFile.text.contains("Manifest-Version: 1.0")
    }
}
