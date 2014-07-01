package control_structure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import file_operations.FileOps;
import tools.Basics;
import web.Client;

public class Control {
	public static void main(String[] args){
		try{
			Client cc = new Client(Defines.BING_LOGIN_URL);
			ArrayList<String> dictionary = FileOps.loadFileIntoArrayList(Defines.DICTIONARY_FILE);
			login(cc);
			runSearches(Defines.NUMBER_OF_SEARCHES, cc, dictionary);
		} catch (IOException e) {
			System.out.println("Couldn't load dictionary.");
		}
		
	}
	
	public static void login(Client cc){
		cc.sendKeys(Defines.LOGIN_ID, Defines.USERNAME, false);
		cc.sendKeys(Defines.PASSWORD_ID, Defines.PASS, false);
		cc.click(Defines.SIGNIN_BTN_ID, false);
		cc.acceptAlert();
	}
	
	public static void runSearches(int number_of_searches, Client cc, ArrayList<String> dictionary){
		Random rand = new Random(System.currentTimeMillis());
		for(int ii = 0; ii < number_of_searches; ii++){
			cc.getPage(Defines.BING_URL);
			String phrase = dictionary.get(rand.nextInt(dictionary.size())) + " " + dictionary.get(rand.nextInt(dictionary.size())) + " " + dictionary.get(rand.nextInt(dictionary.size()));
			cc.sendKeys(Defines.SEARCH_BAR_ID, phrase, false);
			Basics.delay(Defines.DELAY_GO);
			cc.click(Defines.SEARCH_BTN_ID, false);
			Basics.delay(Defines.DELAY_SEARCH);
		}
	}
}
