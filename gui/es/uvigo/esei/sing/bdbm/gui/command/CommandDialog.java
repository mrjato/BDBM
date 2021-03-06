package es.uvigo.esei.sing.bdbm.gui.command;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ServiceLoader;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang3.StringEscapeUtils;

import es.uvigo.ei.sing.yaacli.Command;
import es.uvigo.ei.sing.yaacli.DefaultValuedOption;
import es.uvigo.ei.sing.yaacli.Option;
import es.uvigo.ei.sing.yaacli.Parameters;
import es.uvigo.esei.sing.bdbm.controller.BDBMController;
import es.uvigo.esei.sing.bdbm.gui.command.input.DefaultInputComponentBuilder;
import es.uvigo.esei.sing.bdbm.gui.command.input.InputComponentBuilder;

public class CommandDialog extends JDialog {
	private final static long serialVersionUID = 1L;
	
	private final static ImageIcon ICON_HELP = 
		new ImageIcon(CommandDialog.class.getResource("images/help-about.png"));
	
	private final static ServiceLoader<InputComponentBuilder> SERVICE_LOADER = 
		ServiceLoader.load(InputComponentBuilder.class);

	private static final InputComponentBuilder DEFAULT_INPUT_COMPONENT_BUILDER = 
		new DefaultInputComponentBuilder();

	protected final BDBMController controller;
	protected final Command command;
	private final Parameters defaultParameters;

	private JButton btnOk;

	protected ParameterValues parameterValues;

	public CommandDialog(BDBMController controller, Command command) {
		this(controller, command, null);
	}
	
	public CommandDialog(BDBMController controller, Command command, Parameters defaultParameters) {
		this.controller = controller;
		this.command = command;
		this.defaultParameters = defaultParameters;
		
		this.init();
	}
	
	protected boolean hasDefaultOption(Option<?> option) {
		return this.defaultParameters != null && this.defaultParameters.hasOption(option);
	}
	
	protected String getDefaultOptionString(Option<?> option) {
		if (this.hasDefaultOption(option)) {
			return this.defaultParameters.getSingleValueString(option);
		} else {
			return null;
		}
	}
	
	protected List<String> getDefaulOptionStringList(Option<?> option) {
		if (this.hasDefaultOption(option)) {
			return this.defaultParameters.getAllValuesString(option);
		} else {
			return null;
		}
	}

