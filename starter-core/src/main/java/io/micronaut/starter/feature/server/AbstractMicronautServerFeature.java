/*
 * Copyright 2017-2021 original authors
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
package io.micronaut.starter.feature.server;

import io.micronaut.starter.application.generator.GeneratorContext;
import io.micronaut.starter.build.dependencies.MicronautDependencyUtils;
import io.micronaut.starter.feature.MicronautRuntimeFeature;

public abstract class AbstractMicronautServerFeature implements ServerFeature, MicronautRuntimeFeature {

    @Override
    public void apply(GeneratorContext generatorContext) {
        generatorContext.addDependency(MicronautDependencyUtils.coreDependency()
                .artifactId("micronaut-http-validation")
                .versionProperty("micronaut.version")
                .annotationProcessor());
        generatorContext.getBuildProperties().put(PROPERTY_MICRONAUT_RUNTIME, resolveMicronautRuntime(generatorContext));
        doApply(generatorContext);
    }

    public abstract void doApply(GeneratorContext generatorContext);
}
