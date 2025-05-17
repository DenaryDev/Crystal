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
import org.gradle.api.tasks.Input;

import javax.inject.Inject;
import java.io.Serial;
import java.io.Serializable;

public class CrystalModuleExtension extends CrystalExtension implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Property<String> name;
    private final Property<String> library;

    @Inject
    public CrystalModuleExtension(ObjectFactory factory, Project project) {
        super(project);
        this.name = factory.property(String.class);
        this.name.finalizeValueOnRead();
        this.library = factory.property(String.class);
        this.library.finalizeValueOnRead();
    }

    @Input
    public Property<String> getName() {
        return this.name;
    }

    @Input
    public Property<String> getLibrary() {
        return this.library;
    }
}
