import org.gradle.jvm.tasks.Jar

plugins {
    java
}

base {
    archivesName = project.name
}

tasks {
    withType<JavaCompile>().configureEach {
        with(options) {
            encoding = "UTF-8"
            isDeprecation = true
            compilerArgs.add("-Xlint:all")
        }
    }

    withType<Javadoc>().configureEach {
        options {
            encoding = "UTF-8"
        }
    }

    withType<AbstractArchiveTask>().configureEach {
        archiveBaseName = project.name
    }

    withType<Jar>().configureEach {
        from("LICENSE") {
            rename { "${it}_${rootProject.name}" }
        }
    }

    named<Task>("build") {
        dependsOn(withType<Jar>())
    }
}
