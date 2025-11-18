/**
 * @author DenaryDev
 * @since 20:00 17.05.2025
 */
module crystal.paper {
    requires org.bukkit;
    requires org.jetbrains.annotations;
    requires net.kyori.adventure;
    requires net.kyori.adventure.text.minimessage;
    requires net.kyori.adventure.text.serializer.plain;
    requires com.google.common;
    requires org.spongepowered.configurate;
    requires net.kyori.examination.api;
    requires com.google.gson;
    requires java.logging;
    requires org.spongepowered.configurate.yaml;
    requires configlib;
    requires crystal.common;
    requires org.slf4j;

    exports me.denarydev.crystal.paper.configurate;
    exports me.denarydev.crystal.paper.configurate.serializers;
    exports me.denarydev.crystal.paper.gui;
    exports me.denarydev.crystal.paper.item;
    exports me.denarydev.crystal.paper.utils;
}
