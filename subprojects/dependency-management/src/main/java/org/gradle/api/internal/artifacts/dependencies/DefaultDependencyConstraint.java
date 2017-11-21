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

package org.gradle.api.internal.artifacts.dependencies;

import org.apache.commons.lang.StringUtils;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencyConstraint;
import org.gradle.api.artifacts.MutableVersionConstraint;

import javax.annotation.Nullable;

public class DefaultDependencyConstraint implements DependencyConstraint {
    private final String group;
    private final String name;
    private final MutableVersionConstraint versionConstraint;

    public DefaultDependencyConstraint(String group, String name, String version) {
        this.group = group;
        this.name = name;
        this.versionConstraint = new DefaultMutableVersionConstraint(version);
    }

    private DefaultDependencyConstraint(String group, String name, MutableVersionConstraint versionConstraint) {
        this.group = group;
        this.name = name;
        this.versionConstraint = versionConstraint;
    }

    @Nullable
    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public String getVersion() {
        return versionConstraint.getPreferredVersion();
    }

    @Override
    public boolean contentEquals(Dependency dependency) {
        if (this == dependency) {
            return true;
        }
        if (dependency == null || getClass() != dependency.getClass()) {
            return false;
        }
        DependencyConstraint that = (DependencyConstraint) dependency;
        return StringUtils.equals(group, that.getGroup()) && StringUtils.equals(name, that.getName()) && versionConstraint.equals(that.getVersionConstraint());
    }

    @Override
    public Dependency copy() {
        return new DefaultDependencyConstraint(group, name, versionConstraint);
    }

    public MutableVersionConstraint getVersionConstraint() {
        return versionConstraint;
    }
}
