package es.uvigo.esei.sing.bdbm.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.AppenderAttachable;
import es.uvigo.esei.sing.bdbm.LogConfiguration;
import es.uvigo.esei.sing.bdbm.environment.SequenceType;
import es.uvigo.esei.sing.bdbm.gui.repository.OperationsRepositoryListener;
import es.uvigo.esei.sing.bdbm.gui.repository.RepositoryTreeModel;
import es.uvigo.esei.sing.bdbm.gui.repository.RepositoryTreeRenderer;
import es.uvigo.esei.sing.bdbm.gui.repository.RepositoryTreeModel.TextFileMutableTreeNode;
import es.uvigo.esei.sing.bdbm.gui.tabpanel.CloseableJTabbedPane;
import es.uvigo.esei.sing.bdbm.gui.tabpanel.TabCloseAdapter;
import es.uvigo.esei.sing.bdbm.gui.tabpanel.TabCloseEvent;
import es.uvigo.esei.sing.bdbm.persistence.BDBMRepositoryManager;

public class BDBMMainPanel extends JPanel {
	private static final String TAB_LABEL_NUCLEOTIDE = "Nucleotide";
	private static final String TAB_LABEL_PROTEIN = "Protein";

	private static final long serialVersionUID = 1L;
	
	private final JTree treeNucleotide;
	private final JTree treeProtein;

	private final CloseableJTabbedPane tbMain;
	private final JTabbedPane tpData;
	
	public BDBMMainPanel(BDBMGUIController controller) {
		super(new BorderLayout());
		
		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		splitPane.setContinuousLayout(true);
		splitPane.setDividerLocation(240);
		
		this.tpData = new JTabbedPane();
		this.tbMain = new CloseableJTabbedPane(JTabbedPane.NORTH, JTabbedPane.SCROLL_TAB_LAYOUT);
		
		
		this.treeNucleotide = createRepositoryTree(
			SequenceType.NUCLEOTIDE, 
			controller.getManager().getRepositoryManager(),
			BDBMMainPanel.TAB_LABEL_NUCLEOTIDE
		);
		this.treeProtein = createRepositoryTree(
			SequenceType.PROTEIN,
			controller.getManager().getRepositoryManager(),
			BDBMMainPanel.TAB_LABEL_PROTEIN
		);
		
		this.treeNucleotide.addMouseListener(new OperationsRepositoryListener(controller));
		this.treeProtein.addMouseListener(new OperationsRepositoryListener(controller));
		
		this.treeNucleotide.setDoubleBuffered(true);
		this.treeProtein.setDoubleBuffered(true);
		this.treeNucleotide.setShowsRootHandles(true);
		this.treeProtein.setShowsRootHandles(true);
		
		final TextFileMouseListener textFileMouseListener = 
			new TextFileMouseListener(this.tbMain);
		this.treeNucleotide.addMouseListener(textFileMouseListener);
		this.treeProtein.addMouseListener(textFileMouseListener);
		
		this.tpData.addTab(
			BDBMMainPanel.TAB_LABEL_NUCLEOTIDE, 
			new JScrollPane(this.treeNucleotide)
		);
		this.tpData.addTab(
			BDBMMainPanel.TAB_LABEL_PROTEIN, 
			new JScrollPane(this.treeProtein)
		);

		final JComponent componentMain;
		
		if (BDBMMainPanel.hasAnyExecutionAppender()) {
			final JSplitPane splitMain = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			splitMain.setOneTouchExpandable(true);
			splitMain.setContinuousLayout(true);
			splitMain.setDividerLocation(400);
	
			final JTabbedPane tpLoggers = new JTabbedPane();
			
			if (hasExecutionAppender(LogConfiguration.EXECUTION_STD_MARKER_LABEL)) {
				final PanelLogger standardPanelLogger = new PanelLogger(
					getExecutionAppender(LogConfiguration.EXECUTION_STD_MARKER_LABEL)
				);
				standardPanelLogger.setForeground(new Color(0, 224, 0));
				tpLoggers.addTab("Standard Log", standardPanelLogger);
			}
			
			if (hasExecutionAppender(LogConfiguration.EXECUTION_ERROR_MARKER_LABEL)) {
				final PanelLogger errorPanelLogger = new PanelLogger(
					getExecutionAppender(LogConfiguration.EXECUTION_ERROR_MARKER_LABEL)
				);
				errorPanelLogger.setForeground(new Color(224, 0, 0));
				tpLoggers.addTab("Error Log", errorPanelLogger);
			}
			
			splitMain.setTopComponent(tbMain);
			splitMain.setBottomComponent(tpLoggers);
		
			componentMain = splitMain;
		} else {
			componentMain = tbMain;
		}
		
		splitPane.setLeftComponent(tpData);
		splitPane.setRightComponent(componentMain);
		
		this.add(splitPane, BorderLayout.CENTER);
	}
	
