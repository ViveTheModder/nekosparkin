package cmd;
//Nekosparkin: Named Parameter Object by ViveTheJoestar
import java.io.File;
import java.io.IOException;

public class NamedParam {
	
	private int id;
	private String name;
	private String type;
	
	public NamedParam(int id, String type, File[] csvFiles, String[] names) throws IOException {
		this.id = id;
		this.type = type;
		this.name = getParamNameFromType(id, type, csvFiles, names);
	}
	private String getParamNameFromType(int id, String type, File[] csvFiles, String[] names) throws IOException {
		String name = null;
		File csv = csvFiles[CsvHandler.getCsvSearchResult(csvFiles, type)];
		if (csv == null) return name;
		if (names != null) {
			if (id < names.length) name = names[id];
		}
		return name;
	}
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}
}