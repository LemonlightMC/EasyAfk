package com.julizey.easyafk.database;

import com.julizey.easyafk.EasyAFK;
import com.julizey.easyafk.utils.Text;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLManager implements DatabaseManager.DatabaseProvider {

  private Connection connection;

  public MySQLManager() {
    try {
      openConnection();
    } catch (final SQLException ex) {
      Text.warn("Could not connect to MySQL server!");
      ex.printStackTrace();
    }
  }

  public boolean isConnected() {
    try {
      return connection != null && !connection.isClosed();
    } catch (final SQLException ex) {
      Text.warn("MySQL connection is not available!");
      return false;
    }
  }

  public void addAfkPlayer(final UUID playerId, final long lastActive) {
    try {
      final PreparedStatement selectStatement = connection.prepareStatement(
        "SELECT COUNT(*) FROM afk_players WHERE player_id = ?"
      );
      selectStatement.setString(1, playerId.toString());
      final ResultSet resultSet = selectStatement.executeQuery();
      String insertQuery;
      PreparedStatement insertStatement;
      if (resultSet.next() && resultSet.getInt(1) > 0) {
        insertQuery =
          "UPDATE afk_players SET last_active = ? WHERE player_id = ?";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.setLong(1, lastActive);
        insertStatement.setString(2, playerId.toString());
        insertStatement.executeUpdate();
      } else {
        insertQuery =
          "INSERT INTO afk_players (player_id, last_active) VALUES (?, ?)";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.setString(1, playerId.toString());
        insertStatement.setLong(2, lastActive);
        insertStatement.executeUpdate();
      }
    } catch (final SQLException ex) {
      ex.printStackTrace();
      Text.warn("Could not add or update AFK player in MySQL!");
    }
  }

  public void removeAfkPlayer(final UUID playerId) {
    if (!containsAfkPlayer(playerId)) {
      return;
    }
    try {
      final PreparedStatement statement = connection.prepareStatement(
        "DELETE FROM afk_players WHERE player_id = ?"
      );
      statement.setString(1, playerId.toString());
      statement.executeUpdate();
    } catch (final SQLException ex) {
      Text.warn("Could not remove AFK player from MySQL!");
      ex.printStackTrace();
    }
  }

  public boolean containsAfkPlayer(final UUID playerId) {
    try {
      final PreparedStatement statement = connection.prepareStatement(
        "SELECT 1 FROM afk_players WHERE player_id = ?"
      );
      statement.setString(1, playerId.toString());
      final ResultSet resultSet = statement.executeQuery();
      return resultSet.next();
    } catch (final SQLException ex) {
      Text.warn("Could not check if AFK player exists in MySQL!");
      ex.printStackTrace();

      return false;
    }
  }

  public void removeAllAfkPlayers() {
    try (
      PreparedStatement ps = connection.prepareStatement(
        "DELETE FROM afk_players"
      )
    ) {
      ps.executeUpdate();
    } catch (final SQLException ex) {
      ex.printStackTrace();
    }
  }

  public void close() {
    try {
      if (connection == null || connection.isClosed()) {
        return;
      }
      connection.close();
    } catch (final SQLException ex) {
      Text.warn("Could not close MySQL connection!");
      ex.printStackTrace();
    }
  }

  private void openConnection() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      return;
    }
    synchronized (MySQLManager.class) {
      if (connection != null && !connection.isClosed()) {
        return;
      }
      connection =
        DriverManager.getConnection(
          "jdbc:mysql://" +
          EasyAFK.config.host +
          ":" +
          EasyAFK.config.port +
          "/" +
          EasyAFK.config.database,
          EasyAFK.config.username,
          EasyAFK.config.password
        );
    }
  }
}
