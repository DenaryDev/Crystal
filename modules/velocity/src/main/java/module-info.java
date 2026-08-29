module crystal.velocity {
    requires com.google.guice;
    requires com.velocitypowered.api;
    requires crystal.common;
    requires org.jetbrains.annotations;
    requires org.slf4j;
    requires net.kyori.adventure.api;
    requires net.kyori.adventure.text.minimessage;
    requires org.jspecify;

    exports me.denarydev.crystal.velocity;
    exports me.denarydev.crystal.velocity.error;
    exports me.denarydev.crystal.velocity.skin;
}
