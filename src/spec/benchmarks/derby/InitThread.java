package spec.benchmarks.derby;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import spec.harness.StopBenchmarkException;

class InitThread extends Thread {
	Connection connection;	
	private DataReader dataReader;	
	private int accounts;

	public InitThread(int databaseIndex, DataReader dataReader, int accounts)
			throws SQLException {
		connection = DerbyHarness.getNestedConnection(databaseIndex);
		this.dataReader = dataReader;
		this.accounts = accounts;
	}

	public void run() {
		try {
			
			PreparedStatement[] pstmt = new PreparedStatement[DerbyHarness.tableNumber];
			for (int i = 0; i < DerbyHarness.tableNumber; i++) {
				pstmt[i] = connection.prepareStatement(Utils
						.getInsertIntoDurationQuery(DerbyHarness.scale, i));
			}
			DataReader.Data[] data = new DataReader.Data[Utils.INIT_ARRAYS_SIZE];
			for (int i = 0; i < data.length; i++) {
				data[i] = dataReader.new Data();
			}
			for (int i = 0; i < DerbyHarness.tableNumber; i++) {				
				while (true) {
					int[] info = dataReader.getData(data, i);
					if (info[3] == -1) {
						break;
					}					
					if (info[0] == 0) {
						if (DerbyHarness.trans) {
							connection.commit();
						}
						throw new IOException("cannot read input files");
					} else {
						for (int index = 0; index < info[0]; index++) {
							int base = info[1] + index * Main.THREADSPERDB;
							for (int offset = 0; offset < Main.THREADSPERDB; offset++) {
								int id = base + offset;							
								DataReader.Data currentData = data[index];
								pstmt[i].setInt(1, id);
								pstmt[i].setInt(2, id % accounts);
								for (int j = 0; j < DerbyHarness.scale; j++) {
									pstmt[i].setString(3 + j,
											currentData.durations[j]);
								}

								pstmt[i].setBytes(3 + DerbyHarness.scale,
										currentData.info);
								pstmt[i].executeUpdate();
								pstmt[i].clearWarnings();
							}
						}
						if (DerbyHarness.trans) {
							connection.commit();
						}
					}
				}
				if (DerbyHarness.trans) {
					connection.commit();
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
			throw new StopBenchmarkException("Cannot initialize database");
		} finally {			
			DerbyHarness.connectClose(connection);			
		}
	}
}
