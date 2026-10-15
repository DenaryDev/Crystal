/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal;

import me.denarydev.crystal.scheduler.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.concurrent.Executor;

public class TestCrystal extends Crystal {
    private static final Logger LOG = LoggerFactory.getLogger("crystal-test");

    public TestCrystal() {
        super(Platform.CORE);
        setInstance(this);
    }

    @Override
    public Logger logger() {
        return LOG;
    }

    @Override
    public Path dataFolder() {
        return Path.of("test");
    }

    @Override
    public Scheduler scheduler() {
        return new Scheduler() {
            @Override
            public Executor async() {
                return Runnable::run;
            }

            @Override
            public Executor sync() {
                return Runnable::run;
            }
        };
    }
}
