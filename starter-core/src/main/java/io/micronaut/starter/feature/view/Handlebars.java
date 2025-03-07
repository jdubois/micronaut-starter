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
package io.micronaut.starter.feature.view;

import io.micronaut.starter.application.generator.GeneratorContext;
import io.micronaut.starter.build.dependencies.Dependency;
import io.micronaut.starter.feature.server.MicronautServerDependent;

import jakarta.inject.Singleton;

@Singleton
public class Handlebars implements ViewFeature, MicronautServerDependent {

    @Override
    public String getName() {
        return "views-handlebars";
    }

    @Override
    public String getTitle() {
        return "Handlebars Views";
    }

    @Override
    public String getDescription() {
        return "Adds support for Server-Side View Rendering using Handlebars";
    }

    @Override
    public String getThirdPartyDocumentation() {
        return "https://jknack.github.io/handlebars.java/";
    }

    @Override
    public String getMicronautDocumentation() {
        return "https://micronaut-projects.github.io/micronaut-views/latest/guide/index.html#handlebars";
    }

    @Override
    public void apply(GeneratorContext generatorContext) {
        generatorContext.addDependency(Dependency.builder()
                .groupId("io.micronaut.views")
                .artifactId("micronaut-views-handlebars")
                .compile());
    }
}
