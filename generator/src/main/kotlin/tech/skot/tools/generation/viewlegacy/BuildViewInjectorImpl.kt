package tech.skot.tools.generation.viewlegacy

import com.squareup.kotlinpoet.*
import tech.skot.tools.generation.*

fun Generator.generateViewLegacyInjectorImpl(module: String) {

    FileSpec.builder(viewInjectorImpl.packageName, viewInjectorImpl.simpleName)
            .addType(TypeSpec.classBuilder(viewInjectorImpl.simpleName)
                    .addSuperinterface(viewInjectorInterface)
                    .addFunctions(
                            components.map { it.asViewLegacyInjection(this) }
                    )
                    .build())
            .apply {
                components.forEach {
                    addImportClassName(it.proxy())
                    it.subComponents.forEach {
                        addImportTypeName(it.type.toProxy())
                    }
                }

            }
            .build()
            .writeTo(generatedAndroidSources(module))
}

fun ComponentDef.asViewLegacyInjection(generator: Generator) =
        FunSpec.builder(name.decapitalize())
                .addModifiers(KModifier.OVERRIDE)
                .apply {
                    subComponents.forEach {
                        addParameter(it.asParam())
                    }
                    fixProperties.forEach {
                        addParameter(it.asParam())
                    }
                    mutableProperties.forEach {
                        addParameter(it.initial().asParam())
                    }
                }
                .returns(vc)
                .addCode(
                        "return ${proxy().simpleName}(${(subComponents.map { "${it.name} as ${it.type.toProxy().simpleName()}" } + (fixProperties.map { it.name } + mutableProperties.map { it.initial().name })).joinToString(separator = ",")})")
                .build()