/*
 * Copyright 2016 the original author or authors.
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

package org.gradle.api.internal.changedetection.state;

import com.google.common.collect.ImmutableSet;
import org.gradle.api.file.FileTreeElement;
import org.gradle.api.file.RelativePath;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.util.PatternSet;
import org.gradle.internal.file.FileType;
import org.gradle.internal.nativeintegration.filesystem.FileSystem;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * Represents the state of a directory tree.
 */
public class DirectoryTreeDetails implements FileTreeSnapshot {
    // Interned path
    private final String path;
    // All elements, not just direct children
    private final Collection<FileSnapshot> descendants;

    public DirectoryTreeDetails(String path, Collection<FileSnapshot> descendants) {
        this.path = path;
        this.descendants = descendants;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Collection<FileSnapshot> getDescendants() {
        return descendants;
    }

    @Override
    public FileTreeSnapshot filter(PatternSet patternSet) {
        if (patternSet.isEmpty()) {
            return this;
        }
        Spec<FileTreeElement> spec = patternSet.getAsSpec();
        ImmutableSet.Builder<FileSnapshot> builder = ImmutableSet.builder();
        FileTreeElementAdapter adapter = new FileTreeElementAdapter();
        for (FileSnapshot descendant : descendants) {
            adapter.setSnapshot(descendant);
            if (spec.isSatisfiedBy(adapter)) {
                builder.add(descendant);
            }
        }
        return new DirectoryTreeDetails(path, builder.build());
    }

    @Override
    public String toString() {
        return path + " (" + descendants.size() + " descendants)";
    }

    private static class FileTreeElementAdapter implements FileTreeElement {
        private FileSnapshot snapshot;
        private File file;

        public void setSnapshot(FileSnapshot snapshot) {
            this.snapshot = snapshot;
            this.file = null;
        }

        @Override
        public File getFile() {
            if (file == null) {
                file = new File(snapshot.getPath());
            }
            return file;
        }

        @Override
        public boolean isDirectory() {
            return snapshot.getType() == FileType.Directory;
        }

        @Override
        public long getLastModified() {
            return file.lastModified();
        }

        @Override
        public long getSize() {
            return file.length();
        }

        @Override
        public InputStream open() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void copyTo(OutputStream output) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean copyTo(File target) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getName() {
            return snapshot.getName();
        }

        @Override
        public String getPath() {
            return snapshot.getPath();
        }

        @Override
        public RelativePath getRelativePath() {
            return snapshot.getRelativePath();
        }

        @Override
        public int getMode() {
            return isDirectory()
                ? FileSystem.DEFAULT_DIR_MODE
                : FileSystem.DEFAULT_FILE_MODE;
        }
    }
}
