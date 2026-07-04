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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Prompts a player to type a response in chat.
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
     * Opens a chat input prompt for the given player.
     *
     * @param plugin the owning plugin.
     * @param player the player to prompt.
     * @param action the action to run when the player responds.
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
     * Sets the action to run when this prompt closes.
     *
     * @param action the close action.
     */
    public ChatPrompt closeAction(@NonNull CloseAction action) {
        this.closeAction = action;

        return this;
    }

    /**
     * Sets the word that, when typed, cancels the prompt.
     *
     * @param word the cancel word.
     */
    public ChatPrompt cancelWord(@NonNull String word) {
        this.cancelWord = word;

        return this;
    }

    /**
     * Sets how long to wait for the player's response, in ticks (20 ticks = 1 second).
     * <p>
     * When the timeout expires, the prompt closes with {@link CloseReason#TIMEOUT}.
     *
     * @param timeout the timeout duration in ticks.
     */
    public ChatPrompt timeout(long timeout) {
        this.timeoutTask = Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task -> {
            close(CloseReason.TIMEOUT);
            HandlerList.unregisterAll(this.listener);
        }, timeout);

        return this;
    }

    /**
     * Returns whether this prompt has been closed.
     *
     * @return {@code true} if the prompt is closed; {@code false} otherwise.
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
