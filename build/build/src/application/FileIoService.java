package application;


import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import com.thoughtworks.xstream.XStream;

public class FileIoService {
	
	public boolean toXML(Object object, File file) {
		XStream xStream = new XStream();
		OutputStream outputStream = null;
		Writer writer = null;

		try {
			outputStream = new FileOutputStream(file);
			writer = new OutputStreamWriter(outputStream,
					Charset.forName("UTF-8"));
			xStream.toXML(object, writer);
		} catch (Exception exp) {
			exp.printStackTrace();// log.error(null, exp);
			return false;
		} finally {
			try {
				writer.close();
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return true;
	}

	public Object fromXML(File file) {
		XStream xStream = new XStream();
		return xStream.fromXML(file);
	}
	
	
	public static String createFolder(File folder) {
		try {
			FileUtils.forceMkdir(folder);
		} catch (IOException e) {
			return "Could not create folder: " + folder.getAbsolutePath();
		}
		return "";
	}

	public List<File> getFilesInFolder(File modelFolderFile, String[] extensions) {
		try {
			return (List<File>) FileUtils.listFiles(modelFolderFile, extensions, false);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
		
	}

	public boolean copyFileToFolder(File src, File target) {
		try {
			FileUtils.copyFileToDirectory(src, target, false);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public boolean copyFile(String emptyRsiFile, String rsiFile) {
		File emptFile = new File(emptyRsiFile);
		File file = new File(rsiFile);
		
		try {
			FileUtils.copyFile(emptFile, file, false);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	
	public static boolean openFile(File file) {
		assert(file != null);
		assert(file.exists());

		Desktop desktop = null;
		if (Desktop.isDesktopSupported()) {
			desktop = Desktop.getDesktop();
			try {
				desktop.open(file);
			} catch (IOException e1) {
				return false;
			}
		} else {
			// TODO: must handle eventually
			return false;
		}
		return true;
	}

	public void deleteFolder(File file) {
		try {
			FileUtils.forceDelete(file);
			//FileUtils.deleteDirectory(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<File> getFolders(File folder) {
		return (List<File>) FileUtils.listFilesAndDirs(folder, new NotFileFilter(TrueFileFilter.INSTANCE), DirectoryFileFilter.DIRECTORY);
	}
	
	public static String getExtension(File file){
		return FilenameUtils.getExtension(file.getName());
	}

	
}
