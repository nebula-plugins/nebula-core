package nebula.core

import spock.lang.Specification

import java.util.jar.Manifest

class ClassHelperSpec extends Specification {

    def 'read manifest'() {
        when:
        Manifest manifest = ClassHelper.findManifest( getClass() )

        then:
        manifest != null
    }
}