	private JTree createRepositoryTree(
		SequenceType sequenceType, 
		BDBMRepositoryManager repositoryManager,
		final String tabTitle
	) {
		final JTree tree = new JTree(
			new RepositoryTreeModel(sequenceType, repositoryManager)
		);
		tree.setCellRenderer(new RepositoryTreeRenderer());
		tree.setRootVisible(false);
		tree.setExpandsSelectedPaths(true);
		tree.getSelectionModel().setSelectionMode(
			TreeSelectionModel.SINGLE_TREE_SELECTION
		);
		tree.getModel().addTreeModelListener(new TreeModelListener() {
			@Override
			public void treeNodesInserted(final TreeModelEvent e) {
				final TreePath path = e.getTreePath().pathByAddingChild(e.getChildren()[0]);
				
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						tree.setSelectionPath(path);
						
						tpData.setSelectedIndex(tpData.indexOfTab(tabTitle));
					}
				});
			}
			
			@Override
			public void treeNodesChanged(TreeModelEvent e) {}
			@Override
			public void treeNodesRemoved(TreeModelEvent e) {}
			@Override
			public void treeStructureChanged(TreeModelEvent e) {}
		});
		
		return tree;
	}
	
	private final static class TextFileMouseListener extends MouseAdapter {
		private final CloseableJTabbedPane tabbedPane;
		private final Map<File, TextFileViewer> views;
		
		public TextFileMouseListener(CloseableJTabbedPane tabbedPane) {
			this.tabbedPane = tabbedPane;
			this.views = Collections.synchronizedMap(new HashMap<File, TextFileViewer>());
			
			this.tabbedPane.addTabCloseListener(new TabCloseAdapter() {
				@Override
				public void tabClosing(TabCloseEvent event) {
					TextFileMouseListener.this.removeView(event.getTabIndex());
				}
			});
		}
		
		private void removeView(int index) {
			synchronized (this.tabbedPane) {
				final Component component = this.tabbedPane.getComponentAt(index);
				
				for (Map.Entry<File, TextFileViewer> viewer : this.views.entrySet()) {
					if (viewer.getValue().equals(component)) {
						this.views.remove(viewer.getKey());
						break;
					}
				}
			}
		}

		private TextFileViewer addView(final File file, final Icon icon, final String tabName) {
			synchronized (this.tabbedPane) {
				if (!this.views.containsKey(file)) {
					TextFileViewer viewer;
					try {
						viewer = new TextFileViewer(file);
						this.tabbedPane.addTab(tabName, icon, viewer);
						this.views.put(file, viewer);
					} catch (IOException e) {
						JOptionPane.showMessageDialog(
							this.tabbedPane, 
							"Error reading data file in file '" + file.getAbsolutePath() + "': " + e.getMessage(), 
							"Error Reading File", 
							JOptionPane.ERROR_MESSAGE
						);
						return null;
					} catch (IllegalArgumentException iae) {
						JOptionPane.showMessageDialog(
							this.tabbedPane, 
							"Invalid or missing file: " + file.getAbsolutePath(), 
							"Invalid File", 
							JOptionPane.ERROR_MESSAGE
						);
						return null;
					}
				}
				
				return this.views.get(file);
			}
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2 && e.getSource() instanceof JTree) {
				final JTree tree = (JTree) e.getSource();
				
				final TreePath path = tree.getClosestPathForLocation(e.getX(), e.getY());
				
				final Object value = path.getLastPathComponent();
				if (value instanceof TextFileMutableTreeNode) {
					final TextFileMutableTreeNode<?> node = 
						(TextFileMutableTreeNode<?>) value;
					
					final String tabName = node.getUserObject().toString();
					
					final TextFileViewer view = addView(node.getFile(), node.getIcon(), tabName);
					if (view != null)
						this.tabbedPane.setSelectedComponent(view);
				}
			}
		}
	}

	private static boolean hasAnyExecutionAppender() {
		final Logger root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		
		if (root instanceof AppenderAttachable) {
			final AppenderAttachable<?> rootAA = (AppenderAttachable<?>) root;
			
			return rootAA.getAppender(LogConfiguration.EXECUTION_STD_MARKER_LABEL) instanceof ExecutionObservableAppender ||
				rootAA.getAppender(LogConfiguration.EXECUTION_ERROR_MARKER_LABEL) instanceof ExecutionObservableAppender;
		} else {
			return false;
		}
	}
	
	private static boolean hasExecutionAppender(String appenderName) {
		return getExecutionAppender(appenderName) != null;
	}

	private static ExecutionObservableAppender getExecutionAppender(
		final String appenderName
	) {
		final Logger root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		
		if (root instanceof AppenderAttachable) {
			final AppenderAttachable<?> rootAA = (AppenderAttachable<?>) root;
			
			final Appender<?> appender = rootAA.getAppender(appenderName);
			
			if (appender instanceof ExecutionObservableAppender) {
				return (ExecutionObservableAppender) appender;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
