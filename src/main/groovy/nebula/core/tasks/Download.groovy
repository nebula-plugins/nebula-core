package nebula.core.tasks

import org.gradle.api.internal.ConventionTask
import org.gradle.api.internal.file.TemporaryFileProvider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * downloadUrl or downloadBase need to be provided. downloadFileName is required if downloadBase is provided. If downloadUrl
 * is provided, then downloadFileName is optional but can be used to control the destination file name. If downloadFileName
 * isn't provided, it'll saved to a temporary directory which is not destinationDir.
 *
 * <pre>
 *     import netflix.nebula.*
 *     task download(type: Download) {
 *         downloadBase = 'http://localhost'
 *         downloadFileName = 'file.zip'
 *     }
 *     task unzip(type: Unzip) {
 *         from(tasks.download)
 *     }
 * </pre>
 */
class Download extends ConventionTask {
    @Input @Optional
    String downloadBase

    @Input @Optional
    String downloadFileName

    @Input @Optional
    File destinationDir

    @OutputFile
    File destinationFile

    String downloadUrl


    Download(TemporaryFileProvider temporaryFileProvider) {
        super()
        conventionMapping('destinationDir') {
            // Cache value back onto class, to lock it in.
            destinationDir = temporaryFileProvider.createTemporaryDirectory('download', 'tmp')
            return destinationDir
        }
        conventionMapping('destinationFile') {
            destinationFile = (getDownloadFileName()) ?
                new File(getDestinationDir(), getDownloadFileName()) :
                temporaryFileProvider.createTemporaryFile('downloaded', 'part')
            return destinationFile
        }
        conventionMapping('downloadUrl') {
            "${getDownloadBase()}/${getDownloadFileName()}"
        }
//        outputs.upToDateWhen {
//            // TODO Use httpclient or such to calculate up-to-date
//        }
    }

    @TaskAction
    doDownload() {
        // TODO Use a smarter download library like httpclient
        // TODO Show progress markers
        logger.info("Downloading ${getDownloadUrl()} to ${getDestinationFile()}")
        getDestinationFile().bytes = new URL(getDownloadUrl()).bytes
        logger.info("Downloaded ${getDestinationFile().size()} bytes")
    }

}