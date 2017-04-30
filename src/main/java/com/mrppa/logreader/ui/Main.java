package com.mrppa.logreader.ui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;

import com.mrppa.logreader.reader.Line;
import com.mrppa.logreader.reader.LineReader;
import com.mrppa.logreader.reader.Progress;

import net.miginfocom.swing.MigLayout;

public class Main {

	private JFrame frame;

	private final JFileChooser fileChooser = new JFileChooser();
	private LineReader lineReader;
	private JTable table;
	private DefaultTableModel model;
	private static final int NU_OF_REC = 100;
	private JTextField searchField;
	private int fontSize = 20;
	private JButton btnA, btnA_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 958, 482);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("", "[grow]", "[][410.00,grow][][grow]"));

		model = new DefaultTableModel();
		table = new JTable(model);
		model.addColumn(new String[] { "COL1" });
		model.addColumn(new String[] { "COL2" });
		model.addColumn(new String[] { "COL3" });
		table.removeColumn(table.getColumnModel().getColumn(1));
		table.removeColumn(table.getColumnModel().getColumn(1));
		table.setShowGrid(false);
		table.setFont(new Font("Serif", Font.PLAIN, this.fontSize));

		table.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				int notches = arg0.getWheelRotation();
				if (notches < 0) {
					System.out.println("Mouse wheel moved UP " + -notches + " notch(es)");
					Long lastRowEndVal = (Long) model.getValueAt(model.getRowCount() - 1, 2);
					if (model.getRowCount() > 1) {
						model.removeRow(0);
					}
					try {
						Line line = lineReader.getNextPosition(lastRowEndVal + 1);
						if (line != null) {
							model.addRow(new Object[] { line.getContent(), line.getStartPos(), line.getEndPos() });
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(lastRowEndVal);
				} else {
					System.out.println("Mouse wheel moved DOWN " + -notches + " notch(es)");
					Long firstRowStartVal = (Long) model.getValueAt(0, 1);
					if (firstRowStartVal > 0) {
						if (model.getRowCount() >= NU_OF_REC) {
							model.removeRow(model.getRowCount() - 1);
						}
						try {
							Line line = lineReader.getPrevPosition(firstRowStartVal - 1);
							if (line != null) {
								model.insertRow(0,
										new Object[] { line.getContent(), line.getStartPos(), line.getEndPos() });
							}

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});

		JToolBar toolBar_nav = new JToolBar();
		frame.getContentPane().add(toolBar_nav, "flowx,cell 0 0");

		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					loadTableFromTop(-1);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		toolBar_nav.add(btnStart);

		JButton btnGoToEnd = new JButton("End");
		toolBar_nav.add(btnGoToEnd);
		btnGoToEnd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Line line = lineReader.getNearestPrevLine(lineReader.getNuOfBytes() - 1);
					loadTableFromBottom(line.getEndPos() + 1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		JSeparator separator = new JSeparator();
		toolBar_nav.add(separator);

		frame.getContentPane().add(table, "cell 0 1 1 3,grow");

		JToolBar toolBar_Search = new JToolBar();
		frame.getContentPane().add(toolBar_Search, "cell 0 0");

		searchField = new JTextField();
		toolBar_Search.add(searchField);
		searchField.setColumns(10);

		JButton btnSrchTop = new JButton("<");
		toolBar_Search.add(btnSrchTop);

		JButton btnSrchDown = new JButton(">");
		toolBar_Search.add(btnSrchDown);

		JToolBar toolBar_font = new JToolBar();
		frame.getContentPane().add(toolBar_font, "cell 0 0");

		btnA = new JButton("A+");
		btnA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fontSize++;
				table.setFont(new Font("Serif", Font.PLAIN, fontSize));
				btnA_1.setEnabled(true);
				if (fontSize > 30) {
					btnA.setEnabled(false);
				}
			}
		});
		toolBar_font.add(btnA);

		btnA_1 = new JButton("A-");
		btnA_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fontSize--;
				table.setFont(new Font("Serif", Font.PLAIN, fontSize));
				btnA.setEnabled(true);
				if (fontSize < 5) {
					btnA_1.setEnabled(false);
				}
			}
		});
		toolBar_font.add(btnA);

		toolBar_font.add(btnA_1);
		btnSrchDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Long firstRowStartVal = (Long) model.getValueAt(0, 1);
					long searchRes = lineReader.getCachedLogReader().searchNextItem(searchField.getText(),
							firstRowStartVal, new Progress());
					Line line = lineReader.getNearestPrevLine(searchRes);
					loadTableFromTop(line.getStartPos() - 1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnSrchTop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = fileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try {

						lineReader = new LineReader(file.getAbsolutePath(), 1024);
						loadTableFromTop(-1);
					} catch (IOException ex) {
						System.out.println("problem accessing file" + file.getAbsolutePath());
					}
				} else {
					System.out.println("File access cancelled by user.");
				}
			}
		});
		mnFile.add(mntmOpen);
	}

	private void loadTableFromTop(long startingPos) throws IOException {
		model.setRowCount(0);
		Line line = new Line();
		line.setEndPos(startingPos);
		for (int i = 0; i < NU_OF_REC; i++) {
			line = lineReader.getNextPosition(line.getEndPos() + 1);
			model.addRow(new Object[] { line.getContent(), line.getStartPos(), line.getEndPos() });
		}
	}

	private void loadTableFromBottom(long endPos) throws IOException {
		model.setRowCount(0);
		Line line = new Line();
		line.setStartPos(endPos);
		for (int i = 0; i < NU_OF_REC; i++) {
			line = lineReader.getPrevPosition(line.getStartPos() - 1);
			model.insertRow(0, new Object[] { line.getContent(), line.getStartPos(), line.getEndPos() });
		}
	}

}
