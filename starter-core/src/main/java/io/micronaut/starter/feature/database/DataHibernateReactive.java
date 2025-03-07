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
package io.micronaut.starter.feature.database;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.starter.application.generator.GeneratorContext;
import io.micronaut.starter.build.dependencies.Dependency;
import io.micronaut.starter.build.dependencies.MicronautDependencyUtils;
import io.micronaut.starter.feature.FeatureContext;
import io.micronaut.starter.feature.testresources.TestResources;
import jakarta.inject.Singleton;

@Singleton
public class DataHibernateReactive extends HibernateReactiveFeature implements DataFeature {

    public static final String NAME = "data-hibernate-reactive";

    private static final Dependency.Builder DEPENDENCY_MICRONAUT_DATA_HIBERNATE_REACTIVE = MicronautDependencyUtils.dataDependency()
            .artifactId("micronaut-data-hibernate-reactive")
                .compile();

    private final Data data;

    public DataHibernateReactive(Data data, TestContainers testContainers, TestResources testResources) {
        super(testContainers, testResources);
        this.data = data;
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    @Override
    public String getTitle() {
        return "Micronaut Data Hibernate Reactive";
    }

    @Override
    @NonNull
    public String getDescription() {
        return "Adds support for Micronaut Data Hibernate Reactive";
    }

    @Override
    public void processSelectedFeatures(FeatureContext featureContext) {
        featureContext.addFeature(data);
    }

    @Override
    public void apply(GeneratorContext generatorContext) {
        super.apply(generatorContext);
        generatorContext.addDependency(DEPENDENCY_MICRONAUT_DATA_PROCESSOR);
        generatorContext.addDependency(DEPENDENCY_MICRONAUT_DATA_HIBERNATE_REACTIVE);
    }
}
