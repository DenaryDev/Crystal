/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.gui;

import com.google.common.base.Preconditions;
import me.denarydev.crystal.paper.gui.template.MatrixTemplate;
import me.denarydev.crystal.paper.gui.template.SimpleTemplate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Абстрактный класс шаблона.
 *
 * Для создания шаблонов используйте {@link SimpleTemplate#builder()} и {@link MatrixTemplate#builder()}
 *
 * @author DenaryDev
 * @since 16:04 25.11.2025
 */
public sealed abstract class Template permits SimpleTemplate, MatrixTemplate {

    @Nullable
    protected final Component title;
    protected final int size;
    protected final Map<Integer, ItemStack> items;
    protected final long cooldown;

    protected Template(@Nullable Component title, int size, Map<Integer, ItemStack> items, long cooldown) {
        this.title = title;
        this.size = size;
        this.items = items;
        this.cooldown = cooldown;
    }

    /**
     * Проверяет, указан-ли в шаблоне заголовок меню.
     *
     * @return true, если заголовок указан, иначе false
     */
    public boolean hasTitle() {
        return title != null;
    }

    /**
     * Возвращает заголовок меню, если он был указан.
     *
     * @return заголовок меню или null, если не указан
     */
    @Nullable
    public Component title() {
        return title;
    }

    /**
     * Проверяет, указан-ли в шаблоне размер меню.
     *
     * @return true, если размер указан, иначе false
     */
    public boolean hasSize() {
        return size > 0;
    }

    /**
     * Возвращает кол-во слотов в меню.
     *
     * @return кол-во слотов в меню
     */
    public int size() {
        return size;
    }

    /**
     * Проверяет, указан ли в шаблоне хотя бы один предмет.
     *
     * @return true, если хотя бы один предмет указан, иначе false
     */
    public boolean hasItems() {
        return !items.isEmpty();
    }

    /**
     * Возвращает словарь с указанием предметов в слотах.
     *
     * @return словарь предметов
     */
    public Map<Integer, ItemStack> items() {
        return items;
    }

    /**
     * Возвращает задержку между обработкой кликов.
     *
     * @return задержка обработки кликов
     */
    public long cooldown() {
        return cooldown;
    }

    public sealed abstract static class Builder<B extends Builder<B, T>, T extends Template> permits SimpleTemplate.Builder, MatrixTemplate.Builder {
        protected Component title;
        protected long cooldown;

        /**
         * Возвращает заголовок меню, если установлен.
         *
         * @return заголовок меню, или null, если он не установлен.
         */
        @Nullable
        public final Component title() {
            return title;
        }

        /**
         * Устанавливает заголовок меню из компонента MiniMessage.
         * <p>
         * Если указать null, заголовок будет сброшен до заголовка по умолчанию!
         *
         * @param title заголовок
         */
        public final B title(@Nullable final Component title) {
            this.title = title;

            return self();
        }

        /**
         * Устанавливает заголовок меню из строки, применяя к ней
         * форматирование MiniMessage и указанные плейсхолдеры.
         *
         * @param title     заголовок
         * @param resolvers плейсхолдеры
         */
        public final B titleRich(@NotNull String title, @NotNull TagResolver... resolvers) {
            this.title = MiniMessage.miniMessage().deserialize(title, resolvers);

            return self();
        }

        /**
         * Устанавливает заголовок меню из строки без форматирования.
         *
         * @param title заголовок
         */
        public final B titlePlain(@NotNull String title) {
            this.title = Component.text(title);

            return self();
        }

        /**
         * Возвращает задержку обработки кликов меню.
         *
         * @return задержка обработки кликов
         */
        public final long cooldown() {
            return cooldown;
        }

        /**
         * Устанавливает задержку обработки кликов.
         *
         * @param cooldown задержка
         */
        public final B cooldown(long cooldown) {
            Preconditions.checkArgument(cooldown >= 0, "cooldown must be positive");

            this.cooldown = cooldown;

            return self();
        }

        /**
         * Собирает указанные параметры "до кучи" и
         * создаёт из них шаблон.
         *
         * @return созданный шаблон
         */
        public abstract T build();

        /**
         * Нужно для того, чтобы возвращать имплементацию билдера, а не абстрактный класс
         */
        @ApiStatus.Internal
        protected abstract B self();
    }
}
