package es.uvigo.esei.sing.bdbm.persistence.entities;

import java.io.File;
import java.util.List;
import java.util.Observer;

public interface SearchEntry extends SequenceEntity, Comparable<SearchEntry> {
	public abstract File getDirectory();
	public abstract List<? extends Query> listQueries();
	public abstract Query getQuery(String name);
	
	// Observable methods
	public void addObserver(Observer o);
	public int countObservers();
	public void deleteObserver(Observer o);
	public void deleteObservers();
	public boolean hasChanged();
	public void notifyObservers();
	public void notifyObservers(Object arg);
	
	public interface Query extends SequenceEntity {}
}