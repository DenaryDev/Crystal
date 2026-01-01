/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.paper.input;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.denarydev.crystal.paper.input.actions.CloseAction;
import me.denarydev.crystal.paper.input.actions.MessageAction;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Запрашивает у игрока ответ в виде сообщения в чат.
 *
 * @author DenaryDev
 * @since 4:12 27.11.2025
 */
public final class ChatPrompt {

    private final Plugin plugin;
    private final Player player;
    private final MessageAction messageAction;
    private final ChatPromptListener listener;

    @Nullable
    private CloseAction closeAction;
    @Nullable
    private String cancelWord;
    @Nullable
    private ScheduledTask timeoutTask;

    private boolean closed = false;

    /**
     * Выводит в чат игроку запрос ввода.
     *
     * @param plugin ваш плагин
     * @param player игрок
     * @param action действие при получении ввода
     */
    public static ChatPrompt show(Plugin plugin, Player player, MessageAction action) {
        return new ChatPrompt(plugin, player, action);
    }

    @ApiStatus.Internal
    private ChatPrompt(Plugin plugin, Player player, MessageAction action) {
        this.plugin = plugin;
        this.player = player;
        this.messageAction = action;

        this.listener = new ChatPromptListener(this);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    /**
     * Устанавливает действие при закрытии запроса.
     *
     * @param action действие при закрытии запроса
     */
    public ChatPrompt closeAction(@NotNull CloseAction action) {
        this.closeAction = action;

        return this;
    }

    /**
     * Устанавливает слово, при вводе которого запрос будет отменён.
     *
     * @param word слово, отменяющее запрос
     */
    public ChatPrompt cancelWord(@NotNull String word) {
        this.cancelWord = word;

        return this;
    }

    /**
     * Устанавливает время ожидания ответа от игрока в тиках (1 секунда = 20 тиков).
     * <p>
     * По истечении указанного времени запрос закроется с {@link CloseReason#CANCELLED}
     *
     * @param timeout время ожидания ответа
     */
    public ChatPrompt timeout(long timeout) {
        this.timeoutTask = Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task -> {
            close(CloseReason.TIMEOUT);
            HandlerList.unregisterAll(this.listener);
        }, timeout);

        return this;
    }

    /**
     * Проверяет, закрыт ли этот запрос.
     *
     * @return true, если запрос закрыт, иначе false
     */
    public boolean closed() {
        return closed;
    }

    @ApiStatus.Internal
    private void close(CloseReason reason) {
        if (closeAction != null) {
            Bukkit.getGlobalRegionScheduler().run(plugin, task -> closeAction.onClose(reason));
        }
    }

    @ApiStatus.Internal
    private record ChatPromptListener(ChatPrompt prompt) implements Listener {
        @EventHandler(priority = EventPriority.LOWEST)
        private void onChat(AsyncChatEvent event) {
            final Player player = event.getPlayer();

            if (!player.getUniqueId().equals(prompt.player.getUniqueId())) {
                return;
            }

            event.setCancelled(true);
            prompt.closed = true;

            final String message = PlainTextComponentSerializer.plainText().serialize(event.originalMessage());
            player.sendPlainMessage("» " + message);

            if (!message.equalsIgnoreCase(prompt.cancelWord)) {
                try {
                    prompt.messageAction.onMessage(message);
                    prompt.close(CloseReason.SUCCESS);
                } catch (Throwable t) {
                    prompt.plugin.getSLF4JLogger().error("Failed to process chat prompt answer from {}", player.getName(), t);
                    prompt.close(CloseReason.ERROR);
                }
            } else {
                prompt.close(CloseReason.CANCELLED);
            }

            HandlerList.unregisterAll(this);
            if (prompt.timeoutTask != null) {
                prompt.timeoutTask.cancel();
            }
        }
    }
}
