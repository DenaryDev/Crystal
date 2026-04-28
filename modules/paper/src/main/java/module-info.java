module crystal.paper {
    requires org.bukkit;
    requires org.jetbrains.annotations;
    requires net.kyori.adventure;
    requires net.kyori.adventure.text.minimessage;
    requires net.kyori.adventure.text.serializer.plain;
    requires com.google.common;
    requires io.sapphiremc.lib.configurate;
    requires net.kyori.examination.api;
    requires com.google.gson;
    requires java.logging;
    requires crystal.common;
    requires org.slf4j;
    requires com.mojang.brigadier;
    requires org.jspecify;

    exports me.denarydev.crystal.paper;
    exports me.denarydev.crystal.paper.command;
    exports me.denarydev.crystal.paper.configurate;
    exports me.denarydev.crystal.paper.configurate.common;
    exports me.denarydev.crystal.paper.configurate.serializers;
    exports me.denarydev.crystal.paper.error;
    exports me.denarydev.crystal.paper.gui;
    exports me.denarydev.crystal.paper.gui.actions;
    exports me.denarydev.crystal.paper.gui.template;
    exports me.denarydev.crystal.paper.input;
    exports me.denarydev.crystal.paper.input.actions;
    exports me.denarydev.crystal.paper.item;
    exports me.denarydev.crystal.paper.listener;
    exports me.denarydev.crystal.paper.skin;
    exports me.denarydev.crystal.paper.utils;
}
