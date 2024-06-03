import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.GradleBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2024.03"

project {

    vcsRoot(HttpsGithubComFrank89722JourneyMapIntegrationRefsHeadsMain)
    vcsRoot(HttpsGithubComFrank89722JourneyMapIntegrationGitRefsHeadsMain1)
    vcsRoot(HttpsGithubComFrank89722JourneyMapIntegrationGitRefsHeadsMain2)

    buildType(Release_old)
    buildType(Build)
    buildType(Release)
}

object Build : BuildType({
    name = "Build"

    vcs {
        root(HttpsGithubComFrank89722JourneyMapIntegrationRefsHeadsMain)
    }

    steps {
        gradle {
            id = "gradle_runner"
            tasks = "clean build"
            gradleWrapperPath = ""
        }
    }

    features {
        perfmon {
        }
    }
})

object Release : BuildType({
    name = "Build and release (legacy)"
    description = "Before MC 1.21"

    enablePersonalBuilds = false
    type = BuildTypeSettings.Type.DEPLOYMENT
    maxRunningBuilds = 1

    params {
        param("env.RUN_NUMBER", "%build.counter%")
        password("env.MODRINTH_TOKEN", "credentialsJSON:717b932c-6ced-49fd-8ad2-62e7adfa76e0", display = ParameterDisplay.HIDDEN)
        password("env.CF_TOKEN", "credentialsJSON:67886215-1e4c-43e7-9e83-202a2e72eaca", display = ParameterDisplay.HIDDEN)
    }

    vcs {
        root(HttpsGithubComFrank89722JourneyMapIntegrationGitRefsHeadsMain2)
    }

    steps {
        gradle {
            name = "Build"
            tasks = "build modrinth"
            enableStacktrace = true
            jdkHome = "%env.JDK_17_0_ARM64%"
            dockerImagePlatform = GradleBuildStep.ImagePlatform.Linux
        }
    }

    features {
        perfmon {
        }
        commitStatusPublisher {
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "credentialsJSON:6e15a3dd-5d9c-484b-a3be-6c8aeaedb371"
                }
            }
        }
    }
})

object Release_old : BuildType({
    name = "Release"

    enablePersonalBuilds = false
    type = BuildTypeSettings.Type.DEPLOYMENT
    maxRunningBuilds = 1

    params {
        param("env.RUN_NUMBER", "%build.counter%")
        password("env.MODRINTH_TOKEN", "credentialsJSON:717b932c-6ced-49fd-8ad2-62e7adfa76e0", display = ParameterDisplay.HIDDEN)
        password("env.CF_TOKEN", "credentialsJSON:67886215-1e4c-43e7-9e83-202a2e72eaca", display = ParameterDisplay.HIDDEN)
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        gradle {
            name = "Upload to Modrinth"
            executionMode = BuildStep.ExecutionMode.RUN_ON_SUCCESS
            tasks = "modrinth"
            enableStacktrace = true
            jdkHome = "%env.JDK_17_0_ARM64%"
        }
        gradle {
            name = "Upload to Curseforge"
            id = "Upload_to_Curseforge"
            executionMode = BuildStep.ExecutionMode.RUN_ON_SUCCESS
            tasks = "curseforge"
            jdkHome = "%env.JDK_17_0_ARM64%"
        }
    }

    features {
        perfmon {
        }
        commitStatusPublisher {
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "credentialsJSON:6e15a3dd-5d9c-484b-a3be-6c8aeaedb371"
                }
            }
        }
    }
})

object HttpsGithubComFrank89722JourneyMapIntegrationGitRefsHeadsMain1 : GitVcsRoot({
    name = "https://github.com/frank89722/JourneyMapIntegration.git#refs/heads/main (1)"
    url = "https://github.com/frank89722/JourneyMapIntegration.git"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "frank89722"
        password = "credentialsJSON:6e15a3dd-5d9c-484b-a3be-6c8aeaedb371"
    }
})

object HttpsGithubComFrank89722JourneyMapIntegrationGitRefsHeadsMain2 : GitVcsRoot({
    name = "https://github.com/frank89722/JourneyMapIntegration.git#refs/heads/main (2)"
    url = "https://github.com/frank89722/JourneyMapIntegration.git"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "frank89722"
        password = "credentialsJSON:6e15a3dd-5d9c-484b-a3be-6c8aeaedb371"
    }
})

object HttpsGithubComFrank89722JourneyMapIntegrationRefsHeadsMain : GitVcsRoot({
    name = "https://github.com/frank89722/JourneyMapIntegration#refs/heads/main"
    url = "https://github.com/frank89722/JourneyMapIntegration"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "frank89722"
        password = "credentialsJSON:6e15a3dd-5d9c-484b-a3be-6c8aeaedb371"
    }
})
