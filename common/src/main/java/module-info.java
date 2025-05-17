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

    exports me.denarydev.crystal.common.config;
    exports me.denarydev.crystal.common.database;
    exports me.denarydev.crystal.common.database.connection;
    exports me.denarydev.crystal.common.database.connection.file;
    exports me.denarydev.crystal.common.database.connection.hikari;
    exports me.denarydev.crystal.common.database.util;
    exports me.denarydev.crystal.common.utils;
}
