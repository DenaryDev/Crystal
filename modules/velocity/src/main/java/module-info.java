module crystal.velocity {
    requires com.google.guice;
    requires com.velocitypowered.api;
    requires crystal.common;
    requires org.jetbrains.annotations;
    requires org.slf4j;
    requires net.kyori.adventure;
    requires net.kyori.adventure.text.minimessage;
    requires net.kyori.examination.api;
    requires org.jspecify;

    exports me.denarydev.crystal.velocity;
    exports me.denarydev.crystal.velocity.error;
    exports me.denarydev.crystal.velocity.skin;
}
