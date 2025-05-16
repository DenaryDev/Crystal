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
    private final Property<String> moduleName;
    private final Property<String> id;
    private final Property<String> description;

    @Inject
    public CrystalModuleExtension(ObjectFactory factory, Project project) {
        super(project);
        this.library = factory.property(String.class);
        this.library.finalizeValueOnRead();
        this.moduleName = factory.property(String.class);
        this.moduleName.finalizeValueOnRead();
        this.id = factory.property(String.class);
        this.id.finalizeValueOnRead();
        this.name = factory.property(String.class);
        this.name.finalizeValueOnRead();
        this.description = factory.property(String.class);
        this.description.finalizeValueOnRead();
    }

    @Input
    public Property<String> getModuleName() {
        return this.moduleName;
    }

    @Input
    public Property<String> getLibrary() {
        return this.library;
    }

    @Input
    public Property<String> getName() {
        return this.name;
    }

    @Input
    public Property<String> getId() {
        return this.id;
    }

    @Input
    public Property<String> getDescription() {
        return this.description;
    }
}
