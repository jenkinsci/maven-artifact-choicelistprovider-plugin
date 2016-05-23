package com.wincornixdorf.jenkins.plugins.jenkinsextensiblechoiceproviderurlprovider;

import java.util.ArrayList;
import java.util.List;

import jp.ikedam.jenkins.plugins.extensible_choice_parameter.ChoiceListProvider;

public class URLChoiceList extends ChoiceListProvider{

	@Override
	public List<String> getChoiceList() {
		List<String> retVal = new ArrayList<String>();
		retVal.add("Hallo Welt");
		return retVal;
	}

}
