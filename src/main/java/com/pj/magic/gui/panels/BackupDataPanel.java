package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.ApplicationProperties;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.dialog.WaitDialog;
import com.pj.magic.service.LoginService;

@Component
public class BackupDataPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(BackupDataPanel.class);
	
	private static final String BACKUP_TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";
	
	@Autowired private WaitDialog waitDialog;
	@Autowired private LoginService loginService;
	
	private String backupFilename = null;
	private JButton backupButton;
	private JFileChooser restoreFileChooser;
	private JButton restoreButton;
	
	@Override
	protected void initializeComponents() {
		backupButton = new JButton("Backup Data");
		backupButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				backupData();
			}
		});
		
		restoreFileChooser = new JFileChooser();
		restoreFileChooser.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return "Backup files (*.7z)";
			}
			
			@Override
			public boolean accept(File f) {
				return FilenameUtils.getExtension(f.getName()).equals("7z");
			}
		});
		
		restoreButton = new JButton("Restore Data");
		restoreButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				restoreData();
			}
		});
	}

	private void restoreData() {
		if (!isMysqlInstalled()) {
			showErrorMessage("Restore cannot be done through this terminal");
			return;
		}
		
		restoreFileChooser.setCurrentDirectory(new File(getBackupFolderAbsolutePath()));
		int returnVal = restoreFileChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			final File file = restoreFileChooser.getSelectedFile();
			SwingWorker<Integer, Integer> worker = new SwingWorker<Integer, Integer>() {

				public Integer doInBackground() throws Exception {
					Process process = Runtime.getRuntime().exec(
							new String[] { "cmd.exe", "/c", constructDecompressBackupFileCommand(file)});
					int result = process.waitFor();
					if (result != 0) {
						return result;
					}
					
					System.out.println(constructRestoreCommand(file));
					process = Runtime.getRuntime().exec(
							new String[] { "cmd.exe", "/c", constructRestoreCommand(file) });
					result = process.waitFor();
					System.out.println(result);
					System.out.println(IOUtils.toString(process.getErrorStream()));
					return result;
				}

				public void done() {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							waitDialog.setVisible(false);
							waitDialog.dispose();
						}
					});
				}

			};
			worker.execute();
			waitDialog.setVisible(true);
			
			try {
				if (worker.get() == 0) {
					showMessage("Restore completed");
				} else {
					showMessage("Restore failed");
				}
			} catch (InterruptedException | ExecutionException e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
			}
		}
	}

	private String constructRestoreCommand(File file) {
		String command = "mysql -u{0} -p{1} {2} < \"{3}\"";
		backupFilename = constructBackupFilename();
		return MessageFormat.format(command, 
				ApplicationProperties.getProperty("db.user"),
				ApplicationProperties.getProperty("db.password"),
				ApplicationProperties.getProperty("db.name"),
				file.getAbsolutePath().replaceAll(".7z", "")
		);
	}

	private String constructDecompressBackupFileCommand(File file) {
		String command = "7z e \"{0}\" -o\"{1}\"";
		return MessageFormat.format(command, 
				file.getAbsolutePath(),
				file.getParentFile().getAbsolutePath()
		);
	}
	
	private void backupData() {
		if (!isMysqlInstalled()) {
			showErrorMessage("Backup cannot be done through this terminal");
			return;
		}
		
		try {
			Process process = Runtime.getRuntime().exec(
					new String[] {"cmd.exe", "/c", constructBackupCommand()});
			process.waitFor();
			if (Files.exists(Paths.get(backupFilename + ".7z"))) {
				showMessage("Backup completed.\n" + backupFilename + ".7z");
			} else {
				showMessage("Backup failed");
			}
		} catch (IOException | InterruptedException e) {
			logger.error(e.getMessage(), e);
			showMessageForUnexpectedError();
		}
	}

	private String constructBackupCommand() {
		String command = "mysqldump -u{0} -p{1} --databases {2} | 7z.exe a \"{3}.7z\" -si \"{4}\"";
		backupFilename = constructBackupFilename();
		return MessageFormat.format(command, 
				ApplicationProperties.getProperty("db.user"),
				ApplicationProperties.getProperty("db.password"),
				ApplicationProperties.getProperty("db.name"),
				backupFilename,
				backupFilename
		);
	}

	private String constructBackupFilename() {
		StringBuilder sb = new StringBuilder(getBackupFolderAbsolutePath());
		sb.append("\\").append(ApplicationProperties.getProperty("db.backup.name"));
		sb.append("_").append(new SimpleDateFormat(BACKUP_TIMESTAMP_FORMAT).format(new Date()));
		sb.append(".sql");
		return sb.toString();
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.insets = new Insets(10, 10, 10, 10);
		mainPanel.add(backupButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.insets = new Insets(10, 10, 10, 10);
		mainPanel.add(restoreButton, c);
	}

	@Override
	protected void registerKeyBindings() {
		// none
	}

	public void updateDisplay() {
		restoreButton.setVisible(loginService.getLoggedInUser().isSupervisor());
	}
	
	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}
	
	private String getBackupFolderAbsolutePath() {
		Path directory = Paths.get(System.getProperty("user.home"),
				"Desktop",
				ApplicationProperties.getProperty("db.backup.folder.name"));
		if (Files.notExists(directory)) {
			try {
				Files.createDirectory(directory);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		directory = directory.toAbsolutePath();
		return directory.toString();
	}
	
	private boolean isMysqlInstalled() {
		try {
			Process process = Runtime.getRuntime().exec(
					new String[] {"cmd.exe", "/c", constructMysqlCheckCommand()});
			return process.waitFor() == 0;
		} catch (IOException | InterruptedException e) {
			return false;
		}
	}
	
	private static String constructMysqlCheckCommand() {
		String command = "mysql -u{0} -p{1} -e \"select now()\"";
		return MessageFormat.format(command, 
				ApplicationProperties.getProperty("db.user"),
				ApplicationProperties.getProperty("db.password")
		);
	}
	
}