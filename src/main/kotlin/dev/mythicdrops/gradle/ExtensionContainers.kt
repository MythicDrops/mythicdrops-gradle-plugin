package dev.mythicdrops.gradle

import dev.mythicdrops.gradle.conventions.MythicDropsJavaExtension
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType

fun ExtensionContainer.findOrCreateMythicDropsJavaExtension(): MythicDropsJavaExtension =
    findByType() ?: create("mythicDropsJava")
