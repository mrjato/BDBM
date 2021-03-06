package es.uvigo.esei.sing.bdbm;

import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarkerFactory;

public class LogConfiguration {
	public static final String EXECUTION_STD_MARKER_LABEL = "EXECUTION_STD";
	public static final String EXECUTION_ERROR_MARKER_LABEL = "EXECUTION_ERROR";
	
	public static final Marker MARKER_EXECUTION_STD;
	public static final Marker MARKER_EXECUTION_ERROR;
	
	static {
		final BasicMarkerFactory markerFactory = new BasicMarkerFactory();
		MARKER_EXECUTION_STD = markerFactory.getMarker(LogConfiguration.EXECUTION_STD_MARKER_LABEL);
		MARKER_EXECUTION_ERROR = markerFactory.getMarker(LogConfiguration.EXECUTION_ERROR_MARKER_LABEL);
	}
}
