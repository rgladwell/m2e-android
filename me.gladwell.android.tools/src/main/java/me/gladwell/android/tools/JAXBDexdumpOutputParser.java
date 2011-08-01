package me.gladwell.android.tools;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import me.gladwell.android.tools.model.ClassDescriptor;
import me.gladwell.android.tools.model.DexInfo;
import me.gladwell.android.tools.model.PackageInfo;


public class JAXBDexdumpOutputParser implements DexdumpOutputParser {

	public DexInfo parse(String xml) throws AndroidToolsException {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance("me.gladwell.android.tools.model", Activator.class.getClassLoader());
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			DexInfo dex = (DexInfo) unmarshaller.unmarshal(new StringReader(xml));
			for(PackageInfo packageInfo : dex.getPackages()) {
				for(ClassDescriptor classDescriptor : packageInfo.getClassDescriptors()) {
					dex.getClassDescriptors().add(classDescriptor);
					classDescriptor.setPackageInfo(packageInfo);
				}
			}
			return dex;
		} catch (JAXBException e) {
			throw new AndroidToolsException("error parsing dexdump XML output", e);
		}
	}

}
