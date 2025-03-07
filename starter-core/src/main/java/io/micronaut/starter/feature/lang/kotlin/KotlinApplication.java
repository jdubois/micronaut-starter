/*
 * Copyright 2017-2022 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.starter.feature.lang.kotlin;

import com.fizzed.rocker.RockerModel;
import io.micronaut.context.env.Environment;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.starter.application.ApplicationType;
import io.micronaut.starter.application.Project;
import io.micronaut.starter.application.generator.GeneratorContext;
import io.micronaut.starter.feature.RequireEagerSingletonInitializationFeature;
import io.micronaut.starter.feature.database.TransactionalNotSupported;
import io.micronaut.starter.feature.test.template.koTest;
import io.micronaut.starter.feature.test.template.kotlinJunit;
import io.micronaut.starter.feature.test.template.spock;
import io.micronaut.starter.options.DefaultTestRockerModelProvider;
import io.micronaut.starter.options.TestFramework;
import io.micronaut.starter.options.TestRockerModelProvider;
import io.micronaut.starter.template.RockerTemplate;

import jakarta.inject.Singleton;

@Singleton
public class KotlinApplication implements KotlinApplicationFeature {

    @Override
    @Nullable
    public String mainClassName(GeneratorContext generatorContext) {
        return generatorContext.getProject().getPackageName() + ".ApplicationKt";
    }

    @Override
    @NonNull
    public String getName() {
        return "kotlin-application";
    }

    @Override
    public boolean supports(ApplicationType applicationType) {
        return applicationType != ApplicationType.CLI && applicationType != ApplicationType.FUNCTION;
    }

    @Override
    public void apply(GeneratorContext generatorContext) {
        KotlinApplicationFeature.super.apply(generatorContext);
        if (shouldGenerateApplicationFile(generatorContext)) {
            addApplication(generatorContext);
            addApplicationTest(generatorContext);
        }
    }

    protected boolean shouldGenerateApplicationFile(GeneratorContext generatorContext) {
        return generatorContext.getApplicationType() == ApplicationType.DEFAULT
                || !generatorContext.getFeatures().hasFunctionFeature();
    }

    protected void addApplication(GeneratorContext generatorContext) {
        generatorContext.addTemplate("application", new RockerTemplate(getPath(), application(generatorContext)));
    }

    protected RockerModel application(GeneratorContext generatorContext) {
        String defaultEnvironment = getDefaultEnvironment(generatorContext);
        boolean eagerInitSingleton = generatorContext.getFeatures().isFeaturePresent(RequireEagerSingletonInitializationFeature.class);
        return application.template(
                generatorContext.getProject(),
                generatorContext.getFeatures(),
                new KotlinApplicationRenderingContext(defaultEnvironment, eagerInitSingleton)
        );
    }

    private static String getDefaultEnvironment(GeneratorContext generatorContext) {
        return generatorContext.hasConfigurationEnvironment(Environment.DEVELOPMENT) ? Environment.DEVELOPMENT : null;
    }

    protected void addApplicationTest(GeneratorContext generatorContext) {
        String testSourcePath = generatorContext.getTestSourcePath("/{packagePath}/{className}");
        generatorContext.addTemplate("applicationTest",
                new RockerTemplate(testSourcePath, applicationTest(generatorContext))
        );
    }

    protected RockerModel applicationTest(GeneratorContext generatorContext) {
        TestFramework testFramework = generatorContext.getTestFramework();
        Project project = generatorContext.getProject();
        boolean transactional = !generatorContext.getFeatures().hasFeature(TransactionalNotSupported.class);
        TestRockerModelProvider provider = new DefaultTestRockerModelProvider(spock.template(project, transactional),
                kotlinJunit.template(project, transactional),
                kotlinJunit.template(project, transactional),
                kotlinJunit.template(project, transactional),
                koTest.template(project, transactional));
        return provider.findModel(generatorContext.getLanguage(), testFramework);
    }

    protected String getPath() {
        return "src/main/kotlin/{packagePath}/Application.kt";
    }
}
