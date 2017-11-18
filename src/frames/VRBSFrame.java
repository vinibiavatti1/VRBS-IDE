package frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import jsyntaxpane.syntaxkits.JavaScriptSyntaxKit;
import jsyntaxpane.syntaxkits.JavaSyntaxKit;

import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Dimension;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.JSplitPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import compiler.VRBSCompiler;
import compiler.VRBSException;

import javax.swing.ImageIcon;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;

public class VRBSFrame extends JFrame {

	private JPanel contentPane;
	private JEditorPane codeArea;
	private JEditorPane consoleArea;
	private JTable codeTable;
	private VRBSCompiler compiler = null;
	private JTable varTable;
	private JTable listTable;
	private JScrollPane varTableScrl;
	private JScrollPane listTableScrl;

	private File savedFile = null;
	private JButton btnSave;
	private JTable objTable;
	private JButton btnPlay;
	private JButton btnContinuar;

	/**
	 * Create the frame.
	 */
	public VRBSFrame() {
		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(VRBSFrame.class.getResource("/resources/icons/application_view_tile.png")));
		setTitle("VRBSIDE 1.0.0");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1098, 800);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("Arquivo");
		menuBar.add(mnFile);

		JMenuItem mntmTest = new JMenuItem("Novo");
		mntmTest.setIcon(new ImageIcon(VRBSFrame.class.getResource("/resources/icons/application_add.png")));
		mntmTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newFile();
			}
		});
		mnFile.add(mntmTest);

		JMenuItem mntmOpen = new JMenuItem("Abrir...");
		mntmOpen.setIcon(new ImageIcon(VRBSFrame.class.getResource("/resources/icons/folder_page.png")));
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				open();
			}
		});
		mnFile.add(mntmOpen);

		JMenuItem mntmSalvar = new JMenuItem("Salvar...");
		mntmSalvar.setIcon(new ImageIcon(VRBSFrame.class.getResource("/resources/icons/disk.png")));
		mntmSalvar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		mnFile.add(mntmSalvar);

		JMenuItem mntmSalvarComo = new JMenuItem("Salvar como...");
		mntmSalvarComo.setIcon(new ImageIcon(VRBSFrame.class.getResource("/resources/icons/disk.png")));
		mntmSalvarComo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("VRBS File", "VRBS"));
				int option = chooser.showSaveDialog(null);
				if (option == JFileChooser.APPROVE_OPTION) {
					File f = chooser.getSelectedFile();
					String name = f.getName();
					if (!name.endsWith(".vrbs")) {
						f = new File(f.getAbsolutePath() + ".vrbs");
					}
					if (!f.exists()) {
						try {
							f.createNewFile();
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(null, e1, "Erro", JOptionPane.ERROR_MESSAGE);
							e1.printStackTrace();
						}
					}
					try {
						PrintWriter pw = new PrintWriter(new FileOutputStream(f));
						pw.write(codeArea.getText());
						pw.flush();
						pw.close();
						savedFile = f;
						btnSave.setEnabled(false);
					} catch (FileNotFoundException e1) {
						JOptionPane.showMessageDialog(null, e1, "Erro", JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}
				}
			}
		});
		mnFile.add(mntmSalvarComo);

		JSeparator separator = new JSeparator();
		mnFile.add(separator);

		JMenuItem mntmExit = new JMenuItem("Sair");
		mntmExit.setIcon(new ImageIcon(VRBSFrame.class.getResource("/resources/icons/door_in.png")));
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int conf = JOptionPane.showConfirmDialog(null, "Deseja mesmo sair?", "Confirmação",
						JOptionPane.YES_NO_OPTION);
				if (conf == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		});
		mnFile.add(mntmExit);

		JMenu mnProgram = new JMenu("Programa");
		menuBar.add(mnProgram);

		JMenuItem mntmPlay = new JMenuItem("Executar");
		mntmPlay.setIcon(new ImageIcon(VRBSFrame.class.getResource("/resources/icons/resultset_next.png")));
		mntmPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runProgram();
			}
		});
		mnProgram.add(mntmPlay);

		JMenuItem mntmStop = new JMenuItem("Parar");
		mntmStop.setIcon(new ImageIcon(VRBSFrame.class.getResource("/resources/icons/cancel.png")));
		mntmStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopProgram();
			}
		});
		mnProgram.add(mntmStop);

		JMenu mnSource = new JMenu("C\u00F3digo");
		menuBar.add(mnSource);

		JMenuItem mntmAddImport = new JMenuItem("Add Importa\u00E7\u00E3o...");
		mntmAddImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("VRBS File", "VRBS"));
				int option = chooser.showOpenDialog(null);
				if (option == JFileChooser.APPROVE_OPTION) {
					File f = chooser.getSelectedFile();
					String text = codeArea.getText();
					StringBuffer sb = new StringBuffer(text);
					text = sb.insert(codeArea.getCaretPosition(), "import(\"" + f.getAbsolutePath() + "\")").toString();
					codeArea.setText(text);
				}
			}
		});
		mntmAddImport.setIcon(new ImageIcon(VRBSFrame.class.getResource("/resources/icons/package_add.png")));
		mnSource.add(mntmAddImport);

		JMenu mnAbout = new JMenu("Ajuda");
		menuBar.add(mnAbout);

		JMenuItem mntmHelp = new JMenuItem("Documenta\u00E7\u00E3o");
		mntmHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openDoc();
			}
		});
		mntmHelp.setIcon(new ImageIcon(VRBSFrame.class.getResource("/resources/icons/book_addresses.png")));
		mnAbout.add(mntmHelp);

		JMenuItem mntmAbout = new JMenuItem("Sobre");
		mntmAbout.setIcon(new ImageIcon(VRBSFrame.class.getResource("/resources/icons/rosette.png")));
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String about = //
						"VRBSIDE versão 1.1.1\n" + //
				"Desenvolvido por: Vinícius Reif Biavatti\n\n" + //
				"Tecnologia utilizada: Java 8\n" + //
				"Tema Swing: Weblaf 1.29\n" + //
				"VRBS 1.1.1\n" + //
				"Highlighter: JSyntaxPane 1.1.5\n" + //
				"Ícones: Silk Icons (famfamfam.com)\n\n" + //
				"\u00A9 Copyright";

				JOptionPane.showMessageDialog(null, about, "Sobre", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		mnAbout.add(mntmAbout);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		contentPane.add(toolBar, BorderLayout.NORTH);

		btnPlay = new JButton("Executar");
		btnPlay.setIcon(new ImageIcon(VRBSFrame.class.getResource("/resources/icons/resultset_next.png")));
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runProgram();
			}

		});

		JButton btnNew = new JButton("Novo");
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newFile();
			}
		});
		btnNew.setIcon(new ImageIcon(VRBSFrame.class.getResource("/resources/icons/application_add.png")));
		toolBar.add(btnNew);

		JButton btnLoad = new JButton("Abrir");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				open();
			}
		});
		btnLoad.setIcon(new ImageIcon(VRBSFrame.class.getResource("/resources/icons/folder_page.png")));
		toolBar.add(btnLoad);

		btnSave = new JButton("Salvar");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}

		});
		btnSave.setIcon(new ImageIcon(VRBSFrame.class.getResource("/resources/icons/disk.png")));
		toolBar.add(btnSave);
		toolBar.add(btnPlay);

		JButton btnStop = new JButton("Parar");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopProgram();
			}
		});
		
		btnContinuar = new JButton("Continuar");
		btnContinuar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compiler.setBreakCursor(false);
				btnContinuar.setEnabled(false);
			}
		});
		btnContinuar.setEnabled(false);
		btnContinuar.setIcon(new ImageIcon(VRBSFrame.class.getResource("/resources/icons/resultset_next.png")));
		toolBar.add(btnContinuar);
		btnStop.setIcon(new ImageIcon(VRBSFrame.class.getResource("/resources/icons/cancel.png")));
		toolBar.add(btnStop);

		JButton btnDocumentao = new JButton("Documenta\u00E7\u00E3o");
		btnDocumentao.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openDoc();
			}
		});
		btnDocumentao.setIcon(new ImageIcon(VRBSFrame.class.getResource("/resources/icons/book_addresses.png")));
		toolBar.add(btnDocumentao);

		JSplitPane splitPane = new JSplitPane();
		contentPane.add(splitPane, BorderLayout.CENTER);

		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setRightComponent(splitPane_1);

		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane_1.setLeftComponent(scrollPane_1);

		codeArea = new JEditorPane();
		codeArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				btnSave.setEnabled(true);
			}
		});
		codeArea.addInputMethodListener(new InputMethodListener() {
			public void caretPositionChanged(InputMethodEvent event) {
			}

			public void inputMethodTextChanged(InputMethodEvent event) {
				btnSave.setEnabled(true);
			}
		});
		codeArea.setText(
				"/*\r\n * Bem vindo ao VRBS!\r\n * \r\n * Clique em \"Executar\" para executar o programa Hello World!!\r\n * Clique em \"Novo\" para come\u00E7ar a programar!\r\n * Clique em \"Documenta\u00E7\u00E3o\" para obter informa\u00E7\u00F5es da linguagem\r\n */\r\nprint(\"Hello World !!\")\r\nstop()");
		codeArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
		scrollPane_1.setViewportView(codeArea);

		JScrollPane scrollPane_2 = new JScrollPane();
		splitPane_1.setRightComponent(scrollPane_2);

		consoleArea = new JEditorPane();
		consoleArea.setEditable(false);
		consoleArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
		scrollPane_2.setViewportView(consoleArea);
		splitPane_1.setDividerLocation(550);
		splitPane.setDividerLocation(200);

		codeArea.setEditorKit(new JavaScriptSyntaxKit());
		codeArea.setText("/*\r\n" + " * Bem vindo ao VRBS\r\n" + " * \r\n"
				+ " * Clique em \"Executar\" para executar o programa Hello World!!\r\n"
				+ " * Clique em \"Novo\" para começar a programar!\r\n"
				+ " * Clique em \"Documentação\" para obter informações da linguagem\r\n" + " */\r\n"
				+ "printLn(\"Hello World !!\")\r\n" + "stop()");

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setLeftComponent(tabbedPane);

		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("C\u00F3digo", null, scrollPane, null);

		codeTable = new JTable();
		scrollPane.setViewportView(codeTable);
		codeTable.setModel(
				new DefaultTableModel(
			new Object[][] {
				{null, null},
			},
			new String[] {
				"#", "Fun\u00E7\u00E3o"
			}
		) {
			boolean[] columnEditables = new boolean[] {
				false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		codeTable.getColumnModel().getColumn(0).setPreferredWidth(30);
		codeTable.getColumnModel().getColumn(1).setPreferredWidth(120);

		varTableScrl = new JScrollPane();
		tabbedPane.addTab("Vari\u00E1veis", null, varTableScrl, null);

		varTable = new JTable();
		varTable.setModel(new DefaultTableModel(new Object[][] { { null, null }, }, new String[] { "Nome", "Valor" }) {
			boolean[] columnEditables = new boolean[] { false, false };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		varTableScrl.setViewportView(varTable);

		listTableScrl = new JScrollPane();
		tabbedPane.addTab("Listas", null, listTableScrl, null);

		listTable = new JTable();
		listTable.setModel(new DefaultTableModel(new Object[][] { { null, null }, }, new String[] { "Nome", "Valor" }) {
			boolean[] columnEditables = new boolean[] { false, false };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		listTableScrl.setViewportView(listTable);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		tabbedPane.addTab("Objetos", null, scrollPane_3, null);
		
		objTable = new JTable();
		objTable.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null},
			},
			new String[] {
				"Nome", "Atributos"
			}
		));
		scrollPane_3.setViewportView(objTable);
		compiler = new VRBSCompiler(codeArea, consoleArea);
		compiler.setVarTable(varTable);
		compiler.setCodeTable(codeTable);
		compiler.setObjTable(objTable);
		compiler.setListTable(listTable);
	}

	/**
	 * Abrir Doc
	 */
	public void openDoc() {
		try {
			Desktop.getDesktop().browse(new URI("https://github.com/vinibiavatti1/VRBS"));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Não foi possível abrir a documentação", "Erro", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (URISyntaxException e) {
			JOptionPane.showMessageDialog(null, "Não foi possível abrir a documentação", "Erro", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	/**
	 * Parar programa
	 */
	private void stopProgram() {
		if (compiler == null) {
			return;
		}
		compiler.setStop(true);
		compiler.setBreakCursor(false);
		btnContinuar.setEnabled(false);
	}

	/**
	 * Rodar
	 */
	private void runProgram() {
		consoleArea.setText("");
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					consoleArea.setForeground(Color.BLACK);
					getBtnPlay().setEnabled(false);
					compiler.execute();
				} catch (VRBSException e1) {
					if (consoleArea.getText().equals("")) {
						consoleArea.setText(consoleArea.getText() + e1.getMessage() + "\n");
					} else {
						consoleArea.setText(consoleArea.getText() + "\n" + e1.getMessage() + "\n");
					}
					consoleArea.setForeground(Color.RED);
					System.err.println(e1);
				} finally {
					getBtnPlay().setEnabled(true);
				}
			}
		}).start();
	}

	/**
	 * Novo
	 */
	private void newFile() {
		int conf = JOptionPane.showConfirmDialog(null, "Deseja mesmo iniciar um novo programa?", "Confirmação",
				JOptionPane.YES_NO_OPTION);
		if (conf == JOptionPane.YES_OPTION) {
			consoleArea.setText("");
			codeArea.setText("");
			btnSave.setEnabled(true);
			savedFile = null;
		}
	}

	/**
	 * Carregar arquivo
	 */
	private void open() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("VRBS File", "VRBS"));
		int option = chooser.showOpenDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			try {
				Scanner s = new Scanner(new FileInputStream(f));
				String content = "";
				while (s.hasNext()) {
					content += s.nextLine() + "\n";
				}
				codeArea.setText(content);
				btnSave.setEnabled(true);
				consoleArea.setText("");
				savedFile = f;
			} catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(null, e1, "Erro", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Salvar Arquivo
	 */
	private void save() {
		if (savedFile != null) {
			PrintWriter pw;
			try {
				pw = new PrintWriter(new FileOutputStream(savedFile));
				pw.write(codeArea.getText());
				pw.flush();
				pw.close();
			} catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(null, e1, "Erro", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
			btnSave.setEnabled(false);
			return;
		}
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("VRBS File", "VRBS"));
		int option = chooser.showSaveDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			String name = f.getName();
			if (!name.endsWith(".vrbs")) {
				f = new File(f.getAbsolutePath() + ".vrbs");
			}
			if (!f.exists()) {
				try {
					f.createNewFile();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, e1, "Erro", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
			try {
				PrintWriter pw = new PrintWriter(new FileOutputStream(f));
				pw.write(codeArea.getText());
				pw.flush();
				pw.close();
				savedFile = f;
				btnSave.setEnabled(false);
			} catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(null, e1, "Erro", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
	}

	/**
	 * @return the codeArea
	 */
	public JEditorPane getCodeArea() {
		return codeArea;
	}

	/**
	 * @param codeArea
	 *            the codeArea to set
	 */
	public void setCodeArea(JEditorPane codeArea) {
		this.codeArea = codeArea;
	}

	/**
	 * @return the consoleArea
	 */
	public JEditorPane getConsoleArea() {
		return consoleArea;
	}

	/**
	 * @param consoleArea
	 *            the consoleArea to set
	 */
	public void setConsoleArea(JEditorPane consoleArea) {
		this.consoleArea = consoleArea;
	}

	/**
	 * @return the codeTable
	 */
	public JTable getCodeTable() {
		return codeTable;
	}

	/**
	 * @param codeTable
	 *            the codeTable to set
	 */
	public void setCodeTable(JTable codeTable) {
		this.codeTable = codeTable;
	}

	/**
	 * @return the varTable
	 */
	public JTable getVarTable() {
		return varTable;
	}

	/**
	 * @param varTable
	 *            the varTable to set
	 */
	public void setVarTable(JTable varTable) {
		this.varTable = varTable;
	}

	/**
	 * @return the listTable
	 */
	public JTable getListTable() {
		return listTable;
	}

	/**
	 * @param listTable
	 *            the listTable to set
	 */
	public void setListTable(JTable listTable) {
		this.listTable = listTable;
	}

	/**
	 * @return the objTable
	 */
	public JTable getObjTable() {
		return objTable;
	}

	/**
	 * @param objTable the objTable to set
	 */
	public void setObjTable(JTable objTable) {
		this.objTable = objTable;
	}

	/**
	 * @return the btnPlay
	 */
	public JButton getBtnPlay() {
		return btnPlay;
	}

	/**
	 * @param btnPlay the btnPlay to set
	 */
	public void setBtnPlay(JButton btnPlay) {
		this.btnPlay = btnPlay;
	}

	/**
	 * @return the btnContinuar
	 */
	public JButton getBtnContinuar() {
		return btnContinuar;
	}

	/**
	 * @param btnContinuar the btnContinuar to set
	 */
	public void setBtnContinuar(JButton btnContinuar) {
		this.btnContinuar = btnContinuar;
	}

	
}
