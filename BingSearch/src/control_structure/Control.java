package control_structure;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import logging.Log;
import file_operations.FileOps;
import tools.Basics;
import web.Client;

public class Control {
	public static void main(String[] args){
		Log log = new Log(Defines.LOGGING_LEVEL, Defines.LOG_FILE_LOCATION);

		try{
			log.write(2, "Starting BingSearch Automation v" + Defines.VERSION + "\r\n");
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			log.write(2, " Starting at " + dateFormat.format(new Date()) + "\r\n");
			
			long time_start = System.currentTimeMillis(), time_elapsed;
			
			time_elapsed = System.currentTimeMillis();
			
			log.write(2, "Starting Web Client.\r\n");
			Client cc = new Client(Defines.BING_LOGIN_URL);
			log.write(2, "Web Client Ready. Took " + (System.currentTimeMillis() - time_elapsed) + "ms.\r\n");
			
			time_elapsed = System.currentTimeMillis();
			log.write(2, "Loading dictionary.\r\n");
			ArrayList<String> dictionary = FileOps.loadFileIntoArrayList(Defines.DICTIONARY_FILE);
			log.write(2, "Web Client Ready. Took " + (System.currentTimeMillis() - time_elapsed) + "ms.\r\n");

			time_elapsed = System.currentTimeMillis();
			log.write(2, "Performing login.\r\n");
			login(cc);
			log.write(2, "Web Client Ready. Took " + (System.currentTimeMillis() - time_elapsed) + "ms.\r\n");
			
			time_elapsed = System.currentTimeMillis();
			log.write(2, "Starting searches.\r\n");
			runSearches(Defines.NUMBER_OF_SEARCHES, cc, dictionary, log);
			log.write(2, "Search complete. Took " + (System.currentTimeMillis() - time_elapsed) + "ms.\r\n");

			log.write(2, "All operations complete at " + dateFormat.format(new Date()) + ". Took " + (System.currentTimeMillis() - time_start) + "ms.\r\n");
		
		} catch (IOException e) {
			log.write(1,"Couldn't load dictionary.");
		}
		
		log.write(2, "Operation Complete. Closing log.\r\n");
		log.close();
		
	}
	
	public static void login(Client cc){
		cc.sendKeys(Defines.LOGIN_ID, Defines.USERNAME, false);
		cc.sendKeys(Defines.PASSWORD_ID, Defines.PASS, false);
		cc.click(Defines.SIGNIN_BTN_ID, false);
		cc.acceptAlert();
	}
	
	public static void runSearches(int number_of_searches, Client cc, ArrayList<String> dictionary, Log log){
		Random rand = new Random(System.currentTimeMillis());
		for(int ii = 0; ii < number_of_searches; ii++){
			cc.getPage(Defines.BING_URL);
			String phrase = dictionary.get(rand.nextInt(dictionary.size())) + " " + dictionary.get(rand.nextInt(dictionary.size())) + " " + dictionary.get(rand.nextInt(dictionary.size()));
			log.write(3, "Performing search " + phrase + ".\r\n");
			cc.sendKeys(Defines.SEARCH_BAR_ID, phrase, false);
			Basics.delay(Defines.DELAY_GO);
			cc.click(Defines.SEARCH_BTN_ID, false);
			Basics.delay(Defines.DELAY_SEARCH);
		}
	}
}
