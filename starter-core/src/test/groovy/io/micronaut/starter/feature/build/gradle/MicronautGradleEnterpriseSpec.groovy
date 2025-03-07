package io.micronaut.starter.feature.build.gradle

import groovy.xml.XmlParser
import io.micronaut.starter.ApplicationContextSpec
import io.micronaut.starter.BuildBuilder
import io.micronaut.starter.application.ApplicationType
import io.micronaut.starter.application.Project
import io.micronaut.starter.build.gradle.GradleBuild
import io.micronaut.starter.feature.build.gradle.templates.settingsGradle
import io.micronaut.starter.fixture.CommandOutputFixture
import io.micronaut.starter.options.BuildTool
import io.micronaut.starter.options.Language
import io.micronaut.starter.options.Options

class MicronautGradleEnterpriseSpec extends ApplicationContextSpec implements CommandOutputFixture {

    void "if you add micronaut-gradle-enterprise it is configured for #buildTool"() {
        given:when:
        BuildBuilder builder = new BuildBuilder(beanContext, buildTool)
                    .language(Language.JAVA)
                    .applicationType(ApplicationType.DEFAULT)
                    .features(["micronaut-gradle-enterprise"])
        Project project = builder.getProject()
        GradleBuild gradleBuild = (GradleBuild) builder.build(false)
        String settings = settingsGradle.template(project, gradleBuild, []).render().toString()

        then:
        settings.contains('pluginManagement {')
        settings.contains('    repositories {')
        settings.contains('        gradlePluginPortal()')
        settings.contains('        mavenCentral()')
        settings.contains('    }')
        settings.contains('}')
        settings.contains('plugins {')
        if (buildTool == BuildTool.GRADLE_KOTLIN) {
            assert settings.contains('    id("io.micronaut.build.internal.gradle-enterprise") version("')
        } else if (buildTool == BuildTool.GRADLE) {
            assert settings.contains('    id "io.micronaut.build.internal.gradle-enterprise" version "')
        }
        settings.contains('}')

        where:
        buildTool << [BuildTool.GRADLE, BuildTool.GRADLE_KOTLIN]
    }

    void "io.micronaut.starter.feature.build.gradle.MicronautGradleEnterprise is not visible"() {
        expect:
        !beanContext.getBean(MicronautGradleEnterprise).isVisible()
    }

    void 'feature micronaut-gradle-enterprise creates a .mvn/extensions dot xml file'() {
        when:
        Map<String, String> output = generate(ApplicationType.DEFAULT, new Options(Language.JAVA, BuildTool.MAVEN), ["micronaut-gradle-enterprise"])
        def xml = new XmlParser().parseText(output[".mvn/extensions.xml"])

        then:
        xml.name() == 'extensions'

        def enterpriseExtension = xml.extension.find { it.artifactId.text() == 'gradle-enterprise-maven-extension' }
        enterpriseExtension.groupId.text() == 'com.gradle'
        enterpriseExtension.version.text() ==~ /[\d.]+/ // numbers and fullstops
        def userDataExtension = xml.extension.find { it.artifactId.text() == 'common-custom-user-data-maven-extension' }
        userDataExtension.groupId.text() == 'com.gradle'
        userDataExtension.version.text() ==~ /[\d.]+/ // numbers and fullstops
    }

    void 'feature micronaut-gradle-enterprise creates a .mvn/gradle-enterprise-custom-user-data dot groovy file'() {
        when:
        Map<String, String> output = generate(ApplicationType.DEFAULT, new Options(Language.JAVA, BuildTool.MAVEN), ["micronaut-gradle-enterprise"])

        then:
        output[".mvn/gradle-enterprise-custom-user-data.groovy"] == "buildCache.remote.storeEnabled = System.getenv('GITHUB_ACTIONS') != null"
    }

    void 'feature micronaut-gradle-enterprise does not create maven files for #buildTool'() {
        when:
        Map<String, String> output = generate(ApplicationType.DEFAULT, new Options(Language.JAVA, buildTool), ["micronaut-gradle-enterprise"])

        then:
        output[".mvn/gradle-enterprise.xml"] == null
        output[".mvn/gradle-enterprise-custom-user-data.groovy"] == null
        output[".mvn/extensions.xml"] == null

        where:
        buildTool << [BuildTool.GRADLE, BuildTool.GRADLE_KOTLIN]
    }

    void 'feature micronaut-gradle-enterprise creates a .mvn/gradle-enterprise dot xml file'() {
        when:
        Map<String, String> output = generate(ApplicationType.DEFAULT, new Options(Language.JAVA, BuildTool.MAVEN), ["micronaut-gradle-enterprise"])
        def xml = new XmlParser().parseText(output[".mvn/gradle-enterprise.xml"])

        then:
        xml.name() == 'gradleEnterprise'
        xml.server.url.text() == 'https://ge.micronaut.io'
        xml.buildScan.publish.text() == 'ALWAYS'
        xml.buildCache.remote.server.credentials.username.text() == '${env.GRADLE_ENTERPRISE_CACHE_USERNAME}'
        xml.buildCache.remote.server.credentials.password.text() == '${env.GRADLE_ENTERPRISE_CACHE_PASSWORD}'
        !xml.buildCache.remote.storeEnabled // Not set in here, this is handled in custom data
    }
}
