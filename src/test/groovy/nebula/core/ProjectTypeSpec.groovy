package nebula.core

import nebula.test.ProjectSpec
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

class ProjectTypeSpec extends ProjectSpec {
    def 'single project is properly identified as a root and leaf'() {
        when:
        def singleProject = new ProjectType(project)

        then:
        singleProject.isRootProject
        singleProject.isLeafProject
        !singleProject.isParentProject
    }

    def 'single level multiproject identifies top level as root and parent'() {
        createSubproject(project, 'sub')

        when:
        def multiProject = new ProjectType(project)

        then:
        multiProject.isRootProject
        multiProject.isParentProject
        !multiProject.isLeafProject
    }

    def 'single level multiproject identifies subprojects as leaf'() {
        def sub = createSubproject(project, 'sub')

        when:
        def multiSub = new ProjectType(sub)

        then:
        !multiSub.isRootProject
        !multiSub.isParentProject
        multiSub.isLeafProject
    }

    def 'multi level multiproject identifies top level as root and parent'() {
        def mid = createSubproject(project, 'mid')
        createSubproject(mid, 'sub')

        when:
        def multiTop = new ProjectType(project)

        then:
        multiTop.isRootProject
        multiTop.isParentProject
        !multiTop.isLeafProject
    }

    def 'multi level multiproject identifies mid level as parent'() {
        def mid = createSubproject(project, 'mid')
        createSubproject(mid, 'sub')

        when:
        def multiMid = new ProjectType(mid)

        then:
        !multiMid.isRootProject
        multiMid.isParentProject
        !multiMid.isLeafProject
    }

    def 'multi level multiproject identifies sub as leaf'() {
        def mid = createSubproject(project, 'mid')
        def sub = createSubproject(mid, 'sub')

        when:
        def multiSub = new ProjectType(sub)

        then:
        !multiSub.isRootProject
        !multiSub.isParentProject
        multiSub.isLeafProject
    }

    static Project createSubproject(Project parentProject, String name) {
        ProjectBuilder.builder().withName(name).withParent(parentProject).build()
    }
}
