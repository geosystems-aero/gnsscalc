group = "aero.geosystems"
version = "18.1115"
buildscript {
    val kotlinVersion: String by extra("1.3.11")

    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

plugins {
    val kotlinVersion = "1.3.11"
    kotlin("jvm") version kotlinVersion
    id("application")
}
application {
    mainClassName = "gnss.calc.MainKt"
}

repositories {
    mavenCentral()
}

task<JavaExec>("sample1") {
    group = "samples"
    main = "gnss.calc.MainKt"
    args = listOf("sample/brdc3140.16n","32","1922","292229.000")
}
task<JavaExec>("sample2") {
    group = "samples"
    main = "gnss.calc.Sample2Kt"
}
task<JavaExec>("sample3") {
    group = "samples"
    main = "gnss.calc.Sample3Kt"
}
/*
// template
task sample2(type: JavaExec) {
    group "samples"
    main = "gnss.calc.MainKt"
    args += "sample/brdc3140.16n"
}
*/

tasks.withType<JavaExec> {
    if (group == "samples") {
        classpath = java.sourceSets["main"].runtimeClasspath
    }
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile(project(":gnss-core"))
    compile(project(":formats-rtcm3"))
}