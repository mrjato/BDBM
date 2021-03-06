package es.uvigo.esei.sing.bdbm.environment.binaries;

import java.io.File;
import java.util.Map;

import es.uvigo.esei.sing.bdbm.environment.BLASTEnvironmentFactory;
import es.uvigo.esei.sing.bdbm.environment.binaries.BLASTBinaries;
import es.uvigo.esei.sing.bdbm.environment.binaries.BLASTType;

public class DefaultBLASTBinaries implements BLASTBinaries {
	private File baseDirectory;
	private String makeBlastDB;
	private String blastDBAliasTool;
	private String blastDBCmd;
	private String blastN;
	private String blastP;
	private String tBlastX;
	private String tBlastN;
	
	public DefaultBLASTBinaries() {
		this(
			null,
			defaultMakeBlastDB(),
			defaultBlastDBAliasTool(),
			defaultBlastDBCmd(),
			defaultBlastN(),
			defaultBlastP(),
			defaultTBlastX(),
			defaultTBlastN()
		);
	}
	
	public DefaultBLASTBinaries(File baseDirectory) {
		this(
			baseDirectory,
			new File(baseDirectory, defaultMakeBlastDB()).getAbsolutePath(),
			new File(baseDirectory, defaultBlastDBAliasTool()).getAbsolutePath(),
			new File(baseDirectory, defaultBlastDBCmd()).getAbsolutePath(),
			new File(baseDirectory, defaultBlastN()).getAbsolutePath(),
			new File(baseDirectory, defaultBlastP()).getAbsolutePath(),
			new File(baseDirectory, defaultTBlastX()).getAbsolutePath(),
			new File(baseDirectory, defaultTBlastN()).getAbsolutePath()
		);
	}
	
	public DefaultBLASTBinaries(String baseDirectoryPath) {
		this(new File(baseDirectoryPath));
	}
	
	public DefaultBLASTBinaries(
		File baseDirectory,
		String makeBlastDB,
		String blastDBAliasTool,
		String blastDBCmd,
		String blastN,
		String blastP,
		String tBlastX,
		String tBlastN
	) {
		this.baseDirectory = baseDirectory;
		this.makeBlastDB = makeBlastDB;
		this.blastDBAliasTool = blastDBAliasTool;
		this.blastDBCmd = blastDBCmd;
		this.blastN = blastN;
		this.blastP = blastP;
		this.tBlastX = tBlastX;
		this.tBlastN = tBlastN;
	}
	
	public void setBaseDirectory(String path) {
		this.setBaseDirectory(new File(path));
	}
	
	public void setBaseDirectory(File baseDirectory) {
		this.baseDirectory = baseDirectory;
		
		if (this.baseDirectory == null) {
			this.makeBlastDB = defaultMakeBlastDB();
			this.blastDBAliasTool = defaultBlastDBAliasTool();
			this.blastDBCmd = defaultBlastDBCmd();
			this.blastN = defaultBlastN();
			this.blastP = defaultBlastP();
			this.tBlastX = defaultTBlastX();
			this.tBlastN = defaultTBlastN();
		} else {
			this.makeBlastDB = new File(baseDirectory, defaultMakeBlastDB()).getAbsolutePath();
			this.blastDBAliasTool = new File(baseDirectory, defaultBlastDBAliasTool()).getAbsolutePath();
			this.blastDBCmd = new File(baseDirectory, defaultBlastDBCmd()).getAbsolutePath();
			this.blastN = new File(baseDirectory, defaultBlastN()).getAbsolutePath();
			this.blastP = new File(baseDirectory, defaultBlastP()).getAbsolutePath();
			this.tBlastX = new File(baseDirectory, defaultTBlastX()).getAbsolutePath();
			this.tBlastN = new File(baseDirectory, defaultTBlastN()).getAbsolutePath();
		}
	}

	@Override
	public File getBaseDirectory() {
		return this.baseDirectory;
	}
	
	@Override
	public String getMakeBlastDB() {
		return this.makeBlastDB;
	}

