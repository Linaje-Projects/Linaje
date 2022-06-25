/*
 * Copyright 2022 Pablo Linaje
 * 
 * This file is part of Linaje Framework.
 *
 * Linaje Framework is free software: you can redistribute it and/or modify it under the terms 
 * of the GNU Lesser General Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 *
 * Linaje Framework is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Linaje Framework.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package linaje.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import linaje.logs.Console;
import linaje.statics.Constants;
import sun.awt.shell.ShellFolder;

public class Files {

	public static final int EQUALS = 0;
	public static final int STARTS = 1;
	public static final int ENDS = 2;
	public static final int CONTAINS = 3;
	
	public static final String FILE_SEPARATOR = Security.getSystemProperty(Security.KEY_FILE_SEPARATOR);
	public static final char[] FORBIDDEN_FILE_CHARACTERS = {'\\','/',':','*','?','\"','>','<','|'};
	
	public static byte[] read(File file) throws IOException {
		return Reader.read(new FileInputStream(file));
	}
	public static String readText(File file) throws IOException {
		return Reader.read(new FileReader(file));
	}
	public static List<String> readLines(File textFile) throws IOException {
		return Reader.readLines(new FileReader(textFile));
	}
	public static String readFirstLine(File textFile) throws IOException {
		return Reader.readFirstLine(new FileReader(textFile));
	}
	public static List<String> readLines(File textFile, int numLines, boolean readBlankLines) throws IOException {
		return Reader.readLines(new FileReader(textFile), numLines, readBlankLines);
	}
	public static Object readSerializedObject(File file) throws IOException, ClassNotFoundException {
		return Reader.readSerializedObject(new FileInputStream(file));
	}
	public static Properties readProperties(File fileProperties) throws IOException {
		return Reader.readProperties(new FileInputStream(fileProperties));
	}	
	/*
	 * Lo metemos en try catch para no interrumpir el código por no poder reproducir un audio
	 * Si se quiere controlar la excepción usar el playSound de Reader
	 */
	public static void playSound(File soundFile) {
		try {
			Reader.playSound(new FileInputStream(soundFile));
		}
		catch (Exception ex) {
			Console.printException(ex);
		}
	}
	
	public static void save(byte[] bytes, File file) throws IOException {
		
		FileOutputStream fos = null;
		
		try {
			
			createDirParent(file);
			fos = new FileOutputStream(file);
					
			fos.write(bytes);
		}
		finally {
			if (fos != null) {
				try { fos.close(); } catch (IOException e) {}
			}
		}
	}
	
	
	public static void saveText(List<String> lines, File file) throws IOException {
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {
			
			createDirParent(file);
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			
			for (int i = 0; i < lines.size(); i++) {
				if (i > 0)
					bw.newLine();
				bw.write(lines.get(i));
			}
		}
		finally {
			if (bw != null) {
				try { bw.close(); } catch (IOException e) {}
			}
			if (fw != null) {
				try { fw.close(); } catch (IOException e) {}
			}
		}
	}
	
	public static void saveText(String texto, File file) throws IOException {
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {
			
			createDirParent(file);
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			
			bw.write(texto);
		}
		finally {
			if (bw != null) {
				try { bw.close(); } catch (IOException e) {}
			}
			if (fw != null) {
				try { fw.close(); } catch (IOException e) {}
			}
		}
	}
	
	public static void saveProperties(Properties properties, File file, String comments) throws IOException {
		
		StringWriter sw = new StringWriter();
		properties.store(sw, comments);
		
		Files.saveText(sw.toString(), file);
	}
		
	public static void serializeObject(Object object, File file) throws IOException {
		
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		
		try {
			
			createDirParent(file);
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
					
			oos.writeObject(object);
		}
		finally {
			if (oos != null) {
				try { oos.close(); } catch (IOException e) {}
			}
			if (fos != null) {
				try { fos.close(); } catch (IOException e) {}
			}
		}
	}
	
	public static void copyDir(File sourceDir, File targetDir) throws IOException {

		if (sourceDir.exists() && sourceDir.isDirectory()) {
				
			File[] sourceFiles = sourceDir.listFiles();

			if (!targetDir.exists())
				targetDir.mkdirs();

			String targetPath = targetDir.getAbsolutePath();
			if (!targetPath.endsWith(FILE_SEPARATOR))
				targetPath = targetPath + FILE_SEPARATOR;
			
			for (int i = 0; i < sourceFiles.length; i++) {

				if (sourceFiles[i].isDirectory())
					copyDir(sourceFiles[i], new File(targetPath + sourceFiles[i].getName()));
				else	
					copyFile(sourceFiles[i], new File(targetPath + sourceFiles[i].getName()));
			}
		}
	}
	
	public static void copyFile(File sourceFile, File targetFile) throws IOException {
		byte[] sourceFileData = read(sourceFile);
		save(sourceFileData, targetFile);
	}
	public static void copyResource(String resourceName, File file) throws IOException {
		byte[] resourceData = Resources.read(resourceName);
		save(resourceData, file);
	}
	public static void copyResource(URL url, File file) throws IOException {
		byte[] resourceData = Resources.read(url);
		save(resourceData, file);
	}
	
	public static void rename(File sourceFile, File targetFile) throws IOException {
		
		try {
			if (targetFile.exists())
				targetFile.delete();
			sourceFile.renameTo(targetFile);
		}catch (Throwable e) {
			Files.copyFile(sourceFile, targetFile);
		}
	}

	public static File createDir(String dirPath) {

		File dir = new File(dirPath);
		createDir(dir);
		
		return dir;
	}
	
	public static void createDir(File dir) {

		if (dir != null && !dir.exists())
			dir.mkdirs();
	}
	
	public static File createFile(String filePath) {
		
		File file = new File(filePath);
		createDirParent(file);
		
		return file;
	}
	
	public static void createDirParent(File file) {
		
		createDir(file.getParentFile());
	}
	
	public static File getFileStartsWith(File dir, String partialFileName) {
		return getFirstFileFromDir(dir, partialFileName, STARTS, false);
	}
	public static File getFileEndsWith(File dir, String partialFileName) {
		return getFirstFileFromDir(dir, partialFileName, ENDS, false);
	}
	public static File getFileEquals(File dir, String fileName) {		
		return getFirstFileFromDir(dir, fileName, EQUALS, false);
	}	
	public static File getFileContains(File dir, String partialFileName) {		
		return getFirstFileFromDir(dir, partialFileName, CONTAINS, false);
	}
	
	public static File getFirstFileFromDir(File dir, String partialFileName, int equity, boolean includeDirs) {
		
		List<File> files = getFilesFromDir(dir, partialFileName, equity, includeDirs, true);
		
		if (files.isEmpty())
			return null;
		else
			return Lists.getFirstElement(files);
	}
	
	public static List<File> getFilesFromDir(File dir, String partialFileName, int equity, boolean includeDirs) {
		
		return getFilesFromDir(dir, partialFileName, equity, includeDirs, false);
	}
	
	private static List<File> getFilesFromDir(File dir, String partialFileName, int equity, boolean includeDirs, boolean onlyFirstFound) {
		
		List<File> files = Lists.newList();
		
		if (dir != null && dir.exists() && dir.isDirectory()) {
			
			List<File> allFiles = getFilesFromDir(dir);
			String partialFileNameUPPER = null;
			
			for (int i = 0; i < allFiles.size(); i++) {
				
				File file = allFiles.get(i);
				boolean found = false;
				
				if (includeDirs || !file.isDirectory()) {
					
					if (partialFileNameUPPER == null)
						partialFileNameUPPER = partialFileName.toUpperCase();
					String fileNameUPPER = file.getName().toUpperCase();
					
					if (equity == EQUALS)
						found = fileNameUPPER.equals(partialFileNameUPPER);
					else if (equity == STARTS)
						found = fileNameUPPER.startsWith(partialFileNameUPPER);
					else if (equity == ENDS)
						found = fileNameUPPER.endsWith(partialFileNameUPPER);
					else if (equity == CONTAINS)
						found = fileNameUPPER.indexOf(partialFileNameUPPER) != -1;
					
					if (found) {
						files.add(file);
						if (onlyFirstFound)
							return files;
					}
				}
			}
		}
				
		return files;
	}
	
	public static List<File> getFilesFromDir(File dir) {
		return getFilesFromDir(dir, null);
	}
	public static List<File> getFilesFromDir(File dir, FileFilter fileFilter) {
		
		List<File> files = null;
		try {
			
			if (dir != null && dir.exists() && dir.isDirectory()) {
				File[] filesArr = dir.listFiles(fileFilter);
				files = Lists.arrayToList(filesArr);
			}
		}
		catch (Exception ex) {
			Console.printException(ex);
		}
		
		if (files == null)
			files = Lists.newList();
				
		return files;
	}
	
	public static Process open(File file) throws IOException {
		return Processes.executeFile(file);
	}

	public static Process openWith(File file, String app) throws IOException {
		
		if (Security.isOSWindows())
			return ProcessesWindows.executeFileWith(file, app);
		else
			return open(file);
	}
	
	public static String preparePathWithSpaces(String path) {
		
		if (path.indexOf(Constants.SPACE) != -1)
			return Constants.QUOTE + path + Constants.QUOTE;
		else
			return path;
	}
	
	public static String preparePathWithSpacesByDirectory(String ruta) {
		
		//Ponemos comillas a cada directorio que tenga espacios
		String[] dirNames = Strings.split(ruta, File.separator);
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < dirNames.length; i++) {
			String dirName = dirNames[i];
			if (dirName.indexOf(Constants.SPACE) != -1)
				dirName = Constants.QUOTE + dirName + Constants.QUOTE;
			sb.append(dirName);
			if (i < dirNames.length - 1)
				sb.append(File.separator);
		}
		
		return sb.toString();
	}
	
	public static String getExt(File file) throws IOException {
		
		String ext = null;
		/*try {
			if (file.exists()) {
				ShellFolder shellfolder = getShellFolder(file);
				ext = shellfolder.getFolderType();
				int indexSpace = ext.lastIndexOf(Constants.SPACE);
				if (indexSpace > 0)
					ext = ext.substring(indexSpace+1);
			}
		}
		catch (Throwable ex) {
		}
		if (ext == null || ext.equals(Constants.VOID))*/
			ext = getExtAlternative(file);
		
		return ext.toLowerCase();
	}

	private static String getExtAlternative(File file) {
		
		String fileName = file.getName();
		int indexPoint = fileName.lastIndexOf(Constants.POINT);
		String ext = indexPoint > 0 ? fileName.substring(indexPoint+1) : Constants.VOID;
		
		return ext;
	}
	
	public static String getNameWithoutExt(File file) {
		
		String fileName = file.getName();
		int indexPoint = fileName.lastIndexOf(Constants.POINT);
		String fileNameWithoutExt = indexPoint > 0 ? fileName.substring(0, indexPoint) : fileName;
		
		return fileNameWithoutExt;
	}
	
	public static ShellFolder getShellFolder(File file) throws FileNotFoundException {
		
		ShellFolder shellfolder = file instanceof ShellFolder ? (ShellFolder) file : ShellFolder.getShellFolder(file);
		return shellfolder;
	}
	
	public static boolean update(File currentFile, File updatedFile) throws IOException {
		
		if (updatedFile.exists()) {

			Date updatedDate = new Date(updatedFile.lastModified());
			Date currentDate = new Date(currentFile.lastModified());
			//Solo copiamos el fichero si la fecha del fichero actual es anterior a la fecha del fichero actualizado
			if (updatedDate.compareTo(currentDate) > 0)
				copyFile(updatedFile, currentFile);
			
			return true;
		}
		
		return false;
	}
	
	public static void deleteDirContent(File dir) throws IOException {
		
		if (dir != null && dir.exists() && dir.isDirectory()) {
			
			String[] files = dir.list();

			for (int i = 0; i < files.length; i++) {			
				File file = new File(dir, files[i]);
				if (file.isDirectory())
					deleteDirContent(file);
				file.delete();	
			}
			//Console.println("Se han eliminado " + files.length + " elementos de la carpeta \"" + dir.getAbsolutePath() + "\"", Color.ORANGE);
		}
	}
	
	public static boolean isNameCompatibleWithWindowsFiles(String fileName) {
		
		if (fileName != null) {
			for (int i = 0; i < FORBIDDEN_FILE_CHARACTERS.length; i++) {
				if (fileName.indexOf(FORBIDDEN_FILE_CHARACTERS[i]) != -1)
					return false;
			}
			return true;
		}
		else return false;
	}
	
	public static String getNameCompatibleWithWindowsFiles(String fileName) {
		
		//Quitamos los espacios seguidos que no le sientan bien a los VBScript
		final boolean removeSpacesExtra = true;
		
		return Strings.replaceForbiddenCharacters(fileName, FORBIDDEN_FILE_CHARACTERS, removeSpacesExtra);
	}
	
	public static File getFileNonExistent(File dir, String fileNameWithoutExt, String ext) {
		
		if (fileNameWithoutExt == null)
			fileNameWithoutExt = Constants.VOID;
		if (ext == null)
			ext = Constants.VOID;
		
		boolean hasExt = !ext.trim().equals(Constants.VOID);
		if (hasExt && !ext.startsWith(Constants.POINT))
			ext = Constants.POINT + ext;
		
		File fileNonExistent = new File(dir, fileNameWithoutExt + ext);
		while (fileNonExistent.exists()) {
			
			boolean hasName = !fileNameWithoutExt.trim().equals(Constants.VOID);
			if (hasName || hasExt)
				fileNameWithoutExt = Strings.newName(fileNameWithoutExt);
			else {
				//Es un directorio
				String newDirPath = Strings.newName(dir.getAbsolutePath());
				dir = new File(newDirPath);
			}
			fileNonExistent = new File(dir, fileNameWithoutExt + ext);
		}
		
		return fileNonExistent;
	}
	
	/**
	 * Si el fichero no existe devolverá el mismo fichero y si ya existe uno que se llame igual, devolverá un fichero con el mismo nombre con el primer número entre préntesis disponible.
	 * Por ejemplo: 
	 * 		fichero.txt --> fichero (1).txt
	 * 		fichero.txt --> fichero (2).txt
	 * 		fichero (4).txt --> fichero (5).txt
	 * */
	public static File getFileNonExistent(File file) {
	
		if (file.exists()) {
			
			String ext = null;
			String fileNameWithoutExt = null;
			File dir;
			if (file.isDirectory()) {
				
				dir = file;
			}
			else {
							
				dir = file.getParentFile();
				String fileName = file.getName();
				int indexPoint = fileName.lastIndexOf(Constants.POINT);
				fileNameWithoutExt = fileName;
				if (indexPoint > 0) {
					ext = fileName.substring(indexPoint+1);
					fileNameWithoutExt = fileName.substring(0, indexPoint);
				}
			}
			
			return getFileNonExistent(dir, fileNameWithoutExt, ext);
		}
		else {
			return file;
		}
	}
}
