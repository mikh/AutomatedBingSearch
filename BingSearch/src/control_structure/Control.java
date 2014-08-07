package control_structure;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.remote.UnreachableBrowserException;

import logging.Log;
import file_operations.FileOps;
import tools.Basics;
import web.Client;
import define_loader.DefineLoader;

public class Control {
	public static void main(String[] args){
		
		
		try {
			System.out.println("Loading defined values from xml.");
			DefineLoader dl = new DefineLoader("Automated Bing Search");
			if(!dl.loadDefines("defines.xml", Defines.class)){
				System.out.println("[ERROR] Defines xml file not correct. Please specify the location of the correct file.");
			}
		} catch (IllegalArgumentException  e1) {
			System.out.println("[ERROR] Improper loading of defines!");
		}


		Log log = new Log(Defines.LOGGING_LEVEL, Defines.LOG_FILE_LOCATION);

		try{
			log.write(2, "Starting BingSearch Automation v" + Defines.VERSION + "\r\n");
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			log.write(2, " Starting at " + dateFormat.format(new Date()) + "\r\n");
			
			long time_start = System.currentTimeMillis(), time_elapsed;
			
			time_elapsed = System.currentTimeMillis();
			
			log.write(2, "Starting Web Client.\r\n");
			Client cc = new Client(Defines.BING_LOGIN_URL, null, Defines.FIREFOX_PATH);
			System.out.println(cc.getPageTitle());
			if(cc.checkForAlert(60))	//wait 60s
				cc.acceptAlert();
			log.write(2, "Web Client Ready. Took " + (System.currentTimeMillis() - time_elapsed) + "ms.\r\n");
			
			time_elapsed = System.currentTimeMillis();
			log.write(2, "Loading dictionary.\r\n");
			ArrayList<String> dictionary = FileOps.loadFileIntoArrayList(Defines.DICTIONARY_FILE);
			log.write(2, "Dictionary ready. Took " + (System.currentTimeMillis() - time_elapsed) + "ms.\r\n");

			time_elapsed = System.currentTimeMillis();
			log.write(2, "Performing login.\r\n");
			if(!login(cc)){
				log.write(1, "Login Failed");
				throw new LoginException();
			}
			System.out.println(cc.getPageTitle());
			log.write(2, "Logged in. Took " + (System.currentTimeMillis() - time_elapsed) + "ms.\r\n");
			
			time_elapsed = System.currentTimeMillis();
			log.write(2, "Starting searches.\r\n");
			runSearches(Defines.NUMBER_OF_SEARCHES, cc, dictionary, log);
			log.write(2, "Search complete. Took " + (System.currentTimeMillis() - time_elapsed) + "ms.\r\n");

			log.write(2, "All operations complete at " + dateFormat.format(new Date()) + ". Took " + (System.currentTimeMillis() - time_start) + "ms.\r\n");
		
		} catch (IOException e) {
			log.write(1,"Couldn't load dictionary.\r\n");
		} catch (UnreachableBrowserException e){
			log.write(1, "Browser has closed prematurely.\r\n");
		} catch (LoginException e){
			log.write(1, "Unable to login. Aborting. \r\n");
		}
		
		log.write(2, "Operation Complete. Closing log.\r\n");
		log.close();
		
	}
	
	public static boolean login(Client cc){
		String title = Defines.PRELOGIN_PAGE_TITLE;
		int attempts = 0;
		
		while(title.equals(Defines.PRELOGIN_PAGE_TITLE) && attempts++ < Defines.MAX_LOGIN_ATTEMPTS){
			cc.sendKeys(Defines.LOGIN_ID, Defines.USERNAME, false);
			cc.sendKeys(Defines.PASSWORD_ID, Defines.PASS, false);
			cc.click(Defines.SIGNIN_BTN_ID, false);
			try{
				if(cc.checkForAlert(30))
					cc.acceptAlert();
			} catch(NoAlertPresentException e){
				//if no alert it's hardly the end of the world...
			}
			title = cc.getPageTitle();
		}
		
		if(attempts == Defines.MAX_LOGIN_ATTEMPTS){
			return false;
		}
		return true;
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
