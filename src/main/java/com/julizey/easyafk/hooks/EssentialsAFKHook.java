package com.julizey.easyafk.hooks;

import java.util.UUID;
import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import org.bukkit.Bukkit;

public class EssentialsAFKHook {

  private final IEssentials essentials;

  public EssentialsAFKHook() {
    essentials =
      (IEssentials) Bukkit.getPluginManager().getPlugin("Essentials");
  }

  public void setAFK(UUID uuid, boolean state) {
    IUser user = essentials.getUser(uuid);
    if (user == null) return;
    user.setAfkMessage("");
    user.setAfk(state, net.ess3.api.events.AfkStatusChangeEvent.Cause.UNKNOWN);
  }
}
