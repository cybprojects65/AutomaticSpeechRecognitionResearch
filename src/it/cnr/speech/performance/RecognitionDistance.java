package it.cnr.speech.performance;

public class RecognitionDistance {

	public static void main(String[] args) throws Exception{
		String s1 []= "il movimento cinque stelle non molla il presidente del consiglio dimissionario due sette con tre ma il dialogo con il pd continua".split(" ");
		String s2 []= "il movimento cinque stelle non molla il presidente del consiglio dimissionario due sette con tre ma il dialogo con i spinti con tina".split(" ");
		
		System.out.println(calcWER(s1, s2));
		
	}
	public static int calcWER(String[] wordseq1,String[] wordseq2) {
		
		int len1 = wordseq1.length;
		int len2 = wordseq2.length;
	 
		// len1+1, len2+1, because finally return dp[len1][len2]
		int[][] dp = new int[len1 + 1][len2 + 1];
	 
		for (int i = 0; i <= len1; i++) {
			dp[i][0] = i;
		}
	 
		for (int j = 0; j <= len2; j++) {
			dp[0][j] = j;
		}
	 
		//iterate though, and check last char
		for (int i = 0; i < len1; i++) {
			String c1 = wordseq1[i];
			for (int j = 0; j < len2; j++) {
				String c2 = wordseq2[j];
	 
				//if last two chars equal
				if (c1.equals(c2)) {
					//update dp value for +1 length
					dp[i + 1][j + 1] = dp[i][j];
				} else {
					int replace = dp[i][j] + 1;
					int insert = dp[i][j + 1] + 1;
					int delete = dp[i + 1][j] + 1;
	 
					int min = replace > insert ? insert : replace;
					min = delete > min ? min : delete;
					dp[i + 1][j + 1] = min;
				}
			}
		}
	 
		return dp[len1][len2];
		
	}
	
}
