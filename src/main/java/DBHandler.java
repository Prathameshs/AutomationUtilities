import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBHandler {
	private Connection conn = null;
	private final static String dbFilePath = "src/main/resources/test.db";

	public static void main(String args[])  {
		DBHandler dbOperator = new DBHandler();
		try {
			// 1. Create a database if it does not exist
			dbOperator.createDatabaseFile(dbFilePath);
			// 2. Setup the DB with some default data
			dbOperator.populateDB();
			// 3 Retrieve all the data from the db
			dbOperator.retrieveAllData();
		}catch(ClassNotFoundException cnfe) {
			System.err.println("");
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if (dbOperator.conn != null)
					dbOperator.conn.close();
			} catch (SQLException e) {
				// connection close failed.
				System.err.println(e);
			}
		}
	}

	public void createDatabaseFile(String filePath) throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		this.conn = DriverManager.getConnection("jdbc:sqlite:" + filePath);
		System.out.println("LOG: Opened database successfully");
	}

	public void populateDB() throws SQLException {
		Statement statement = conn.createStatement();
		statement.setQueryTimeout(5); // set timeout to 5 sec.
		statement.executeUpdate("drop table if exists person");
		System.out.println("LOG: Table Person dropped");
		statement.executeUpdate("create table person (id INT PRIMARY KEY, name VARCHAR (255))");
		System.out.println("LOG: Table Person created");
//		statement.executeUpdate("insert into person(id, name) values ((1, 'Prathamesh'), (2,'Kingshuk'))");
		PreparedStatement preapredStatement = conn.prepareStatement("insert into person(id, name) values (?,?)");
		preapredStatement.setObject(1, 1);
		preapredStatement.setObject(2, "Kingshuk");
		preapredStatement.addBatch();
		preapredStatement.setObject(1, 2);
		preapredStatement.setObject(2, "Prathamesh");
		preapredStatement.addBatch();
		preapredStatement.setObject(1, 3);
		preapredStatement.setObject(2, "Hemant");
		preapredStatement.addBatch();
		int[] result = preapredStatement.executeBatch();
		System.out.println("LOG: Records inserted: " + result.length);
	}

	public void retrieveAllData() throws SQLException{
			Statement statement = conn.createStatement();
			statement.setQueryTimeout(5); // set timeout to 5 sec.
			ResultSet data = statement.executeQuery("select * from person"); 
			System.out.println("LOG: --------- Table Data -----------");
			while(data.next()) {
				System.out.println("Person => ID: "+data.getInt(1)+", Name: "+data.getString("Name"));
			}
			System.out.println("LOG: --------- xxxxxxxxxx -----------");			
	}	

}
