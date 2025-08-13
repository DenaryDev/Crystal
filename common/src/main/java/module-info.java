/**
 * @author DenaryDev
 * @since 19:58 17.05.2025
 */
module crystal.common {
    requires org.spongepowered.configurate.hocon;
    requires org.jetbrains.annotations;
    requires org.spongepowered.configurate;
    requires org.spongepowered.configurate.yaml;
    requires java.sql;
    requires org.slf4j;
    requires org.xerial.sqlitejdbc;
    requires com.h2database;
    requires com.zaxxer.hikari;
    requires com.mysql;
    requires org.mariadb.jdbc;
    requires org.postgresql.jdbc;
    requires skinsrestorer.api;

    exports me.denarydev.crystal.config;
    exports me.denarydev.crystal.database;
    exports me.denarydev.crystal.database.connection;
    exports me.denarydev.crystal.database.connection.file;
    exports me.denarydev.crystal.database.connection.hikari;
    exports me.denarydev.crystal.database.util;
    exports me.denarydev.crystal.skin;
    exports me.denarydev.crystal.skin.provider;
    exports me.denarydev.crystal.utils;
}
