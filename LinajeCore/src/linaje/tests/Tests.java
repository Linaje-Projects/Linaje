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
package linaje.tests;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.crypto.BadPaddingException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import linaje.App;
import linaje.LocalizedStrings;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.statics.Directories;
import linaje.utils.Dates;
import linaje.utils.Encryptor;
import linaje.utils.FormattedData;
import linaje.utils.Lists;
import linaje.utils.Processes;
import linaje.utils.Xmls;

public class Tests {

	public static class Texts extends LocalizedStrings {

		public String chooseTest;
		public String test1;
		public String test2;
		public String test3;
		public String test4;
		public String test5;
		public String exit;
		public String runConsoleFirst;
		public String endedTests;
		public String endedTest;
		public String chooseCorrect;
		public String consoleTest;
		public String lastPeriodDates;
		public String title;
		public String titleChecked;
		
		public String colorTracesNoBg;
		public String colorTracesBgBlack;
		public String colorTracesBgWhite;
		public String tracesIn;
		public String traceIn;
		public String tracesOut;
		public String traceOut;
		public String tracesOther;
		public String traceOther;
		public String tracesError;
		public String traceColor;
		public String testEx;

		public String chooseCompatible;
		public String compatible_24bit;
		public String compatible_8bit;
		public String no_compatible;

		public String datesTestDesc1;
		public String datesTestDesc2;

		public String instanceFormattedData;
		public String formatWithThousand;
		public String setDecimals;
		public String getFormatNumber;

		public String formatPeriods;
		public String getFormatDate;
		public String getFormatFont;
		public String getFormatColor;
		
		public String createDocument;
		public String saveDocument;
		public String readDocument;
		
		public String testText;
		public String insertText;
		public String insertPass;
		public String text;
		public String encryptedText;
		public String decryptedText;
		public String repeatPass;
		public String wrongPass;
		public String wrongPassEx;
		