	private void init() {
		this.setTitle(this.command.getDescriptiveName());
		
		final JPanel panel = new JPanel(new BorderLayout());
		panel.setLayout(new BorderLayout());
	
		final JTextArea taDescription = new JTextArea(this.command.getDescription());
		taDescription.setEditable(false);
		taDescription.setWrapStyleWord(true);
		taDescription.setLineWrap(true);
		taDescription.setMargin(new Insets(10, 8, 10, 8));
		
		final JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panelButtons.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
		
		btnOk = new JButton("Ok");
		final JButton btnCancel = new JButton("Cancel");
		panelButtons.add(btnOk);
		panelButtons.add(btnCancel);
		
		final JPanel panelOptions = new JPanel();
		panelOptions.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		
		final GroupLayout layout = new GroupLayout(panelOptions);
		panelOptions.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		final SequentialGroup verticalGroup = layout.createSequentialGroup();
		layout.setVerticalGroup(verticalGroup);
		final SequentialGroup horizontalGroup = layout.createSequentialGroup();
		layout.setHorizontalGroup(horizontalGroup);
		final ParallelGroup pgLblName = layout.createParallelGroup(Alignment.LEADING, false);
		final ParallelGroup pgText = layout.createParallelGroup();
		final ParallelGroup pgLblDescription = layout.createParallelGroup(Alignment.CENTER, false);
		horizontalGroup.addGroup(pgLblName).addGroup(pgText).addGroup(pgLblDescription);
		
		this.parameterValues = new ParameterValues(this.command.getOptions());
		this.updateButtonOk();
		this.parameterValues.addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				CommandDialog.this.updateButtonOk();
			}
		});
		for (Option<?> option : this.command.getOptions()) {
			final JLabel lblName = new JLabel(option.getParamName());
			lblName.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
			final JLabel lblDescription = new JLabel(CommandDialog.ICON_HELP);
			
			final String description = StringEscapeUtils.escapeHtml4(option.getDescription())
				.replaceAll("\n", "<br/>")
				.replaceAll("\t", "&nbsp;&nbsp;&nbsp;");
			
			lblDescription.setToolTipText("<html>" + description + "</html>");
			
			final Component inputComponent = this.createComponentForOption(option, parameterValues);
			verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER, false)
				.addComponent(lblName, Alignment.LEADING)
				.addComponent(inputComponent)
				.addComponent(lblDescription)
			);
			
			pgLblName.addComponent(lblName);
			pgText.addComponent(inputComponent);
			pgLblDescription.addComponent(lblDescription);
			
			inputComponent.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentShown(ComponentEvent e) {
					lblName.setVisible(true);
					lblDescription.setVisible(true);
				}
				
				@Override
				public void componentHidden(ComponentEvent e) {
					lblName.setVisible(false);
					lblDescription.setVisible(false);
				}
			});
			lblName.setVisible(inputComponent.isVisible());
			lblDescription.setVisible(inputComponent.isVisible());
		}
		
		panel.add(taDescription, BorderLayout.NORTH);
		panel.add(panelOptions, BorderLayout.CENTER);
		panel.add(panelButtons, BorderLayout.SOUTH);
		
		this.setContentPane(panel);
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					CommandDialog.this.setVisible(false);
				}
			}
		});
		
		this.btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CommandDialog.this.setVisible(false);
				CommandDialog.this.dispose();
				
				final CommandExecutionDialog executionDialog = 
					new CommandExecutionDialog(getOwner(), command, parameterValues);
				executionDialog.pack();
				executionDialog.setLocationRelativeTo(getOwner());
				executionDialog.startExecution();
			}
		});
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CommandDialog.this.setVisible(false);
			}
		});
	}
	
	private static class MICBParameterValuesReceiver 
	extends Observable
	implements ParameterValuesReceiver {
		private Option<?> option = null;
		private String lastValue = null;
		
		@Override
		public boolean hasOption(Option<?> option) {
			return this.option != null && this.option.equals(option);
//			throw new UnsupportedOperationException();
		}
		
		@Override
		public String getValue(Option<?> option) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean hasValue(Option<?> option) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public List<String> getValues(Option<?> option) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean removeValue(Option<?> option) {
			if (this.option != null && this.option.equals(option)) {
				this.option = null;
				this.lastValue = null;
				return true;
			} else {
				return false;
			}
		}
		
		public boolean hasValue() {
			return this.lastValue != null;
		}
		
		public String getValue() {
			return this.lastValue;
		}
		
		@Override
		public void setValue(Option<?> option, String value) {
			this.lastValue = value;
			this.option = option;
			this.setChanged();
			this.notifyObservers();
		}
	
		@Override
		public void setValue(Option<?> option, List<String> value) {
			throw new IllegalStateException("Multiple values not supported");
		}
	}
	
	protected static  List<String> listModelToList(ListModel<Object> listModel) {
		final List<String> list = new ArrayList<String>(listModel.getSize());
		
		for (int i = 0; i < listModel.getSize(); i++) {
			list.add(listModel.getElementAt(i).toString());
		}
		
		return list;
	}
	
	protected <T> Component createComponentForOption(
		final Option<T> option, final ParameterValuesReceiver receiver
	) {
		if (option.isMultiple()) {
			final JPanel panelInput = new JPanel(new BorderLayout());
			final JPanel panelInner = new JPanel(new BorderLayout());
			
			final MICBParameterValuesReceiver micbReceiver = 
				new MICBParameterValuesReceiver();
			
			if (receiver.hasOption(option)) {
				micbReceiver.setValue(option, receiver.getValue(option));
				receiver.removeValue(option);
			}
			
			final Component singleInputComponent = 
				this.createComponentForOption(
					new Option<T>(
						option.getParamName(), 
						option.getShortName(), 
						option.getDescription(), 
						option.isOptional(), 
						option.requiresValue(), 
						false,
						option.getConverter()
					),
					micbReceiver
				);
			
			final JPanel panelButtons = new JPanel(new GridLayout(1, 3));
			final JButton btnAdd = new JButton("Add");
			final JButton btnRemove = new JButton("Remove");
			final JButton btnClear = new JButton("Clear");
			btnAdd.setEnabled(micbReceiver.hasValue());
			btnRemove.setEnabled(false);
			btnClear.setEnabled(false);
			
			panelButtons.add(btnAdd);
			panelButtons.add(btnRemove);
			panelButtons.add(btnClear);
			
			final DefaultListModel<Object> listModel = new DefaultListModel<>();
			this.setMultipleDefaultParameters(option, receiver, listModel);
			final JList<Object> listValues = new JList<>(listModel);
			
			panelInner.add(panelButtons, BorderLayout.NORTH);
			panelInner.add(new JScrollPane(listValues), BorderLayout.CENTER);
			
			panelInput.add(singleInputComponent, BorderLayout.NORTH);
			panelInput.add(panelInner, BorderLayout.CENTER);
			
			micbReceiver.addObserver(new Observer() {
				@Override
				public void update(Observable o, Object arg) {
					btnAdd.setEnabled(micbReceiver.hasValue());
				}
			});
			listValues.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					btnRemove.setEnabled(!listValues.isSelectionEmpty());
				}
			});
			listModel.addListDataListener(new ListDataListener() {
				protected void updateOption() {
					CommandDialog.this.updateMultipleValues(option, listModel, receiver);
					
					btnClear.setEnabled(!listModel.isEmpty());
					CommandDialog.this.updateButtonOk();
				}
				
				@Override
				public void intervalRemoved(ListDataEvent e) {
					this.updateOption();
				}
				
				@Override
				public void intervalAdded(ListDataEvent e) {
					this.updateOption();
				}
				
				@Override
				public void contentsChanged(ListDataEvent e) {
					this.updateOption();
				}
			});
			btnAdd.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					CommandDialog.this.addMultipleValue(
						option, listModel, micbReceiver.getValue()
					);
				}
			});
			btnRemove.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					CommandDialog.this.removeMultipleValue(
						option, listModel, listValues.getSelectedValuesList().toArray()
					);
				}
			});
			btnClear.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					CommandDialog.this.clearMultipleValue(option, listModel, receiver);
				}
			});
			
			return panelInput;
		} else {
			InputComponentBuilder builder = CommandDialog.DEFAULT_INPUT_COMPONENT_BUILDER;
			for (InputComponentBuilder icb : CommandDialog.SERVICE_LOADER) {
				if (icb.canHandle(option)) {
					builder = icb;
					break;
				}
			}
			
			this.setSingleDefaultValueOption(option, receiver);
			
			return builder.createFor(this, option, receiver);
		}
	}
	
	protected void addMultipleValue(Option<?> option, DefaultListModel<Object> listModel, String value) {
		listModel.addElement(value);
	}

	protected void removeMultipleValue(Option<?> option, DefaultListModel<Object> listModel, Object[] values) {
		for (Object value : values) {
			listModel.removeElement(value);
		}
	}
	
	protected void clearMultipleValue(Option<?> option, DefaultListModel<Object> listModel, ParameterValuesReceiver receiver) {
		listModel.clear();

		receiver.setValue(option, (List<String>) null);
	}
	
	protected void updateMultipleValues(Option<?> option, DefaultListModel<Object> listModel, ParameterValuesReceiver receiver) {
		receiver.setValue(option, CommandDialog.listModelToList(listModel));
	}
	
	protected <T> void setMultipleDefaultParameters(
		final Option<T> option,
		final ParameterValuesReceiver receiver,
		final DefaultListModel<Object> listModel
	) {
		if (this.defaultParameters != null && this.defaultParameters.hasOption(option)) {
			final List<String> values = this.defaultParameters.getAllValuesString(option);
			
			receiver.setValue(option, values);
			for (String value : values) {
				listModel.addElement(value);
			}
		}
	}

	protected <T> void setSingleDefaultValueOption(
		final Option<T> option,
		final ParameterValuesReceiver receiver
	) {
		if (this.defaultParameters != null && this.defaultParameters.hasOption(option)) {
			receiver.setValue(option, this.defaultParameters.getSingleValueString(option));
		} else if (option instanceof DefaultValuedOption<?>) {
			receiver.setValue(option, ((DefaultValuedOption<?>) option).getDefaultValue());
		}
	}

	protected void updateButtonOk() {
		this.btnOk.setEnabled(this.parameterValues.isComplete());
	}
}
