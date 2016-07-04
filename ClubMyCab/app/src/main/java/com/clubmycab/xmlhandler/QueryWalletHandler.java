package com.clubmycab.xmlhandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class QueryWalletHandler extends DefaultHandler {

	private StringBuilder builder;
	Boolean isSuccces;

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();

	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		builder = new StringBuilder();

		if (localName.equals("status")) {
			// Log.d(TAG, "startElement status : " + builder.toString());
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		if (localName.equals("status")) {
			if (builder.toString().equals("SUCCESS"))
				isSuccces = true;
			else
				isSuccces = false;

			// Log.d(TAG, "startElement status : " + builder.toString());
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		builder.append(new String(ch, start, length));
	}

}