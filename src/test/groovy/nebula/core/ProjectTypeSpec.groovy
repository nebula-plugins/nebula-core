package nebula.core

import nebula.test.ProjectSpec
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

    def 'multiproject identifies subprojects as leaf and top level as root and parent'() {
        def sub1 = ProjectBuilder.builder().withName('sub1').withParent(project).build()

        when:
        def multiProject = new ProjectType(project)
        def multiSub1 = new ProjectType(sub1)

        then:
        multiProject.isRootProject
        multiProject.isParentProject
        !multiProject.isLeafProject
        !multiSub1.isRootProject
        !multiSub1.isParentProject
        multiSub1.isLeafProject
    }
}
