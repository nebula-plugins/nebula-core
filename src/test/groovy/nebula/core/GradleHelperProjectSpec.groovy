package nebula.core

import nebula.test.ProjectSpec

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class GradleHelperProjectSpec extends ProjectSpec {
    def 'able to hijack afterEvaluate'() {
        def helper = new GradleHelper(project)
        def counter = new AtomicInteger(0)
        def ran = new AtomicBoolean(false)
        when:
        helper.beforeEvaluate {
            counter.set(1)
            ran.set(true)
        }
        project.afterEvaluate {
            counter.set(2)
        }
        project.evaluate()

        then:
        counter.get() == 2
        ran.get() == true
    }

    def 'able to hijack afterEvaluate afterwards'() {
        def helper = new GradleHelper(project)
        def counter = new AtomicInteger(0)
        def ran = new AtomicBoolean(false)
        when:
        project.afterEvaluate {
            counter.set(2)
        }
        helper.beforeEvaluate {
            counter.set(1)
            ran.set(true)
        }
        project.evaluate()

        then:
        counter.get() == 2
        ran.get() == true
    }
}
