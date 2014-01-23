package nebula.core.tasks

import nebula.test.IntegrationSpec

class UnzipLauncherSpec extends IntegrationSpec {

    def 'confirm task exists'() {
        setup:
        buildFile << '''
import nebula.core.tasks.*
task download(type: Download) {
    downloadBase = 'http://localhost'
    downloadFileName = 'file.zip'
}
task unzip(type: Unzip) {
    from(tasks.download)
}
'''

        when:
        def result = analyze('unzip')

        then:
        def project = result.gradle.getRootProject()
        result.gradle.taskGraph.hasTask(':download')
        def unzip = (Unzip) project.tasks.getByName('unzip')
        unzip.source
    }

    def 'confirm task runs'() {
        setup:
        URL url = getClass().getClassLoader().getResource("test.zip");
        buildFile << """
import nebula.core.tasks.*
task download(type: Download) {
    downloadBase = "${url.toExternalForm()}"
    downloadFileName = 'test.zip'
}
task unzip(type: Unzip) {
    from(tasks.download)
}
"""

        when:
        def result = analyze('unzip')

        then:
        def project = result.gradle.getRootProject()
        Unzip unzipTask = (Unzip) project.tasks.getByName('unzip')
        def output = unzipTask.destinationDir
        output.exists()
    }

    def 'destination dir can be overridden'() {
        setup:
        buildFile << '''
import nebula.core.tasks.*
task download(type: Download) {
    downloadBase = "http://localhost"
    downloadFileName = 'test.zip'
}
task unzip(type: Unzip) {
    into( new File(buildDir, 'unzipped') )
    from(tasks.download)
}
'''

        when:
        def result = analyze('unzip')

        then:
        def project = result.gradle.getRootProject()
        Unzip unzipTask = (Unzip) project.tasks.getByName('unzip')
        def output = unzipTask.destinationDir
        output.name == 'unzipped'

    }

    def 'task runs'() {
        setup:
        URL url = getClass().getClassLoader().getResource("test.zip");
        buildFile << """
import nebula.core.tasks.*
task download(type: Download) {
    downloadUrl = "${url.toExternalForm()}"
    downloadFileName = 'file.zip'
}
task unzip(type: Unzip) {
    into( new File(buildDir, 'unzipped') )
    from(tasks.download)
}
"""

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

        Unzip unzipTask = (Unzip) project.tasks.getByName('unzip')
        unzipTask.firstDirectory().name == 'test-1.0.0'

    }
}
