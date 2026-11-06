// Plugin do ObjectBox (banco NoSQL) é publicado no Maven Central, por isso via buildscript.
buildscript {
    repositories { mavenCentral() }
    dependencies { classpath("io.objectbox:objectbox-gradle-plugin:4.3.0") }
}

plugins {
    id("com.android.application") version "8.12.0" apply false
    id("org.jetbrains.kotlin.android") version "2.2.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.21" apply false
    id("com.google.devtools.ksp") version "2.2.21-2.0.4" apply false
}
