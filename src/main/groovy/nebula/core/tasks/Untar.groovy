package nebula.core.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.internal.file.DefaultFileOperations
import org.gradle.api.internal.file.TemporaryFileProvider
import org.gradle.api.tasks.Copy

/**
 * Task to take File or Task inputs, then untar them implicitly
 */
class Untar extends Copy {

    DefaultFileOperations fileOperations

    Untar(DefaultFileOperations fileOperations, TemporaryFileProvider temporaryFileProvider) {
        super()
        this.fileOperations = fileOperations
//        conventionMapping('destinationDir') {
//            temporaryFileProvider.createTemporaryDirectory('untar', 'extracted')
//        }

        // Destination dir should be set for up-to-date checks. User can overwrite.
        into { temporaryFileProvider.createTemporaryDirectory('untar', 'extracted') }
    }

    Untar from(File tarFile) {
        from( fileOperations.tarTree(tarFile) )
        return this
    }

    Untar from(DefaultTask task) {
        // We can't just pass the outputs, since we're actually going to pass a tarTree
        inputs.source(task)
        super.from {
            logger.info("Lazily pulling output from ${task} specifically ${task.outputs.files.singleFile}")
            fileOperations.tarTree(task.outputs.files.singleFile)
        }
        return this
    }

    /**
     * It's common to have a single directory in a tar file. This method extract that for the user.
     */
    File firstDirectory() {
        def files = getDestinationDir().listFiles()
        (files.length > 0)?files[0]:null
    }

    // destinationFile will be to the temporary directory, unless overridden.
}