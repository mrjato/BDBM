package es.uvigo.esei.sing.bdbm.environment.paths;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import es.uvigo.esei.sing.bdbm.environment.SequenceType;


public class DefaultRepositoryPaths implements RepositoryPaths {
	public static final String DB_DIRECTORY_NAME = "db";
	public static final String FASTA_DIRECTORY_NAME = "fasta";
	public static final String ENTRY_DIRECTORY_NAME = "entry";
	public static final String EXPORT_DIRECTORY_NAME = "export";
	public static final String ORF_DIRECTORY_NAME = "orf";
	
	private File baseDirectory;
	private final Map<SequenceType, File> dbDirectories;
	private final Map<SequenceType, File> fastaDirectories;
	private final Map<SequenceType, File> entryDirectories;
	private final Map<SequenceType, File> exportDirectories;
	private final Map<SequenceType, File> orfDirectories;
	
	public DefaultRepositoryPaths(File baseDirectory) 
	throws IllegalArgumentException {
		if (baseDirectory == null)
			throw new IllegalArgumentException("baseDirectory can't be null");
		
		this.baseDirectory = baseDirectory;
		
		this.dbDirectories = new HashMap<SequenceType, File>();
		this.fastaDirectories = new HashMap<SequenceType, File>();
		this.entryDirectories = new HashMap<SequenceType, File>();
		this.exportDirectories = new HashMap<SequenceType, File>();
		this.orfDirectories = new HashMap<SequenceType, File>();
		
		this.updateDefaultDirectories();
	}

	protected void updateDefaultDirectories() {
		this.dbDirectories.clear();
		this.fastaDirectories.clear();
		this.entryDirectories.clear();
		this.exportDirectories.clear();
		this.orfDirectories.clear();
		
		for (SequenceType sequence : SequenceType.values()) {
			this.dbDirectories.put(sequence, this.defaultDBDirectory(sequence));
			this.fastaDirectories.put(sequence, this.defaultFastaDirectory(sequence));
			this.entryDirectories.put(sequence, this.defaultEntryDirectory(sequence));
			this.exportDirectories.put(sequence, this.defaultExportDirectory(sequence));
			this.orfDirectories.put(sequence, this.defaultORFDirectory(sequence));
		}
	}
	
	protected File defaultDirectory(String dirName, SequenceType sequenceType) {
		return new File(
			new File(this.getBaseDirectory(), dirName),
			sequenceType.getDirectoryExtension()
		);
	}
	
	protected File defaultDBDirectory(SequenceType sequenceType) {
		return this.defaultDirectory(DefaultRepositoryPaths.DB_DIRECTORY_NAME, sequenceType);
	}
	
	protected File defaultFastaDirectory(SequenceType sequenceType) {
		return this.defaultDirectory(DefaultRepositoryPaths.FASTA_DIRECTORY_NAME, sequenceType);
	}
	
	protected File defaultEntryDirectory(SequenceType sequenceType) {
		return this.defaultDirectory(DefaultRepositoryPaths.ENTRY_DIRECTORY_NAME, sequenceType);
	}
	
	protected File defaultExportDirectory(SequenceType sequenceType) {
		return this.defaultDirectory(DefaultRepositoryPaths.EXPORT_DIRECTORY_NAME, sequenceType);
	}
	
	protected File defaultORFDirectory(SequenceType sequenceType) {
		return this.defaultDirectory(DefaultRepositoryPaths.ORF_DIRECTORY_NAME, sequenceType);
	}
	
	@Override
	public File getBaseDirectory() {
		return this.baseDirectory;
	}
	
	@Override
	public boolean checkBaseDirectory(File baseDirectory) {
		
		if (baseDirectory.isDirectory()) {
			final DefaultRepositoryPaths stub = 
				new DefaultRepositoryPaths(baseDirectory);
			
			return stub.getDBNucleotidesDirectory().isDirectory() &&
				stub.getDBProteinsDirectory().isDirectory() &&
				stub.getFastaNucleotidesDirectory().isDirectory() &&
				stub.getFastaProteinsDirectory().isDirectory() &&
				stub.getSearchEntryNucleotidesDirectory().isDirectory() &&
				stub.getSearchEntryProteinsDirectory().isDirectory() &&
				stub.getExportNucleotidesDirectory().isDirectory() &&
				stub.getExportProteinsDirectory().isDirectory() &&
				stub.getORFNucleotidesDirectory().isDirectory();
		} else {
			return false;
		}
	}
	
	@Override
	public void buildBaseDirectory(File baseDirectory) 
	throws IOException {
		if (!baseDirectory.isDirectory() && !baseDirectory.mkdirs()) {
			throw new IOException("Base directory " + baseDirectory + " could not be created");
		} else {
			final DefaultRepositoryPaths stub = 
				new DefaultRepositoryPaths(baseDirectory);
			
			final File[] subdirectories = new File[] {
				stub.getDBNucleotidesDirectory(),
				stub.getDBProteinsDirectory(),
				stub.getFastaNucleotidesDirectory(),
				stub.getFastaProteinsDirectory(),
				stub.getSearchEntryNucleotidesDirectory(),
				stub.getSearchEntryProteinsDirectory(),
				stub.getExportNucleotidesDirectory(),
				stub.getExportProteinsDirectory(),
				stub.getORFNucleotidesDirectory()
			};
			
			for (File subdirectory : subdirectories) {
				if (!subdirectory.isDirectory() && !subdirectory.mkdirs()) {
					throw new IOException("Directory " + subdirectory + " could not be created");
				}
			}
		}
	}
	
	public void setBaseDirectory(File baseDirectory) {
		this.baseDirectory = baseDirectory;
		
		this.updateDefaultDirectories();
	}
	
