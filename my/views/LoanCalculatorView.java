/*
 * Author : Bhojal Gelda
 * Purpose: This is the main frame of the application.
 */

package my.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JTable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import my.beans.MonthlyRowData;
import my.beans.SingletonFieldDataHolder;
import my.controllers.MyFrontController;

import javax.swing.SwingConstants;

public class LoanCalculatorView extends JFrame {
	
	private Locale setLocale=Locale.US;
	private static double DEFAULT_AMOUNT = 0.0;
	private static int DEFAULT_PERIOD = 0;
	private static double DEFAULT_RATE=0.00;
	private Object[][] dataValues; // This is a list to contain the rows of the Amortization Chart
	private String[] chartColumnNames = { "Payment Number", "Principal","Interest", "Remaining Balance" };

	private JPanel contentPane; // This is the contentPane of the main frame.
	private JTextField tfPrincipal; // Text field to enter Principal Loan Amount
	private JTextField tfTerm; // Text field to enter Loan Period in Months ( I have restricted it to 480 months or 40 years.)
	private JTextField tfRate; // Text field to enter Annual Rate of Interest in percentage on the loan
	private JTextField tfMonthlyPayment; // This holds the dynamically calculated Monthly Payment. Is updated as and when the user changes the above 3 textfield data
	private NumberFormat moneyFormat; // Numberformatter to parse the Principal Loan amount field data to a locale format
	private NumberFormat percentFormat; // Numberformatter to parse the Rate of Interest field data
	private DecimalFormat decimalFormat;
	private DecimalFormat paymentFormat; // Numberformatter to parse the MonthlyPayment field data

	// labels to display error message if user enter invalid data in the text fields
	private JLabel lblPrincipalErr;
	private JLabel lblTermErr;
	private JLabel lblRateErr;

	// 2 Buttons as per the requirement.
	private JButton btnGraph;
	private JButton btnTable;
	private MyFrontController controller; // Controller reference

	// Constants to show the respective textfield labels
	private static final String pamountString = "Loan Amount (>0.0): ";
	private static final String rateString = "Rate (>= 0.00%): ";
	private static final String termString = "Term (Months<=480): ";
	private static final String mpaymentString = "Monthly Payment: ";

	private JButton[] button_array = new JButton[2]; // holds the buttons
	private String[] button_name = { "Show Chart", "Show Graph" }; // holds the button names.
	private SpringLayout sl_contentPane = new SpringLayout(); // Using Spring Layout as the Layout manager for the contentPane
	private JScrollPane scrollPane;
	private JPanel graphCard;
	private JPanel chartCard;

	// Labels to display message that there is no chartData or Graph generated till now.
	private JLabel noChartMessage = new JLabel("Please click 'Show Chart' button to generate chart data.");
	private JLabel noGraphMessage = new JLabel("Please click 'Show Graph' button to generate the Graph");

	// Labels for Principal Amt , Rate and Term
	private JLabel lblRate;
	private JLabel lblTerm;
	private JLabel lblPrincipalAmount;
	private JLabel lblMonthlyPayment;
	
	// Labels to display Icons showing right and wrong images
	private JLabel lblRateStatusIcon;
	private JLabel lblPrincipalStatusIcon;
	private JLabel lblTermStatusIcon;
	private java.net.URL wrongImage = LoanCalculatorView.class.getResource("wrong.png");
	private java.net.URL rightImage = LoanCalculatorView.class.getResource("right.png");
	private java.net.URL logoImage = LoanCalculatorView.class.getResource("logo.png");
	private JLabel lblLogo;  // Simply Displays the Product Logo

	private JTabbedPane tabbedPane; // This is the tabbed pane where graph and chart can be toggled
	private JPanel tableAndGaphPanel;
	private JLabel lblUtdCopyright;  //Copyright Text

	/**
	 * Create the frame.
	 */
	public LoanCalculatorView() {
		setResizable(false);

		createAndShowGUI();

	}

