package com.hollow.controllers.manager;

import com.badlogic.gdx.Gdx;
import com.hollow.models.data.GameData;
import com.hollow.models.data.SettingData;
import com.hollow.models.data.SlotData;
import com.hollow.models.entities.knight.Charm;
import com.hollow.models.language.Language;

import java.sql.*;

public class SaveManager {
    private static final String DB_PATH = Gdx.files.local("save/game_data.db").file().getAbsolutePath();
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;

    static {
        try {
            Gdx.files.local("save").mkdirs();
            initDatabase();
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Error initializing SQLite DB", e);
        }
    }

    private static void initDatabase() throws Exception {
        String createGameState = "CREATE TABLE IF NOT EXISTS GameState (id INTEGER PRIMARY KEY, "
            + "activeSlotId INTEGER);";

        String createSettings = "CREATE TABLE IF NOT EXISTS Settings (id INTEGER PRIMARY KEY, "
            + "musicVolume REAL, sfxVolume REAL, brightness REAL, isMusicOn INTEGER, "
            + "isSfxOn INTEGER, lang TEXT, keyLeft INTEGER, keyRight INTEGER, keyUp INTEGER, "
            + "keyDown INTEGER, keyJump INTEGER, keyDash INTEGER, keyAttack INTEGER, keyFocus INTEGER);";

        String createAchievements = "CREATE TABLE IF NOT EXISTS Achievements (name TEXT PRIMARY KEY);";

        String createSlots = "CREATE TABLE IF NOT EXISTS Slots (id INTEGER PRIMARY KEY, "
            + "isEmpty INTEGER, location TEXT, mask INTEGER, fkDefeated INTEGER, "
            + "fkDeathX REAL, fkDeathY REAL, playTime REAL);";

        String createSlotCharms = "CREATE TABLE IF NOT EXISTS SlotCharms (slotId INTEGER, "
            + "charm TEXT, isEquipped INTEGER, PRIMARY KEY (slotId, charm));";

        String createSlotEnemies = "CREATE TABLE IF NOT EXISTS SlotEnemies (slotId INTEGER, "
            + "enemy TEXT, PRIMARY KEY (slotId, enemy));";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createGameState);
            stmt.execute(createSettings);
            stmt.execute(createAchievements);
            stmt.execute(createSlots);
            stmt.execute(createSlotCharms);
            stmt.execute(createSlotEnemies);
        }
    }

    public static void save(GameData data) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            saveGameState(conn, data);
            saveSettings(conn, data.getSettings());
            saveAchievements(conn, data);
            saveSlotsAndCharms(conn, data);
            conn.commit();
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Error saving to SQLite database", e);
        }
    }

    private static void saveGameState(Connection conn, GameData data) throws SQLException {
        String q = "INSERT OR REPLACE INTO GameState (id, activeSlotId) VALUES (1, ?)";
        try (PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setInt(1, data.getActiveSlotId());
            ps.executeUpdate();
        }
    }

    private static void saveSettings(Connection conn, SettingData s) throws SQLException {
        String q = "INSERT OR REPLACE INTO Settings (id, musicVolume, sfxVolume, brightness, isMusicOn, "
            + "isSfxOn, lang, keyLeft, keyRight, keyUp, keyDown, keyJump, keyDash, keyAttack, keyFocus) "
            + "VALUES (1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setFloat(1, s.getMusicVolume());
            ps.setFloat(2, s.getSfxVolume());
            ps.setFloat(3, s.getBrightness());
            ps.setInt(4, s.isMusicOn() ? 1 : 0);
            ps.setInt(5, s.isSfxOn() ? 1 : 0);
            ps.setString(6, s.getLang().name());
            ps.setInt(7, s.getKeyLeft());
            ps.setInt(8, s.getKeyRight());
            ps.setInt(9, s.getKeyUp());
            ps.setInt(10, s.getKeyDown());
            ps.setInt(11, s.getKeyJump());
            ps.setInt(12, s.getKeyDash());
            ps.setInt(13, s.getKeyAttack());
            ps.setInt(14, s.getKeyFocus());
            ps.executeUpdate();
        }
    }

    private static void saveAchievements(Connection conn, GameData data) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM Achievements");
        }
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO Achievements (name) VALUES (?)")) {
            for (String ach : data.getAchievements().unlockedAchievements) {
                ps.setString(1, ach);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void saveSlotsAndCharms(Connection conn, GameData data) throws SQLException {
        String insertSlot = "INSERT OR REPLACE INTO Slots (id, isEmpty, location, mask, "
            + "fkDefeated, fkDeathX, fkDeathY, playTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String insertCharm = "INSERT INTO SlotCharms (slotId, charm, isEquipped) VALUES (?, ?, ?)";
        String insertEnemy = "INSERT INTO SlotEnemies (slotId, enemy) VALUES (?, ?)";

        try (PreparedStatement psSlot = conn.prepareStatement(insertSlot);
             PreparedStatement psCharmDel = conn.prepareStatement("DELETE FROM SlotCharms");
             PreparedStatement psCharm = conn.prepareStatement(insertCharm);
             PreparedStatement psEnemyDel = conn.prepareStatement("DELETE FROM SlotEnemies");
             PreparedStatement psEnemy = conn.prepareStatement(insertEnemy)) {

            psCharmDel.executeUpdate();
            psEnemyDel.executeUpdate();

            for (int i = 1; i <= 4; i++) {
                SlotData slot = data.getSlot(i);
                if (slot != null) {
                    psSlot.setInt(1, slot.getId());
                    psSlot.setInt(2, slot.isEmpty() ? 1 : 0);
                    psSlot.setString(3, slot.getLocation());
                    psSlot.setInt(4, slot.getMask());
                    psSlot.setInt(5, slot.isFalseKnightDefeated() ? 1 : 0);
                    psSlot.setFloat(6, slot.getFalseKnightDeathX());
                    psSlot.setFloat(7, slot.getFalseKnightDeathY());
                    psSlot.setFloat(8, slot.getPlayTime());
                    psSlot.addBatch();

                    for (Charm c : slot.getUnlockedCharms()) {
                        psCharm.setInt(1, slot.getId());
                        psCharm.setString(2, c.name());
                        psCharm.setInt(3, slot.getEquippedCharms().contains(c, true) ? 1 : 0);
                        psCharm.addBatch();
                    }

                    for (String enemy : data.getKilledEnemyTypes()) {
                        psEnemy.setInt(1, slot.getId());
                        psEnemy.setString(2, enemy);
                        psEnemy.addBatch();
                    }
                }
            }
            psSlot.executeBatch();
            psCharm.executeBatch();
            psEnemy.executeBatch();
        }
    }

    public static GameData load() {
        GameData data = new GameData();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            loadGameState(conn, data);
            loadSettings(conn, data);
            loadAchievements(conn, data);
            loadSlots(conn, data);
            loadSlotCharms(conn, data);
            loadSlotEnemies(conn, data);
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Error loading from SQLite DB, generating new data", e);
            return new GameData();
        }
        return data;
    }

    private static void loadGameState(Connection conn, GameData data) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT activeSlotId FROM GameState WHERE id = 1")) {
            if (rs.next()) data.setActiveSlotId(rs.getInt("activeSlotId"));
        }
    }

    private static void loadSettings(Connection conn, GameData data) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Settings WHERE id = 1")) {
            if (rs.next()) {
                SettingData s = data.getSettings();
                s.setMusicVolume(rs.getFloat("musicVolume"));
                s.setSfxVolume(rs.getFloat("sfxVolume"));
                s.setBrightness(rs.getFloat("brightness"));
                s.setMusicOn(rs.getInt("isMusicOn") == 1);
                s.setSfxOn(rs.getInt("isSfxOn") == 1);
                s.setLang(Language.valueOf(rs.getString("lang")));
                s.setKeyLeft(rs.getInt("keyLeft"));
                s.setKeyRight(rs.getInt("keyRight"));
                s.setKeyUp(rs.getInt("keyUp"));
                s.setKeyDown(rs.getInt("keyDown"));
                s.setKeyJump(rs.getInt("keyJump"));
                s.setKeyDash(rs.getInt("keyDash"));
                s.setKeyAttack(rs.getInt("keyAttack"));
                s.setKeyFocus(rs.getInt("keyFocus"));
            }
        }
    }

    private static void loadAchievements(Connection conn, GameData data) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT name FROM Achievements")) {
            while (rs.next()) {
                data.getAchievements().unlockedAchievements.add(rs.getString("name"));
            }
        }
    }

    private static void loadSlots(Connection conn, GameData data) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Slots")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                SlotData slot = data.getSlot(id);
                if (slot != null) {
                    slot.getUnlockedCharms().clear();
                    slot.getEquippedCharms().clear();
                    data.getKilledEnemyTypes().clear();

                    slot.setEmpty(rs.getInt("isEmpty") == 1);
                    slot.setLocation(rs.getString("location"));
                    slot.setMask(rs.getInt("mask"));
                    slot.setFalseKnightDefeated(rs.getInt("fkDefeated") == 1);
                    slot.setFalseKnightDeathX(rs.getFloat("fkDeathX"));
                    slot.setFalseKnightDeathY(rs.getFloat("fkDeathY"));
                    slot.setPlayTime(rs.getFloat("playTime"));
                }
            }
        }
    }

    private static void loadSlotCharms(Connection conn, GameData data) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM SlotCharms")) {
            while (rs.next()) {
                int slotId = rs.getInt("slotId");
                Charm charm = Charm.valueOf(rs.getString("charm"));
                boolean isEquipped = rs.getInt("isEquipped") == 1;

                SlotData slot = data.getSlot(slotId);
                if (slot != null) {
                    slot.addUnlockedCharm(charm);
                    if (isEquipped) slot.addEquippedCharm(charm);
                }
            }
        }
    }

    private static void loadSlotEnemies(Connection conn, GameData data) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM SlotEnemies")) {
            while (rs.next()) {
                int slotId = rs.getInt("slotId");
                String enemy = rs.getString("enemy");
                SlotData slot = data.getSlot(slotId);
                if (slot != null) {
                    data.registerEnemyKill(enemy);
                }
            }
        }
    }
}
