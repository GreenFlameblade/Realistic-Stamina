package com.gfb.realisticstamina;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class StaminaDebugCommand extends AbstractPlayerCommand {
    private final RequiredArg<List<String>> targetsArg;
    private final StaminaDebugWatchers watchers;

    public StaminaDebugCommand(StaminaDebugWatchers watchers) {
        super("staminadebug", "Prints target(s) stamina values in your chat");
        targetsArg = withListRequiredArg("targets", "player names, all, or off", ArgTypes.STRING);
        this.watchers = watchers;
    }

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store,
                           @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef sender, @Nonnull World world) {
        List<String> targets = commandContext.get(targetsArg);

        if (targets.size() == 1) {
            if (targets.get(0).equalsIgnoreCase("off")) {
                watchers.unsubscribe(sender.getUuid(), new DebugTarget.ALL());
                sender.sendMessage(Message.raw("Stamina Debug: OFF"));
                return;
            } else if (targets.get(0).equalsIgnoreCase("all")) {
                watchers.subscribe(sender.getUuid(), new DebugTarget.ALL());
                sender.sendMessage(Message.raw("Stamina Debug: ALL"));
                return;
            }
        }

        Set<UUID> ids = new HashSet<>();

        for (String target : targets) {
            PlayerRef found = Universe.get().getPlayerByUsername(target, NameMatching.DEFAULT);

            if (found == null) {
                sender.sendMessage(Message.raw(target + " is not online"));
            } else {
                ids.add(found.getUuid());
            }
        }

        if (!ids.isEmpty()) {
            watchers.subscribe(sender.getUuid(), new DebugTarget.Players(ids));
        }
    }
}

