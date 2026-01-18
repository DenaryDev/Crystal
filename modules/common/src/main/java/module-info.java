module crystal.common {
    requires org.jetbrains.annotations;
    requires io.sapphiremc.lib.configurate;
    requires io.sapphiremc.lib.configurate.hocon;
    requires io.sapphiremc.lib.configurate.yaml;
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
    exports me.denarydev.crystal.config.internal;
    exports me.denarydev.crystal.database;
    exports me.denarydev.crystal.database.connection;
    exports me.denarydev.crystal.database.connection.file;
    exports me.denarydev.crystal.database.connection.hikari;
    exports me.denarydev.crystal.database.pool;
    exports me.denarydev.crystal.database.query;
    exports me.denarydev.crystal.database.query.batch;
    exports me.denarydev.crystal.database.query.impl;
    exports me.denarydev.crystal.database.query.set;
    exports me.denarydev.crystal.database.schema;
    exports me.denarydev.crystal.database.util;
    exports me.denarydev.crystal.skin;
    exports me.denarydev.crystal.skin.provider;
    exports me.denarydev.crystal.utils;
    exports me.denarydev.crystal;
    exports me.denarydev.crystal.utils.time;
}
