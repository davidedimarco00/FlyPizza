import org.gradle.configurationcache.extensions.capitalized
import org.gradle.internal.impldep.org.fusesource.jansi.AnsiRenderer.test

plugins {
    java
}

allprojects {
    apply<JavaPlugin>()

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }

    repositories {
        mavenCentral()
    }



    group = "it.project.flypizza"
}






subprojects {
    sourceSets {
        main {
            resources {
                srcDir("src/main/asl")
            }
        }
    }



    dependencies {
        implementation("io.github.jason-lang:interpreter:3.2.0")
        testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")


    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }


    file(projectDir).listFiles().filter { it.extension == "mas2j" }.forEach { mas2jFile ->
        task<JavaExec>("run${mas2jFile.nameWithoutExtension.capitalized()}Mas") {
            group = "run"
            classpath = sourceSets.getByName("main").runtimeClasspath
            mainClass.set("jason.infra.centralised.RunCentralisedMAS")
            args(mas2jFile.path)
            standardInput = System.`in`
            javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
        }
    }
}
