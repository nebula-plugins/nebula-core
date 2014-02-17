package nebula.core.tasks

import nebula.test.ProjectSpec

class UntarProjectSpec extends ProjectSpec {
    def 'create task'() {
        when:
        def downloadTask = project.task([type:Download], 'download')
        def uploadTask = (Untar) project.task([type:Untar], 'untar')
        uploadTask.from(downloadTask)

        then:
        uploadTask.inputs.hasInputs
        !uploadTask.outputs.files.isEmpty()
    }
}