	@Override
	public String getBlastDBAliasTool() {
		return this.blastDBAliasTool;
	}

	@Override
	public String getBlastDBCmd() {
		return this.blastDBCmd;
	}

	@Override
	public String getBlastN() {
		return this.blastN;
	}
	
	@Override
	public String getBlastP() {
		return this.blastP;
	}

	@Override
	public String getTBlastX() {
		return this.tBlastX;
	}

	@Override
	public String getBlast(BLASTType blastType) {
		return blastType.getBinary(this);
	}

	@Override
	public String getTBlastN() {
		return this.tBlastN;
	}

	public void setTBlastX(String tBlastX) {
		this.tBlastX = tBlastX;
	}

	public void setTBlastN(String tBlastN) {
		this.tBlastN = tBlastN;
	}

	public void setMakeBlastDB(String makeBlastDB) {
		this.makeBlastDB = makeBlastDB;
	}

	public void setBlastDBAliasTool(String blastDBAliasTool) {
		this.blastDBAliasTool = blastDBAliasTool;
	}

	public void setBlastDBCmd(String blastDBCmd) {
		this.blastDBCmd = blastDBCmd;
	}

	public void setBlastN(String blastN) {
		this.blastN = blastN;
	}
	
	private static String defaultMakeBlastDB() {
		return BLASTEnvironmentFactory.createEnvironment().getDefaultMakeBlastDB();
	}
	
	private static String defaultBlastDBAliasTool() {
		return BLASTEnvironmentFactory.createEnvironment().getDefaultBlastDBAliasTool();
	}

	private static String defaultBlastDBCmd() {
		return BLASTEnvironmentFactory.createEnvironment().getDefaultBlastDBCmd();
	}
	
	private static String defaultBlastN() {
		return BLASTEnvironmentFactory.createEnvironment().getDefaultBlastN();
	}
	
	private static String defaultBlastP() {
		return BLASTEnvironmentFactory.createEnvironment().getDefaultBlastP();
	}
	
	private static String defaultTBlastX() {
		return BLASTEnvironmentFactory.createEnvironment().getDefaultTBlastX();
	}
	
	private static String defaultTBlastN() {
		return BLASTEnvironmentFactory.createEnvironment().getDefaultTBlastN();
	}
	
	public void setProperties(Map<String, String> props) {
		if (props.containsKey(BLASTBinaries.BASE_DIRECTORY_PROP)) {
			this.setBaseDirectory(props.get(BLASTBinaries.BASE_DIRECTORY_PROP));
		}
		
		if (props.containsKey(BLASTBinaries.MAKE_BLAST_DB_PROP)) {
			this.setMakeBlastDB(props.get(BLASTBinaries.MAKE_BLAST_DB_PROP));
		}
		if (props.containsKey(BLASTBinaries.BLASTDB_ALIASTOOL_PROP)) {
			this.setBlastDBAliasTool(props.get(BLASTBinaries.BLASTDB_ALIASTOOL_PROP));
		}
		if (props.containsKey(BLASTBinaries.BLASTDB_CMD_PROP)) {
			this.setBlastDBCmd(props.get(BLASTBinaries.BLASTDB_CMD_PROP));
		}
		if (props.containsKey(BLASTBinaries.BLAST_N_PROP)) {
			this.setBlastN(props.get(BLASTBinaries.BLAST_N_PROP));
		}
		if (props.containsKey(BLASTBinaries.BLAST_P_PROP)) {
			this.setBlastN(props.get(BLASTBinaries.BLAST_P_PROP));
		}
		if (props.containsKey(BLASTBinaries.T_BLAST_X_PROP)) {
			this.setTBlastX(props.get(BLASTBinaries.T_BLAST_X_PROP));
		}
		if (props.containsKey(BLASTBinaries.T_BLAST_N_PROP)) {
			this.setTBlastN(props.get(BLASTBinaries.T_BLAST_N_PROP));
		}
	}
}
