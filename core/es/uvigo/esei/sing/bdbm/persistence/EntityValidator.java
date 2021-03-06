package es.uvigo.esei.sing.bdbm.persistence;

import java.util.List;

import es.uvigo.esei.sing.bdbm.persistence.entities.AbstractDatabase;
import es.uvigo.esei.sing.bdbm.persistence.entities.Database;
import es.uvigo.esei.sing.bdbm.persistence.entities.Export;
import es.uvigo.esei.sing.bdbm.persistence.entities.Export.ExportEntry;
import es.uvigo.esei.sing.bdbm.persistence.entities.Fasta;
import es.uvigo.esei.sing.bdbm.persistence.entities.SearchEntry;
import es.uvigo.esei.sing.bdbm.persistence.entities.SearchEntry.Query;

abstract class EntityValidator<T> {
	public abstract void validate(T entity)
	throws EntityValidationException;
	
	public static EntityValidator<Fasta> fasta() {
		return new EntityValidator<Fasta>() {
			@Override
			public void validate(Fasta fasta)
			throws EntityValidationException {
				if (Boolean.valueOf(System.getProperty("entities.fasta.validate", "true"))) {
					if (fasta.getFile().isFile()) {
						try {
							if (!fasta.getType().isValidFastaFile(fasta.getFile())) {
								throw new EntityValidationException("Invalid fasta file: " + fasta.getFile(), fasta);
							}
						} catch (IllegalArgumentException e) {
							throw new EntityValidationException("Unable to load Fasta file: " + fasta.getFile(), e, fasta);
						}
					} else {
						throw new EntityValidationException("Fasta file doesn't exists or isn't a file: "  + fasta.getFile());
					}
				}
			}
		};
	}
	
	public static EntityValidator<Database> db() {
		return new EntityValidator<Database>() {
			protected boolean suffixValidation(Database database) {
				return AbstractDatabase.isAggregatedDatabase(database) ||
						AbstractDatabase.isNormalDatabase(database);
			}
			
			@Override
			public void validate(Database database)
			throws EntityValidationException {
				if (Boolean.valueOf(System.getProperty("entities.database.validate", "true"))) {
					if (database.getDirectory().isDirectory()) {
						switch (database.getType()) {
						case NUCLEOTIDE:
							if (!this.suffixValidation(database))
								throw new EntityValidationException("Missing database files in " + database.getName(), database);
							break;
						case PROTEIN:
							if (!this.suffixValidation(database))
								throw new EntityValidationException("Missing database files in " + database.getName(), database);
							break;
						default:
							throw new IllegalStateException("Unknown database type");
						}
					} else {
						throw new EntityValidationException("Database file doesn't exists or isn't a directory: " + database.getDirectory(), database);
					}
				}
			}
		};
	}

	//TODO: Improve directory validation
	public static EntityValidator<SearchEntry> searchEntry() {
		return new EntityValidator<SearchEntry>() {
			@Override
			public void validate(SearchEntry searchEntry)
			throws EntityValidationException {
				if (Boolean.valueOf(System.getProperty("entities.searchentry.validate", "true"))) {
					if (searchEntry.getDirectory().isDirectory()) {
						final List<? extends Query> queries = searchEntry.listQueries();
						
						if (queries.isEmpty()) {
							throw new EntityValidationException("Search entry file hasn't any query: " + searchEntry.getDirectory(), searchEntry);
						} else {
							for (Query query : queries) {
								if (query.getBaseFile().isFile()) {
									return;
								}
							}
							
							throw new EntityValidationException("Search entry file hasn't any valid query: " + searchEntry.getDirectory(), searchEntry);
						}
					} else {
						throw new EntityValidationException("Search entry file doesn't exists or isn't a directory: " + searchEntry.getDirectory(), searchEntry);
					}
				}
			}
		};
	}

	//TODO: Improve directory validation
	public static EntityValidator<Export> export() {
		return new EntityValidator<Export>() {
			@Override
			public void validate(Export export)
			throws EntityValidationException {
				if (Boolean.valueOf(System.getProperty("entities.export.validate", "true"))) {
					if (export.getDirectory().isDirectory()) {
						final List<? extends ExportEntry> entries = export.listEntries();
						
						if (entries.isEmpty()) {
							throw new EntityValidationException("Export file hasn't any entry: " + export.getDirectory(), export);
						} else {
							for (ExportEntry entry : entries) {
								if (entry.getOutFile().isFile()/* && entry.getSequenceFiles().length > 0*/) {
									return;
								}
							}
							
							throw new EntityValidationException("Export file hasn't any valid entry.");
						}
					} else {
						throw new EntityValidationException("Export file doesn't exists or isn't a directory: " + export.getDirectory(), export);
					}
				}
			}
		};
	}
}
