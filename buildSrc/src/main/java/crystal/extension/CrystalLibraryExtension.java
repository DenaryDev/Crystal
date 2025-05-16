/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package crystal.extension;

import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

public class CrystalLibraryExtension extends CrystalExtension {
    private final Property<String> libraryName;

    @Inject
    public CrystalLibraryExtension(ObjectFactory factory, Project project) {
        super(project);
        this.libraryName = factory.property(String.class);
        this.libraryName.finalizeValueOnRead();
    }

    public Property<String> getLibraryName() {
        return this.libraryName;
    }
}
