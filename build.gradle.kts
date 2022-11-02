plugins {
    java
    id("com.diffplug.spotless") version "6.11.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jctools:jctools-core:4.0.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.1")
}

group = "me.siquel"
version = "1.0-SNAPSHOT"
description = "memorylane"
java.sourceCompatibility = JavaVersion.VERSION_17

spotless {
    java {
        palantirJavaFormat()
        formatAnnotations()
        importOrder("java", "javax", "com", "org", "me")
        trimTrailingWhitespace()
    }
    format("misc") {
        // define the files to apply `misc` to
        target("*.md", ".gitignore")
        // define the steps to apply to those files
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint().setUseExperimental(true).editorConfigOverride(mapOf("indent_size" to "4"))
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.create("fatJar", Jar::class) {
    group = "build"
    description = "Creates a self-contained fat JAR of the application that can be run."
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree)
    from(dependencies)
    with(tasks.jar.get())
}
