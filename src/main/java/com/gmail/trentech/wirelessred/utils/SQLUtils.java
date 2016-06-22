package com.gmail.trentech.wirelessred.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.spongepowered.api.service.sql.SqlService;

import com.gmail.trentech.wirelessred.Main;

public abstract class SQLUtils {

	protected static SqlService sql;

	protected static DataSource getDataSource() throws SQLException {
		if (sql == null) {
			sql = Main.getGame().getServiceManager().provide(SqlService.class).get();
		}

		return sql.getDataSource("jdbc:h2:./config/wirelessred/data");
	}

	public static void createTables() {

		try {
			Connection connection = getDataSource().getConnection();
			PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Receivers (Location TEXT, Enabled BOOL, Transmitter TEXT, Destination TEXT)");
			statement.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}