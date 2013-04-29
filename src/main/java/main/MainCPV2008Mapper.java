package main;
import java.io.IOException;


public class MainCPV2008Mapper {

	public static void main(String []args) throws IOException{
		String label = "services";
		MapperService mapper = new MapperService();
		System.out.println(mapper.cpv2008(label));
		System.out.println(mapper.aus("Oracle"));
	}
}
