package com.julizey.easyafk.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.julizey.easyafk.EasyAFK;
import com.julizey.easyafk.hooks.Hooks.Hook;
import com.julizey.easyafk.utils.Text;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextManager;

public class LuckPermsHook extends Hook {
  private ContextManager ctxManager;
  private AFKContext ctx;

  public LuckPermsHook() {
    super("luckperms");
  }

  public void load() {
    reload();
  }

  public void unload() {
    ctxManager.unregisterCalculator(ctx);
  }

  public void reload() {
    RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager()
        .getRegistration(LuckPerms.class);
    if (provider == null) {
      Text.warn("Failed to load LuckPerms");
      return;
    }
    ctxManager = provider.getProvider().getContextManager();
    ctx = new AFKContext();
    ctxManager.registerCalculator(ctx);
  }

  public static class AFKContext implements ContextCalculator<Player> {
    @Override
    public void calculate(Player target, ContextConsumer consumer) {
      boolean playerAFKState = EasyAFK.manager.isAFK((Player) target);
      consumer.accept("afkstate", playerAFKState ? "true" : "false");
    }
  }
}