		public Texts(String resourceBundle) {
			super(resourceBundle);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts(LocalizedStrings.DEFAULT_TESTS_RESOURCE_BUNDLE);
	
	Scanner scanner = new Scanner(System.in);
	
	public static void main(String[] args) {

		App.getCurrentApp().setName("Linaje tests");
		App.getCurrentApp().setId("LT");
		
		Console.setConsoleWindowEnabled(true);
		
		Tests tests = new Tests();
		tests.initialize();
	}

	private void printOptions() {
		
		Color colorOptions = new Color(0, 192, 0);
		Console.println(TEXTS.chooseTest, colorOptions);
		Console.print("1. ", colorOptions); Console.print(TEXTS.test1); Console.println(TEXTS.runConsoleFirst, Color.orange);
		Console.print("2. ", colorOptions); Console.println(TEXTS.test2);
		Console.print("3. ", colorOptions); Console.println(TEXTS.test3);
		Console.print("4. ", colorOptions); Console.println(TEXTS.test4);
		Console.print("5. ", colorOptions); Console.println(TEXTS.test5);
		Console.print("0. ", colorOptions); Console.println(TEXTS.exit);
	}
	
	private void initialize() {
		
		printOptions();
		
		String option = scanner.nextLine();
		Color colorOptions = new Color(0, 192, 0);
		while (!option.equals(Constants.VOID)) {
			
			Console.print(option+". ", colorOptions);
			if (option.trim().equals("1")) {
				Console.println(TEXTS.test1, Color.magenta);
				executeConsoleTest();
			}
			else if (option.trim().equals("2")) {
				Console.println(TEXTS.test2, Color.magenta);
				executeDatesTest();
			}
			else if (option.trim().equals("3")) {
				Console.println(TEXTS.test3, Color.magenta);
				executeFormattedDataTest();
			}
			else if (option.trim().equals("4")) {
				Console.println(TEXTS.test4, Color.magenta);
				executeXmlsTest();
			}
			else if (option.trim().equals("5")) {
				Console.println(TEXTS.test5, Color.magenta);
				executeEncryptTest();
			}
			else if (option.trim().equals("0")) {
				Console.println(TEXTS.endedTests);
				System.exit(0);
			}
			else {
				Console.println(TEXTS.chooseCorrect);
			}
			
			Console.println(TEXTS.endedTest, Color.red);
			Console.println(Constants.VOID);
			printOptions();
			option = scanner.nextLine();
		}
	}
	
	private void executeConsoleTest() {
		//Ejecutar ConsoleWindow en paralelo
		chooseColorCompatibility();
		//Console.setCurrentSystemConsoleAnsiColorsCompatibility(Console.SYSTEM_CONSOLE_COLORS_24_BIT);
		
		List<Color> colors = Lists.newList();
		colors.add(Color.red);
		colors.add(new Color(255,192,64));
		colors.add(Color.yellow);
		colors.add(new Color(192,255,64));
		colors.add(Color.green);
		colors.add(new Color(64,255,192));
		colors.add(Color.cyan);
		colors.add(new Color(64,192,255));
		colors.add(Color.blue);
		colors.add(new Color(192,64,255));
		colors.add(Color.magenta);
		colors.add(new Color(255,64,192));
		colors.add(Color.black);
		colors.add(Color.white);
		
		Console.println(TEXTS.colorTracesNoBg);
		printColorTraces(colors);
		Console.println(TEXTS.colorTracesBgBlack);
		Console.println("Console.setCurrentSystemBackground(Color.black)", Color.blue);
		Console.setCurrentSystemBackground(Color.black);
		printColorTraces(colors);
		Console.println(TEXTS.colorTracesBgWhite);
		Console.setCurrentSystemBackground(null);
		Console.println("Console.setCurrentSystemBackground(Color.white)", Color.blue);
		Console.setCurrentSystemBackground(Color.white);
		printColorTraces(colors);
		Console.setCurrentSystemBackground(null);
		
		Console.println(TEXTS.tracesIn, Console.TYPE_DATA_IN);
		for (int i = 1; i < 5; i++) {
			Console.println(TEXTS.traceIn+i, Color.green, Console.TYPE_DATA_IN);
		}
		
		Console.printLineBreak();
		Console.println(TEXTS.tracesOut, Console.TYPE_DATA_OUT);
		for (int i = 1; i < 10; i++) {
			Console.println(TEXTS.traceOut+i, Color.blue, Console.TYPE_DATA_OUT);
		}
		
		Console.printLineBreak();
		Console.println(TEXTS.tracesOther, Console.TYPE_DATA_OTHER);
		for (int i = 1; i < 5; i++) {
			Console.println(TEXTS.traceOther+i, Color.orange, Console.TYPE_DATA_OTHER);
		}
		
		Console.printLineBreak();
		Console.println(TEXTS.tracesError, Console.TYPE_DATA_ERROR);
		Exception ex = new Exception(TEXTS.testEx);
		Console.printException(ex);
	}
	
	private void chooseColorCompatibility() {
		
		Color colorOptions = Color.blue;
		Console.println(TEXTS.chooseCompatible, colorOptions);
		Console.print("1. ", colorOptions); Console.println(TEXTS.compatible_24bit);
		Console.print("2. ", colorOptions); Console.println(TEXTS.compatible_8bit);
		Console.print("3. ", colorOptions); Console.println(TEXTS.no_compatible);
		String option = scanner.nextLine();
		if (!option.equals(Constants.VOID)) {
			
			if (option.trim().equals("1")) {
				Console.setCurrentSystemConsoleAnsiColorsCompatibility(Console.SYSTEM_CONSOLE_COLORS_24_BIT);
			}
			else if (option.trim().equals("2")) {
				Console.setCurrentSystemConsoleAnsiColorsCompatibility(Console.SYSTEM_CONSOLE_COLORS_8_BIT);
			}
			else if (option.trim().equals("3")) {
				Console.setCurrentSystemConsoleAnsiColorsCompatibility(Console.SYSTEM_CONSOLE_COLORS_NOT_COMPATIBLE);
			}
			else {
				chooseColorCompatibility();
			}
		}
	}
	
	private void printColorTraces(List<Color> colors) {
		for (int i = 0; i < colors.size(); i++) {
			try {
				Color color = colors.get(i);
				Console.println(TEXTS.traceColor+color.toString(), color);
				//Thread.sleep(100);
			} catch (Exception ex) {
				Console.printException(ex);
			}
		}
		Console.printLineBreak();
	}
	
	private void executeDatesTest() {
		
		try {
			Console.println(TEXTS.datesTestDesc1);
			Console.println(TEXTS.datesTestDesc2);
			Console.println("Dates.getLastDatesOfPeriod(Date startDate, int period, boolean includeCurrentDate, boolean useFirstPeriodDate, int numDates)", Color.blue);
			
			for (int i = 0; i < Dates.PERIODS.length; i++) {
				int period = Dates.PERIODS[i];
				ArrayList<Date> dates = Dates.getLastDatesOfPeriod(null, period, true, true, -1);
				Console.printLineBreak();
				Console.println(TEXTS.lastPeriodDates + Dates.getPeriodName(period), Color.blue);
				for (int j = 0; j < dates.size(); j++) {
					Console.print(Dates.getFormattedDate(dates.get(j), period));
					if (period != Dates.PERIOD_DAILY && period != Dates.PERIOD_WEEKLY) {
						FormattedData formattedData = new FormattedData(dates.get(j));
						formattedData.setPeriod(Dates.PERIOD_DAILY);
						formattedData.setPrefix("\t (");
						formattedData.setPostfix(")");
						Console.println(formattedData.getFormattedText(), Color.blue);
					}
					else {
						Console.printLineBreak();
					}
				}
			}
		}
		catch (Exception ex) {
			Console.printException(ex);
		}	
	}
	
	private void executeFormattedDataTest() {
		
		try {
			
			formatNumbers();
			formatDates();
			formatFonts();
			formatColors();
			
			/*HashMap<String, String> fontFieldsMap = Utils.decodeFieldValuesMap(font.toString());
			String encodedFont = Utils.encodeFieldValuesMap(fontFieldsMap, font.getClass());
			Console.println(Utils.decodeFont(font.toString()).toString());
			Console.println(Font.decode(font.toString()).toString());
			Console.println(Font.decode(encodedFont).toString());
			Console.println(encodedFont);*/
		}
		catch (Exception ex) {
			Console.printException(ex);
		}
	}
	
	private void formatNumbers() {
		
		Color colorTraces = Color.blue;
		double number = 12345678.9;
		Console.println(TEXTS.instanceFormattedData);
		Console.println("double number = 12345678.9;", colorTraces);
		FormattedData formattedData = new FormattedData(number);
		Console.println(TEXTS.formatWithThousand);
		Console.println("formattedData.setThousandsSeparatorEnabled(true);", colorTraces);
		formattedData.setThousandsSeparatorEnabled(true);
		Console.print("formattedData.getFormattedText(): ", colorTraces);
		Console.println(formattedData.getFormattedText());
		Console.println(TEXTS.setDecimals);
		Console.println("formattedData.setDecimals(3);", colorTraces);
		formattedData.setDecimals(3);
		Console.print("formattedData.getFormattedText(): ", colorTraces);
		Console.println(formattedData.getFormattedText());
		Console.print("formattedData.getValue().toString(): ", colorTraces);
		Console.println(formattedData.getValue().toString());
		Console.println(TEXTS.getFormatNumber);
		Console.print("FormattedData.getValueNumber(formattedData.getFormattedText(), true).toString(): ", colorTraces);
		Console.println(FormattedData.getValueNumber(formattedData.getFormattedText(), true).toString());
		Console.printLineBreak();
	}
	
	private void formatDates() throws ParseException {
		
		Color colorTraces = Color.magenta;
		Date date = Calendar.getInstance().getTime();
		Console.println(TEXTS.instanceFormattedData);
		Console.println("Calendar.getInstance().getTime();", colorTraces);
		FormattedData formattedData = new FormattedData(date);
		Console.println(TEXTS.formatPeriods);
		Console.println("formattedData.setPeriod(Dates.PERIOD_DAILY);", colorTraces);
		formattedData.setPeriod(Dates.PERIOD_DAILY);
		Console.print("formattedData.getFormattedText(): ", colorTraces);
		Console.println(formattedData.getFormattedText());
		Console.println("formattedData.setPeriod(Dates.PERIOD_MONTHLY);", colorTraces);
		formattedData.setPeriod(Dates.PERIOD_MONTHLY);
		Console.print("formattedData.getFormattedText(): ", colorTraces);
		Console.println(formattedData.getFormattedText());
		Console.print("formattedData.getValue().toString(): ", colorTraces);
		Console.println(formattedData.getValue().toString());
		Console.println(TEXTS.getFormatDate);
		Console.print("FormattedData.getValueDate(formattedData.getFormattedText(), true).toString(): ", colorTraces);
		Console.println(FormattedData.getValueDate(formattedData.getFormattedText(), true).toString());
		Console.printLineBreak();
	}
	
	private void formatFonts() throws ParseException {
		
		Color colorTraces = Color.blue;
		Font font = new Font("Arial", Font.BOLD, 16);
		Console.println(TEXTS.instanceFormattedData);
		Console.println("Font font = new Font(\"Arial\", Font.BOLD, 16);", colorTraces);
		FormattedData formattedData = new FormattedData(font);
		Console.print("formattedData.getFormattedText(): ", colorTraces);
		Console.println(formattedData.getFormattedText());
		Console.print("formattedData.getValue().toString(): ", colorTraces);
		Console.println(formattedData.getValue().toString());
		Console.println(TEXTS.getFormatFont);
		Console.print("FormattedData.getValueFont(formattedData.getFormattedText()).toString(): ", colorTraces);
		Console.println(FormattedData.getValueFont(formattedData.getFormattedText()).toString());
		Console.printLineBreak();
	}
	
	private void formatColors() throws ParseException {
		
		Color colorTraces = Color.magenta;
		Color color = Color.magenta;
		Console.println(TEXTS.instanceFormattedData);
		Console.println("Color color = Color.magenta;", colorTraces);
		FormattedData formattedData = new FormattedData(color);
		Console.print("formattedData.getFormattedText(): ", colorTraces);
		Console.println(formattedData.getFormattedText());
		Console.print("formattedData.getValue().toString(): ", colorTraces);
		Console.println(formattedData.getValue().toString());
		Console.println(TEXTS.getFormatColor);
		Console.print("FormattedData.getValueColor(formattedData.getFormattedText()).toString(): ", colorTraces);
		Console.println(FormattedData.getValueColor(formattedData.getFormattedText()).toString());
		Console.printLineBreak();
	}
	
	private void executeXmlsTest() {
		
		try {
			
			Console.println(TEXTS.createDocument);
			final String[] TITULOS = {"Mi libro < & 1", "Mi Libro > \"2\""}; 
			final String[] ANOS = {"2018", "2019"}; 
			
			Document doc = Xmls.createXMLDocument("Biblioteca");
			Element root = Xmls.getRootNode(doc);
			
			for (int i = 0; i < TITULOS.length; i++) {
				
				Element nodoLibro = Xmls.createChildNode(root, "Libro");
				Element nodoTitulo = Xmls.createChildNode(nodoLibro, "Titulo", TITULOS[i]);
				Xmls.createAttribute(nodoTitulo, "lang", "spanish");
				Xmls.createChildNode(nodoLibro, "Año", ANOS[i]);
			}
			Console.println(TEXTS.saveDocument);
			File xmlFile = new File(Directories.getAppGeneratedFiles(),  "test.xml");
			Xmls.saveXMLFile(xmlFile, doc);
			
			Console.println(TEXTS.readDocument);
			doc = Xmls.readXMLFile(xmlFile);
			root = Xmls.getRootNode(doc);
			
			NodeList nodosTitulo = Xmls.getChildNodes(root, "Titulo");
			for (int i = 0; i < nodosTitulo.getLength(); i++) {
				Node nodoTitulo = nodosTitulo.item(i);
				String tituloOrig = TITULOS[i];
				String tituloRevisado = Xmls.getValue(nodoTitulo);
				Console.println(TEXTS.title + tituloOrig);
				Console.println(TEXTS.titleChecked + tituloRevisado);
			}
			
			Processes.executeFile(xmlFile);
		}
		catch (Exception ex) {
			Console.printException(ex);
		}
	}
	
	private void executeEncryptTest() {
		
		try {
			
			String text = TEXTS.testText;
			Encryptor encryptor = Encryptor.getInstance();
			String encriptedText = encryptor.encryptText(text);
			Console.print(TEXTS.text, Color.green); Console.println(text);
			Console.print(TEXTS.encryptedText, Color.red); Console.println(encriptedText);
			String decryptedText = encryptor.decryptText(encriptedText);
			Console.print(TEXTS.decryptedText, Color.green); Console.println(decryptedText);
			Console.printLineBreak();
			
			Console.println(TEXTS.insertText);
			text = scanner.nextLine();
			String key = readPassword(TEXTS.insertPass);
			encryptor = new Encryptor(key);
			encriptedText = encryptor.encryptText(text);
			Console.print(TEXTS.text, Color.green); Console.println(text);
			Console.print(TEXTS.encryptedText, Color.red); Console.println(encriptedText);
			
			String message = TEXTS.repeatPass;
			String key2;
			int maxTries = 3;
			int numTries = 0;
			decryptedText = null;
			while (decryptedText == null) {
				try {
					key2 = readPassword(message);
					encryptor = new Encryptor(key2);
					decryptedText = encryptor.decryptText(encriptedText);	
				}
				catch (BadPaddingException ex) {
					numTries++;
					if (numTries < maxTries)
						message = TEXTS.wrongPass + (maxTries - numTries) +")";
					else
						throw new Exception(TEXTS.wrongPassEx);
				}
			}
			Console.print(TEXTS.decryptedText, Color.green); Console.println(decryptedText);
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	private String readPassword(String message) {
		
		java.io.Console systemConsole = System.console();
		String key;
		if (systemConsole != null)
			key = new String(systemConsole.readPassword(message));
		else {
			Color color = message.contains("(") ? Color.red : null;
			Console.println(message, color);
			key = scanner.nextLine();
		}
		
		return key;
	}
}
