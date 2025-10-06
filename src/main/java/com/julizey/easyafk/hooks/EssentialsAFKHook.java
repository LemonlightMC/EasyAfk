package com.julizey.easyafk.hooks;

import com.julizey.easyafk.hooks.Hooks.Hook;

/* 
import java.util.UUID;
import net.essentialsx.api.IEssentials;
import net.essentialsx.api.IUser;
import org.bukkit.Bukkit;
*/

public class EssentialsAFKHook extends Hook {

  // private final IEssentials essentials;

  public EssentialsAFKHook() {
    super("essentials");
  }

  public static void create() {
    new TabHook();
  }

  @Override
  public void load() {
    // essentials = (IEssentials) Bukkit.getPluginManager().getPlugin("Essentials");
  }
  /*
   * public void setAFK(UUID uuid, boolean state) {
   * IUser user = essentials.getUser(uuid);
   * if (user == null) return;
   * user.setAfkMessage("");
   * user.setAfk(state, net.ess3.api.events.AfkStatusChangeEvent.Cause.UNKNOWN);
   * }
   */
}
