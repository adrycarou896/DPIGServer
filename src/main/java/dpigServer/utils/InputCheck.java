package dpigServer.utils;

import java.io.File;

public class InputCheck {

	public static boolean validParams(String ... args) {
		try {
			Integer.parseInt(args[3]);
			return args.length == 4 && 
					(new File(args[0])).exists() && (new File(args[1])).exists();
		}catch (Exception e) {
			return false;
		}
	}
}
