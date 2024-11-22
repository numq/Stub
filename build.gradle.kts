import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.protobuf")
}

group = "com.github.numq"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.6.11")
    implementation("io.insert-koin:koin-compose:4.0.0")
    implementation("io.grpc:grpc-kotlin-stub:1.4.1")
    implementation("io.grpc:grpc-protobuf:1.68.1")
    implementation("com.google.protobuf:protobuf-java-util:4.28.0")
    implementation("com.google.code.gson:gson:2.11.0")
    compileOnly("com.google.protobuf:protoc:4.28.0")
}

compose.desktop {
    application {
        mainClass = "ApplicationKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Stub"
            packageVersion = "1.0.0"
        }
    }
}
