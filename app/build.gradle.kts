import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")   // gera o código do Room (SQL)
    id("kotlin-kapt")               // usado pelo processador do ObjectBox (NoSQL)
    id("io.objectbox")              // deve ser aplicado depois dos plugins Kotlin
}

android {
    namespace = "br.com.easyshop"
    compileSdk = 36

    defaultConfig {
        applicationId = "br.com.easyshop"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
}

ksp {
    // Exporta o schema relacional (tabelas/índices) em JSON para documentação
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    // AndroidX / Compose (camada de apresentação)
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.1")
    implementation(platform("androidx.compose:compose-bom:2025.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Banco SQL relacional: Room (abstração oficial sobre SQLite)
    implementation("androidx.room:room-runtime:2.7.2")
    implementation("androidx.room:room-ktx:2.7.2")
    ksp("androidx.room:room-compiler:2.7.2")

    // Banco NoSQL: ObjectBox — dependências adicionadas automaticamente pelo plugin io.objectbox

    // Testes unitários (JVM) das regras de negócio
    testImplementation("junit:junit:4.13.2")
}
