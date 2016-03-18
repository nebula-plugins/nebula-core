package nebula.core.tasks

import org.apache.http.HttpStatus
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.gradle.api.internal.ConventionTask
import org.gradle.api.internal.file.TemporaryFileProvider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

/**
 * downloadUrl or downloadBase need to be provided. downloadFileName is required if downloadBase is provided. If downloadUrl
 * is provided, then downloadFileName is optional but can be used to control the destination file name. If downloadFileName
 * isn't provided, it'll saved to a temporary directory which is not destinationDir.
 *
 * <pre>
 *     import netflix.nebula.*
 *     task download(type: Download) {*         downloadBase = 'http://localhost'
 *         downloadFileName = 'file.zip'
 *}*     task unzip(type: Unzip) {*         from(tasks.download)
 *}* </pre>
 */
class Download extends ConventionTask {
    @Input
    @Optional
    String downloadBase

    @Input
    @Optional
    String downloadFileName

    @Input
    @Optional
    File destinationDir

    @OutputFile
    File destinationFile

    String downloadUrl

    private CloseableHttpClient httpClient
    private File cacheDir

    @Inject
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
            "${getDownloadBase()}/${getDownloadFileName()}".toString() // Can't return a GString
        }

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(30000)
                .setSocketTimeout(30000)
                .build()

        httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build()
    }

    @TaskAction
    doDownload() {
        logger.info("Downloading ${getDownloadUrl()} to ${getDestinationFile()}")
        def httpGet = new HttpGet(getDownloadUrl())
        CloseableHttpResponse response = httpClient.execute(httpGet)
        if (response.statusLine.statusCode != HttpStatus.SC_OK) {
            throw new IllegalStateException("Download of ${getDownloadUrl()} failed: $response.statusLine")
        }
        try {
            response.entity.writeTo(new FileOutputStream(getDestinationFile()))
        } finally {
            response.close()
        }
    }
}
