package com.julizey.easyafk.database;

import com.julizey.easyafk.EasyAFK;
import com.julizey.easyafk.utils.AfkMode;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseManager {

  public interface DatabaseProvider {
    void close();

    boolean isConnected();

    void addAfkPlayer(UUID playerId, AfkMode mode, long lastActive);

    void removeAfkPlayer(UUID playerId);

    void removeAllAfkPlayers();

    boolean containsAfkPlayer(UUID playerId);
  }

  private static ConcurrentHashMap<UUID, Long> lastActiveCache = new ConcurrentHashMap<>();
  private static ConcurrentHashMap<UUID, Boolean> afkPlayerCache = new ConcurrentHashMap<>();
  private static final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();
  private static DatabaseProvider provider = null;
  private static boolean isSetup = false;

  public static void setup() {
    final String databaseType = EasyAFK.config.configFile.getString("database");
    if (databaseType.equalsIgnoreCase("mysql") &&
        EasyAFK.config.configFile.getBoolean("mysql.enabled")) {
      provider = new MySQLManager();
    } else if (databaseType.equalsIgnoreCase("sqlite") &&
        EasyAFK.config.configFile.getBoolean("sqlite.enabled")) {
      provider = new SQLiteManager();
    }
    if (provider != null && provider.isConnected())
      isSetup = true;
    reload();
  }

  public static void reload() {
    if (!isSetup)
      return;
    if (EasyAFK.config.clearOnReload) {
      dbExecutor.submit(() -> provider.removeAllAfkPlayers());
    }
  }

  public static void close() {
    if (isSetup) {
      provider.close();
    }
    dbExecutor.shutdown();
    lastActiveCache.clear();
    afkPlayerCache.clear();
  }

  public static boolean isConnected() {
    return provider != null && provider.isConnected();
  }

  public static void addAfkPlayer(final UUID playerId, AfkMode mode, final long lastActive) {
    if (playerId == null)
      return;
    lastActiveCache.put(playerId, lastActive);
    afkPlayerCache.put(playerId, true);
    if (isSetup) {
      dbExecutor.submit(() -> provider.addAfkPlayer(playerId, mode, lastActive));
    }
  }

  public static void addAfkPlayer(final UUID playerId, AfkMode mode) {
    if (playerId == null)
      return;
    final long time = System.currentTimeMillis();
    lastActiveCache.put(playerId, time);
    afkPlayerCache.put(playerId, true);
    if (isSetup) {
      dbExecutor.submit(() -> provider.addAfkPlayer(playerId, mode, time));
    }
  }

  public static void removeAfkPlayer(final UUID playerId) {
    if (playerId == null)
      return;
    lastActiveCache.remove(playerId);
    afkPlayerCache.remove(playerId);
    if (isSetup) {
      dbExecutor.submit(() -> provider.removeAfkPlayer(playerId));
    }
  }

  public static long getLastActive(final UUID playerId) {
    if (playerId == null)
      return 0L;
    final long time = lastActiveCache.get(playerId);
    if (time > 0)
      return time;
    return 0L;
  }

  public static void updateLastActive(
      final UUID playerId,
      final long lastActive) {
    if (playerId == null)
      return;
    lastActiveCache.put(playerId, lastActive);
  }

  public static boolean containsAfkPlayer(final UUID playerId) {
    if (playerId == null)
      return false;
    return afkPlayerCache.computeIfAbsent(
        playerId,
        id -> {
          if (isSetup) {
            return provider.containsAfkPlayer(id);
          }
          return false;
        });
  }
}
