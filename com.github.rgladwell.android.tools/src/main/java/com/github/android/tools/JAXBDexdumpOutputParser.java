package com.github.android.tools;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.github.android.tools.model.ClassDescriptor;
import com.github.android.tools.model.DexInfo;
import com.github.android.tools.model.PackageInfo;

public class JAXBDexdumpOutputParser implements DexdumpOutputParser {

	public DexInfo parse(String xml) throws AndroidToolsException {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(DexInfo.class, PackageInfo.class, ClassDescriptor.class);
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
