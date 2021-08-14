package icode_console;

/**
 * 
 * 
 * @author ICode Studio
 * @version 1.0  2018年11月21日
 */
public class Sqr {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for(int i = 1; i <= 100; i ++) {
			double dbl = Math.round(Math.sqrt(1.2 * i) - 2.5) * 0.5;
			System.out.println( i + ". " + dbl);
		}
	}
}
