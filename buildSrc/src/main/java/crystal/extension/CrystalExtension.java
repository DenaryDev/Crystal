/*
 * Copyright (c) 2025 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package crystal.extension;

import org.gradle.api.Project;

public class CrystalExtension {
    protected final Project project;

    public CrystalExtension(Project project) {
        this.project = project;
    }
}
