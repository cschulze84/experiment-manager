package experiment.reactis.utils;

public class StringProcessor {
	
	public static void main(String[] args) {
		String string1 = "_033_040rb_095IndicatorsRequested_032_061_061_032rb_095IndicatorsRequested_032_038_038_032rb_095indicators_095HzrdSwtchEdgesIsPressedPlaus_032_061_061_032rb_095indicators_095HzrdSwtchEdgesIsPressedPlaus_032_038_038_032rb_095state_032_061_061_032rb_095state_041_032_124_124_032rb_095IndicatorsNew_032_061_061_032rb_095IndicatorsNew";
		System.out.println(replaceStringFromReactis(string1));
		
		
		
		String string2 = "_033_040_034rb_095IndicatorsRequested_034_032_061_061_0323_0460_032_038_038_032Memory_032_061_061_0320_0460_041_032_124_124_032_034Emergency_032Blinking_032Controller_032Model_034_058_058_034rb_095IndicatorsNew_034_032_061_061_0327_0460";
		System.out.println(replaceStringFromReactis(string2));
		
		System.out.println();
	
		System.out.println(string1.equals(string2));
		
		System.out.println(replaceStringFromReactis(string1).equals(replaceStringFromReactis(string2)));
	}
	
	private static String replaceSymbolToReactis(char symbol){
		switch (symbol) {
		case ' ':
			return "_032";
		case '!':
			return "_033";
		case '"':
			return "_034";
		case '&':
			return "_038";
		case '(':
			return "_040";
		case ')':
			return "_041";
		case '+':
			return "_043";
		case '-':
			return "_045";
		case '.':
			return "_046";
		case ':':
			return "_058";
		case '<':
			return "_060";
		case '>':
			return "_062";
		case '=':
			return "_061";
		case '\\':
			return "_092";
		case '_':
			return "_095";
		case '|':
			return "_124";
		default:
			return symbol + "";
		}
	}
	
	public static String replaceStringFromReactis(String string){
		String result = string;
		
		result = result.replace("_032", " ");
		result = result.replace("_033", "!");
		result = result.replace("_034", "\"");
		result = result.replace("_038", "&");
		result = result.replace("_040", "(");
		result = result.replace("_041", ")");
		result = result.replace("_043", "+");
		result = result.replace("_045", "-");
		result = result.replace("_046", ".");
		result = result.replace("_058", ":");
		result = result.replace("_060", "<");
		result = result.replace("_062", ">");
		result = result.replace("_061", "=");
		result = result.replace("_092", "\\");
		result = result.replace("_095", "_");
		result = result.replace("_124", "|");
		
		return result;
	}
	
	public String replace(String string){
		String result = "";
		
		for (int i=0; i<string.length(); i++) {
			result += replaceSymbolToReactis(string.charAt(i));
		}
		
		return result;
	}

	public static String getReactisASCII(String string) {
		String result = "";
		
		for (int i=0; i<string.length(); i++) {
			result += replaceSymbolToReactis(string.charAt(i));
		}
		
		return result;
	}
}