	private void createAndShowGUI() {
		try {
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		/* Turn off metal's use of bold fonts */
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		setTitle("Loan Calculator");
		// This will set up the formats for the text fields in which they can
		// take input from the user
		setUpFormats();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);

		setContentPane(createContentPane());

		lblPrincipalStatusIcon = new JLabel("");
		sl_contentPane.putConstraint(SpringLayout.NORTH,lblPrincipalStatusIcon, 9, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblPrincipalErr, 21,SpringLayout.EAST, lblPrincipalStatusIcon);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblPrincipalStatusIcon,1, SpringLayout.EAST, tfPrincipal);
		contentPane.add(lblPrincipalStatusIcon);

		lblTermStatusIcon = new JLabel("");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblTermStatusIcon, 4,SpringLayout.NORTH, lblTerm);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblTermStatusIcon, 0,SpringLayout.WEST, lblPrincipalStatusIcon);
		lblTermStatusIcon.setVerticalAlignment(SwingConstants.BOTTOM);
		contentPane.add(lblTermStatusIcon);

		lblRateStatusIcon = new JLabel("");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblRateStatusIcon, 60,SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblRateStatusIcon, 0,SpringLayout.WEST, lblPrincipalStatusIcon);
		contentPane.add(lblRateStatusIcon);

		lblMonthlyPayment = new JLabel("Monthly Payment:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblMonthlyPayment, 0,SpringLayout.NORTH, tfMonthlyPayment);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblMonthlyPayment, 0,SpringLayout.EAST, lblPrincipalAmount);
		contentPane.add(lblMonthlyPayment);
		
		JLabel lblLogo = new JLabel();
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblLogo, 14, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblLogo, -31, SpringLayout.EAST, contentPane);
		lblLogo.setIcon(new ImageIcon(logoImage));
		contentPane.add(lblLogo);
		
		lblUtdCopyright = new JLabel("\u00A92013 UT Dallas, Bhojal Gelda (bxg131830)\n");
		lblUtdCopyright.setForeground(Color.BLACK);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblUtdCopyright, -1, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblUtdCopyright, -304, SpringLayout.EAST, contentPane);
		lblUtdCopyright.setFont(new Font("Arial Unicode MS", Font.BOLD, 10));
		contentPane.add(lblUtdCopyright);

	}

	private JPanel createContentPane() {
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		contentPane.setLayout(sl_contentPane);
		// get controller reference
		controller = new MyFrontController();

		// This is a private inner class which validates the Text Field
		MyVerifier inputVerifier = new MyVerifier();

		lblPrincipalAmount = new JLabel(pamountString);
		contentPane.add(lblPrincipalAmount);

		lblTerm = new JLabel(termString);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblTerm, 30,SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblTerm, -514,SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblPrincipalAmount,-6, SpringLayout.NORTH, lblTerm);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblPrincipalAmount, 0,SpringLayout.EAST, lblTerm);
		contentPane.add(lblTerm);

		lblRate = new JLabel(rateString);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblRate, 6, SpringLayout.SOUTH, lblTerm);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblRate, 0, SpringLayout.EAST, lblPrincipalAmount);
		contentPane.add(lblRate);

		tfPrincipal = new JTextField(moneyFormat.format(DEFAULT_AMOUNT));
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblPrincipalAmount, 2,SpringLayout.NORTH, tfPrincipal);
		sl_contentPane.putConstraint(SpringLayout.NORTH, tfPrincipal, 7,SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, tfPrincipal, 165,SpringLayout.WEST, contentPane);
		tfPrincipal.setInputVerifier(inputVerifier);
		tfPrincipal.addFocusListener(inputVerifier);
		contentPane.add(tfPrincipal);
		tfPrincipal.setColumns(10);

		tfTerm = new JTextField(decimalFormat.format(DEFAULT_PERIOD));
		sl_contentPane.putConstraint(SpringLayout.NORTH, tfTerm, 32,SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblTerm, -5,SpringLayout.WEST, tfTerm);
		sl_contentPane.putConstraint(SpringLayout.EAST, tfTerm, 0,SpringLayout.EAST, tfPrincipal);
		tfTerm.setInputVerifier(inputVerifier);
		tfTerm.setColumns(10);
		tfTerm.addFocusListener(inputVerifier);
		contentPane.add(tfTerm);

		tfRate = new JTextField(percentFormat.format(DEFAULT_RATE));
		sl_contentPane.putConstraint(SpringLayout.SOUTH, tfTerm, -6,SpringLayout.NORTH, tfRate);
		sl_contentPane.putConstraint(SpringLayout.NORTH, tfRate, 58,SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, tfRate, 0,SpringLayout.WEST, tfPrincipal);
		tfRate.setInputVerifier(inputVerifier);
		contentPane.add(tfRate);
		tfRate.addFocusListener(inputVerifier);
		tfRate.setColumns(10);

		btnTable = new JButton("Show Chart");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnTable, 108,SpringLayout.NORTH, contentPane);
		btnTable.setBackground(Color.WHITE);
		btnTable.setToolTipText("Click to see amortization table.");
		btnTable.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		contentPane.add(btnTable);

		btnGraph = new JButton("Show Graph");
		sl_contentPane.putConstraint(SpringLayout.EAST, btnTable, -6,SpringLayout.WEST, btnGraph);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnGraph, 108,SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnGraph, -157,SpringLayout.EAST, contentPane);
		btnGraph.setBackground(Color.WHITE);
		btnGraph.setToolTipText("Click to see balance principal variation graph");
		btnGraph.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		contentPane.add(btnGraph);
		button_array[0] = btnTable;
		button_array[1] = btnGraph;
		// Disabling button until valid data is input in the fields.
		btnGraph.setEnabled(false);
		btnTable.setEnabled(false);

		tfMonthlyPayment = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.WEST, btnTable, 105,SpringLayout.EAST, tfMonthlyPayment);
		sl_contentPane.putConstraint(SpringLayout.NORTH, tfMonthlyPayment, 6,SpringLayout.SOUTH, tfRate);
		sl_contentPane.putConstraint(SpringLayout.EAST, tfMonthlyPayment, 0,SpringLayout.EAST, tfPrincipal);
		// Monthly Payment should not be editable, as it just display the
		// result.
		tfMonthlyPayment.setEditable(false);
		tfMonthlyPayment.setFocusable(false);
		tfMonthlyPayment.setColumns(10);
		contentPane.add(tfMonthlyPayment);

		lblPrincipalErr = new JLabel("");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblPrincipalErr, 1,SpringLayout.NORTH, tfPrincipal);
		lblPrincipalErr.setVerticalAlignment(SwingConstants.TOP);
		lblPrincipalErr.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		lblPrincipalErr.setForeground(Color.RED);
		contentPane.add(lblPrincipalErr);

		lblTermErr = new JLabel("");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblTermErr, 6,SpringLayout.NORTH, lblTerm);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblTermErr, 0,SpringLayout.WEST, lblPrincipalErr);
		lblTermErr.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		lblTermErr.setForeground(Color.RED);
		contentPane.add(lblTermErr);

		lblRateErr = new JLabel("");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblRateErr, 59,SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblRateErr, 0,SpringLayout.WEST, lblPrincipalErr);
		lblRateErr.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		lblRateErr.setForeground(Color.RED);
		contentPane.add(lblRateErr);

		createTabbedPane();

		return contentPane;

	}

	// This method creates a tabbed pane which can help toggle between
	// Amortization Chart and Graph .
	// Layout Manager = Card Layout

	private void createTabbedPane() {

		tableAndGaphPanel = new JPanel();
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnGraph, -12,SpringLayout.NORTH, tableAndGaphPanel);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnTable, -12,SpringLayout.NORTH, tableAndGaphPanel);
		sl_contentPane.putConstraint(SpringLayout.NORTH, tableAndGaphPanel,144,SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, tableAndGaphPanel,-15,SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, tableAndGaphPanel, -5,SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, tableAndGaphPanel, 0,SpringLayout.WEST, contentPane);

		contentPane.add(tableAndGaphPanel);
		tabbedPane = new JTabbedPane() {
			public Dimension getPreferredSize() {
				Dimension size = super.getPreferredSize();
				size.width = 750;
				size.height = 400;
				return size;
			}
		};
		tabbedPane.setMinimumSize(new Dimension(11, 11));

		// Create the "cards".
		chartCard = new JPanel() {
			// Make the panel wider than it really needs, so
			// the window's wide enough for the tabs to stay
			// in one row.
			public Dimension getPreferredSize() {
				Dimension size = super.getPreferredSize();
				size.width = 750;
				size.height = 340;
				return size;
			}
		};
		// chartCard.add(new JButton("Button 1"));
		// chartCard.add(new JButton("Button 2"));
		// chartCard.add(new JButton("Button 3"));

		graphCard = new JPanel() {
			public Dimension getPreferredSize() {
				Dimension size = super.getPreferredSize();
				size.width = 750;
				size.height = 340;
				return size;
			}
		};
		// graphCard.add(new JTextField("TextField", 20));
		graphCard.setLayout(new FlowLayout());
		tabbedPane.addTab("Amortization Table", chartCard);
		tabbedPane.addTab("Graph", graphCard);
		tabbedPane.setFocusable(false);
		// Set Default text
		graphCard.add(noGraphMessage, BorderLayout.CENTER);
		tableAndGaphPanel.add(tabbedPane, BorderLayout.NORTH);

		scrollPane = new JScrollPane() {
			public Dimension getPreferredSize() {
				Dimension size = super.getPreferredSize();
				size.width = 700;
				size.height = 335;
				return size;
			}
		};
		designPopulateChart();
	}

	// This method is called from the showData() method which receives it's data
	// from the controller
	private void designPopulateChart() {

		JTable table;
		// Create a new table instance if user clicked on the Show Chart button
		if (dataValues != null && chartColumnNames != null) {
			noChartMessage.setText("");

			table = new JTable(dataValues, chartColumnNames);

			// Configure some of JTable's paramters
			table.setShowHorizontalLines(true);
			table.setRowSelectionAllowed(false);
			table.setColumnSelectionAllowed(false);
			table.setEnabled(false);
			table.setFillsViewportHeight(true);

			scrollPane.setViewportView(table);

			chartCard.add(scrollPane, BorderLayout.CENTER);

		} else // else the chart is yet not prepared
		{
			chartCard.add(noChartMessage, BorderLayout.CENTER);
			return;
		}
		// topPanel.add( scrollPane, BorderLayout.CENTER );
		tabbedPane.setSelectedIndex(0);
	}

	public void showData(List<MonthlyRowData> chartData) {
		// Create data for each element
		dataValues = new Object[chartData.size()][chartColumnNames.length];

		for (int iY = 0; iY < chartData.size(); iY++) {
			dataValues[iY][0] = chartData.get(iY).getPaymentNumber();
			dataValues[iY][1] = NumberFormat.getCurrencyInstance(setLocale).format(chartData.get(iY).getPaymentAmt());
			dataValues[iY][2] = NumberFormat.getCurrencyInstance(setLocale).format(chartData.get(iY).getInterestAmt());
			dataValues[iY][3] = NumberFormat.getCurrencyInstance(setLocale).format(chartData.get(iY).getRemainingBalance());
		}
		designPopulateChart();
	}

	// Called from showGraph method
	private void designPopulateGraph(JPanel graphPanel) {
		graphPanel.setPreferredSize(new Dimension(720, 340));
		graphCard.removeAll();
		JLabel graphLabel = new JLabel(" X-Axis : Loan Period vs Y-Axis: Remaining Balance in USD (Y-axis)");
		graphLabel.setForeground(new Color(255, 97, 3));
		graphCard.add(graphLabel, BorderLayout.NORTH);
		graphCard.add(graphPanel, BorderLayout.CENTER);
		graphCard.validate();
		graphCard.repaint();
		tabbedPane.setSelectedIndex(1);
	}

	// Recieves the panel with the graph from the controller.
	public void showGraph(JPanel graphPanel) {
		designPopulateGraph(graphPanel);
	}

	// Here we add the ActionListener passed by the Controller to each of the
	// buttons
	public void buttonActionListeners(ActionListener al) {
		for (int i = 0; i < button_name.length; i++) {
			button_array[i].setActionCommand(button_name[i]);
			button_array[i].addActionListener(al);
		}
	}

	private class MyVerifier extends InputVerifier implements FocusListener {
		boolean wasPrincipalValid = false;
		boolean wasRateValid = false;
		boolean wasTermValid = false;

		/*
		 * Calls verify(input) to ensure that the input is valid. In particular,
		 * this method is called when the user attempts to advance focus out of
		 * the argument component into another Swing component in this window.
		 * If this method returns true, then the focus is transfered normally;
		 * if it returns false, then the focus remains in the argument
		 * component.
		 */
		public boolean shouldYieldFocus(JComponent input) {

			boolean inputOK = verify(input);

			/*
			 * If the inputOK is true and all the fields contain valid data.
			 * Update Monthly Paymnent. SingletonFieldDataHolder is a singleton
			 * object used for TextFields data sharing , to avoid access to view
			 * components from outside this view class.
			 */

			if (inputOK && wasPrincipalValid && wasRateValid && wasTermValid) {
				updatePayment();
				// Now the singleton object is needed to be instantiated and
				// loaded (lazy intializing).
				try {
					SingletonFieldDataHolder fieldData = SingletonFieldDataHolder.getInstance();
					fieldData.setTfPrincipal(moneyFormat.parse(tfPrincipal.getText()).doubleValue());
					fieldData.setTfRate(percentFormat.parse(tfRate.getText()).doubleValue());
					fieldData.setTfTerm(decimalFormat.parse(tfTerm.getText()).intValue());
					fieldData.setMonthlyPayAmt(paymentFormat.parse(tfMonthlyPayment.getText()).doubleValue());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				btnGraph.setEnabled(true);
				btnTable.setEnabled(true);

			} else {
				tfMonthlyPayment.setText("");
				btnGraph.setEnabled(false);
				btnTable.setEnabled(false);
			}

			if (inputOK) {
				return true;
			} else {
				Toolkit.getDefaultToolkit().beep();
				return false;
			}
		}

		// Calls cnotroller to compute the monthly payment and displays

		protected void updatePayment() {
			double amount = DEFAULT_AMOUNT;
			double rate = DEFAULT_RATE;
			int numPeriods = DEFAULT_PERIOD;
			double payment = 0.0;

			// Parse the values.
			try {
				amount = moneyFormat.parse(tfPrincipal.getText()).doubleValue();
			} catch (ParseException pe) {
				pe.printStackTrace();
			}
			try {
				rate = percentFormat.parse(tfRate.getText()).doubleValue();
			} catch (ParseException pe) {
				pe.printStackTrace();
			}
			try {
				numPeriods = decimalFormat.parse(tfTerm.getText()).intValue();
			} catch (ParseException pe) {
				pe.printStackTrace();
			}
			// System.out.println("Let me calculate Payment Now");
			// Calculate the result and update the GUI.
			payment = controller.computePayment(amount, rate, numPeriods);

			tfMonthlyPayment.setText(paymentFormat.format(payment));
		}

		// This method checks input, but should cause no side effects.
		public boolean verify(JComponent input) {
			return checkField(input);
		}

		protected boolean checkField(JComponent input) {
			if (input == tfPrincipal) {
				return checktfPrincipal();
			} else if (input == tfRate) {
				return checktfRate();
			} else if (input == tfTerm) {
				return checktfTerm();
			} else {
				return true; // shouldn't happen
			}
		}

		// Checks that the amount field is valid. If it is valid,
		// it returns true; otherwise, returns false. This method reigns in the
		// value if necessary and (even if not) sets it to the
		// parsed number so that it looks good -- no letters,
		// for example.

		protected boolean checktfPrincipal() {

			double amount = DEFAULT_AMOUNT;
			wasPrincipalValid = false;
			// System.out.println("Inside chcktfPrincipal"+tfPrincipal.getText());
			// Parse the value.
			try {
				amount = Double.parseDouble(tfPrincipal.getText());
				tfPrincipal.setText(moneyFormat.format(amount));
				lblPrincipalErr.setText("");
				lblPrincipalStatusIcon.setIcon(new ImageIcon(rightImage));
				wasPrincipalValid = true;
			} catch (NumberFormatException pe) {
				if(tfPrincipal.getText().equals(""))
					lblPrincipalErr.setText("This field is mandatory.");
				else
					lblPrincipalErr.setText("'" + tfPrincipal.getText() + "'"+ "is not a valid input.");
				lblPrincipalStatusIcon.setIcon(new ImageIcon(wrongImage));
			}

			// Value was invalid.
			if (amount < 1 && wasPrincipalValid) {
				lblPrincipalErr.setText("Loan amount should be greater than 1");
				lblPrincipalStatusIcon.setIcon(new ImageIcon(wrongImage));
				wasPrincipalValid = false;
			}

			return true;
		}

		// Checks that the rate field is valid. If it is valid,
		// it returns true; otherwise, returns false. If the
		// change argument is true, this method reigns in the
		// value if necessary and (even if not) sets it to the
		// parsed number so that it looks good -- no letters,
		// for example.
		protected boolean checktfRate() {

			double rate = DEFAULT_RATE;
			wasRateValid = false;
			// Parse the value.
			try {
				// rate = percentFormat.parse(tfRate.getText()).doubleValue();
				rate = Double.parseDouble(tfRate.getText());
				tfRate.setText(percentFormat.format(rate));
				lblRateErr.setText("");
				wasRateValid = true;
				lblRateStatusIcon.setIcon(new ImageIcon(rightImage));
			} catch (NumberFormatException pe) {
				if(tfRate.getText().equals(""))
					lblRateErr.setText("This field is mandatory.");
				else 
					lblRateErr.setText("'" + tfRate.getText() + "'"+ "is not a valid input of the form 0.00");
				lblRateStatusIcon.setIcon(new ImageIcon(wrongImage));
			}

			// Value was invalid.
			if (rate <=0 && wasRateValid) {
				lblRateErr.setText(" Interest Rate should be greater than 0.");
				lblRateStatusIcon.setIcon(new ImageIcon(wrongImage));
				wasRateValid = false;
			}

			return true;

		}

		// Checks that the numPeriods field is valid. If it is valid,
		// it returns true; otherwise, returns false. If the
		// change argument is true, this method reigns in the
		// value if necessary and (even if not) sets it to the
		// parsed number so that it looks good -- no letters,
		// for example.
		protected boolean checktfTerm() {

			int term = DEFAULT_PERIOD;
			wasTermValid = false;
			// Parse the value.
			try {
				// term = decimalFormat.parse(tfTerm.getText()).intValue();
				term = Integer.parseInt(tfTerm.getText());
				tfTerm.setText(decimalFormat.format(term));
				lblTermErr.setText("");
				lblTermStatusIcon.setIcon(new ImageIcon(rightImage));
				wasTermValid = true;
				lblTermErr.setText("");
			} catch (NumberFormatException pe) {
				lblTermErr.setText("'" + tfTerm.getText() + "'"+ "is not a valid input.");
				lblTermStatusIcon.setIcon(new ImageIcon(wrongImage));
			}

			// Value was invalid.
			if ( term <1 || term >480  && wasTermValid) {
				if(tfTerm.getText().equals(""))
					lblTermErr.setText("This field is mandatory.");
				else 
				lblTermErr.setText(" Loan period should be within(including) 1 to 480 months.");
				lblTermStatusIcon.setIcon(new ImageIcon(wrongImage));
				wasTermValid=false;
			}

			return true;
		}

		// On focus gain, all the text is selected inside the component the
		// focus is on.
		// The principal amount is put in the format easily editable by the user
		@Override
		public void focusGained(FocusEvent arg0) {
			JTextField source = (JTextField) arg0.getSource();
			source.selectAll();
			if (source == tfPrincipal && wasPrincipalValid) {
				try {
					String s = String.valueOf((moneyFormat.parse(tfPrincipal
							.getText()).doubleValue()));
					tfPrincipal.setText(s);
				} catch (ParseException pe) {
				}

			}
		}

		@Override
		public void focusLost(FocusEvent arg0) {

		}

	}

	private void setUpFormats() {
		moneyFormat = (NumberFormat) NumberFormat.getNumberInstance();

		percentFormat = NumberFormat.getNumberInstance();
		percentFormat.setMaximumFractionDigits(2);

		decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance();
		decimalFormat.setParseIntegerOnly(true);

		paymentFormat = (DecimalFormat) NumberFormat.getNumberInstance();
		paymentFormat.setMaximumFractionDigits(2);

	}
}
