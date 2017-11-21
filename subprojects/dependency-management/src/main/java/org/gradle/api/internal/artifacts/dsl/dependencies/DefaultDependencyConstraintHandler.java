/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.artifacts.dsl.dependencies;

import org.gradle.api.Action;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.DependencyConstraint;
import org.gradle.api.artifacts.dsl.DependencyConstraintHandler;

public class DefaultDependencyConstraintHandler implements DependencyConstraintHandler {
    private final ConfigurationContainer configurationContainer;
    private final DependencyFactory dependencyFactory;

    public DefaultDependencyConstraintHandler(ConfigurationContainer configurationContainer,
                                              DependencyFactory dependencyFactory) {
        this.configurationContainer = configurationContainer;
        this.dependencyFactory = dependencyFactory;
    }

    @Override
    public DependencyConstraint add(String configurationName, Object dependencyNotation) {
        return add(configurationName, dependencyNotation, null);
    }

    @Override
    public DependencyConstraint add(String configurationName, Object dependencyNotation, Action<? super DependencyConstraint> configureAction) {
        return doAdd(configurationContainer.getByName(configurationName), dependencyNotation, configureAction);
    }

    @Override
    public DependencyConstraint create(Object dependencyNotation) {
        return create(dependencyNotation, null);
    }

    @Override
    public DependencyConstraint create(Object dependencyNotation, Action<? super DependencyConstraint> configureAction) {
        DependencyConstraint dependencyConstraint = dependencyFactory.createDependencyConstraint(dependencyNotation);
        configureAction.execute(dependencyConstraint);
        return dependencyConstraint;
    }

    private DependencyConstraint doAdd(Configuration configuration, Object dependencyNotation, Action<? super DependencyConstraint> configureAction) {
        if (dependencyNotation instanceof Configuration) {
            Configuration other = (Configuration) dependencyNotation;
            if (!configurationContainer.contains(other)) {
                throw new UnsupportedOperationException("Currently you can only declare dependencies on configurations from the same project.");
            }
            configuration.extendsFrom(other);
            return null;
        }

        DependencyConstraint dependency = create(dependencyNotation, configureAction);
        configuration.getDependencies().add(dependency);
        return dependency;
    }
}