	@Override
	public File getDBProteinsDirectory() {
		return this.dbDirectories.get(SequenceType.PROTEIN);
	}
	
	public void setDBProteinsDirectory(File directory) {
		this.dbDirectories.put(SequenceType.PROTEIN, directory);
	}
	
	@Override
	public File getDBNucleotidesDirectory() {
		return this.dbDirectories.get(SequenceType.NUCLEOTIDE);
	}
	
	public void setDBNucleotidesDirectory(File directory) {
		this.dbDirectories.put(SequenceType.NUCLEOTIDE, directory);
	}
	
	@Override
	public File getFastaProteinsDirectory() {
		return this.fastaDirectories.get(SequenceType.PROTEIN);
	}
	
	public void setFastaProteinsDirectory(File directory) {
		this.fastaDirectories.put(SequenceType.PROTEIN, directory);
	}
	
	@Override
	public File getFastaNucleotidesDirectory() {
		return this.fastaDirectories.get(SequenceType.NUCLEOTIDE);
	}
	
	public void setFastaNucleotidesDirectory(File directory) {
		this.fastaDirectories.put(SequenceType.NUCLEOTIDE, directory);
	}
	
	@Override
	public File getSearchEntryProteinsDirectory() {
		return this.entryDirectories.get(SequenceType.PROTEIN);
	}
	
	public void setEntryProteinsDirectory(File directory) {
		this.entryDirectories.put(SequenceType.PROTEIN, directory);
	}
	
	@Override
	public File getSearchEntryNucleotidesDirectory() {
		return this.entryDirectories.get(SequenceType.NUCLEOTIDE);
	}
	
	public void setEntryNucleotidesDirectory(File directory) {
		this.entryDirectories.put(SequenceType.NUCLEOTIDE, directory);
	}
	
	@Override
	public File getExportProteinsDirectory() {
		return this.exportDirectories.get(SequenceType.PROTEIN);
	}

	public void setExportProteinsDirectory(File directory) {
		this.exportDirectories.put(SequenceType.PROTEIN, directory);
	}
	
	@Override
	public File getExportNucleotidesDirectory() {
		return this.exportDirectories.get(SequenceType.NUCLEOTIDE);
	}
	
	public void setExportNucleotidesDirectory(File directory) {
		this.exportDirectories.put(SequenceType.NUCLEOTIDE, directory);
	}
	
	public void setORFProteinsDirectory(File directory) {
		this.orfDirectories.put(SequenceType.PROTEIN, directory);
	}
	
	@Override
	public File getORFNucleotidesDirectory() {
		return this.orfDirectories.get(SequenceType.NUCLEOTIDE);
	}
	
	public void setORFNucleotidesDirectory(File directory) {
		this.orfDirectories.put(SequenceType.NUCLEOTIDE, directory);
	}
	
	public void setProperties(Map<String, String> props) {
		if (props.containsKey(RepositoryPaths.BASE_DIRECTORY_PROP)) {
			this.setBaseDirectory(
				new File(props.get(RepositoryPaths.BASE_DIRECTORY_PROP))
			);
		}
		if (props.containsKey(RepositoryPaths.DB_PROTEINS_DIRECTORY_PROP)) {
			this.setDBProteinsDirectory(
				new File(props.get(RepositoryPaths.DB_PROTEINS_DIRECTORY_PROP))
			);
		}
		if (props.containsKey(RepositoryPaths.DB_NUCLEOTIDES_DIRECTORY_PROP)) {
			this.setDBNucleotidesDirectory(
				new File(props.get(RepositoryPaths.DB_NUCLEOTIDES_DIRECTORY_PROP))
			);
		}
		if (props.containsKey(RepositoryPaths.FASTA_PROTEINS_DIRECTORY_PROP)) {
			this.setFastaProteinsDirectory(
				new File(props.get(RepositoryPaths.FASTA_PROTEINS_DIRECTORY_PROP))
			);
		}
		if (props.containsKey(RepositoryPaths.FASTA_NUCLEOTIDES_DIRECTORY_PROP)) {
			this.setFastaNucleotidesDirectory(
				new File(props.get(RepositoryPaths.FASTA_NUCLEOTIDES_DIRECTORY_PROP))
			);
		}
		if (props.containsKey(RepositoryPaths.ENTRY_PROTEINS_DIRECTORY_PROP)) {
			this.setEntryProteinsDirectory(
				new File(props.get(RepositoryPaths.ENTRY_PROTEINS_DIRECTORY_PROP))
			);
		}
		if (props.containsKey(RepositoryPaths.ENTRY_NUCLEOTIDES_DIRECTORY_PROP)) {
			this.setEntryNucleotidesDirectory(
				new File(props.get(RepositoryPaths.ENTRY_NUCLEOTIDES_DIRECTORY_PROP))
			);
		}
		if (props.containsKey(RepositoryPaths.EXPORT_PROTEINS_DIRECTORY_PROP)) {
			this.setExportProteinsDirectory(
				new File(props.get(RepositoryPaths.EXPORT_PROTEINS_DIRECTORY_PROP))
			);
		}
		if (props.containsKey(RepositoryPaths.EXPORT_NUCLEOTIDES_DIRECTORY_PROP)) {
			this.setExportNucleotidesDirectory(
				new File(props.get(RepositoryPaths.EXPORT_NUCLEOTIDES_DIRECTORY_PROP))
			);
		}
		if (props.containsKey(RepositoryPaths.ORF_NUCLEOTIDES_DIRECTORY_PROP)) {
			this.setORFNucleotidesDirectory(
				new File(props.get(RepositoryPaths.ORF_NUCLEOTIDES_DIRECTORY_PROP))
			);
		}
	}
}
