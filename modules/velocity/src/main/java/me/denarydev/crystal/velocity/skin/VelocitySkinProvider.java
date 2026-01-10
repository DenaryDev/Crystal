package me.denarydev.crystal.velocity.skin;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.GameProfile;
import me.denarydev.crystal.skin.SkinProperty;
import me.denarydev.crystal.skin.SkinProvider;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author DenaryDev
 * @since 22:14 10.01.2026
 */
@ApiStatus.Internal
public final class VelocitySkinProvider extends SkinProvider {

    private final ProxyServer proxy;

    public VelocitySkinProvider(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public Optional<SkinProperty> getPlayerSkin(@NotNull UUID uuid) {
        final Optional<Player> opt = proxy.getPlayer(uuid);
        if (opt.isEmpty()) return Optional.empty();

        return getPlayerSkin(opt.get());
    }

    @Override
    public Optional<SkinProperty> getPlayerSkin(@NotNull String name) {
        final Optional<Player> opt = proxy.getPlayer(name);
        if (opt.isEmpty()) return Optional.empty();

        return getPlayerSkin(opt.get());
    }

    private Optional<SkinProperty> getPlayerSkin(@NotNull Player player) {
        final List<GameProfile.Property> properties = player.getGameProfile().getProperties();
        for (GameProfile.Property property : properties) {
            if (property.getName().equals("textures")) {
                final String value = property.getValue();
                final String signature = property.getSignature();

                return Optional.of(new SkinProperty(value, signature));
            }
        }

        return Optional.empty();
    }
}
