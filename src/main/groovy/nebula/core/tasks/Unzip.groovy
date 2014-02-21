package nebula.core.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.internal.IConventionAware
import org.gradle.api.internal.file.DefaultFileOperations
import org.gradle.api.internal.file.TemporaryFileProvider
import org.gradle.api.tasks.Copy

import javax.inject.Inject

/**
 * Task to take File or Task inputs, then unzip them implicitly
 */
class Unzip extends Copy {

    DefaultFileOperations fileOperations

    @Inject
    Unzip(DefaultFileOperations fileOperations, TemporaryFileProvider temporaryFileProvider) {
        super()
        this.fileOperations = fileOperations

        // Destination dir should be set for up-to-date checks. User has to overwrite to keep using the same output
        // This is set now, but the user can easily override.
        into {
            temporaryFileProvider.createTemporaryDirectory('unzip', 'extracted')
        }

//        conventionMapping('destinationDir') {
//            destinationDir = temporaryFileProvider.createTemporaryDirectory('unzip', 'extracted')
//            return destinationDir
//        }
    }

    Unzip from(File zipFile) {
        from( fileOperations.zipTree(zipFile) )
        return this
    }

    Unzip from(DefaultTask task) {
        // We can't just pass the outputs, since we're actually going to pass a zipTree
        inputs.source(task)
        super.from {
            logger.info("Lazily pulling output from ${task} specifically ${task.outputs.files.singleFile}")
            fileOperations.zipTree(task.outputs.files.singleFile)
        }
        return this
    }

    /**
     * It's common to have a single directory in a zip file. This method extract that for the user.
     */
    File firstDirectory() {
        def files = getDestinationDir().listFiles()
        (files.length > 0)?files[0]:null
    }

    // destinationFile will be to the temporary directory, unless overridden.
}